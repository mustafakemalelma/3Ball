package manager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import listeners.OnlineListener;
import models.Ball;
import models.Cue;
import online.Client;
import online.Server;

public class OnlineManager implements OnlineListener {

	private GameManager gm;

	private Server server;
	private String serverIp;
	private Client client;

	private Thread networkThread;

	public OnlineManager(String serverIp) {
		gm = GameManager.getGameManager();
		server = null;
		this.serverIp = serverIp;
		client = null;
	}

	public boolean isServer() {
		return server != null && client == null;
	}

	public void createOrJoinRoom() throws IOException {
		if (serverIp.length() == 0) {
			server = new Server(this);

		} else {
			client = new Client(serverIp, this);
		}
	}

	public void startOnlineGame() {
		if (isServer()) {
			networkThread = new Thread(new Runnable() {
				@Override
				public void run() {
					server.startServer(gm.getBm().getBalls());
				}
			});
		} else {
			networkThread = new Thread(new Runnable() {
				@Override
				public void run() {
					client.startClient();
				}
			});
		}
		networkThread.start();
	}

	public boolean isReady() {
		if (isServer())
			return server.isMyTurn();
		else
			return client.isMyTurn();
	}

	@Override
	public void onClientConnectedToServer(Ball[] balls) {
		gm.getBm().setBalls(balls);
		gm.getBm().setActiveBall(balls[2]);
		gm.setCue(new Cue());

		gm.getMainFrame().renderLayout();
		gm.getMainFrame().getSidePanel().updateStatusLabel("Connected to the server");
	}

	@Override
	public void onConnectionSuccess() {
		if (isServer())
			server.setMyTurn(true);
		else
			client.setMyTurn(false);

		gm.getMainFrame().getSidePanel().startGameClock();
	}

	public void shoot(double angle, double force) {
		try {
			if (isServer())
				server.shoot(angle, force);
			else
				client.shoot(angle, force);
		} catch (IOException e) {
			System.out.println("Can not shoot");
			e.printStackTrace();
		}
	}

	public void closeConnections() {
		try {
			if (isServer()) {
				server.setGameDying(true);
				server.closeConnections();
			} else {
				client.setGameDying(true);
				client.closeConnections();
			}
		} catch (IOException e) {
			System.out.println("can not close connections");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("null pointer exception");
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionLost() {
		gm.exitGame();
	}

	public String getServerIp() {
		InetAddress localhost = null;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		if (localhost == null)
			return "";

		return (localhost.getHostAddress()).trim();
	}

	public void finishTheGame() {
		try {
			if (isServer())
				server.sendFinish();
			else
				client.sendFinish();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
