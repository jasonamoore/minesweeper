package minesweeper.util.anim;

import minesweeper.util.Timer;
import minesweeper.util.Utility;

public class Animation {
	
	public static final int HOLD = 0;
	public static final int LINEAR = 1;
	public static final int CUBIC = 2;

	private Timer timer;
	
	private double[] keyframes;
	private int[] times;
	private int[] transitions;

	private boolean playing = false;
	private boolean looping = false;

	private int frame;

	private double value;
	private double defval = 0;
	
	private boolean empty = false;
	
	public Animation() {
		empty = true;
		defval = 0;
	}
	
	public Animation(double def) {
		empty = true;
		defval = def;
	}

	public Animation(int start, boolean started, boolean loop, double[] keys, int[] time, int[] trans) {
		frame = start;
		looping = loop;
		keyframes = keys;
		times = time;
		transitions = trans;
		value = keyframes[frame];
		if (started) start();
	}

	public Animation(int start, boolean started, boolean loop, double[] keys, int time, int trans) {
		frame = start;
		looping = loop;
		timer = new Timer(false);
		keyframes = keys;
		times = new int[keys.length];
		for (int i = 0; i < times.length; i++) times[i] = time;
		transitions = new int[keys.length];
		for (int i = 0; i < transitions.length; i++) transitions[i] = trans;
		value = keyframes[frame];
		if (started) start();
	}

	public void start() {
		if (empty) return;
		playing = true;
		frame = 0;
		timer.reset(true);
	}

	public void pause() {
		playing = false;
		timer.pause();
	}

	public void resume() {
		playing = true;
		timer.resume();
	}

	public int getIndex() {
		return frame;
	}
	
	public int getLength() {
		return keyframes.length;
	}
	
	public double getValue() {
		if (empty) return defval;
		update();
		return value;
	}

	public int getIntValue() {
		if (empty) return (int) defval;
		update();
		return (int) value;
	}

	public boolean isPlaying() {
		update();
		return playing;
	}

	public boolean isFinished() {
		update();
		return !playing && frame == keyframes.length - 1;
	}
	
	public void update() {
		if (!playing || empty) return;
		
		int elapse = timer.elapsed() - times[frame];
		while (elapse > 0) {
			if (!increment()) {
				value = keyframes[++frame];
				return;
			}
			elapse -= times[frame];
		}
		
		double pkey = keyframes[frame];
		double nkey = keyframes[(frame + 1) % keyframes.length];
		int time = times[frame];
		int tran = transitions[frame];
		
		if (tran == Animation.HOLD) {
			value = pkey;
		} else if (tran == Animation.LINEAR) {
			value = pkey + (nkey - pkey) * ((double) timer.elapsed() / (double) time);
		}
	}

	private boolean increment() {
		if (frame >= keyframes.length - 2 && !looping) {
			playing = false;
			return false;
		}
		frame = (frame + 1) % keyframes.length;
		timer.reset(true);
		return true;
	}
	
	public static Animation generateShake(int shakeAmount, int threshold, boolean start) {
		double[] randFrames = new double[shakeAmount];
		for (int i = 0; i < shakeAmount - 1; i++) {
			double shakeRange = (shakeAmount / (i + 1)) * 0.5;
			if (shakeRange < threshold)
				shakeRange = 0;
			double shake = Utility.randNumSigned(0, shakeRange);
			randFrames[i] = shake;
		} 
		randFrames[shakeAmount - 1] = 0;
		return new Animation(0, start, false, randFrames, 2, 0);
}

}