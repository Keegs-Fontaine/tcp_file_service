import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Need <serverIP> and <serverPort>.");
            return;
        }

        ExecutorService es = Executors.newFixedThreadPool(4);

        Scanner keyboard = new Scanner(System.in);
        boolean online = true;
        char command;

        while (online) {

            System.out.println("Enter a command: " +
                    "\nL - list all files" +
                    "\nX - delete a file" +
                    "\nR - rename a file" +
                    "\nU - upload a file" +
                    "\nD - download a file" +
                    "\nQ - quit program\n");
            String userInput = keyboard.nextLine();
            command = userInput.toUpperCase().charAt(0);
            if (command == 'Q') {
                online = false;
            }
            es.submit(new RunClientTask(args, command));
        }
    }
}



