/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        21 févr. 2013
 */

package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {
	private boolean[][] accumulator;
	private final AffineTransformation toAccumulator;
	private final Rectangle frame;
	
	public IFSAccumulatorBuilder(Rectangle frame, int width, int height) 
		throws IllegalArgumentException {
		
		if(width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Negative or null width or height");
		}
		
		accumulator = new boolean[width][height];
		this.frame = new Rectangle(frame);
		
		// translates lower left angle of the frame to the origin
		Point newCenter = new Point(frame.width()/2.0, frame.height()/2.0);
		AffineTransformation toOrigin = AffineTransformation.newTranslation(-(frame.center().x() - newCenter.x()),
																				 -(frame.center().y() - newCenter.y()));
		
		// looking for scaling coefficients
		double sX = width / frame.width();
		double sY = height / frame.height();
		
		// scales the frame to the size of the accumulator
		toAccumulator = new AffineTransformation(sX, 0, 0, 0, sY, 0).composeWith(toOrigin);
	}
	
	public void hit(Point p) {
		if(frame.contains(p)) {
			Point newP = toAccumulator.transformPoint(p);
			int x = (int)Math.floor(newP.x());
			int y = (int)Math.floor(newP.y());
			
			// hits the square with the y-axis reversed
			accumulator[x][accumulator[0].length - 1 - y] = true; 
		}
	}
	
	public IFSAccumulator build() {
		return new IFSAccumulator(accumulator);
	}
	
//	 TEST de la transformation du constructeur
//	public static void main(String[] args){
//		int width = 5;
//		int height = 4;
//		
//		Rectangle frame = new Rectangle(new Point(10, 10), 5, 3);
//		
//		Point newCenter = new Point(frame.width()/2.0, frame.height()/2.0);
//		
//		AffineTransformation toOrigin = AffineTransformation.newTranslation(-(frame.center().x() - newCenter.x()),
//				 -(frame.center().y() - newCenter.y()));
//		
//		double sX = width / frame.width();
//		double sY = height / frame.height();
//		
//		
//		Point p = new Point(12.5, 10);
//		
//		System.out.println(new AffineTransformation(sX, 0, 0, 0, sY, 0).composeWith(toOrigin).transformPoint(p));
//	}
}
