package simulador;

/**
 * Relógio da simulação: converte tempo simulado em tempo real de execução.
 *
 * <p>A janela de análise é das 11h00 às 13h00, totalizando 7.200 segundos
 * simulados. Para viabilizar a execução em segundos reais, aplica-se um
 * fator de escala {@code escala}: cada segundo simulado corresponde a
 * {@code 1 / escala} milissegundos reais.</p>
 *
 * <p>Exemplo: com {@code escala = 100}, 7.200 s simulados são executados
 * em 72 segundos reais.</p>
 *
 * @author  Simulador Firmeza Investimentos
 * @version 1.0
 */
public class RelogioSimulado {

    // ------------------------------------------------------------------
    // Domínio da simulação
    // ------------------------------------------------------------------

    /** Duração total da janela simulada: 11h00–13h00 = 7.200 segundos. */
    public static final int DURACAO_TOTAL_SEG = 7_200;

    // ------------------------------------------------------------------
    // Atributos de instância
    // ------------------------------------------------------------------

    /**
     * Fator de aceleração da simulação.
     * Quanto maior, mais rápida a execução real.
     */
    private final double escala;

    /** Instante real (ms) em que a simulação foi iniciada. */
    private long inicioReal;

    // ------------------------------------------------------------------
    // Construtor
    // ------------------------------------------------------------------

    /**
     * Cria o relógio com o fator de escala desejado.
     *
     * @param escala fator de aceleração (e.g., 100 para 100x mais rápido)
     */
    public RelogioSimulado(double escala) {
        this.escala = escala;
    }

    // ------------------------------------------------------------------
    // Controle
    // ------------------------------------------------------------------

    /** Registra o instante de início da simulação. */
    public void iniciar() {
        this.inicioReal = System.currentTimeMillis();
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    /**
     * Retorna o tempo simulado decorrido em segundos desde o início.
     *
     * @return segundos simulados desde o início da janela
     */
    public double agora() {
        long decorrido = System.currentTimeMillis() - inicioReal;
        return decorrido * escala / 1_000.0;
    }

    /**
     * Converte uma duração em segundos simulados para milissegundos reais
     * de sleep.
     *
     * @param segundosSimulados duração a converter
     * @return milissegundos reais equivalentes
     */
    public long paraTempoReal(double segundosSimulados) {
        return Math.round(segundosSimulados * 1_000.0 / escala);
    }

    /**
     * Verifica se a janela de simulação ainda está aberta.
     *
     * @return {@code true} enquanto o tempo simulado for menor que
     *         {@link #DURACAO_TOTAL_SEG}
     */
    public boolean dentro() {
        return agora() < DURACAO_TOTAL_SEG;
    }

    public double getEscala() {
        return escala;
    }
}
