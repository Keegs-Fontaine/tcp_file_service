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

            es.submit(new RunClientTask(serverPort, args, command));

        } while (command != 'Q');
    }
}



