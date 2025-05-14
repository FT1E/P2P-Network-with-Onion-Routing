import Util.LogLevel;
import Util.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PeerConnection implements Runnable{


    // variables
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;

    private final Object writeLock;
    private final Object readLock;
    // end variables


    // CONSTRUCTORS

    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os));
        reader = new BufferedReader(new InputStreamReader(is));


        writeLock = new Object();
        readLock = new Object();

        PeerList.addConnection(this);
    }

    public PeerConnection(InetAddress inetAddress) throws IOException{
        this.socket = new Socket(inetAddress, Constants.getSERVER_PORT());
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os));
        reader = new BufferedReader(new InputStreamReader(is));

        writeLock = new Object();
        readLock = new Object();

        PeerList.addConnection(this);
    }
    public PeerConnection(String stringAddress) throws IOException{

        if(stringAddress.equals(Constants.getMY_IP())){
            Logger.log("Trying to connect to myself", LogLevel.DEBUG);
            throw new IOException();
        }

        InetAddress inetAddress = InetAddress.getByName(stringAddress);
        this.socket = new Socket(inetAddress, Constants.getSERVER_PORT());
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os));
        reader = new BufferedReader(new InputStreamReader(is));


        writeLock = new Object();
        readLock = new Object();

        PeerList.addConnection(this);
    }
    // end CONSTRUCTORS


    // GETTERS
    public InetAddress getAddress(){
        return socket.getInetAddress();
    }
    // end GETTERS


    // Core methods for sending and receiving message
    private String getRawMessage() {
        synchronized (readLock) {
            try {
                return reader.readLine();
            } catch (IOException e) {
                return null;
            }
        }
    }

    public boolean sendMessage(Message message){
        synchronized (writeLock){
            try {
//            Logger.log("Sending:" + message.toString(), LogLevel.DEBUG);
                writer.write(message.toString() + "\n");
                writer.flush();
                return true;
            } catch (IOException e) {
                Logger.log("Error in sending message to peer!", LogLevel.ERROR);
                Logger.log("(writer == null) " + (writer == null), LogLevel.DEBUG);
                Logger.log("Message:" + message.toString(), LogLevel.DEBUG);
                return false;
            }
        }
    }


    // end Core methods



    // Runnable - get message, process it with the appropriate method, repeat
    @Override
    public void run() {

        String rawMessage;
        Message message;
        while(true){

            rawMessage = getRawMessage();

            if(rawMessage == null) {
                Logger.log("rawMessage == null, breaking connection with " + getAddress().getHostAddress(), LogLevel.DEBUG);
                break;
            }
            try{
                message = new Message(rawMessage);
            }catch (IOException e){
                Logger.log("Invalid raw message", LogLevel.ERROR);
                continue;
            }

            processMessage(message);
//            MessageProcessing.addMessage(message);
        }

        Logger.log("Disconnecting with " + getAddress() + ", status:" + disconnect(), LogLevel.STATUS);
    }

    private void processMessage(Message message){
        if (message.getType() == MessageMainType.REQUEST){
            switch (message.getSubType()){
                case CHAT -> RequestProcessing.handleCHAT(message, this);
                case PEER_DISCOVERY -> RequestProcessing.handlePEER_DISCOVERY(message, this);
                // todo
                default -> Logger.log("Implement ONION and KEY_EXCHANGE REQUEST handling");
            }
        }else{
            switch (message.getSubType()){
                case CHAT -> ReplyProcessing.handleCHAT(message, this);
                case PEER_DISCOVERY -> ReplyProcessing.handlePEER_DISCOVERY(message);
                // todo
                default -> Logger.log("To be implemented");

            }
        }
    }
    // end Runnable + extra method

    // Disconnect
    // Closing i/o streams and socket connection
    // returns true/false, so I know whether the connection closed without errors
    public boolean disconnect(){
//        Logger.log("Trying to close connection with peer [" + getAddress().getHostAddress() + "]", LogLevel.INFO);
        try{
            PeerList.removeConnection(this);
            writer.close();
            reader.close();
            socket.close();
//            Logger.log("Successfully closed connection with peer [" + getAddress().getHostAddress() + "]", LogLevel.SUCCESS);
            return true;
        }catch (IOException e){
//            Logger.log("Error in trying to close socket with address " + socket.getInetAddress().getHostAddress(), LogLevel.ERROR);
            return false;
        }
    }
    // end disconnect
}
