package lego.ohjaus;

/**
 * <h1>Main</h1> Koko ohjelman pääluokka, joka suorittaa ohjelman
 * käynnistämisen.
 * 
 * @author 0x03
 *
 */
public class Main {

	/**
	 * Main luokan päämetodi main, joka suorittaa ohjelman ajamisen.
	 * 
	 * @param args
	 *            (String[]) komentoriviparametrit, joita ei käytetä)
	 */
	public static void main(String[] args) {

		Puzzlebot puzzle = new Puzzlebot();
		puzzle.pelataan();

	}

}
