import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class Client {
    private static final String DEFAULT_NODE_HOST = "localhost";
    private static final int DEFAULT_NODE_PORT = 12345;

    public static void main(String[] args) {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        try{
            while(true){
                displayMenu();
                String[] choice = userInput.readLine().split(" ");
                processChoice(choice, userInput);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayMenu() {
        System.out.println("\nChoose an option:");
        System.out.println("Note: By default, if port 12345 is empty, that will be the first port a node will try to use.");
        System.out.println("[topology <NODE_PORT>]. Show topology from node perspective.");
        System.out.println("[service <NODE_PORT> <SERVICE> <VERSION>]. Run service on node.");
        System.out.println("[exit]. Exit\n");
        System.out.print(">>");
    }

    private static void processChoice(String[] choice, BufferedReader userInput) throws IOException {
        String message = choice[0];
        int nodePort;
        try{
            nodePort = (choice.length > 1) ? Integer.parseInt(choice[1]) : DEFAULT_NODE_PORT;
        }catch(Exception e){
            System.out.println("Bad parameters...");
            return;
        }

        switch (message) {
            case "topology":
                handleTopologyRequest(nodePort, userInput);
                break;
            case "service":
                if (choice.length > 3) {
                    String service = choice[2];
                    String version = choice[3];
                    String[] data = Arrays.copyOfRange(choice, 2, choice.length);
                    handleServiceRequest(nodePort, data, userInput);
                } else {
                    System.out.println("Too few parameters were given...");
                }
                break;
            case "exit":
                System.out.println("Press any key to continue...");
                userInput.readLine();
                System.exit(0);
            default:
                System.out.println("The command you entered does not exist...");
                break;
        }
    }

    private static void handleTopologyRequest(int nodePort, BufferedReader userInput) throws IOException {
        try (Socket socket = new Socket(DEFAULT_NODE_HOST, nodePort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("topology");
            System.out.println(String.format("Sent to node %s: topology", nodePort));

            String[] res = in.readLine().split("\\|");
            System.out.println("Received from node:\n" + String.join("\n", res));
            System.out.println("Press any key to continue...");
            userInput.readLine();
        } catch (IOException e) {
            System.out.println("Could not establish connection to the requested node...");
            System.out.println("Press any key to continue...");
            userInput.readLine();
        }
    }

    private static void handleServiceRequest(int nodePort, String[] data, BufferedReader userInput) throws IOException {
        try (Socket socket = new Socket(DEFAULT_NODE_HOST, nodePort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = String.format("service %s", String.join(" ", data));
            out.println(message);
            System.out.println("Sent to node: " + message);

            String serverResponse = in.readLine();
            System.out.println("Received from node: " + serverResponse);
            System.out.println("Press any key to continue...");
            userInput.readLine();
        } catch (IOException e) {
            System.out.println("Could not establish connection to the requested node...");
            System.out.println("Press any key to continue...");
            userInput.readLine();
        }
    }
}
