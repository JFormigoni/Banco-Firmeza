// Representa um cliente com seus tempos de chegada, início e fim de atendimento
public class Cliente {

    private int id;
    private double tempoChegada;
    private double tempoInicioAtendimento;
    private double tempoFimAtendimento;
    private int idAtendente;

    public Cliente(int id, double tempoChegada) {
        this.id = id;
        this.tempoChegada = tempoChegada;
    }

    public double getTempoEspera() {
        return tempoInicioAtendimento - tempoChegada;
    }

    public double getTempoAtendimento() {
        return tempoFimAtendimento - tempoInicioAtendimento;
    }

    public double getLeadTime() {
        return tempoFimAtendimento - tempoChegada;
    }

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

    public String toString() {
        return "Cliente{id=" + id + ", chegada=" + String.format("%.1f", tempoChegada) + 
            "s, espera=" + String.format("%.1f", getTempoEspera()) + 
            "s, atendimento=" + String.format("%.1f", getTempoAtendimento()) + 
            "s, leadTime=" + String.format("%.1f", getLeadTime()) + "s}";
    }
}
