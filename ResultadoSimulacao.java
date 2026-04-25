import java.util.List;

// Calcula e armazena métricas de um cenário: throughput, espera média/máxima, lead time
// Verifica se cumpriu a meta de espera <= 120s
public class ResultadoSimulacao {

    public static final double META_ESPERA = 120.0;

    private int numAtendentes;
    private int throughput;
    private double esperaMedia;
    private double esperaMaxima;
    private double atendimentoMaximo;
    private double leadTimeMedia;
    private boolean slaCumprido;

    // Calcula todas as métricas a partir da lista de clientes atendidos
    public ResultadoSimulacao(int numAtendentes, List<Cliente> atendidos) {
        this.numAtendentes = numAtendentes;
        this.throughput = atendidos.size();

        if (atendidos.isEmpty()) {
            esperaMedia = 0;
            esperaMaxima = 0;
            atendimentoMaximo = 0;
            leadTimeMedia = 0;
        } else {
            double somaEspera = 0;
            double maxEspera = 0;
            double maxAtend = 0;
            double somaLead = 0;

            for (Cliente c : atendidos) {
                double esp = c.getTempoEspera();
                double atd = c.getTempoAtendimento();
                double lead = c.getLeadTime();

                somaEspera += esp;
                somaLead += lead;

                if (esp > maxEspera) maxEspera = esp;
                if (atd > maxAtend) maxAtend = atd;
            }

            esperaMedia = somaEspera / throughput;
            leadTimeMedia = somaLead / throughput;
            esperaMaxima = maxEspera;
            atendimentoMaximo = maxAtend;
        }

        this.slaCumprido = (esperaMedia <= META_ESPERA);
    }

    public void imprimir() {
        System.out.println("=".repeat(60));
        System.out.println("  CENARIO: " + numAtendentes + " ATENDENTE(S)");
        System.out.println("=".repeat(60));
        System.out.println("  Throughput (clientes atendidos) : " + throughput);
        System.out.println("  Espera media na fila            : " + String.format("%.1f", esperaMedia) + " s");
        System.out.println("  Espera maxima registrada        : " + String.format("%.1f", esperaMaxima) + " s");
        System.out.println("  Atendimento maximo registrado   : " + String.format("%.1f", atendimentoMaximo) + " s");
        System.out.println("  Lead Time medio (ciclo)         : " + String.format("%.1f", leadTimeMedia) + " s");
        System.out.println("  Meta (espera <= 120 s)          : " + (slaCumprido ? "CUMPRIDA" : "NAO CUMPRIDA"));
        System.out.println("-".repeat(60));
    }

    public int getNumAtendentes() { 
        return numAtendentes; 
    }
    
    public int getThroughput() { 
        return throughput; 
    }
    
    public double getEsperaMedia() { 
        return esperaMedia; 
    }
    
    public double getEsperaMaxima() { 
        return esperaMaxima; 
    }
    
    public double getAtendimentoMaximo() { 
        return atendimentoMaximo; 
    }
    
    public double getLeadTimeMedia() { 
        return leadTimeMedia; 
    }
    
    public boolean isSlaCumprido() { 
        return slaCumprido; 
    }
}
