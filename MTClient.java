/**
 * MTClient.java
 * <p>
 * This program implements a simple multithreaded chat client.  It connects to the
 * server (assumed to be localhost on port 7654) and starts two threads:
 * one for listening for data sent from the server, and another that waits
 * for the user to type something in that will be sent to the server.
 * Anything sent to the server is broadcast to all clients.
 * <p>
 * The MTClient uses a ClientListener whose code is in a separate file.
 * The ClientListener runs in a separate thread, recieves messages form the server,
 * and displays them on the screen.
 * <p>
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.
 */

import java.io.*;
import java.io.BufferedReader;
import java.io.Console;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.*;
import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.StringBuilder;
import java.lang.System;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MTClient {
    private static InputStreamReader reader;
    private static ArrayList<User> userList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("MTClient Started...\n");
        DataOutputStream serverOutput = null;
        String randomNumber = Integer.toString(new Random().nextInt(999));
        String username = "User" + "000".substring(randomNumber.length()) + randomNumber;
        Scanner keyboard = null;

        String hostname = "localhost";
        int port = 7654;
        try {
            System.out.println("Connecting to server on port " + port);
            Socket connectionSock = new Socket(hostname, port);

            serverOutput = new DataOutputStream(connectionSock.getOutputStream());


            System.out.println("Connection made.");
            System.out.print("Choose A Username: ");

            keyboard = new Scanner(System.in);

            // Start a thread to listen and display data sent by the server
            ClientListener listener = new ClientListener(connectionSock);
            Thread theThread = new Thread(listener);
            theThread.start();

            //This first one is his username
            username = keyboard.nextLine();
            serverOutput.writeBytes("JOIN" + username + "\n");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //Connected and conversing with others
        try {
            while (true) {
                String data = keyboard.nextLine();
                if (data.startsWith("\\Users")){
                    System.out.println("List of all connected users: ");
                    for(User u : userList){
                        String socket = u.getSocket().toString();
                        int ipStart = socket.indexOf("=/") + 2;
                        int ipEnd = socket.indexOf(',', ipStart);
                        int portStart = ipEnd + 6;
                        int portEnd = socket.indexOf(',',portStart);
                        System.out.println("[" + u.getName() + "] connected at " + socket.substring(ipStart, ipEnd) + socket.substring(portStart, portEnd) + "\n");
                    }
                } else {
                    serverOutput.writeBytes("[" + username + "]: " + data + "\n");
                }
            }
        } catch (Exception e) {
            try {
                serverOutput.writeBytes("LEAV\n");
            } catch (Exception ex){
                e.printStackTrace();
                ex.printStackTrace();
            }
        }
    }

    public static void addUser(User user){
        userList.add(user);
        System.out.println("Connected to User " + user.getName() + " at " + user.getAddress() + ":" + user.getPort());
        System.out.println("Type \"\\Users\" at any time to show the full user list");
    }

    public static void removeUser(int index){
        userList.remove(index);
    }
} // MTClient

