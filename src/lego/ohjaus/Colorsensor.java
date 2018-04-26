package lego.ohjaus;

import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.Color;
import lejos.utility.Delay;

/**
 * <h1>Colorsensor</h1> värisensorin käyttöluokka
 * 
 * @author 0x03
 *
 */
public class Colorsensor extends Thread {

	private EV3ColorSensor cs;
	private int vari;
	private boolean running;

	/**
	 * Konstruktori, joka luo uuden värisensorin käyttöön portiin S3.
	 */
	public Colorsensor() {
		cs = new EV3ColorSensor(SensorPort.S3);
		vari = 0;
		running = true;
	}

	/**
	 * Threadin ajometodi, jossa kutsutaan metodia, joka lukee värin ja tallenetaan
	 * se muuttujaan vari.
	 */
	@Override
	public void run() {
		while (running) {
			this.vari = this.getColor();
		}
	}

	/**
	 * Lukee sensorilla värin ja palauttaa int arvon: 1 = vihreä, 2 = punainen, 3 =
	 * keltainen, muut = 0
	 * 
	 * @return (int) väri
	 */
	public int getColor() {
		switch (cs.getColorID()) {
		case Color.GREEN:
			return 1;
		case Color.YELLOW:
			return 3;
		case Color.RED:
			return 2;
		default:
			return 0;
		}
	}

	/**
	 * Värin getteri. 
	 * 
	 * @return (int) väri: 1 = vihreä, 2 = punainen, 3 = keltainen, muut = 0
	 */
	public int getVari() {
		return this.vari;
	}

	/**
	 * Suljetaan thread, odotetaan hetki, että thread sulkeutuu ja sitten suljetaan
	 * värisensori.
	 */
	public void closeSensor() {
		running = false;
		Delay.msDelay(100);
		cs.close();
	}
}