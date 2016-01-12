/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.color;

import java.util.ArrayList;
import java.util.List;

/**
 * A palette represented by a list of colors, interpolating between themselves
 *
 */
public final class InterpolatedPalette implements Palette {
	private final List<Color> colors = new ArrayList<Color>();
	
	/**
	 * Creates an InterpolatedPalette
	 * @param col the list of colors to create from
	 * @throws IllegalArgumentException if there is less than 2 colors in <strong>col</strong>
	 */
	public InterpolatedPalette(List<Color> col) {
		if(col.size() < 2) {
			throw new IllegalArgumentException("There must be at least two colors");
		}
		
		for (Color  c : col) {
			colors.add(c);
		}
	}
	
	@Override
	public Color colorForIndex(double index) {
		int nbrOfColors = colors.size();
		double[] indexOfColors = new double[nbrOfColors];
		
		double intervallSize = 1.0 / (nbrOfColors - 1);
		// fills indexOfColors
		for (int i = 0; i < nbrOfColors; i++) {
			indexOfColors[i] = i *  intervallSize;
		}
		
		if(index == 0) {
			return new Color(colors.get(0));
		}
		
		else if(index == 1) {
			return new Color(colors.get(nbrOfColors - 1));
		}
		
		else {
			int i = 0;
			
			// finds the index of the next color near the index
			while(indexOfColors[i] < index) {
				i++;
			}
			
			Color nextColor = colors.get(i);
			Color previousColor = colors.get(i - 1);
			
			// mixes the two nearest colors to the index with the right proportion
			Color colorOfIndex = previousColor.mixWith(nextColor, (index - indexOfColors[i - 1]) * (nbrOfColors - 1));
			
			return new Color(colorOfIndex);
		}
	}
}
