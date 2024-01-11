import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeInstance {

  private NodeType _nodeType;
  private ConcurrentHashMap<String, Long> nodeMap = new ConcurrentHashMap<>();
  private int nodePort;
  private final int PORT = 8888;
  private final ExecutorService executorService = Executors.newFixedThreadPool(
    10
  ); // Adjust the pool size as needed
  private ServerSocket nodeSocket;

  public NodeInstance(NodeType nodeType) {
    try {
      nodeSocket = new ServerSocket(12345);
    } catch (IOException e1) {
      try {
        nodeSocket = new ServerSocket(0);
      } catch (IOException e2) {
        e1.printStackTrace();
      }
    }
    this.nodePort = nodeSocket.getLocalPort();
    this._nodeType = nodeType;
  }

  private void receive(MulticastSocket multicastSocket) {
    try {
      byte[] receiveData = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(
        receiveData,
        receiveData.length
      );

      while (true) {
        multicastSocket.receive(receivePacket);
        String received = new String(
          receivePacket.getData(),
          0,
          receivePacket.getLength()
        );
        String[] receivedData = received.split("\\|");
        String senderPort = receivedData[0];
        String senderNodeType = receivedData[1];
        Long actual_time_in_millis = System.currentTimeMillis();
        System.out.println(
          "Received heartbeat from [" +
          senderPort +
          " at " +
          actual_time_in_millis +
          "]"
        );
        nodeMap.put(senderPort + " " + senderNodeType, actual_time_in_millis);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void send(MulticastSocket multicastSocket, InetAddress group) {
    try {
      int counter = 0;
      String dataToSend =
        String.valueOf(nodePort) + "|" + this._nodeType.toString();
      DatagramPacket packet = new DatagramPacket(
        dataToSend.getBytes(),
        dataToSend.length(),
        group,
        this.PORT
      );
      while (true) {
        multicastSocket.send(packet);
        Thread.sleep(5000);
        if (counter == 1) {
          System.out.println(
            "\n<<<<<<<<<<<<<<<<<<<<<<CLEANUP THREAD execution:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
          );
          Iterator<Map.Entry<String, Long>> iterator = nodeMap
            .entrySet()
            .iterator();
          while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            long timeDifference = System.currentTimeMillis() - entry.getValue();

            if (timeDifference > 10000) {
              iterator.remove();
              System.out.println(
                "\t Node PORT =  " +
                entry.getKey() +
                " deleted because time difference " +
                timeDifference +
                ">10000."
              );
            }
          }
          System.out.println("\n");
          for (Map.Entry<String, Long> entry : nodeMap.entrySet()) System.out.println(
            "\t Node PORT = " +
            entry.getKey() +
            " -> last heartbeat time = " +
            entry.getValue()
          );
          System.out.println("\n");
          counter = 0;
        } else {
          counter = 1;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    try {
      InetAddress group = InetAddress.getByName("224.0.0.1");
      MulticastSocket multicastSocket = new MulticastSocket(this.PORT);
      multicastSocket.joinGroup(group);
      System.out.println(
        this._nodeType +
        " node on PORT " +
        String.valueOf(this.nodePort) +
        " started!"
      );

      new Thread(() -> receive(multicastSocket)).start();
      new Thread(() -> send(multicastSocket, group)).start();

      //tcp request, with default socket on 12345
      System.out.println(
        String.format(
          "Node on port [%s] is waiting for a connection...",
          String.valueOf(this.nodePort)
        )
      );

      while (true) {
        Socket clientSocket = nodeSocket.accept();
        System.out.println("Connection established with a client.");

        // Submit the client handling task to the thread pool
        executorService.submit(() -> handleClient(clientSocket));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void handleClient(Socket clientSocket) {
    try {
      BufferedReader in = new BufferedReader(
        new InputStreamReader(clientSocket.getInputStream())
      );
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

      // Read the message from the client
      String rawMessage = in.readLine();
      String[] clientMessage = rawMessage.split(" ");
      System.out.println("Received from client: " + rawMessage);

      String message = "";
      switch (clientMessage[0]) {
        case "topology":
          message = handleTopologyRequest();
          break;
        case "service":
          message = handleServiceRequest(clientMessage);
          break;
        default:
          break;
      }

      out.println(message);
      out.close();
      in.close();
      System.out.println(String.format("Sent to client: %s.\n", message));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String handleTopologyRequest(){
    String message = "";
    for (Map.Entry<String, Long> entry : nodeMap.entrySet()){
      message = message + "PORT: " + entry.getKey().split(" ")[0] + "; Node type: "+ entry.getKey().split(" ")[1] +"; last heartbeat: "+ entry.getValue() + "; " + Utils.getMethodsForNodeType(entry.getKey().split(" ")[1]) + "|";
    }
    return message;
  }

  private String handleServiceRequest(String[] clientMessage){
    String message = "";
    String services = Utils.getMethodsForNodeType(_nodeType.toString());
    if(clientMessage.length >1 && services.contains(clientMessage[1])){
      message =  Utils.computeMethod(clientMessage);
    }else{
      message = String.format("The desired service [%s] is not on the node services list.", clientMessage[1]);
    }
    return message;
  }

}
