package lego.ohjaus;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

/**
 * <h1>Moottorit</h1> luokalla käytetään laitteen moottoreita.
 * 
 * @author 0x03
 *
 */
public class Moottorit {
	private RegulatedMotor mA = new EV3LargeRegulatedMotor(MotorPort.A);
	private RegulatedMotor mB = new EV3MediumRegulatedMotor(MotorPort.B);
	private int kaannos = 0; // käännös tapahtuu 35 asteen inkrementeissä ja maksimissaan 35 astetta yhteen
							// suuntaan, joten jos halutaan kääntyä vasemmalta oikealle pitää kääntää kaksi
							// kertaa oikealle ensiksi keskelle ja sitten oikealle)

	/**
	 * Ajetaan eteenpäin nopeudella 400.
	 */
	public void forward() {
		mA.setSpeed(400);
		mA.forward();
	}

	/**
	 * Pysäytetään moottorit.
	 */
	public void stop() {
		mA.stop();
	}

	/**
	 * Ajetaan taaksepäin.
	 */
	public void backwards() {
		mA.setSpeed(400);
		mA.backward();
	}

	/**
	 * Käännetään renkaita oikealle.
	 * 
	 * @return (int) kaannos eli mihin suuntaan renkaat ovat kääntyneet
	 */
	public int kaannosOikealle() {
		if (this.kaannos == 1) {
			//ei anneta kääntää yli 35 astetta
		} else if (this.kaannos == 0) {
			mB.rotate(-35);
			this.kaannos = 1;
		} else if (this.kaannos == -1) {
			mB.rotate(-35);
			this.kaannos = 0;
		}
		return kaannos;
	}

	/**
	 * Käännetään renkaita vasemmalle.
	 * 
	 * @return (int) kaannos eli mihin suuntaan renkaat ovat kääntyneet
	 */
	public int kaannosVasemmalle() {
		if (this.kaannos == 1) {
			mB.rotate(35);
			this.kaannos = 0;
		} else if (this.kaannos == 0) {
			mB.rotate(35);
			this.kaannos = -1;
		} else if (this.kaannos == -1) {
			//ei anneta kääntää yli 35 astetta
		}
		return kaannos;
	}

	/**
	 * Suljetaan moottorit.
	 */
	public void closeMotors() {
		mA.close();
		mB.close();
	}
}
