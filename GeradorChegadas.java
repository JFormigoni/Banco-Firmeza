import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GeradorChegadas implements Runnable {

    private static final int MIN_INTERVALO = 5;
    private static final int MAX_INTERVALO = 50;

    private BlockingQueue<Cliente> fila;
    private RelogioSimulado relogio;
    private AtomicInteger contador;
    private Random random;
    private int total;

    public GeradorChegadas(BlockingQueue<Cliente> fila, RelogioSimulado relogio, AtomicInteger contador) {
        this.fila = fila;
        this.relogio = relogio;
        this.contador = contador;
        this.random = new Random();
        this.total = 0;
    }

    public void run() {
        while (relogio.dentro()) {
            int intervalo = MIN_INTERVALO + random.nextInt(MAX_INTERVALO - MIN_INTERVALO + 1);

            try {
                Thread.sleep(relogio.paraTempoReal(intervalo));
            } catch (InterruptedException e) {
                break;
            }

            if (!relogio.dentro()) break;

            Cliente c = new Cliente(contador.incrementAndGet(), relogio.agora());

            try {
                fila.put(c);
                total++;
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public int getTotalGerado() {
        return total;
    }
}
