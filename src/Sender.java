
import java.io.*;
import java.net.*;

import CMPC3M06.AudioRecorder;

public class Sender
{
    public static void main(String[] args) throws Exception
    {
        int port = 5555;

        InetAddress clientIP = null;
        AudioRecorder recorder = new AudioRecorder();

        try
        {
            //temp localhost
            clientIP =InetAddress.getByName("localhost");
        } catch(UnknownHostException e)
        {
            System.err.println("ERROR : TextSender : could not find client IP ");
            e.printStackTrace();
            System.exit(0);
        }
    }

}
