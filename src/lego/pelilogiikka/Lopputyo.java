package lego.pelilogiikka;

import java.util.Random;

/**
 * <h1>Lopputyo</h1>luokka, joka luo valmistumispelille lopputyön, minne pelaajan pitää ajaa.
 * @author 0x03
 *
 */
public class Lopputyo {
    
    private int x;
    private int y;
     
    /**
     * Luokan konstruktori, mikä alustaa lopputyölle random-kordinaatit (0-17, 0-7).
     */
    public Lopputyo(){
        x = random(17);
        y = random(7);
        if (x == 0 && y == 0) { //ei voi alussa "spawnata" meidän robotin päälle
            x = 1;
        }
    }
     
    /**
     * Syötetyn parametrin mukaan luo random muuttajan 0-luku;
     * @param luku (int) maksimi arvo random luvulle
     * @return (int) random arvo nollasta lukuun.
     */
    public int random(int luku){
        Random rn = new Random();
        int range = luku - 0 + 1;
        int randomNum = rn.nextInt(range) + 0;
        return randomNum;
    }
 
    /**
     * Palauttaa x-koordinaatin
     * @return (int) Palauttaa x-koordinaatin
     */
    public int getX() {
        return x;
    }
    
    /**
     * Palauttaa y-koordinaatin
     * @return (int) Palauttaa y-koordinaatin
     */
    public int getY() {
        return y;
    }
 
}