package sendrecieve;

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import CMPC3M06.AudioPlayer;

import java.nio.ByteBuffer;
import java.util.Random;


public class Receiver implements Runnable {

    static DatagramSocket receivingSocket;
    private AudioPlayer player;
    private final long p = 66371551L;
    private final long g = 6L;
    private final Random rand = new Random(System.currentTimeMillis());
    private final long y = rand.nextLong();

    public void audioPlayer() throws Exception{
       player = new AudioPlayer();
    }
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    // Key exchange method using TCP sockets. Receiver acts as the client
    public BigInteger keyExchange() {
        BigInteger K = BigInteger.valueOf(0);
        try {
            // Listens for incoming server requests on the specified port
            ServerSocket serverSocket = new ServerSocket(5000);

            // Accepts and opens a connection with the server
            Socket socket = serverSocket.accept();

            // OutputStream receives data from Server output stream
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Input stream sends data to Server output stream
            DataInputStream input = new DataInputStream(socket.getInputStream());

            BigInteger R = BigInteger.valueOf(g).modPow(BigInteger.valueOf(y), BigInteger.valueOf(p));

            // Sends the value of R back to the server
            output.writeLong(R.longValue());

            // Reads the value of X sent from the server
            BigInteger X = BigInteger.valueOf(input.readLong());

            K = X.modPow(BigInteger.valueOf(y), BigInteger.valueOf(p));

            return K;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return K;
    }

    public void run() {
        int port = 55555;

        try {
            receivingSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            System.out.println("ERROR Receiver: Could not open UDP packet to send from");
            e.printStackTrace();
            System.exit(0);
        }

        BigInteger symKey = keyExchange();

        boolean running = true;

        while (running){
            try{
                byte[] buffer = new byte[514];
                DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);


                receivingSocket.receive(packet);

                // Wraps the packet into a ByteBuffer for more functionality
                ByteBuffer byteBuffer = ByteBuffer.wrap(packet.getData());

                // Transfers the first 2 bytes in the byte buffer which will be the sequence number
                short sequenceNumber = byteBuffer.getShort();

                byte[] audioBlock = new byte[512];

                // Retrieves the rest of packet bytes which is the entire audio block
                byteBuffer.get(audioBlock);
                if (packet.getLength() > 0){
                    player.playBlock(audioBlock);
                    System.out.println("received audioblock " + sequenceNumber + " of size of : " + audioBlock.length + " bytes");
                }
            } catch(IOException e){
                System.out.println("ERROR : Receiver : Some random IO error has occurred");
                e.printStackTrace();
            }
        }
        receivingSocket.close();
//        for (int i = 0; i < 999; i++) {
//            try {
//                byte[] buffer = new byte[200];
//                DatagramPacket packet = new DatagramPacket(buffer, 0, 80);
//
//                receivingSocket.setSoTimeout(30);
//
//                try {
//                    receivingSocket.receive(packet);
//                    String str = new String(buffer);
//                    System.out.println(str);
//                } catch (SocketTimeoutException e) {
//                    System.out.println(".");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (IOException e) {
//                System.out.println("ERROR Receiver: Some IO error occured");
//                e.printStackTrace();
//
//            }
//        }
//        receivingSocket.close();
    }
}
