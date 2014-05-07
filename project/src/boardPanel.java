import java.awt.*;

import javax.swing.*;

import sun.security.acl.WorldGroupImpl;

import java.util.*;

public class boardPanel extends JPanel {
	static final boolean USEVEC = true; // multiple painting per square (assume user paints each default)
	int xdim, ydim;
	int wdim, hdim,sqw, sqh;
	final Color background = Color.white;
	
	private CatAndMouseWorld world = null;
	
	boardObject[][] board; // objects on board, listed as array of boardObject elements
	Vector boardVec; // objects on board to paint, listed as vector in order of painting
	
	boardObject def;
	
	Dimension preferredSize;
	
	// d is a boardObject for the background.
	public boardPanel(boardObject d, int x, int y) { this(d,1,1,x,y); }
	public boardPanel(boardObject d, int x, int y, int w, int h) {
		//bObjects = b;
		def = d;
		xdim = x;
		ydim = y;
		wdim = w;
		hdim = h;
		
		sqw = wdim / xdim;
		sqh = hdim / ydim;
		
		//System.out.println("sqx:"+sqw+" sqy:"+sqh);
		
		preferredSize = new Dimension(wdim, hdim);
		
		board = new boardObject[xdim][ydim];

		boardVec = new Vector();  // this instance will probably get replaced,
									// but allows setting before clearing
	}
	
	public boolean setSquare(boardObject b, Dimension d) { return setSquare(b,d.width,	d.height); }
	public boolean setSquare(boardObject b, int x, int y) {
		if ((x<0) || (x>=xdim) || (y<0) || (y>=ydim)) return false; //out of range
		if (USEVEC) {
			boardContainer thisSquare = new boardContainer(new Dimension(x,y),b);
			boardVec.addElement(thisSquare);
		}
		else board[x][y] = b;
		return true;
	}
	
	public void clearBoard() {
		if (USEVEC) boardVec = new Vector();
		else {
			for (int i=0; i<xdim; i++) {
				for (int j=0; j<ydim; j++)
					board[i][j] = null;
			}
		}
	}
	
	public boolean clearSquare(int x, int y) {
		if ((x<0) || (x>=xdim) || (y<0) || (y>ydim)) return false; //out of range
		board[x][y] = null;
		return true;
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		drawboard(g);
	}
	
	public Dimension getPreferredSize() { return preferredSize;	}
	public Dimension getMaximumSize() {	return preferredSize; }
	public Dimension getMinimumSize() {	return preferredSize; }

	ArrayList<Point> allowed = new ArrayList<Point>();  
	
	private void buildAllowed() {
	    ConfigReader cr = ConfigReader.getInstance();
	    allowed.clear();
	    for (int i = 0; i < cr.getBatasPenglihatan() + 1; i++) {
	        if (i == 0) {
	            allowed.add(new Point(world.mx, world.my));
	        } else {
	            switch (world.arah) {
	                case 0:
	                    allowed.add(new Point(world.mx, world.my-i));
	                    break;
	                case 1:
	                    allowed.add(new Point(world.mx+i, world.my-i));
                        break;
	                case 2:
	                    allowed.add(new Point(world.mx+i, world.my));
                        break;
	                case 3:
	                    allowed.add(new Point(world.mx+i, world.my+i));
                        break;
	                case 4:
	                    allowed.add(new Point(world.mx, world.my+i));
                        break;
	                case 5:
	                    allowed.add(new Point(world.mx-i, world.my+i));
                        break;
	                case 6:
	                    allowed.add(new Point(world.mx-i, world.my));
                        break;
	                case 7:
	                    allowed.add(new Point(world.mx-i, world.my-i));
                        break;
	            }
	        }
        }
	}
	
	private boolean isInAllowed(int x, int y) {
	    for (int i = 0; i < allowed.size(); i++) {
            Point temp = allowed.get(i);
            if (x == temp.x && y == temp.y) {
                return true;
            }
        }
	    return false;
	}
	
	void drawboard(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0,0,getWidth(), getHeight());
		
		sqw = getWidth()/xdim;
		sqh = getHeight()/ydim;
		
		// draw background panels
		if (def != null) {
			for (int y=0; y<ydim; y++) {
				for (int x=0; x<xdim; x++) {
				    if (world != null) {
    				    buildAllowed();
                        if (isInAllowed(x, y)) {
                            def.drawObject(g, x, y, sqw, sqh, this);
                        }
				    } else {
				        def.drawObject(g, x, y, sqw, sqh, this);
				    }
//				    def.drawObject(g, x, y, sqw, sqh, this);
				}
			}
		}
		
		if (USEVEC) {
			// draw each element in the vector
			for (Enumeration e = boardVec.elements(); e.hasMoreElements();) {
				boardContainer c = (boardContainer) e.nextElement();
				if (c.o.getType() > 0) {
				    if (world != null) {
//				        buildAllowed();
//				        if (isInAllowed(c.d.width, c.d.height)) {
				            c.o.drawObject(g,c.d.width, c.d.height, sqw, sqh, this);
//				        }
				    } else {
				        c.o.drawObject(g,c.d.width, c.d.height, sqw, sqh, this);
				    }
				}
			}
		} else {
		    System.out.println("salvian");
			// draw from grid
			for (int y=0; y<ydim; y++) {
				for (int x
				        
				        =0; x<xdim; x++)
					if (board[x][y] != null) 
						board[x][y].drawObject(g, x, y, sqw, sqh, this);
			}
		}
	}

	public void setDimensions(int x, int y) {
		this.xdim = x;
		this.ydim = y;
		this.sqw = wdim / xdim;
		this.sqh = hdim / ydim;
		if (!USEVEC) board = new boardObject[xdim][ydim];
	}
	
	public String toString() {
		String retString="";
		for (int j=0;j<ydim; j++) {
			retString+="[";
			if (board[0][j] != null) retString += board[0][j].toString();
			else retString += "x";
			for (int i=1; i<xdim; i++) {
				retString+=",";
				if (board[i][j] != null) retString += board[i][j].toString();
				else retString += "x";
			}
			retString+=("]\n");
		}
		return retString;
	}
	
	public void setWorld(CatAndMouseWorld world) {
        this.world = world;
    }
	
	public CatAndMouseWorld getWorld() {
        return world;
    }
}

class boardContainer {
	public Dimension d;
	public boardObject o;
	
	public boardContainer(Dimension d, boardObject o) {
		this.d = d; this.o = o;
	}
}
