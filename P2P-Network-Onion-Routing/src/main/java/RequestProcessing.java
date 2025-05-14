import Util.LogLevel;
import Util.Logger;

import java.io.IOException;

public class RequestProcessing {


    public static void handleCHAT(Message message, PeerConnection peer){
        Logger.chat(peer.getAddress().getHostAddress(), message.getBody());
        try {
            peer.sendMessage(Message.createReply(message.getId(), MessageSubType.CHAT, "Hello, chat message was received!"));
        } catch (IOException e) {
            Logger.log("Error at Request.handleCHAT()", LogLevel.WARN);
        }
    }

    public static void handlePEER_DISCOVERY(Message message, PeerConnection peer){
        try {
            peer.sendMessage(Message.createReply(message.getId(), MessageSubType.PEER_DISCOVERY, PeerList.getAddressList(peer.getAddress().getHostAddress())));
        } catch (IOException e) {
            Logger.log("Error at Request.handlePEER_DISCOVERY()", LogLevel.WARN);
        }
    }
}
