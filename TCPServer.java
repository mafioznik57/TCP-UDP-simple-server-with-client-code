import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class InvalidCommandException extends Exception {
    public InvalidCommandException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
    }
    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}
class EmtyStoreException extends Exception {
    public EmtyStoreException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
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
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
    }
    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}
class InvalidChangeCommandException extends Exception {
    public InvalidChangeCommandException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
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
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
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
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
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
        return "\u001B[31m" +"["+getCurrentTimeStamp()+"]"+ super.getMessage() + "\u001B[0m";
    }
    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}

public class TCPServer {

    private static final int PORT = 12347;
    private static final String DATA_FILE = "keyValueStore.ser";
    private static Map<String, String> keyValueStore = new HashMap<>();


    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";
    private static final String HELP = "HELP";
    private static final String CHANGE = "CHANGE";
    private static final String VALUES = "VALUES";
    private static final Lock lock = new ReentrantLock();


    
    private static boolean isRunning = true;

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";



    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Key-Value Store Server started on port " + PORT);

            while (isRunning) { 
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                executor.submit(() -> handleClientRequest(clientSocket));
            }
        } catch (IOException e) {
            handleIOException(e);
        } finally {
            executor.shutdown();
            shutdownServer();
        }
    }

    private static void shutdownServer() {
        try {
            System.out.println("Shutting down server...");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private static void handleClientRequest(Socket clientSocket) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

        String clientSocketIP = clientSocket.getInetAddress().toString();
        int clientSocketPort = clientSocket.getPort();

        String request;
        while ((request = reader.readLine()) != null) {
            String[] tokens = request.split(" ");

            if (tokens.length == 0) {
                String errorMessage = new InvalidCommandException("Invalid command").getMessage();
                writer.println(errorMessage);
                System.out.println(errorMessage); 
                continue;
            }

            String command = tokens[0];

            switch (command) {
                case PUT:
                    try {
                        handlePutRequest(clientSocketIP, clientSocketPort, tokens, writer);
                    } catch (InvalidPutCommandException | MaxLengthExceededException e) {
                        String errorMessage = e.getMessage();
                        writer.println(errorMessage);
                        System.out.println(errorMessage); 
                    }
                    break;
                case DELETE:
                    try {
                        handleDelRequest(clientSocketIP, clientSocketPort, tokens, writer);
                    } catch (KeyNotFoundException e) {
                        String errorMessage = e.getMessage();
                        writer.println(errorMessage);
                        System.out.println(errorMessage);
                    }
                    break;
                case GET:
                    try {
                        handleGetRequest(clientSocketIP, clientSocketPort, tokens, writer);
                    } catch (InvalidGetCommandException e) {
                        String errorMessage = e.getMessage();
                        writer.println(errorMessage);
                        System.out.println(errorMessage); 
                    }
                    break;
                case KEYS:
                    try{
                        handleKeysRequest(clientSocketIP, clientSocketPort, writer);
                    }catch(EmtyStoreException e){
                         String errorMessage = e.getMessage();
                         writer.println(errorMessage);
                         System.out.println(errorMessage);
                    }
                    
                    break;
                case QUIT:
                    writer.println("Goodbye!");
                    clientSocket.close();
                    System.out.println("[" + getCurrentTimeStamp() + "] Client requested to quit");
                    return;
                case CHANGE:
                    try{
                        handleChangeRequest(clientSocketIP, clientSocketPort, tokens, writer);
                    }catch(InvalidChangeCommandException | MaxLengthExceededException e){
                        String errorMessage = e.getMessage();
                        writer.println(errorMessage);
                        System.out.println(errorMessage);
                    }
                    break;
                case VALUES:
                    try{
                        handleValuesRequest(clientSocketIP, clientSocketPort, writer);
                    }catch(EmtyStoreException e){
                         String errorMessage = e.getMessage();
                         writer.println(errorMessage);
                         System.out.println(errorMessage);
                    }
                    break;
                default:
                    String errorMessage = new InvalidCommandException("Invalid command").getMessage();
                    writer.println(errorMessage);
                    System.out.println(errorMessage);
                }
            }
        } catch (IOException e) {
            String errorMessage = ANSI_RED + "[" + getCurrentTimeStamp() + "] Error handling client: " + e.getMessage() + ANSI_RESET;
            System.err.println(errorMessage);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                String errorMessage = ANSI_RED + "[" + getCurrentTimeStamp() + "] Error closing client socket: " + e.getMessage() + ANSI_RESET;
                System.err.println(errorMessage);
            }
            System.out.println("Client disconnected: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
        }
    }

    private static void handlePutRequest(String clientSocketIP, int clientSocketPort, String[] tokens, PrintWriter writer) throws InvalidPutCommandException, MaxLengthExceededException {
        
           
            if (tokens.length < 3 || tokens.length > 3) {
                throw new InvalidPutCommandException("Invalid PUT command. Usage: PUT <key> <value>");
            } else {
                String key = tokens[1];
                String value = tokens[2];
                if (value.length() > 10 || key.length() > 10) {
                    throw new MaxLengthExceededException("Invalid length, max length of value and key can be no more than 10 symbols");
                } else if(!keyValueStore.containsKey(key)){
                    keyValueStore.put(key, value);
                    writer.println("[" + getCurrentTimeStamp() + "] Key-value pair added successfully");
                    System.out.println("[" + getCurrentTimeStamp() + "] PUT request handled: Key-value pair added - Key: " + key + ", Value: " + value);
                }else{
                    System.out.println(ANSI_RED+"key already executed error "+ANSI_RESET);
                    writer.println(ANSI_RED+"This key already executed place enter value with another key! "+ANSI_RESET);
                }
            }
        
    }
    private static void handleChangeRequest(String clientSocketIP, int clientSocketPort, String[] tokens, PrintWriter writer) throws InvalidChangeCommandException, MaxLengthExceededException {
           
            if (tokens.length < 3 || tokens.length > 3) {
                throw new InvalidChangeCommandException("Invalid CHANGE command. Usage: CHANGE <key> <value>");
            } else {
                String key = tokens[1];
                String value = tokens[2];
                if (value.length() > 10 || key.length() > 10) {
                    throw new MaxLengthExceededException("Invalid length, max length of value and key can be no more than 10 symbols");
                } else {
                    if (keyValueStore.containsKey(key)) {
                        keyValueStore.put(key, value);
                        writer.println("[" + getCurrentTimeStamp() + "] Value pair changed successfully");
                        System.out.println("[" + getCurrentTimeStamp() + "] CHANGE request handled: Key-value pair changed - Key: " + key + ", Value: " + value);
                    } else {
                        writer.println("[" + getCurrentTimeStamp() + "] Key not found in the store");
                        System.out.println("[" + getCurrentTimeStamp() + "] CHANGE request: Key not found - Key: " + key);
                    }
                }
            }
    }

    private static void handleDelRequest(String clientSocketIP, int clientSocketPort, String[] tokens, PrintWriter writer) throws KeyNotFoundException {
           
            if (tokens.length < 2 || tokens.length > 2) {
                throw new KeyNotFoundException("Invalid DELETE command. Usage: DELETE <key>");
                
            } else {
                String key = tokens[1];
                if (keyValueStore.containsKey(key)) {
                    keyValueStore.remove(key);
                    writer.println("[" + getCurrentTimeStamp() + "] Key deleted successfully");
                    System.out.println("[" + getCurrentTimeStamp() + "] DELETE request handled: Key deleted - Key: " + key);
                } else {
                    throw new KeyNotFoundException("DELETE request: Key not found - Key: " + key);
                }
            }
        
    }

    private static void handleGetRequest(String clientSocketIP, int clientSocketPort, String[] tokens, PrintWriter writer) throws InvalidGetCommandException {
        
            if (tokens.length < 2 || tokens.length > 2) {
                throw new InvalidGetCommandException("Invalid GET command. Usage: GET <key>");
                
            } else {
                String key = tokens[1];
                String value = keyValueStore.get(key);
                writer.println("[" + getCurrentTimeStamp() + "] " + (value != null ? value : "Key "+key+" not found"));
                System.out.println("[" + getCurrentTimeStamp() + "] GET request handled: Key: " + key + ", Value: " + (value != null ? value : "Key not found"));
            }
        
    }

    private static void handleKeysRequest(String clientSocketIP, int clientSocketPort, PrintWriter writer) throws EmtyStoreException {
        
            if(!keyValueStore.isEmpty()){
                String keys = String.join(", ", keyValueStore.keySet());
                writer.println("[" + getCurrentTimeStamp() + "] Keys in the store: " + keys);
                System.out.println("[" + getCurrentTimeStamp() + "] KEYS request handled: Keys in the store - " + keys);
            }else{
                throw new EmtyStoreException("Store is emty right now this comad not unavailable!"); 
            }
        
    }
    private static void handleIOException(IOException e) {
        System.out.println(ANSI_RED + "[" + getCurrentTimeStamp() + "] Unknown I/O Error. Command Not Successful" + ANSI_RESET);
    }

    private static void handleValuesRequest(String clientSocketIP, int clientSocketPort, PrintWriter writer) throws EmtyStoreException{
            
            if (!keyValueStore.isEmpty()) {
                StringBuilder values = new StringBuilder();
                for (String value : keyValueStore.values()) {
                    values.append(value).append(", ");
                }
                values.delete(values.length() - 2, values.length());
                writer.println("[" + getCurrentTimeStamp() + "] Values in the store: " + values.toString());
                System.out.println("[" + getCurrentTimeStamp() + "] Values in the store: " + values.toString());
            } else {
                throw new EmtyStoreException("Store is emty right now this comad not unavailable!");
            }
        
    }

    private static String getCurrentTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }
}




