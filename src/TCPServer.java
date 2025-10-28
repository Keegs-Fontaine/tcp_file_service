import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        ExecutorService es = Executors.newFixedThreadPool(4);

        System.out.println("Enter 'q' to shut down the server.");

        String keyboardInput = sc.nextLine();

        while (!keyboardInput.equals("q")) {
            es.submit(new ClientAcceptance());
        }
        //elegantly handle this later
        es.shutdown();
        sc.close();
        System.out.println("Server shut down");
    }
}
