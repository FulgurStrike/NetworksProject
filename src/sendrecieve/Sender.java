package sendrecieve;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import CMPC3M06.AudioRecorder;

public class Sender implements Runnable {
    static DatagramSocket sendingSocket;
    private AudioRecorder recorder;
    private final String p = "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74"
            + "020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374"
            + "FE1356D6D51C245E485B576625E7EC6F44C42E9A63A36210000000000090563E0"
            + "B7F1A5A6F9A6C5E939A6E317955DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4"
            + "187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A41"
            + "87B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A418"
            + "7B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187"
            + "B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B"
            + "8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8"
            + "AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8A"
            + "C3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC"
            + "3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3"
            + "B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B"
            + "5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5"
            + "DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5D"
            + "CB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DC"
            + "B7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB"
            + "7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7"
            + "A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A"
            + "4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4"
            + "187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A41"
            + "87B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A418"
            + "7B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187"
            + "B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B"
            + "8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8"
            + "AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8A"
            + "C3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC"
            + "3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3"
            + "B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B"
            + "5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5DCB7A4187B8AC3B5D";
    private final BigInteger g = BigInteger.valueOf(2);
    private final Random rand = new Random(System.currentTimeMillis());
    private final long x = rand.nextLong();

    public void audioRecorder()throws Exception{
        recorder = new AudioRecorder();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    // Key exchange method using TCP sockets. Sender acts as the server
    public BigInteger keyExchange() {
        long key = 0;
        BigInteger K = BigInteger.valueOf(0);
        try {

            // Opens a Socket on port 5000
            Socket socket = new Socket("localhost", 5000);

            // Output stream becomes client input stream
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Input stream receives data from client output stream
            DataInputStream input = new DataInputStream(socket.getInputStream());

            BigInteger P = new BigInteger(p, 16);

            BigInteger R = g.modPow(BigInteger.valueOf(x), P);

            // Sends the value of R to the client
            output.writeBytes(R.toString());

            // Reads the value Y sent from the client
            BigInteger Y = new BigInteger(String.valueOf(input.read()));

            K = Y.modPow(BigInteger.valueOf(x), P);

            return K;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return K;
    }


    public void run() {
        InetAddress clientIP = null;
        int port = 55555;
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

        BigInteger symKey = keyExchange();
        System.out.println(symKey);

        int runTime = 10;

        while (true) {
            for (int i = 0; i < Math.ceil(runTime / 0.032); i++) {
                try {
                    byte[] audioBlock = recorder.getBlock();

                    // Allocates a 514 byte long byte buffer
                    ByteBuffer buffer = ByteBuffer.allocate(514);
                    if (audioBlock != null) {

                        // First 2 bytes of the packet will be a short representing the sequence number
                        buffer.putShort((short) i);

                        // Remaining bits will be the audio block
                        buffer.put(audioBlock);
                        DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.capacity(), clientIP, port);
                        sendingSocket.send(packet);
                        System.out.println("audio packet sent, size :" + buffer.capacity() + " bytes");
                    }
                } catch (IOException e) {
                    System.out.println("Error : TextSender: Some random IO error has occurred");
                    e.printStackTrace();
                }
            }
        }
       // for (int i = 0; i < 999; i++) {
       //     try {
       //         String str = "Packet " + i;

       //         byte[] buffer = str.getBytes();

       //         DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientIP, port);

       //         sendingSocket.send(packet);
       //     }catch (IOException e) {
       //         System.out.println("ERROR Sender 2: Some random IO error occured");
       //         e.printStackTrace();
       //     }
       // }
       // sendingSocket.close();
    }
}
