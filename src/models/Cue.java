package models;
import java.awt.Color;
import java.awt.Point;

import manager.GameManager;
import ui.SidePanel;

public class Cue {
	
	public static final double MAX_SHOOT_FORCE = 50;

	private int strength;
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private Color color;
	
	private Ball activeBall;
	
	private double angle;
	private int offset;
	
	private Point startPoint;
	private Point endPoint;
	
	public static final int DEFAULT_OFFSET = 50;
	public static final int CUE_SIZE = 400;
	
	private SidePanel sidePanel;
	
	public Cue() {
		setColor(Color.WHITE);
		setAngle(0);
		setOffset(0);
		setStrength(30);
		
		activeBall = GameManager.getGameManager().getBm().getActiveBall();
		sidePanel = GameManager.getGameManager().getMainFrame().getSidePanel();
		
		setNewPosition();
		
	}
	
	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int getX2() {
		return x2;
	}

	public void setX2(int x2) {
		this.x2 = x2;
	}

	public int getY2() {
		return y2;
	}

	public void setY2(int y2) {
		this.y2 = y2;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public void rotate(double angle) {
		setAngle(angle);
		setNewPosition();
	}
	
	
	public void setNewPosition() {
		Point activeBallCenter = activeBall.getCenterPoint();
		int x1 = (int)(Math.cos(angle) * (offset + DEFAULT_OFFSET) + activeBallCenter.getX());
		int y1 = (int)(Math.sin(angle) * (offset + DEFAULT_OFFSET) + activeBallCenter.getY());
		int x2 = (int)(Math.cos(angle) * (offset + DEFAULT_OFFSET + CUE_SIZE) + activeBallCenter.getX());
		int y2 = (int)(Math.sin(angle) * (offset + DEFAULT_OFFSET + CUE_SIZE) + activeBallCenter.getY());
		
		setX1(x1);
		setX2(x2);
		setY1(y1);
		setY2(y2);
	}

	public void pull(Point mousePos) {
		if(startPoint == null) return;
		
		double delta = mousePos.distance(startPoint.getX(), startPoint.getY());
		offset =(int) Math.min(delta, MAX_SHOOT_FORCE);
		
		int posX = (int) (offset / MAX_SHOOT_FORCE * 250 - 250);
		sidePanel.setPower(posX);
		setNewPosition();
	}
	
	public double getShootForce() {
		return (offset / MAX_SHOOT_FORCE) * strength;
	}

}
