import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
                //List File
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
                //Delete File
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
                //Rename File >> may not work, this will have to be tested
                case 'R':
                    System.out.println("Enter the filename of what you want to rename:");
                    String oldFileName = keyboard.nextLine();
                    System.out.println("Enter the new name:");
                    String newFileName = keyboard.nextLine();
                    //put everything into a string with a character separator for the server
                    clientMessage = oldFileName + "/" + newFileName;
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);
                    messageBuffer = ByteBuffer.wrap(clientMessage.getBytes());
                    channel.write(messageBuffer);
                    channel.shutdownOutput();
                    //receive status code
                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                //Upload File
                case 'U':
                    System.out.println("Enter the name of the file you want to upload:");
                    String fileName = keyboard.nextLine();
                    File file = new File("ClientFiles", fileName);

                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();
                    channel.write(commandBuffer);

                    ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
                    int fileNameLength = fileName.length();
                    lengthBuffer.putInt(fileNameLength);
                    lengthBuffer.flip();
                    channel.write(lengthBuffer);

                    ByteBuffer nameBuffer = ByteBuffer.wrap(fileName.getBytes());
                    channel.write(nameBuffer);
                    FileInputStream fis = new FileInputStream(file);
                    FileChannel fc = fis.getChannel();
                    ByteBuffer contentBuffer = ByteBuffer.allocate(1024);
                    while(fc.read(contentBuffer) != -1) {
                        contentBuffer.flip();
                        channel.write(contentBuffer);
                        contentBuffer.clear();
                    }
                    channel.shutdownOutput();
                    fis.close();

                    replyBuffer = ByteBuffer.allocate(1024);
                    bytesRead = channel.read(replyBuffer);
                    channel.close();
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                //Download File
                case 'D':
                    //TODO: write download case
                    System.out.println("Enter the filename of what you want to download:");
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
                    //write something that intercepts the file content (replybuffer may have to be larger)
                    //find way to store file content (this'll just be the reverse of the upload case)
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
                //Test Case 'Ping'
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
    public static void wrapCommand(char command, String[] args, int serverPort) throws IOException {
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



