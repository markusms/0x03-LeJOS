package lego.ohjaus;

import java.io.File;

import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sounds;

/**
 * <h1>Sound</h1> luokka toistaa äänen ja implementoi Sounds rajapinnan.
 * 
 * @author 0x03
 *
 */
public class Sound implements Sounds {

	private Audio audio;

	/**
	 * Konstruktori alustaa oliomuuttuja (Audio) audion, että voidaan toistaa ääniä.
	 */
	public Sound() {
		audio = BrickFinder.getDefault().getAudio();
	}

	/**
	 * Toistaa äänen BEEP
	 */
	public void beep() {
		audio.systemSound(BEEP);
	}

	/**
	 * Toistaa musiikkitiedoston.
	 */
	public void playVoitto() {
		File file = new File("You_WIN_TRON.wav");
		audio.playSample(file, 100); // file, äänenvoimakkuus
	}

}
