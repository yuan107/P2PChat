/**
 * ClientListener.java
 * <p>
 * This class runs on the client end and just
 * displays any text received from the server.
 */

import java.lang.Exception;
import java.lang.Integer;
import java.lang.System;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;

public class ClientListener implements Runnable {
    private Socket connectionSock = null;

    ClientListener(Socket sock) {
        this.connectionSock = sock;
    }

    public void run() {
        // Wait for data from the server.  If received, output it.
        try {
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
            while (true) {
                // Get data sent from the server
                String serverText = serverInput.readLine();
                if (serverInput != null) {
                    if(serverText.startsWith("JOIN")){
                        int nameStart = 4;
                        int nameEnd = serverText.lastIndexOf('|');
                        int ipStart = serverText.indexOf("=/", nameEnd) + 2;
                        int ipEnd = serverText.indexOf(',', ipStart);
                        int portStart = serverText.indexOf("port=", ipEnd) + 5;
                        int portEnd = serverText.indexOf(',', portStart);

                        System.out.println("Adding User " + serverText.substring(nameStart, nameEnd) + " on " + serverText.substring(ipStart, ipEnd) + ":" + Integer.valueOf(serverText.substring(portStart, portEnd)));
                        //I haven't implemented the listening part of this thing, but when I do I'll probably just add 1000 to the port number
                        try {
                            MTClient.addUser(new User(serverText.substring(nameStart, nameEnd), new Socket(serverText.substring(ipStart, ipEnd), 1234 + Integer.valueOf(serverText.substring(portStart, portEnd)))));
                        } catch (Exception ex){
                            //Probably going to happen
                        }
                    } else if(serverText.startsWith("LEAV")){

                    } else {
                        System.out.println(serverText);
                    }
                } else {
                    // Connection was lost
                    System.out.println("Closing connection for socket " + connectionSock);
                    connectionSock.close();
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());

            try {
                connectionSock.close();
            } catch (Exception ex) {

            }
        }
    }
} // ClientListener for MTClient
