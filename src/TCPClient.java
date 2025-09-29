import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

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
                case 'X':
                    System.out.println("Enter the filename of what you want to delete:");
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
                //rename file >> may not work, this will have to be tested
                case 'R':
                    System.out.println("Enter the filename of what you want to rename:");
                    String fileName = keyboard.nextLine();
                    System.out.println("Enter the new name:");
                    String newFileName = keyboard.nextLine();
                    //putting everything into a string with a character separator for the server
                    clientMessage = fileName + "/" + newFileName;
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    channel.shutdownOutput();
                    messageBuffer = ByteBuffer.wrap(clientMessage.getBytes());
                    channel.write(messageBuffer);
                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                //upload file >> unfinished
                case 'U':
                    System.out.println("Enter the filename of what you want to upload:");
                    fileName = keyboard.nextLine();
                    //send command
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    channel.shutdownOutput();
                    //send file name
                    ByteBuffer fileNameBuffer = ByteBuffer.allocate(4);
                    fileNameBuffer.putInt(fileName.length());
                    fileNameBuffer.flip();
                    //TODO: send file content
                    break;
                //download file
                case 'D':
                    //TODO: write download case
                    System.out.println("Enter the filename of what you want to rename:");
                    fileName = keyboard.nextLine();
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    channel.shutdownOutput();
                    messageBuffer = ByteBuffer.wrap(fileName.getBytes());
                    channel.write(messageBuffer);
                    //write something that intercepts the file content
//                    replyBuffer = ByteBuffer.allocate(1024);
//                    bytesRead = channel.read(replyBuffer);
//                    channel.close();
//                    replyBuffer.flip();
//                    byteArray = new byte[bytesRead];
//                    replyBuffer.get(byteArray);
//                    System.out.println(new String(byteArray));
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

    //methods to clean up switch statement
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



