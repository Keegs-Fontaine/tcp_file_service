import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class TCPServer {
    static final int PORT = 3000;
    static final String DEFAULT_FILE_DIR = "ServerFiles";

    private static void sendFailure(SocketChannel ch) throws IOException {
        ch.write(ByteBuffer.wrap("F".getBytes()));
    }

    private static void sendSuccess(SocketChannel ch) throws IOException {
        ch.write(ByteBuffer.wrap("S".getBytes()));
    }

    private static String decodeBuffer(ByteBuffer buf) {
        return StandardCharsets.UTF_8.decode(buf).toString();
    }

    public static void main(String[] args) {
        try (ServerSocketChannel listenSocket = ServerSocketChannel.open()) {
            listenSocket.bind(new InetSocketAddress(PORT));

            while (true) {
                SocketChannel serverChannel = listenSocket.accept();

                ByteBuffer commandBuffer = ByteBuffer.allocate(2);
                int bytesRead = serverChannel.read(commandBuffer);
                commandBuffer.flip();

                char command = commandBuffer.getChar();

                switch (command) {
                    case 'L': {
                        System.out.println("Listing Files");

                        File directory = new File(DEFAULT_FILE_DIR);

                        if (!directory.exists() || !directory.isDirectory()) {
                            throw new RuntimeException("Default file directory doesn't exist");
                        }

                        File[] files = directory.listFiles();

                        StringBuilder message = new StringBuilder();

                        if (files == null || files.length == 0) {
                            System.out.println("No files found");
                            serverChannel.write(ByteBuffer.wrap("No Files Found".getBytes()));
                            sendSuccess(serverChannel);

                            break;
                        }

                        for (File file : files) {
                            message.append(file.getName()).append("\n");
                        }

                        ByteBuffer responseBuffer = ByteBuffer.wrap(message.toString().getBytes());

                        try {
                            serverChannel.write(responseBuffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                            sendFailure(serverChannel);

                            break;
                        }

                        sendSuccess(serverChannel);
                        break;
                    }

                    case 'X': {
                        ByteBuffer filename = ByteBuffer.allocate(1024);

                        serverChannel.read(filename);
                        filename.flip();

                        File fileToDelete = new File(DEFAULT_FILE_DIR, decodeBuffer(filename));

                        if (fileToDelete.delete()) {
                            System.out.println("File Deleted");

                            sendSuccess(serverChannel);
                        } else {
                            sendFailure(serverChannel);
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

                        File fileToRename = new File(DEFAULT_FILE_DIR, splitFilenames[0]);

                        boolean didWork = fileToRename.renameTo(new File(DEFAULT_FILE_DIR, splitFilenames[1]));

                        if (didWork) {
                            sendSuccess(serverChannel);
                        } else {
                            sendFailure(serverChannel);
                        }

                        break;
                    }

                    case 'U': {
                        System.out.println("Uploading File");

                        ByteBuffer filenameLength = ByteBuffer.allocate(4);
                        serverChannel.read(filenameLength);
                        filenameLength.flip();

                        int actualLength = filenameLength.getInt();

                        ByteBuffer filename = ByteBuffer.allocate(actualLength);
                        serverChannel.read(filename);
                        filename.flip();

                        String decodedFilename = decodeBuffer(filename);

                        System.out.println(decodedFilename);

                        File fileToUpload = new File(DEFAULT_FILE_DIR, decodedFilename);
                        boolean didCreate = fileToUpload.createNewFile();

                        if (!didCreate) {
                            sendFailure(serverChannel);

                            break;
                        }

                        FileOutputStream fos = new FileOutputStream(fileToUpload);
                        FileChannel fc = fos.getChannel();

                        ByteBuffer contentBuffer = ByteBuffer.allocate(1024);

                        while (serverChannel.read(contentBuffer) != -1) {
                            contentBuffer.flip();
                            fc.write(contentBuffer);
                            contentBuffer.clear();
                        }

                        sendSuccess(serverChannel);

                        fc.close();

                        break;
                    }

                    case 'D': {
                        System.out.println("Downloading File");

                        ByteBuffer filename = ByteBuffer.allocate(1024);
                        serverChannel.read(filename);
                        filename.flip();

                        String decodedFilename = decodeBuffer(filename);

                        File fileToDownload = new File(DEFAULT_FILE_DIR, decodedFilename);
                        FileInputStream fileInputStream = new FileInputStream(fileToDownload);
                        FileChannel fis = fileInputStream.getChannel();

                        long fileLength = fileToDownload.length();
                        ByteBuffer lengthBuffer = ByteBuffer.allocate(8);
                        lengthBuffer.putLong(fileLength);

                        lengthBuffer.flip();
                        serverChannel.write(lengthBuffer);

                        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);

                        while (fis.read(responseBuffer) != -1) {
                            responseBuffer.flip();

                            serverChannel.write(responseBuffer);
                            responseBuffer.clear();
                        }

                        responseBuffer.flip();
                        fis.close();

                        sendSuccess(serverChannel);

                        fileInputStream.close();

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
