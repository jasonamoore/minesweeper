package minesweeper.field;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import minesweeper.field.entity.Worm;
import minesweeper.field.particle.Particle;
import minesweeper.field.tile.Tile;
import minesweeper.resource.ImageBank;
import minesweeper.resource.SoundBank;
import minesweeper.util.Input;
import minesweeper.util.Utility;
import minesweeper.util.anim.Animation;
import minesweeper.util.anim.SpriteAnimation;
import minesweeper.util.sprite.Sprite;

public class Field {

	public static final int LOSE = -1;
	public static final int UNSTARTED = 0;
	public static final int ACTIVE = 1;
	public static final int WIN = 2;

	public int state = UNSTARTED;
	
	public Tile[][] tiles;
	
	public int numTill = 0;
	public int numBombs;

	public int width;
	public int height;
	public int gridWidth;
	public int gridHeight;

	private double tileScale = 10;
	private double pixelScale = tileScale / 10;

	public Animation shakeX = new Animation();
	public Animation shakeY = new Animation();
	public Animation scale = new Animation(0, false, false, new double[] {1, 1.08, 1.1, 1.08, 1}, 2, Animation.LINEAR);
	
	public ArrayList<Particle> particles = new ArrayList<Particle>();
	public Worm worm = new Worm();

	public int maxLives = 1;
	public int lives = maxLives;
	
	public Field(int gwidth, int gheight, int scale, int bombs) {
		width = gwidth * scale;
		height = gheight * scale;
		gridWidth = gwidth;
		gridHeight = gheight;
		numBombs = Math.min(bombs, Math.max(0, gridWidth * gridHeight - 9));
		tileScale = Math.max(scale, 1);
		pixelScale = tileScale / 10;
		tiles = new Tile[width][height];
		reset(false);
	}
	
