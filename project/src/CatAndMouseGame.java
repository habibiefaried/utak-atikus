import java.awt.*;
import java.util.Random;

import javax.swing.*;

public class CatAndMouseGame extends Thread {
    long delay;
    SwingApplet a;
    RLPolicy policy;
    CatAndMouseWorld world;

    static final int GREEDY = 0, SMART = 1; // type of mouse to use
    int mousetype = SMART;

    public boolean gameOn = false, single = false, gameActive, newInfo = false;

    public CatAndMouseGame(SwingApplet s, long delay, CatAndMouseWorld w, RLPolicy policy) {
        world = w;

        a = s;
        this.delay = delay;
        this.policy = policy;
    }

    /* Thread Functions */
    public void run() {
        System.out.println("--Game thread started");
        // start game
        try {
            while (true) {
                while (gameOn) {
                    gameActive = true;
                    resetGame();
                    SwingUtilities.invokeLater(a); // draw initial state
                    runGame();
                    gameActive = false;
                    newInfo = true;
                    SwingUtilities.invokeLater(a); // update state
                    sleep(delay);
                }
                sleep(delay);
            }
        } catch (InterruptedException e) {
            System.out.println("interrupted.");
        }
        System.out.println("== Game finished.");
    }

    public void runGame() {
        while (!world.endGame()) {
            // System.out.println("Game playing. Making move.");
            int action = -1;
            if (mousetype == GREEDY) {
                System.out.println("CatAndMouseGame.java:GREEDY:runGame");
                // action = policy.getBestAction(world.getStateBaru());
                action = world.mouseAction();

                // Random rnd = new Random();
                // action = rnd.nextInt(3);
                // switch (action) {
                // case 0: System.out.println("left"); break;
                // case 1: System.out.println("up"); break;
                // case 2: System.out.println("right"); break;
                // }
            } else if (mousetype == SMART) {
                System.out.println("CatAndMouseGame.java:SMART:runGame");
                // action = policy.getSmartAction(world.getStateBaru());
                action = policy.getBestAction(world.getStateBaru());
                // Random rnd = new Random();
                // action = rnd.nextInt(3);
                // switch (action) {
                // case 0: System.out.println("left"); break;
                // case 1: System.out.println("up"); break;
                // case 2: System.out.println("right"); break;
                // }
            } else {
                System.err.println("Invalid mouse type:" + mousetype);
            }
            // System.out.println(world.mx + ", " + world.my +
            // ", arah: "+world.arah);
            world.getNextState(action);
            a.mousescore = world.mousescore;

            // a.updateBoard();
            SwingUtilities.invokeLater(a);

            try {
                sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("interrupted.");
            }
        }
        a.totalscore += world.mousescore;
        // a.catscore += world.catscore;

        // turn off gameOn flag if only single game
        if (single)
            gameOn = false;
    }

    public void interrupt() {
        super.interrupt();
        System.out.println("(interrupt)");
    }

    /* end Thread Functions */

    public void setPolicy(RLPolicy p) {
        policy = p;
    }

    public Integer getArah() {
        return world.arah;
    }

    public Dimension getMouse() {
        return new Dimension(world.mx, world.my);
    }

    public Dimension getCat(int id) {
        // System.out.println("JUMLAH KUCING : --> "+ConfigReader.getInstance().getJumlahKucing());
        if (ConfigReader.getInstance().getJumlahKucing() > 0) {
            Point catPoint = world.catCoord.get(id);
            return new Dimension(catPoint.x, catPoint.y);
        } else
            return new Dimension();
    }

    // public Dimension getCheese() { return new Dimension(world.chx,
    // world.chy); }
    public Dimension getCheese(int id) {
        if (ConfigReader.getInstance().getJumlahKeju() > 0) {
            Point cheesePoint = world.cheeseCoord.get(id);
            return new Dimension(cheesePoint.x, cheesePoint.y);
        } else
            return new Dimension();
    }

    public Dimension getHole() {
        return new Dimension(world.hx, world.hy);
    }

    public boolean[][] getWalls() {
        return world.walls;
    }

    public void makeMove() {
        world.moveMouse();
        world.moveCat();
    }

    public void resetGame() {
        world.resetState(0);
    }
}
