import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class Message {

    // todo - connectionId
    //      - so you can use the same ONION path multiple times, with the established
    //          in-between nodes/peers

    // note: REQUEST ONION always has inside body a REQUEST message

    // variables
    private String id;
    private MessageMainType type;
    private MessageSubType subType;
    private String nextAddress;     // for ONION messages
    private String body;
    // end variables

    // Constructors

    // Default
    public Message(String id, MessageMainType type, MessageSubType subType, String nextAddress, String body) throws IOException {


        if (type == MessageMainType.REQUEST){
            this.id = UUID.randomUUID().toString();
        }else if (id == null){
            // REPLY message with no id set
            // need to know the id of the request you're responding to
            Logger.log("Need to set id for REPLY messages!", LogLevel.WARN);
            throw new IOException();
        }else{
            // REPLY message with id set
            this.id = id;
        }
        this.type = type;
        this.subType = subType;
        this.nextAddress = nextAddress;
        this.body = body;
    }

    // default constructor for creating REQUEST messages
    // NOT FOR ONION Messages
    public static Message createRequest(MessageSubType subType, String body){
        try {
            return new Message(null, MessageMainType.REQUEST, subType, null, body);
        } catch (IOException e) {
            Logger.log("Error at Message.createRequest()! Check code!", LogLevel.WARN);
            return null;
        }
    }

    // default constructor for creating REPLY messages
    public static Message createReply(String id, MessageSubType subType, String body) throws IOException {
        return new Message(id, MessageMainType.REPLY, subType, null, body);
    }

    // default constructor for creating ONION REQUEST messages
    public static Message createOnionRequest(String nextAddress, String body){
        try {
            return new Message(null, MessageMainType.REQUEST, MessageSubType.ONION, nextAddress, body);
        } catch (IOException e) {
            Logger.log("Error in createOnionRequest() Check code!", LogLevel.WARN);
            return null;
        }
    }

    public static Message createONION_REQUEST_PACKET(String final_dest, Message message){

        ArrayList<String> addresses = PeerList.getAddressArrayList(final_dest);
        Collections.shuffle(addresses);

        message = Message.createOnionRequest(final_dest, message.toString());
        for (int i = 0; i < 3; i++) {
            message = Message.createOnionRequest(addresses.get(i), message.toString());
        }
        return message;
    }

    // default constructor for creating ONION REPLY messages
    public static Message createOnionReply(String id, String body) throws IOException {
        return new Message(id, MessageMainType.REPLY, MessageSubType.ONION, null, body);
    }



    // for converting raw strings into message objects
    public Message(String rawMessage) throws IOException {
        String[] tokens = rawMessage.split(" ", 5);
        if (tokens.length != 5){
            Logger.log("Invalid raw message structure!", LogLevel.WARN);
            throw new IOException();
        }
        id = tokens[0];
        type = MessageMainType.valueOf(tokens[1]);
        subType = MessageSubType.valueOf(tokens[2]);
        nextAddress = tokens[3];
        body = tokens[4];
    }
    // end constructors

    // converting message object to raw string
    public String toString(){
        return id + " " + type.name() + " " + subType.name() + " " + nextAddress + " " + body;
    }


    // Getters

    public String getId() {
        return id;
    }

    public MessageMainType getType() {
        return type;
    }

    public MessageSubType getSubType() {
        return subType;
    }

    public String getNextAddress() {
        // in REQUEST ONION message
        // for where to send the internal message
        return nextAddress;
    }

    public String getBody() {
        return body;
    }

    // end getters
}
