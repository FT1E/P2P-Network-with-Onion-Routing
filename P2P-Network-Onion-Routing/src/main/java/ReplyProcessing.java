import Util.LogLevel;
import Util.Logger;

import java.io.IOException;

public class ReplyProcessing {

    public static void handleCHAT(Message message, PeerConnection peer){
        Logger.chat(peer.getAddress().getHostAddress(), message.getBody());
    }

    public static void handlePEER_DISCOVERY(Message message){
        String[] addresses = message.getBody().split(";");
        for (int i = 0; i < addresses.length; i++) {
            try {
                new PeerConnection(addresses[i]);
            } catch (IOException e) {
                Logger.log("Error in trying to connect with " + addresses[i], LogLevel.ERROR);
            }
        }
    }

}
