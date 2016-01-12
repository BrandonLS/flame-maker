/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        19 févr. 2013
 */

package ch.epfl.flamemaker.geometry2d;

/**
 * A Transformation is a function from the two-dimensional vector space of real numbers 
 * to the two-dimensional vector space of real numbers
 */
public interface Transformation {
	/**
	 * Transforms a point into another by the Transformation
	 * @param p the point to transform
	 * @return the point transformed
	 */
	public Point transformPoint(Point p);
}
