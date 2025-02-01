package minesweeper.field.particle;

import java.awt.Graphics2D;

import minesweeper.util.anim.Animation;
import minesweeper.util.sprite.Sprite;
import minesweeper.util.sprite.SpriteEffect;

public class Particle {

	private double x, y;
	private double w, h;
	private double xv, yv;
	private double rot;
	private double rotv;
	
	private double cx, cy;
	private double fx, fy;
	private double crot, frot;
	
	private int age = 0;
	private int lifetime = 0;
	private int fade = 0;
	private boolean dead;
	
	private Sprite[] sprite;
	private Animation spriteAnim = new Animation(0);

	public final int ANIM_FIXED = 0;
	public final int ANIM_RELATIVE = 1;
	public final int PHYSICAL = 2;
	
	private int type;
	
	private Animation animX = new Animation();
	private Animation animY = new Animation();
	
	public Particle(double x, double y, double w, double h, double rot,
					double initXv, double initYv, double constx, double consty, double factx, double facty,
					double initRotv, double constRot, double factRot,
					int lifetime, int fade,
					Sprite[] sprite, Animation spriteAnim) {
		type = PHYSICAL;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.rot = rot;
		xv = initXv;
		yv = initYv;
		cx = constx;
		cy = consty;
		fx = factx;
		fy = facty;
		rotv = initRotv;
		crot = constRot;
		frot = factRot;
		this.lifetime = lifetime;
		this.fade = fade;
		this.sprite = sprite;
		this.spriteAnim = spriteAnim;
	}

	public Particle(double x, double y, double w, double h, double rot,
					double initXv, double initYv, double constx, double consty, double factx, double facty,
					double initRotv, double constRot, double factRot,
					int lifetime, int fade,
					Sprite sprite) {
		type = PHYSICAL;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.rot = rot;
		xv = initXv;
		yv = initYv;
		cx = constx;
		cy = consty;
		fx = factx;
		fy = facty;
		rotv = initRotv;
		crot = constRot;
		frot = factRot;
		this.lifetime = lifetime;
		this.fade = fade;
		this.sprite = new Sprite[] {sprite};
	}

	public Particle(double x, double y, Animation animX, Animation animY, int lifetime, int fade, Sprite[] sprite, Animation spriteAnim) {
		type = ANIM_RELATIVE;
		this.animX = animX;
		this.animY = animY;
		this.lifetime = lifetime;
		this.fade = fade;
		this.sprite = sprite;
		this.spriteAnim = sprite == null ? new Animation() : spriteAnim;
	}
	
	public Particle(Animation animX, Animation animY, int lifetime, int fade, Sprite[] sprite, Animation spriteAnim) {
		type = ANIM_FIXED;
		this.animX = animX;
		this.animY = animY;
		this.lifetime = lifetime;
		this.fade = fade;
		this.sprite = sprite;
		this.spriteAnim = spriteAnim == null ? new Animation() : spriteAnim;
	}

	private void die() {
		if (dead) return;
		dead = true;
	}
	
	public void tick() {
		age++;
		if (age >= lifetime) {
			die();
			return;
		}
		
		if (spriteAnim.isPlaying()) {
			//
		}
		
		if (type == ANIM_FIXED) {
			//
		}
		if (type == PHYSICAL) {
			xv += cx;
			yv += cy;
			xv *= fx;
			yv *= fy;
			rotv += crot;
			rotv *= frot;
			
			x += xv;
			y += yv;
			rot += rotv;
		}
	}
	
	public void render(Graphics2D g) {
		if (dead) return;
		
		float opacity = age >= lifetime - fade ? (float) (lifetime - age) / (float) fade : 1;	
		
		sprite[(int) spriteAnim.getValue()].render(g, x, y, w, h, new SpriteEffect().rotate(rot, 0.5, 0.5).opacity(opacity));
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getW() {
		return w;
	}
	
	public double getH() {
		return h;
	}
	
	public boolean isDead() {
		return dead;
	}
	
}
