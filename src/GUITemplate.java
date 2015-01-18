/*	Ofek Gila
	February 3rd, 2014
	ColorPicker.java
	This program will attempt to let the user get RGB values for a color
*/
import java.awt.*;			// Imports
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.Scanner;
import java.net.MalformedURLException;
import javax.sound.sampled.*;
import java.io.*;			// classes File, IOException
import javax.imageio.*;	// class ImageIO

public class GUITemplate	extends JApplet{			// I'm pretty sure I copied down one of your online codes for key and focus listeners for their methods
	
	public JFrame frame;
	
	public static void main(String[] args) {	// when I made snake.java, and I copied snake.java to have all the implements for this code, so don't
		GUITemplate GUIT = new GUITemplate();
		//GUIT.run();
	}
	public void run(){
		frame = new JFrame("GUITemplate");	// ask why I extend JApplet or implement all of those things ^_^
		frame.setContentPane(new TemplatePanel());
		frame.setSize(width(100), height(100));		// Sets size of frame
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
		setContentPane(	new TemplatePanel());
	}
	public int cC() {
		return 1;
	}
}
class TemplatePanel extends JPanel implements ActionListener, KeyListener, FocusListener, MouseListener, MouseMotionListener, ChangeListener	{

	public final int setWidth = 0, setHeight = 0;
	public int width, height;							// width and height of frame
	public Graphics g;									// Graphics of frame
	public Color c;
	public boolean initial = false;
	public boolean veryFirstTime = true;
	public Timer t;
	public ArrayKit AK;
	public Scanner keyboard;
	public Clip clip;
	public AudioInputStream audioInputStream;
	public AudioSystem audiosystem;
	public String[] files;
	public String imageLocation;
	public Image image;
	
	public TemplatePanel()	{
		addKeyListener(this);							// implements the implements
		addFocusListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		AK = new ArrayKit();
		keyboard = new Scanner(System.in);
	}
	public void startup()	{
		
	}
	public void constructor()	{

	}
	public int getRelativeX(double x)	{
		return (int)((x / width) * setWidth + 0.5);
	}
	public int getRelativeY(double y)	{
		return (int)((y / height) * setHeight + 0.5);
	}
	public void paintComponent(Graphics a)	{
		super.paintComponent(a);
		g = a;
		width = getWidth();
		height = getHeight();	
		//System.out.println(width + " " + height);
		if (initial)	{
			constructor();
			initial = false;
		}
	}
	public double playSound(String soundLocation, boolean loopContinuously) {
		double audioLength = 0;
    	try {
			audioInputStream = audiosystem.getAudioInputStream(getClass().getResource(soundLocation)); 
			clip = audiosystem.getClip();
			clip.open(audioInputStream);
    	    if (loopContinuously) clip.loop(clip.LOOP_CONTINUOUSLY);
    	    else clip.start();
    	    audioLength = clip.getMicrosecondLength();
    	} catch(Exception ex) {
    	    System.out.println("Error with playing sound.");
    	    ex.printStackTrace();
    	}
    	return audioLength;
	}
	public double playSound(String soundLocation) {
		double audioLength = 0;
    	try {
			audioInputStream = audiosystem.getAudioInputStream(getClass().getResource(soundLocation)); 
			clip = audiosystem.getClip();
			clip.open(audioInputStream);
    	    clip.start();
    	    audioLength = clip.getMicrosecondLength();
    	} catch(Exception ex) {
    	    System.out.println("Error with playing sound.");
    	    ex.printStackTrace();
    	}
    	return audioLength;
	}
	public String[] filesCopy = new String[1];
	public int count = 0;

