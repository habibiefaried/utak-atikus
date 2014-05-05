import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 Original Author : Javin Paul
 Modified by : Utak-atikus team
*/

public class MapReader {  
    private static boolean[][] temporaryMap = new boolean[100][100];
    public static boolean[][] wallsMap;
    private static int jmlKolom;
    private static int jmlBaris;

    private static void parseIntoWallsMap(String line, int Kolom) {
        if (line != null) {
            System.out.println(line);
            System.out.println("Kolom : "+Kolom);
            int baris = 0;
            for (String retval: line.split(" ")){
                System.out.println("WORK : "+baris+","+Kolom);
                if (retval.equals("0")) {
                    temporaryMap[baris][Kolom] = (boolean) false;
                }else {
                    temporaryMap[baris][Kolom] = (boolean) true;
                }
                baris++;
                }
            jmlBaris = baris;
        }
    }

    private static void copyMap() {
        System.out.println("Jumlah Baris : "+jmlBaris);
        System.out.println("Jumlah Kolom : "+jmlKolom);
        wallsMap = new boolean[jmlBaris][jmlKolom];
        int i; int j;
        for (i=0;i<jmlBaris;i++) {
            for(j=0;j<jmlKolom;j++) {
                wallsMap[i][j] = temporaryMap[i][j]; //copy semua
                System.out.println("CopyMap : "+i+","+j);
            }
        }
    }

        public MapReader() {
    //public boolean[][] temporaryMap = new bool
    jmlKolom = 0;
        //reading file line by line in Java using BufferedReader      
        FileInputStream fis = null;
        BufferedReader reader = null;

        try {
            fis = new FileInputStream("map.txt");
            reader = new BufferedReader(new InputStreamReader(fis));
         
            System.out.println("Reading Maps...");
         
            String line="sonny ganteng";
            //String line = reader.readLine();
            while(line != null){
                line = reader.readLine();
                parseIntoWallsMap(line,jmlKolom);
                jmlKolom++;
            }          
         
        } catch (FileNotFoundException ex) {
           ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
         
        } finally {
            try {
        jmlKolom--; //jumlah kolom dikurangin(harusnya kyk gini)
                reader.close();
                fis.close();
        copyMap(); //copy map
        //System.out.println(wallsMap.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
  }
}