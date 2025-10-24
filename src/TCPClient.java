import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Need <serverIP> and <serverPort>.");
            return;
        }
        int serverPort = Integer.parseInt(args[1]);
        ExecutorService es = Executors.newFixedThreadPool(4);

        Scanner keyboard = new Scanner(System.in);
        char command;
        do {
            System.out.println("Enter a command: " +
                    "\nL - list all files" +
                    "\nX - delete a file" +
                    "\nR - rename a file" +
                    "\nU - upload a file" +
                    "\nD - download a file" +
                    "\nE - echo a message" +
                    "\nQ - quit program\n");
            String userInput = keyboard.nextLine();
            command = userInput.toUpperCase().charAt(0);

            es.submit(new ClientRunnable(serverPort, args, command));

        } while (command != 'Q');
    }
}



