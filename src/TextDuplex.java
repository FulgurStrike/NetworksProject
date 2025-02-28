import sendrecieve.Receiver;
import sendrecieve.Sender;

public class TextDuplex {
    public static void main(String[] args) {

        Sender sender = new Sender();
        Receiver receiver = new Receiver();

        try{
            receiver.audioPlayer();
            sender.audioRecorder();
        }catch(Exception e){
            System.out.println("ERROR : an error initializing has occurred");
            e.printStackTrace();
            return;
        }

        receiver.start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        sender.start();
    }
}
