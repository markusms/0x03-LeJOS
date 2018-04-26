package lego.pelilogiikka;

import java.util.ArrayList;

import lego.io.Tulostaulukko;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;
import lego.ohjaus.Colorsensor;
import lego.ohjaus.Moottorit;
import lego.ohjaus.Ohjaus;
import lego.ohjaus.Sound;

/**
 * <h1>Randompelin</h1>
 * luokka, jossa pelataan peliä, jonka on tarkoitus pelaajan kannalta vaikeutua inkrementiaalisesti koko ajan.
 * Peli2_1 on työnimi, saa refactoroida myöhemmin.
 * 
 * @author 0x03
 *
 */

public class Peli2_1 {
	
	private Tulostaulukko results;	//This one is supposed to hold all the necessary	results.
	private int triggerColor;		//The desired goal where to drive.
	private int lap;					//Counter for the amount of laps.
	private int checkpointColor;		//The desired chekcpoint color.
	private int errorColor;			//The color where player gets minus points.
	private boolean lapUnderWay;
	private int mistakes;
	private Sound sound;
	
	/**
	 * Constructor for the "random" game.
	 */
	
	public Peli2_1() {
		
		this.results = new Tulostaulukko("gambit");
		this.triggerColor = 3;     //Sets the color for time counting to red.
		this.checkpointColor = 1;   //Sets the color for checkpoint to green.
		this.errorColor = 2;			//Error color is yellow. Points will be taken if this color is met during game.
		this.lap = 0;				//Initial lap count.
		this.lapUnderWay = false;	//Flag to check whether lap is currently underway.
		this.sound = new Sound();
		
		
	}
	
	/**
	 * This method is just in charge of calling other methods of this class.
	 * It will eventually return whether multiple rounds and thus laps are played.
	 * Only by playing multiple rounds are good scores achievable. This is not revealed to the player as trick to be found.
	 * 
	 * @return (boolean) whether game shall continue
	 */
	
	public boolean letsPlay() {
		
		this.instructions();
		this.ending(this.calculateFinalScoreForLap(this.mainGame()));		
		return this.playMore();
		
		
	}
	