	public String[] getFilesInFolder(final File folder) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	getFilesInFolder(fileEntry);
	        } else {
	        	files = new String[count+1];
	        	for (int i = 0; i < count; i++)	files[i] = filesCopy[i];
	            files[count] = fileEntry.getPath().substring(System.getProperty("user.dir").length()+1);
	        	//System.out.println(files[count]);
	            count++;
	            filesCopy = new String[count];
	            for (int i = 0; i < count; i++)	filesCopy[i] = files[i];
	        }
	    }
	    return files;
	}
    public String getDirectory() {
       return  System.getProperty("user.dir");
    }
    public void drawImage()	{
		try {
			image = ImageIO.read(new File(imageLocation));
		}
		catch (IOException e)	{
			System.err.println("ERROR: Cannot read image file");
			System.exit(1);
		}
		g.drawImage(	image, 0, 0, getWidth(), getHeight(), this	);
	}
	public void mouseDragged(MouseEvent evt)	{	}
	public void mouseMoved(MouseEvent evt)	{	}
	public void actionPerformed(ActionEvent e)	{	}
	public void focusGained(FocusEvent evt) {	}
	public void focusLost(FocusEvent evt) {	}
	public void keyTyped(KeyEvent evt) {	}
	public void keyPressed(KeyEvent evt) {	}
	public void keyReleased(KeyEvent evt) {	}
	public void mouseEntered(MouseEvent evt) {	} 
	public void mousePressed(MouseEvent evt) {	}
    public void mouseExited(MouseEvent evt) {	} 
    public void mouseReleased(MouseEvent evt) {  } 
    public void mouseClicked(MouseEvent evt) { }
    public void stateChanged(ChangeEvent e) {	}
}
class NorthPanel extends JPanel implements KeyListener, ActionListener, FocusListener	{
	public JTextField JTF;
	public Graphics g;
	
	public NorthPanel()	{
		JTF = new JTextField("N");
		add(JTF);
		JTF.addKeyListener(this);
		JTF.addActionListener(this);
		JTF.addFocusListener(this);
	}
	public void paintComponent(Graphics g) {
		this.g = g;
		super.paintComponent(g);
	}
	public void actionPerformed(ActionEvent e)	{
		String str = e.getActionCommand();
	}
	public void keyPressed(KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void keyReleased (KeyEvent e) {	}
	public void keyTyped (KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void focusGained(FocusEvent f)	{	}
	public void focusLost(FocusEvent f)	{	}
}
class SouthPanel extends JPanel implements KeyListener, ActionListener, FocusListener	{
	public JTextField JTF;
	public Graphics g;
	
	public SouthPanel()	{
		JTF = new JTextField("S");
		add(JTF);
		JTF.addKeyListener(this);
		JTF.addActionListener(this);
		JTF.addFocusListener(this);
	}
	public void paintComponent(Graphics g) {
		this.g = g;
		super.paintComponent(g);
	}
	public void actionPerformed(ActionEvent e)	{
		String str = e.getActionCommand();
	}
	public void keyPressed(KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void keyReleased (KeyEvent e) {	}
	public void keyTyped (KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void focusGained(FocusEvent f)	{	}
	public void focusLost(FocusEvent f)	{	}
}
class EastPanel extends JPanel implements KeyListener, ActionListener, FocusListener	{
	public JTextField JTF;
	public Graphics g;
	
	public EastPanel()	{
		JTF = new JTextField("E");
		add(JTF);
		JTF.addKeyListener(this);
		JTF.addActionListener(this);
		JTF.addFocusListener(this);
	}
	public void paintComponent(Graphics g) {
		this.g = g;
		super.paintComponent(g);
	}
	public void actionPerformed(ActionEvent e)	{
		String str = e.getActionCommand();
	}
	public void keyPressed(KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void keyReleased (KeyEvent e) {	}
	public void keyTyped (KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void focusGained(FocusEvent f)	{	}
	public void focusLost(FocusEvent f)	{	}
}
class WestPanel extends JPanel implements KeyListener, ActionListener, FocusListener	{
	public JTextField JTF;
	public Graphics g;
	
	public WestPanel()	{
		JTF = new JTextField("W");
		add(JTF);
		JTF.addKeyListener(this);
		JTF.addActionListener(this);
		JTF.addFocusListener(this);
	}
	public void paintComponent(Graphics g) {
		this.g = g;
		super.paintComponent(g);
	}
	public void actionPerformed(ActionEvent e)	{
		String str = e.getActionCommand();
	}
	public void keyPressed(KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void keyReleased (KeyEvent e) {	}
	public void keyTyped (KeyEvent e)	{
		char c = e.getKeyChar();
	}
	public void focusGained(FocusEvent f)	{	}
	public void focusLost(FocusEvent f)	{	}
}