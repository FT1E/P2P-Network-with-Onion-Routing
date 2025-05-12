
// todo:
//      - static createConnection/createPeer method in PeerList for new connections
//

import Util.LogLevel;
import Util.Logger;

import java.awt.desktop.ScreenSleepEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args){

        // todo
        //      1) start server
        //          - wait a sec or two, so everyone can start (ONLY IN TESTING)
        //      2) connect to someone, so you're in a network of at least 2
        //      3) connect to everyone, might take n2 time,
        //      basically 2) + 3) == set up network, i.e. connect all nodes with each other
        //      4) interface for sending messages

        try{
            Constants.setMY_IP(InetAddress.getLocalHost().getHostAddress());
        }catch (UnknownHostException e){
            Constants.setMY_IP(System.getenv("PEER_ID"));
        }

        // 1) Start server
        try{
            new Thread(new Server(Constants.getSERVER_PORT())).start();
        }catch (IOException e){
            Logger.log("Error in starting server! Exiting app ...", LogLevel.ERROR);
            return;
        }

        // DEBUG / TESTING
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Logger.log("Error in main at currentThread.wait()", LogLevel.ERROR);
        }





        // todo 2) Connect to someone

//        try {
//            new PeerConnection(System.getenv("BOOTSTRAP_ADDRESS"));
////            Logger.log("Connected to peer", LogLevel.SUCCESS);
//        } catch (IOException e) {
//            Logger.log("Error in trying to connect to BOOTSTRAP_ADDRESS=" + System.getenv("BOOTSTRAP_ADDRESS"), LogLevel.ERROR);
//        }


        // todo 3) Connect to everyone in the network
        //      for now passing it as csv list in env variable
        String[] hosts = System.getenv("PEERS").split(",");
        for (int i = 0; i < hosts.length; i++) {
            try{
                new PeerConnection(hosts[i]);
            }catch (IOException e){
                Logger.log("Error in trying to connect with " + hosts[i], LogLevel.ERROR);
            }
        }


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Logger.log("Error in main at currentThread.wait()", LogLevel.ERROR);
        }

        // todo 4) Interface for sending messages
        //      - maybe a gui base on command line args
        //      - for now just text interface

        if (System.getenv("PEER_ID").equals("peer1")){
            String address_dest = PeerList.get(0).getAddress().getHostAddress();
            String address_mid = PeerList.get(1).getAddress().getHostAddress();
            Logger.log("Destination address:" + address_dest, LogLevel.INFO);
            Logger.log("Middle address:" + address_mid, LogLevel.INFO);

            Message message;
            try {
                message = new Message(address_dest, new Message(MessageType.CHAT, "Hello from peer1"));
                PeerList.get(address_mid).sendMessage(message);
            }catch (IOException e){
                Logger.log("Error in creating onion message, nextAddress == null", LogLevel.ERROR);
            }
        }
    }
}
