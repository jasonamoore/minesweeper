package minesweeper.util.sprite;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import minesweeper.util.anim.SpriteAnimation;

public class Sprite {

	private BufferedImage img;
	
	private double offX, offY;
	private double width, height;

	public Sprite(BufferedImage img, double offX, double offY, double width, double height) {
		this.img = img;
		this.offX = offX;
		this.offY = offY;
		this.width = width;
		this.height = height;
	}	

	public void render(Graphics2D g, double x, double y) {
		int dx = (int) Math.round(x + offX);
		int dy = (int) Math.round(y + offY);
		int dw = (int) Math.round(width);
		int dh = (int) Math.round(height);
		g.drawImage(img, dx, dy, dw, dh, null);
	}
	
	public void render(Graphics2D g, double x, double y, double w, double h) {
		int dx = (int) Math.round(x + (offX * w / width));
		int dy = (int) Math.round(y + (offY * h / height));
		int dw = (int) Math.round(w);
		int dh = (int) Math.round(h);
		g.drawImage(img, dx, dy, dw, dh, null);
	}
	
	public void render(Graphics2D g, double x, double y, SpriteEffect ef) {
		render(g, x, y, width, height, ef);
	}
	/*
	public void render(Graphics2D g, double x, double y, double w, double h, SpriteEffect ef) {
		double rw = w * ef.scale[0];
		double rh = h * ef.scale[1];
		double rx = x + ef.anchor[0] - (ef.anchor[0] / w) * rw;
		double ry = y + ef.anchor[1] - (ef.anchor[1] / h) * rh;

		int dx = (int) Math.round(rx + (offX * rw / width));
		int dy = (int) Math.round(ry + (offY * rh / height));
		int dw = (int) Math.round(rw);
		int dh = (int) Math.round(rh);
		
		AffineTransform restore = g.getTransform();
		AffineTransform trans = new AffineTransform();
		trans.rotate(Math.toRadians(ef.rot), dx + dw / 2, dy + dh / 2);
		g.setTransform(trans);
		
		if (ef.opacity != 1.0) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) ef.opacity));

		g.drawImage(img, dx, dy, dw, dh, null);
	
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.setTransform(restore);
	}*/
	
	public void render(Graphics2D g, double x, double y, double w, double h, SpriteEffect ef) {		
		AffineTransform restore = g.getTransform();
		
		AffineTransform tsfm = new AffineTransform();
		tsfm.translate(x + offX + ef.trans[0], y + offY + ef.trans[1]);

		double curw = w;
		double curh = h;
		if (ef.anim) {
			for (int i = 0; i < ef.anims.length; i++) {
				double val = ef.anims[i].getValue();
				int efid = ef.anims[i].getEffect();
				int axis = ef.anims[i].getAxis();
				boolean axX = axis == SpriteAnimation.X || axis == SpriteAnimation.BOTH;
				boolean axY = axis == SpriteAnimation.Y || axis == SpriteAnimation.BOTH;
				double[] anch = ef.anims[i].getAnchor();
				
				if (efid == SpriteAnimation.TRANSLATE) {
					tsfm.translate(axX ? val : 0, axY ? val : 0);
				}
				else if (efid == SpriteAnimation.SCALE) {
					tsfm.translate(curw * anch[0], curh * anch[1]);
					tsfm.scale(axX ? val : 1, axY ? val : 1);
					tsfm.translate(curw * -anch[0], curh * -anch[1]);

					curw *= axX ? val : 1;
					curh *= axY ? val : 1;
				}
				else if (efid == SpriteAnimation.ROTATE) {
					tsfm.translate(curw * anch[0], curh * anch[1]);
					tsfm.rotate(axX ? Math.toRadians(val) : 0, axY ? Math.toRadians(val) : 0);
					tsfm.translate(curw * -anch[0], curh * -anch[1]);
				}
				else if (efid == SpriteAnimation.SHEAR) {
					tsfm.shear(axX ? val : 0, axY ? val : 0);
				}
			}
		}
		
		tsfm.translate(curw * ef.scaleAnchor[0], curh * ef.scaleAnchor[1]);
		tsfm.scale(ef.scale[0], ef.scale[1]);
		tsfm.translate(curw * -ef.scaleAnchor[0], curh * -ef.scaleAnchor[1]);

		tsfm.translate(curw * ef.rotAnchor[0], curh * ef.rotAnchor[1]);
		tsfm.rotate(Math.toRadians(ef.rot));
		tsfm.translate(curw * -ef.rotAnchor[0], curh * -ef.rotAnchor[1]);
		
		g.setTransform(tsfm);
		
		if (ef.opacity != 1.0) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) ef.opacity));

		g.drawImage(img, 0, 0, (int) w, (int) h, null);
	
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g.setTransform(restore);
	}
	
	public double getW() {
		return width;
	}
	public double getH() {
		return height;
	}
	
}

