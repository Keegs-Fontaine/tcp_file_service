import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: allow for server to shutdown on 'q' keypress on the server end (not the client end)

public class TCPServer {
    static final int PORT = 4526;

    public static void main(String[] args) {

        try (ServerSocketChannel listenSocket = ServerSocketChannel.open()) {
            listenSocket.bind(new InetSocketAddress(PORT));
            ExecutorService es = Executors.newFixedThreadPool(4);
            while (true) {
                SocketChannel serverChannel = listenSocket.accept();
                es.submit(new ServerRunnable(serverChannel));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
