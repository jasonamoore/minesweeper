package minesweeper.util.anim;

import minesweeper.util.Utility;

public class SpriteAnimation extends Animation {

	public static final int TRANSLATE = 0;
	public static final int ROTATE = 1;
	public static final int OPACITY = 2;
	public static final int SCALE = 3;
	public static final int SHEAR = 4;
	
	private int effect;
	
	public static final int X = 0;
	public static final int Y = 1;
	public static final int BOTH = 2;
	
	private int axis;
	private double[] anchor = new double[2];

	public int getEffect() {
		return effect;
	}
	
	public int getAxis() {
		return axis;
	}
	
	public double[] getAnchor() {
		return anchor;
	}
	
	public SpriteAnimation set(int effect) {
		this.effect = effect;
		return this;
	}
	
	public SpriteAnimation set(int effect, int axis) {
		this.effect = effect;
		this.axis = axis;
		return this;
	}
	
	public SpriteAnimation set(int effect, int axis, double anchorX, double anchorY) {
		this.effect = effect;
		this.axis = axis;
		anchor[0] = anchorX;
		anchor[1] = anchorY;
		return this;
	}
	
	public SpriteAnimation() {
		super();
	}
	
	public SpriteAnimation(double def) {
		super(def);
	}

	public SpriteAnimation(int start, boolean started, boolean loop, double[] keys, int[] time, int[] trans) {
		super(start, started, loop, keys, time, trans);
	}

	public SpriteAnimation(int start, boolean started, boolean loop, double[] keys, int time, int trans) {
		super(start, started, loop, keys, time, trans);
	}
	
	// preset Animation generators (temp)

	public static SpriteAnimation generateShake(int shakeAmount, int threshold, boolean start) {
		double[] randFrames = new double[shakeAmount];
		for (int i = 0; i < shakeAmount - 1; i++) {
			double shakeRange = shakeAmount / (i + 1) * 0.5;
			if (shakeRange < threshold) {
				shakeRange = 0;
			}
			double shake = Utility.randNumSigned(0, shakeRange);
			randFrames[i] = shake;
		}
		randFrames[shakeAmount - 1] = 0;
		return new SpriteAnimation(0, start, false, randFrames, 32, Animation.HOLD);
	}
	
}
