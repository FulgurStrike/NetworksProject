import sendrecieve.Receiver;
import sendrecieve.Sender;

public class TextDuplex {
    public static void main(String[] args) {

        Sender sender = new Sender();
        Receiver receiver = new Receiver();

        try{
            sender.audioRecorder();
            receiver.audioPlayer();
        }catch(Exception e){
            System.out.println("ERROR : an error initializing has occurred");
            e.printStackTrace();
            return;
        }

        receiver.start();
        sender.start();
    }
}
