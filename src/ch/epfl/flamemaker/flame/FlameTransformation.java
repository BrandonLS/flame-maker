/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        26 févr. 2013
 */

package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * A weighted composition of Transformations
 */
public final class FlameTransformation implements Transformation {
	private final AffineTransformation affineTransfo;
	private final double[] variationWeight;
	
	/**
	 * Creates a FlameTransformation
	 * @param affineTransfo the AffineTransformation
	 * @param variationWeight the array of weights
	 * @throws IllegalArgumentException when size of the array of weights isn't equal to 6
	 */
	public FlameTransformation(AffineTransformation affineTransfo, double[] variationWeight) {
		this.affineTransfo = affineTransfo;
		
		if(variationWeight.length != 6) {
			throw new IllegalArgumentException("Size of variationWeight array not equal to 6");
		}
		this.variationWeight = variationWeight.clone();
	}

	/**
	 * Copy constructor
	 * @param f the FlameTransformation to copy
	 */
	public FlameTransformation(FlameTransformation f) {
		affineTransfo = new AffineTransformation(f.affineTransfo);
		variationWeight = f.variationWeight.clone();
	}


	@Override
	public Point transformPoint(Point p) {
		Point p1 = affineTransfo.transformPoint(p);
		double x = 0.0;
		double y = 0.0;
		
		for (int i = 0; i < 6; i++) {
			double w = variationWeight[i];
			
			if(w > 0) {
				Variation v = Variation.ALL_VARIATIONS.get(i);
				x += w * v.transformPoint(p1).x();
				y += w * v.transformPoint(p1).y();
			}
		}
		
		return new Point(x, y);
	}
	
	/**
	 * A builder for FlameTransformation, wich allows us to build a FlameTransformation 
	 * in an incremented way while keeping the immutability of FlameTransformation.
	 */
	public static final class Builder {
		private double[] variationWeight;
		private AffineTransformation affineTransfo;
		
		/**
		 * Creates a Builder
		 * @param f the FlameTransformation to build from.
		 */
		public Builder(FlameTransformation f) {
			variationWeight = f.variationWeight.clone();
			affineTransfo = new AffineTransformation(f.affineTransfo);
		}
		
		/**
		 * @param variation the variation to extract the weight from
		 * @return the weight of the variation.
		 */
		public double variationWeight(Variation variation) {
			return variationWeight[variation.index()];
		}

		/**
		 * Sets a new weight to the variation given
		 * @param variation the variation to set the new weight to
		 * @param newWeight the new weight
		 */
		public void setVariationWeight(Variation variation, double newWeight) {
			variationWeight[variation.index()] = newWeight;
		}
		
		/**
		 * @return the AffineTransformation
		 */
		public AffineTransformation affineTransformation() {
			return affineTransfo;
		}

		/**
		 * Sets an AffineTransformation
		 * @param a the AffineTransformation to set to
		 */
		public void setAffineTransformation(AffineTransformation a) {
			affineTransfo = a;
		}
		
		/**
		 * @return the builded finished FlameTransformation
		 */
		public FlameTransformation build() {
			return new FlameTransformation(affineTransfo, variationWeight); 
		}
	}
}

