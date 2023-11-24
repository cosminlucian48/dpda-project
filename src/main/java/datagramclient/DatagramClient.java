package datagramclient;

import java.net.*;
import java.io.*;

public class DatagramClient {
    public static void main(String[] args) throws IOException {
//adresa IP si portul la care ruleaza serverul
//127.0.0.1 sau localhost - reprezinta calculatorul local
        InetAddress address = InetAddress.getByName("127.0.0.1");
        int port = 12000;
        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buf = null;
        try {
//Construirea unui socket pentru comunicare
            socket = new DatagramSocket();
//Construirea si trimiterea pachetului cu cererea catre server
            String s = "John";
            buf = s.getBytes();
            packet = new DatagramPacket(buf, buf.length, address, port);
            socket.send(packet);
//Se asteapta pachetul cu raspunsul de la server
            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
//Afisarea raspunsului
            String raspuns = new String(
                    packet.getData(),
                    packet.getOffset(),
                    packet.getLength());
            System.out.println("raspuns: " + raspuns);
        } finally {
            socket.close();
        }
    }
}