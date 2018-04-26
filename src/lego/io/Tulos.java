package lego.io;

/**
 * <h1>Tulos</h1> luokka tallentaa yhden tuloksen ja sen haltijan. Implementoi
 * rajapinnan Comparable, minkä avulla tulos-luokan pisteitä voidaan vertailla
 * keskenään.
 * 
 * @author 0x03
 *
 */
public class Tulos implements Comparable<Tulos> {

	private String nimi;
	private long tulos;

	/**
	 * konstruktori, joka alustaa muuttujat nimi, tulos
	 * 
	 * @param nimi
	 *            (String) tallennetaan tuloksen haltijan nimeksi
	 * @param tulos
	 *            (long) tallennetaan tuloksen haltijan pisteiksi
	 */
	public Tulos(String nimi, long tulos) {
		this.nimi = nimi;
		this.tulos = tulos;
	}

	/**
	 * Tuloksen getteri
	 * 
	 * @return (long) tulos
	 */
	public long getTulos() {
		return this.tulos;
	}

	/**
	 * luokan tiedot Stringiksi tulostettavaan muotoon
	 */
	@Override
	public String toString() {
		return this.nimi + " - " + this.tulos;
	}

	/**
	 * Verrataan tulosta, johonkin toiseen tulokseen. Jos luku on positiivinen, niin
	 * toinen tulos on suurempi, jos luku on 0, molemmat ovat yhtä suuria, jos luku
	 * on negatiivinen, tämän luokan tulos on suurempi.
	 */
	@Override
	public int compareTo(Tulos o) {
		return (int) o.getTulos() - (int) this.tulos;
	}

}
