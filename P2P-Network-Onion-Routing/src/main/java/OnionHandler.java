import Util.LogLevel;
import Util.Logger;

import java.io.IOException;

public class OnionHandler implements Runnable{

    private final PeerConnection before_peer;
    private final PeerConnection next_peer;
    private final String packet_id;
    // the id of the REQUEST ONION message which you need to give a reply to

    private final String message_id;
    // the id of the message inside the ONION packet
    // which is a REQUEST message
    // the peer needs to wait for reply of this message
    // wrap it in REPLY ONION message
    // and send it back to the one who sent REQUEST ONION

    private Message replyMessage;

    Object ALARM = new Object();
    public OnionHandler(String packet_id, String message_id, PeerConnection before_peer, PeerConnection next_peer){
        this.before_peer = before_peer;
        this.packet_id = packet_id;
        this.message_id = message_id;
        this.next_peer = next_peer;
    }
    @Override
    public void run() {
        synchronized (ALARM){

            // todo - wait for the REPLY message to be received
            try {
                ALARM.wait();
            } catch (InterruptedException e) {
                Logger.log("Error in OnionHandler at ALARM.wait()", LogLevel.WARN);
                return;
            }
            // todo - once REPLY is received

            // if you don't need to send it back
            // just process it
            if (before_peer == null){
                MessageProcessing.handleONION_REPLY(replyMessage, 3, next_peer);
                return;
            }


            // if you ned to send a reply
            Message packet_message_reply = null;
            try {
                packet_message_reply = Message.createReply(packet_id, MessageSubType.ONION, replyMessage.toString());
            } catch (IOException e) {
                Logger.log("Error while creating a REPLY ONION for packet_id:" + packet_id, LogLevel.WARN);
                return;
            }

            before_peer.sendMessage(packet_message_reply);



        }
    }

    // reply message has been received
    // wrap it and send it back
    public void awake(Message replyMessage){
        synchronized (ALARM) {
            // set the received REPLY message
            this.replyMessage = replyMessage;
            // awake the thread
            ALARM.notifyAll();
        }
    }
}
