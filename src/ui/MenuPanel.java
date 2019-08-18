package ui;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.Utils;
import manager.GameManager;
import models.MODE;

public class MenuPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GameManager gm;
	LoadingPanel loadingPanel;
	
	public MenuPanel() {
		super();
		gm = GameManager.getGameManager();
		
		setLayout(null);
		
		loadingPanel = new LoadingPanel();
		loadingPanel.setBounds(0, 0, 1800, 700);
		loadingPanel.setVisible(false);
		add(loadingPanel);
		
		JLabel lblName = new JLabel("Nickname: ");
		lblName.setBounds(1150, 320, 100, 20);
		lblName.setForeground(Color.WHITE);
		lblName.setFont(new Font("Arial", Font.PLAIN, 20));
		add(lblName);
		
		JTextField txtName = new JTextField("bestplayer");
		txtName.setBounds(1275, 310, 250, 50);
		add(txtName);
		
		JButton practiceButton = new JButton("Practice Mode");
		practiceButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				gm.pickMode(txtName.getText(),"","",MODE.PRACTICE);
			}
		});
		practiceButton.setBounds(1300, 380, 200, 50);
		practiceButton.setBorder(null);
		practiceButton.setBackground(new Color(235,47,6));
		practiceButton.setForeground(Color.WHITE);
		practiceButton.setFont(new Font("Arial", Font.PLAIN, 18));
		practiceButton.setOpaque(true);
		add(practiceButton);
		
		JLabel lblServerIp = new JLabel("Enter Server IP (leave empty for server creation): ");
		lblServerIp.setBounds(1100, 460, 400, 50);
		lblServerIp.setForeground(Color.WHITE);
		lblServerIp.setFont(new Font("Arial", Font.PLAIN, 16));
		add(lblServerIp);
		
		JTextField txtServerIp = new JTextField("");
		txtServerIp.setBounds(1125, 515, 250, 50);
		add(txtServerIp);
		
		JButton onlineButton = new JButton("Online Mode");
		onlineButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(txtServerIp.getText().length() > 0)
					loadingPanel.setVisible(true);
				gm.pickMode(txtName.getText(),"",txtServerIp.getText(),MODE.ONLINE);
			}
		});
		onlineButton.setBounds(1425 ,515, 200, 50);
		onlineButton.setBorder(null);
		onlineButton.setBackground(new Color(246,185,59));
		onlineButton.setForeground(Color.WHITE);
		onlineButton.setFont(new Font("Arial", Font.PLAIN, 18));
		onlineButton.setOpaque(true);
		add(onlineButton);
		
		JLabel backgroundImg = Utils.getImageLabel("/images/menu-bg.png");
		backgroundImg.setBounds(0, 0, 1800, 700);
		add(backgroundImg);
	}
	
	public void resetLoadingPanel() {
		loadingPanel.setVisible(false);
	}

}
