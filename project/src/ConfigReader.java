import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ConfigReader {
	private int posisi_play = 0;
	private int posisi_train = 1;
	
	private int episode = 10;
	private int batas_penglihatan;
	private int jumlahKeju;
	private int jumlahKucing;
	private ArrayList<Point> set_posisi_play;
	private ArrayList<Point> set_posisi_train;
	private ArrayList<Point> arr_posisi_tikus;
	private ArrayList<ArrayList<Point>> arr_of_arr_posisi_kucing;
	
	public static ConfigReader conf;
	
//	public static void main(String[] args){
//		ConfigReader conf = new ConfigReader();
//		System.out.println("arr posisi tikus: "+conf.getArrayPosisiTikus(1));
//		System.out.println("arr posisi kucing:"+conf.getArrayPosisiKucing(1));
//		System.out.println("arr posisi keju:"+conf.getArrayPosisiKeju(1));
//	}
	
	public ConfigReader() {
	        //reading file line by line in Java using BufferedReader      

	        FileInputStream fis = null;
	        BufferedReader reader = null;
	        String line;
            int ind = 0;
            set_posisi_play = new ArrayList<Point>();
            set_posisi_train= new ArrayList<Point>();
            arr_posisi_tikus = new ArrayList<Point>();
            arr_of_arr_posisi_kucing = new ArrayList<ArrayList<Point>>();
            
	        try {
	            fis = new FileInputStream("tabel.txt");
	            reader = new BufferedReader(new InputStreamReader(fis));       
	            System.out.println("Reading tabel");
	            
                //read batas penglihatan
                batas_penglihatan = Integer.parseInt(reader.readLine());
                
                //read c,k
                line = reader.readLine();
            	for (String retval: line.split(" ")){
            		if (ind == 0) {jumlahKeju = Integer.parseInt(retval);}
            		else if (ind == 1){jumlahKucing = Integer.parseInt(retval); }
            		ind++;
                }

	            //read set_posisi_play

            	line = reader.readLine();
            	int a = 0,b = 0;
            	for (String retval: line.split(" ")){
        			retval = retval.substring(1,retval.length()-1);
        			retval = retval.replace(",", " ");
                	ind = 0;
        			for (String ret: retval.split(" ")){
                		if (ind == 0) {
                			a = Integer.parseInt(ret);
                		}
                		else if (ind == 1){
                			b = Integer.parseInt(ret);
                		}
                		ind++;
                    }
        			Point posisiPlay = new Point(a, b);
            		set_posisi_play.add(posisiPlay);
            		
                }

                //read set_posisi_train
            	line = reader.readLine();
            	for (String retval: line.split(" ")){
        			retval = retval.substring(1,retval.length()-1);
        			retval = retval.replace(",", " ");
        			ind  = 0;
        			for (String ret: retval.split(" ")){
                		if (ind == 0) {
                			a = Integer.parseInt(ret);
                		}
                		else if (ind == 1){
                			b = Integer.parseInt(ret);
                		}
                		ind++;
                    }
        			System.out.println(""+a+""+b);
        			Point posisiTrain = new Point(a,b);
            		set_posisi_train.add(posisiTrain);
                }
            	
	        } catch (FileNotFoundException ex) {
	        	ex.printStackTrace();
	        } catch (IOException ex) {
	            ex.printStackTrace();	         
	        }finally {

	                try {
						reader.close();
		                fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

	        }
	}
	
	public static ConfigReader getInstance(){
		if (conf == null){
			conf = new ConfigReader();
		}
		return conf;
	}
	
	private ArrayList<Point> getSetPosisiPlay(){
		return set_posisi_play;	
	}
	
	private ArrayList<Point> getSetPosisiTrain(){
		return set_posisi_train;	
	}
	
	public int getBatasPenglihatan(){
		return batas_penglihatan;
	}

	public int getJumlahKeju(){
		return jumlahKeju;
	}
	
	public int getJumlahKucing(){
		return jumlahKucing;
	}
	
	/**
	 * @param 0 = posisi_play; 1 = posisi_train
	 * @return ArrayList<Point>
	 */
	public ArrayList<Point> getArrayPosisiTikus(int posisi){
		ArrayList<Point> resPosisi = new ArrayList<Point>();
		int Eps = 0;
		int ind = 0;
		
		if (posisi == posisi_play){
			ArrayList<Point> arrp = getSetPosisiPlay();
			while (Eps < 10){
				resPosisi.add(arrp.get(ind));
				Eps++;
				ind = ind + getJumlahKeju() + getJumlahKucing()+1;
				if (ind >= episode)	ind = ind % episode;
			}	
		}else if (posisi == posisi_train){
			ArrayList<Point> arrt = getSetPosisiTrain();
			while (Eps < 10){
				resPosisi.add(arrt.get(ind));
				Eps++;
				ind = ind + getJumlahKeju() + getJumlahKucing()+1;
				if (ind >= episode)	ind = ind % episode;
			}
		}
		return resPosisi;
	}
	
	/**
	 * @param 0 = posisi_play; 1 = posisi_train
	 * @return ArrayList<ArrayList<Point>>
	 */
	public ArrayList<ArrayList<Point>> getArrayPosisiKucing(int posisi){
		ArrayList<ArrayList<Point>> resPosisi = new ArrayList<ArrayList<Point>>();
		
		int Eps = 0;
		int ind = 0;

		if (posisi == posisi_play){
			ArrayList<Point> arrp = getSetPosisiPlay();
			while (Eps < 10){
				int i = 0;
				ArrayList<Point> Pos = new ArrayList<Point>();
				while (i < getJumlahKucing()){
					ind++;
					if (ind >= episode) ind = ind % episode;
					Pos.add(arrp.get(ind));
					i++;
				}
				resPosisi.add(Pos);
				Eps++;
				ind = ind + getJumlahKeju() + 1;
				if (ind >= episode)	ind = ind % episode;
			}	
		}else if (posisi == posisi_train){
			ArrayList<Point> arrp = getSetPosisiTrain();
			while (Eps < 10){
				int i = 0;
				ArrayList<Point> Pos = new ArrayList<Point>();
				while (i < getJumlahKucing()){
					ind++;
					if (ind >= episode) ind = ind % episode;
					Pos.add(arrp.get(ind));
					i++;
				}
				resPosisi.add(Pos);
				Eps++;
				ind = ind + getJumlahKeju() + 1;
				if (ind >= episode)	ind = ind % episode;
			}
		}
		return resPosisi;
	}
	
	
	/**
	 * @param 0 = posisi_play; 1 = posisi_train
	 * @return ArrayList<ArrayList<Point>>
	 */
	public ArrayList<ArrayList<Point>> getArrayPosisiKeju(int posisi){
		ArrayList<ArrayList<Point>> resPosisi = new ArrayList<ArrayList<Point>>();
		
		int Eps = 0;
		int ind = getJumlahKucing();

		if (posisi == posisi_play){
			ArrayList<Point> arrp = getSetPosisiPlay();
			while (Eps < 10){
				int i = 0;
				ArrayList<Point> Pos = new ArrayList<Point>();
				while (i < getJumlahKeju()){
					ind++;
					if (ind >= episode) ind = ind % episode;
					Pos.add(arrp.get(ind));
					i++;
				}
				resPosisi.add(Pos);
				Eps++;
				ind = ind +getJumlahKucing()+ 1;
				if (ind >= episode)	ind = ind % episode;
			}	
		}else if (posisi == posisi_train){
			ArrayList<Point> arrp = getSetPosisiTrain();
			while (Eps < 10){
				int i = 0;
				ArrayList<Point> Pos = new ArrayList<Point>();
				while (i < getJumlahKeju()){
					ind++;
					if (ind >= episode) ind = ind % episode;
					Pos.add(arrp.get(ind));
					i++;
				}
				resPosisi.add(Pos);
				Eps++;
				ind = ind + getJumlahKucing() + 1;
				if (ind >= episode)	ind = ind % episode;
			}
		}
		return resPosisi;
	}
}

