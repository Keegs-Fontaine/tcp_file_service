import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    static final int PORT = 3000;
    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        String keyboardInput = "";

        ServerSocketChannel listenSocket = ServerSocketChannel.open();
        listenSocket.bind(new InetSocketAddress(PORT));
        System.out.println("Listening on port " + PORT);

        ExecutorService es = Executors.newFixedThreadPool(4);
        es.submit(new ClientAcceptance(listenSocket));

        do {
            System.out.println("Enter 'q' to shut down the server.");
            keyboardInput = sc.nextLine();
        }
        while (!keyboardInput.equals("q"));

        sc.close();
        es.shutdown();
        listenSocket.close();
        System.out.println("Server shut down");
    }
}
