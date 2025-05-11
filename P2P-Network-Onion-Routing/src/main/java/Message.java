import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class Message {

    private String id;
    private String sourceAddress;
    private MessageType type;
    private String body;

    public Message(MessageType type, String body){
        id = UUID.randomUUID().toString();
        this.type = type;
        this.body = body;
        this.sourceAddress = Constants.getMY_IP();
    }

    public Message(String rawMessage) throws IOException {
        String[] tokens = rawMessage.split(" ", 4);
        if (tokens.length != 4){
            Logger.log("Invalid raw message", LogLevel.ERROR);
            throw new IOException();
        }
        id = tokens[0];
        sourceAddress = tokens[1];
        type = MessageType.valueOf(tokens[2]);
        body = tokens[3];

    }

    public static Message createPEER_DISCOVERY_REPLY(String excludeAddr){
        return new Message(MessageType.PEER_DISCOVERY_REPLY, PeerList.getAddressList(excludeAddr));
    }
    public static Message createPEER_DISCOVERY_REQUEST(){
        return new Message(MessageType.PEER_DISCOVERY_REQUEST, ".");
    }

    public String toString(){
        return id + " " + sourceAddress + " "+ type.name() + " " + body;
    }

    public String getId() {
        return id;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }
    public MessageType getType() {
        return type;
    }

    public String getBody() {
        return body;
    }
}
