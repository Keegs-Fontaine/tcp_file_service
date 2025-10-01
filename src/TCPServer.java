import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * I'm leaving a note in here on java file handling since it's pretty extensively documented
 * and you can do a bunch of cool stuff
 * <p>
 * https://www.w3schools.com/java/java_files.asp
 */

public class TCPServer {
    static final int PORT = 3000;

    private static String decodeBuffer(ByteBuffer buf) {
        return StandardCharsets.UTF_8.decode(buf).toString();
    }

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

                    case 'X': {
                        ByteBuffer filename = ByteBuffer.allocate(1024);

                        serverChannel.read(filename);
                        filename.flip();

                        File fileToDelete = new File("ServerFiles/" + decodeBuffer(filename));

                        if (fileToDelete.delete()) {
                            System.out.println("File Deleted");

                            serverChannel.write(ByteBuffer.wrap("OK".getBytes()));
                        }

                        break;
                    }

                    case 'R': {
                        System.out.println("Renaming File");

                        ByteBuffer filenames = ByteBuffer.allocate(1024);

                        serverChannel.read(filenames);
                        filenames.flip();

                        String decodedFilenames = decodeBuffer(filenames);
                        String[] splitFilenames = decodedFilenames.split("/");

                        File fileToRename = new File("ServerFiles/" + splitFilenames[0]);

                        boolean didWork = fileToRename.renameTo(new File("ServerFiles/" + splitFilenames[1]));

                        char status = didWork ? 'S' : 'F';

                        byte statusByte = (byte) status;

                        ByteBuffer replyBuffer = ByteBuffer.wrap(new byte[]{statusByte});
                        serverChannel.write(replyBuffer);

                        break;
                    }

                    case 'D': {

                        break;
                    }

                    case 'E': {
                        ByteBuffer messageBuffer = ByteBuffer.allocate(1024);
                        bytesRead = serverChannel.read(messageBuffer);
                        messageBuffer.flip();

                        byte[] messageByteArr = new byte[bytesRead];
                        messageBuffer.get(messageByteArr);

                        String clientMessage = new String(messageByteArr);

                        ByteBuffer replyBuffer = ByteBuffer.wrap(clientMessage.getBytes());
                        serverChannel.write(replyBuffer);

                        break;
                    }
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

    private static void printBuffer(ByteBuffer buf) {
        System.out.println(StandardCharsets.UTF_8.decode(buf));
    }
}
