import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class Message {


    // Message contents/structure
    // variables
    private String id;
//    private String sourceAddress;
    private MessageType type;
    private String nextAddress;
    private String body;

    // end variables


    // Constructors

    // Message constructor when sending a message
    // nextAddress == null
    public Message(MessageType type, String body){
        id = UUID.randomUUID().toString();
        this.type = type;
        this.body = body;
//        this.sourceAddress = Constants.getMY_IP();
    }

    // Constructor for onion messages
    // i.e. messages that need to be resent to host with nextAddress
    public Message(String nextAddress, Message message) throws IOException{
        if (nextAddress == null) throw new IOException();

        id = UUID.randomUUID().toString();
        this.type = MessageType.ONION;
        this.nextAddress = nextAddress;
        this.body = message.toString();
    }

    // static method for creating onion messages
    // i.e. messages so they have 3 hops in between the first sender (this)
    // and the final receiver (the final destination of address)
    // note: call MessageProcessing.handleONION() on the return value
    //
    public static Message createONION(String finalDest, Message originalMessage){
        Message message = null;
        try {
            message = new Message(finalDest, originalMessage);
        }catch (IOException e){
            Logger.log("onion message with nextAddress==null", LogLevel.ERROR);
        }
        ArrayList<String> addresses = PeerList.getAddressArrayList(finalDest);
        Collections.shuffle(addresses);
        int max = Math.min(3, addresses.size());
        for (int i = 0; i < max; i++) {
            try{
                message = new Message(addresses.get(i), message);
            }catch (IOException e){
                Logger.log("onion message with nextAddress==null", LogLevel.ERROR);
            }
        }

        return message;
    }

    // Message constructor when receiving a message
    // throws an error if message has invalid structure
    public Message(String rawMessage) throws IOException {
        String[] tokens = rawMessage.split(" ", 4);
        if (tokens.length != 4){
            Logger.log("Invalid raw message", LogLevel.ERROR);
            throw new IOException();
        }
        id = tokens[0];
        nextAddress = tokens[1];
        type = MessageType.valueOf(tokens[2]);
        body = tokens[3];

    }

    // static method for creating a PEER_DISCOVERY_REPLY message
    // contains all IPs except one (the one of that which requested)
    public static Message createPEER_DISCOVERY_REPLY(String excludeAddr){
        return new Message(MessageType.PEER_DISCOVERY_REPLY, PeerList.getAddressList(excludeAddr));
    }

    // static method for creating a PEER_DISCOVERY_REQUEST message
    public static Message createPEER_DISCOVERY_REQUEST(){
        return new Message(MessageType.PEER_DISCOVERY_REQUEST, ".");
    }
    // end constructors

    // toString method
    public String toString(){
        return id + " " + nextAddress + " "+ type.name() + " " + body;
    }

    // end toString method


    // Getters
    public String getId() {
        return id;
    }

//    public String getSourceAddress() {
//        return sourceAddress;
//    }
    public MessageType getType() {
        return type;
    }

    public String getBody() {
        return body;
    }

    public String getNextAddress(){
        return nextAddress;
    }
    // end getters
}
