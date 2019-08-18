package manager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import models.Ball;
import models.HIT_TYPE;
import models.Hitted;

public class BallManager {

	private Ball[] balls;
	private Ball activeBall;

	ArrayList<Hitted> activeBallHittedObjects;

	public BallManager() {
		setActiveBall(null);
		activeBallHittedObjects = new ArrayList<Hitted>();
	}

	public Ball getActiveBall() {
		return activeBall;
	}

	public void setActiveBall(Ball activeBall) {
		this.activeBall = activeBall;
	}

	public Ball[] getBalls() {
		return balls;
	}

	public void setBalls(Ball[] balls) {
		this.balls = balls;
	}

	public void addToActiveBallHittedObjects(Hitted hitted) {
		Iterator<Hitted> iter = activeBallHittedObjects.iterator();
		while (iter.hasNext()) {
			Hitted obj = iter.next();

			if (obj.getId() == hitted.getId())
				return;
		}

		activeBallHittedObjects.add(hitted);
	}

	public void checkCollision(Ball refBall) {

		for (Ball ball : balls) {
			if (refBall.getBallId().equals(ball.getBallId()))
				continue;

			double r = refBall.getCenterPoint().distance(ball.getCenterPoint());
			if (r < Ball.DEFAULT_BALL_SIZE) {
				handleCollision(refBall, ball);
				break;
			}
		}

		refBall.setCollidingNow(false);
	}

	public void handleCollision(Ball b1, Ball b2) {
		
		Point b1Center = b1.getCenterPoint();
		Point b2Center = b2.getCenterPoint();
		
		Point xAxis = new Point(b2Center.x - b1Center.x, b2Center.y - b1Center.y);
		double dfAngle = Math.atan2(xAxis.y, xAxis.x);
		
		double distanceOfCenters = Math.hypot(b2Center.x - b1Center.x, b2Center.y - b1Center.y);
		double relativeDistance = Math.ceil((Ball.DEFAULT_BALL_SIZE - distanceOfCenters) / 2);
		b1.setX(b1.getX() + (int)(relativeDistance * Math.cos(dfAngle) * -1));
		b1.setY(b1.getY() + (int)(relativeDistance * Math.sin(dfAngle) * -1));
		b2.setX(b2.getX() + (int)(relativeDistance * Math.cos(dfAngle)));
		b2.setY(b2.getY() + (int)(relativeDistance * Math.sin(dfAngle)));

		if (!b1.isCollidingNow() && !b2.isCollidingNow()) {
			b1.setCollidingNow(true);
			b2.setCollidingNow(true);

			if (b1.getBallId().equals(activeBall.getBallId())) {
				addToActiveBallHittedObjects(new Hitted(HIT_TYPE.BALL, b2.getBallId()));
			} else if (b2.getBallId().equals(activeBall.getBallId())) {
				addToActiveBallHittedObjects(new Hitted(HIT_TYPE.BALL, b1.getBallId()));
			}

			double scalerV1 = b1.getvX() * xAxis.x + b1.getvY() * xAxis.y;
			double normV1 = Math.sqrt(Math.pow(xAxis.x, 2) + Math.pow(xAxis.y, 2));

			double scalerV2 = b2.getvX() * xAxis.x + b2.getvY() * xAxis.y;
			double normV2 = Math.sqrt(Math.pow(xAxis.x, 2) + Math.pow(xAxis.y, 2));

			double f1 = 0;
			if (normV1 != 0)
				f1 = scalerV1 / normV1;

			double f2 = 0;
			if (normV2 != 0)
				f2 = scalerV2 / normV2;

			double dF = f1 - f2;

			double b1xSpeed = b1.getvX() + dF * Math.cos(Math.PI + dfAngle);
			double b1ySpeed = b1.getvY() + dF * Math.sin(Math.PI + dfAngle);

			double b2xSpeed = b2.getvX() + dF * Math.cos(dfAngle);
			double b2ySpeed = b2.getvY() + dF * Math.sin(dfAngle);

			b1.setvX(b1xSpeed);
			b1.setvY(b1ySpeed);

			b2.setvX(b2xSpeed);
			b2.setvY(b2ySpeed);

			if (!b1.getTimer().isRunning())
				b1.getTimer().start();
			if (!b2.getTimer().isRunning())
				b2.getTimer().start();
		}
	}

	public void checkPoint() {
		
		Thread pointCalculationThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!isEveryBallStopped());
				
				int size = activeBallHittedObjects.size();
				if (size == 0)
					return;

				int wallCounter = 0;
				int ballCounter = 0;
				for (int i = 0; i < activeBallHittedObjects.size(); i++) {
					Hitted hitted = activeBallHittedObjects.get(i);
					if (hitted.getHitType() == HIT_TYPE.BALL)
						ballCounter++;
					else
						wallCounter++;
				}

				if (wallCounter == 2 && ballCounter == 1) {
					GameManager.getGameManager().addScore(10);
					GameManager.getGameManager().getMainFrame().updateScore();
				}
				else if (wallCounter == 1 && ballCounter == 0) {
					GameManager.getGameManager().addScore(-5);
					GameManager.getGameManager().getMainFrame().updateScore();
				}
				else if (wallCounter == 0 && ballCounter == 1) {
					GameManager.getGameManager().addScore(-5);
					GameManager.getGameManager().getMainFrame().updateScore();
				}

				activeBallHittedObjects.clear();
			}
		});
		pointCalculationThread.start();
	}

	private boolean isEveryBallStopped() {
		for (Ball ball : balls) {
			if(ball.getTimer().isRunning())
				return false;
		}
		
		return true;
	}

}
