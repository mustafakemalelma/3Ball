package main;
import manager.GameManager;

public class Source {

	public static void main(String[] args) {
		GameManager gm = GameManager.getGameManager();
		gm.startGame();
	}

}
