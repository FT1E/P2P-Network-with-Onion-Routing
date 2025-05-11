import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageProcessing{


    public static void handleChat(Message message){
        Logger.chat(message.getSourceAddress(), message.getBody());
    }

    public static void handlePeerDiscoveryRequest(PeerConnection peer){
        // todo
        peer.sendMessage(Message.createPEER_DISCOVERY_REPLY(peer.getAddress().getHostAddress()));
    }

    public static void handlePeerDiscoveryReply(Message message){
        // todo
        //      - add only new addresses, so you don't have 2 or more sockets for the same host

        String[] newAddresses = message.getBody().split(";");
        for (int i = 0; i < newAddresses.length; i++) {
            try {
                PeerList.addConnection(new PeerConnection(newAddresses[i]));
            } catch (IOException e) {
                Logger.log("Error in trying to connect with " + newAddresses[i], LogLevel.ERROR);
            }
        }
    }
}
