package simulador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe principal do Simulador de Filas — Banco Firmeza Investimentos.
 *
 * <p>Executa cenários iterativos variando o número de atendentes de 1 a
 * {@link #MAX_ATENDENTES}, imprimindo as métricas de cada rodada e
 * identificando o número mínimo de postos necessário para que o tempo
 * médio de espera não ultrapasse 120 segundos.</p>
 *
 * <h3>Arquitetura Concorrente</h3>
 * <pre>
 *   GeradorChegadas (Thread)
 *         │
 *         │  put(cliente)
 *         ▼
 *   BlockingQueue&lt;Cliente&gt;   ◄── compartilhada
 *         │
 *         │  poll(timeout)
 *         ├──► Atendente-1 (Thread)
 *         ├──► Atendente-2 (Thread)
 *         └──► Atendente-N (Thread)
 * </pre>
 *
 * <p>O {@link RelogioSimulado} controla a conversão entre tempo simulado
 * (segundos) e tempo real de execução (milissegundos), possibilitando
 * rodar toda a janela de 2 horas em poucos segundos reais.</p>
 *
 * @author  Simulador Firmeza Investimentos
 * @version 1.0
 */
public class SimuladorFila {

    // ------------------------------------------------------------------
    // Parâmetros da simulação
    // ------------------------------------------------------------------

    /** Número máximo de atendentes a testar iterativamente. */
    private static final int MAX_ATENDENTES = 10;

    /**
     * Fator de aceleração do relógio simulado.
     * Com 200, os 7.200 s simulados executam em ~36 s reais.
     */
    private static final double ESCALA = 200.0;

    // ------------------------------------------------------------------
    // Ponto de entrada
    // ------------------------------------------------------------------

    /**
     * Ponto de entrada da aplicação.
     *
     * @param args argumentos da linha de comando (não utilizados)
     * @throws InterruptedException se alguma thread for interrompida
     */
    public static void main(String[] args) throws InterruptedException {

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║   SIMULADOR DE FILAS — BANCO FIRMEZA INVESTIMENTOS      ║");
        System.out.println("║   Janela: 11h00 – 13h00  |  Meta espera: <= 120 s       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        List<ResultadoSimulacao> resultados = new ArrayList<>();
        int atendentesIdeal = -1;

        for (int n = 1; n <= MAX_ATENDENTES; n++) {
            ResultadoSimulacao resultado = executarCenario(n);
            resultado.imprimir();
            resultados.add(resultado);

            if (resultado.isSlaCumprido() && atendentesIdeal == -1) {
                atendentesIdeal = n;
            }
        }

        // ----------------------------------------------------------
        // Conclusão
        // ----------------------------------------------------------
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                     CONCLUSÃO                           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        if (atendentesIdeal > 0) {
            System.out.printf(
                "  Numero minimo de atendentes para cumprir a meta: %d%n%n",
                atendentesIdeal
            );
        } else {
            System.out.printf(
                "  Meta nao atingida com ate %d atendentes.%n%n",
                MAX_ATENDENTES
            );
        }

        // Tabela resumo
        System.out.printf("  %-12s %-12s %-14s %-14s %-12s %-10s%n",
            "Atendentes", "Throughput", "Esp.Media(s)",
            "Esp.Max(s)", "LeadTime(s)", "SLA");
        System.out.println("  " + "-".repeat(74));
        for (ResultadoSimulacao r : resultados) {
            System.out.printf("  %-12d %-12d %-14.1f %-14.1f %-12.1f %-10s%n",
                r.getNumAtendentes(),
                r.getThroughput(),
                r.getEsperaMedia(),
                r.getEsperaMaxima(),
                r.getLeadTimeMedia(),
                r.isSlaCumprido() ? "OK" : "FALHA"
            );
        }
    }

    // ------------------------------------------------------------------
    // Execução de um cenário
    // ------------------------------------------------------------------

    /**
     * Executa um único cenário de simulação com {@code numAtendentes} postos.
     *
     * <p>O método cria as threads de geração e atendimento, aguarda o
     * término da janela temporal, encerra os atendentes e consolida os
     * clientes processados em um {@link ResultadoSimulacao}.</p>
     *
     * @param numAtendentes número de atendentes neste cenário
     * @return métricas calculadas para o cenário
     * @throws InterruptedException se a thread principal for interrompida
     */
    private static ResultadoSimulacao executarCenario(int numAtendentes)
            throws InterruptedException {

        System.out.printf("%nIniciando cenario com %d atendente(s)...%n", numAtendentes);

        // Fila compartilhada: capacidade ilimitada (sistema M/M/n real)
        BlockingQueue<Cliente> fila = new LinkedBlockingQueue<>();

        // Contador atômico de IDs de clientes
        AtomicInteger contadorId = new AtomicInteger(0);

        // Relógio simulado
        RelogioSimulado relogio = new RelogioSimulado(ESCALA);

        // ----------------------------------------------------------
        // Cria e inicia os atendentes (threads de consumo)
        // ----------------------------------------------------------
        List<Atendente> atendentes = new ArrayList<>();
        List<Thread>    threadsAt  = new ArrayList<>();

        for (int i = 1; i <= numAtendentes; i++) {
            Atendente at = new Atendente(i, fila, relogio);
            Thread    t  = new Thread(at, "Atendente-" + i);
            atendentes.add(at);
            threadsAt.add(t);
        }

        // ----------------------------------------------------------
        // Cria e inicia o gerador de chegadas (thread produtora)
        // ----------------------------------------------------------
        GeradorChegadas gerador    = new GeradorChegadas(fila, relogio, contadorId);
        Thread          threadGer  = new Thread(gerador, "Gerador-Chegadas");

        // Inicia o relógio e todas as threads
        relogio.iniciar();
        threadsAt.forEach(Thread::start);
        threadGer.start();

        // ----------------------------------------------------------
        // Aguarda o término da janela de simulação (7.200 s simulados)
        // ----------------------------------------------------------
        threadGer.join();  // Gerador termina ao fim da janela

        // Sinaliza aos atendentes que não virão mais clientes
        atendentes.forEach(Atendente::encerrar);

        // Aguarda todos os atendentes esgotarem a fila
        for (Thread t : threadsAt) {
            t.join();
        }

        // ----------------------------------------------------------
        // Consolida a lista de todos os clientes atendidos
        // ----------------------------------------------------------
        List<Cliente> todos = new ArrayList<>();
        for (Atendente at : atendentes) {
            todos.addAll(at.getClientesAtendidos());
        }

        return new ResultadoSimulacao(numAtendentes, todos);
    }
}
