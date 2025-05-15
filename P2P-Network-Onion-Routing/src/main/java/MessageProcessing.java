import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;

public class MessageProcessing {

    private static Map<String, OnionHandler> onionRequests = new HashMap<>();
    // key = request id
    // value = PeerConnection of the peer requestor
    private static ExecutorService thread_pool = Executors.newCachedThreadPool();


    public static void handle(Message message, PeerConnection peer){
        if (message.getType() == MessageMainType.REQUEST){
            handleRequest(message, peer);
        }else{
            handleReply(message, peer);
        }
    }

    public static void handleRequest(Message message, PeerConnection peer){
        // todo
        switch (message.getSubType()){
            case CHAT -> handleCHAT_REQUEST(message, peer);
            case PEER_DISCOVERY -> handlePEER_DISCOVERY_REQUEST(message, peer);
            case ONION -> handleONION_REQUEST(message, peer, 0);
            // todo
            case KEY_EXCHANGE -> Logger.log("Todo");
        }
    }

    public static void handleReply(Message message, PeerConnection peer){
        // todo

        Logger.log("RECEIVED REPLY:" + message.toString(), LogLevel.DEBUG);



        OnionHandler onionHandler = onionRequests.remove(message.getId());
        if (onionHandler == null) {
            // handle reply normally
            switch (message.getSubType()){
                case CHAT -> handleCHAT_REPLY(message, peer);
                case PEER_DISCOVERY -> handlePEER_DISCOVERY_REPLY(message);
                // todo
                case ONION -> handleONION_REPLY(message, 3, peer);
                case KEY_EXCHANGE -> Logger.log("Todo");
            }
            // todo add handle REPLY ONION
            //  i.e. unwrap it and process the original message normally
            return;
        }


        Logger.log("This REPLY needs to awake an OnionHandler", LogLevel.DEBUG);
        // if this message needs to be sent back to a REQUEST ONION
        // send it back
        onionHandler.awake(message);
    }


    public static void handleCHAT_REQUEST(Message message, PeerConnection peer){
        Logger.chat(peer.getAddress().getHostAddress(), message.getBody());
        try {
            peer.sendMessage(Message.createReply(message.getId(), MessageSubType.CHAT, "Hello, chat message was received!"));
        } catch (IOException e) {
            Logger.log("Error at Request.handleCHAT()", LogLevel.WARN);
        }
    }

    public static void handlePEER_DISCOVERY_REQUEST(Message message, PeerConnection peer){
        try {
            peer.sendMessage(Message.createReply(message.getId(), MessageSubType.PEER_DISCOVERY, PeerList.getAddressList(peer.getAddress().getHostAddress())));
        } catch (IOException e) {
            Logger.log("Error at Request.handlePEER_DISCOVERY()", LogLevel.WARN);
        }
    }

    public static void handleONION_REQUEST(Message packetMessage, PeerConnection peer, int wrap_count) {
        // TODO
        //      1) unwrap the message
        //      if it's an ONION internal message:
        //          2) send it and wait for REPLY ONION
        //          3) once you receive the REPLY:
        //          check if you need to wrap it in REPLY ONION and send it back
        //          OR
        //          process it
        //      if it's not an ONION message:
        //          4) send the REQUEST inside and wait for its REPLY
        //          5) once you receive the REPLY:
        //          wrap it in REPLY ONION and send it back


        // 1) unwrap the message
        Message internalMessage = null;
        try {
            internalMessage = new Message(packetMessage.getBody());
        } catch (IOException e) {
            Logger.log("Error in unwrapping an ONION message", LogLevel.WARN);
            return;
        }


        // note: inside of REQUEST ONION is always REQUEST [something]

        // get next peer
        PeerConnection nextPeer = PeerList.get(packetMessage.getNextAddress());
        if (nextPeer == null){
            Logger.log("next peer for REQUEST ONION not present:" + packetMessage.getNextAddress(), LogLevel.WARN);
            return;
        }

        // todo wait for its REPLY (its id == same id as INTERNAL MESSAGE)
        //  before sending it
        OnionHandler onionHandler = new OnionHandler(packetMessage.getId(), internalMessage.getId(), peer, nextPeer, wrap_count);
        onionRequests.put(internalMessage.getId(), onionHandler);
        thread_pool.submit(onionHandler);

        // todo send it to the next
        nextPeer.sendMessage(internalMessage);





    }

    // handlers for REPLY

    // CHAT
    public static void handleCHAT_REPLY(Message message, PeerConnection peer){
        Logger.chat(peer.getAddress().getHostAddress(), message.getBody());
    }


    // PEER_DISCOVERY
    public static void handlePEER_DISCOVERY_REPLY(Message message){
        String[] addresses = message.getBody().split(";");
        for (int i = 0; i < addresses.length; i++) {
            try {
                new PeerConnection(addresses[i]);
            } catch (IOException e) {
                Logger.log("Error in trying to connect with " + addresses[i], LogLevel.ERROR);
            }
        }
    }

    // ONION
    public static void handleONION_REPLY(Message message, int wrap_count, PeerConnection peer){
        // todo
        // note: this REPLY doesn't need to be sent back to someone else
        // if it needed to be, it would've been done in handleReply()

        // todo
        //      1) unwrap
        //      2) handle the non-wrapped/original message

        for (int i = 0; i < wrap_count; i++) {
            try {
                message = new Message(message.getBody());
            } catch (IOException e) {
                Logger.log("Error while unwrapping message", LogLevel.WARN);
            }
            Logger.log("After unwrapping ONION message " + i + " times:" + message.toString());
        }
        handle(message, peer);

    }


    // KEY_EXCHANGE
}
