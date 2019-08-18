package ui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JLabel lblLoading;
	
	public LoadingPanel() {
		super();
		setLayout(null);
		setVisible(true);
		setBackground(new Color(0f,0f,0f,0.8f));		

		lblLoading = new JLabel();
		ImageIcon icon = new ImageIcon(this.getClass().getResource("/images/loading.gif"));
		lblLoading.setIcon(icon);		
		lblLoading.setBounds(750, 250, 200, 200);
		add(lblLoading);
	}
}
