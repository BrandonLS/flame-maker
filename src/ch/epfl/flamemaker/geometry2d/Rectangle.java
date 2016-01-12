/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        19 févr. 2013
 */

package ch.epfl.flamemaker.geometry2d;

/**
 * A Rectangle in two dimensions
 */
public final class Rectangle {
	private final Point center;
	private final double width;
	private final double height;
	
	/**
	 * Creates a Rectangle
	 * @param pCenter the center of the Rectangle
	 * @param pW the width of the Rectangle
	 * @param pH the height of the Rectangle
	 * @throws IllegalArgumentException if the height or width is smaller or equal to zero
	 */
	public Rectangle(Point pCenter, double pW, double pH)
		throws IllegalArgumentException {
			center = pCenter;
			
			if(pW <= 0 || pH <= 0) {
				throw new IllegalArgumentException("Negative or null height or width");
			}
			
			else {
				width = pW;
				height = pH;	
			}
	}
	
	/**
	 * Copy constructor
	 * @param r the Rectangle to copy
	 */
	public Rectangle(Rectangle r) {
		center = new Point(r.center);
		width = r.width;
		height = r.height;
	}
	
	/**
	 * @return the smallest x coordinate of the Rectangle
	 */
	public double left() {
		return center.x() - (width / 2.0);
	}
	
	/**
	 * @return the greatest x coordinate of the Rectangle
	 */
	public double right() {
		return center.x() + (width / 2.0);
	}
	
	/**
	 * @return the smallest y coordinate of the Rectangle
	 */
	public double bottom() {
		return center.y() - (height / 2.0);
	}
	
	/**
	 * @return the greatest y coordinate of the Rectangle
	 */
	public double top() {
		return center.y() + (height / 2.0);
	}
	
	/**
	 * @return the width
	 */
	public double width() {
		return width;
	}
	
	/**
	 * @return the height
	 */
	public double height() {
		return height;
	}
	
	/**
	 * @return the center
	 */
	public Point center() {
		return center;
	}
	
	/**
	 * Looks if a point is contained in the Rectangle
	 * @param p the point
	 * @return true if and only if the point is contained in the Rectangle
	 */
	public boolean contains(Point p) {
		return p.x() >= left() && p.x() < right() && p.y() >= bottom() && p.y() < top();
	}
	
	/**
	 * @return ratio of the width divided by the height
	 */
	public double aspectRatio() {
		return width / height;
	}
	
	/**
	 * returns the smallest Rectangle with the same center as the receptor, with a new aspectRatio, containing the receptor
	 * @param newRatio
	 * @return the new Rectangle
	 * @throws IllegalArgumentException if the new Ratio is smaller or equal to zero.
	 */
	public Rectangle expandToAspectRatio(double newRatio)
			throws IllegalArgumentException {
		if(newRatio <= 0) {
			throw new IllegalArgumentException("Negative or null ratio");
		}
		
		if(aspectRatio() == newRatio) {
			return new Rectangle(this);
		}
		
		else if(aspectRatio() > newRatio) {
			return new Rectangle(center, width, width / newRatio);
		}
		
		else {
			return new Rectangle(center, height * newRatio, height);
		}
	}
	
	@Override
	public String toString() {
		return "(" + center.toString() + ", " + width + ", " + height + ")";
	}
}






