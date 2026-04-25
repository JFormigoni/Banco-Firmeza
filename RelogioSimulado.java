// Relógio acelerado que converte tempo real em tempo simulado
// Escala 200 = 1s real vira 200s simulados (2h em ~36s)
public class RelogioSimulado {

    public static final int DURACAO_TOTAL = 7200; // 2 horas em segundos

    private double escala;
    private long inicio;

    public RelogioSimulado(double escala) {
        this.escala = escala;
    }

    public void iniciar() {
        this.inicio = System.currentTimeMillis();
    }

    // Retorna tempo simulado decorrido em segundos
    public double agora() {
        long tempo = System.currentTimeMillis() - inicio;
        return tempo * escala / 1000.0;
    }

    // Converte segundos simulados em milissegundos reais
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
