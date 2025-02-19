package sendrecieve;

import java.io.*;
import java.net.*;

public class Sender implements Runnable {
    static DatagramSocket sendingSocket;

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        int port = 55555;
        InetAddress clientIP = null;
        try {
            //temp localhost
            clientIP =InetAddress.getByName("localhost");
        } catch(UnknownHostException e) {
            System.err.println("ERROR : TextSender : could not find client IP ");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            sendingSocket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("ERROR Sender 1: Could not open UDP packet to send from");
            e.printStackTrace();
            System.exit(0);
        }

        for (int i = 0; i < 999; i++) {
            try {
                String str = "Packet " + i;

                byte[] buffer = str.getBytes();

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, port);

                sendingSocket.send(packet);
            }catch (IOException e) {
                System.out.println("ERROR Sender 2: Some random IO error occured");
                e.printStackTrace();
            }
        }
        sendingSocket.close();
    }
}
