import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String nodeHost = "localhost";
        Socket socket;
        int nodePort;
        PrintWriter out;
        BufferedReader in;
        String message, serverResponse;

        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        try{
            while(true){
                System.out.println("Choose an option:");
                System.out.println("1. List all nodes");
                System.out.println("2. Connect to a node");
                System.out.println("3. Run method on a node");
                System.out.println("4. Exit");

                String choice = userInput.readLine();
                
                switch (choice) {
                    case "1":
                        System.out.println("Enter node port to connect:");
                        nodePort = Integer.parseInt(userInput.readLine());

                        System.out.println(String.format("Trying to connect to %s:%s", nodeHost, nodePort));
                        
                        socket = new Socket(nodeHost, nodePort);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        System.out.println(String.format("Connected to %s:%s", nodeHost, nodePort));

                        message = "topology";
                        out.println(message);
                        System.out.println("Sent to server: " + message);

                        String [] res = in.readLine().split("\\|");
                        System.out.println("Received from server:\n" + String.join("\n", res));
                        System.out.println("Press any key to continue...");
                        userInput.readLine();

                        in.close();
                        out.close();
                        socket.close();

                        break;
                    case "2":
                        System.out.println("Enter node port to connect:");
                        nodePort = Integer.parseInt(userInput.readLine());

                        System.out.println("Enter service to run on node:");
                        String service = userInput.readLine();

                        System.out.println(String.format("Trying to connect to %s:%s", nodeHost, nodePort));
                        
                        socket = new Socket(nodeHost, nodePort);
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        System.out.println(String.format("Connected to %s:%s", nodeHost, nodePort));

                        message = String.format("service %s", service);
                        out.println(message);
                        System.out.println("Sent to server: " + message);

                        serverResponse = in.readLine();
                        System.out.println("Received from server: " + serverResponse);
                        System.out.println("Press any key to continue...");
                        userInput.readLine();

                        in.close();
                        out.close();
                        socket.close();

                        break;
                    case "3":
                        System.out.println("Press any key to continue...");
                        userInput.readLine();
                        break;
                    case "4":
                        System.out.println("Press any key to continue...");
                        userInput.readLine();
                        return;
                    default:
                        return;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
