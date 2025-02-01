package minesweeper.field.entity;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import minesweeper.field.Field;
import minesweeper.field.particle.Particle;
import minesweeper.resource.ImageBank;
import minesweeper.util.Input;
import minesweeper.util.Timer;
import minesweeper.util.Utility;
import minesweeper.util.anim.Animation;
import minesweeper.util.sprite.Sprite;
import minesweeper.util.sprite.SpriteEffect;

public class Worm {

	public static final int LEFT = -1;
	public static final int RIGHT = 1;
	
	public static final double GRAVITY = 0.18;

	public static final int UNCREATED = -1;
	public static final int AIRBORNE = 0;
	public static final int CRAWLING = 1;
	public static final int DIGGING = 2;
	public static final int HIDDEN = 3;

	private Field par;
	
	private int state;
	
	private double groundY;

	private double x, y;
	private double w, h;
	private double xv, yv;
	
	private int dir;
	
	private double rot;
	private double rotv;
	
	private double crawlSpeed = 1.5;

	private Animation crawl = new Animation(0, false, true, new double[]{0, 0, 0, 1, 2, 3, 3, 3, 2, 1}, 64, Animation.HOLD);
	private Animation dig = new Animation(0, false, false, new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 48, Animation.HOLD);
	
	private boolean hovered;
	
	public Worm() {
		state = UNCREATED;
	}
	
	public Worm(Field par, double base, double sx, double sy, double sxv, double syv, double srot, double srotv) {
		this.par = par;
		state = AIRBORNE;
		groundY = base;
		x = sx;
		y = sy;
		w = par.tileScale();
		h = par.tileScale() / 2;
		xv = sxv;
		yv = syv;
		rot = srot;
		rotv = srotv;
		dir = (int) Math.signum(sxv);
	}
	
	
	private boolean crawling = false;
	private Timer crawlTimer = new Timer(false);
	private int crawlTime = 192;
	private boolean tryDig = false;
	
	public void tick() {
		if (state == UNCREATED) return;
		
		hovered = (checkHover() && state == CRAWLING);

		if (state == AIRBORNE) {
			rot += rotv;
			
			yv += GRAVITY;
			
			x += xv;
			y += yv;
			
			if (y + h >= groundY) {
				state = CRAWLING;
				y = groundY - h;
				rot = 0;
				crawl.start();
				crawlTimer.reset(true);
			}
		}
		else if (state == CRAWLING) {
			if (crawlTimer.elapsed() >= crawlTime) {
				crawling = !crawling;
				if (crawling) crawlTime = 128;
				else crawlTime = 192;
				crawlTimer.reset(true);
			}
			
			xv = crawling ? crawlSpeed * dir : 0;
			x += xv;
			if (x > par.width * par.tileScale() || x + w < 0) {
				par.worm = new Worm();
				return;
			}
			
			if (hovered && Input.checkMouse(MouseEvent.BUTTON1)) {
				tryDig = true;
				if (!crawling && crawl.getValue() != 0) {
					crawling = true;
					crawlTimer.reset(true);
				}
			}
			if (tryDig && crawl.getValue() == 0) {
				state = DIGGING;
				dig.start();
			}
		}
		else if (state == DIGGING) {
			if (dig.getLength() - dig.getIndex() <= 5) {
				int nump = Utility.randInt(0, 1);
				for (int i = 0; i < nump; i++) {
					par.particles.add(new Particle(x + w / 2, y + h - par.pxScale(), par.pxScale() * 2, par.pxScale() * 2, 0, Utility.randNum(-1.2, 1.2), -2, 0,
							0.2, 0.98, 1, Utility.randNum(-2, 2), 0, 0, 20, Utility.randInt(5, 10), ImageBank.particles[Utility.randInt(8, 11)]));
				}
			} 
			if (dig.isFinished()) {
				state = HIDDEN;
			}
		}
	}

	public boolean isCreated() {
		return state != UNCREATED;
	}
	
	public boolean checkHover() {
		if (state == UNCREATED) return false;
		if (Input.getMouseX() >= x - 5 && Input.getMouseX() < x + w + 5 &&
			Input.getMouseY() >= y - 5 && Input.getMouseY() < y + h + 5) {
			return true;
		}
		return false;
	}
	
	private Sprite getSprite() {
		if (state == AIRBORNE) {
			return ImageBank.worm[8 + (dir == LEFT ? 1 : 0)];
		} else if (state == CRAWLING) {
			return ImageBank.worm[(int) crawl.getValue() + (dir == LEFT ? 4 : 0)];
		}
		else if (state == DIGGING) {
			return ImageBank.wormDig[(int) dig.getValue() + (dir == LEFT ? 11 : 0)];
		}
		return null;
	}

	public void render(Graphics2D g) {
		if (state == UNCREATED || state == HIDDEN) return;
		
		SpriteEffect ef = new SpriteEffect().rotate(rot, 0.5, 0.5);
		Sprite sprite = getSprite();
		sprite.render(g, x, y, ef);
		if (hovered) {
			Sprite hov = ImageBank.wormBorder[(int) crawl.getValue() + (dir == LEFT ? 4 : 0)];
			hov.render(g, x, y, ef);
		}
	}
	
}
