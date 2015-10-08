/**
 * ClientHandler.java
 * <p>
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 */

import java.lang.Exception;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientHandler implements Runnable {
    private Socket connectionSock = null;

    ClientHandler(Socket sock) {
        this.connectionSock = sock;
    }

    public void run() {
        // Get data from a client and send it to everyone else
        try {
            System.out.println("Connection made with socket " + connectionSock);

            // Get data sent from a client
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
            String clientText = clientInput.readLine();
            if (clientText != null && !clientText.startsWith("LEAV")) {
                System.out.println("Received: " + clientText);
                MTServer.addUser(new User(clientText.substring(4), connectionSock));
                //broadcast(clientText + "|" + connectionSock.toString() + "\n");
                //Send the userlist over
                ConcurrentLinkedQueue<User> userList = MTServer.getUserList();
                for (User user : userList) {
                    if (user.getSocket() != connectionSock) {
                        DataOutputStream clientOutput = new DataOutputStream(connectionSock.getOutputStream());
                        clientOutput.writeBytes("JOIN" + user.getName() + "|" + user.getSocket() + "\n");
                        System.out.println("SENDING: " + "JOIN" + user.getName() + "|" + user.getSocket() + "\n");
                    }
                }
            } else {
                throw new Exception("Connection Lost");
            }

            while (clientText != null && !clientText.startsWith("LEAV")) {
                clientText = clientInput.readLine();
                broadcast(clientText);
            }

            // Connection was lost
            System.out.println("Closing connection for socket " + connectionSock);
            broadcast("LEAV" + connectionSock.toString());
            connectionSock.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }

        MTServer.removeUser(connectionSock);
    }

    public void broadcast(String message) throws IOException{//For use in future versions
        ConcurrentLinkedQueue<User> userList = MTServer.getUserList();
        for (User user : userList) {
            if (user.getSocket() != connectionSock) {
                DataOutputStream clientOutput = new DataOutputStream(user.getOutputStream());
                clientOutput.writeBytes(message + "\n");
                //System.out.println(message + "\n");
            }
        }
    }
} // ClientHandler for MTServer.java
