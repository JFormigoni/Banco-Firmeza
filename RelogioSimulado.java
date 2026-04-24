public class RelogioSimulado {

    public static final int DURACAO_TOTAL = 7200;

    private double escala;
    private long inicio;

    public RelogioSimulado(double escala) {
        this.escala = escala;
    }

    public void iniciar() {
        this.inicio = System.currentTimeMillis();
    }

    public double agora() {
        long tempo = System.currentTimeMillis() - inicio;
        return tempo * escala / 1000.0;
    }

    public long paraTempoReal(double seg) {
        return Math.round(seg * 1000.0 / escala);
    }

    public boolean dentro() {
        return agora() < DURACAO_TOTAL;
    }

    public double getEscala() {
        return escala;
    }
}
