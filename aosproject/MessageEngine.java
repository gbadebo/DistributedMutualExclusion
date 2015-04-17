/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

/**
 *
 * @author GbadeboAyoade
 */
public class MessageEngine {
    
   public LamportClock clock;
   public  MessageType MessageType;
    
    public MessageEngine(MessageType mType , LamportClock theClock){
        
        MessageType = mType;
        clock = theClock;
    }
    public synchronized LamportClock getClock(){
        return clock;
    }
    public synchronized MessageEngine stringToMessageEngine(String message){
        //mestype:processid:clockval0:clockval1:...cockval9
       
        String[] parts = message.split(":");
        String mestype = parts[0].trim(); 
        int procid = Integer.parseInt(parts[1].trim()); 
             
       
        MessageType type =null;
        LamportClock theClock = new LamportClock(procid);
        theClock.setClockValue(Integer.parseInt(parts[2].trim())); 
        
        if(mestype.compareTo("RQ")== 0 ){type = MessageType.RQ;}
        else if(mestype.compareTo("RP")== 0){type=MessageType.RP;}
        
       return new MessageEngine(type, theClock) ;
        
    }
        
      
    
    public synchronized String messageEngineToString( ){
        
            String messageString="";

            messageString+= this.MessageType;
            
            messageString+=":"+ this.clock.processid;
             messageString+=":"+ this.clock.getClockValue();
           
            return messageString;
            
    }
    
          
}

enum MessageType {
    RQ,RP
}