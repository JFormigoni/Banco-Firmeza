package simulador;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gerador de chegadas de clientes ao banco Firmeza Investimentos.
 *
 * <p>Executa em thread dedicada e injeta objetos {@link Cliente} na
 * {@link BlockingQueue} compartilhada, simulando o fluxo de chegada de
 * acordo com a distribuição uniforme U[5, 50] segundos entre chegadas
 * consecutivas.</p>
 *
 * <p>A geração é interrompida quando o relógio simulado ultrapassa os
 * 7.200 segundos (janela 11h00–13h00).</p>
 *
 * @author  Simulador Firmeza Investimentos
 * @version 1.0
 */
public class GeradorChegadas implements Runnable {

    // ------------------------------------------------------------------
    // Constantes do domínio
    // ------------------------------------------------------------------

    /** Intervalo mínimo entre chegadas consecutivas (segundos simulados). */
    private static final int CHEGADA_MIN_SEG = 5;

    /** Intervalo máximo entre chegadas consecutivas (segundos simulados). */
    private static final int CHEGADA_MAX_SEG = 50;

    // ------------------------------------------------------------------
    // Atributos de instância
    // ------------------------------------------------------------------

    /** Fila compartilhada onde os clientes serão inseridos. */
    private final BlockingQueue<Cliente> filaClientes;

    /** Relógio da simulação. */
    private final RelogioSimulado relogio;

    /** Contador global e thread-safe de IDs de clientes gerados. */
    private final AtomicInteger contadorId;

    /** Gerador de números aleatórios. */
    private final Random random;

    /** Total de clientes gerados (preenchido ao término). */
    private int totalGerado = 0;

    // ------------------------------------------------------------------
    // Construtor
    // ------------------------------------------------------------------

    /**
     * Cria o gerador de chegadas.
     *
     * @param filaClientes fila de destino dos clientes gerados
     * @param relogio      relógio simulado compartilhado
     * @param contadorId   contador atômico para IDs únicos
     */
    public GeradorChegadas(BlockingQueue<Cliente> filaClientes,
                           RelogioSimulado relogio,
                           AtomicInteger contadorId) {
        this.filaClientes = filaClientes;
        this.relogio      = relogio;
        this.contadorId   = contadorId;
        this.random       = new Random();
    }

    // ------------------------------------------------------------------
    // Lógica principal (Runnable)
    // ------------------------------------------------------------------

    /**
     * Laço de geração de chegadas.
     *
     * <ol>
     *   <li>Sorteia o intervalo entre chegadas: U[5, 50] s simulados.</li>
     *   <li>Dorme o equivalente em tempo real.</li>
     *   <li>Cria o cliente com o timestamp atual do relógio.</li>
     *   <li>Insere o cliente na fila (bloqueante até que haja espaço).</li>
     *   <li>Repete enquanto a janela temporal estiver aberta.</li>
     * </ol>
     */
    @Override
    public void run() {
        while (relogio.dentro()) {
            // Sorteia intervalo entre chegadas: U[5, 50] segundos simulados
            int intervalo = CHEGADA_MIN_SEG
                + random.nextInt(CHEGADA_MAX_SEG - CHEGADA_MIN_SEG + 1);

            try {
                Thread.sleep(relogio.paraTempoReal(intervalo));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            // Verifica novamente: pode ter ultrapassado o limite durante o sleep
            if (!relogio.dentro()) {
                break;
            }

            int idCliente   = contadorId.incrementAndGet();
            double chegada  = relogio.agora();
            Cliente cliente = new Cliente(idCliente, chegada);

            try {
                filaClientes.put(cliente); // bloqueante
                totalGerado++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // ------------------------------------------------------------------
    // Acesso ao resultado
    // ------------------------------------------------------------------

    /**
     * Retorna o número de clientes efetivamente colocados na fila.
     * Válido somente após o término da thread.
     *
     * @return total de clientes gerados
     */
    public int getTotalGerado() {
        return totalGerado;
    }
}
