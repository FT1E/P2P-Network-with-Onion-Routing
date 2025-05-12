import Util.LogLevel;
import Util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    // Server - getting new connections

    // note: the port the server uses is the same in all nodes
    // set in Constants class, retrieved with Constants.getSERVER_PORT()


    // variables
    ServerSocket serverSocket;
    private final int port;
    // end variables


    // Constructor
    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.port = port;
    }
    // end constructor

    // Getters
    public int getPort(){
        return port;
    }
    // end getters


    // Runnable
    // for receiving new connections/sockets
    @Override
    public void run() {

        Logger.log("Server started ... ", LogLevel.SUCCESS);

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
    // end runnable
}
