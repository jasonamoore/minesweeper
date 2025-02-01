package minesweeper.util;

public class Timer {

	// really basic class that keeps track of time!
	
	// super simple - only makes calculations when accessed.
	
	private long start;
	
	private long elapsedSum = 0;
	
	public Timer(boolean started) {
		if (started) resume();
	}
	
	// if paused return elapsedSum, because elapsedSum will have already been updated to the correct amount when pause() was called.
	// if not paused, add on the current elapsed time that has not yet been counted and added to elapsedSum (sum is only changed when timer pauses)
	public int elapsed() {
		if (paused) return (int) elapsedSum;
		else return (int) (elapsedSum + time() - start);
	}
	
	//returns number of full seconds passed (ie seconds rounded down)
	public int elapsedSec() {
		return (int) Math.floor(elapsed() / 1000.0);
	}
	
	private boolean paused = true;
	
	public void pause() {
		if (paused) return;
		
		paused = true;
		elapsedSum += time() - start;
	}
	
	public void resume() {
		if (!paused) return;
		
		paused = false;
		start = time();
	}
	
	//if paused, resume, if unpaused, pause
	public void toggle() {
		if (paused) resume();
		else pause();
	}
	
	public void reset(boolean restart) {
		paused = !restart; // if restart is true, the timer will start (unpause) immediately
		elapsedSum = 0;
		start = time();
	}
	
	//literally just a convenience method bc its shorter
	public static long time() {
		return System.currentTimeMillis();
	}
	
}
