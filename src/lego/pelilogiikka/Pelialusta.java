package lego.pelilogiikka;

import java.util.*;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;
import lego.ohjaus.Moottorit;
import lego.ohjaus.Ohjaus;
import lego.ohjaus.Sound;

/**
 * Valmistumispelin
 * <h1>Pelialusta</h1> luokka, joka luo pelialustan, missä pelin pelaaminen
 * tapahtuu. Luokka luo näytölle kartan piirtämällä jokaiseen näytön
 * koordinaattiin . (pisteen) ja robotin kordinaattiin R ja kohteen
 * koordinaattiin L. Kun robottia ajetaan eteen- tai taaksepäin puolen sekunnin
 * ajan, liikkuu robotti näytölä ylös tai alas päin. Jos robotin renkaat ovat
 * käännetty oikealle ja ajetaan eteenpäin liikkuu robotti (R) näytöllä oikealla
 * ja, jos peruutetaan, liikkuu robotti näytöllä vasemmalle. Toimii toisinpäin,
 * jos renkaat on käännetty vasemmalle.
 * 
 * @author 0x03
 */
public class Pelialusta {

	private int pituus;
	private int korkeus;
	private boolean voitto;
	private String suunta;
	private Lopputyo lego;
	private Robotti puzzlebot;
	private Sound aani;

	/**
	 * Konstruktori, joka luo lopputyön, robotin ja alustaa pelialueen näytön
	 * rajojen mukaan.
	 */
	public Pelialusta() {

		this.pituus = 17; // LCD-näytön leveys
		this.korkeus = 7; // LCD-näytön korkeus
		this.voitto = false; // voitettiinko peli
		this.lego = new Lopputyo();
		this.puzzlebot = new Robotti();
		aani = new Sound();
		suunta = "eteen";

	}

	/**
	 * Itse pelin pelaaminen.
	 * 
	 * @return (long) pelin tulos eli jäljelle jäänyt aika tai 0, jos hävittiin.
	 */
	public long peli() {

		EV3IRSensor infraredSensor = new EV3IRSensor(SensorPort.S2);
		Moottorit motor = new Moottorit();
		Ohjaus checkerThread = new Ohjaus(infraredSensor);

		int kasky = 0; // kaukosäätimen käsky
		boolean stop = false; // jos true ei voida ajaa eteenpäin (seinään), voidaan vain peruttaa
		long startTime = System.currentTimeMillis();
		long elapsedTime = 0L;

		checkerThread.start(); // infrapunathread päälle lukemaan komentoja kaukosäätimeltä

		while (elapsedTime < 60000) { // 60 sek aika

			elapsedTime = (new Date()).getTime() - startTime;
			this.piirretaanLattia();

			if (voitto == true) {
				LCD.clear();
				break;
			}

			kasky = checkerThread.getKomento();
			if (kasky == 1 && stop == false) {
				this.move(1, motor);
			} else if (kasky == 2) {
				this.move(2, motor);
			} else if (kasky == 3) {
				this.move(3, motor);
				stop = false;
			} else if (kasky == 4) {
				this.move(4, motor);
			} else {
				motor.stop();
			}
			if (checkerThread.getEtaisyys() < 15) {
				motor.stop();
				stop = true;
			}

		}

		checkerThread.running(); // pysäytetään infrapunathread
		Delay.msDelay(500); // pieni delay, että ei suljeta sensoria ennen kuin thread on pysäytetty
		infraredSensor.close();
		motor.closeMotors();

		if (elapsedTime < 60000) {
			aani.playVoitto(); // voitit musiikit
			return 60000 - elapsedTime; // palautetaan jäljelle jäänyt aika
		} else {
			aani.beep(); // hävisit piippaus
			return 0; // elapsedtime on 60 sek, joten palautetaan nolla
		}

	}

