package main;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import listeners.ClockListener;

public class GameClock extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final SimpleDateFormat clockFormat = new SimpleDateFormat("mm:ss");
	
	private Timer clockTimer;
	private TimerTask clocking;
	
	private long startTime;
	
	private ClockListener cl;
	
	public GameClock(ClockListener cl) {
		this.cl = cl;
	}
	
	public void startClock() {
		startTime = System.currentTimeMillis();
		
		clockTimer = new Timer();
		clocking = new TimerTask() {
			
			@Override
			public void run() {
				Date elapsed = new Date(System.currentTimeMillis() - startTime);
				
				cl.onClockTick(clockFormat.format(elapsed));
			}
		};
		
		clockTimer.schedule(clocking,0,1000);
	}
	
	public void stopClock() {
		if(clockTimer == null || clocking == null)
			return;
		
		clocking.cancel();
		clockTimer.cancel();
		clockTimer.purge();
	}
}
