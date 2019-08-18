package models;

import java.io.Serializable;

import main.Utils;

public class Player implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8562712565256168139L;
	
	private String nickname;
	private int score;
	
	public Player() {
		setNickname("gameplayer-" + Utils.randomAlphaNumeric(4));
		setScore(0);
	}
	
	public Player(String nick) {
		setNickname(nick);
		setScore(0);
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