	/**
	 * LCD-näytölle pelilattian piirtämismetodi. Jokainen koordinaatti on . (piste),
	 * Robotti on R ja kohde Lopputyö on L.
	 */
	public void piirretaanLattia() {

		for (int i = 0; i < this.korkeus + 1; i++) { // käydään läpi jokainen näytön rivi

			char[] xakseli = new char[this.pituus + 1]; // luodaan char array (yksi rivi merkkejä 0-17), johon
														// tallennetaan lattian tiedot

			for (int j = 0; j < this.pituus + 1; j++) {
				xakseli[j] = '.';
			}

			if (this.puzzlebot.getY() == i) { // robotti löytyy kyseiseltä näytön riviltä
				xakseli[this.puzzlebot.getX()] = 'R';
			}

			if (this.lego.getY() == i) { // Lopputyö (lego) löytyy kyseiseltä näytön riviltä
				if (this.puzzlebot.getX() == this.lego.getX()) { // Robotti ja lego ovat samassa pisteessä!
					// ei tarvitse tehdä mitään (muuta kuin ilmoittaa, että peli on voitettu), koska
					// R piirrettiin jo aiemmin
					this.voitto = true;
				} else {
					xakseli[this.lego.getX()] = 'L';
				}
			}
			StringBuilder sb = new StringBuilder(); // char array ei näy LCD:llä järkevästi, joten muunnetaan se
													// Stringiksi StringBuilderin avulla.
			sb.append(xakseli);
			String xrivi = sb.toString();

			LCD.drawString(xrivi, 0, i);
		}

	}

	/**
	 * Metodi missä liikutetaan robottia näytöllä, kun liikutaan robotilla 0,5
	 * sekuntia oikeassa elämässä. Liike tapahtuu näytöllä, jos ajetaan eteen- tai
	 * taaksepäin. Jos käännetään renkaita vaihtuu robotin suunta. Suunta ohjaa
	 * mihin päin robotti liikkuu näytöllä, kun ajetaan eteen- tai taaksepäin.
	 * 
	 * @param liike
	 *            (int) kaukosäätimeltä saatava komento
	 * @param motor
	 *            (Moottorit) moottoriluokka, jolla voidaan ohjata moottoreita.
	 */
	public void move(int liike, Moottorit motor) {
		if (liike == 1) { // ylöspäin
			motor.forward();
			if (suunta.equals("eteen")) {
				if (this.puzzlebot.getY() != 0) { // ei mennä reunoista yli
					this.puzzlebot.setY(this.puzzlebot.getY() - 1); // miinus koska y ylöspäin on negatiivinen
				}
			} else if (suunta.equals("oikealle")) {
				if (this.puzzlebot.getX() < this.pituus) {
					this.puzzlebot.setX(this.puzzlebot.getX() + 1); // ajetaan oikealle
				}
			} else if (suunta.equals("vasemmalle")) { // ajetaan vasemmalle
				if (this.puzzlebot.getX() != 0) {
					this.puzzlebot.setX(this.puzzlebot.getX() - 1);
				}
			}
			Delay.msDelay(500);
		} else if (liike == 2) { // vasemmalle
			int kaannos = motor.kaannosVasemmalle();
			if (kaannos == 0) { // renkaat suorassa
				this.suunta = "eteen";
			} else {
				this.suunta = "vasemmalle";
			}
		} else if (liike == 4) { // oikealla
			int kaannos = motor.kaannosOikealle();
			if (kaannos == 0) { // renkaat suorassa
				this.suunta = "eteen";
			} else {
				this.suunta = "oikealle";
			}
		} else if (liike == 3) { // alaspäin
			motor.backwards();
			if (suunta.equals("eteen")) {
				if (this.puzzlebot.getY() < this.korkeus) {
					this.puzzlebot.setY(this.puzzlebot.getY() + 1);
				}
			} else if (suunta.equals("oikealle")) {
				if (this.puzzlebot.getX() != 0) {
					this.puzzlebot.setX(this.puzzlebot.getX() - 1); // peruutetaan vasemmalle
				}
			} else if (suunta.equals("vasemmalle")) {
				if (this.puzzlebot.getX() < this.pituus) {
					this.puzzlebot.setX(this.puzzlebot.getX() + 1); // peruutetaan oikealle
				}
			}
			Delay.msDelay(500);
		}
	}

}
