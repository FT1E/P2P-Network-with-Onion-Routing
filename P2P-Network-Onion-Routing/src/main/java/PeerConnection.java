import Util.LogLevel;
import Util.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class PeerConnection implements Runnable{

    private Socket socket;
    BufferedWriter writer;
    BufferedReader reader;
    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os));
        reader = new BufferedReader(new InputStreamReader(is));


        PeerList.addConnection(this);
    }

    public PeerConnection(InetAddress inetAddress) throws IOException{
        this.socket = new Socket(inetAddress, Constants.getSERVER_PORT());
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os));
        reader = new BufferedReader(new InputStreamReader(is));

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

        PeerList.addConnection(this);
    }

    public InetAddress getAddress(){
        return socket.getInetAddress();
    }

    private String getRawMessage(){
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean sendMessage(Message message){
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


    @Override
    public void run() {

        String rawMessage;
        Message message;
        while(true){

            rawMessage = getRawMessage();

            if(rawMessage == null) continue;
            try{
                message = new Message(rawMessage);
            }catch (IOException e){
                Logger.log("Invalid raw message", LogLevel.ERROR);
                continue;
            }

            proccessMessage(message);
//            MessageProcessing.addMessage(message);
        }
    }


    private void proccessMessage(Message message){
        switch (message.getType()){
            case CHAT -> MessageProcessing.handleChat(message);
            case PEER_DISCOVERY_REPLY -> MessageProcessing.handlePeerDiscoveryReply(message);
            case PEER_DISCOVERY_REQUEST -> MessageProcessing.handlePeerDiscoveryRequest(this);
        }
    }


    public boolean disconnect(){
        try{
            writer.close();
            reader.close();
            socket.close();
            return true;
        }catch (IOException e){
            Logger.log("Error in trying to close socket with address " + socket.getInetAddress().getHostAddress(), LogLevel.ERROR);
            return false;
        }
    }
}
