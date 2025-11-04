import java.io.IOException;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientAcceptance implements Runnable {
    ServerSocketChannel listenSocket;

    public ClientAcceptance(ServerSocketChannel listenSocket) {
        this.listenSocket = listenSocket;
    }

    public void run() {

        ExecutorService es;
        es = Executors.newSingleThreadExecutor();

            do {
                try {
                    SocketChannel serverChannel = listenSocket.accept();
                    System.out.println("Client Connected.");
                    es.submit(new RunServerTask(serverChannel));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
    }
}
