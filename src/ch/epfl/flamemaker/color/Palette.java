/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.color;

/**
 * A range of colors
 */
public interface Palette {

	/**
	 * Returns the color corresponding to the index
	 * @param index the index to look at
	 * @return the color corresponding to the index
	 * @throws IllegalArgumentException if the index is smaller than 0 or greater than 1
	 */
	public Color colorForIndex(double index) throws IllegalArgumentException;
}
