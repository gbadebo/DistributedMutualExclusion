/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author GbadeboAyoade
 */
public class ServerLogger extends Thread {

    private int procid;
    ServerSocket serverlog;
    private BufferedReader in;
    private PrintWriter out;

    public ServerLogger() {
        // log("New connection with client# " + procid + " at " + socket);
        init();
    }

    public void init() {
        try {
            serverlog = new ServerSocket(ProcessServer.loggingPort);
        } catch (IOException ex) {
        }
    }

    public void run() {

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverlog.accept();
                processClientRequest(clientSocket);
             } catch (IOException io) {
            }

        }
    }

    public static synchronized void logToFile(String message) {

        new LogPrinter("logfile.txt", message).run();
        return;
    }

    private void processClientRequest(Socket clientSocket) {
        try {
            in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            String input = in.readLine();       
          
            
            logToFile(input);
            input = in.readLine();
            
            logToFile(input);
            
        } catch (Exception ex) {
        } finally {
            try {
              //  clientSocket.close();
            } catch (Exception e) {
                //   log("Couldn't close a socket, what's going on?");
            }
            //   log("Connection with client# " + " closed");
        }


    }
}