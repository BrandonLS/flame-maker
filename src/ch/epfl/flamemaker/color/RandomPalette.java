/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A palette represented by a list of random colors, interpolating between themselves
 *
 */
public final class RandomPalette implements Palette {
	private final List<Color> colors = new ArrayList<Color>();
	
	/**
	 * Creates a new RandomPalette
	 * @param n the number of colors to be in the palette
	 */
	public RandomPalette(int n) {
		if(n < 2) {
			throw new IllegalArgumentException("There must be at least two colors");
		}
		
		Random r = new Random();
		
		// adds n random colors to the list
		for (int i = 0; i < n; i++) {
			colors.add(new Color(r.nextDouble(), r.nextDouble(), r.nextDouble()));
		}
	}

	@Override
	public Color colorForIndex(double index) throws IllegalArgumentException {
		InterpolatedPalette palette = new InterpolatedPalette(colors);
		return palette.colorForIndex(index);
	}

}
