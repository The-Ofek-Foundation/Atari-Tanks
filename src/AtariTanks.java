/*	Ofek Gila
	March 14th, 2014
	AtariTanks.java
	This program will play the retro "Atari Tanks" game
*/
import java.awt.*;			// Imports
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class AtariTanks	extends JApplet{			// I'm pretty sure I copied down one of your online codes for key and focus listeners for their methods
	
	public JFrame frame;
	
	public static void main(String[] args) {	// when I made snake.java, and I copied snake.java to have all the implements for this code, so don't
		AtariTanks GUIT = new AtariTanks();
		GUIT.run();
	}
	public void run(){
		frame = new JFrame("Atari Tank Game");	// ask why I extend JApplet or implement all of those things ^_^
		frame.setContentPane(new AtariTankPanel());
		frame.setSize(width(760), height(468));		// Sets size of frame
		frame.setResizable(true);						// Makes it so you can't resize the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public int width(int w) {
		return w + 12;
	}
	public int height(int h) {
		return h + 31;
	}
	public void init()	{
		setContentPane(	new AtariTankPanel());
	}
}
class AtariTankPanel extends TemplatePanel	{
	public int pixel = 5;
	public final int wallWidth = 4;
	public final int setWidth = 750, setHeight = 450;
	private PrintWriter output;
	private File[]	saves;
	private Scanner input;
	public boolean[][][] tankShapes;
	public char[][] map;
	public boolean[][] weakWallHere;
	public int tankValues[][];	// shape, y, x, symbol, bulletX, bulletY, speedX, speedY, deaths, killed by, bounces, spins, moreSpin, base time
	public Timer bulletTimer, deathTimer, soundTimer, baseControlTimer;
	public BulletShot BS;
	public DeathSpin DS;
	public SoundTimer ST;
	public BaseControlTimer BCT;
	public boolean bounce = true;
	public boolean editLevels = false;
	public int editLevelNumber = 0;
	public boolean wallPaint = false;
	public boolean playMoveSound = true;
	public double movingST, movingET;
	public boolean stop;
	public boolean kingOfTheHill;
	public boolean[][] base;
	public int baseControl; //0 = none, red, blue, both
	
