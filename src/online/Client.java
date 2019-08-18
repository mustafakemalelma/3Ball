package online;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import listeners.OnlineListener;
import manager.GameManager;
import models.Ball;



public class Client { 
	
	
	private OnlineListener onlineListener;
	
    private Socket socket = null; 
    
    private ObjectInputStream input = null; 
    private ObjectOutputStream output = null; 
    
    private boolean isMyTurn;
    private boolean isGameDying;
  
    public Client(String serverIp,OnlineListener ol) throws IOException {
    	onlineListener = ol;
    	
    	setMyTurn(false);
    	setGameDying(false);
    	
    	System.out.println("trying to open");
    	socket = new Socket(serverIp, 3466);
    	System.out.println("Client is created");
    } 
    
    public void startClient() {
    	try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject("CLIENT_CONNECTED");
            output.flush();
            
            input = new ObjectInputStream(socket.getInputStream());
            Ball[] balls = new Ball[3];
            for (int i = 0; i < balls.length; i++) {
				Ball b = (Ball) input.readObject();
				balls[i] = new Ball(b.getBallId(), b.getColor(), b.getX(), b.getY());
			}
            
            onlineListener.onClientConnectedToServer(balls);
            onlineListener.onConnectionSuccess();
            listen();
        } 
        catch(Exception e) { 
            System.out.println("Error when trying to connect to the server => " + e.getMessage()); 
            e.printStackTrace();
        }
    }
  
    public void closeConnections() throws IOException {
    	if(output != null)
    		output.close();
    	
    	if(input != null)
    		input.close();
    	
    	if(socket != null)
    		socket.close();
    }

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	
	public boolean isGameDying() {
		return isGameDying;
	}

	public void setGameDying(boolean isGameDying) {
		this.isGameDying = isGameDying;
	}

	public void listen() {
		while(true) {
			if(isGameDying())
				break;
			
			try {
				String check = (String) input.readObject();
				if(check.equals("GAME_FINISHED")) {
					isMyTurn = false;
					GameManager.getGameManager().getMainFrame().getSidePanel().updateStatusLabel("You lose the game :((");
					break;
				}
				else if(!check.equals("BALL_SHOOTED"))
					continue;

				Ball activeBall = GameManager.getGameManager().getBm().getActiveBall();
				activeBall.shoot(input.readDouble(), input.readDouble());
				
				isMyTurn = true;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				onlineListener.onConnectionLost();
				break;
			} catch (IOException e) {
				e.printStackTrace();
				onlineListener.onConnectionLost();
				break;
			}
		}
	}

	public void shoot(double angle, double force) throws IOException {	
		output.writeObject("BALL_SHOOTED");
		output.writeDouble(angle);
		output.writeDouble(force);
		output.flush();
		
		isMyTurn = false;
	}

	public void sendFinish() throws IOException {
		output.writeObject("GAME_FINISHED");
		output.flush();
	}
} 