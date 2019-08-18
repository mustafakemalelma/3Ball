package ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import listeners.ClockListener;
import main.GameClock;
import main.Utils;
import manager.GameManager;
import models.Player;

public class SidePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GameManager gm;
	
	PowerPanel powerPanel;
	JLabel statusLabel;
	JLabel scoreLabel;
	JLabel speedLabel;
	JLabel clockLabel;
	JButton exitButton;
	JList<String> highscoresList;
	GameClock gameClock;
		
	public SidePanel() {
		super();
		setLayout(null);
		setBackground(new Color(0,0,0));
		
		gm = GameManager.getGameManager();
		gameClock = new GameClock(new ClockListener() {
			
			@Override
			public void onClockTick(String clock) {
				clockLabel.setText(clock);
			}
		});
		
		JLabel backgroundImg = Utils.getImageLabel("/images/logo.png", 250, 120);
		backgroundImg.setBounds(25, 25, 250, 120);
		add(backgroundImg);
		
		scoreLabel = new JLabel("", JLabel.LEFT);
		scoreLabel.setText("Score:" + gm.getScore());
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
		scoreLabel.setBounds(25,180,125,20);
		add(scoreLabel);
		
		speedLabel = new JLabel("Speed: 0 km/h");
		speedLabel.setForeground(Color.WHITE);
		speedLabel.setBounds(25, 225, 125, 20);
		add(speedLabel);
		
		clockLabel = new JLabel("00:00", JLabel.RIGHT);
		clockLabel.setForeground(Color.WHITE);
		clockLabel.setBounds(150, 225, 125, 20);
		add(clockLabel);
		
		JLabel powerLbl = new JLabel("Power :", JLabel.CENTER);
		powerLbl.setForeground(Color.WHITE);
		powerLbl.setBounds(25,275,250,20);
		add(powerLbl);
		
		powerPanel = new PowerPanel();
		powerPanel.setBounds(25,300,250,20);
		add(powerPanel);
		
		statusLabel = new JLabel("", JLabel.CENTER);
		statusLabel.setText("Have Fun !");
		statusLabel.setForeground(Color.WHITE);
		statusLabel.setBounds(25, 375, 250, 20);
		add(statusLabel);
		
		JLabel highScoresLbl = new JLabel("Highscores", JLabel.CENTER);
		highScoresLbl.setForeground(Color.WHITE);
		highScoresLbl.setBounds(25,425,250,20);
		add(highScoresLbl);
		
		highscoresList = new JList<String>();
		highscoresList.setBounds(25,450, 250, 100);
		add(highscoresList);
		
		exitButton = new JButton("Exit Game");
		exitButton.setForeground(Color.BLACK);
		exitButton.setBackground(Color.WHITE);
		exitButton.setBounds(75, 600, 150, 50);
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gm.exitGame();
			}
		});
		add(exitButton);
	}
	
	public void updateHighScoreList() {
		ArrayList<Player> playerHighscores = gm.getPlayerHighscores();
		String[] temp = new String[playerHighscores.size()];
		for (int i = 0; i < playerHighscores.size(); i++) {
			Player player = playerHighscores.get(i);
			temp[i] = player.getNickname() + "  -  " + player.getScore();
		}
		highscoresList.setListData(temp);
	}

	public void startGameClock() {
		gameClock.startClock();
	}
	public void stopGameClock() {
		gameClock.stopClock();
		clockLabel.setText("00:00");
	}
	
	public void setPower(int posX) {
		powerPanel.setPosX(posX);
	}
	
	public void setSpeed(double speed) {
		speedLabel.setText("Speed: " + (int)speed + " km/h");
	}
	
	public void updateStatusLabel(String text) {
		statusLabel.setText(text);
	}
}
