/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;
import ch.epfl.flamemaker.color.*;

/**
 * An accumulator to store the number of hits and the sum of the index of colors in each square
 */
public final class FlameAccumulator {
	private final int[][] hitCount;
	private final double[][] colorIndexSum;
	private final double denominator;
	
	/**
	 * Creates a FlameAccumulator
	 * @param h the two dimension array of the number of hits in each square
	 * @param colorIndexSum the two dimension array of the sum of the index of colors in each square
	 */
	private FlameAccumulator(int[][] h, double[][] colorIndexSum) {
		hitCount = h.clone();
		this.colorIndexSum = colorIndexSum.clone();
		denominator = Math.log(max(h) + 1);
	}
	
	/**
	 * Gets the max value in a two dimension array
	 * @param tab the two dimension array
	 * @return the max found
	 */
	private int max(int[][] tab) {
		int max = 0;
		
		for (int i = 0; i < tab.length; i++) {
			for (int j = 0; j < tab[0].length; j++) {
				if(tab[i][j] > max) {
					max = tab[i][j];
				}
			}
		}
		
		return max;
	}
	
	/**
	 * @return the width of the accumulator
	 */
	public int width() {
		return hitCount.length;
	}
	
	/**
	 * @return the height of the accumulator
	 */
	public int height() {
		return hitCount[0].length;
	}
	
	/**
	 * Computes the color of the (x,y) square
	 * @param palette the Palette in wich the color is
	 * @param background the background color
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @throws IndexOutOfBoundsException if (x,y) isn't a point in the accumulator
	 * @return the computed color
	 */
	public Color color(Palette palette, Color background, int x, int y) {
		if(x < 0 || x >= width()) {
			throw new IndexOutOfBoundsException("Invalid x coordinate");
		}
		if(y < 0 || y >= height()) {
			throw new IndexOutOfBoundsException("Invalid y coordinate");
		}
		int hits = hitCount[x][y];
		
		if(hits <= 0) {
			return background; // new Color(background) ?
		}
		
		double index = colorIndexSum[x][y] / hits;
		Color c = palette.colorForIndex(index);
		
		return background.mixWith(c, intensity(x,y));
	}
	
	/**
	 * Computes the intensity of the square at (x,y) position via a logarithmic fomula
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the computed intensity
	 */
	private double intensity(int x, int y) {		
		return Math.log(hitCount[x][y] + 1) / denominator;
	}
	
	
	/**
	 * A builder for FlameAccumulator, wich allows us to build a FlameAccumulator 
	 * in an incremented way while keeping the immutability of FlameAccumulator.
	 */
	public static final class Builder {	
		public static final int PERCENTAGE_STEP = 1;
		private int buildingPercent = 0;
		
		private int[][] accumulator;
		private double[][] colorIndexSum;
		private AffineTransformation toAccumulator;
		private Rectangle frame;
		
		
		/**
		 * Creates a Builder
		 * @param frame the frame wich gets hit by the points
		 * @param width the width of the accumulator
		 * @param height the height of the accumulator
		 * @throws IllegalArgumentException if width or height is smaller or equal to zero
		 */
		public Builder(Rectangle frame, int width, int height) {

			if(width <= 0 || height <= 0) {
				throw new IllegalArgumentException("Negative or null width or height");
			}
			
			accumulator = new int[width][height];
			colorIndexSum = new double[width][height];
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
		
		/**
		 * Returns the building percentage
		 * @return the building percentage
		 */
		public int buildingPercent() {
			return buildingPercent;
		}
		
		/**
		 * Returns true if the building process is finished
		 * @return true if the building process is finished (when buildingPercent = 100)
		 */
		public boolean buildingFinished() {
			return buildingPercent == 100;
		}
		
		/**
		 * Sets the building percentage to zero
		 */
		public void setBuildingPercentToZero() {
			buildingPercent = 0;
		}
		
		/**
		 * Increments the building percentage by the percentage step (PERCENTAGE_STEP)
		 * @throws IllegalStateException if calling this method would create a percentage above 100
		 */
		public void incrementBuildingPercent(){
			if(buildingPercent > 100 - PERCENTAGE_STEP) {
				throw new IllegalStateException("Percentage > 100%");
			}
			buildingPercent += PERCENTAGE_STEP;
		}
		
		/**
		 * Hits the point p on the accumulator
		 * @param p the point we hit
		 * @param colorIndex the color the square gets hit with
		 */
		public void hit(Point p, double colorIndex) {
			if(frame.contains(p)) {
				Point newP = toAccumulator.transformPoint(p);
				int x = (int)Math.floor(newP.x());
				int y = (int)Math.floor(newP.y());
				
				// hits the square with the y-axis reversed
				accumulator[x][accumulator[0].length - 1 - y] += 1; 
				colorIndexSum[x][colorIndexSum[0].length - 1 - y] += colorIndex;
			}
		}
		
		/**
		 * @return the builded finished FlameAccumulator
		 */
		public FlameAccumulator build() {
			return new FlameAccumulator(accumulator, colorIndexSum);
		}

		/** 
		 * Clears the accumulator so that it's ready to be built again, with a new width, height and frame.
		 * @param newFrame the new frame
		 * @param newWidth the new width
		 * @param newHeight the new height
		 */
		public void clear(Rectangle newFrame, int newWidth, int newHeight) {
			frame = newFrame;
			accumulator = new int[newWidth][newHeight];
			colorIndexSum = new double[newWidth][newHeight];
			
			// translates lower left angle of the frame to the origin
			Point newCenter = new Point(frame.width()/2.0, frame.height()/2.0);
			AffineTransformation toOrigin = AffineTransformation.newTranslation(-(frame.center().x() - newCenter.x()),
																					 -(frame.center().y() - newCenter.y()));
			
			// looking for scaling coefficients
			double sX = newWidth / frame.width();
			double sY = newHeight / frame.height();
			
			// scales the frame to the size of the accumulator
			toAccumulator = new AffineTransformation(sX, 0, 0, 0, sY, 0).composeWith(toOrigin);
			
			setBuildingPercentToZero();
		}
	}
}
