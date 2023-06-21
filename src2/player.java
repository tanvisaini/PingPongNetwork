package src2;

import java.io.Serializable;

public class player implements Serializable{

    private static final long serialVersionUID = 123456789L;

    String name;
    int score;
    int posX;
    int posY;
    boolean painted;
    int ballX;
    int ballY;

    public player(String name){
        this.name = name;
        this.painted = false;
        posX = 1;
        posY = 225;
        ballX = 395;
        ballY = 250;
        score = 0;
    }

    public player(String name, int pos){
        this.name = name;
        this.painted = false;
        posX = pos;
        posY = 225;
        score = 0;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int num){
        this.score = num;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getPosX(){
        return this.posX;
    }

    public void setPosX(int num){
        this.posX = num;
    }

    public int getPosY(){
        return this.posY;
    }

    public void setPosY(int num){
        this.posY = num;
    }

    public void isPainted(boolean bool){
        this.painted = bool;
    }

    public void setBallX(int num){
        this.ballX = num;
    }

    public void setBallY(int num){
        this.ballY = num;
    }
}
