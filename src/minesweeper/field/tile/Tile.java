package minesweeper.field.tile;

import java.awt.Graphics2D;

import minesweeper.field.Field;
import minesweeper.field.particle.Particle;
import minesweeper.resource.ImageBank;
import minesweeper.resource.SoundBank;
import minesweeper.util.Input;
import minesweeper.util.Utility;
import minesweeper.util.anim.Animation;
import minesweeper.util.anim.SpriteAnimation;
import minesweeper.util.sprite.Sprite;
import minesweeper.util.sprite.SpriteEffect;

public class Tile {

	private Field parent;
	
	private boolean tilled = false;
	private boolean flagged = false;
	
	private boolean bomb = false;
	
	private int num;

	private int gx, gy;
	
	private double x, y;
	private double w, h;
	
	private SpriteAnimation shakeX = new SpriteAnimation();
	private SpriteAnimation shakeY = new SpriteAnimation();
	private SpriteAnimation scale = new SpriteAnimation(0, false, false, new double[] {1, 1.35, 1.5, 1.35, 1}, 32, Animation.LINEAR).set(SpriteAnimation.SCALE, SpriteAnimation.BOTH, 0.5, 0.5);
	public Animation fill = new Animation(0, false, false, new double[] {1, 2, 3, 4, 0}, Utility.randInt(128, 256), Animation.HOLD);

	private boolean hovered = false;
	
	public boolean printOnTop = false;
	// z index?
	
	public Tile(Field par, int tx, int ty) {
		parent = par;
		gx = tx;
		gy = ty;
		w = par.tileScale();
		h = par.tileScale();
		x = tx * w;
		y = ty * h;
	}
	
	public boolean checkHover() {
		if (Input.getMouseX() >= x && Input.getMouseX() < x + w &&
			Input.getMouseY() >= y && Input.getMouseY() < y + h) {
			return true;
		}
		return false;
	}
	
	public boolean till(boolean muted) {
		if (tilled) return false;
		if (flagged) {
			SoundBank.denied.play();
			shakeX = SpriteAnimation.generateShake((int) (w / 5), 0, true).set(SpriteAnimation.TRANSLATE, SpriteAnimation.X);
			shakeY = SpriteAnimation.generateShake((int) (h / 5), 0, true).set(SpriteAnimation.TRANSLATE, SpriteAnimation.Y);
			printOnTop = true;
			return false;
		}
		tilled = true;
		
		if (!muted) SoundBank.dig[Utility.randInt(0, SoundBank.dig.length - 1)].play();
		
		int nump = !muted ? (int) Utility.randNum(1, 4) : (int) Utility.randNum(0, 2);
		for (int i = 0; i < nump; i++) {
			parent.particles.add(new Particle(Utility.randNum(x, x + w), Utility.randNum(y, y + h), w / 5, h / 5, 0,
					Utility.randNum(-1.6, 1.6), -2, 0, 0.2, 0.98, 1, 0, 0, 0, 100, Utility.randInt(0, 50), 
					ImageBank.particles[Utility.randInt(0, 3)]));
		}
		return true;
	}
	
	public void flag() {
		if (tilled) return;
		
		flagged = !flagged;
		
		SoundBank.flag.play();

		if (flagged) {
			scale.start();
			printOnTop = true;
		} else {
			parent.particles.add(new Particle(x, y, w, h, 0, Utility.randNum(-2, 2), 0, 0, 0.2, 0.98, 1,
					Utility.randNum(-2, 2), 0, 0.98, 100, 20, ImageBank.nums[9]));
		}
	}

	public int getX() {
		return gx;
	}
	public int getY() {
		return gy;
	}
	
	public boolean isTilled() {
		return tilled;
	}
	
	public boolean isFlagged() {
		return flagged;
	}
	
	public boolean isBomb() {
		return bomb;
	}
	
	public void setBomb() {
		bomb = true;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}

	public Sprite getNumSprite() {
		return ImageBank.nums[num];
	}
	
	public void tick() {		
		if (checkHover() && !parent.worm.checkHover()) {
			printOnTop = true;
			hovered = true;
		}
		else
			this.hovered = false;

		if (!hovered && !scale.isPlaying() && !shakeX.isPlaying()) printOnTop = false;
	}
	
	public void render(Graphics2D g) {		
		double dx = x + shakeX.getValue();
		double dy = y + shakeY.getValue();
		SpriteEffect ef = new SpriteEffect(new SpriteAnimation[] {shakeX, shakeY, scale});
		if (!tilled) {
			if (fill.isPlaying()) {
				ImageBank.tiles[(int) fill.getValue()].render(g, dx, dy, w, h, ef);
			} else {
				ImageBank.tiles[0].render(g, dx, dy, w, h, ef);
			}
			if (parent.state == Field.LOSE && bomb) {
				ImageBank.nums[10].render(g, dx, dy, w, h, ef);
			} else if (parent.state == Field.WIN && bomb) {
				ImageBank.nums[11].render(g, dx, dy, w, h, ef);
			} else if (flagged) {
				ImageBank.nums[9].render(g, dx, dy, w, h, ef);
			}
		} else {
			ImageBank.tiles[1].render(g, dx, dy, w, h, ef);
			if (bomb) ImageBank.nums[10].render(g, dx, dy, w, h, ef);
			else if (num != 0) getNumSprite().render(g, dx, dy, w, h);
		}
		
		if (hovered && !tilled && (parent.state == Field.UNSTARTED || parent.state == Field.ACTIVE)) {
			Sprite hs = ImageBank.tileBorder[flagged ? 1 : 0];
			hs.render(g, dx, dy, ef);
		}
	}
	
}
