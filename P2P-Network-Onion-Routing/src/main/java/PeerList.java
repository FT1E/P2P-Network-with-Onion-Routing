import Util.LogLevel;
import Util.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerList {

    // static class for keeping all the peers
    // list of all peers and a thread_pool for their runnables (receiving and processing messages)


    // variables
    private static ArrayList<PeerConnection> peerList = new ArrayList<>();

    private static ExecutorService thread_pool = Executors.newCachedThreadPool();
    // end variables


    // Add connection
    public synchronized static void addConnection(PeerConnection peer){

        for (int i = 0; i < peerList.size(); i++) {
            if (peerList.get(i).getAddress().getHostAddress().equals(peer.getAddress().getHostAddress())) {
                peer.disconnect();
                return;
            }
        }

        peerList.add(peer);
        thread_pool.submit(peer);

//        Logger.log("New peer added, has address == " + peer.getAddress().getHostAddress() + " number of peers == " + get_PEER_NUM(), LogLevel.INFO);

//        peer.sendMessage(Message.createPEER_DISCOVERY_REQUEST());
//        Logger.log("New peer added to peerList", LogLevel.SUCCESS);
    }
    // end add connection

    // Remove connection
    public static void removeConnection(PeerConnection peer){
        peerList.remove(peer);
        peer.disconnect();
    }
    // end remove connection


    // get PeerConnection
    // based on index in list
    // for sending messages
    public static PeerConnection get(int i){
        try{
            return peerList.get(i);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    // get PeerConnection
    // based on address
    public static PeerConnection get(String address){
        for (int i = 0; i < peerList.size(); i++) {
            if (peerList.get(i).getAddress().getHostAddress().equals(address)) return peerList.get(i);
        }
        return null;
    }

    // end get PeerConnection


    // Get number of connections
    public static int get_PEER_NUM(){
        return peerList.size();
    }
    // end get number of connections



    // Get addresses
    // get a list of all addresses except excludeAddr
    // used for generating PEER_DISCOVERY_REPLY message
    // returns array
    public static String[] getAddresses(String excludeAddr){
        ArrayList<String> addresses = new ArrayList<>(peerList.size() - 1);
        for (int i=0; i<peerList.size(); i++){
            addresses.add(peerList.get(i).getAddress().getHostAddress());
        }
        return addresses.toArray(new String[0]);
    }

    // returns one string joined by ";"
    public static String getAddressList(String excludeAddr){
        String[] addresses = getAddresses(excludeAddr);
        return String.join(";", addresses);
    }
    // end getAddresses
}
