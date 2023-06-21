package src2;

public class ball extends Thread{
    int xVelocity = 4;
    int yVelocity = 4;
    int radius = 4;
    int posX;
    int posY;
    int score;
    
    @Override
    public void run(){
        while(true){
            move();
            try{
                sleep(10);
            }catch(InterruptedException ex){
                System.out.println(ex);
                return;
            }
        }
    }

    public ball(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
        score = 0;
    }

    public void move(){
        if(posX + xVelocity > 800){ //hits right side posX
            posX = 800 - radius;
            xVelocity = xVelocity * -1;

        }else if(posX + xVelocity == 0){ //hits left side posX
            score += 1;
            posX = radius;
            xVelocity = xVelocity * -1;
        }
        
        if(posY + yVelocity > 500){ //hits bottom posY
            posY = 500 - radius;
            yVelocity = yVelocity * -1;
        }else if((posY + yVelocity) < 1){ //hits top posY
            posY = radius; 
            yVelocity = yVelocity * -1; 
        }

        //incrememnting position 
        posX += xVelocity;
        posY += yVelocity;
    }


}
