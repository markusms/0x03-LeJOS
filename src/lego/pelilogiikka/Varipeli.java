package lego.pelilogiikka;

import java.util.Date;
import java.util.Random;

import lego.io.Tulostaulukko;
import lego.ohjaus.Colorsensor;
import lego.ohjaus.Moottorit;
import lego.ohjaus.Ohjaus;
import lego.ohjaus.Sound;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;

/**
 * <h1>Varipeli</h1>luokka minkä tehtävänä on antaa käyttäjälle suoritettavaksi
 * väripelin pelaaminen.
 * 
 * @author 0x03
 *
 */
public class Varipeli {

	private int vari; // väri jonne mennään
	private long tulos; // tulos
	private Tulostaulukko tulokset;
	private Sound aani;

	/**
	 * Varipeliluokan konstruktori, joka alustaa muuttujat vari ja tulos nollaksi.
	 */
	public Varipeli() {

		vari = 0;
		tulos = 0;
		tulokset = new Tulostaulukko("varipeli");
		aani = new Sound();

	}

	/**
	 * Luokan Varipeli päämetodi, joka suorittaa väripeli ohjelman ajamisen ja
	 * palauttaa arvona halutaanko vielä pelata.
	 * 
	 * @return boolean jatketaanko pelaamista
	 */
	public boolean pelataan() {

		this.ohjeet();
		this.peliAlkaa();
		tulos = this.peli();
		this.lopetus(tulos);
		return this.pelataankoViela();

	}

	/**
	 * Metodi pelataankoViela kysyy käyttäjältä halutaanko vielä pelata ja palauttaa
	 * sen.
	 * 
	 * @return boolean jatketaanko pelaamista
	 */
	public boolean pelataankoViela() {

		LCD.drawString("Haluatko jatkaa", 0, 0);
		LCD.drawString("pelaamista?", 0, 1);
		LCD.drawString("Paina esc,", 0, 3);
		LCD.drawString("jos haluat", 0, 4);
		LCD.drawString("lopettaa.", 0, 5);
		int key = Button.waitForAnyPress();
		if (key == Button.ID_ESCAPE) {
			LCD.clear();
			return false;
		} else {
			LCD.clear();
			return true;
		}

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

		if (tulos == 0) { // jos hävittiin
			LCD.drawString("Havisit :(", 0, 0);
			LCD.drawString("Parempi onni", 0, 2);
			LCD.drawString("ensi kerralla!", 0, 3);
			Delay.msDelay(2500);
			LCD.clear();
		} else { // jos voitettiin
			LCD.drawString("VOITIT :)", 0, 0);
			LCD.drawString("Pisteesti oli:", 0, 2);
			LCD.drawString(tulos + "", 0, 3);
			Delay.msDelay(2500);
			LCD.clear();
			this.tulokset.tallennaTulos(tulos);
		}
		this.tulokset.tulostaulukkoPrint();

	}

	/**
	 * Se metodi missä itse pelin pelaamislogiikka tapahtuu. Palauttaa tuloksen.
	 * 
	 * @return (long) tulos tai 0, jos hävinnyt
	 */
	public long peli() {

		int kasky = 0; //kaukosäätimen käsky
		int LED = 0; //käydään vain kerran vaihtamassa näppäimistön väriä while loopin sisällä
		boolean stop = false; //jos true ei voida ajaa eteenpäin (seinään), voidaan vain peruttaa
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		Moottorit motor = new Moottorit();
		Colorsensor checkerThreadColor = new Colorsensor();
		EV3IRSensor infraredSensor = new EV3IRSensor(SensorPort.S2);
		Ohjaus checkerThread = new Ohjaus(infraredSensor);

		checkerThread.start(); // infrapunathread päälle lukemaan komentoja kaukosäätimeltä
		checkerThreadColor.start(); // värianturin thread päälle

		while (elapsedTime < 60000) { // 60 sek aika
			elapsedTime = (new Date()).getTime() - startTime;
			if (elapsedTime > 30000 && LED == 0) { // alle 10 sek jäljellä välkytetään valoja nopeasti
				this.nappaimistonValkytys();
				LED = 1;
			} else if (elapsedTime > 50000 && LED == 1) { // alle 30 sek jäljellä välkytetään valoja
				this.nappaimistonNopeaValkytys();
			}
			kasky = checkerThread.getKomento();
			if (kasky == 1 && stop == false) {
				motor.forward();
			} else if (kasky == 3) {
				stop = false;
				motor.backwards();
			} else if (kasky == 2) {
				motor.kaannosVasemmalle();
			} else if (kasky == 4) {
				motor.kaannosOikealle();
			} else {
				motor.stop();
			}
			if (checkerThread.getEtaisyys() < 15) {
				motor.stop();
				stop = true;
			}
			if (checkerThreadColor.getVari() == this.vari) { // haetaan värianturilta väri
				break;
			}
		}
		
		
		Button.LEDPattern(0); // sammutetaan ledit
		checkerThread.running(); // pysäytetään thread
		Delay.msDelay(500); // pieni delay, että ei suljeta sensoria ennen kuin thread on pysäytetty
		infraredSensor.close();
		motor.closeMotors();
		checkerThreadColor.closeSensor(); // sulkee threadin ja sensorin

		if (elapsedTime < 60000) {
			aani.playVoitto(); // voitit musiikit
			return 60000 - elapsedTime; // palautetaan jäljelle jäänyt aika
		} else {
			aani.beep(); // hävisit piippaus
			return 0; //elapsedtime on 60 sek, joten palautetaan nolla
		}

	}

