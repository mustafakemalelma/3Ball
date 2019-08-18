package ui;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class PowerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Color START;
	private Color FINISH;
	
	private int posX;
	
	public PowerPanel() {
		super();
		setLayout(null);
		setVisible(true);
		setBackground(new Color(0,0,0,0));
		
		START = Color.GREEN;
		FINISH = Color.red;
		
		setPosX(-250);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2d =(Graphics2D) g;
		
		g2d.setColor(Color.white);
		g2d.setStroke(new BasicStroke(4));
		g2d.fillRect(0,0,250,20);
		
		double ratio = Math.abs(posX) / 250.0;
		int red = (int) Math.abs((ratio * START.getRed()) + ((1 - ratio) * FINISH.getRed()));
		int green = (int) Math.abs((ratio * START.getGreen()) + ((1 - ratio) * FINISH.getGreen()));
		int blue = (int) Math.abs((ratio * START.getBlue()) + ((1 - ratio) * FINISH.getBlue()));
		
		g2d.setColor(new Color(red, green, blue));
		g2d.fillRect(posX, 0, 250, 20);
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
		repaint();
	}
	
}
