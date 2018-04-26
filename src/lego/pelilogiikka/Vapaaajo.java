package lego.pelilogiikka;

import lego.ohjaus.Moottorit;
import lego.ohjaus.Ohjaus;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;

/**
 * <h1>Vapaaajo</h1> luokka jonka tehtävänä on antaa pelaajan ajaa robotilla
 * vapaasti ilman tehtävää.
 * 
 * @author 0x03
 *
 */
public class Vapaaajo {

	/**
	 * Luokan päämetodi, joka käynnistää inhrapunasensorithreadin ja ottaa vastaan
	 * infrapunasensorilta käskyjä ja ohjaa moottoreita sen mukaan.
	 */
	public void ajellaan() {
		EV3IRSensor infraredSensor = new EV3IRSensor(SensorPort.S2);
		Moottorit motor = new Moottorit();
		Ohjaus checkerThread = new Ohjaus(infraredSensor);
		int kasky = 0;
		boolean stop = false;

		checkerThread.start();

		while (!Button.ESCAPE.isDown()) {
			LCD.drawString("Ajele vapaasti!", 0, 0);
			LCD.drawString("Voit lopettaa", 0, 1);
			LCD.drawString("painamalla ESC", 0, 2);
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
		}
		LCD.clear();
		motor.closeMotors();
	}
}