	/**
	 * Method playMore ask from the user, whether they want to play more
	 * and returns it back.
	 * 
	 * @return boolean shall the game continue
	 */
	public boolean playMore() {
		
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
	 * Method that tells the instructions for the game.
	 */
	public void instructions() {

		LCD.drawString("Tervetuloa", 0, 0);
		LCD.drawString("haasteeseen", 0, 1);
		Delay.msDelay(2000);
		LCD.clear();

		LCD.drawString("Tehtavasi", 0, 0);
		LCD.drawString("on ajaa", 0, 1);
		LCD.drawString("nopeasti", 0, 2);
		LCD.drawString("punaisten alueiden", 0, 3);
		LCD.drawString("valilla", 0, 4);
		LCD.drawString("vihrean kautta", 0, 5);
		Delay.msDelay(3500);
		LCD.clear();

		LCD.drawString("Haasteesi on", 0, 0);
		LCD.drawString("valttaa muita", 0, 1);
		LCD.drawString("vareja ja", 0, 2);
		LCD.drawString("selvita monta", 0, 3);
		LCD.drawString("kierrosta mukana", 0, 4);
		Delay.msDelay(3500);
		LCD.clear();
		
		
		
	}
	
	/**
	 * Main method containing nearly all logic behind the game. First a hardcoded limit is set for both chekcpoint time
	 * and finaltime. Laps and mistakes are counted, as well as the laptimes. If timelimits are reached for one checkpoint
	 * or for the whole game, it will exit with zero.
	 * 
	 * @return (long) lapTime
	 */
	
	public long mainGame () {
		
		final long hardLimitLap = 30000;								//Hardlimit for "laptime" in ms.
		final long hardLimitCheckpoint = hardLimitLap / 2 + 2000;		//Hardlimit for checkpoint time in ms. Has to be longer than hardLimit to prevent cheating.
		int order = 0;												//This is the order from remote control.
		long lapTime = 0;
		this.mistakes = 0;
		boolean mistakesInitialize = true;			//ready to count the mistakes the player makes.
		boolean stop = false;						//This is to make the motors stop.
		boolean lapCheckpointReached = false;		//Checkpoint flag.
		boolean endingReached = false;				//Final ending condition to stop threads. This will be achieved either by winning completely or losing.
		Moottorit motor = new Moottorit();
		Colorsensor checkerThreadColor = new Colorsensor();
		EV3IRSensor infraredSensor = new EV3IRSensor(SensorPort.S2);
		Ohjaus checkerThreadSteering = new Ohjaus(infraredSensor);
		
		checkerThreadSteering.start();
		checkerThreadColor.start();
		long ending = System.currentTimeMillis() + hardLimitLap;					//Hardlimit ending.
		long checkpoint = System.currentTimeMillis() + hardLimitCheckpoint;			//Hardlimit checkpoint time.
		while(endingReached == false) {
			order = checkerThreadSteering.getKomento();
			if (order == 1 && stop == false) {
				motor.forward();
			} else if (order == 3) {
				stop = false;
				motor.backwards();
			} else if (order == 2) {
				motor.kaannosVasemmalle();
			} else if (order == 4) {
				motor.kaannosOikealle();
			} else {
				motor.stop();
			}
			if (checkerThreadSteering.getEtaisyys() < 15) {
				motor.stop();
				stop = true;
			}
			int coloring = checkerThreadColor.getVari();				//Get the color from sensor.
			if (!lapUnderWay && coloring == this.triggerColor) {  	//First, we have to cross into red to start the game.
				this.lapUnderWay = true;
			}
			if (System.currentTimeMillis() < ending && (lapCheckpointReached || System.currentTimeMillis() < checkpoint)) {  //Checks the time limits.
				if (coloring == this.checkpointColor && !lapCheckpointReached && lapUnderWay) {
					lapCheckpointReached = true;						//sets the trigger for checkpoint reached.
				}
				else if  (coloring == this.triggerColor && lapCheckpointReached) {		//This is the true winners exit condition. Checkpoint is reached and triggerColor is found.
					lapTime = ending - System.currentTimeMillis();
					this.lapUnderWay = false;
					lap++;
					endingReached = true;
					break;
				}	
				else if (coloring == this.errorColor && mistakesInitialize) {				//Now the costly mistakes are counted here.
					this.mistakes++;
					mistakesInitialize = false;
				}
				else if (coloring != this.errorColor) {		//Mistakes are only counted after another color is encountered.
					mistakesInitialize = true;				//Sets the flag to count mistakes again.
				}
			}
					
			else {					//Losers go here as time has expired. Goto would be useful here as nothing else is left to be done.
				endingReached = true;
				lap++;
				break;
			}
					
	}
		checkerThreadSteering.running();
		Delay.msDelay(200);
		infraredSensor.close();
		motor.closeMotors();
		checkerThreadColor.closeSensor();
		return lapTime; 				//Default exit value.
	}
		
	
	
	/**
	 * Method calculates the final score of the lap. The resolute player who hangs on will eventually get score up due to
	 * bigger amount of laps.
	 * @return (long) points
	 */
	
	public long calculateFinalScoreForLap (long lapTimez) {
		long points = lapTimez;
		points -= (1000 * mistakes);
		points += (lap - 1) * 2000;
		return points;
	}
	
	
	/**
	 * Method that checks, whether the calculated final score is positive and if it is, gives the player the option to store it.
	 * @param number (long) finalScore
	 */
	
	public void ending (long number) {
		if (number > 0) {
			sound.playVoitto(); //Winning song.
			LCD.drawString("VOITIT :)", 0, 0);
			LCD.drawString("Pisteesti oli:", 0, 2);
			LCD.drawString(number + "", 0, 3);
			Delay.msDelay(2500);
			LCD.clear();
			this.results.tallennaTulos(number);
		}
		else {
			LCD.drawString("Go home", 0, 0);
			LCD.drawString("you are drunk", 0, 1);
			Delay.msDelay(2000);
			LCD.clear();
		}
		this.results.tulostaulukkoPrint();
	}
	
}
