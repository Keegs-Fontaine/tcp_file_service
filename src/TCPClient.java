import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
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
                    replyBuffer.flip();
                    byte[] byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));

                    ByteBuffer status = ByteBuffer.allocate(4);
                    channel.read(status);
                    status.flip();

                    System.out.println(StandardCharsets.UTF_8.decode(status));

                    channel.close();

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
                    replyBuffer.flip();
                    byteArray = new byte[bytesRead];
                    replyBuffer.get(byteArray);
                    System.out.println(new String(byteArray));

                    //receive status code
                    ByteBuffer statusBuffer = ByteBuffer.allocate(2);
                    bytesRead = channel.read(statusBuffer);
                    channel.close();
                    statusBuffer.flip();
                    System.out.println(StandardCharsets.UTF_8.decode(statusBuffer));
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
                    statusBuffer = ByteBuffer.allocate(2);
                    bytesRead = channel.read(statusBuffer);
                    channel.close();
                    statusBuffer.flip();
                    byteArray = new byte[bytesRead];
                    statusBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                //Upload File
                case 'U':
                    System.out.println("Enter the filename of what you want to upload:");
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

                    while (fc.read(contentBuffer) != -1) {
                        contentBuffer.flip();
                        channel.write(contentBuffer);
                        contentBuffer.clear();
                    }
                    channel.shutdownOutput();
                    fis.close();

                    //receive status code
                    statusBuffer = ByteBuffer.allocate(2);
                    bytesRead = channel.read(statusBuffer);
                    channel.close();
                    statusBuffer.flip();
                    byteArray = new byte[bytesRead];
                    statusBuffer.get(byteArray);
                    System.out.println(new String(byteArray));
                    break;
                //Download File
                case 'D':
                    System.out.println("Enter the filename of what you want to download:");

                    fileName = keyboard.nextLine();
                    commandBuffer = ByteBuffer.allocate(2);
                    commandBuffer.putChar(command);
                    commandBuffer.flip();

                    channel = SocketChannel.open();
                    channel.connect(new InetSocketAddress(args[0], serverPort));
                    channel.write(commandBuffer);

                    messageBuffer = ByteBuffer.wrap(fileName.getBytes());
                    channel.write(messageBuffer);
                    channel.shutdownOutput();

                    // Read filesize from server
                    contentBuffer = ByteBuffer.allocate(8);
                    channel.read(contentBuffer);
                    contentBuffer.flip();
                    final long fileLength = contentBuffer.getLong();

                    System.out.println(fileLength);

                    // create new file from filename
                    File newFile = new File("ClientFiles", fileName);
                    newFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(newFile);
                    fc = fos.getChannel();

                    contentBuffer = ByteBuffer.allocate(1024);
                    long totalBytesRead = 0;
                    bytesRead = 0;
                    while (totalBytesRead + 1024 < fileLength) {
                        bytesRead = channel.read(contentBuffer);
                        contentBuffer.flip();
                        fc.write(contentBuffer);
                        contentBuffer.clear();

                        totalBytesRead += bytesRead;
                    }

                    // get remaining bytes
                    int remainingBytes = Math.toIntExact(fileLength - totalBytesRead);
                    System.out.println(remainingBytes);
                    contentBuffer.clear();
                    contentBuffer = ByteBuffer.allocate(remainingBytes);
                    channel.read(contentBuffer);
                    contentBuffer.flip();
                    fc.write(contentBuffer);

                    // read status code
                    ByteBuffer statusBuf = ByteBuffer.allocate(2);
                    channel.read(statusBuf);
                    statusBuf.flip();

                    System.out.println(StandardCharsets.UTF_8.decode(statusBuf));

                    channel.close();
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



