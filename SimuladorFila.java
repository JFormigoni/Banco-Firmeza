import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimuladorFila {

    private static final int MAX_ATENDENTES = 10;
    private static final double ESCALA = 200.0;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("============================================================");
        System.out.println("   SIMULADOR DE FILAS - BANCO FIRMEZA INVESTIMENTOS      ");
        System.out.println("   Janela: 11h00 - 13h00  |  Meta espera: <= 120 s       ");
        System.out.println("============================================================");
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

        System.out.println();
        System.out.println("============================================================");
        System.out.println("                     CONCLUSAO                           ");
        System.out.println("============================================================");

        if (atendentesIdeal > 0) {
            System.out.println("  Numero minimo de atendentes para cumprir a meta: " + atendentesIdeal);
            System.out.println();
        } else {
            System.out.println("  Meta nao atingida com ate " + MAX_ATENDENTES + " atendentes.");
            System.out.println();
        }

        System.out.println("  Atendentes   Throughput   Esp.Media(s)   Esp.Max(s)     LeadTime(s)  SLA");
        System.out.println("  " + "-".repeat(74));
        for (ResultadoSimulacao r : resultados) {
            System.out.println("  " + r.getNumAtendentes() + "            " + 
                r.getThroughput() + "          " + 
                String.format("%.1f", r.getEsperaMedia()) + "         " + 
                String.format("%.1f", r.getEsperaMaxima()) + "        " + 
                String.format("%.1f", r.getLeadTimeMedia()) + "       " + 
                (r.isSlaCumprido() ? "OK" : "FALHA"));
        }
    }

    private static ResultadoSimulacao executarCenario(int n) throws InterruptedException {

        System.out.println();
        System.out.println("Iniciando cenario com " + n + " atendente(s)...");

        BlockingQueue<Cliente> fila = new LinkedBlockingQueue<>();
        AtomicInteger contador = new AtomicInteger(0);
        RelogioSimulado relogio = new RelogioSimulado(ESCALA);

        List<Atendente> atendentes = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            Atendente a = new Atendente(i, fila, relogio);
            Thread t = new Thread(a, "Atendente-" + i);
            atendentes.add(a);
            threads.add(t);
        }

        GeradorChegadas gerador = new GeradorChegadas(fila, relogio, contador);
        Thread tGerador = new Thread(gerador, "Gerador");

        relogio.iniciar();
        for (Thread t : threads) {
            t.start();
        }
        tGerador.start();

        tGerador.join();

        for (Atendente a : atendentes) {
            a.encerrar();
        }

        for (Thread t : threads) {
            t.join();
        }

        List<Cliente> todos = new ArrayList<>();
        for (Atendente a : atendentes) {
            todos.addAll(a.getClientesAtendidos());
        }

        return new ResultadoSimulacao(n, todos);
    }
}
