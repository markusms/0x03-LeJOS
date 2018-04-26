package lego.ohjaus;

import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.utility.Delay;

/**
 * <h1>Ohjaus</h1> luokka, joka omassa säikeessä suorittaa infrapuna-anturin
 * lukemisen.
 * 
 * @author 0x03
 *
 */
public class Ohjaus extends Thread {

	private EV3IRSensor irSensor;
	private int komento;
	private boolean este;
	private float etaisyys;
	private boolean running;

	/**
	 * Konstruktori (luo luokan ja) tallentaa oliomuuttuja irSensoriin saadun
	 * parametrin irSensorin ja alustaa etaisyydeen nollaksi. Alustaa myös running
	 * boolean muuttujan trueksi. Niin kauan kuin true, niin threadia ajetaan.
	 * 
	 * @param irSensor
	 *            (EV3IRSensor)
	 */
	public Ohjaus(EV3IRSensor irSensor) {
		this.irSensor = irSensor;
		this.etaisyys = 0;
		this.running = true;
		this.este = false;
	}

	/**
	 * Threadissa pyörivä looppi, mikä tallentaa muuttujiin etäisyyden ja
	 * kaukosäätimen komennon.
	 */
	@Override
	public void run() {
		while (running) {
			this.komento = irSensor.getRemoteCommand(0);
			this.etaisyys = this.irDistance();
		}
	}
	
	/**
	 * Este muuttujan getteri. Este on esim. seinä ja tämä estää siihen ajamisen,
	 * kun true.
	 * 
	 * @return (boolean) este
	 */
	public boolean getEste() {
		return this.este;
	}

	/**
	 * Este muuttujan setteri. Asettaa esteen falseksi. Este on esim. seinä ja tämä
	 * estää siihen ajamisen, kun true.
	 */
	public void setEste() {
		this.este = false;
	}
	
	/**
	 * Metodi pysäyttää threadin ajamisen.
	 */
	public void running() {
		this.running = false;
	}

	/**
	 * Komento muuttujan getteri. Komento kertoo infrapunalta luetun arvon.
	 * 
	 * @return (int) komento
	 */
	public int getKomento() {
		return this.komento;
	}

	/**
	 * Etaisyys muuttujan getteri. Palatuttaa infrapuna-anturin etäisyyden.
	 * 
	 * @return (float) etaisyys
	 */
	public float getEtaisyys() {
		return this.etaisyys;
	}

	/**
	 * Metodi lukee etäisyyden ja keskiarvoistaa 25 lukemaa.
	 * 
	 * @return (float) etäisyys
	 */
	public float irDistance() {
		SampleProvider distance = irSensor.getDistanceMode();
		Delay.msDelay(50);
		SampleProvider average = new MeanFilter(distance, 7); // 7 samplea
		float[] sample = new float[average.sampleSize()];
		average.fetchSample(sample, 0);
		return sample[0];
	}

}
