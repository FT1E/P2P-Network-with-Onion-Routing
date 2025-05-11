import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    ServerSocket serverSocket;
    private final int port;
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.port = port;
    }

    public int getPort(){
        return port;
    }

    @Override
    public void run() {

        Socket socket;
        while (true){

            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Logger.log("Error in accepting new connection!", LogLevel.ERROR);
                continue;
            }

            if (socket == null) continue;

            try{
                new PeerConnection(socket);
            }catch (IOException e){
                Logger.log("Error in creating PeerConnection object from socket with address [" + socket.getInetAddress() + "]", LogLevel.ERROR);
                continue;
            }
        }
    }
}
