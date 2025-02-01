package minesweeper.util.sprite;

import minesweeper.util.anim.SpriteAnimation;

public class SpriteEffect {

	public double opacity = defOpacity();
	public double[] trans = defTrans();
	public double[] scale = defScale();
	public double rot = defRot();
	public double[] shear = defShear();
	
	public double[] scaleAnchor = defScaleAnchor();
	public double[] rotAnchor = defRotAnchor();
	
	public boolean anim = false;
	public SpriteAnimation[] anims;
	
	public SpriteEffect() {
	}
	
	public SpriteEffect(double opacity, double[] trans, double[] scale, double[] scaleAnchor, double rot, double[] rotAnchor, double[] shear) {
		this.trans = trans;
		this.rot = rot;
		this.opacity = opacity;
		this.scale = scale;
		this.shear = shear;
		this.scaleAnchor = scaleAnchor;
		this.rotAnchor = rotAnchor;
	}
	
	public SpriteEffect(SpriteAnimation[] anims) {
		this.anims = anims;
		anim = true;
	}

	public SpriteEffect(SpriteAnimation anims) {
		this.anims = new SpriteAnimation[] {anims};
		anim = true;
	}

	public SpriteEffect opacity(double opacity) {
		this.opacity = opacity;
		return this;
	}
	
	public SpriteEffect translate(double[] trans) {
		this.trans = trans;
		return this;
	}
	
	public SpriteEffect scale(double scaleX, double scaleY, double anchorX, double anchorY) {
		this.scale = new double[] {scaleX, scaleY};
		this.scaleAnchor = new double[] {anchorX, anchorY};
		return this;
	}
	
	public SpriteEffect scale(double scale, double anchorX, double anchorY) {
		this.scale = new double[] {scale, scale};
		this.scaleAnchor = new double[] {anchorX, anchorY};
		return this;
	}
	
	public SpriteEffect rotate(double rot, double anchorX, double anchorY) {
		this.rot = rot;
		this.rotAnchor = new double[] {anchorX, anchorY};
		return this;
	}

	public SpriteEffect shear(double shearX, double shearY) {
		this.shear = new double[] {shearX, shearY};
		return this;
	}	
	
	private static double[] defTrans() {
		return new double[] {0, 0};
	}
	private static double defRot() {
		return 0;
	}
	private static double defOpacity() {
		return 1;
	}
	private static double[] defScale() {
		return new double[] {1, 1};
	}
	private static double[] defShear() {
		return new double[] {0, 0};
	}

	private static double[] defScaleAnchor() {
		return new double[] {0, 0};
	}
	private static double[] defRotAnchor() {
		return new double[] {0, 0};
	}
	
}