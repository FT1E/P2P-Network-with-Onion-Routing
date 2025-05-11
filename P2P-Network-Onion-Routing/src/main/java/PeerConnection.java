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
        InetAddress inetAddress = InetAddress.getByName(stringAddress);
        this.socket = new Socket(inetAddress, Constants.getSERVER_PORT());
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os));
        reader = new BufferedReader(new InputStreamReader(is));


        PeerList.addConnection(this);
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
            return false;
        }
    }


    @Override
    public void run() {

        String rawMessage;
        while(true){

            rawMessage = getRawMessage();

            if(rawMessage == null) continue;

            proccessMessage(new Message(rawMessage));
        }


    }

    private void proccessMessage(Message message){
        switch (message.getType()){
            case CHAT -> Logger.chat(socket.getInetAddress().toString(), message.getBody());
            default -> Logger.log("Invalid message type", LogLevel.INFO);
        }
    }

    public boolean disconnect(){
        try{
            writer.close();
            reader.close();
            socket.close();
            return true;
        }catch (IOException e){
            Logger.log("Error in trying to disconnect socket", LogLevel.ERROR);
            return false;
        }
    }
}
