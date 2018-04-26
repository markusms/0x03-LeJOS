package lego.pelilogiikka;

/**
 * <h1>Robotti</h1>luokka, joka luo valmistumispelille robotin, joka kertoo missä pelaaja ajaa.
 * @author 0x03
 *
 */
public class Robotti {

	private int x;
	private int y;
	
	/**
	 * Konstruktori, joka alustaa muuttujat x ja y nollaksi.
	 */
	public Robotti() {
		this.x = 0;
		this.y = 0;
	}
	
	/**
	 * x-koordinaatin getteri
	 * @return (int) x
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * y-koordinaatin getteri
	 * @return (int) y
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * x-koordinaatin setteri
	 * @param x (int)
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * y-koordinaatin setteri
	 * @param y (int)
	 */
	public void setY(int y) {
		this.y = y;
	}
}
