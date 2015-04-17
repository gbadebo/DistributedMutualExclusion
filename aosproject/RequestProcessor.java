/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author GbadeboAyoade
 */

// 0 means not critical, 1 means trying to enter, 2 means inside
public class RequestProcessor extends Thread {

    private Socket socket;
    private int procid;
    private LamportClock clock;
    ArrayList<LamportClock> myclocks;

    public RequestProcessor(Socket socket, LamportClock theclock) {
        this.socket = socket;
        this.procid = theclock.processid;
        clock = theclock;
        myclocks = new ArrayList<LamportClock>();


      //   log("New connection with client# " + procid + " at " + socket);
    }

    //This processes all request to the server
    public void run() {
        try {

            // Decorate the streams so we can send characters
            // and not just bytes.  Ensure output is flushed
            // after every newline.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String input = in.readLine();
                if (input == null || input.equals(".")) {
                    break;
                }
               
                System.out.println(input+"\n");
                synchronized (ProcessServer.getbuffer()) {
                    System.out.println(" status "+ ProcessServer.getbuffer().getval());
        
                    System.out.println("1\n");
                    
                    MessageEngine recvMessage = new MessageEngine(null, null).stringToMessageEngine(input);
                    clock.incrementClock();
                    clock.merge(recvMessage.clock);



                    if (ProcessServer.getbuffer().getval()== 0) {
                        System.out.println(input+"2\n");
                        clock.incrementClock();//reply event
                        String themessage = new MessageEngine(MessageType.RP, clock).messageEngineToString();

                        System.out.println("nc"+themessage+"\n");
                        out.println(themessage);
                       
                        
                    } else if (ProcessServer.getbuffer().getval() == 1) { // trying to enter critcal section
                        System.out.println(ProcessServer.getSetClone().getClockValue() +" "+ recvMessage.getClock().getClockValue());
                        myclocks.add(ProcessServer.getSetClone());
                        myclocks.add(recvMessage.getClock());

                        Collections.sort(myclocks, new VectorClockComparator());

                        LamportClock cl = myclocks.get(0);
                         System.out.println("picked"+ cl.getClockValue()+ "id:"+cl.processid);
                       

                        if (cl.processid == procid) {
                            System.out.println("3\n");
                          
                            //if the serve clock is smaller than incoming request dont send a reply

                            int count = 0;



                            if (ProcessServer.getbuffer().getval() == 1 ) {
                                System.out.println("blocking1\n");
                                ProcessServer.getbuffer().wait();
                                System.out.println("blocking2\n");

                            }
                            if (ProcessServer.getbuffer().getval() == 2 ) {// in critical wait till finish
                                System.out.println("blocking3\n");
                                ProcessServer.getbuffer().wait();
                                System.out.println("blocking4\n");

                            }


                            System.out.println("blocking\n");//if wait block till critical section over
                            count++;

                            clock.incrementClock();//reply event
                            String replymessage = new MessageEngine(MessageType.RP, clock).messageEngineToString();
                            
                            out.println(replymessage);
                            ProcessServer.myReq.put(ProcessServer.myserverports[recvMessage.clock.processid],ProcessServer.myservernames[recvMessage.clock.processid]);
                            // sine you replied add to request queue so that you can send it request when you need to enter critical section


                        } else {//send a reply
                            
                             clock.incrementClock();//reply event
                            String replymessage = new MessageEngine(MessageType.RP, clock).messageEngineToString();
                            System.out.println("nyet2"+replymessage+"\n");
                            out.println(replymessage);
                            
                            
                        }
                        
                        myclocks.clear();

                    } else if (ProcessServer.getbuffer().getval() == 2) {
                        System.out.println("critical");
                                
                        if(ProcessServer.getbuffer().getval() == 2) {
                           ProcessServer.getbuffer().wait();

                        }

                        clock.incrementClock();//reply event
                        String replymessage = new MessageEngine(MessageType.RP, clock).messageEngineToString();
                         System.out.println("c"+replymessage+"\n");
                        out.println(replymessage);
                        ProcessServer.myReq.put(ProcessServer.myserverports[recvMessage.clock.processid],ProcessServer.myservernames[recvMessage.clock.processid]);
                          

                    }
                }
               

                //
            }
        } catch (Exception e) {
           //  log("Error handling client# " + ": " + e);
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                //   log("Couldn't close a socket, what's going on?");
            }
              // log("Connection with client# " + " closed");
        }
    }

    /**
     * Logs a simple message. In this case we just write the message to the
     * server applications standard output.
     */
    private void log(String message) {
        System.out.println(message);
    }
}
