package listeners;

import models.Ball;

public interface OnlineListener {

	public void onClientConnectedToServer(Ball[] balls);
	
	public void onConnectionSuccess();
	
	public void onConnectionLost();
	
}
