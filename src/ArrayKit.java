/*	Ofek Gila
	March 3rd, 2014
	ArrayKit.java
	This program will give me helpful tools to use with Arrays in java
*/
public class ArrayKit	{
	public static void main(String[] args)	{	
		ArrayKit AK = new ArrayKit();
		boolean[][] test = 	{	{true, true, true},
							{false, false, false},
							{true, true, true}	};
		AK.outputArray(test);
		test = AK.rotateClockWise(test);
		AK.outputArray(test);
		test = AK.mirrorVertically(test);
		AK.outputArray(test);
	}
	public boolean[][] rotateClockWise(boolean[][] array)	{
		if (array.length != array[0].length) return null;
		boolean[][] turned = new boolean[array.length][array.length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array.length; a++)
				turned[i][a] = array[array.length - a - 1][i];
		return turned;
	}
	public char[][] rotateClockWise(char[][] array)	{
		if (array.length != array[0].length) return null;
		char[][] turned = new char[array.length][array.length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array.length; a++)
				turned[i][a] = array[a][array.length - i - 1];
		return turned;
	}
	public int[][] rotateClockWise(int[][] array)	{
		if (array.length != array[0].length) return null;
		int[][] turned = new int[array.length][array.length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array.length; a++)
				turned[i][a] = array[a][array.length - i - 1];
		return turned;
	}
	public String[][] rotateClockWise(String[][] array)	{
		if (array.length != array[0].length) return null;
		String[][] turned = new String[array.length][array.length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array.length; a++)
				turned[i][a] = array[a][array.length - i - 1];
		return turned;
	}
	public double[][] rotateClockWise(double[][] array)	{
		if (array.length != array[0].length) return null;
		double[][] turned = new double[array.length][array.length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array.length; a++)
				turned[i][a] = array[a][array.length - i - 1];
		return turned;
	}
	public boolean[][] mirrorHorizontally(boolean[][] array)	{
		boolean[][] mirrored = new boolean[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array[i].length; a++)
				mirrored[i][a] = array[i][array[i].length - a - 1];
		return mirrored;
	}
	public char[][] mirrorHorizontally(char[][] array)	{
		char[][] mirrored = new char[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array[i].length; a++)
				mirrored[i][a] = array[i][array[i].length - a - 1];
		return mirrored;
	}
	public boolean[][] mirrorVertically(boolean[][] array)	{
		boolean[][] mirrored = new boolean[array.length][array.length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array[i].length; a++)
				mirrored[i][a] = array[array.length - i - 1][a];
		return mirrored;
	}
	public char[][] mirrorVertically(char[][] array)	{
		char[][] mirrored = new char[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array[i].length; a++)
				mirrored[i][a] = array[array.length - i - 1][a];
		return mirrored;
	}
	public void convertToCharAndOutput(boolean[][] array)	{
		char[][] output = new char[array.length][array[0].length];
		output = convertToChar(array);
		outputArray(output);
	}
	public char[][]	convertToChar(boolean[][] array)	{
		char[][] converted = new char[array.length][array[0].length];
		for (int i = 0; i < array.length; i++)
			for (int a = 0; a < array[i].length; a++)
				if (array[i][a])	converted[i][a] = '*';
				else converted[i][a] = ' ';
		return converted;
	}
	public void outputArray(boolean[][] array)	{
		for (int i = 0; i < array.length; i++)	{
			for (int a = 0; a < array[i].length; a++)
				System.out.print(array[i][a] + " ");
			System.out.println();
		}
		System.out.println();
	}
	public void outputArray(char[][] array)	{
		for (int i = 0; i < array.length; i++)	{
			for (int a = 0; a < array[i].length; a++)
				System.out.print(array[i][a] + " ");
			System.out.println();
		}
		System.out.println();
	}
	public void outputArray(String[] array)	{
		for (int i = 0; i < array.length; i++)
				System.out.println(array[i]);
	}
	public char[][] mergeArray(char[][] background, boolean[][] merge, int x, int y, char symbol)	{
		for (int i = 0; i < merge.length; i++)
			for (int a = 0; a < merge[i].length; a++)
				try {
					if (merge[i][a]) background[i+x][a+y] = symbol;
				}
				catch (ArrayIndexOutOfBoundsException e) {

				}
		return background; 
	}
	public char[][] mergeArray(char[][] background, char[][] merge, int x, int y)	{
		for (int i = 0; i < merge.length; i++)
			for (int a = 0; a < merge[i].length; a++)
				try {
					background[i+x][a+y] = merge[i][a];
				}
				catch (ArrayIndexOutOfBoundsException e) {

				}
		return background; 
	}
	public char[][] mergeArrayOn(char[][] background, boolean[][] merge, int x, int y, char symbol, char symbolOn)	{
		for (int i = 0; i < merge.length; i++)
			for (int a = 0; a < merge[i].length; a++)
				try {
					if (merge[i][a] && background[i][a] == symbolOn) background[i+x][a+y] = symbol;
				}
				catch (ArrayIndexOutOfBoundsException e) {

				}
		return background; 
	}
	public char[][] fillBoxInArray(char[][] background, int x, int y, int width, int height, char symbol) {
		for (int i = 0; i < width; i++)
			for (int a = 0; a < height; a++)
				background[i+x][a+y] = symbol;
		return background;
	}
	public char[][] fillInArray(char[][] background, boolean[][] frontGround, int x, int y, int width, int height, char symbol) {
		for (int i = 0; i < width; i++)
			for (int a = 0; a < height; a++)
				try	{
					if (frontGround[i][a])	background[i+x][a+y] = symbol;
				}
				catch (ArrayIndexOutOfBoundsException e) {

				}
		return background;
	}
	public char[][] fillInArray(char[][] background, boolean[][] frontGround, int x, int y, int width, int height, char symbol, char symbolOn) {
		for (int i = 0; i < width; i++)
			for (int a = 0; a < height; a++)
				try	{
					if (frontGround[i][a] && background[i][a] == symbolOn)	{	background[i+x][a+y] = symbol;	System.out.println(symbol);	}
				}
				catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("out of bounds");
				}
		return background;
	}
	public char[][] fillInArray(char[][] background, boolean[][] frontGround, int x, int y, char symbol, char symbolOn) {
		int width = frontGround.length;
		int height = frontGround[0].length;
		for (int i = 0; i < width; i++)
			for (int a = 0; a < height; a++)
				try	{
					if (frontGround[i][a] && background[i][a] == symbolOn)	background[i+x][a+y] = symbol;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("out of bounds");
				}
		return background;
	}
	public char[][] fillInArray(char[][] background, boolean[][] frontGround, int x, int y, char symbol) {
		int width = frontGround.length;
		int height = frontGround[0].length;
		for (int i = 0; i < width; i++)
			for (int a = 0; a < height; a++)
				try	{
					if (frontGround[i][a])	background[i+x][a+y] = symbol;
				}
				catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("out of bounds");
				}
		return background;
	}
	public boolean onlyOverlaps(char[][] background, boolean[][] frontGround, int x, int y, char correctSymbol) {
		for (int i = 0; i < frontGround.length; i++)
			for (int a = 0; a < frontGround[i].length; a++)
				try {
					if (frontGround[i][a] && background[i+x][a+y] != correctSymbol) return false;
				}
				catch ( ArrayIndexOutOfBoundsException e)	{
					return false;
				}
		return true;
	}
	public boolean overlaps(char[][] background, boolean[][] frontGround, int x, int y, char correctSymbol) {
		for (int i = 0; i < frontGround.length; i++)
			for (int a = 0; a < frontGround[i].length; a++)
				try {
					if (frontGround[i][a] && background[i+x][a+y] == correctSymbol) return true;
				}
				catch ( ArrayIndexOutOfBoundsException e)	{
					return false;
				}
		return false;
	}
}