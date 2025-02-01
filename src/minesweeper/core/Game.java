package minesweeper.core;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.VolatileImage;

import javax.swing.JFrame;

import minesweeper.field.Field;
import minesweeper.resource.ImageBank;
import minesweeper.resource.SoundBank;
import minesweeper.util.Input;
import minesweeper.util.Utility;

public class Game {

	private static final int TPS = 60;
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	private static final int SCALE = 20;
	
	public static JFrame frame;
	public static Input input;
	
	public static Field field;
	
	private static boolean dragging;
	private static int dragsX;
	private static int dragsY;
	
	private static boolean debug = false;
	
	public static void main(String args[]) {
		input = new Input();
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.addKeyListener(input);
		frame.addMouseListener(input);
		frame.addMouseMotionListener(input);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		field = new Field(WIDTH / SCALE, HEIGHT / SCALE, SCALE, 200);
		
		ImageBank.loadResources();
		SoundBank.loadResources();
		
		Thread loop = new Thread("loop") {
			public void run() {
				loop();
			}
		};
		loop.start();
	}
	
	public static void loop() {
		final double tickDelay = 1_000_000_000.0 / TPS;
		final int SLEEP = 3;
		// game loop stuff
		long lastTime = System.nanoTime();
		double queuedTicks = 0;
		VolatileImage canvas = frame.createVolatileImage(WIDTH, HEIGHT);
		while (true) {
			// calculate unprocessed ticks
			long now = System.nanoTime();
			queuedTicks += (now - lastTime) / tickDelay;
			lastTime = now;
			// do ticks
			while (queuedTicks > 0) {
				// handle window events
				if (Input.checkMouse(MouseEvent.BUTTON2)) {
					if (!dragging) {
						dragging = true;
						dragsX = Input.getMouseX();
						dragsY = Input.getMouseY();
					}
				} else dragging = false;
				if (dragging) {
					java.awt.Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					double x = Math.min(d.getWidth() - WIDTH, Math.max(0, Input.getAbsoluteMouseX() - dragsX));
					double y = Math.min(d.getHeight() - HEIGHT, Math.max(0, Input.getAbsoluteMouseY() - dragsY));
					frame.setLocation((int) x, (int) y);
				}
				// debug mode
				if (Input.checkKey(KeyEvent.VK_D) && Input.checkKey(KeyEvent.VK_1)) {
					Input.forceKeyUp(KeyEvent.VK_D);
					Input.forceKeyUp(KeyEvent.VK_1);
					debug = !debug;
				}
				
				// do field tick
				field.tick();
				
				// Input updates
				Input.eatPresses();
				Input.eatReleases();
				
				// ~DEBUG
				Utility.doTickDebug(now, lastTime);
				//
				queuedTicks -= 1;
				//lastTick = now;
			}
			
            // sleep to save CPU for next cycle
            try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            
            // get graphics context
			Graphics2D g = canvas.createGraphics();
			
			// render field
			field.render(g);
			
			// render debug stats
			if (debug) Utility.drawDebug(g);

			// get new graphics context
			g.dispose();
			g = (Graphics2D) frame.getGraphics();
			
			//java.awt.geom.AffineTransform af = new java.awt.geom.AffineTransform();
			//af.shear(-0.3, 0);
			//g.setTransform(af);
			
			int dw = (int) Math.round(WIDTH * field.scale.getValue());
			int dh = (int) Math.round(HEIGHT * field.scale.getValue());
			int dx = (int) Math.round(field.shakeX.getValue()) - (dw - WIDTH) / 2;
			int dy = (int) Math.round(field.shakeY.getValue()) - (dh - HEIGHT) / 2;
			g.drawImage(canvas, dx, dy, dw, dh, null);
			
			g.dispose();
			
			// ~DEBUG
			Utility.doRenderDebug(now, lastTime);
		}
	}
	
}
