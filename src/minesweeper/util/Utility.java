package minesweeper.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import minesweeper.core.Game;

public class Utility {

	//private static int tickTime = 0;
	
	public static int randInt(double min, double max) {
		return (int) Math.floor(min + Math.random() * (max - min + 1));
	}
	public static double randNum(double min, double max) {
		return min + Math.random() * (max - min);
	}
	public static double randNumSigned(double min, double max) {
		return randNum(min, max) * randSign();
	}
	public static double randSign() {
		return Math.random() > 0.5 ? -1 : 1;
	}
	
	// ~~DEBUG STUFF~~
	private static long[] usedMemory = new long[Game.WIDTH];
	private static long maxUsedMem = 0;
	private static long runtimeMem = Runtime.getRuntime().maxMemory();
	private static double lastGCTime = System.currentTimeMillis();
	//
	private static double[] tickTimes = new double[Game.WIDTH];
	private static double lastTPS;
	private static double lowestTPS = Double.POSITIVE_INFINITY;
	private static int lastTickDur;
	//
	private static double[] renderTimes = new double[Game.WIDTH];
	private static double lastFPS;
	private static double lowestFPS = Double.POSITIVE_INFINITY;
	private static int lastRendDur;
	//
	static long appStart = System.currentTimeMillis();
	
	public static void doTickDebug(long nowTime, long lastTime) {
		for (int i = 1; i < Game.WIDTH; i++) {
			tickTimes[i - 1] = tickTimes[i];
			usedMemory[i - 1] = usedMemory[i];
		}
		tickTimes[tickTimes.length - 1] = (nowTime - lastTime) / 1_000_000;
		usedMemory[usedMemory.length - 1] = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		maxUsedMem = Math.max(maxUsedMem, usedMemory[usedMemory.length - 1]);
		if (usedMemory[usedMemory.length - 1] < usedMemory[usedMemory.length - 2]) lastGCTime = System.currentTimeMillis();
		lastTPS = 1_000_000_000.0 / (nowTime - lastTime);
		lowestTPS = Math.min(lowestTPS, lastTPS);
		lastTickDur = (int) (System.nanoTime() - nowTime) / 1_000_000;
	}
	
	public static void doRenderDebug(long nowTime, long lastTime) {
		for (int i = 1; i < renderTimes.length; i++) {
			renderTimes[i - 1] = renderTimes[i];
		}
		renderTimes[renderTimes.length - 1] = (nowTime - lastTime) / 1_000_000;
		lastFPS = 1_000_000_000.0 / (nowTime - lastTime);
		lowestFPS = Math.min(lowestFPS, lastFPS);
		lastRendDur = (int) (System.nanoTime() - nowTime) / 1_000_000;
	}
	
	public static void drawDebug(Graphics2D g) {
		int graphH = Game.HEIGHT / 2;
		String xmx = String.format("%05.2f GB", runtimeMem / 1_000_000_000.0); // gigabyte conversion
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		
		g.setColor(Color.MAGENTA);
		g.drawLine(0, Game.HEIGHT / 2, Game.WIDTH, graphH);
		g.drawString("y = 1000 ms / " + xmx, 0, graphH - 1);
		
		for (int i = 0; i < Game.WIDTH; i++) {
			g.setColor(Color.GREEN);
			g.drawLine(i, Game.HEIGHT, i + 1, (int) (Game.HEIGHT - graphH * (renderTimes[i] / 1000.0)));
			g.setColor(Color.RED);
			g.drawLine(i, Game.HEIGHT, i + 1, (int) (Game.HEIGHT - graphH * (tickTimes[i] / 1000.0)));
			g.setColor(Color.BLUE);
			g.drawLine(i, Game.HEIGHT, i + 1, (int) (Game.HEIGHT - graphH * ((double) usedMemory[i] / runtimeMem)));
		}
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 500, 40);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g.setColor(Color.WHITE);
		g.drawString("TPS: " + String.format("%05.2f/s", lastTPS), 0, 30);
		g.drawString("LOWTPS: " + String.format("%02d/s", (int) lowestTPS), 100, 30);
		g.drawString("FPS: " + String.format("%05.2f/s", lastFPS), 0, 20);
		g.drawString("LOWFPS: " + String.format("%02d/s", (int) lowestTPS), 100, 20);
		g.drawString("TDUR: " + String.format("%04dms", lastTickDur), 200, 30);
		g.drawString("RDUR: " + String.format("%04dms", lastRendDur), 200, 20);
		
		double curpercent = (double) usedMemory[usedMemory.length - 1] / runtimeMem * 100;
		double maxpercent = (double) maxUsedMem / runtimeMem * 100;
		g.drawString("MAX%: " + String.format("%05.2f%%", maxpercent), 300, 30);
		g.drawString("MEM%: " + String.format("%05.2f%%", curpercent), 300, 20);
		g.drawString("TM: " + xmx, 400, 30);
		g.drawString("GC: " + String.format("%05dms", (int) (System.currentTimeMillis() - lastGCTime)), 400, 20);
	}
	
}
