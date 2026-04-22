package simulador;

/**
 * Representa um cliente que chega ao banco Firmeza Investimentos.
 *
 * <p>Cada instância armazena os instantes de chegada, início e fim do
 * atendimento, permitindo o cálculo das métricas de desempenho da fila.</p>
 *
 * @author  Simulador Firmeza Investimentos
 * @version 1.0
 */
public class Cliente {

    /** Identificador único sequencial do cliente. */
    private final int id;

    /** Instante (em segundos simulados) em que o cliente chegou ao banco. */
    private final double tempoChegada;

    /**
     * Instante (em segundos simulados) em que o atendimento foi iniciado.
     * Zero enquanto o cliente ainda aguarda na fila.
     */
    private volatile double tempoInicioAtendimento;

    /**
     * Instante (em segundos simulados) em que o atendimento foi concluído.
     * Zero enquanto o cliente ainda está sendo atendido ou aguardando.
     */
    private volatile double tempoFimAtendimento;

    /** Identificador do atendente (thread) que processou este cliente. */
    private volatile int idAtendente;

    // ------------------------------------------------------------------
    // Construtor
    // ------------------------------------------------------------------

    /**
     * Cria um novo cliente com o instante de chegada informado.
     *
     * @param id           identificador único do cliente
     * @param tempoChegada instante de chegada em segundos simulados
     */
    public Cliente(int id, double tempoChegada) {
        this.id = id;
        this.tempoChegada = tempoChegada;
    }

    // ------------------------------------------------------------------
    // Métricas derivadas
    // ------------------------------------------------------------------

    /**
     * Retorna o tempo de espera do cliente na fila (em segundos).
     *
     * <p>Equivale ao intervalo entre a chegada e o início do atendimento.</p>
     *
     * @return tempo de espera em segundos
     */
    public double getTempoEspera() {
        return tempoInicioAtendimento - tempoChegada;
    }

    /**
     * Retorna o tempo efetivo de atendimento (em segundos).
     *
     * @return duração do atendimento em segundos
     */
    public double getTempoAtendimento() {
        return tempoFimAtendimento - tempoInicioAtendimento;
    }

    /**
     * Retorna o tempo total de permanência no sistema, do ingresso à saída
     * (lead time / tempo de ciclo), em segundos.
     *
     * @return lead time em segundos
     */
    public double getLeadTime() {
        return tempoFimAtendimento - tempoChegada;
    }

    // ------------------------------------------------------------------
    // Getters e Setters
    // ------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public double getTempoChegada() {
        return tempoChegada;
    }

    public double getTempoInicioAtendimento() {
        return tempoInicioAtendimento;
    }

    public void setTempoInicioAtendimento(double tempoInicioAtendimento) {
        this.tempoInicioAtendimento = tempoInicioAtendimento;
    }

    public double getTempoFimAtendimento() {
        return tempoFimAtendimento;
    }

    public void setTempoFimAtendimento(double tempoFimAtendimento) {
        this.tempoFimAtendimento = tempoFimAtendimento;
    }

    public int getIdAtendente() {
        return idAtendente;
    }

    public void setIdAtendente(int idAtendente) {
        this.idAtendente = idAtendente;
    }

    @Override
    public String toString() {
        return String.format(
            "Cliente{id=%d, chegada=%.1fs, espera=%.1fs, atendimento=%.1fs, leadTime=%.1fs}",
            id, tempoChegada, getTempoEspera(), getTempoAtendimento(), getLeadTime()
        );
    }
}
