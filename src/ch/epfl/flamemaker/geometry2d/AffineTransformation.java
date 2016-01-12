/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        19 févr. 2013
 */

package ch.epfl.flamemaker.geometry2d;

/**
 * A Transformation of the type "Affine"
 */
public final class AffineTransformation implements Transformation {
	public final static AffineTransformation IDENTITY = new AffineTransformation(1, 0, 0, 0, 1, 0);
	
	private final double a, b, c, d, e, f;
	

	/**
	 * Creates a Affine Transoformation in the form of a matrix
	 * @param a is the value of the point (1,1) of the matrix
	 * @param b is the value of the point (1,2) of the matrix
	 * @param c is the value of the point (1,3) of the matrix
	 * @param d is the value of the point (2,1) of the matrix
	 * @param e is the value of the point (2,2) of the matrix
	 * @param f is the value of the point (2,3) of the matrix
	 */
	public AffineTransformation(double a, double b, double c, double d, double e, double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}
	
	/**
	 * Copy constructor
	 * @param af is the Affine Transformation to copy
	 */
	public AffineTransformation(AffineTransformation af) {
		a = af.a;
		b = af.b;
		c = af.c;
		d = af.d;
		e = af.e;
		f = af.f;
	}

	/**
	 * Creates a new translation
	 * @param dx the x param of the translation
	 * @param dy the y param of the translation
	 * @return the translation created
	 */
	public static AffineTransformation newTranslation(double dx, double dy) {
		return new AffineTransformation(1, 0, dx, 0, 1, dy);
	}
	
	/**
	 * Creates a new rotation
	 * @param theta the angle of the rotation
	 * @return the rotation created
	 */
	public static AffineTransformation newRotation(double theta) {
		return new AffineTransformation(Math.cos(theta), -Math.sin(theta), 0, Math.sin(theta), Math.cos(theta), 0);
	}
	
	/**
	 * Creates a new scaling
	 * @param sx the x param of the scaling
	 * @param sy the y param of the scaling
	 * @return the scaling created
	 */
	public static AffineTransformation newScaling(double sx, double sy) {
		return new AffineTransformation(sx, 0, 0, 0, sy, 0);
	}
	
	/**
	 * Creates a new shear over x
	 * @param sx the param of the shear over x
	 * @return the shear over x created
	 */
	public static AffineTransformation newShearX(double sx) {
		return new AffineTransformation(1, sx, 0, 0, 1, 0);
	}
	
	/**
	 * Creates a new shear over y
	 * @param sy the param of the shear over y
	 * @return the shear over y created
	 */
	public static AffineTransformation newShearY(double sy) {
		return new AffineTransformation(1, 0, 0, sy, 1, 0);
	}
	
	/**
	 * @return the x coordinate of the translation (3rd value in the matrix)
	 */
	public double translationX() {
		return c;
	}

	/**
	 * @return the y coordinate of the translation (6th value in the matrix)
	 */
	public double translationY() {
		return f;
	}

	@Override
	public Point transformPoint(Point p) {
		return new Point(a*p.x() + b*p.y() + c, d*p.x() + e*p.y() + f);
	}
	
	/**
	 * Composes an Affine Transformation with another
	 * @param that the function to compose this one with
	 * @return the composition of the Affine Transformations
	 */
	public AffineTransformation composeWith(AffineTransformation that) {
		return new AffineTransformation(
				a*that.a + b*that.d, 
				a*that.b + b*that.e, 
				a*that.c + b*that.f + c, 
				d*that.a + e*that.d, 
				d*that.b + e*that.e, 
				d*that.c + e*that.f + f
				);
	}
}




