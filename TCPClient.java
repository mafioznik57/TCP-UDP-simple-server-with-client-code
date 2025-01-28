import java.io.*;
import java.net.Socket;
import java.net.ConnectException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TCPClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12347;

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean connected = false;

        while (!connected) {
            try {
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                System.out.println("Connected to TCP Key-Value Store Server");
                connected = true;

                while (true) {
                    displayMenu();
                    System.out.print("> ");
                    String input = scanner.nextLine();

                    writer.println(input);

                    String response = reader.readLine();
                    
                    if (input.equals(QUIT)) {
                        System.out.println("Server response: " + response);
                        break;
                    } else if (response == null) {
                        System.out.println(ANSI_RED + "[" + getCurrentTimeStamp() + "] Unknown I/O Error. Command Not Successful" + ANSI_RESET);
                    } else {
                        System.out.println("Server response: " + response);
                    }
                }

                socket.close(); 

            } catch (ConnectException e) {
                System.out.println(ANSI_RED + "[" + getCurrentTimeStamp() + "] Unknown I/O Error. Command Not Successful" + ANSI_RESET);
                System.out.println("Trying to reconnect...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void displayMenu(){
        System.out.println("Available commands:");
        System.out.println(PUT + " <key> <value>");
        System.out.println(GET + " <key>");
        System.out.println(DELETE + " <key>");
        System.out.println(KEYS);
        System.out.println(QUIT);
        System.out.println("Enter command:");
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}




