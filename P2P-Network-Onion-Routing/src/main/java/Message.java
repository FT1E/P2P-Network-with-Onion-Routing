import java.util.UUID;

public class Message {

    private String id;
    MessageType type;
    private String body;

    public Message(MessageType type, String body){
        id = UUID.randomUUID().toString();
        this.type = type;
        this.body = body;
    }

    public Message(String rawMessage){
        String[] tokens = rawMessage.split(" ", 3);
        id = tokens[0];
        type = MessageType.valueOf(tokens[1]);
        body = tokens[2];
    }
    public String toString(){
        return id + " " + type.name() + " " + body;
    }

    public String getId() {
        return id;
    }

    public MessageType getType() {
        return type;
    }

    public String getBody() {
        return body;
    }
}
