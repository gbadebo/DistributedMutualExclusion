/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author GbadeboAyoade
 */
public class LogProcess extends Thread {

      
        private BufferedReader in;
        private PrintWriter out;
        Socket socket;
        int procid;
        private String message;

        public LogProcess(Socket theSocket, String themessage) {
            
            message = themessage;
            socket = theSocket;
            //  log("New connection with client# " + server + " at " + port);
        }

        /**
         * Services this thread's client by first sending the client a welcome
         * message then repeatedly reading strings and sending back the
         * capitalized version of the string.
         */
        public void connectToServer() throws IOException {

           if(socket == null){return;}


            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(message);


        }

        public void run() {
            try {

                connectToServer();
            } catch (IOException e) {
                // log("Error handling client# " + port + ": " + e);
            } finally {
                try {
                  //  socket.close();
                } catch (Exception e) {
                    //   log("Couldn't close a socket, what's going on?");
                }
                //  log("Connection with client# " + port + " closed");
            }
        }

        private void log(String message) {
            System.out.println(message);
        }
    }
