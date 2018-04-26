package lego.ohjaus;

import lego.pelilogiikka.Peli2_1;
import lego.pelilogiikka.Valmistumispeli;
import lego.pelilogiikka.Vapaaajo;
import lego.pelilogiikka.Varipeli;
import lejos.hardware.Button;

/**
 * <h1>Puzzlebot</h1> Suorittaa pelien valinnan ja käynnistämisen.
 * 
 * @author 0x03
 *
 */
public class Puzzlebot {

	/**
	 * Päämetodi, joka suorittaa laitteen logiikaan ajamisen.
	 */
	public void pelataan() {

		boolean pelataanko = true; // pelataan niin kauan kun true
		int key; // painettu näppäin
		Kayttoliittyma ui = new Kayttoliittyma(); // luodaan käyttöliittymäluokka

		while (pelataanko) {

			key = ui.peli(); // valitaan peli
			if (key == Button.ID_ESCAPE) { // lopetetaan pelaaminen
				break;
			} else if (key == Button.ID_UP) { // väripeli
				Varipeli vari = new Varipeli(); // luodaan väripeliluokka
				if (vari.pelataan() == false) {
					break;
				}
			} else if (key == Button.ID_LEFT) { // peli 2
				Valmistumispeli valmistumispeli = new Valmistumispeli();
				valmistumispeli.pelataanPelia();
			} else if (key == Button.ID_RIGHT) { // peli 3
				Vapaaajo vapaaajo = new Vapaaajo();
				vapaaajo.ajellaan();
			} else if (key == Button.ID_DOWN) { // peli 4
				Peli2_1 randomness = new Peli2_1();
				boolean check = false;
				do {
					check = randomness.letsPlay();
				} while (check);
				break;
			}
		}

	}

}