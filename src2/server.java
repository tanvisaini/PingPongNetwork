package src2;
// import src2.player;

import javax.swing.JFrame;
import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.lang.Thread;
import java.awt.Font;

//TANVI SAINI ts4175 SERVER.JAVA
//Server thread that creates player1, dummy player2 until they connect. Once connected, player2 is rewritten with proper variables 
//Player1 and player 2 is sent/read through object stream and ball thread starts and runs across screen. 
//Ball position information is sent through player1. ***I wasn't able to figure out if you can send threads over object stream and 
//I wasn't able to implement pointers in java either. Is there a better way to do this? ***
//Score is kept based on pong's position relative to player's bar. 

public class server extends JFrame implements KeyListener, Runnable, WindowListener {

    private static final long serialVersionUID = 123456789L;

    ball pong;
    public player player1;
    public player player2;
    private Thread pongThread;
    String message;
    String message2;
    static int max = 100;
    private Graphics graphic;
    private Socket clientsock;
    private ServerSocket serversock;
    boolean finished;
    watch watch;

    public server(String player1Name){
        //intializes players and ball
        player1 = new player(player1Name);
        message = "Waiting for Player 2";
        message2 = "Use Up and Down Arrows to Control the Bar!";
        player2 = new player("",776);
        pong = new ball(390,250);
        watch = new watch();

        //sets jframe
        this.setTitle("Ping Pong");
        this.setSize(800,500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
        this.setResizable(false);

        //listen for key press
        addKeyListener(this);
    }

    public static void main(String args[]){
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter username");

        String userName = myObj.nextLine();  // Read user input
        System.out.println("Username is: " + userName);  // Output user input
        server myServer = new server(userName);
		Thread myServerT = new Thread(myServer);
		myServerT.start();
    }

    @Override
	public void run() {
        try{
            serversock = new ServerSocket(3306); //serversocket at port 3306
            clientsock = serversock.accept();

            if(clientsock.isConnected()){ //loop is connected
                Thread pongThread = new Thread(pong);
                Thread watchThread = new Thread(watch);
                boolean connected = true;
                while(true){
                    if(player1.getScore() >= max || player2.getScore() >= max){
                        if(player1.getScore() > player2.getScore()){
                            message = "GAME OVER: " + player1.name + " WON";
                        }else{
                            message = "GAME OVER: " + player2.name + " WON";
                        }
                        pongThread.stop();
                    }

                    if(player2.painted && connected){
                        message = "";
                        message2 = "";
                        pongThread.start();
                        watchThread.start();
                        connected = false;
                    }

                    //check for collision, update ball position(send to player2) and update score 
                    checkCollision();
                    sendBallPos();

                    //send player 2 to here
                    ObjectInputStream getObj = new ObjectInputStream(clientsock.getInputStream());
					player2 = (player) getObj.readObject();
					
					//send player 1 to player 2
					ObjectOutputStream sendObj = new ObjectOutputStream(clientsock.getOutputStream());
                 	sendObj.writeObject(player1);

                     //continuously repaint jframe
                    repaint();
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private Image createImage(){
        BufferedImage bufferedImage = new BufferedImage(800, 500, BufferedImage.TYPE_INT_RGB);
	    graphic = bufferedImage.createGraphics();

        //background 
        graphic.setColor(new Color(108,215,52));
        graphic.fillRect(0, 0, 800, 500);
        graphic.setColor(Color.white);
	    graphic.fillRect(400, 0, 5, 500);
        //set score
        graphic.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        graphic.setColor(Color.white);
        graphic.drawString(""+player1.getScore(), 380, 100);
        graphic.drawString(""+player2.getScore(), 420, 100);
        graphic.drawString(player1.name, 80, 450);
        graphic.drawString(player2.name, 720, 450);
        graphic.drawString(message, 420, 260);
        graphic.drawString(message2, 420, 280); 
        graphic.setFont(new Font("TimesRoman", Font.PLAIN, 30));
        graphic.setColor(Color.black);
        graphic.drawString("" + String.format("%02d", watch.minutes) + ":" + String.format("%02d", watch.seconds), 369, 400);
        //draw player bars 
        graphic.setColor(new Color(240,192,90));
        graphic.fillRect(player1.getPosX(), player1.getPosY(), 25, 100);
        graphic.fillRect(player2.getPosX(), player2.getPosY(), 25, 100);
        //draw pong 
        graphic.setColor(new Color(233,49,76));
        graphic.fillOval(pong.posX, pong.posY, 25, 25);
        //message drawing 
	    // graphic.setColor(Color.white);
		// graphic.drawString(message, 420, 260);
        // graphic.drawString(message2, 420, 270);

        return bufferedImage;

    }

    @Override
	public void paint(Graphics graphic){
		graphic.drawImage(createImage(), 0, 0, this);
        player2.painted = true; //confirming player2 in connected
	}

    public void checkCollision(){
        //BUG IN CODE HERE: score will continously increase despite the ball only touching the side once. 
        //This is because I incrememnt score based off of location which is continuous causing the ifstatements to occur more than once. 
        //There is another glitch with player2's score it keeps reverting to 0. 
        if(pong.posX > player1.posX | pong.posX < player2.posX){
            finished = true;
        }

        if(pong.posX + pong.xVelocity > 799 & finished){ //hits right side posX
            finished = false;
            player1.score = player1.getScore() + 1;
            //System.out.println("player 1 + 1");
            finished = false;

        }else if(pong.posX + pong.xVelocity < 1 & finished){ //hits left side posX
            finished = false;
            player2.score = player2.getScore() + 1;
            //System.out.println("player 2 + 1");
            finished = false;
        }

        //bounces off player's rect
        if(pong.posX <= (player1.posX + 25) && (pong.posY + pong.radius) >= player1.posY && pong.posY <= (player1.posY+100)){
            pong.posX = player1.posX + 25;
            pong.xVelocity = pong.xVelocity * -1;
        }

        else if((pong.posX + pong.radius) >= (player2.posX) && (pong.posY + pong.radius) >= player2.posY && pong.posY <= (player2.posY+100)){
            pong.posX = player2.posX - 25;
            pong.xVelocity = pong.xVelocity * -1;
            System.out.println("hit player2 bar");
        }
    }

    public void sendBallPos(){
        //Updating position for client to also draw ball
        player1.ballX = pong.posX;
        player1.ballY = pong.posY;
    }

    @Override
	public void keyPressed(KeyEvent arg0) {
        int keycode = arg0.getKeyCode();
        if(keycode == KeyEvent.VK_UP){
			if(player1.posY - 10 > 0){
                player1.posY -= 10;
                repaint();
            }
			
		}
		if(keycode == KeyEvent.VK_DOWN){
			if(player1.posY + 10 < 400){
                player1.posY += 10;
                repaint();
            }
		}

    }

    @Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}



	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}



	@Override
	public void windowClosed(WindowEvent arg0) {
		 
	}



	@SuppressWarnings("deprecation")
	@Override
	public void windowClosing(WindowEvent arg0) {
	
		Thread.currentThread().stop();
		this.setVisible(false);
		try {
			clientsock.close();
            serversock.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}


    //leftover key listeners that are not used
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}



	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}



	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}



	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}


}
