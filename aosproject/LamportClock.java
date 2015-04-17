/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 *
 * @author GbadeboAyoade
 */
public class LamportClock {

   
    public final static int NumProc = 10;
    public int processid;
    private  int clockLamport;

    public LamportClock(int procid) {
        processid = procid;
        clockLamport = 0;
    }

    public synchronized void incrementClock() {
       
        ++clockLamport;
    }
    public synchronized void setClockValue(int clockval){
        clockLamport = clockval;
    }
    
     public synchronized int getClockValue(){
       return clockLamport;
    }

    public synchronized  void merge(LamportClock recvClock) {

        //the recv proc will increment clock and the merge with the recclock vector
        int i = 0;
       setClockValue(recvClock.getClockValue()+1);
        clockLamport = Math.max(getClockValue(), recvClock.getClockValue());
  

    }  
    
    @Override
    public synchronized String toString() {
        
      String output ="Procid: "+this.processid + " [";
     
       output+= this.clockLamport+"]";
       
       return output;
    }

    
    
   
}
class VectorClockComparator implements Comparator<LamportClock> {

    @Override
    public int compare(LamportClock cl1, LamportClock cl2) {
        //if 1 is greater return +1 else -1 if equal return 0
        //loop throgh the clocks 
        int countClock1 = 0;
        int countClock2 = 0;
        
        if(cl1.getClockValue() > cl2.getClockValue()){
            
            return 1;
        }
        else if(cl1.getClockValue() < cl2.getClockValue()){
            return -1;
        }
        else if(cl1.getClockValue() == cl2.getClockValue()){
            
             if (cl1.processid > cl2.processid) {

                return 1;
            } else {

                return -1;
            }
        }
        return 0;
    }
}