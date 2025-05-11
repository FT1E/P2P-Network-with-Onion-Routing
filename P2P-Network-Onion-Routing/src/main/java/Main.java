
// todo:
//      - static createConnection/createPeer method in PeerList for new connections
//

import Util.LogLevel;
import Util.Logger;

import java.awt.desktop.ScreenSleepEvent;
import java.io.IOException;

public class Main {
    public static void main(String[] args){

        // todo
        //      1) start server
        //          - wait a sec or two, so everyone can start (ONLY IN TESTING)
        //      2) connect to someone, so you're in a network of at least 2
        //      3) interface for sending messages


        // 1) Start server
        try{
            new Thread(new Server(Constants.getSERVER_PORT())).start();
        }catch (IOException e){
            Logger.log("Error in starting server! Exiting app ...", LogLevel.ERROR);
            return;
        }
        Logger.log("Server started ... ", LogLevel.SUCCESS);


        // DEBUG / TESTING
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Logger.log("Error in main at currentThread.wait()", LogLevel.ERROR);
        }

        // todo 2) Connect to someone

        try {
            new PeerConnection(System.getenv("BOOTSTRAP_ADDRESS"));
            Logger.log("Connected to peer", LogLevel.SUCCESS);
        } catch (IOException e) {
            Logger.log("Error in trying to connect to BOOTSTRAP_ADDRESS=" + System.getenv("BOOTSTRAP_ADDRESS"), LogLevel.ERROR);
        }

        // todo 3) Interface for sending messages
        //      - maybe a gui base on command line args
        //      - for now just text interface

        PeerConnection peer;
        do{
            Logger.log("Trying to get peer", LogLevel.DEBUG);
            peer = PeerList.get(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Logger.log("Error in main at currentThread.wait()", LogLevel.ERROR);
            }
        }while (peer == null);

        peer.sendMessage(new Message(MessageType.CHAT, "Message from " + System.getenv("PEER_ID")));
    }
}
