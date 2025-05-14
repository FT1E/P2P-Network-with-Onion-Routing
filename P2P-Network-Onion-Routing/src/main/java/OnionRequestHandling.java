import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OnionRequestHandling {

    // todo - also process KEY_EXCHANGE REQUEST here


    private static Map<String, PeerConnection> onionRequests = new HashMap<>();

    private static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static synchronized void addRequest(String req_id, PeerConnection peer){
        onionRequests.put(req_id, peer);
    }

    public static synchronized void addReply(Message message){
        PeerConnection peer = onionRequests.remove(message.getId());
        if (peer == null) return;
        peer.sendMessage(message);
    }

}
