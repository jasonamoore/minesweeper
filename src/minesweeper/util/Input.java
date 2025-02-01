package minesweeper.util;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Input implements KeyListener, MouseListener, MouseMotionListener {

	private static int keySize = 255;
	private static int mouseSize = MouseInfo.getNumberOfButtons();
	
	private static boolean[] keys = new boolean[keySize];
	private static boolean[] keyPress = new boolean[keySize];
	private static boolean[] keyRelease = new boolean[keySize];
	private static boolean[] mouse = new boolean[mouseSize];
	private static boolean[] mousePress = new boolean[mouseSize];
	private static boolean[] mouseRelease = new boolean[mouseSize];

	private static int mouseX = 0;
	private static int mouseY = 0;
	
	private static boolean mouseHovered = false;
	
	public static boolean checkKey(int code) {
		if (code < keySize) return keys[code];
		else return false;
	}
	
	public static boolean checkMouse(int code) {
		if (code < mouseSize) return mouse[code];
		else return false;
	}
	
	public static boolean checkKeyDown(int code) {
		if (code < keySize) return keyPress[code];
		else return false;
	}
	
	public static boolean checkMouseDown(int code) {
		if (code < mouseSize) return mousePress[code];
		else return false;
	}
	
	public static boolean checkKeyUp(int code) {
		if (code < keySize) return keyRelease[code];
		else return false;
	}
	
	public static boolean checkMouseUp(int code) {
		if (code < mouseSize) return mouseRelease[code];
		else return false;
	}

	public static void forceKeyUp(int code) {
		if (code < keySize) keys[code] = false;
	}
	
	public static void forceMouseUp(int code) {
		if (code < mouseSize) mouse[code] = false;
	}
	
	public static void eatPresses() {
		keyPress = new boolean[keySize];
		mousePress = new boolean[mouseSize];
	}
	
	public static void eatReleases() {
		keyRelease = new boolean[keySize];
		mouseRelease = new boolean[mouseSize];
	}
	
	public static int getMouseX() {
		return mouseX;
	}
	public static int getMouseY() {
		return mouseY;
	}
	public static int getAbsoluteMouseX() {
		java.awt.Point p = MouseInfo.getPointerInfo().getLocation();
		return (int) p.getX();
	}
	public static int getAbsoluteMouseY() {
		java.awt.Point p = MouseInfo.getPointerInfo().getLocation();
		return (int) p.getY();
	}
	
	public static int numKeyButtons() {
		return keySize;
	}
	
	public static int numMouseButtons() {
		return mouseSize;
	}
	
	public static boolean isMouseHovering() {
		return mouseHovered;
	}
	
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code < keySize) {
			keys[code] = true;
			keyPress[code] = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code < keySize) {
			keys[code] = false;
			keyRelease[code] = true;
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int code = e.getButton();
		if (code < mouseSize) {
			mouse[code] = true;
			mousePress[code] = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int code = e.getButton();
		if (code < mouseSize) {
			mouse[code] = false;
			mouseRelease[code] = true;
		}
	}

	public void mouseEntered(MouseEvent e) {
		mouseHovered = true;
	}

	public void mouseExited(MouseEvent e) {
		mouseHovered = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		updateMousePos(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateMousePos(e);
	}

	private void updateMousePos(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
}