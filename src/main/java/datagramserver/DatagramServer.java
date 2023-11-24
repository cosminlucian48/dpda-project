package datagramserver;

import java.net.*;
import java.io.*;
public class DatagramServer {
    public static final int PORT = 12000;
    private DatagramSocket socket = null;
    private DatagramPacket cerere, raspuns = null;
    public DatagramServer() throws IOException {
        socket = new DatagramSocket(PORT);
    }

    public void startServer() throws IOException {
        try {
            while (true) {
                byte[] buf = null;
//Crearea pachetului in care va fi receptionata cererea
                buf = new byte[256];
                cerere = new DatagramPacket(buf, buf.length);
//Preluarea pachetului cu cererea
                socket.receive(cerere);
//Aflarea adresei si a portului de la care vine cererea
                InetAddress adresa = cerere.getAddress();
                int port = cerere.getPort();
                System.out.println("Adresa si port client: " +
                        adresa + ":" + port);
//Construirea raspunsului
                String s = "Hello " +
                        new String(cerere.getData(),
                                cerere.getOffset(), cerere.getLength());
                buf = s.getBytes();
//Trimite un pachet cu raspunsul catre client
                raspuns = new DatagramPacket(buf, buf.length, adresa, port);
                socket.send(raspuns);
            }
        } finally {
            socket.close();
        }
    }
    public static void main(String[] args) throws IOException {
        DatagramServer ds = new DatagramServer();
        ds.startServer();
    }
}