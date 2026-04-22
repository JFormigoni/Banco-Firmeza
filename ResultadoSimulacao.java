package simulador;

import java.util.List;

/**
 * Agrega e calcula as métricas de desempenho para um cenário simulado
 * com {@code n} atendentes no banco Firmeza Investimentos.
 *
 * <p>As métricas seguem os requisitos do enunciado:</p>
 * <ul>
 *   <li><b>Throughput</b>: volume total de clientes atendidos.</li>
 *   <li><b>Tempo máximo de espera</b>: pior caso de espera na fila.</li>
 *   <li><b>Tempo máximo de atendimento</b>: maior duração de serviço.</li>
 *   <li><b>Lead Time médio</b>: média do tempo total de permanência.</li>
 *   <li><b>Espera média</b>: indicador de nível de serviço (meta ≤ 120 s).</li>
 *   <li><b>SLA cumprido</b>: se espera média ≤ 120 s.</li>
 * </ul>
 *
 * @author  Simulador Firmeza Investimentos
 * @version 1.0
 */
public class ResultadoSimulacao {

    /** Meta máxima de tempo médio de espera (segundos). */
    public static final double META_ESPERA_SEG = 120.0;

    // ------------------------------------------------------------------
    // Métricas calculadas
    // ------------------------------------------------------------------

    private final int    numAtendentes;
    private final int    throughput;
    private final double esperaMedia;
    private final double esperaMaxima;
    private final double atendimentoMaximo;
    private final double leadTimeMedia;
    private final boolean slaCumprido;

    // ------------------------------------------------------------------
    // Construtor — calcula as métricas a partir da lista de clientes
    // ------------------------------------------------------------------

    /**
     * Processa a lista de clientes atendidos e calcula todas as métricas.
     *
     * @param numAtendentes número de postos de atendimento neste cenário
     * @param atendidos     lista consolidada de todos os clientes processados
     */
    public ResultadoSimulacao(int numAtendentes, List<Cliente> atendidos) {
        this.numAtendentes = numAtendentes;
        this.throughput    = atendidos.size();

        if (atendidos.isEmpty()) {
            esperaMedia        = 0;
            esperaMaxima       = 0;
            atendimentoMaximo  = 0;
            leadTimeMedia      = 0;
        } else {
            double somaEspera    = 0;
            double maxEspera     = 0;
            double maxAtend      = 0;
            double somaLeadTime  = 0;

            for (Cliente c : atendidos) {
                double espera   = c.getTempoEspera();
                double atend    = c.getTempoAtendimento();
                double leadTime = c.getLeadTime();

                somaEspera   += espera;
                somaLeadTime += leadTime;

                if (espera > maxEspera) maxEspera = espera;
                if (atend  > maxAtend)  maxAtend  = atend;
            }

            esperaMedia       = somaEspera    / throughput;
            leadTimeMedia     = somaLeadTime  / throughput;
            esperaMaxima      = maxEspera;
            atendimentoMaximo = maxAtend;
        }

        this.slaCumprido = (esperaMedia <= META_ESPERA_SEG);
    }

    // ------------------------------------------------------------------
    // Exibição
    // ------------------------------------------------------------------

    /**
     * Imprime um relatório formatado do cenário no console.
     */
    public void imprimir() {
        System.out.println("=".repeat(60));
        System.out.printf("  CENÁRIO: %d ATENDENTE(S)%n", numAtendentes);
        System.out.println("=".repeat(60));
        System.out.printf("  Throughput (clientes atendidos) : %d%n",       throughput);
        System.out.printf("  Espera media na fila            : %7.1f s%n",  esperaMedia);
        System.out.printf("  Espera maxima registrada        : %7.1f s%n",  esperaMaxima);
        System.out.printf("  Atendimento maximo registrado   : %7.1f s%n",  atendimentoMaximo);
        System.out.printf("  Lead Time medio (ciclo)         : %7.1f s%n",  leadTimeMedia);
        System.out.printf("  Meta (espera <= 120 s)          : %s%n",
            slaCumprido ? "CUMPRIDA ✓" : "NAO CUMPRIDA ✗");
        System.out.println("-".repeat(60));
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    public int    getNumAtendentes()      { return numAtendentes;      }
    public int    getThroughput()          { return throughput;          }
    public double getEsperaMedia()         { return esperaMedia;         }
    public double getEsperaMaxima()        { return esperaMaxima;        }
    public double getAtendimentoMaximo()   { return atendimentoMaximo;   }
    public double getLeadTimeMedia()       { return leadTimeMedia;        }
    public boolean isSlaCumprido()         { return slaCumprido;         }
}
