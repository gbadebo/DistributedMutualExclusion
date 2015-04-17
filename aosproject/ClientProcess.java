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
public class ClientProcess extends Thread {

    private String server;
    private int port;
    private BufferedReader in;
    private PrintWriter out;
    Socket socket;
    int procid;
    private LamportClock clock;
    private LamportClock theReqClock;

    public synchronized LamportClock getReqClock() {
        return theReqClock;
    }

    public synchronized void setReqClock(LamportClock reqClock) {
        theReqClock = reqClock;
    }

    public ClientProcess(String server, int port, LamportClock theclock, LamportClock thereq) {
        this.server = server;
        this.port = port;
        this.procid = theclock.processid;
        clock = theclock;
        theReqClock = thereq;

        //  log("New connection with client# " + server + " at " + port);
    }

    /**
     * Services this thread's client by first sending the client a welcome
     * message then repeatedly reading strings and sending back the capitalized
     * version of the string.
     */
    public void connectToServer() throws IOException {

        boolean scanning = true;

        Socket logsocket = null;
        if (logsocket == null) {
            while (scanning) {
                try {
                    logsocket = new Socket(ProcessServer.loggingServer, ProcessServer.loggingPort);
                    logsocket.setSoTimeout(30000);
                    logsocket.setKeepAlive(true);
                    scanning = false;
                } catch (IOException e) {
                    System.out.println("Connect failed, waiting and trying again " + port);
                    try {
                        Thread.sleep(10);//2 seconds
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }




        scanning = true;
        if (socket == null) {
            while (scanning) {
                try {
                    socket = new Socket(server, port);
                    scanning = false;
                } catch (IOException e) {
                    System.out.println("Connect failed, waiting and trying again " + port);
                    try {
                        Thread.sleep(10);//2 seconds
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }


        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);


        MessageEngine engine = new MessageEngine(MessageType.RQ, theReqClock);
        String mymessage = engine.messageEngineToString();
        int procid = clock.processid;
        int theport = port;
        new LogProcess(logsocket, procid + "RAC log sent time " + mymessage + " to " + theport).run();

        System.out.println("client sent: " + mymessage + " " + port + "\n");
        out.println(mymessage);
        String input = in.readLine();
        new LogProcess(logsocket, procid + "RAC log recv time " + input).run();
        System.out.println("client recv: " + input + "\n");
        MessageEngine recvMessage = new MessageEngine(null, null).stringToMessageEngine(input);


        clock.incrementClock();
        clock.merge(recvMessage.clock);
        ProcessServer.myReq.remove(ProcessServer.myserverports[recvMessage.clock.processid]);// After getting a reply remove from queue so that you wont send request to it

        try {
            logsocket.close();
        } catch (IOException e) {
        } finally {
            try {
                logsocket.close();
            } catch (IOException e) {
                //   log("Couldn't close a socket, what's going on?");
            }
            //  log("Connection with client# " + port + " closed");
        }

    }

    public void run() {
        try {

            connectToServer();
        } catch (IOException e) {
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //   log("Couldn't close a socket, what's going on?");
            }
            //  log("Connection with client# " + port + " closed");
        }
    }

    private void log(String message) {
        System.out.println(message);
    }
}
