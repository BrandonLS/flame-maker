/*
 *	Author:      Timothée Lottaz &  Brandon Le Sann
 *	Date:        28 févr. 2013
 */

package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * A Flame fractal defined by a list of FlameTransformation
 */
public final class Flame {
	private final List<FlameTransformation> list;
	
	private double actualColorIndex = 0;
	private Point actualPoint = Point.ORIGIN;
	
	/**
	 * Creates a Flame fractal
	 * @param l the list to create the Flame fractal from
	 */
	public Flame(List<FlameTransformation> l) {
		List<FlameTransformation> listTemp = new ArrayList<FlameTransformation>();
		
		for (FlameTransformation i : l) {
			listTemp.add(i);
		}
		
		list = listTemp;
	}
	

	/**
	 * Copy constructor
	 * @param f the Flame to copy
	 */
	public Flame(Flame f) {
		list = new ArrayList<FlameTransformation>();
		
		for (FlameTransformation i : f.list) {
			list.add(new FlameTransformation(i));
		}
	}
	
	/* 
	 * Statics methods, to creates preset fractals
	 */
	public static Flame createSharkFin() {
		ArrayList<FlameTransformation> list = new ArrayList<FlameTransformation>();
		double[] w = {1, 0.1, 0, 0, 0, 0};
		list.add(new FlameTransformation(new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8), w));
		
