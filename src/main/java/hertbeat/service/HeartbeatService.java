package hertbeat.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatService {

    public static void main(String[] args) {
        ConcurrentHashMap<String, Long> nodeMap = new ConcurrentHashMap<>(); //concurrent map
        try {
            UUID my_unique_id_hopefully = UUID.randomUUID();

            InetAddress group = InetAddress.getByName("224.0.0.1");
            int port = 8888;

            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(group);
            System.out.println(my_unique_id_hopefully + " Started!");

            //receiver
            Thread receiverThread = new Thread(() -> {
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
            });
            receiverThread.start();
            //---------------------------------------------------------------------------------------------------------

            //sender
            Thread senderThread = new Thread(() -> {
                try {
                    int counter = 0;
                    DatagramPacket packet = new DatagramPacket(String.valueOf(my_unique_id_hopefully).getBytes(), String.valueOf(my_unique_id_hopefully).length(), group, port);
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
            });
            senderThread.start();
            //---------------------------------------------------------------------------------------------------------

            //clean up
//            Thread cleanUpThread = new Thread(() -> {
//                try {
//                    while (true) {
//                        Thread.sleep(10000);
//                        System.out.println("\n<<<<<<<<<<<<<<<<<<<<<<CLEANUP THREAD:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
//                        Iterator<Map.Entry<String, String>> iterator = nodeMap.entrySet().iterator();
//                        while (iterator.hasNext()) {
//                            Map.Entry<String, String> entry = iterator.next();
//                            long timeDifference = System.currentTimeMillis() - Long.parseLong(entry.getValue());
//
//                            if (timeDifference > 10000) {
//                                iterator.remove();
//                                System.out.println("Entry with key " + entry.getKey() + " deleted because time difference " + timeDifference + ">10000.");
//                            }
//                        }
//                        System.out.println("\n");
//                        System.out.println(Arrays.asList(nodeMap));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//            cleanUpThread.start();
            //---------------------------------------------------------------------------------------------------------


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
