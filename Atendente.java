import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Atendente implements Runnable {

    private static final int SERVICO_MIN = 30;
    private static final int SERVICO_MAX = 120;

    private int id;
    private BlockingQueue<Cliente> filaClientes;
    private RelogioSimulado relogio;
    private Random random;
    private boolean ativo = true;
    private List<Cliente> clientesAtendidos;

    public Atendente(int id, BlockingQueue<Cliente> filaClientes, RelogioSimulado relogio) {
        this.id = id;
        this.filaClientes = filaClientes;
        this.relogio = relogio;
        this.random = new Random();
        this.clientesAtendidos = new ArrayList<>();
    }

    public void run() {
        while (ativo || !filaClientes.isEmpty()) {
            try {
                Cliente cliente = filaClientes.poll(200, java.util.concurrent.TimeUnit.MILLISECONDS);
                
                if (cliente == null) continue;

                cliente.setTempoInicioAtendimento(relogio.agora());
                cliente.setIdAtendente(id);

                int duracao = SERVICO_MIN + random.nextInt(SERVICO_MAX - SERVICO_MIN + 1);
                Thread.sleep(relogio.paraTempoReal(duracao));

                cliente.setTempoFimAtendimento(relogio.agora());
                clientesAtendidos.add(cliente);

            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void encerrar() {
        this.ativo = false;
    }

    public List<Cliente> getClientesAtendidos() {
        return Collections.unmodifiableList(clientesAtendidos);
    }

    public int getId() {
        return id;
    }
}
