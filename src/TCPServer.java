import java.io.File;

/**
 * I'm leaving a note in here on java file handling since it's pretty extensively documented
 * and you can do a bunch of cool stuff
 *
 * https://www.w3schools.com/java/java_files.asp
 */

public class TCPServer {
    public static void main(String[] args) {
        System.out.println("hi tcp client");

        // --Deleting a server file-- I cannot attest for the functionality of this code
        String fileName = ""; //whatever is passed in from the client
        File file = new File("ServerFiles/" + fileName);
        if (file.exists()) {
            file.delete();
            //send a status code: success
        } else {
            //send a status code: fail
        }
    }
}
