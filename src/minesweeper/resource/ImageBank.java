package minesweeper.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import minesweeper.util.sprite.Sprite;

public class ImageBank {

	public static Sprite[] tiles;
	public static Sprite[] tileBorder;
	public static Sprite[] nums;
	public static Sprite[] particles;
	public static Sprite[] worm;
	public static Sprite[] wormBorder;
	public static Sprite[] wormDig;
	
	private static HashMap<String, BufferedImage> loaded = new HashMap<String, BufferedImage>();
	
	private static BufferedImage load(String src) {
		if (loaded.containsKey(src)) return loaded.get(src);
		BufferedImage image = null;
		try {
			image = ImageIO.read(ImageBank.class.getClassLoader().getResourceAsStream(src));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private static Sprite[] loadSheet(String src, int stx, int sty, int sw, int sh, int lx, int ly, int offX, int offY, double scale) {
		BufferedImage sheet = load(src);
		Sprite[] list = new Sprite[lx * ly];
		
		if (stx + lx * sw > sheet.getWidth() || sty + ly * sh > sheet.getHeight()) return null;
		
		int i = 0;
		for (int y = 0; y < ly; y++) {
			for (int x = 0; x < lx; x++) {
				list[i] = new Sprite(sheet.getSubimage(stx + x * sw, sty + y * sh, sw, sh), offX * scale, offY * scale, sw * scale, sh * scale);
				i++;
			}
		}
		return list;
	}
	
	public static void loadResources() {
		//						  source, stx, sty, sw, sh, lx, ly, offX, offY, scale
		final int scale = 2;
		tiles = 		loadSheet("tiles.png", 0, 0, 10, 10, 5, 1, 0, 0, scale);
		tileBorder = 	loadSheet("tiles.png", 0, 10, 12, 12, 3, 1, -1, -1, scale);
		nums = 			loadSheet("tiles.png", 50, 0, 10, 10, 12, 1, 0, 0, scale);
		/*
		flag =	 		loadSheet("tiles2x.png", 0, 0, 20, 20, 8, 1, 0, -1, 1);
		tiles = 		loadSheet("tiles2x.png", 0, 20, 20, 20, 5, 1, 0, 0, 1);
		tileBorder = 	loadSheet("tiles2x.png", 0, 40, 24, 24, 3, 1, -1, -1, 1);
		nums = 			loadSheet("tiles2x.png", 100, 20, 20, 20, 12, 1, 0, 0, 1);
		 */
		particles = 	loadSheet("particles.png", 0, 0, 2, 2, 8, 2, 0, 0, scale);
		worm = 			loadSheet("worm.png", 0, 0, 10, 5, 10, 1, 0, 0, scale);
		wormBorder = 	loadSheet("worm.png", 0, 5, 12, 7, 10, 1, -1, -1, scale);
		wormDig = 		loadSheet("worm.png", 0, 12, 10, 10, 11, 2, 0, -5, scale);
	}
	
}
