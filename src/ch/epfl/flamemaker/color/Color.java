/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.color;

/**
 * Represents a RGB color
 *
 */
public final class Color {
	/*
	 * Static colors that are often used
	 */
	public static final Color BLACK = new Color(0, 0, 0);
	public static final Color WHITE  = new Color(1, 1, 1);
	public static final Color RED = new Color(1, 0, 0);
	public static final Color GREEN = new Color(0, 1, 0);
	public static final Color BLUE = new Color(0, 0, 1);
	
	
	private final double red;
	private final double green;
	private final double blue;
	
	/**
	 * Creates a RGB Color
	 * @param r the red factor
	 * @param g the green factor
	 * @param b the blue factor
	 */
	public Color(double r, double g, double b) {
		if(r < 0 || r > 1) {
			throw new IllegalArgumentException("Invalid red color");
		}
		
		if(g < 0 || g > 1) {
			throw new IllegalArgumentException("Invalid green color");
		}
		
		if(b < 0 || b > 1) {
			throw new IllegalArgumentException("Invalid blue color");
		}
		
		red = r;
		green = g;
		blue = b;
	}
	
	/**
	 * Copy constructor
	 * @param that the color to copy
	 */
	public Color(Color that) {
		red = that.red;
		green = that.green;
		blue = that.blue;
	}
	
	/**
	 * @return the blue factor of the color
	 */
	public double blue() {
		return blue;
	}
	
	/**
	 * @return the green factor of the color
	 */
	public double green() {
		return green;
	}
	
	/**
	 * @return the red factor of the color
	 */
	public double red() {
		return red;
	}
	
	/**
	 * Mixes two colors with a certain proportion
	 * @param that the color to mix with
	 * @param proportion the proportion of <strong>that</strong> in the mix
	 * @return the new mixed color
	 */
	public Color mixWith(Color that, double proportion) {
		if(proportion < 0 || proportion > 1) {
			throw new IllegalArgumentException("Invalid proportion");
		}
		
		return new Color(proportion * that.red + (1 - proportion) * red, proportion * that.green + (1 - proportion) * green, proportion * that.blue + (1 - proportion) * blue);
	}
	
	/**
	 * Packs the color in an integer with blue on the eight first bytes, green on the eight next, and red on the eight next.
	 * @return the integer
	 */
	public int asPackedRGB() {
		int n = sRGBEncode(blue, 255);
		n += sRGBEncode(green, 255) << 8;
		n += sRGBEncode(red, 255) << 16;
	
		return n;
	}
	
	/**
	 * Gamma-encodes the double <strong>v</strong> representing a color in an integer value between 0 and <strong>max</strong>
	 * @param v the double representing the color
	 * @param max the maximum value that can be returned
	 * @return the gamma-encoded value between 0 and <strong>max</strong>
	 */
	public static int sRGBEncode(double v, int max) {
		double newV = v;
		if(v <= 0.0031308 && v >= 0) {
			return (int)(max*v*12.92);
		}
		
		else if(newV > 0.0031308) {
			return (int)(max*(1.055*Math.pow(newV, 1.0/2.4)- 0.055));
		}
		
		else {
			throw new IllegalArgumentException("Invalid argument v");
		}
	}
}
