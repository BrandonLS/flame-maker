/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        19 févr. 2013
 */

package ch.epfl.flamemaker.geometry2d;

/**
 * A Point in two dimensions
 */
public final class Point {
	/**
	 * The Point (0,0)
	 */
	public static final Point ORIGIN = new Point(0,0);
	
	private final double x;
	private final double y;
	
	/**
	 * Creates a point 
	 * @param pX the x coordinate
	 * @param pY the y coordinate
	 */
	public Point(double pX, double pY) {
		x = pX;
		y = pY;
	}
	
	/**
	 * Copy constructor
	 * @param p the Point to copy
	 */
	public Point(Point p) {
		x = p.x;
		y = p.y;
	}

	/**
	 * @return the value of x
	 */
	public double x() {
		return x;
	}
	
	/**
	 * @return the value of y
	 */
	public double y() {
		return y;
	}
	
	/**
	 * @return the length of the polar coordinate
	 */
	public double r() {
		return Math.sqrt(x*x + y*y);
	}
	
	/**
	 * @return the angle of the polar coordinate
	 */
	public double theta() {
		return Math.atan2(y, x);
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
