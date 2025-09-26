import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * TODO: Add switch statement for file directory (LIST, DELETE, RENAME)
 *
 */

public class TCPClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Need <serverIP> and <serverPort>.");
            return;
        }
        int serverPort = Integer.parseInt(args[1]);
        Scanner keyboard = new Scanner(System.in);
        char command;
        do {
            System.out.println("Enter a command " +
                    "[L (list), D (delete), R (rename), " +
                    "E (echo), Q (quit)]");
            String userInput = keyboard.nextLine();
            command = userInput.toUpperCase().charAt(0);

            switch (command) {
                //list file
                case 'L':
                    ByteBuffer commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    SocketChannel channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    channel.shutdownOutput();
                    ByteBuffer replyBuffer = ByteBuffer.allocate(1024);
                    int bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byte[] byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                //delete file
                case 'D':
                    System.out.println("Enter the filename that you want to delete:");
                    String clientMessage = keyboard.nextLine();
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    ByteBuffer messageBuffer = ByteBuffer.wrap(clientMessage.getBytes());
                    channel.write(messageBuffer);
                    channel.shutdownOutput();
                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                case 'R':
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    channel.shutdownOutput();
                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                case 'E':
                    System.out.println("Enter the message:");
                    clientMessage = keyboard.nextLine();
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    messageBuffer = ByteBuffer.wrap(clientMessage.getBytes());
                    channel.write(messageBuffer);
                    channel.shutdownOutput();
                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                case 'P':
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    channel.shutdownOutput();
                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                case 'Q':
                    break;
                default:
                    System.err.println("Invalid command, try again.");
            }
        } while (command != 'Q');
    }
    public static void wrapCommand (char command, String[] args, int serverPort) throws IOException {
        ByteBuffer commandBuffer = ByteBuffer.allocate(2);
        commandBuffer.putChar(command);
        commandBuffer.flip();
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(args[0], serverPort));
        channel.write(commandBuffer);
        channel.shutdownOutput();
    }

    public static void readReply(SocketChannel channel) throws IOException {
        ByteBuffer replyBuffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(replyBuffer);
        channel.close();
        replyBuffer.flip();
        byte[] byteArray = new byte[bytesRead];
        replyBuffer.get(byteArray);
        System.out.println(new String(byteArray));
    }
}



