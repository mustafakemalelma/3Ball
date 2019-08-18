package ui;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.Utils;
import manager.BallManager;
import manager.GameManager;
import models.Ball;
import models.Cue;
import models.MODE;

public class TablePanel extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static Rectangle tableBounds = new Rectangle(150, 150, 1200, 400);
	private GameManager gm;
	private BallManager bm;

	BufferedImage tableImg;
	BufferedImage girl1Img;
	BufferedImage girl2Img;
	BufferedImage redBallImg;
	BufferedImage purpleBallImg;
	BufferedImage whiteBallImg;

	public TablePanel() {
		super();

		addMouseListener(this);
		addMouseMotionListener(this);

		setBackground(new Color(33, 33, 33));

		gm = GameManager.getGameManager();
		bm = gm.getBm();

		try {
			tableImg = ImageIO.read(Utils.loadResource("/images/table.png"));
			girl1Img = ImageIO.read(Utils.loadResource("/images/girl1.png"));
			girl2Img = ImageIO.read(Utils.loadResource("/images/girl2.png"));
			redBallImg = ImageIO.read(Utils.loadResource("/images/red-ball.png"));
			purpleBallImg = ImageIO.read(Utils.loadResource("/images/purple-ball.png"));
			whiteBallImg = ImageIO.read(Utils.loadResource("/images/white-ball.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color startColor = new Color(120, 0, 0);
		Color endColor = new Color(0, 0, 0);
		GradientPaint gradient = new GradientPaint(0, 350, startColor, 1500, 350, endColor);
		g2d.setPaint(gradient);
		g2d.fillRect(0, 0, 1500, 700);

		g.drawImage(girl1Img, -200, 120, null);
		g.drawImage(girl2Img, 1250, 120, null);
		
		g.drawImage(tableImg, tableBounds.x - 50, tableBounds.y - 50, null);

		renderBalls(g);
		renderCue(g);
	}

	private void renderCue(Graphics g) {
		if (!canShoot())
			return;

		Cue cue = gm.getCue();

		g.setColor(Color.white);
		g.drawLine(cue.getX1(), cue.getY1(), cue.getX2(), cue.getY2());
	}

	private void renderBalls(Graphics g) {
		Ball[] balls = bm.getBalls();
		for (Ball ball : balls) {
			if(ball.getBallId().equals("Beyaz Diş"))
				g.drawImage(whiteBallImg, ball.getX(), ball.getY(), null);
			else if(ball.getBallId().equals("Füttü"))
				g.drawImage(redBallImg, ball.getX(), ball.getY(), null);
			else
				g.drawImage(purpleBallImg, ball.getX(), ball.getY(), null);
		}
	}

	private boolean canShoot() {
		if(gm.getPickedMode() == MODE.ONLINE && !gm.getOm().isReady())
			return false;
		
		if (bm.getActiveBall().getTimer().isRunning())
			return false;

		return true;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!canShoot())
			return;

		Cue cue = gm.getCue();
		cue.setStartPoint(e.getPoint());
		cue.setNewPosition();

		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!canShoot())
			return;

		Cue cue = gm.getCue();
		Ball activeBall = bm.getActiveBall();
		SidePanel sidePanel = gm.getMainFrame().getSidePanel();

		double force = cue.getShootForce();
		activeBall.shoot(cue.getAngle(), force);
		
		if(gm.getPickedMode() == MODE.ONLINE)
			gm.getOm().shoot(cue.getAngle(), force);

		cue.setOffset(0);
		cue.setNewPosition();
		sidePanel.setPower(-250);

		repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!canShoot())
			return;

		gm.getCue().pull(e.getPoint());
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!canShoot())
			return;

		Point mousePosition = e.getPoint();
		Point activeBallCenter = bm.getActiveBall().getCenterPoint();

		double dx = mousePosition.getX() - activeBallCenter.getX();
		double dy = mousePosition.getY() - activeBallCenter.getY();

		double angle = Math.atan2(dy, dx);
		gm.getCue().rotate(angle);
		repaint();
	}
}