package ui;
import javax.swing.JFrame;

import manager.GameManager;
import models.MODE;

public class MainFrame extends JFrame {

	/**
	 * Main Container Frame
	 */
	private static final long serialVersionUID = 1L;

	private GameManager gm;

	private MenuPanel menuPanel;
	private TablePanel tablePanel;
	private SidePanel sidePanel;

	public MainFrame() {
		super("3 Ball");

		gm = GameManager.getGameManager();

		setSize(1800, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);

		menuPanel = new MenuPanel();
		menuPanel.setBounds(0, 0, 1800, 700);
		tablePanel = new TablePanel();
		tablePanel.setBounds(0, 0, 1500, 700);
		sidePanel = new SidePanel();
		sidePanel.setBounds(1500, 0, 300, 700);
		add(menuPanel);
		add(sidePanel);
		add(tablePanel);

		renderLayout();
	}

	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	public TablePanel getTablePanel() {
		return tablePanel;
	}

	public SidePanel getSidePanel() {
		return sidePanel;
	}

	public void renderLayout() {
		MODE pickedMode = gm.getPickedMode();
		
		if (pickedMode == null) {
			tablePanel.setVisible(false);
			sidePanel.setVisible(false);

			menuPanel.setVisible(true);
		} else {
			menuPanel.resetLoadingPanel();
			menuPanel.setVisible(false);

			tablePanel.setVisible(true);
			sidePanel.setVisible(true);
			
			if(pickedMode == MODE.ONLINE)
				sidePanel.stopGameClock();
			else
				sidePanel.startGameClock();
			
		}
	}

	public void repaintTable() {
		tablePanel.repaint();
	}

	public void updateScore() {
		sidePanel.scoreLabel.setText("SCORE : " + gm.getScore());
		sidePanel.repaint();
	}
}
