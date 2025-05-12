import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.net.InetAddress;
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
    public Message(String nextAddress, Message message) throws IOException{
        if (nextAddress == null) throw new IOException();

        id = UUID.randomUUID().toString();
        this.type = MessageType.ONION;
        this.nextAddress = nextAddress;
        this.body = message.toString();
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
