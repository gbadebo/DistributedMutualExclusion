/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

import java.net.Socket;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author GbadeboAyoade
 */
public class RequestGenerator extends Thread {

    LamportClock theClock;
    int Port;
    int count;

    public RequestGenerator(LamportClock myclock, int port) {
        theClock = myclock;
        Port = port;
        count = 0;
    }

    public void run() {
        boolean running = true;
        try {
            Thread.sleep(10000); //wait for others to start
        } catch (InterruptedException ex) {
        }

        while (running) {
            try {
                ProcessServer.getbuffer().setVal(0);
              
                if (count > 20 && count < 40 && theClock.processid % 2 == 0) {
                    try {
                    //    Thread.sleep(2000);
                       Thread.sleep(genRandomWaitTime(20, 500));
                    } catch (InterruptedException ex) {
                    }

                }
                if (count < 20) {
                    try {
                      //  Thread.sleep(2000);
                       Thread.sleep(genRandomWaitTime(10, 100));
                    } catch (InterruptedException ex) {
                    }
                }
                LamportClock myRequestClock;
                long startime;
                int numpend;
                synchronized (ProcessServer.getbuffer()) {
                    theClock.incrementClock();

                    startime = new Date().getTime();
                     myRequestClock = ProcessServer.getClonedClock();
                  
                  
                    

                }
                sendRequest(myRequestClock);//the first time this process is starting make sure they all request
             
                System.out.println("critical section true");

                count++;
               


                synchronized (ProcessServer.getbuffer()) {
                   

                    ProcessServer.getbuffer().setVal(2);
                    ProcessServer.getbuffer().notifyAll();
                    Thread criticalthread = new PerformCriticalSection(count, theClock, myRequestClock, Port, startime);

                  criticalthread.run();





                }
                System.out.println("critical section false");
                if (count == 40) {
                    running = false;
                }

            } catch (Exception ex) {
                running = false;
            }
        }
    }

    public boolean sendRequest(LamportClock theReqClock) {
     
       Thread[] mysendThreads = new Thread[LamportClock.NumProc];
       synchronized (ProcessServer.getbuffer()) {
        ProcessServer.getbuffer().setVal(1);
             ProcessServer.getbuffer().notifyAll();
       }
   //      
         int i=0;           
        for (int ports : ProcessServer.myReq.keySet()) {

            if (Port != ports) {
                mysendThreads[i] = new ClientProcess(ProcessServer.myReq.get(ports), ports, theClock, theReqClock);
                mysendThreads[i].start();
                i++;
               
        }}
         i=0;           
        for (int ports : ProcessServer.myReq.keySet()) {
            try{
            if (Port != ports) {
                mysendThreads[i].join();
                i++;
            }
            }catch(InterruptedException ex){
                
            }
               
        }
        try {
           
        } catch (Exception e) {
        }
       
        return true;

    }

    public int genRandomWaitTime(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        System.out.println("wait time" + randomNum);
        return randomNum;

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author GbadeboAyoade
 */
class PerformCriticalSection extends Thread {

    int count;
    LamportClock theClock;
    LamportClock myRequestClock;
    int Port;
    long startime;

    public PerformCriticalSection(int count, LamportClock clock, LamportClock myRequestClock, int Port, long startime) {
        this.count = count;
        this.theClock = clock;
        this.myRequestClock = myRequestClock;
        this.Port = Port;
        this.startime = startime;
    }

    public void run() {
        try {
            Socket socket = new Socket(ProcessServer.loggingServer, ProcessServer.loggingPort);
            socket.setSoTimeout(0); //infinite 
            socket.setKeepAlive(true);

            synchronized (ProcessServer.getbuffer()) {
                long entertime = new Date().getTime();

               
                new LogProcess(socket,"ID:"+this.theClock.processid+ " ENTER| count: " + count + " log:in critical section client side server " + Port + " time to enter " + (entertime - startime) + " clock:" + ProcessServer.getClock().toString()).run();
                
               
                Thread.sleep(20);//in critical section for 20msec
                
                


                long exittime = new Date().getTime();
                new LogProcess(socket,"ID:"+this.theClock.processid + " EXIT count: " + count + " log:exit critical section client side server  " + Port + " time to exit " + (exittime - entertime) + " clock:" + ProcessServer.getClock().toString()).run();
                socket.close();
                ProcessServer.getbuffer().setVal(0);
                ProcessServer.getbuffer().notifyAll();
                System.out.println(" i am aout "+ ProcessServer.getbuffer().getval());
            }





        } catch (Exception e) {
            // log("Error handling client# " + port + ": " + e);
        } finally {
            // try {
            //  socket.close();
            //  } catch (IOException e) {
            //   log("Couldn't close a socket, what's going on?");
            //   }
            //  log("Connection with client# " + port + " closed");
        }
    }

    private void log(String message) {
        System.out.println(message);
    }
}
