import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" + "[" + getCurrentTimeStamp() + "]" + super.getMessage() + "\u001B[0m";
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

class InvalidPutCommandException extends Exception {
    public InvalidPutCommandException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" + "[" + getCurrentTimeStamp() + "]" + super.getMessage() + "\u001B[0m";
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

class MaxLengthExceededException extends Exception {
    public MaxLengthExceededException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" + "[" + getCurrentTimeStamp() + "]" + super.getMessage() + "\u001B[0m";
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

class KeyNotFoundException extends Exception {
    public KeyNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" + "[" + getCurrentTimeStamp() + "]" + super.getMessage() + "\u001B[0m";
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

class InvalidGetCommandException extends Exception {
    public InvalidGetCommandException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" + "[" + getCurrentTimeStamp() + "]" + super.getMessage() + "\u001B[0m";
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

public class UDPServer {

    private static final int PORT = 12347;
    private static final String DATA_FILE = "keyValueStore.ser";
    private static Map<String, String> keyValueStore = new HashMap<>();

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    public static void main(String[] args) {
    try (DatagramSocket socket = new DatagramSocket(PORT)) {
        System.out.println("UDP Key-Value Store Server started on port " + PORT);

        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();

            String request = new String(packet.getData(), 0, packet.getLength());
            handleRequest(request, clientAddress, clientPort, socket);
            }
        } catch (IOException e) {
            System.err.println(ANSI_RED + "Error starting the server: " + e.getMessage() + ANSI_RESET);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void handleRequest(String request, InetAddress clientAddress, int clientPort, DatagramSocket socket) throws IOException {
        String[] tokens = request.split(" ");

        if (tokens.length == 0) {
            String errorMessage = new InvalidCommandException("Invalid command").getMessage();
            sendResponse(errorMessage, clientAddress, clientPort, socket);
            return;
        }

        String command = tokens[0];

        switch (command) {
            case PUT:
                try {
                    handlePutRequest(tokens);
                    sendResponse("[" + getCurrentTimeStamp() + "] Key-value pair added successfully", clientAddress, clientPort, socket);
                } catch (InvalidPutCommandException | MaxLengthExceededException e) {
                    String errorMessage = e.getMessage();
                    sendResponse(errorMessage, clientAddress, clientPort, socket);
                }
                break;
            case DELETE:
                try {
                    handleDelRequest(tokens);
                    sendResponse("[" + getCurrentTimeStamp() + "] Key deleted successfully", clientAddress, clientPort, socket);
                } catch (KeyNotFoundException e) {
                    String errorMessage = e.getMessage();
                    sendResponse(errorMessage, clientAddress, clientPort, socket);
                }
                break;
            case GET:
                try {
                    String response = handleGetRequest(tokens);
                    sendResponse(response, clientAddress, clientPort, socket);
                } catch (InvalidGetCommandException e) {
                    String errorMessage = e.getMessage();
                    sendResponse(errorMessage, clientAddress, clientPort, socket);
                }
                break;
            case KEYS:
                String keys = handleKeysRequest();
                sendResponse("[" + getCurrentTimeStamp() + "] Keys in the store: " + keys, clientAddress, clientPort, socket);
                break;
            case QUIT:
                sendResponse("Goodbye!", clientAddress, clientPort, socket);
                break;
            default:
                String errorMessage = new InvalidCommandException("Invalid command").getMessage();
                sendResponse(errorMessage, clientAddress, clientPort, socket);
        }
    }

    private static void handlePutRequest(String[] tokens) throws InvalidPutCommandException, MaxLengthExceededException {
        if (tokens.length < 3) {
            throw new InvalidPutCommandException("Invalid PUT command. Usage: PUT <key> <value>");
        }

        String key = tokens[1];
        String value = tokens[2];

        if (value.length() > 10 || key.length() > 10) {
            throw new MaxLengthExceededException("Invalid length, max length of value and key can be no more than 10 symbols");
        }
        
        if (keyValueStore.containsKey(key)) {
            throw new InvalidPutCommandException("Key already exists in the store");
        }

        
        keyValueStore.put(key, value);
        System.out.println("[" + getCurrentTimeStamp() + "] PUT request handled: Key-value pair added - Key: " + key + ", Value: " + value);
    }
    
    private static void sendResponse(String response, InetAddress clientAddress, int clientPort, DatagramSocket socket) throws IOException {
    byte[] responseData = response.getBytes();
    DatagramPacket packet = new DatagramPacket(responseData, responseData.length, clientAddress, clientPort);
    socket.send(packet);
    System.out.println(response); 

    }


    private static void handleDelRequest(String[] tokens) throws KeyNotFoundException {
        if (tokens.length < 2) {
            throw new KeyNotFoundException("Invalid DELETE command. Usage: DELETE <key>");

        } else {
            String key = tokens[1];
            if (keyValueStore.containsKey(key)) {
                keyValueStore.remove(key);
                System.out.println("[" + getCurrentTimeStamp() + "] DELETE request handled: Key deleted - Key: " + key);
            } else {
                throw new KeyNotFoundException("DELETE request: Key not found - Key: " + key);
            }
        }
    }

    private static String handleGetRequest(String[] tokens) throws InvalidGetCommandException {
        if (tokens.length < 2) {
            throw new InvalidGetCommandException("Invalid GET command. Usage: GET <key>");

        } else {
            String key = tokens[1];
            String value = keyValueStore.get(key);
            return ANSI_RED + "[" + getCurrentTimeStamp() + "] " + (value != null ? value : "Key " + key + " not found") + ANSI_RESET;
        }
    }

    private static String handleKeysRequest() {
        String keys = String.join(", ", keyValueStore.keySet());
        if (keys != null) {
            System.out.println("[" + getCurrentTimeStamp() + "] KEYS request handled: Keys in the store - " + keys);
            return keys;
        } else {
            System.out.println("[" + getCurrentTimeStamp() + "] Keys in the store: NONE ");
            return "NONE";
        }
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

