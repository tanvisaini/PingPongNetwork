package src2;

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
import java.awt.Font;

//TANVI SAINI ts4175 CLIENT.JAVA 
//mirrors same graphics as Server class. 

public class client extends JFrame implements KeyListener, Runnable, WindowListener{
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
    watch watch;
    Thread watchThread;

    public client(String player2Name){
        player1 = new player("user1");
        player2 = new player(player2Name, 776);
        message = "connected!";
        message2 = "Use Up and Down Arrows to Control the Player's Bar!";
        pong = new ball(395,250);
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
		client myClient = new client(userName);
		Thread myClientT = new Thread(myClient);
		myClientT.start();
	}

    @Override
    public void run(){
        try{
            clientsock = new Socket("localhost", 3306);
            if(clientsock.isConnected()){
                boolean connected = true;
                while(true){
                    if(player1.getScore() >= max || player2.getScore() >= max){
                        if(player1.getScore() > player2.getScore()){
                            message = "GAME OVER: " + player1.name + " WON";
                        }else{
                            message = "GAME OVER: " + player2.name + " WON";
                        }
                    }


            		//sending and writing objects 
            		 ObjectOutputStream sendObj = new ObjectOutputStream(clientsock.getOutputStream());
        			 sendObj.writeObject(player2);
        			 
        			 ObjectInputStream getObj = new ObjectInputStream(clientsock.getInputStream());
        			 player1 = (player) getObj.readObject();
                    //  pong = (ball) getObj.readObject();

                     if(player2.painted && connected){
                        watchThread = new Thread(watch);
                        watchThread.start();
                         message="";
                         message2 = "";
                         connected = false;
                     }
        			
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
        graphic.fillOval(player1.ballX, player1.ballY, 25, 25); //taking ball position based on server

        return bufferedImage;

    }

    @Override
	public void paint(Graphics graphic){
		graphic.drawImage(createImage(), 0, 0, this);
        player2.painted = true;
	}

    @Override
	public void keyPressed(KeyEvent arg0) {
        int keycode = arg0.getKeyCode();
        if(keycode == KeyEvent.VK_UP){
			if(player2.posY - 10 > 0){
                player2.posY -= 15;
                repaint();
            }
			
		}
		if(keycode == KeyEvent.VK_DOWN){
			if(player2.posY + 10 < 400){
                player2.posY += 15;
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
		} catch (IOException e) {
			System.out.println(e);
		}
	}



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
