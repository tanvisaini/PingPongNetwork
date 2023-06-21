package src2;
import java.time.*;
import java.time.LocalDateTime;
import java.util.*;


public class watch extends Thread{
    int startM;
    int startS;
    long minutes; 
    long seconds;
    long start;
    public Date d;

    public watch(){
        d=new Date();  
        startM = d.getMinutes();
        startS = d.getSeconds();
        start = System.currentTimeMillis()/1000;
        minutes = 0;
        seconds = 0;
    }

    @Override
    public void run(){
        while(true){
            try{
                long now = System.currentTimeMillis()/1000;
                seconds = now - start;
                if(seconds >= 60){
                    minutes += 1;
                }    
                //System.out.println("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }catch(Exception e){
                System.out.println(e);
            }
    }
    }

    public static void main(String args[]){
       watch newwatch = new watch();
       Thread watchthread = new Thread(newwatch);
       watchthread.start();
    }

    public long getMinutes(){
        return minutes;
    }

    public long getSeconds(){
        return seconds;
    }
}
