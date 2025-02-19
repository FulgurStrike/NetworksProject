import sendrecieve.Receiver;
import sendrecieve.Sender;

public class TextDuplex {
    public static void main(String[] args) {

        Sender sender = new Sender();
        Receiver receiver = new Receiver();

        receiver.start();
        sender.start();

        
    }
}
