import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class CatAndMouseWorld implements RLWorld {
    public int bx, by;

    public int mx, my;

    ArrayList<ArrayList<Point>> catPos = new ArrayList<ArrayList<Point>>();
    ArrayList<ArrayList<Point>> cheesePos = new ArrayList<ArrayList<Point>>();
    public ArrayList<Boolean> cheeseStatus = new ArrayList<Boolean>();
    public ArrayList<Point> catCoord = new ArrayList<Point>();
    public ArrayList<Point> cheeseCoord = new ArrayList<Point>();

    public int currentEpisode = 0;
    public int cx, cy;
    public int chx, chy;
    public int hx, hy;
    public boolean gotCheese = false;
    public int arah = 0;

    public int catscore = 0, mousescore = 0;
    public int cheeseReward, deathPenalty;
    static final int NUM_OBJECTS = 2, NUM_ACTIONS = 3, WALL_TRIALS = 100;
    static final double INIT_VALS = 0;

    int[] stateArrayBaru;
    double waitingReward;
    public boolean[][] walls;

    /* Kode tambahan */
    private void debug(String msg) {
        System.out.println("DEBUG : " + msg);
    }

    /* End of kode tambahan */

    public CatAndMouseWorld(int x, int y, int numWalls) {
        bx = x;
        by = y;
        makeWalls(x, y, numWalls);
        cheeseReward = x + y; // Kode tambahan
        deathPenalty = x + y; // Kode tambahan
        System.out.println("TRAINING...........");
        resetState(1);
    }

    public CatAndMouseWorld(int x, int y, boolean[][] newwalls) {
        bx = x;
        by = y;

        walls = newwalls;
        
        resetState(0);

        // cheeseCoord.clear();
        // Random rnd = new Random();
        // for (int i = 0; i < 3; i++) {
        // cheeseCoord.add(new Point(rnd.nextInt(8), rnd.nextInt(8)));
        // }
    }

    /******* RLWorld interface functions ***********/
    public int[] getDimension() {
        int[] retDim = new int[NUM_OBJECTS + 1];
        int i;
        for (i = 0; i < NUM_OBJECTS;) {
            retDim[i++] = bx;
            retDim[i++] = by;
        }
        retDim[i] = NUM_ACTIONS;
        // debug("X : " + bx + " Y : " + by);
        return retDim;
    }

    // given action determine next state
    public int[] getNextState(int action) {
        // action is mouse action: 0=u 1=ur 2=r 3=dr ... 7=ul
        Dimension d = getCoords(action);
        int ax = d.width, ay = d.height;
        if (legal(ax, ay)) {
            // move agent
            mx = ax;
            my = ay;
            if (action == 1) {
                mousescore--;
//                System.out.println("jalan maju" + mousescore);
            }
            // debug("MoveX : "+mx+" MoveY : "+my);
        } else {
            // System.err.println("Illegal action: "+action);
        }
        // update world
        moveCat();
        waitingReward = calcReward();

        // if mouse has cheese, relocate cheese
        if ((mx == chx) && (my == chy)) {
            d = getRandomPos();
            chx = d.width;
            chy = d.height;
        }

        /*
         * // if cat has mouse, relocate mouse if ((mx==cx) && (my==cy)) { d =
         * getRandomPos(); mx = d.width; my = d.height; }
         */

        return getStateBaru();
    }

    public double getReward(int i) {
        return getReward();
    }

    public double getReward() {
        return waitingReward;
    }

    public boolean validAction(int action) {
        Dimension d = getCoords(action);
        return legal(d.width, d.height);
    }

    Dimension getCoords(int action) {
        int ax = mx, ay = my;
        switch (action) {
        case 0:
            arah -= 1;
            if (arah < 0)
                arah = 7;
            break;
        case 1:
            switch (arah) {
            case 0:
                ay = my - 1;
                break;
            case 1:
                ay = my - 1;
                ax = mx + 1;
                break;
            case 2:
                ax = mx + 1;
                break;
            case 3:
                ay = my + 1;
                ax = mx + 1;
                break;
            case 4:
                ay = my + 1;
                break;
            case 5:
                ay = my + 1;
                ax = mx - 1;
                break;
            case 6:
                ax = mx - 1;
                break;
            case 7:
                ay = my - 1;
                ax = mx - 1;
                break;
            }
            break;
        case 2:
            arah += 1;
            if (arah > 7)
                arah = 0;
            break;
        default: // System.err.println("Invalid action: "+action);
        }
        return new Dimension(ax, ay);
    }

    // find action value given x,y=0,+-1
    int getAction(int x, int y) {
        System.out.println("CatAndMouseWorld.java:getAction");
        int[][] vals = { { 0 }, { 1 }, { 2 } };
        if ((x < -1) || (x > 1) || (y < -1) || (y > 1) || ((y == 0) && (x == 0)))
            return -1;
        int retVal = vals[y + 1][x + 1];
        return retVal;
    }

    public boolean endState() {
        return endGame();
    }

    public int[] resetState(int play_or_train) {
        catscore = 0;
        mousescore = 0;
        // setRandomPos();//set random position
        setPosFromFile(play_or_train);
        return getStateBaru();
    }

    public double getInitValues() {
        return INIT_VALS;
    }

    /******* end RLWorld functions **********/
    public ArrayList<Point> allowed = new ArrayList<Point>();
    public int distanceObject;
    public int typeObject;
    
    public void getAllowed() {
        ConfigReader cr = ConfigReader.getInstance();
        allowed.clear() ;
        for (int i = 0; i < cr.getBatasPenglihatan() + 1; i++) {
            if (i == 0) {
                allowed.add(new Point(mx, my));
            } else {
                switch (arah) {
                case 0:
                    allowed.add(new Point(mx, my - i));
                    break;
                case 1:
                    allowed.add(new Point(mx + i, my - i));
                    break;
                case 2:
                    allowed.add(new Point(mx + i, my));
                    break;
                case 3:
                    allowed.add(new Point(mx + i, my + i));
                    break;
                case 4:
                    allowed.add(new Point(mx, my + i));
                    break;
                case 5:
                    allowed.add(new Point(mx - i, my + i));
                    break;
                case 6:
                    allowed.add(new Point(mx - i, my));
                    break;
                case 7:
                    allowed.add(new Point(mx - i, my - i));
                    break;
                }
            }
        }
    }
    
    public void getNearestObject() {
        for (int i = 0; i < allowed.size(); i++) {
            Point poin = allowed.get(i);
            //cek kucing
            for (int j = 0; j < ConfigReader.getInstance().getJumlahKucing(); j++) {
                if (catCoord.get(j).x == poin.x && catCoord.get(j).y == poin.y) {
                    distanceObject = i;
                    typeObject = 3;
                    
                    System.out.println("ADA KUCING:");
                    System.out.println(poin.x + ", " + poin.y);
                    return;
                }
            }
            //cek keju            
            for (int j = 0; j < ConfigReader.getInstance().getJumlahKeju(); j++) {
                if (cheeseCoord.get(j).x == poin.x && cheeseCoord.get(j).y == poin.y && !cheeseStatus.get(j)) {
                    distanceObject = i;
                    typeObject = 2;
                    System.out.println("ADA KEJU:");
                    System.out.println(poin.x + ", " + poin.y);
                    return;
                }
            }
            
            //cek tembok
            if (isInbound(poin)) {
                if (MapReader.wallsMap[poin.x][poin.y]) {
                    distanceObject = i;
                    typeObject = 4;
                    System.out.println("ADA TEMBOK :");
                    System.out.println(poin.x + ", " + poin.y);
                    return;
                }
            } else {
                distanceObject = i;
                typeObject = 6;
                return;
            }
        }        
        distanceObject = 0;
        typeObject = 0;
    }
        
    public boolean isInbound(Point poin) {
        return poin.x >= 0 && poin.y >= 0 && poin.x < MapReader.jmlKolom && poin.y < MapReader.jmlBaris;
    }
    
    
    public int[] getStateBaru() {
//         translates current state into int array
//        NUM_OBJECTS = 2 + ConfigReader.getInstance().getJumlahKucing() * 2 + ConfigReader.getInstance().getJumlahKeju() * 2;
        stateArrayBaru = new int[NUM_OBJECTS];
//        stateArray[0] = mx;
//        stateArray[1] = my;
//        for (int i = 0; i < ConfigReader.getInstance().getJumlahKucing(); i++) {
//            stateArray[2*i+2] = catCoord.get(i).x;
//            stateArray[2*i+3] = catCoord.get(i).y;
//        }
//        for (int i = 0; i < ConfigReader.getInstance().getJumlahKeju(); i++) {
//            stateArray[2*i+(2 + ConfigReader.getInstance().getJumlahKucing() * 2)] = cheeseCoord.get(i).x;
//            stateArray[2*i+(3 + ConfigReader.getInstance().getJumlahKucing() * 2)] = cheeseCoord.get(i).y;
//        }
        getAllowed();
        getNearestObject();
        stateArrayBaru[0] = distanceObject; // jarak ke benda 
        stateArrayBaru[1] = typeObject; // jenis benda
        
//        System.out.println("----");
//        System.out.println(stateArrayBaru[0]);
//        System.out.println(stateArrayBaru[1]);
//        System.out.println("----");
        
        //kucing = 3
        //keju = 2
        //tembok = 4
        
        return stateArrayBaru;
    }

    public double calcReward() {
        double newReward = 0;

        for (int i = 0; i < ConfigReader.getInstance().getJumlahKeju(); i++) {
            if (cheeseCoord.get(i).x == mx && cheeseCoord.get(i).y == my && !cheeseStatus.get(i)) {
                mousescore += (MapReader.jmlBaris + MapReader.jmlKolom);
                newReward += cheeseReward;
                cheeseStatus.set(i, true);
                System.out.println(":) dapet keju : " + mousescore);
            }
        }
        for (int i = 0; i < ConfigReader.getInstance().getJumlahKucing(); i++) {
            if (catCoord.get(i).x == mx && catCoord.get(i).y == my) {
                mousescore -= (MapReader.jmlBaris + MapReader.jmlKolom);
                newReward -= deathPenalty;
                System.out.println(":( Ketemu kucing : " + mousescore);
            }
        }

        // if ((mx == chx) && (my == chy)) {
        // // mousescore++;
        // newReward += cheeseReward;
        // }
        // if ((cx == mx) && (cy == my)) {
        // catscore++;
        // newReward -= deathPenalty;
        // }
        // if ((mx==hx)&&(my==hy)&&(gotCheese)) newReward += 100;
        return newReward;
    }

    public void setRandomPos() {
        Dimension d = getRandomPos();
        cx = d.width;
        cy = d.height;

        d = getRandomPos();
        mx = d.width;
        my = d.height;
        d = getRandomPos();
        chx = d.width;
        chy = d.height;
        d = getRandomPos();
        hx = d.width;
        hy = d.height;
    }

    public void setPosFromFile(int play_or_train) {
        ConfigReader conf = ConfigReader.getInstance();
        Dimension d = getRandomPos();
        Point p;

        catPos = ConfigReader.getInstance().getArrayPosisiKucing(play_or_train);
        catCoord.clear();
        for (int i = 0; i < ConfigReader.getInstance().getJumlahKucing(); i++) {
            Point baru = (Point) catPos.get(currentEpisode).get(i).clone();
            baru.x -= 1;
            baru.y -= 1;
            catCoord.add(baru);
        }

        cheesePos = ConfigReader.getInstance().getArrayPosisiKeju(play_or_train);
        cheeseCoord.clear();
        cheeseStatus.clear();
        for (int i = 0; i < ConfigReader.getInstance().getJumlahKeju(); i++) {
            Point baru = (Point) cheesePos.get(currentEpisode).get(i).clone();
            baru.x -= 1;
            baru.y -= 1;
            cheeseCoord.add(baru);
            cheeseStatus.add(false);
        }

        // p = conf.getArrayPosisiKucing(0).get(0).get(0);
        // cx = (int) p.getX();
        // cy = (int) p.getY();
        p = conf.getArrayPosisiTikus(0).get(0);
        mx = (int) p.getX() - 1;
        my = (int) p.getY() - 1;
        // p = conf.getArrayPosisiKeju(0).get(0).get(0);
        // chx = (int) p.getX();
        // chy = (int) p.getY();

        d = getRandomPos();
        hx = d.width;
        hy = d.height;
    }

    boolean legal(int x, int y) {
        if ((x >= 0) && (x < bx) && (y >= 0) && (y < by))
            if (walls[x][y]) {
                mousescore -= 2;
                //System.out.println("Tabrak tembok :" + mousescore);
            }
        return ((x >= 0) && (x < bx) && (y >= 0) && (y < by)) && (!walls[x][y]);
    }

    boolean endGame() {
        // return (((mx==hx)&&(my==hy)&& gotCheese) || ((cx==mx) && (cy==my)));
//         return ((cx == mx) && (cy == my));
        for (int i = 0; i < ConfigReader.getInstance().getJumlahKucing(); i++) {
            if (catCoord.get(i).x == mx && catCoord.get(i).y == my) {
                System.out.println("Game OVER");
                System.out.println(ConfigReader.getInstance().getJumlahEpisode());
                if (currentEpisode < ConfigReader.getInstance().getJumlahEpisode() - 1) {
                    currentEpisode++;
                } else {
                    currentEpisode = 0;
                }
                return true;
            }
        }
        boolean allcheese = true;
        for (int i = 0; i < cheeseStatus.size(); i++) {
            if (!cheeseStatus.get(i)) {
                allcheese = false;
                break;
            }
        }
        if (allcheese) {
            System.out.println("Game OVER");
            System.out.println(ConfigReader.getInstance().getJumlahEpisode());
            System.out.println("sonny");
            if (currentEpisode < ConfigReader.getInstance().getJumlahEpisode() - 1) {
                currentEpisode++;
            } else {
                currentEpisode = 0;
            }
            System.out.println("david");
            return true;
        }
        return false;
    }

    Dimension getRandomPos() {
        int nx, ny;
        nx = (int) (Math.random() * bx);
        ny = (int) (Math.random() * by);
        for (int trials = 0; (!legal(nx, ny)) && (trials < WALL_TRIALS); trials++) {
            nx = (int) (Math.random() * bx);
            ny = (int) (Math.random() * by);
        }
        return new Dimension(nx, ny);
    }

    /******** heuristic functions ***********/
    Dimension getNewPos(int x, int y, int tx, int ty) {
        int ax = x, ay = y;

        if (tx == x)
            ax = x;
        else
            ax += (tx - x) / Math.abs(tx - x); // +/- 1 or 0
        if (ty == y)
            ay = y;
        else
            ay += (ty - y) / Math.abs(ty - y); // +/- 1 or 0

        // check if move legal
        if (legal(ax, ay))
            return new Dimension(ax, ay);

        // not legal, make random move
        while (true) {
            // will definitely exit if 0,0
            ax = x;
            ay = y;
            ax += 1 - (int) (Math.random() * 3);
            ay += 1 - (int) (Math.random() * 3);

            // System.out.println("old:"+x+","+y+" try:"+ax+","+ay);
            if (legal(ax, ay))
                return new Dimension(ax, ay);
        }
    }

    void moveCat() {
        // Dimension newPos = getNewPos(cx, cy, mx, my);
        // cx = newPos.width;
        // cy = newPos.height;
    }

    void moveMouse() {
        Dimension newPos = getNewPos(mx, my, chx, chy);
        mx = newPos.width;
        my = newPos.height;
    }

    int mouseAction() {
        int[] state = getStateBaru();
//        Dimension newPos = getNewPos(mx, my, chx, chy);
//        return getAction(newPos.width - mx, newPos.height - my);
        if (state[1] == 3) {
            if (state[0] == 1) {
                Random rn = new Random();
                int tes = rn.nextInt(10);
                if (tes % 2 == 0) {
                    return 0;
                } else {
                    return 2;
                }
            }
        }
        
        if (state[1] == 2) {
            return 1;
        }
        
        if (state[1] == 4) {
            if (state[0] == 1) { 
                Random rn = new Random();
                int tes = rn.nextInt(10);
                if (tes % 2 == 0) {
                    return 0;
                } else {
                    return 2;
                }
            }
        }
        
        if (state[1] == 6) {
            if (state[0] == 1) {
                Random rn = new Random();
                int tes = rn.nextInt(10);
                if (tes % 2 == 0) {
                    return 0;
                } else {
                    return 2;
                }
            }
        }   
        
        Random rn = new Random();
        int tes = rn.nextInt(10);
        if (tes <= 3) {
            return 0;
        } else if (tes <= 6){
            return 2;
        } else {
            return 1;
        }
        
    }

    /******** end heuristic functions ***********/

    /******** wall generating functions **********/
    void makeWalls(int xdim, int ydim, int numWalls) {
        walls = new boolean[xdim][ydim];

        // loop until a valid wall set is found
        for (int t = 0; t < WALL_TRIALS; t++) {
            // clear walls
            for (int i = 0; i < walls.length; i++) {
                for (int j = 0; j < walls[0].length; j++)
                    walls[i][j] = false;
            }

            float xmid = xdim / (float) 2;
            float ymid = ydim / (float) 2;

            // randomly assign walls.
            for (int i = 0; i < numWalls; i++) {
                Dimension d = getRandomPos();

                // encourage walls to be in center
                double dx2 = Math.pow(xmid - d.width, 2);
                double dy2 = Math.pow(ymid - d.height, 2);
                double dropperc = Math.sqrt((dx2 + dy2) / (xmid * xmid + ymid * ymid));
                if (Math.random() < dropperc) {
                    // reject this wall
                    i--;
                    continue;
                }

                walls[d.width][d.height] = true;
            }

            // check no trapped points
            if (validWallSet(walls))
                break;

        }

    }

    boolean validWallSet(boolean[][] w) {
        // copy array
        boolean[][] c;
        c = new boolean[w.length][w[0].length];

        for (int i = 0; i < w.length; i++) {
            for (int j = 0; j < w[0].length; j++)
                c[i][j] = w[i][j];
        }

        // fill all 8-connected neighbours of the first empty
        // square.
        boolean found = false;
        search: for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[0].length; j++) {
                if (!c[i][j]) {
                    // found empty square, fill neighbours
                    fillNeighbours(c, i, j);
                    found = true;
                    break search;
                }
            }
        }

        if (!found)
            return false;

        // check if any empty squares remain
        for (int i = 0; i < c.length; i++) {
            for (int j = 0; j < c[0].length; j++)
                if (!c[i][j])
                    return false;
        }
        return true;
    }

    void fillNeighbours(boolean[][] c, int x, int y) {
        c[x][y] = true;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++)
                if ((i >= 0) && (i < c.length) && (j >= 0) && (j < c[0].length) && (!c[i][j]))
                    fillNeighbours(c, i, j);
        }
    }
    /******** wall generating functions **********/

}
