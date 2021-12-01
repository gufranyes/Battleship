import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class GameBoard {

    //Initialize the layout
    public char[] [] slots;

    //To change the background color
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";

    //Default Constructor for Layout
    public GameBoard() {
        slots = new char[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                slots[i][j] = 'O';
            }
        }
    }
    //Returns true if lengths and positions are correct and places boats
    public boolean placeBoat(int x1, int y1, int x2, int y2, char ship) {
        //Boats length can not be zero
        if (x1 == x2 && y1 == y2) {
            return false;
        }
        //Checks ship length
        else if ((ship == 'C' && (((x2 - x1) == 0 && (y2 - y1) != 4) || ((x2 - x1) != 4 && (y2 - y1) == 0))) ||
                (ship == 'B' && (((x2 - x1) == 0 && (y2 - y1) != 3) || ((x2 - x1) != 3 && (y2 - y1) == 0))) ||
                (ship == 'S' && (((x2 - x1) == 0 && (y2 - y1) != 2) || ((x2 - x1) != 2 && (y2 - y1) == 0))) ||
                (ship == 'D' && (((x2 - x1) == 0 && (y2 - y1) != 1) || ((x2 - x1) != 1 && (y2 - y1) == 0)))) {
            return false;
        } else if (x1 == x2) {
            for (int i = y1; i <= y2; i++) {
                slots[x1 - 1][i - 1] = ship;
            }
        } else if (y1 == y2) {
            for (int i = x1; i <= x2; i++) {
                slots[i - 1][y1 - 1] = ship;
            }
        }
        return true;
    }
    //Checks the states of shots and responds according to these conditions.
    //Plays sound files in the res directory.
    public boolean hitOrMiss(int x, int y) throws Exception {
        if (slots[x][y] == 'S' || slots[x][y] == 'B' || slots[x][y] == 'C' || slots[x][y] == 'D') {
            playSound("res/hit.wav");
            slots[x][y] = 'H';
            return true;
        }
        else if (slots[x][y] == 'H' || slots[x][y] == 'x'){
            playSound("res/again.wav");
            System.out.println("You already hit that location. SHOOT IT AGAIN!");
            return false;
        }
        playSound("res/miss.wav");
        slots[x][y] = 'x';
        return false;
    }

    //To display board according to the attempts that has been made
    public void showAttempts(boolean state, int x, int y, String name){

        if (state){
            slots[x-1][y-1] = 'H';
        }
        else{
            slots[x-1][y-1] = 'x';
        }
        System.out.println(name);
        board();
    }

    //Prints the board
    public void board() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (slots[i][j] == 'H'){
                    System.out.print(ANSI_GREEN_BACKGROUND + slots[i][j] + ANSI_RESET + " ");
                }
                else if (slots[i][j] == 'x'){
                    System.out.print(ANSI_RED_BACKGROUND + slots[i][j] + ANSI_RESET + " ");
                }
                else System.out.print(slots[i][j] + " ");
            }
            System.out.println();
        }
    }

    //Checks for the winning conditions
    public boolean checkForLoss() {
        int count = 0;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (slots[i][j] == 'C' || slots[i][j] == 'B' || slots[i][j] == 'S' || slots[i][j] == 'D') count++;
            }
        }

        return (count > 0);
    }

    //To play sound files
    void playSound(String soundFile) throws Exception {
        File f = new File("./" + soundFile);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
}
