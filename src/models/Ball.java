package models;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.Timer;

import main.Utils;
import manager.BallManager;
import manager.GameManager;
import ui.SidePanel;
import ui.TablePanel;


public class Ball implements ActionListener, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static int DEFAULT_BALL_SIZE = 50;
	private static double ballFriction = 0.15;
	private static double speedThreshold = 0.4;
	private static int timerDelay = 16;

	private String ballId;
	
	transient private GameManager gm;
	transient private BallManager bm;
	transient private SidePanel sidePanel;
	private Color color;
	
	private int x;
	private int y;
	
	private double vX;
	private double vY;
	
	private boolean isCollidingNow;
	
	transient private Timer timer;

	public Ball() {
		gm = GameManager.getGameManager();
		bm = gm.getBm();
		sidePanel = gm.getMainFrame().getSidePanel();
		color = Color.WHITE;
		
		setX(0);
		setY(0);
		setvX(0);
		setvY(0);
		setCollidingNow(false);
		setBallId(Utils.randomAlphaNumeric(4));
	
		setTimer(new Timer(timerDelay, this));
	}
	
	public Ball(String id, Color color,int x,int y) {
		gm = GameManager.getGameManager();
		bm = gm.getBm();
		sidePanel = gm.getMainFrame().getSidePanel();
		this.color = color;
		this.ballId = id;
		
		setX(x);
		setY(y);
		setvX(0);
		setvY(0);
		setCollidingNow(false);
		
		setTimer(new Timer(timerDelay, this));
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public double getvX() {
		return vX;
	}

	public void setvX(double vX) {
		this.vX = vX;
	}

	public double getvY() {
		return vY;
	}

	public void setvY(double vY) {
		this.vY = vY;
	}
	
	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public String getBallId() {
		return ballId;
	}

	public void setBallId(String ballId) {
		this.ballId = ballId;
	}

	public boolean isCollidingNow() {
		return isCollidingNow;
	}

	public void setCollidingNow(boolean isColliding) {
		this.isCollidingNow = isColliding;
	}

	public Point getPoint() {
		return new Point(x,y);
	}
	
	public Point getCenterPoint() {
		return new Point(getX() + DEFAULT_BALL_SIZE / 2, getY() + DEFAULT_BALL_SIZE / 2);
	}
	
	public double getSpeed() {
		return Math.hypot(vX, vY);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		handleHittingWall();
		bm.checkCollision(this);
		
		double speed = Math.hypot(vX, vY);
 		double angle = Math.atan2(vY, vX);
		speed -= ballFriction;
				
		double speedX = Math.cos(angle) * speed;
		double speedY = Math.sin(angle) * speed;
		
		if(speed < speedThreshold) {
			if(ballId.equals(bm.getActiveBall().ballId))
				bm.checkPoint();
			
			timer.stop();
		}
		
		setvX(speedX);
		setvY(speedY);
		
		setX((int)(x + speedX));
		setY((int)(y + speedY));
	
		if(bm.getActiveBall().equals(this))
			sidePanel.setSpeed(getSpeed());
		
		gm.getMainFrame().repaintTable();
	}
	
	private void handleHittingWall() {
		Rectangle tableBounds = TablePanel.tableBounds;
		Point myCenter = getCenterPoint();
		
		double left = tableBounds.x + DEFAULT_BALL_SIZE / 2;
		double right = tableBounds.x + tableBounds.width - DEFAULT_BALL_SIZE / 2;
		
		double top = tableBounds.y + Ball.DEFAULT_BALL_SIZE / 2;
		double bottom = tableBounds.y + tableBounds.height - DEFAULT_BALL_SIZE / 2;
		
		if(myCenter.x < left || myCenter.x > right) {
			setvX(vX * -1);
			
			if(vX >= 0) {
				setX((int)tableBounds.x);
				
				if(ballId.equals(bm.getActiveBall().ballId))
					bm.addToActiveBallHittedObjects(new Hitted(HIT_TYPE.LEFT_SIDE, "left-wall"));
			}
			else {
				setX((int)tableBounds.x + tableBounds.width - DEFAULT_BALL_SIZE);
				
				if(ballId.equals(bm.getActiveBall().ballId))
					bm.addToActiveBallHittedObjects(new Hitted(HIT_TYPE.RIGHT_SIDE, "right-wall"));
			}
		}
		
		if(myCenter.y < top || myCenter.y > bottom) {
			setvY(vY * -1);
			
			if(vY >= 0) {
				setY((int)tableBounds.y);
				
				if(ballId.equals(bm.getActiveBall().ballId))
					bm.addToActiveBallHittedObjects(new Hitted(HIT_TYPE.TOP_SIDE, "top-wall"));
			}
			else {
				setY((int)tableBounds.y + tableBounds.height - DEFAULT_BALL_SIZE);
				
				if(ballId.equals(bm.getActiveBall().ballId))
					bm.addToActiveBallHittedObjects(new Hitted(HIT_TYPE.BOTTOM_SIDE, "bottom-wall"));
			}
		}
	}
	
	public void shoot(double angle, double force) {
		double vx = force * Math.cos(angle) * -1;
		double vy = force * Math.sin(angle) * -1;
		
		setvX(vx);
		setvY(vy);
		if(!timer.isRunning())
			timer.start();
	}
}
