import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Node {
    private String _nodeType = "";
    private ConcurrentHashMap<String, Long> nodeMap = new ConcurrentHashMap<>();
    private final UUID my_unique_id_hopefully = UUID.randomUUID();
    private final int PORT = 8888;

    public Node(String nodeType){
        this._nodeType=nodeType;
    }

    public void receive(MulticastSocket multicastSocket){
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (true) {
                multicastSocket.receive(receivePacket);
                String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String senderUUID = received.split(" ")[0];
                Long actual_time_in_millis = System.currentTimeMillis();
                System.out.println("Received heartbeat from [" + senderUUID + " at " + actual_time_in_millis + "]");
                nodeMap.put(senderUUID, actual_time_in_millis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(MulticastSocket multicastSocket, InetAddress group){
        try {
            int counter = 0;
            DatagramPacket packet = new DatagramPacket(String.valueOf(my_unique_id_hopefully).getBytes(), String.valueOf(my_unique_id_hopefully).length(), group, this.PORT);
            while (true) {
                multicastSocket.send(packet);
                Thread.sleep(5000);
                if (counter == 1) {
                    System.out.println("\n<<<<<<<<<<<<<<<<<<<<<<CLEANUP THREAD execution:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    Iterator<Map.Entry<String, Long>> iterator = nodeMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Long> entry = iterator.next();
                        long timeDifference = System.currentTimeMillis() - entry.getValue();

                        if (timeDifference > 10000) {
                            iterator.remove();
                            System.out.println("\t Node UUID =  " + entry.getKey() + " deleted because time difference " + timeDifference + ">10000.");
                        }
                    }
                    System.out.println("\n");
                    for (Map.Entry<String,Long> entry : nodeMap.entrySet())
                        System.out.println("\t Node UUID = " + entry.getKey() +
                                " -> last heartbeat time = " + entry.getValue());
//                            System.out.println(Arrays.asList(nodeMap));
                    System.out.println("\n");
                    counter = 0;
                }else{
                    counter = 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void start(){
        try{
            InetAddress group = InetAddress.getByName("224.0.0.1");
            MulticastSocket multicastSocket = new MulticastSocket(this.PORT);
            multicastSocket.joinGroup(group);
            System.out.println(this._nodeType + " node with UUID " + this.my_unique_id_hopefully + " started!");

            new Thread(() -> receive(multicastSocket)).start();
            new Thread(() -> send(multicastSocket, group)).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if(args.length!=1){
            System.out.println("This node should receive only the node-type argument.");
            return;
        }
        String _nodeType = args[0];
        Node node = new Node(_nodeType);
        node.start();
    }
}
