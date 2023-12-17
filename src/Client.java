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
                System.out.println("\nChoose an option:");
                System.out.println("Note: By default if port 12345 is empty, that will be the first port a node will try to use.");
                System.out.println("[topology <NODE_PORT>]. Show topology from node perspective.");
                System.out.println("[service <NODE_PORT> <SERVICE>]. Run service on node.");
                System.out.println("[exit]. Exit\n");
                System.out.print(">>");

                String[] choice = userInput.readLine().split(" ");
                message = choice[0];
                nodePort = Integer.valueOf(choice[1]);
                
                switch (choice[0]) {
                    case "topology":
                        if (choice.length<2){
                            System.out.println("To few parameters were given...");
                            System.out.println("Press any key to continue...");
                            userInput.readLine();
                            break;
                        }
                        //System.out.println(String.format("Trying to connect to %s:%s", nodeHost, nodePort));
                        try {
                            socket = new Socket(nodeHost, nodePort);
                        } catch (Exception e) {
                            System.out.println("Could not establish connection to the requested node...");
                            System.out.println("Press any key to continue...");
                            userInput.readLine();
                            break;
                        }
                        
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        //System.out.println(String.format("Connected to %s:%s", nodeHost, nodePort));

                        out.println(message);
                        System.out.println("Sent to node: " + message);

                        String [] res = in.readLine().split("\\|");
                        System.out.println("Received from node:\n" + String.join("\n", res));
                        System.out.println("Press any key to continue...");
                        userInput.readLine();

                        in.close();
                        out.close();
                        socket.close();
                        break;

                    case "service":
                        if (choice.length<3){
                            System.out.println("To few parameters were given...");
                            System.out.println("Press any key to continue...");
                            userInput.readLine();
                            break;
                        }
                        String service = choice[2];
                        
                        //System.out.println(String.format("Trying to connect to %s:%s", nodeHost, nodePort));
                        
                        try {
                            socket = new Socket(nodeHost, nodePort);
                        } catch (Exception e) {
                            System.out.println("Could not establish connection to the requested node...");
                            System.out.println("Press any key to continue...");
                            userInput.readLine();
                            break;
                        }
                        out = new PrintWriter(socket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        //System.out.println(String.format("Connected to %s:%s", nodeHost, nodePort));

                        message = message + " " + service;
                        out.println(message);
                        System.out.println("Sent to node: " + message);

                        serverResponse = in.readLine();
                        System.out.println("Received from node: " + serverResponse);
                        System.out.println("Press any key to continue...");
                        userInput.readLine();

                        in.close();
                        out.close();
                        socket.close();
                        break;

                    case "exit":
                        System.out.println("Press any key to continue...");
                        userInput.readLine();
                        return;

                    default:
                        System.out.println("The command you entered does not exist...");
                        break;
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
