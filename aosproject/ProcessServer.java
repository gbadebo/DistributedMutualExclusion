package aosproject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessServer {

    private static LamportClock clock;
    static LamportClock cloneClock;
    static boolean intensionToEnter;
    private static CriticalVar mycriticBuff;
    static int numofprocess;
    static String loggingServer;
    static int loggingPort;
    static ConcurrentHashMap<Integer, String> myReq;
    static String[] myservernames;
      static int[] myserverports;

    public static synchronized LamportClock getClock() {

        return clock;
    }

    public static synchronized LamportClock getSetClone() {
        return cloneClock;
    }

    public static synchronized LamportClock getClonedClock() {

        LamportClock theclock = getClock();
        LamportClock requestClock = new LamportClock(theclock.processid);

        requestClock.setClockValue(theclock.getClockValue());
        cloneClock = requestClock;
        return requestClock;


    }

    public static synchronized CriticalVar getbuffer() {
        return mycriticBuff;
    }

    public static void main(String[] args) throws Exception {

        System.out.println("The server is running.");
        int clientNumber = 0;

        int port = Integer.parseInt(args[0]);
        int procid = Integer.parseInt(args[1]);
        ServerSocket listener = new ServerSocket(port);
        clock = new LamportClock(procid);

        mycriticBuff = new CriticalVar();
        mycriticBuff.setVal(0);//0 not, 1 trying to, 2 in critical
        myReq = new ConcurrentHashMap();
       
        configure();
        for (int i = 0; i < ProcessServer.numofprocess ; i++) {
            myReq.put(myserverports[i], myservernames[i]);
        }

        new RequestGenerator(getClock(), port).start();

        if (port == myserverports[0]) {
            new ServerLogger().start();
        }


        try {
            while (true) {

                new RequestProcessor(listener.accept(), getClock()).start();
            }
        } finally {
            listener.close();
        }


    }

    public static void configure() {
        // the first server holds the logging server
        try {
            // Open the file
            FileInputStream fstream = new FileInputStream("config.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

//Read File Line By Line
            while ((strLine = br.readLine()) != null) {
               
                if(strLine.startsWith("N"))
                {
                   
                    ProcessServer.numofprocess = Integer.parseInt(strLine.substring(1));
                   myservernames = new String[ProcessServer.numofprocess];
                   myserverports = new int[ProcessServer.numofprocess];
                   
                }
                if(strLine.startsWith("S")){
                    
                    String [] temp = strLine.split(" ");
                    int index = Integer.parseInt((temp[0].substring(1)).trim());
                    myservernames[index] = temp[1].trim();
                    myserverports[index] = Integer.parseInt(temp[2].trim());
                    if (index == 0){loggingServer = temp[1].trim();}
                }
                if(strLine.startsWith("L")){
                    loggingPort = Integer.parseInt(strLine.substring(1).trim());
                }
            }

//Close the input stream
            br.close();
        } catch (IOException e) {
        }
    }
}

class CriticalVar {

    static int critic;

    public CriticalVar() {
    }

    static synchronized void setVal(int val) {
        critic = val;
    }

    static synchronized int getval() {
        return critic;
    }
}