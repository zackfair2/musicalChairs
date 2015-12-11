package musicalChairs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author albin
 */
public class Server {
    static int players = 0;
    static Socket newClient;
    static List<ServerClientThread> PLAYER_LIST = Collections.synchronizedList(new ArrayList<ServerClientThread>());
    

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        ServerPlayerList playerList = new ServerPlayerList();
        playerList.start();
        int id = 0;

        while (players < 2) {
            newClient = waitForConnection(serverSocket);
            setUpConnectionWithClient(newClient, id++);
            for (int i = 0; i < PLAYER_LIST.size(); i++) {
             System.out.println(PLAYER_LIST.get(i).getClientID());
             }
            
            System.out.println("bla" + PLAYER_LIST.toString()); //Bara här för debugg!
            players = players +1;
              
        }
        sendStart();
        ServerGameProtocol.runGame();
    }
    //blah
    private static void sendStart() {
        for (ServerClientThread PLAYER_LIST1 : PLAYER_LIST) {
            try {
                ServerClientThread.STREAM_OUT_TO_CLIENT.writeObject("START");
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                ServerClientThread.STREAM_OUT_TO_CLIENT.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Sending this: 'start' to everyone");
        }
       }

    private static Socket waitForConnection(ServerSocket serverSocket) throws IOException {
        System.out.println("Waiting for client on port: " + serverSocket.getLocalPort()
                + " and IP: " + InetAddress.getLocalHost());
        return serverSocket.accept();
    }

    private static void setUpConnectionWithClient(Socket newclient, int id) throws IOException {
        ServerClientThread clientThread = new ServerClientThread(newclient, id);
        clientThread.start();
        PLAYER_LIST.add(clientThread);
        System.out.println("Accepted Client : ID - " + clientThread.CLIENT_ID + " : Address - "
                + clientThread.getClientIP() + " : Portnumber - " + clientThread.getClientPort());

    }

   

    /*
    Håller koll på vilka spelare som finns
    */
     private static class ServerPlayerList extends Thread {

        public void run() {
            while (true) {
                setPlayerList();
            }
        }

        private static void setPlayerList() {

            for (int i = 0; i < PLAYER_LIST.size(); i++) {

                if (!PLAYER_LIST.get(i).isAlive()) {
                    PLAYER_LIST.remove(i);
                }
            }
        }
    }


}
