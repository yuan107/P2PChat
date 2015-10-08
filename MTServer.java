/**
 * MTServer.java
 * <p>
 * This program implements a simple multithreaded chat server.  Every client that
 * connects to the server can broadcast data to all other clients.
 * The server stores a ConcurrentLinkedQueue of sockets to perform the broadcast.
 * <p>
 * The MTServer uses a ClientHandler whose code is in a separate file.
 * When a client connects, the MTServer starts a ClientHandler in a separate thread
 * to receive messages from the client.
 * <p>
 * To test, start the server first, then start multiple clients and type messages
 * in the client windows.
 */

import java.lang.Exception;
import java.lang.System;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MTServer {
    // Maintain list of all client sockets for broadcast
    private static ConcurrentLinkedQueue<User> userList;

    public MTServer() {
        userList = new ConcurrentLinkedQueue<User>();
    }

    private void getConnection() {
        // Wait for a connection from the client
        ServerSocket serverSock = null;
        System.out.println("Waiting for client connections on port 7654.");
        try {
            serverSock = new ServerSocket(7654);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            if(serverSock != null){
                try {
                    serverSock.close();
                } catch (IOException f){

                }
            }

            System.exit(-1);
        }
        // This is an infinite loop, the user will have to shut it down using control-c
        while (true) {

            try {
                Socket connectionSock = serverSock.accept();
                // Add this socket to the list
                //userList.add(connectionSock);

                // Send to ClientHandler the socket and ConcurrentLinkedQueue of all sockets
                ClientHandler handler = new ClientHandler(connectionSock);
                Thread theThread = new Thread(handler);
                theThread.start();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        // Will never get here, but if the above loop is given
        // an exit condition then we'll go ahead and close the socket
        //serverSock.close();
    }

    public static void addUser(User user){
        userList.add(user);
    }
    public static void removeUser(int user){
        userList.remove(user);
    }
    public static void removeUser(Socket socket){
        for(User user : userList){
            if(Arrays.equals(user.getAddress(), socket.getInetAddress().getAddress()) && user.getPort() == socket.getPort()){
                userList.remove(user);
            }
        }
    }

    public static ConcurrentLinkedQueue<User> getUserList(){
        return userList;
    }

    public static void main(String[] args) {
        MTServer server = new MTServer();
        server.getConnection();
    }
} // MTServer
