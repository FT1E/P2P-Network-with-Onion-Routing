import Util.LogLevel;
import Util.Logger;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerList {

    private static ArrayList<PeerConnection> peerList = new ArrayList<>();

    private static ExecutorService thread_pool = Executors.newCachedThreadPool();

    public static void addConnection(PeerConnection peer){
        peerList.add(peer);
        thread_pool.submit(peer);
//        Logger.log("New peer added to peerList", LogLevel.SUCCESS);
    }

    public static void removeConnection(PeerConnection peer){
        peerList.remove(peer);
        peer.disconnect();
    }

    public static PeerConnection get(int i){
        try{
            return peerList.get(i);
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }
}
