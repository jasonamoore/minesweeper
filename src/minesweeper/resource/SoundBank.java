package minesweeper.resource;

import java.io.BufferedInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundBank {

public static class Sound {
		
		int beltCap = 1;
		Clip[] belt;
		boolean playing = false;
		
		public Sound(String path, int bc) {
			beltCap = bc;
			belt = new Clip[beltCap];
			for (int i = 0; i < beltCap; i++) {
				belt[i] = load(path);
			}
		}

		public void play() {
			Clip avail = null;
			for (int i = 0; i < beltCap; i++) {
				Clip c = belt[i];
				if (c.getLongFramePosition() == c.getFrameLength() || !c.isRunning()) {
					c.setFramePosition(0);
				}
				if (c.getLongFramePosition() == 0) {
					avail = c;
					break;
				}
			}
			if (avail != null) avail.start();
		}
		
	}

	public static Sound flag;
	public static Sound mine;
	public static Sound win;
	public static Sound[] dig;
	public static Sound go;
	public static Sound denied;
	
	public static Clip load(String src) {
		Clip audio = null;
		try {
			audio = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(ImageBank.class.getClassLoader().getResourceAsStream(src)));
			audio.open(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return audio;
	}
	
	public static void loadResources() {
		flag = new Sound("flag.wav", 2);
		mine = new Sound("mine.wav", 1);
		win = new Sound("win.wav", 1);
		dig = new Sound[]{new Sound("dig1.wav", 2), new Sound("dig2.wav", 2), new Sound("dig3.wav", 2), new Sound("dig4.wav", 2)};
		go = new Sound("go.wav", 1);
		denied = new Sound("denied.wav", 1);
		
	}

}