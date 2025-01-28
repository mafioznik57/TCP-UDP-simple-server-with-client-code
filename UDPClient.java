import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UDPClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12347;
    private static final int BUFFER_SIZE = 1024;

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (DatagramSocket clientSocket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);

            while (true) {
                displayMenu();
                System.out.print("> ");
                String input = scanner.nextLine();

                byte[] sendData = input.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);

                if (input.equals(QUIT)) {
                    System.out.println("Client disconnected");
                    break;
                }

                byte[] receiveData = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            System.out.println(ANSI_RED + "[" + getCurrentTimeStamp() + "] Unknown I/O Error. Command Not Successful" + ANSI_RESET);
            e.printStackTrace();
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
