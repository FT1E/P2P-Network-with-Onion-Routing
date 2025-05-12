import Util.LogLevel;
import Util.Logger;

import java.io.IOException;

public class MessageProcessing{

    // basically static methods for handling messages of different types
    // if message has invalid type then none of these will be called


    // HANDLERS START

    // CHAT
    public static void handleChat(PeerConnection peer, Message message){
        Logger.chat(peer.getAddress().getHostAddress(), message.getBody());
    }
    // end CHAT


    // PEER_DISCOVERY_REQUEST
    public static void handlePeerDiscoveryRequest(PeerConnection peer){
        // todo
        peer.sendMessage(Message.createPEER_DISCOVERY_REPLY(peer.getAddress().getHostAddress()));
    }
    // end PEER_DISCOVERY_REQUEST


    // PEER_DISCOVERY_REPLY
    public static void handlePeerDiscoveryReply(Message message){
        // todo
        //      - add only new addresses, so you don't have 2 or more sockets for the same host

        String[] newAddresses = message.getBody().split(";");
        for (int i = 0; i < newAddresses.length; i++) {
            try {
                new PeerConnection(newAddresses[i]);
            } catch (IOException e) {
                Logger.log("Error in trying to connect with " + newAddresses[i], LogLevel.ERROR);
            }
        }
    }
    // end PEER_DISCOVERY_REPLY


    // ONION
    public static void handleONION(Message message){

        Logger.log("Onion message received:\n" + message.toString(), LogLevel.DEBUG);

        PeerConnection nextPeer = PeerList.get(message.getNextAddress());
        if (nextPeer == null){
            Logger.log("nextAddress not present in peerlist [" + message.getNextAddress() + "]", LogLevel.DEBUG);
            return;
        }
        Message nextMessage;

        try{
            nextMessage = new Message(message.getBody());
            nextPeer.sendMessage(nextMessage);
        }catch (IOException e){
            Logger.log("Error in trying to create next message from an onion message", LogLevel.ERROR);
        }

    }
    // end ONION
    // end HANDLERS
}
