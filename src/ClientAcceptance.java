import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientAcceptance implements Runnable {
    static final int PORT = 3000;

    public void run() {
        ExecutorService es;
        try (ServerSocketChannel listenSocket = ServerSocketChannel.open()) {
            es = Executors.newSingleThreadExecutor();
            listenSocket.bind(new InetSocketAddress(PORT));

            while (true) {
                SocketChannel serverChannel = listenSocket.accept();
                System.out.println("Client Connected.");
                es.submit(new RunServerTask(serverChannel));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