		double[] w2 = {0, 0, 0, 0, 0.8, 1};
		list.add(new FlameTransformation(new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), w2));
		
		double[] w3 = {1, 0, 0, 0, 0, 0};
		list.add(new FlameTransformation(new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9), w3));
		
		return new Flame(list);
	}
	
	public static Flame createTurbulence() {
		ArrayList<FlameTransformation> list = new ArrayList<FlameTransformation>();
		double[] w = {0.5, 0, 0, 0.4, 0, 0};
		list.add(new FlameTransformation(new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7), w));
		
		double[] w2 = {1, 0, 0.1, 0, 0, 0};
		list.add(new FlameTransformation(new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3), w2));
		
		double[] w3 = {1, 0, 0, 0, 0, 0};
		list.add(new FlameTransformation(new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3), w3));
		
		return new Flame(list);
	}
	
	public static Flame createTriangle() {
		ArrayList<FlameTransformation> list = new ArrayList<FlameTransformation>();
		double[] w = {1, 0, 0, 0, 0, 0};
		list.add(new FlameTransformation(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0), w));
		
		list.add(new FlameTransformation(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0), w));
		
		list.add(new FlameTransformation(new AffineTransformation(0.5, 0, 0.25, 0, 0.5, 0.5), w));
		
		return new Flame(list);
	}

	/**
	 * Computes the fractal in a FlameAccumulator
	 * @param frame the frame to hit
	 * @param width the width of the accumulator
	 * @param height the height of the accumulator
	 * @param density a coefficient that increases the amount of hits
	 * @return the computed FlameAccumulator
	 */
	public FlameAccumulator computeAll(Rectangle frame, int width, int height, int density) {
		int iterations = width*height*density;
		
		FlameAccumulator.Builder builder = new FlameAccumulator.Builder(frame, width, height);
		
		Point p = Point.ORIGIN;		
		Random random = new Random(2013);
		
		double colorIndex = 0.0;
		
		// 20 first hits don't count
		for (int i = 0; i < 20; i++) {
			// a random number of transformation from the list
			int j = random.nextInt(list.size());
			p = list.get(j).transformPoint(p);
			
			colorIndex = 0.5*(indexColorOfTransformation(j) + colorIndex);
		}
		
		// next hits do count
		for (int i = 20; i < iterations; i++) {
			// a random number of transformation from the list
			int j = random.nextInt(list.size());
			p = list.get(j).transformPoint(p);
			
			colorIndex = 0.5*(indexColorOfTransformation(j) + colorIndex);
			
			// hitting the accumulator
			builder.hit(p, colorIndex);
			
			if(i % (iterations / 10) == 0) {
				System.out.println(((int)(i / (double)iterations * 100) + "%"));
			}
		}
		
		return builder.build();
	}
	
	public boolean computePart(int iterations, FlameAccumulator.Builder builder) {
		Random random = new Random();

		int firstIteration = (int)(iterations / 100.0 * builder.buildingPercent());
		int lastIteration = (int)(firstIteration + iterations * FlameAccumulator.Builder.PERCENTAGE_STEP / 100.0);
		
		// next hits do count
		for (int i = firstIteration; i < lastIteration; i++) {
			// a random number of transformation from the list
			int j = random.nextInt(list.size());
			actualPoint = list.get(j).transformPoint(actualPoint);
			
			actualColorIndex = 0.5*(indexColorOfTransformation(j) + actualColorIndex);
			
			// hitting the accumulator
			if(i > 20) {
				builder.hit(actualPoint, actualColorIndex);
			}
		}
		
		builder.incrementBuildingPercent();
		
		return builder.buildingFinished();
	}
	
	/**
	 * Finds the color index of a Transformation depending on its place in the list
	 * @param indexTransfo index of the Transformation to get the color index from
	 * @return the color index
	 */
	private double indexColorOfTransformation (int indexTransfo) {
		if(indexTransfo == 0) {
			return 0;
		}
		
		else if(indexTransfo == 1) {
			return 1;
		}
		
		else {
			int line = (int)Math.ceil(Math.log(indexTransfo) / Math.log(2));
			double premier = 1.0 / (Math.pow(2, line));
			return premier + ((indexTransfo - Math.pow(2, line - 1) - 1) * 2 * premier);
		}
	}
	
	/**
	 * A builder for Flame, wich allows us to build a Flame 
	 * in an incremented way while keeping the immutability of Flame.
	 */
	public static final class Builder {
		private ArrayList<FlameTransformation.Builder> builders = new ArrayList<FlameTransformation.Builder>();
		
		/**
		 * Creates a Builder
		 * @param flame the Flame to construct the Builder from
		 */
		public Builder(Flame flame) {
			for (FlameTransformation f : flame.list) {
				builders.add(new FlameTransformation.Builder(f));
			}
		}
		
		/**
		 * @return the number of the transformations
		 */
		public int transformationCount() {
			return builders.size();
		}
		
		/**
		 * Adds a Builder of the FlameTransformation given to the list of builders
		 * @param transformation
		 */
		public void addTransformation(FlameTransformation transformation) {
			builders.add(new FlameTransformation.Builder(transformation));
		}
		
		/**
		 * Returns the AffineTransformation at given index
		 * @param index the index given
		 * @return the AffineTransformation
		 */
		public AffineTransformation affineTransformation(int index) {			
			return builders.get(index).affineTransformation();
		}
		
		/**
		 * Sets the AffineTransformation at index given by a new AffineTransformation
		 * @param index the index of the Transformation to set
		 * @param newTransfo the new AffineTransformation to set to
		 * @throws IndexOutOfBoundsException if the index is greater or equal 
		 * to the amount of transformations or smaller than zero
		 */
		public void setAffineTransformation(int index, AffineTransformation newTransfo) {
			if(index < 0 || index >= transformationCount()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index);				
			}
			
			builders.get(index).setAffineTransformation(newTransfo);
		}
		
		/**
		 * Gives the weight of the given variation of the FlameTransformation.Builder at given index
		 * @param index the index of the FlameTransformation.Builder
		 * @param variation the variation to get the weight from
		 * @throws IndexOutOfBoundsException if the index is greater or equal 
		 * to the amount of transformations or smaller than zero
		 * @return the weight found
		 */
		public double variationWeight(int index, Variation variation) {
			if(index < 0 || index >= transformationCount()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index);				
			}
			
			return builders.get(index).variationWeight(variation);
		}
		
		/**
		 * Sets the weight of the given variation of the FlameTransformation.Builder at given index
		 * @param index the index of the FlameTransformation.Builder
		 * @param variation the variation to set the weight of
		 * @param newWeight the new weight to set to
		 * @throws IndexOutOfBoundsException if the index is greater or equal 
		 * to the amount of transformations or smaller than zero
		 */
		public void setVariationWeight(int index, Variation variation, double newWeight) {
			if(index < 0 || index >= transformationCount()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index);				
			}
			
			builders.get(index).setVariationWeight(variation, newWeight);
		}
		
		/**
		 * Removes the FlameTransformation.Builder at given index from the builders
		 * @param index the index of the Builder to remove
		 * @throws IndexOutOfBoundsException if the index is greater or equal 
		 * to the amount of transformations or smaller than zero
		 */
		public void removeTransformation(int index) {
			if(index < 0 || index >= transformationCount()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index);				
			}
			
			builders.remove(index);
		}
		
		/**
		 * @return the builded finished Flame fractal
		 */
		public Flame build() {
			List<FlameTransformation> l = new ArrayList<FlameTransformation>();
			for (FlameTransformation.Builder f : builders) {
				l.add(f.build());
			}
			return new Flame(l);
		}
	}
}




