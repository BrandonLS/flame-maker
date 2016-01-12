/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.*;
import ch.epfl.flamemaker.color.*;

/**
 * Computes color fractals in a PPM file.
 *
 */
public final class FlamePPMMaker {
	/**
	 * The main method creating shark-fin and turbulence fractals
	 * @param args not used
	 */
	public static void main(String[] args) {
		final double[] weightJustAffine = {1, 0, 0, 0, 0, 0};
		
		ArrayList<FlameTransformation> listShark = new ArrayList<FlameTransformation>();
		ArrayList<FlameTransformation> listTurbulence = new ArrayList<FlameTransformation>();
		
		double[] weightShark1 = {1, 0.1, 0, 0, 0, 0};
		listShark.add(new FlameTransformation(new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8), weightShark1));
		
		double[] weightShark2 = {0, 0, 0, 0, 0.8, 1};
		listShark.add(new FlameTransformation(new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), weightShark2));
		
		double[] weightShark3 = weightJustAffine;
		listShark.add(new FlameTransformation(new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9), weightShark3));
		
		
		double[] weightTurbulence1 = {0.5, 0, 0, 0.4, 0, 0};
		listTurbulence.add(new FlameTransformation(new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7), weightTurbulence1));
		
		double[] weightTurbulence2 = {1, 0, 0.1, 0, 0, 0};
		listTurbulence.add(new FlameTransformation(new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3), weightTurbulence2));
		
		listTurbulence.add(new FlameTransformation(new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3), weightJustAffine));
		
		Flame shark = new Flame(listShark);
		Flame turbulence = new Flame(listTurbulence);
		
		FlameAccumulator accumulatorShark = shark.computeAll(new Rectangle(new Point(-0.25, 0), 5, 4), 500, 400, 50);
		FlameAccumulator accumulatorTurbulence = turbulence.computeAll(new Rectangle(new Point(0.1,0.1), 3, 3), 500, 500, 50);
		
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(new Color(1, 0, 0));
		colors.add(new Color(0, 1, 0));
		colors.add(new Color(0, 0, 1));
		
		Palette paletteRGB = new InterpolatedPalette(colors);
	
		Color background = new Color(Color.BLACK);
		
		PPM(accumulatorShark, "shark-fin.ppm", paletteRGB, background);
		PPM(accumulatorTurbulence, "turbulence.ppm", paletteRGB, background);
		
	}
	
	/**
	 * Writes what's in the accumulator in a file with the given palette and background color
	 * @param accumulator the accumulator to write from
	 * @param fileName the name of the file to be written
	 * @param palette the palette to use for the colors
	 * @param background the background color
	 */
	private static void PPM(FlameAccumulator accumulator, String fileName, Palette palette, Color background) {
		try {
			PrintStream outPut = new PrintStream(fileName);
			
			final int MAX_INTENSITY = 100;
			
			outPut.println("P3");
			outPut.println(accumulator.width() + " " + accumulator.height());
			outPut.println(MAX_INTENSITY);
			
			for (int i = 0; i < accumulator.height(); i++) {
				for (int j = 0; j < accumulator.width(); j++) {
					Color c = accumulator.color(palette, background, j, i);
					int r = Color.sRGBEncode(c.red(), MAX_INTENSITY);
					int g = Color.sRGBEncode(c.green(), MAX_INTENSITY);
					int b = Color.sRGBEncode(c.blue(), MAX_INTENSITY);
					
					
					outPut.print(r + " " + g + " " + b + " ");
				}
				outPut.println();
			}
			
			outPut.close();
			System.out.println("\"" + fileName + "\" was sucessfully written");
		}
		
		catch(FileNotFoundException e) {
			System.out.println("Error when creating or modifying the file " + fileName + ": " + e.getMessage());
		}
		
	}
}
