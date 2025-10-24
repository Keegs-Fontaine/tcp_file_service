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
            while (true) {
                SocketChannel serverChannel = listenSocket.accept();

                ByteBuffer commandBuffer = ByteBuffer.allocate(2);
                int bytesRead = serverChannel.read(commandBuffer);
                commandBuffer.flip();

                char command = commandBuffer.getChar();

                ExecutorService es = Executors.newFixedThreadPool(4);
                es.submit(new ServerRunnable(serverChannel, command));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