	private class BaseControlTimer implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			for (int tankNum = 0; tankNum < 2; tankNum++)
				if (baseControl == tankNum+1)	tankValues[tankNum][13]++;
			repaint();
		}
	}
	private class SoundTimer implements ActionListener	{
		public void actionPerformed(ActionEvent e) {
			//System.out.println((System.nanoTime() / 1E2 - movingST / 1E2) + " " + movingET);
			if ((System.nanoTime() / 1E3 - movingST / 1E3) > movingET && !(playMoveSound)) {
				playMoveSound = true;
				soundTimer.stop();
			}
		}
	}
	private class BulletShot implements ActionListener	{
		public void actionPerformed(ActionEvent e) {
			if (tankValues[0][4] == -1 && tankValues[1][4] == -1) {
				bulletTimer.stop();
				return;	
			}
			moveBullet();
			repaint();
		}
		public void breakWalls(int x, int y) {
			deleteWall(x, y);
			deleteWall(x-1,y);
			deleteWall(x+1,y);
			deleteWall(x,y+1);
			deleteWall(x,y-1);
		}
		public void deleteWall(int x, int y) {
			if (map[x][y] == 'w') map[x][y] = ' ';
		}
		public void moveBullet() {
			for (int tankNum = 0; tankNum < 2; tankNum++)
				if (tankValues[tankNum][4] != -1) {
					if (map[tankValues[tankNum][4]][tankValues[tankNum][5]]	!= 'W')	map[tankValues[tankNum][4]][tankValues[tankNum][5]] = ' ';
					tankValues[tankNum][4] += tankValues[tankNum][6];
					tankValues[tankNum][5] += tankValues[tankNum][7];
					if (tankValues[tankNum][4] < 0 || tankValues[tankNum][4] > map.length - 1 || tankValues[tankNum][5] < 0 || tankValues[tankNum][5] > map[0].length - 1) {
						tankValues[tankNum][4] = -1;
					}
					else if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'W' || map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'w') {
						if (bounce && tankValues[tankNum][10] < 4)
							bounce(tankNum);
						else	{
							if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'w' )	breakWalls(tankValues[tankNum][4], tankValues[tankNum][5]);
							tankValues[tankNum][10] = 0;
							tankValues[tankNum][4] = -1;
						}
					}
					else if (tankValues[0][4] == tankValues[1][4] && tankValues[0][5] == tankValues[1][5]) {		// bullet hits bullet
						map[tankValues[0][4]][tankValues[0][5]] = ' ';
						tankValues[0][4] = -1;
						tankValues[1][4] = -1;
					}
					else if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'R') {
						tankValues[0][8]++;
						tankValues[0][11] = 0;
						killTank(0, tankNum);
						tankValues[tankNum][4] = -1;
					}
					else if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'B') {
						tankValues[1][8]++;
						tankValues[1][11] = 0;
						killTank(1, tankNum);
						tankValues[tankNum][4] = -1;
					}
					else	map[tankValues[tankNum][4]][tankValues[tankNum][5]] = (char)tankValues[tankNum][3];
					
				}
		}
		public double freeSpace(int mx, int my, int tankNum) {
			int count = 0;
			int tx = tankValues[tankNum][4];
			int ty = tankValues[tankNum][5];
			while (true) {
				try	{
					if (map[tx][ty] != 'W') break;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					//count = 100;
					break;
				}
				tx+=mx;
				ty+=my;
				count++;
			}
			return count;
		}
		public void bounce(int tankNum) {
			int wall = 0;
			tankValues[tankNum][10]++;
			/*int direction1, direction2;
			double dRight, dDown, dLeft, dUp;	// distance
			double mx = Math.abs(tankValues[tankNum][7]);
			double my = Math.abs(tankValues[tankNum][7]);
			if (tankValues[tankNum][6] < 0)	direction1 = 0;
			else if (tankValues[tankNum][6] == 0)	direction1 = 1;
			else direction1 = 2;
			if (tankValues[tankNum][7] < 0)	direction2 = 0;
			else if (tankValues[tankNum][7] == 0)	direction2 = 1;
			else direction2 = 3;
			dRight = freeSpace(1, 0, tankNum) / mx;
			dLeft = freeSpace(-1, 0, tankNum) / mx;
			dDown = freeSpace(0, 1, tankNum) / my;
			dUp = freeSpace(0, -1, tankNum) / my;
			double delta;
			switch (direction1) {
				case 0:	switch (direction2) {
							case 0:	delta = dDown - dRight;
									if (delta == 0)	{
										tankValues[tankNum][6] *= -1;
										tankValues[tankNum][7] *= -1; break;
									}
									if (delta > 0)	{
										tankValues[tankNum][7] *= -1; break;
									}
									tankValues[tankNum][6] *= -1; break;
							case 1: tankValues[tankNum][6] *= -1; break;
							case 2:	delta = dDown - dLeft;
									if (delta == 0)	{
										tankValues[tankNum][6] *= -1;
										tankValues[tankNum][7] *= -1; break;
									}
									if (delta > 0)	{
										tankValues[tankNum][7] *= -1; break;
									}
									tankValues[tankNum][6] *= -1; break;
						}
						break;
				case 1:	tankValues[tankNum][7] *= -1; break;
				case 2:	switch (direction2) {
							case 0:	delta = dUp - dRight;
									if (delta == 0)	{
										tankValues[tankNum][6] *= -1;
										tankValues[tankNum][7] *= -1; break;
									}
									if (delta > 0)	{
										tankValues[tankNum][7] *= -1; break;
									}
									tankValues[tankNum][6] *= -1; break;
							case 1: tankValues[tankNum][6] *= -1; break;
							case 2:	delta = dUp - dLeft;
									if (delta == 0)	{
										tankValues[tankNum][6] *= -1;
										tankValues[tankNum][7] *= -1; break;
									}
									if (delta > 0)	{
										tankValues[tankNum][7] *= -1; break;
									}
									tankValues[tankNum][6] *= -1; break;
						}
			}*/
			/*if (tankValues[tankNum][5] > map.length - wallWidth - 1) wall = 0;
			if (tankValues[tankNum][4] > map[0].length - wallWidth - 1) wall = 1;
			if (tankValues[tankNum][5] < wallWidth) wall = 2;
			if (tankValues[tankNum][4] < wallWidth) wall = 3;*/
			tankValues[tankNum][4] -= tankValues[tankNum][6];
			tankValues[tankNum][5] -= tankValues[tankNum][7];
			if (map[tankValues[tankNum][4] + tankValues[tankNum][6]][tankValues[tankNum][5]] == 'W' || map[tankValues[tankNum][4] + tankValues[tankNum][6]][tankValues[tankNum][5]] == 'w') wall = 0;
			else wall = 1;
			switch (wall) {
				case 0: tankValues[tankNum][6] *= -1; break;
				case 1: tankValues[tankNum][7] *= -1; break;
			}
			moveBullet();
		}
	}
	private class DeathSpin implements ActionListener	{
		public void actionPerformed(ActionEvent e) {
			spinDead();
			removeFoam();
			repaint();
		}
		public void spinDead()	{
			for (int tankNum = 0; tankNum < 2; tankNum++)
				if (tankValues[tankNum][9] == -1);
				else if (tankValues[tankNum][12] == 0)	{
					tankValues[tankNum][11]++;
					turnTank(tankNum, 1);
					createFoam();
					moveTank(tankNum, 1, tankValues[tankValues[tankNum][9]][6], tankValues[tankValues[tankNum][9]][7]);
					if (tankValues[(tankNum + 1) % 2][12] == -1 && tankValues[(tankNum + 1) % 2][9] != -1)	deathTimer.setDelay(20);
				}
				else if (tankValues[tankNum][11] < 80 + Math.random() * 16)	{
					deathTimer.setDelay(20);
					if (tankValues[(tankNum + 1) % 2][12] == 0 && tankValues[(tankNum + 1) % 2][9] != -1) deathTimer.setDelay(40);
					tankValues[tankNum][11]++;
					turnTank(tankNum, 1);
				}
				else {
					if (tankValues[(tankNum + 1) % 2][12] == 0) deathTimer.setDelay(40);
					if (tankValues[(tankNum + 1) % 2][9] == -1) deathTimer.stop();
					tankValues[tankNum][12] = 0;
					tankValues[tankNum][9] = -1;
				}
		}
		public void createFoam()	{
			for (int i = 0; i < map.length; i++)
				for (int a = 0; a < map[i].length; a++)
					//if (touchingWall(i, a) && map[i][a] == ' ') map[i][a] = 'F';
					if (map[i][a] == ' ' && (i < wallWidth + 1 || a < wallWidth + 1 || i > map.length - wallWidth - 1 || a > map[i].length - wallWidth - 1)) map[i][a] = 'F';
		}
		public boolean touchingWall(int x, int y) {
			try {
				if (map[x+1][y] == 'W') return true;
				if (map[x-1][y] == 'W') return true;
				if (map[x][y+1] == 'W') return true;
				if (map[x][y-1] == 'W') return true;
			}
			catch (ArrayIndexOutOfBoundsException e)	{
				return true;
			}
			return false;
		}
		public void removeFoam()	{
			for (int i = 0; i < map.length; i++)
				for (int a = 0; a < map[i].length; a++)
					if (map[i][a] == 'F') map[i][a] = ' ';
		}
	}
	public void startup()	{
		tankShapes = new boolean[16][8][8];
		setBackground(new Color(145, 193, 101));

		BS = new BulletShot();
		DS = new DeathSpin();
		ST = new SoundTimer();
		BCT = new BaseControlTimer();
		bulletTimer = new Timer(20, BS);
		deathTimer = new Timer(40, DS);
		soundTimer = new Timer(10, ST);
		baseControlTimer = new Timer(1000, BCT);

		//saves = new File[4
		int i;
		for (i = 0; ; i++)
			if (!(new File("AtariTanksLevel" + (i + 1) + ".txt").exists()))
				break;
		saves = new File[i];
		for (int a = 0; a < i; a++)
			saves[a] = new File("AtariTanksLevel" + (a + 1) + ".txt");

		//saves[0] = new File("AtariTanksLevel1.txt");
		//saves[1] = new File("AtariTanksLevel2.txt");
		//saves[2] = new File("AtariTanksLevel3.txt");
		createTankShapes();
	}
	public void createTankShapes()	{
		boolean[][] position1 	= 	{	{	false,	false,	false,	false,	false,	false,	false,	false	},
										{	true,	true,	true,	true,	true,	true,	false,	false	},
										{	true,	true,	true,	true,	true,	true,	false,	false	},
										{	false,	false,	true,	true,	true,	false,	false,	false	},
										{	false,	false,	true,	true,	true,	true,	true,	true		},
										{	false,	false,	true,	true,	true,	false,	false,	false	},
										{	true,	true,	true,	true,	true,	true,	false,	false	},
										{	true,	true,	true,	true,	true,	true,	false,	false	}	};
		boolean[][] position2	=	{	{	false,	false,	false,	true,	true,	true,	false,	false	},
										{	false,	true,	true,	true,	true,	false,	false,	false	},
										{	true,	true,	true,	true,	true,	false,	true,	true		},
										{	false,	true,	true,	true,	true,	true,	false,	false	},
										{	false,	false,	false,	true,	true,	true,	false,	false	},
										{	false,	false,	false,	true,	true,	true,	true,	true		},
										{	false,	false,	true,	true,	true,	true,	true,	false	},
										{	false,	false,	false,	true,	true,	false,	false,	false	}	};
		boolean[][]	position3 	=	{	{	false,	false,	false,	true,	false,	false,	true,	false	},
										{	false,	false,	true,	true,	false,	true,	false,	false	},
										{	false,	true,	true,	true,	true,	false,	false,	false	},
										{	true,	true,	true,	true,	true,	true,	true,	false	},
										{	true,	false,	false,	true,	true,	true,	true,	false	},
										{	false,	false,	false,	false,	true,	true,	false,	false	},
										{	false,	false,	false,	true,	true,	false,	false,	false	},
										{	false,	false,	false,	true,	false,	false,	false,	false	}	};
		boolean[][] position4 = new boolean[position1.length][position1[0].length];
		position4 = AK.rotateClockWise(position2);
		position2 = AK.mirrorVertically(position2);
		position3 = AK.rotateClockWise(position3);
		for (int i = 0; i < 4; i++)	{
			setTankPosition(i*4, position1);
			setTankPosition(i*4+1, position2);
			setTankPosition(i*4+2, position3);
			setTankPosition(i*4+3, position4);
			position1 = AK.rotateClockWise(position1);
			position2 = AK.rotateClockWise(position2);
			position3 = AK.rotateClockWise(position3);
			position4 = AK.rotateClockWise(position4);
		}
		//for (int a = 0; a < 16; a++)
		//	AK.convertToCharAndOutput(tankShapes[a]);
	}
	public void setTankPosition(int pos, boolean[][] position)	{
		for (int i = 0; i < position.length; i++)
			for (int a = 0; a < position.length; a++)	
				tankShapes[pos][i][a] = position[i][a];
	}
	public void constructor()	{
		map = new char[90][150];
		tankValues = new int[2][14];
		weakWallHere = new boolean[map.length][map[0].length];
		base = new boolean[map.length][map[0].length];
		baseControl = 0;
		stopTimers();

		stop = false;

		tankValues[0][0] = 0;
		tankValues[0][1] = map.length / 2 - 4;
		tankValues[0][2] = 5;
		tankValues[0][3] = 'R';
		tankValues[0][4] = -1;
		tankValues[0][5] = -1;
		tankValues[0][6] = 0;
		tankValues[0][7] = 0;
		tankValues[0][8] = 0;
		tankValues[0][9] = -1;
		tankValues[0][10] = 0;
		tankValues[0][11] = 0;
		tankValues[0][12] = 0;
		tankValues[0][13] = 0;

		tankValues[1][0] = 8;
		tankValues[1][1] = map.length / 2 - 4;
		tankValues[1][2] = map[0].length - 13;
		tankValues[1][3] = 'B';
		tankValues[1][4] = -1;
		tankValues[1][5] = -1;
		tankValues[1][6] = 0;
		tankValues[1][7] = 0;
		tankValues[1][8] = 0;
		tankValues[1][9] = -1;
		tankValues[1][10] = 0;
		tankValues[1][11] = 0;
		tankValues[1][12] = 0;
		tankValues[1][13] = 0;

		try {
			changePixelSize();
		}
		catch (NullPointerException e) {}
		//for (int i = 0; i < map.length; i++)
		//	for (int a = 0; a < map[i].length; a++) {
		//		if (i < wallWidth || i > map.length - wallWidth - 1 || a < wallWidth || a > map[i].length - wallWidth - 1) map[i][a] = 'W';
		//		else	map[i][a] = ' ';
		//	}
		if (Math.random()*2 < 1) bounce = true;	else bounce  = false;
		if (Math.random()*2 < 1) kingOfTheHill = true;	else kingOfTheHill  = false;
		for (int i = 0; i <weakWallHere.length; i++)
			for (int a = 0; a < weakWallHere[i].length; a++)
				base[i][a] = false;

		load((int)(Math.random()*saves.length));
		for (int i = 0; i <weakWallHere.length; i++)
			for (int a = 0; a < weakWallHere[i].length; a++)
				if (map[i][a] == 'w') weakWallHere[i][a] = true;
				else weakWallHere[i][a] = false;
		//map[50][50] = 'f';
		if (editLevels) return;
		for (int tankNum = 0; tankNum < 2; tankNum++)
			AK.mergeArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], (char)tankValues[tankNum][3]);
		if (kingOfTheHill)	baseControlTimer.start();
	}
	public void stopTimers() {
		deathTimer.stop();
		bulletTimer.stop();
		soundTimer.stop();
		baseControlTimer.stop();
	}
	public void changePixelSize() {
		width = getWidth();
		height = getHeight();
		pixel = 1;
		while (true) {
			if (width / pixel < map[0].length || height / pixel < map.length) break;
			pixel++;
		}
		pixel--;
	}
	public int getRelativeX(double x)	{
		return (int)((x / width) * setWidth + 0.5);
	}
	public int getRelativeY(double y)	{
		return (int)((y / height) * setHeight + 0.5);
	}
	public void paintComponent(Graphics a)	{
		if (!(hasFocus()))	requestFocus();
		super.paintComponent(a);
		g = a;
		if (veryFirstTime)	{
			startup();
			veryFirstTime = false;
			initial = true;
		}
		if (initial)	{
			initial = false;
			constructor();
		}
		if (!(veryFirstTime))	drawMap();
	}
	public void drawMap()	{
		//AK.outputArray(map);
		for (int i = 0; i < map.length; i++)
			for (int a = 0; a < map[i].length; a++)
				if 		(map[i][a] == 'R')	drawRedPixel(i*pixel + pixel, a*pixel + pixel);
				else if (map[i][a] == 'B')	drawBluePixel(i*pixel + pixel, a*pixel + pixel);
				else if ((base[i][a] || map[i][a] == 'H') && (kingOfTheHill || editLevels))	drawBasePixel(i*pixel + pixel, a*pixel + pixel);
				else if (map[i][a] == 'W')	drawWallPixel(i*pixel + pixel, a*pixel + pixel);
				else if (map[i][a] == 'w')	drawWeakWallPixel(i*pixel + pixel, a*pixel + pixel);
				else if (map[i][a] == 'f')	drawFillWalls(i*pixel + pixel, a*pixel + pixel);
		g.setFont(new Font("Arial", Font.ITALIC, 30));
		if (!(kingOfTheHill))	{
			g.setColor(Color.red);
			g.drawString(tankValues[1][8] + "", pixel * 2, 50);
			g.setColor(Color.blue);
			g.drawString(tankValues[0][8] + "", width - pixel * 8, 50);
		}
		else {
			g.setColor(Color.red);
			g.drawString(tankValues[0][13] + "", pixel * 2, 50);
			g.setColor(Color.blue);
			g.drawString(tankValues[1][13] + "", width - pixel * 8, 50);
		}
		if (!(editLevels))	return;
		drawGrid(2);
	}
	public void getBaseControl() {
		//System.out.println(baseX + " " + baseY + " " + tankValues[1][1] + " " + tankValues[1][2]);
		map = AK.fillInArray(map, base, 0, 0, 'H');
		baseControl = 0;
		if (AK.overlaps(map, tankShapes[tankValues[0][0]],tankValues[0][1], tankValues[0][2], 'H')) baseControl = 1;
		if (AK.overlaps(map, tankShapes[tankValues[1][0]],tankValues[1][1], tankValues[1][2], 'H'))	{
			if (baseControl == 1) baseControl = 3;
			else baseControl = 2;
		}
		map = AK.fillInArray(map, base, 0, 0, ' ');
		for (int tankNum = 0; tankNum < 2; tankNum++)
			map = AK.mergeArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], (char)tankValues[tankNum][3]);
	}
	public void drawGrid(int size) {
		/*g.setColor(Color.lightGray);
		for (int i = 0; i < map.length*pixel; i+=pixel)
			g.drawLine(0, i*size, width, i*size);
		for (int i = 0; i < map[0].length*pixel; i+=pixel)
			g.drawLine(i*size, 0, i*size, height);
		g.setColor(Color.blue);
		for (int i = 0; i < map.length*pixel; i+=pixel)
			g.drawLine(0, i*size*4+pixel, width, i*size*4+pixel);
		for (int i = 0; i < map[0].length*pixel; i+=pixel)
			g.drawLine(i*size*4+pixel, 0, i*size*4+pixel, height);
		g.setColor(Color.magenta);
		for (int i = 0; i < map.length*pixel; i+=pixel)
			g.drawLine(0, i*size*16+pixel, width, i*size*16+pixel);
		for (int i = 0; i < map[0].length*pixel; i+=pixel)
			g.drawLine(i*size*16+pixel, 0, i*size*16+pixel, height);*/
		g.setColor(Color.lightGray);
		for (int i = map.length/2+1; i < map.length; i+=size) {
			g.drawLine(0, i*pixel, width, i*pixel);
			g.drawLine(0, (map.length-i+1)*pixel, width, (map.length-i+1)*pixel);
		}
		for (int i = map[0].length/2+2; i < map[0].length; i+=size) {
			g.drawLine(i*pixel, 0, i*pixel, height);
			g.drawLine((map[0].length-i+2)*pixel, 0, (map[0].length-i+2)*pixel, height);
		}
		g.setColor(Color.blue);
		for (int i = map.length/2+1; i < map.length; i+=size*4) {
			g.drawLine(0, i*pixel, width, i*pixel);
			g.drawLine(0, (map.length-i+1)*pixel, width, (map.length-i+1)*pixel);
		}
		for (int i = map[0].length/2+2; i < map[0].length; i+=size*4) {
			g.drawLine(i*pixel, 0, i*pixel, height);
			g.drawLine((map[0].length-i+2)*pixel, 0, (map[0].length-i+2)*pixel, height);
		}
		g.setColor(Color.magenta);
		for (int i = map.length/2+1; i < map.length; i+=size*16) {
			g.drawLine(0, i*pixel, width, i*pixel);
			g.drawLine(0, (map.length-i+1)*pixel, width, (map.length-i+1)*pixel);
		}
		for (int i = map[0].length/2+2; i < map[0].length; i+=size*16) {
			g.drawLine(i*pixel, 0, i*pixel, height);
			g.drawLine((map[0].length-i+2)*pixel, 0, (map[0].length-i+2)*pixel, height);
		}
	}
	public void drawBasePixel(int x, int y) {
		if (baseControl == 1) drawRedBasePixel(x, y);
		else if (baseControl == 2) drawBlueBasePixel(x, y);
		else if (baseControl == 3) drawMagentaBasePixel(x, y);
		else {
			c = new Color(125, 255, 125);
			g.setColor(c);
			g.fillRect(y, x, pixel, pixel);
		}
	}
	public void drawMagentaBasePixel(int x, int y) {
		c = new Color(255, 125, 255);
		g.setColor(c);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawRedPixel(int x, int y)	{
		g.setColor(Color.red);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawRedBasePixel(int x, int y) {
		c = new Color(255, 125, 125);
		g.setColor(c);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawBluePixel(int x, int y)	{
		g.setColor(Color.blue);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawBlueBasePixel(int x, int y) {
		c = new Color(125, 125, 255);
		g.setColor(c);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawWallPixel(int x, int y)	{
		c = new Color(255, 177, 125);
		g.setColor(c);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawWeakWallPixel(int x, int y)	{
		c = new Color(194, 137, 93);
		g.setColor(c);
		g.fillRect(y, x, pixel, pixel);
	}
	public void drawFillWalls(int x, int y)	{
		g.setColor(Color.blue);
		g.fillRect(y, x, pixel, pixel);
	}
	public void turnTank(int tankNum, int direction) {
		//System.out.println(tankValues[tankNum][0]);
		int getNewShape = (tankValues[tankNum][0] + direction + 16) % 16;
		map = AK.fillInArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], 8, 8, ' ');
		if (AK.onlyOverlaps(map, tankShapes[getNewShape], tankValues[tankNum][1], tankValues[tankNum][2], ' '))
			tankValues[tankNum][0] = getNewShape;
		map = AK.mergeArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], (char)tankValues[tankNum][3]);
		repaint();
	}
	public void MoveTimer(double d) {
		playMoveSound = false;
		movingET = d;
		movingST = System.nanoTime();
		soundTimer.start();
	}
	public void moveTank(int tankNum, int direction) {
		if (playMoveSound) {
			MoveTimer(playSound("Tank-Moving.wav", false));
		}
		int ty = tankValues[tankNum][1];
		int tx = tankValues[tankNum][2];
		switch (tankValues[tankNum][0]) {
			case 0:	tx+=2*direction;						break;
			case 1:	tx+=2*direction;	ty+=1*direction;	break;
			case 2:	tx+=2*direction;	ty+=2*direction;	break;
			case 3:	tx+=1*direction;	ty+=2*direction;	break;
			case 4:						ty+=2*direction;	break;
			case 5:	tx-=1*direction;	ty+=2*direction;	break;
			case 6: tx-=2*direction;	ty+=2*direction;	break;
			case 7:	tx-=2*direction;	ty+=1*direction;	break;
			case 8:	tx-=2*direction;						break;
			case 9:	tx-=2*direction;	ty-=1*direction;	break;
			case 10:tx-=2*direction;	ty-=2*direction;	break;
			case 11:tx-=1*direction;	ty-=2*direction;	break;
			case 12:					ty-=2*direction;	break;			
			case 13:tx+=1*direction;	ty-=2*direction;	break;
			case 14:tx+=2*direction;	ty-=2*direction;	break;
			case 15:tx+=2*direction;	ty-=1*direction;	break;
		}
		map = AK.fillInArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], 8, 8, ' ');
		if (AK.onlyOverlaps(map, tankShapes[tankValues[tankNum][0]], ty, tx, ' '))	{
			tankValues[tankNum][1] = ty;
			tankValues[tankNum][2] = tx;
			if (kingOfTheHill) getBaseControl();
		}
		else if (AK.overlaps(map, tankShapes[tankValues[tankNum][0]], ty, tx, 'f')) {
			map = AK.fillInArray(map, weakWallHere, 0, 0, 'w', ' ');
		}
		map = AK.mergeArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], (char)tankValues[tankNum][3]);
		repaint();
	}
	public void moveTank(int tankNum, int direction, int mY, int mX) {
		int ty = tankValues[tankNum][1];
		int tx = tankValues[tankNum][2];
		tx += mX;
		ty += mY;
		if (tankValues[tankNum][11] > 10) {
			//if (tankValues[(tankNum + 1) % 2][9] == -1) {	moreSpin = true;	}
			tankValues[tankNum][12] = -1;
			//tankValues[tankNum][9] = -1;
			return;
		}
		map = AK.fillInArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], 8, 8, ' ');
		if (AK.onlyOverlaps(map, tankShapes[tankValues[tankNum][0]], ty, tx, ' '))	{
			tankValues[tankNum][1] = ty;
			tankValues[tankNum][2] = tx;
			if (kingOfTheHill) getBaseControl();
		}
		else if (AK.overlaps(map, tankShapes[tankValues[tankNum][0]], ty, tx, 'f')) {
			map = AK.fillInArray(map, weakWallHere, 0, 0, 'w', ' ');
		}
		else {
			//if (tankValues[(tankNum + 1) % 2][9] == -1) {	moreSpin = true;	}
			if (tankValues[tankNum][11] < 4) crashThroughWall(tankNum, mY, mX);
			tankValues[tankNum][12] = -1;
			//tankValues[tankNum][9] = -1;
			return;
		}
		map = AK.mergeArray(map, tankShapes[tankValues[tankNum][0]], tankValues[tankNum][1], tankValues[tankNum][2], (char)tankValues[tankNum][3]);
		repaint();
	}
	public void crashThroughWall(int tankNum, int mY, int mX) {
		int ty = tankValues[tankNum][1];
		int tx = tankValues[tankNum][2];
		for (int i = 0; i >= 0; i++) {
			tx += mX;
			ty += mY;
			ty = (ty + map.length) % map.length;
			tx = (tx + map[0].length) % map[0].length;
			//System.out.println(tx + " " + ty);
			if (AK.onlyOverlaps(map, tankShapes[tankValues[tankNum][0]], ty, tx, ' '))	{
				if (stop)	{
					tankValues[tankNum][1] = ty;
					tankValues[tankNum][2] = tx;
					if (kingOfTheHill) getBaseControl();
					return;
				}
				stop = true;
			}
			else stop = false;
		}
	}
	public void shoot(int tankNum) {
		if (tankValues[tankNum][4] != -1) return;
		playSound("Tank Firing.wav", false);
		int ty = tankValues[tankNum][1];
		int tx = tankValues[tankNum][2];
		int mx = 0, my = 0;
		switch (tankValues[tankNum][0]) {
			case 0:	tx+=8;	ty+=4;	break;
			case 1:	tx+=7;	ty+=6;	break;
			case 2:	tx+=8;	ty+=7;	break;
			case 3:	tx+=6;	ty+=7;	break;
			case 4:	tx+=3;	ty+=8;	break;
			case 5:	tx+=1;	ty+=7;	break;
			case 6: tx+=0;	ty+=8;	break;
			case 7:	tx-=0;	ty+=7; 	break;
			case 8:	tx-=1;	ty+=3;	break;
			case 9:	tx-=0;	ty+=1;	break;
			case 10:tx-=1;	ty-=0;	break;
			case 11:tx+=1;	ty-=0;	break;
			case 12:tx+=4;	ty-=1;	break;			
			case 13:tx+=6;	ty-=0;	break;
			case 14:tx+=7;	ty-=1;	break;
			case 15:tx+=7;	ty+=1;	break;
		}
		switch (tankValues[tankNum][0]) {
			case 0:	mx=2;			break;
			case 1:	mx=2;	my=1;	break;
			case 2:	mx=2;	my=2;	break;
			case 3:	mx=1;	my=2;	break;
			case 4:			my=2;	break;
			case 5:	mx=-1;	my=2;	break;
			case 6: mx=-2;	my=2;	break;
			case 7:	mx=-2;	my=1;	break;
			case 8:	mx=-2;			break;
			case 9:	mx=-2;	my=-1;	break;
			case 10:mx=-2;	my=-2;	break;
			case 11:mx=-1;	my=-2;	break;
			case 12:		my=-2;	break;			
			case 13:mx=1;	my=-2;	break;
			case 14:mx=2;	my=-2;	break;
			case 15:mx=2;	my=-1;	break;
		}
		//System.out.println(tx + " " + ty);
		try {
			if (map[ty][tx] != ' ') return;
		}
		catch (ArrayIndexOutOfBoundsException e)	{
			System.err.println("Error placing bullet");
			return;
		}
		tankValues[tankNum][4] = ty;
		tankValues[tankNum][5] = tx;
		tankValues[tankNum][6] = my;
		tankValues[tankNum][7] = mx;
		if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'W' || map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'w') {
			tankValues[tankNum][4] = -1;
		}
		else if (tankValues[0][4] == tankValues[1][4] && tankValues[0][5] == tankValues[1][5]) {
			tankValues[0][4] = -1;
			tankValues[1][4] = -1;
		}
		else if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'R') {
			tankValues[0][8]++;
			tankValues[0][11] = 0;
			killTank(0, tankNum);
			tankValues[tankNum][4] = -1;
		}
		else if (map[tankValues[tankNum][4]][tankValues[tankNum][5]] == 'B') {
			tankValues[1][8]++;
			tankValues[1][11] = 0;
			killTank(1, tankNum);
			tankValues[tankNum][4] = -1;
		}
		else	map[tankValues[tankNum][4]][tankValues[tankNum][5]] = (char)tankValues[tankNum][3];
		repaint();
		bulletTimer.start();
	}
	public void changeBullet(int tankNum, int cX, int cY)	{
		if (tankFacing(tankNum, 0))
			if (cX > 0)	tankValues[tankNum][7] += cX;
		if (tankFacing(tankNum, 1))
			if (cY > 0)	tankValues[tankNum][6] += cY;
		if (tankFacing(tankNum, 2))
			if (cX < 0)	tankValues[tankNum][7] += cX;
		if (tankFacing(tankNum, 3))
			if (cY < 0)	tankValues[tankNum][6] += cY;
	}
	public boolean tankFacing(int tankNum, int direction)	{
		boolean d0 = false, d1 = false, d2 = false, d3 = false;
		switch (tankValues[tankNum][0]) {
			case 15:
			case 0:
			case 1:	d1 = d3 = true;	break;
			case 2:	d3 = d2 = true;	break;
			case 3:
			case 4:
			case 5:	d0 = d2 = true;	break;
			case 6: d3 = d0 = true;	break;
			case 7:
			case 8:
			case 9:	d1 = d3 = true;	break;
			case 10:d0 = d1 = true;	break;
			case 11:
			case 12:		
			case 13:d0 = d2 = true;	break;
			case 14:d1 = d2 = true;	break;
		}
		switch (direction)	{
			case 0: if (d0) return true; break;
			case 1: if (d1) return true; break;
			case 2: if (d2) return true; break;
			case 3: if (d3) return true; break;
		}
		return false;
	}
	public void killTank(int tankNum, int bulletNum)	{
		tankValues[tankNum][9] = bulletNum;
		playSound("Car Spinning Out.wav", false);
		deathTimer.start();
	}
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		if (key == 'r' || key == 'R') {
			initial = true;
			repaint();
		}
		if (key == 'c') {	changePixelSize();	repaint();	}
		if (key == 'p' && editLevels) save();
		if (tankValues[0][4] == -1 && tankValues[0][9] == -1 && !editLevels) {
			//System.out.println(e.isShiftDown());
			if (key == 'a' || key == 'A') turnTank(0, -1);
			if (key == 'd' || key == 'D') turnTank(0,  1);
			if (key == 'w' || key == 'W')	moveTank(0,  1);
			if (key == 's' || key == 'S')	moveTank(0,  -1);
			if (e.isShiftDown() && tankValues[1][9] == -1) shoot(0);
		}
		else if (tankValues[0][9] == -1 && !editLevels)	{
			if (key == 'a' || key == 'A')	changeBullet(0, -1, 0);
			if (key == 'd' || key == 'D')	changeBullet(0,	 1, 0);
			if (key == 'w' || key == 'W') changeBullet(0, 0, -1);
			if (key == 's' || key == 'S')	changeBullet(0,  0, 1);
		}
		if (tankValues[1][4] == -1 && tankValues[1][9] == -1 && !editLevels)	{
			if (code == KeyEvent.VK_LEFT) 	turnTank(1, -1);
			if (code == KeyEvent.VK_RIGHT) 	turnTank(1,  1);
			if (code == KeyEvent.VK_UP)		moveTank(1,  1);
			if (code == KeyEvent.VK_DOWN)	moveTank(1,  -1);
			if (e.isControlDown() && tankValues[0][9] == -1)	shoot(1);
		}
		else if (tankValues[1][9] == -1 && !editLevels)	{
			if (code == KeyEvent.VK_LEFT) 	changeBullet(1, -1, 0);
			if (code == KeyEvent.VK_RIGHT) 	changeBullet(1, 1, 0);
			if (code == KeyEvent.VK_UP)		changeBullet(1, 0, -1);
			if (code == KeyEvent.VK_DOWN)	changeBullet(1, 0, 1);
		}
	}
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		x-=pixel;
		y-=pixel;
		x /= pixel;
		y /= pixel;
		if (!(hasFocus()))	requestFocus();
		try {
			if (editLevels) {
				if (e.isMetaDown())	map[y][x] = 'w';
				else if (e.isAltDown()) map[y][x] = 'H';
				else if (map[y][x] == ' ') {	map[y][x] = 'W';	wallPaint = true;	}
				else {	map[y][x] = ' ';	wallPaint = false;	}
				repaint();
			}
		}
		catch (ArrayIndexOutOfBoundsException E) {
			System.err.println("out of bounds");
		}
	}
	public void mouseDragged(MouseEvent e)	{
		int x = e.getX();
		int y = e.getY();
		x-=pixel;
		y-=pixel;
		x /= pixel;
		y /= pixel;
		//x-=pixel/2;
		//y-=pixel/2;
		try {
			if (editLevels) {
				if (e.isMetaDown())	map[y][x] = 'w';
				else if (e.isAltDown()) map[y][x] = 'H';
				else if (wallPaint)	map[y][x] = 'W';
				else map[y][x] = ' ';
				repaint();
			}
		}
		catch (ArrayIndexOutOfBoundsException E) {
			System.err.println("out of bounds");
		}
	}
	public void save()	{
		try {
			output = new PrintWriter("AtariTanksLevel" + (saves.length + 1) + ".txt");
		}
		catch (IOException e)	{
			System.err.println("ERROR: Cannot open file AtariTanksLevel" + (saves.length+1) + ".txt");
			System.exit(99);
		}
		for (int row = 0; row < map.length; row++)
			for (int col = 0; col < map[row].length; col++)
				output.print((int)map[row][col] + " ");
		output.println();
		output.close();
		System.out.println("Saved");
	}
	public void load(int saveNumber)	{
		if (editLevels && (editLevelNumber != 0))	saveNumber = editLevelNumber;
		char[][] a = new char[90][75];
		try {
			input = new Scanner(saves[saveNumber]);
		}
		catch (FileNotFoundException e)	{
			System.err.println("ERROR: Cannot open file AtariTanksLevel" + saveNumber + ".txt");
			System.exit(97);
		}
		if (false) {			// true = make map symmetrical (does not change txt file) --- currently all maps are symmetrical so it doesn't really matter, but leave on false for good habit unless making a map
		for (int row = 0; row < map.length; row++)
			for (int col = 0; col < map[row].length; col++)
				if (col < a[0].length)	a[row][col] = (char)(input.nextInt());
				else {	int l = input.nextInt();	}

		map = AK.mergeArray(map, a, 0, 0);
		//AK.outputArray(a);
		a = AK.mirrorHorizontally(a);
		map = AK.mergeArray(map, a, 0, 75);	}
		else {
			char Char;
		for (int row = 0; row < map.length; row++)
			for (int col = 0; col < map[row].length; col++) {
				Char = (char)input.nextInt();
				map[row][col] = ' ';
				if (Char == 'H' && !(editLevels))	base[row][col] = true;
				else map[row][col] = Char;
			}
		}
		input.close();
		repaint();
	}
}