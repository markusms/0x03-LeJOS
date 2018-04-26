package lego.ohjaus;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * <h1>Käyttöliittymä</h1> Pelin käyttöliittymäluokka, joka näyttää aluksi
 * animaation ja sitten kysyy käyttäjältä, että minkä pelin hän haluaa aloittaa.
 * 
 * @author 0x03
 *
 */
public class Kayttoliittyma {

	private int peli; // valittu peli

	/**
	 * Luokan Kayttoliittyma konstruktori, joka alustaa peli muuttujan nollaksi.
	 */
	public Kayttoliittyma() {

		this.peli = 0;

	}

	/**
	 * Luokan "pää"-metodi, mikä jaa ohjelman läpi ja palauttaa lopuksi valitun
	 * pelin.
	 * 
	 * @return int valittu peli
	 */
	public int peli() { // pelin valinta

		this.alkuanimaatio();
		this.valitse();

		return this.peli;

	}

	/**
	 * Metodi mikä kertoo valittavat pelit ja kysyy halutun pelin käyttäjältä.
	 */
	public void valitse() {

		LCD.drawString("Valitse peli!", 0, 0);
		LCD.drawString("^ = varipeli", 0, 2);
		LCD.drawString("< = valmistutaanko", 0, 3);
		LCD.drawString("> = vapaa-ajelu", 0, 4);
		LCD.drawString("v = random", 0, 5);
		LCD.drawString("ESC = lopeta", 0, 7);

		int key = Button.waitForAnyPress();
		LCD.clear();
		this.peli = key;

	}

	/**
	 * Metodi mikä ajaa läpi pelin alun animaation.
	 */
	public void alkuanimaatio() { // x-aks(0 - 17), y-aks(0-7)

		for (int i = 0; i < 7; i++) {
			LCD.drawString("PUZZLEBOT", 0, i);
			Delay.msDelay(200);
			LCD.clear();
		}
		if (!Button.UP.isDown()) { //jos painetaan alaspäin, niin voidaan skipata animaatio
			for (int i = 0; i < 9; i++) {
				LCD.drawString("PUZZLEBOT", i, 7);
				Delay.msDelay(200);
				LCD.clear();
			}
			for (int i = 7; i >= 0; i--) {
				LCD.drawString("PUZZLEBOT", 9, i);
				Delay.msDelay(200);
				LCD.clear();
			}
			for (int i = 9; i >= 0; i--) {
				LCD.drawString("PUZZLEBOT", i, 0);
				Delay.msDelay(200);
				LCD.clear();
			}
			LCD.drawString("PUZZLE", 2, 1);
			LCD.drawString("U", 2, 2);
			LCD.drawString("Z", 2, 3);
			LCD.drawString("Z", 2, 4);
			LCD.drawString("L", 2, 5);
			LCD.drawString("E", 2, 6);
			LCD.drawString("BOT", 4, 3);
			LCD.drawString("O", 4, 4);
			LCD.drawString("T", 4, 5);
			Delay.msDelay(1500);
			LCD.clear();
		}
	}

}