	private void reset(boolean animated) {
		lives = maxLives;
		numTill = 0;

		worm = new Worm();
		
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				Tile oldT = tiles[i][j];
				if (animated && oldT != null && (oldT.isTilled() && oldT.getNum() != 0 || oldT.isBomb())) {
					Sprite fallSprite = oldT.isBomb() ? (state == WIN ? ImageBank.nums[11] : ImageBank.nums[10]) : oldT.getNumSprite();
					particles.add(new Particle(i * tileScale, j * tileScale, tileScale, tileScale, 0,
							Utility.randNum(-2, 2), 0, 0, 0.2, 0.98, 1, Utility.randNum(-1, 1), 0, 0.98, 100, (int) Utility.randNum(15, 65), fallSprite));
				}
				tiles[i][j] = new Tile(this, i, j);
				if (animated && oldT != null && oldT.isTilled()) tiles[i][j].fill.start();
			}
		}
	}

	private void init(int x, int y) {
		reset(false);
		for (int n = 0; n < numBombs; n++) {
			int randX = (int) Math.floor(Math.random() * gridWidth);
			int randY = (int) Math.floor(Math.random() * gridHeight);
			while (isInSurroundingNine(x, y, randX, randY) || tiles[randX][randY].isBomb()) {
				randX = (int) Math.floor(Math.random() * gridWidth);
				randY = (int) Math.floor(Math.random() * gridHeight);
			}
			tiles[randX][randY].setBomb();
		}
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				int count = 0;
				for (int si = -1; si <= 1; si++) {
					for (int sj = -1; sj <= 1; sj++) {
						if ((si != 0 || sj != 0) && isValidGrid(i + si, j + sj) && tiles[i + si][j + sj].isBomb())
							count++;
					}
				}
				tiles[i][j].setNum(count);
			}
		}
		state = ACTIVE;
	}
	
	private void lose() {
		state = LOSE;
		
		SoundBank.mine.play();

		shakeX = SpriteAnimation.generateShake(50, 3, true);
		shakeY = SpriteAnimation.generateShake(50, 3, true);
		
		double nump = Utility.randNum(70, 140);
		for (int i = 0; i < nump; i++) {
			double px = i / nump * gridWidth * tileScale;
			double py = -Math.random() * gridHeight * tileScale * 2;
			particles.add(new Particle(px, py, 10, 10, 0, Utility.randNum(-1.6, 1.6), 0, 0,
					0.2, 0.98, 1, 0, 0, 0, 200, 10, ImageBank.particles[Utility.randInt(4, 7)]));
		}
	}

	private void win() {
		state = WIN;

		scale.start();

		SoundBank.win.play();
	}

	private void forceWin() {
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				if (!tiles[i][j].isBomb()) doTill(i, j, true, true);
			}
		}
	}
	
	private void doTill(int x, int y, boolean allowProp, boolean muted) {
		boolean tillSuccess = tiles[x][y].till(muted);
		if (!tillSuccess) return;
		
		if (tiles[x][y].getNum() == 0 && allowProp) propogateTill(x, y);
		
		if (!tiles[x][y].isBomb()) numTill++;
		if (numTill >= gridWidth * gridHeight - numBombs) win();
	}
	
	private void propogateTill(int sx, int sy) {
		int count = 1;
		ArrayList<Tile> propList = new ArrayList<Tile>();
		ArrayList<Tile> history = new ArrayList<Tile>(); // only used for graphical effects
		propList.add(tiles[sx][sy]);
		history.add(tiles[sx][sy]);
		while (propList.size() > 0) {
			Tile current = propList.get(0);
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (!isValidGrid(current.getX() + i, current.getY() + j)) continue;
					Tile adjacent = tiles[current.getX() + i][current.getY() + j];
					if (!adjacent.isTilled()) {
						doTill(current.getX() + i, current.getY() + j, false, true);
						history.add(adjacent);
						if (adjacent.getNum() == 0 && !adjacent.isFlagged()) {
							propList.add(adjacent);
							count++;
						}
					}
				}
			}
			propList.remove(0);
		}
		if (count >= 5) {
			Tile randTilled = history.get((int) Utility.randNum(0, history.size()));
			if (!worm.isCreated() && Math.random() >= 0) generateWorm(randTilled.getX(), randTilled.getY());
		}
	}
	
	private void generateWorm(int x, int y) {
		double wx = x * tileScale + Math.random() * tileScale;
		double wy = y * tileScale + tileScale / 2;
		double wormHeight = tileScale / 2;
		double initXv = Utility.randNum(1, 2.5) * (wx > width / 2 ? -1 : 1);
		double initYv = Utility.randNum(-4, -6);
		
		double numTicksBeforeLand = 0;
		double testY = wy - wormHeight;
		double testYv = initYv;
		do {
			testYv += Worm.GRAVITY;
			testY += testYv;
			numTicksBeforeLand++;
		} while (testY + wormHeight < wy);
		double srotv = (int) Utility.randNum(1, 3) * 360.0 / numTicksBeforeLand;
		
		worm = new Worm(this, wy, wx, wy - wormHeight, initXv, initYv, 0, srotv);
	}
	
	private boolean isInSurroundingNine(int x, int y, int randX, int randY) {
		if (randX >= x - 1 && randX <= x + 1 && randY >= y - 1 && randY <= y + 1) return true;
		else return false;
	}

	public boolean isValidGrid(int i, int j) {
		if (i < 0) return false;
		if (j < 0) return false;
		if (i >= gridWidth) return false;
		if (j >= gridHeight) return false;
		return true;
	}
	
	public double pxScale() {
		return pixelScale;
	}
	
	public double tileScale() {
		return tileScale;
	}
	
	public void tick() {
		//mouse grid coordinates
		int mgx = (int) Math.floor((Input.getMouseX() / tileScale));
		int mgy = (int) Math.floor((Input.getMouseY() / tileScale));
		
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			particles.get(i).tick();
			if (p.isDead()) {
				particles.remove(p);
				continue;
			}
		}
		
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				tiles[i][j].tick();
			}
		}
		
		worm.tick();
		
		if (state == WIN) {
			if (Input.checkMouseUp(MouseEvent.BUTTON1)) {
				reset(true);
				state = UNSTARTED;
				SoundBank.go.play();
			}
			return;
		}
		else if (state == LOSE) {
			if (Input.checkMouseUp(MouseEvent.BUTTON1)) {
				reset(true);
				state = UNSTARTED;
				SoundBank.go.play();
			}
			return;
		}
		else if (state == UNSTARTED) {
			if (Input.checkMouseUp(MouseEvent.BUTTON1)) {
				if (isValidGrid(mgx, mgy)) {
					init(mgx, mgy);
					doTill(mgx, mgy, true, false);
				}
			}
		}
		else if (state == ACTIVE) {
			if (Input.checkMouseUp(MouseEvent.BUTTON1)) {
				if (isValidGrid(mgx, mgy) && !this.worm.checkHover()) {
					if (tiles[mgx][mgy].isBomb() && !tiles[mgx][mgy].isFlagged() && !tiles[mgx][mgy].isTilled()) {
					lives--;
					if (lives <= 0) {
						lose();
						return;
						} 
					} 
					doTill(mgx, mgy, !tiles[mgx][mgy].isBomb(), false);
				}
			}
			else if (Input.checkMouseUp(MouseEvent.BUTTON3)) {
				if (isValidGrid(mgx, mgy))
					this.tiles[mgx][mgy].flag(); 
			}
			if (Input.checkKey(KeyEvent.VK_0)) {
				Input.forceKeyUp(KeyEvent.VK_0);
				forceWin();
			}
		}
	}

	public void render(Graphics2D g) {
		ArrayList<Tile> lasts = new ArrayList<Tile>();
		for (int i = 0; i < gridWidth; i++) {
			for (int j = 0; j < gridHeight; j++) {
				if (tiles[i][j].printOnTop) {
					lasts.add(tiles[i][j]);
					continue;
				}
				else tiles[i][j].render(g);
			}
		}
		for (int i = 0; i < lasts.size(); i++) {
			lasts.get(i).render(g);
		}

		worm.render(g);
		
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i) != null) particles.get(i).render(g);
		}
	}
	
}
