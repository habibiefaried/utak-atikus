import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ConfigReader {
	private int episode = 10;
	private int batas_penglihatan;
	private int c;
	private int k;
	private ArrayList<Point> set_posisi_play;
	private ArrayList<Point> set_posisi_train;
	public static ConfigReader conf;
	
	public ConfigReader() {
	        //reading file line by line in Java using BufferedReader      

	        FileInputStream fis = null;
	        BufferedReader reader = null;
	        String line;
            int ind = 0;
            set_posisi_play = new ArrayList<Point>();
            set_posisi_train= new ArrayList<Point>();
	        try {
	            fis = new FileInputStream("tabel.txt");
	            reader = new BufferedReader(new InputStreamReader(fis));       
	            System.out.println("Reading tabel");
	            
                //read batas penglihatan
                batas_penglihatan = Integer.parseInt(reader.readLine());
                
                //read c,k
                line = reader.readLine();
            	for (String retval: line.split(" ")){
            		if (ind == 0) {c = Integer.parseInt(retval);}
            		else if (ind == 1){k = Integer.parseInt(retval); }
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
	
	public ArrayList<Point> getSetPosisiPlay(){
		return set_posisi_play;	
	}
	
	public ArrayList<Point> getSetPosisiTrain(){
		return set_posisi_train;	
	}
	
	public int getBatasPenglihatan(){
		return batas_penglihatan;
	}

	public int getJumlahKeju(){
		return c;
	}
	
	public int getJumlahKucing(){
		return k;
	}
}

