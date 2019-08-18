package manager;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;

import main.Utils;
import models.Ball;
import models.Cue;
import models.MODE;
import models.Player;
import ui.MainFrame;
import ui.SidePanel;
import ui.TablePanel;

public class GameManager {
	
	private static final int WIN_GAME_THRESHOLD = 50;

	private static GameManager gm;
	private BallManager bm;
	private OnlineManager om;
	private MainFrame mainFrame;

	private Player mainPlayer;
	private int score;
	private ArrayList<Player> playerHighscores;

	public ArrayList<Player> getPlayerHighscores() {
		return playerHighscores;
	}

	private Cue cue;

	private MODE pickedMode;

	private GameManager() {
		setPickedMode(null);
		mainFrame = null;
		cue = null;

		bm = new BallManager();
	}

	public static GameManager getGameManager() {
		if (gm == null)
			gm = new GameManager();

		return gm;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public BallManager getBm() {
		return bm;
	}

	public void setBm(BallManager bm) {
		this.bm = bm;
	}

	public MODE getPickedMode() {
		return pickedMode;
	}

	public void setPickedMode(MODE pickedMode) {
		this.pickedMode = pickedMode;
	}

	public Cue getCue() {
		return cue;
	}

	public void setCue(Cue cue) {
		this.cue = cue;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public OnlineManager getOm() {
		return om;
	}

	public void setOm(OnlineManager om) {
		this.om = om;
	}

	public void addScore(int value) {
		if(value < 0 && score < Math.abs(value)) return;
		
		if (getPickedMode() == MODE.ONLINE && !om.isReady()) {			
			score += value;
			if(score >= WIN_GAME_THRESHOLD) finishTheGame();
		}
		else if (getPickedMode() == MODE.PRACTICE) 
			score += value;
	}

	private void finishTheGame() {
		mainFrame.getSidePanel().updateStatusLabel("You won the game !!!");
		om.finishTheGame();
	}

	public void startGame() {
		mainFrame = new MainFrame();
		mainFrame.setVisible(true);

		setScore(0);
	}

	public void pickMode(String nickname,String secondNickname,String serverIp, MODE mode) {
		setScore(0);
		mainFrame.updateScore();
		
		fetchHighScores();
		mainFrame.getSidePanel().updateHighScoreList();
		
		setMainPlayer(new Player(nickname));

		switch (mode) {
			case PRACTICE:
				createPracticeMode();
				break;
			case ONLINE:
				createOnlineMode(serverIp);
				break;
			default:
				break;
		}
	}
	private void createOnlineMode(String serverIp) {
		setPickedMode(MODE.ONLINE);

		setOm(new OnlineManager(serverIp));
		try {
			om.createOrJoinRoom();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		if (om.isServer()) {
			SidePanel sidePanel = mainFrame.getSidePanel();
			JLabel serverIpLabel = new JLabel("Server IP: " + om.getServerIp(), JLabel.CENTER);
			serverIpLabel.setForeground(Color.WHITE);
			serverIpLabel.setBounds(25, 350, 250, 20);
			sidePanel.add(serverIpLabel);

			initiateGame();
		}

		om.startOnlineGame();
	}

	private void createPracticeMode() {
		setPickedMode(MODE.PRACTICE);

		initiateGame();
	}

	private void initiateGame() {
		bm.setBalls(new Ball[3]);
		Ball[] balls = bm.getBalls();

		Point rndPointInTable1 = getRandomPositionInTable();
		balls[0] = new Ball("Füttü", Utils.getRandomColor(), rndPointInTable1.x, rndPointInTable1.y);
		Point rndPointInTable2 = getRandomPositionInTable();
		balls[1] = new Ball("Elma", Utils.getRandomColor(), rndPointInTable2.x, rndPointInTable2.y);
		Point rndPointInTable3 = getRandomPositionInTable();
		balls[2] = new Ball("Beyaz Diş", Color.WHITE, rndPointInTable3.x, rndPointInTable3.y);

		bm.setActiveBall(balls[2]);
		cue = new Cue();

		mainFrame.renderLayout();
	}

	public Point getRandomPositionInTable() {
		Random rnd = new Random();

		Rectangle tableBounds = TablePanel.tableBounds;
		int minX = tableBounds.x + (Ball.DEFAULT_BALL_SIZE / 2);
		int rndX;
		int minY = tableBounds.y + (Ball.DEFAULT_BALL_SIZE / 2);
		int rndY;
		do {
			rndX = rnd.nextInt(tableBounds.width - 2 * Ball.DEFAULT_BALL_SIZE) + minX;
			rndY = rnd.nextInt(tableBounds.height - 2 * Ball.DEFAULT_BALL_SIZE) + minY;
		} while (isThereAnyBallInPosition(rndX, rndY));

		return new Point(rndX, rndY);
	}

	private boolean isThereAnyBallInPosition(int xPos, int yPos) {
		Ball[] balls = bm.getBalls();
		for (Ball ball : balls) {
			if (ball == null)
				continue;

			Point ballCenter = ball.getCenterPoint();
			double distance = Math.hypot(ballCenter.x - xPos, ballCenter.y - yPos);
			if (distance <= Ball.DEFAULT_BALL_SIZE)
				return true;
		}

		return false;
	}

	public void exitGame() {
		if (getPickedMode() == MODE.ONLINE)
			om.closeConnections();

		mainPlayer.setScore(score);
		saveScoreOnDisk();

		mainFrame.getSidePanel().stopGameClock();
		setPickedMode(null);
		mainFrame.renderLayout();
	}

	public Player getMainPlayer() {
		return mainPlayer;
	}

	public void setMainPlayer(Player mainPlayer) {
		this.mainPlayer = mainPlayer;
	}

	private void saveScoreOnDisk() {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("highscores.mf");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			playerHighscores.add(mainPlayer);
			for (int i = 0; i < playerHighscores.size(); i++) {
				oos.writeObject(playerHighscores.get(i));
			}
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fetchHighScores() {

		FileInputStream fis = null;
		ObjectInputStream ois = null;

		playerHighscores = new ArrayList<Player>();

		try {
			fis = new FileInputStream("highscores.mf");
			ois = new ObjectInputStream(fis);

			while (true) {
				Player result;
				try {
					result = (Player) ois.readObject();
				} catch (Exception e) {
					break;
				}
				playerHighscores.add(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			if (ois != null)
				ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
