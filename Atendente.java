package simulador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * Representa um posto de atendimento (caixa) do banco Firmeza Investimentos.
 *
 * <p>Cada {@code Atendente} é executado em uma {@link Thread} dedicada.
 * Ele consome continuamente clientes da fila compartilhada
 * ({@link BlockingQueue}), registra os instantes de início e término do
 * atendimento e acumula a lista de clientes processados para posterior
 * extração de métricas.</p>
 *
 * <h3>Modelo de tempo simulado</h3>
 * <p>O tempo de atendimento por cliente é sorteado uniformemente no intervalo
 * [30, 120] segundos, conforme especificado no enunciado do projeto.
 * Como a simulação é acelerada (1 segundo real ≠ 1 segundo simulado), o
 * {@link RelogioSimulado} fornece a escala de conversão.</p>
 *
 * @author  Simulador Firmeza Investimentos
 * @version 1.0
 */
public class Atendente implements Runnable {

    // ------------------------------------------------------------------
    // Constantes do domínio
    // ------------------------------------------------------------------

    /** Tempo mínimo de atendimento por cliente (segundos simulados). */
    private static final int SERVICO_MIN_SEG = 30;

    /** Tempo máximo de atendimento por cliente (segundos simulados). */
    private static final int SERVICO_MAX_SEG = 120;

    // ------------------------------------------------------------------
    // Atributos de instância
    // ------------------------------------------------------------------

    /** Identificador único do posto de atendimento. */
    private final int id;

    /** Fila compartilhada de clientes aguardando atendimento. */
    private final BlockingQueue<Cliente> filaClientes;

    /** Relógio que fornece o tempo atual da simulação. */
    private final RelogioSimulado relogio;

    /** Gerador de números aleatórios exclusivo desta thread. */
    private final Random random;

    /**
     * Flag de encerramento. Quando {@code true}, o atendente finaliza seu
     * loop após esvaziar a fila.
     */
    private volatile boolean ativo = true;

    /**
     * Lista de clientes atendidos por este posto (thread-safe somente via
     * acesso pós-execução ou sincronização externa).
     */
    private final List<Cliente> clientesAtendidos = new ArrayList<>();

    // ------------------------------------------------------------------
    // Construtor
    // ------------------------------------------------------------------

    /**
     * Cria um novo atendente vinculado à fila e ao relógio fornecidos.
     *
     * @param id            identificador do atendente (1-based)
     * @param filaClientes  fila compartilhada de chegada de clientes
     * @param relogio       relógio simulado compartilhado
     */
    public Atendente(int id, BlockingQueue<Cliente> filaClientes, RelogioSimulado relogio) {
        this.id = id;
        this.filaClientes = filaClientes;
        this.relogio = relogio;
        this.random = new Random();
    }

    // ------------------------------------------------------------------
    // Lógica principal (Runnable)
    // ------------------------------------------------------------------

    /**
     * Laço principal do atendente.
     *
     * <ol>
     *   <li>Tenta retirar um cliente da fila (bloqueante com timeout).</li>
     *   <li>Registra o instante de início do atendimento.</li>
     *   <li>Simula o tempo de serviço via {@link Thread#sleep}.</li>
     *   <li>Registra o instante de término e acumula o cliente processado.</li>
     * </ol>
     */
    @Override
    public void run() {
        while (ativo || !filaClientes.isEmpty()) {
            try {
                // Aguarda até 200 ms (tempo real) por um cliente na fila
                Cliente cliente = filaClientes.poll(
                    200, java.util.concurrent.TimeUnit.MILLISECONDS
                );

                if (cliente == null) {
                    // Fila vazia e ainda dentro do período — aguarda próximo
                    continue;
                }

                // --- Início do atendimento ---
                double inicioAtendimento = relogio.agora();
                cliente.setTempoInicioAtendimento(inicioAtendimento);
                cliente.setIdAtendente(id);

                // Sorteia duração de serviço: U[30, 120] segundos simulados
                int duracaoServico = SERVICO_MIN_SEG
                    + random.nextInt(SERVICO_MAX_SEG - SERVICO_MIN_SEG + 1);

                // Converte para tempo real de sleep e aguarda
                Thread.sleep(relogio.paraTempoReal(duracaoServico));

                // --- Fim do atendimento ---
                cliente.setTempoFimAtendimento(relogio.agora());
                clientesAtendidos.add(cliente);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // ------------------------------------------------------------------
    // Controle externo
    // ------------------------------------------------------------------

    /**
     * Sinaliza ao atendente que não chegará mais clientes novos.
     * O atendente continuará processando os clientes restantes na fila
     * antes de encerrar.
     */
    public void encerrar() {
        this.ativo = false;
    }

    // ------------------------------------------------------------------
    // Acesso às métricas
    // ------------------------------------------------------------------

    /**
     * Retorna a lista imutável de clientes atendidos por este posto.
     * Deve ser chamado somente após a thread encerrar.
     *
     * @return lista de clientes processados
     */
    public List<Cliente> getClientesAtendidos() {
        return Collections.unmodifiableList(clientesAtendidos);
    }

    public int getId() {
        return id;
    }
}
