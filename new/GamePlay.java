/* Kelas ini berupa singleton static, memuat seluruh posisi mouse, cheese & cat */
/* Mau dicoba singleton creational design pattern */
import java.util.*;
import java.awt.Dimension;

public class GamePlay {
	public static int jmlCheese=5; //contoh
	public static int jmlCat;

	public static int mx, my; //Koordinat Mouse 
	public static int cx, cy; //Koordinat cat
	public static int chx, chy; //Koordinat cheese

	public static ArrayList<Integer> arr_chx;
	public static ArrayList<Integer> arr_chy;
	public static ArrayList<boardObject> cheese;

	public static void addCheeseCoordinat() {
		/* Ini cuma contoh, nanti diambil dari david */
		for (int i=0;i<jmlCheese;i++) {
			arr_chx.add(i); arr_chy.add(i);
		}
	}

	public GamePlay() {
		arr_chx = new ArrayList<Integer>();
		arr_chy = new ArrayList<Integer>();
		cheese = new ArrayList<boardObject>();
		addCheeseCoordinat(); //nanti akan disesuaikan
	}
	
	public static void setupCheeseBoard(boardObject bc) {
		for (int i=0;i<jmlCheese;i++) {
			cheese.add(bc); //ditambahin terus
		}
	}

	public static void setupCheeseBoardPanel(boardPanel bp, CatAndMouseGame game) {
		for (int i=0;i<jmlCheese;i++) {
			bp.setSquare(cheese.get(i), new Dimension(arr_chx.get(i),arr_chy.get(i)));
		}
	}
}

