package lego.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * <h1>Tulostalukko</h1>luokka näyttää pelin tulostaulukon näytöllä lukemalla
 * sen pelin tulostaulukkotiedostosta ja tallentaa pelin tulokset
 * tulostaulukkotiedostoon.
 * 
 * @author 0x03
 *
 */
public class Tulostaulukko {

	private String pelinNimi;

	/**
	 * konstruktori luo luokan ja antaa sille nimen
	 * 
	 * @param pelinNimi
	 *            (String) tallenetaan muuttujaan pelinNimi
	 */
	public Tulostaulukko(String pelinNimi) {
		this.pelinNimi = pelinNimi;
	}

	/**
	 * Metodi tallennaTulos ottaa sisään tuloksen (long) ja kysyy käyttäjän
	 * nimimerkkiä ja tallentaa sen ja tuloksen tulostaulukkoon.
	 * 
	 * @param tulos
	 *            (long) käyttäjän tulos voitetussa pelissä
	 */
	public void tallennaTulos(long tulos) {
		String filename = this.pelinNimi + ".txt"; // tiedoston nimi mihin tulokset tallennetaan
		File file = new File(filename); // luodaan uusi tiedosto
		LCD.drawString("Anna nimimerkkisi:", 0, 0);
		LCD.drawString("^ = kirjain ylos", 0, 2);
		LCD.drawString("v = kirjain alas", 0, 3);
		LCD.drawString("ENTER = hyvaksy", 0, 4);

		char[] nimimerkki = new char[3]; // nimimerkki char array (3 kirjainta)
		String aakkoset = "abcdefghijklmnopqrstuvwxyz";
		int kirjain = 0; // aakkoset läpikäyvä muuttuja
		int key = 0;
		for (int i = 0; i < 3; i++) { // kolmen merkin nimimerkki
			while (true) { // käydään läpi kirjaimia, kunnes käyttäjä valitsee enterillä kirjaimen
				LCD.drawString(aakkoset.charAt(kirjain) + "", 0, 7);
				if (i == 1) {
					LCD.drawString("Anna toinen:", 0, 6);
				} else if (i == 2) {
					LCD.drawString("Anna viimeinen:", 0, 6);
				}
				key = Button.waitForAnyPress(); // odotetaan painallusta
				if (key == Button.ID_UP) {
					if (aakkoset.charAt(kirjain) != 'z') { // ei anneta yrittää mennä Stringin viimeisen merkin yli (z
															// on viimeinen kirjain)
						kirjain++;
					}
				} else if (key == Button.ID_DOWN) {
					if (aakkoset.charAt(kirjain) != 'a') { // ei anneta yrittää mennä Stringin ensimmäisen merkin ohi (a
															// on ensimmäinen kirjain)
						kirjain--;
					}
				} else if (key == Button.ID_ENTER) {
					break;
				}
				LCD.clear(7);
				LCD.clear(6);
			}
			nimimerkki[i] = aakkoset.charAt(kirjain); // tallenetaan haluttu merkki nimimerkki char arrayn oikeaan
														// paikkaan
		}
		String nimi = String.valueOf(nimimerkki); // muutetaan char array stringiksi

		try { // yritetään kirjoittaa tiedostoon
			if (!file.exists()) { // luodaan uusi tiedosto, jos tiedostoa ei ole luotu
				file.createNewFile();
			}

			FileWriter fileWriter = new FileWriter(file, true); // kirjoitetaan vanhan tekstin perään, ei vanhan päälle
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(nimi + "-" + tulos + "\r\n"); // tallennetaan muodossa NIMI-TULOS
			bufferedWriter.close(); // suljetaan kirjoittaja

		} catch (Exception e) { // napataan kaikki virheet, mitä voi ilmetä yritettäessä kirjoittaa tiedostoon
			LCD.drawString("Virhe lukiessa", 0, 0);
			LCD.drawString("tiedostoa.", 0, 1);
		}

	}

	/**
	 * tulostaulukkoPrint -metodi järjestää tulostaulukon pisteiden mukaan ja
	 * tulostaa tulostaulukon
	 */
	public void tulostaulukkoPrint() { // pävitetään lukemaan tulokset jostain

		ArrayList<String> tulokset = new ArrayList<String>(); // listan mihin luetaan ja tallenetaan tulokset
																// tiedostosta missä tulokset sijaitsevat
		ArrayList<Tulos> tuloksetSort = new ArrayList<Tulos>(); // tulokset muodossa, jossa ne voidaan järjestää
																// pisteiden mukaan

		String filename = this.pelinNimi + ".txt";

		try { // yritetään lukea tiedostoa
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) { // niin kauan kun tiedostossa on tekstiä
				tulokset.add(line); // lisätään jokainen rivi (NIMI-TULOS) ArrayListiin tulokset
			}
			reader.close();

		} catch (Exception e) { // napataan kaikki virheet
			LCD.drawString("Virhe lukiessa", 0, 0);
			LCD.drawString("tiedostoa.", 0, 1);
		}

		for (String s : tulokset) { // iteroidaan ArrayList tulokset läpi
			String[] osat = s.split("-"); // jokainen rivi on NIMI-TULOS, meidän pitää saada jaettua tämä yhdeksi
											// Stringiksi nimi ja toiseksi Stringiksi tulos
			String nimi = osat[0];
			String tulosString = osat[1];
			long tulos = Long.valueOf(tulosString).longValue(); // String tulos pitää muuntaa long muotoon, että sitä
																// voidaan vertailla toisten tulosten kanssa
			Tulos result = new Tulos(nimi, tulos); // luodaan uusi luokka Tulos, johon tallennetaan nimi ja tulos
			tuloksetSort.add(result); // lisätään Tulos ArrayListiin tuloksetSort, joka pystytään järjestämään
										// pisteiden mukaan
		}
		Collections.sort(tuloksetSort); // järjestetään tulokset pisteiden mukaan

		LCD.clear();
		LCD.drawString("TULOSTAULUKKO", 0, 0);

		int k = 1; // tuloksen sijainti tulostalukossa yhdestä eteenpäin
		int j = 0; // tulokset (List) läpi iteroiva muuttuja
		if (!tuloksetSort.isEmpty()) {
			for (int i = 2; i < 7; i++) { // iteroidaan LCD näytön rivit läpi (5 parasta tulosta)
				LCD.drawString(k + ". " + tuloksetSort.get(j), 0, i);
				if (j == tulokset.size()-1) { // jos tuloksia on alle 5
					break;
				}
				j++;
				k++;
			}
		} else {
			LCD.drawString("Ei tuloksia :(", 0, 2);
		}

		Delay.msDelay(5000);
		LCD.clear();

	}

}
