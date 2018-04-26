package lego.pelilogiikka;

import lego.io.Tulostaulukko;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * <h1>Valmistumispeli</h1>, jonka tehtävänä on antaa käyttäjälle suoritettavaksi valmistumispelin
 * pelaaminen.
 * 
 * @author 0x03
 *
 */
public class Valmistumispeli {

	private Tulostaulukko tulokset;
	
	/**
	 * Konstruktori, joka luo tulostaulukon.
	 */
	public Valmistumispeli() {
		tulokset = new Tulostaulukko("valmistumispeli");
	}
	
	/**
	 * Metodi, jossa ajetaan pelin osat läpi.
	 */
	public void pelataanPelia() {
		
		Pelialusta pelialusta = new Pelialusta();
		
		this.ohjeet();
		this.lopetus(pelialusta.peli());
		
	}
	
	/**
	 * Näyttää pelin ohjeet näytöllä.
	 */
	public void ohjeet() {

		LCD.drawString("Tervetuloa", 0, 0);
		LCD.drawString("valmistumispeliin!", 0, 1);
		Delay.msDelay(2000);
		LCD.clear();

		LCD.drawString("Tehtavasi", 0, 0);
		LCD.drawString("on ajaa", 0, 1);
		LCD.drawString("nayton kohteseen ", 0, 2);
		LCD.drawString("L annetussa", 0, 3);
		LCD.drawString("ajassa.", 0, 4);
		LCD.drawString("Sinun hahmosi", 0, 5);
		LCD.drawString("on R.", 0, 6);
		Delay.msDelay(5000);
		LCD.clear();
		
		LCD.drawString("Kun liikut", 0, 0);
		LCD.drawString("robotilla oikeassa", 0, 1);
		LCD.drawString("elamassa, liikut ", 0, 2);
		LCD.drawString("myos naytolla.", 0, 3);
		LCD.drawString("Aja kohteeseen ", 0, 4);
		LCD.drawString("L", 0, 5);
		Delay.msDelay(5000);
		LCD.clear();

		LCD.drawString("Sinulla on", 0, 0);
		LCD.drawString("60 sekuntia", 0, 1);
		LCD.drawString("aikaa.", 0, 2);
		LCD.drawString("Onnea!", 0, 4);
		Delay.msDelay(2000);
		LCD.clear();

	}
	
	/**
	 * lopetusmetodi ottaa sisään tuloksen (long) ja sen avulla päättelee, että
	 * voitettiinko vai hävittinkö ja siitä riippuen, joko heittää käyttäjän
	 * tulostaulukkoon kirjoittamismetodiin tai vaan pelkästään tulostaulukon
	 * näyttämismetodiin.
	 * 
	 * @param tulos
	 *            (long) käyttäjän tulos voitetussa pelissä
	 */
	public void lopetus(long tulos) {

		if (tulos == 0) { //jos hävittiin
			LCD.drawString("Havisit :(", 0, 0);
			LCD.drawString("Parempi onni", 0, 2);
			LCD.drawString("ensi kerralla!", 0, 3);
			Delay.msDelay(2000);
			LCD.clear();
		} else { //jos voitettiin
			LCD.drawString("VOITIT :)", 0, 0);
			LCD.drawString("Pisteesti oli:", 0, 2);
			LCD.drawString(tulos + "", 0, 3);
			Delay.msDelay(2000);
			LCD.clear();
			this.tulokset.tallennaTulos(tulos);
		}
		this.tulokset.tulostaulukkoPrint();

	}
	
	
}