	/**
	 * Metodi mikä kertoo, että mikä väri käyttäjän pitää etsiä ja sitten aloittaa
	 * ajan laskemisen pelin alkuun.
	 */
	public void peliAlkaa() {

		LCD.drawString("Etsi vari: ", 0, 0);
		for (int i = 3; i > 0; i--) {
			LCD.drawString(i + "", 0, 4);
			Delay.msDelay(1000);
		}
		LCD.clear();

		this.vari = this.randomVari();
		this.nappaimistonVari();
		LCD.drawString(this.arvotunVarinNimi(this.vari), 0, 0);
		Delay.msDelay(1000);
		LCD.drawString("Peli alkaa", 0, 3);

		LCD.clear(4);
		for (int i = 5; i > 0; i--) {
			LCD.drawString(i + "", 0, 4);
			Delay.msDelay(1000);
		}
		LCD.clear(3);
		LCD.clear(4);

	}

	/**
	 * Metodi, joka kertoo pelin ohjeet.
	 */
	public void ohjeet() {

		LCD.drawString("Tervetuloa", 0, 0);
		LCD.drawString("vari peliin!", 0, 1);
		Delay.msDelay(2000);
		LCD.clear();

		LCD.drawString("Tehtavasi", 0, 0);
		LCD.drawString("on loytaa", 0, 1);
		LCD.drawString("arvottu vari", 0, 2);
		LCD.drawString("annetussa ajassa.", 0, 3);
		Delay.msDelay(3000);
		LCD.clear();

		LCD.drawString("Sinulla on", 0, 0);
		LCD.drawString("60 sekuntia", 0, 1);
		LCD.drawString("aikaa.", 0, 2);
		LCD.drawString("Onnea!", 0, 4);
		Delay.msDelay(2000);
		LCD.clear();

	}

	/**
	 * Metodi, joka arpoo numeron väliltä 1-3 ja palauttaa tämän arvon.
	 * 
	 * @return int 1-3
	 */
	public int randomVari() {
		Random rn = new Random();
		int range = 3 - 1 + 1;
		int randomNum = rn.nextInt(range) + 1;
		return randomNum;
	}

	/**
	 * Kertoo mikä annetun parametri (int) nimi on (String)inä.
	 * 
	 * @param i
	 *            (int) arvottuväri
	 * @return (String) arvotun värin nimi
	 */
	public String arvotunVarinNimi(int i) {
		if (i == 1) {
			return "vihrea";
		} else if (i == 2) {
			return "punainen";
		} else {
			return "keltainen";
		}
	}

	/**
	 * Vaihtaa näppäimistön värin halutuksi.
	 */
	public void nappaimistonVari() {
		if (this.vari == 1) {
			Button.LEDPattern(1); // vihreä
		} else if (this.vari == 2) {
			Button.LEDPattern(2); // punainen
		} else if (this.vari == 3) {
			Button.LEDPattern(3); // keltainen
		} 
	}

	/**
	 * Vaihtaa Vaihtaa näppäimistön värin halutuksi ja välkyttää sitä hitaasti.
	 */
	public void nappaimistonValkytys() {
		Button.LEDPattern(0);
		if (this.vari == 1) {
			Button.LEDPattern(4); // vihreä
		} else if (this.vari == 2) {
			Button.LEDPattern(5); // punainen
		} else if (this.vari == 3) {
			Button.LEDPattern(6); // keltainen
		}
	}

	/**
	 * Vaihtaa Vaihtaa näppäimistön värin halutuksi ja välkyttää sitä nopeasti.
	 */
	public void nappaimistonNopeaValkytys() {
		Button.LEDPattern(0);
		if (this.vari == 1) {
			Button.LEDPattern(7); // vihreä
		} else if (this.vari == 2) {
			Button.LEDPattern(8); // punainen
		} else if (this.vari == 3) {
			Button.LEDPattern(9); // keltainen
		}
	}

}
