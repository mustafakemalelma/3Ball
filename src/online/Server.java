package online;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import listeners.OnlineListener;
import manager.GameManager;
import models.Ball;
import ui.SidePanel;

public class Server { 

	private OnlineListener onlineListener;
	
	GameManager gm;
	
	private Socket socket; 
    private ServerSocket server;
    
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
    private boolean isMyTurn;
    private boolean isGameDying;

    public Server(OnlineListener ol) throws IOException {
    	
    	gm = GameManager.getGameManager();
    	
    	onlineListener = ol;
    	
    	setMyTurn(false);
    	setGameDying(false);
    	
    	server = new ServerSocket(3466);
    	System.out.println("Server is created");
    }
    
    public void startServer(Ball[] balls) {
    	SidePanel sidePanel =  gm.getMainFrame().getSidePanel();
    	try { 
            sidePanel.updateStatusLabel("Waiting for a client ..."); 
            socket = server.accept(); 
            
            sidePanel.updateStatusLabel("Client is connected.Checking connections ...");
            input = new ObjectInputStream(socket.getInputStream());
            try {
				String comingInput = (String) input.readObject();
				System.out.println("comingInput = " + comingInput);
			} catch (ClassNotFoundException e) {
				System.out.println("coming input is not a string");
				e.printStackTrace();
			}
            
            sidePanel.updateStatusLabel("Trying to sent balls ..."); 
            output = new ObjectOutputStream(socket.getOutputStream());            
            for (Ball ball : balls) {
				output.writeObject(ball);
			}
            output.flush();
            
            sidePanel.updateStatusLabel("Connected to the client");
            onlineListener.onConnectionSuccess();
            listen();
        }
        catch(IOException e) { 
            System.out.println("Exception occured when server is creating => " + e.getMessage());
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
    	
    	if(server != null)
    		server.close();
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
				
				Ball activeBall = gm.getBm().getActiveBall();
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
		isMyTurn = false;

		output.writeObject("BALL_SHOOTED");
		output.writeDouble(angle);
		output.writeDouble(force);
		output.flush();
	}

	public void sendFinish() throws IOException {
		output.writeObject("GAME_FINISHED");
		output.flush();
	}

} 