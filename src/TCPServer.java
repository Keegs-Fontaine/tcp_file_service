import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * I'm leaving a note in here on java file handling since it's pretty extensively documented
 * and you can do a bunch of cool stuff
 * <p>
 * https://www.w3schools.com/java/java_files.asp
 */

public class TCPServer {
    static final int PORT = 3000;

    public static void main(String[] args) {
        System.out.println("hi tcp client");

        // --Deleting a server file-- I cannot attest for the functionality of this code
//        String fileName = ""; //whatever is passed in from the client
//        File file = new File("ServerFiles/" + fileName);
//        if (file.exists()) {
//            file.delete();
//            //send a status code: success
//        } else {
//            //send a status code: fail
//        }

        try (ServerSocketChannel listenSocket = ServerSocketChannel.open()) {
            listenSocket.bind(new InetSocketAddress(PORT));

            while (true) {
                SocketChannel serverChannel = listenSocket.accept();

                ByteBuffer commandBuffer = ByteBuffer.allocate(2);
                int bytesRead = serverChannel.read(commandBuffer);
                commandBuffer.flip();

                char command = commandBuffer.getChar();

                switch (command) {
                    case 'L':

                        System.out.println("Getting Files");
                        // Find list of files in ServerFiles dir
                        File directory = new File("ServerFiles");

                        System.out.println(directory.listFiles());

                        // TODO add error and branch handling
                        if (directory.exists() && directory.isDirectory()) {
                            File[] files = directory.listFiles();

                            StringBuilder message = new StringBuilder();

                            if (files != null && files.length > 0) {
                                for (File file : files) {
                                    message.append(file.getName()).append("\n");
                                }

                                System.out.println(message);
                                ByteBuffer responseBuffer = ByteBuffer.wrap(message.toString().getBytes());

                                serverChannel.write(responseBuffer);
                            }
                        }

                        break;
                    default:
                        // TODO make this send a value of "invalid command" back to client
                        System.out.println("Invalid command");
                }

                serverChannel.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e); // TODO add actual exception handling one day, but finish project first
        }
    }
}
