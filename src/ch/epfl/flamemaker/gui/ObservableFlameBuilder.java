package ch.epfl.flamemaker.gui;

import java.util.HashSet;

import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;

public class ObservableFlameBuilder {
	private final Flame.Builder builder;
	private HashSet<Observer> observers = new HashSet<Observer>();

	public ObservableFlameBuilder(Flame f) {
		builder = new Flame.Builder(f);
	}
	
	public void addObserver(Observer o) {
		observers.add(o);
	}
	
	public void removeObserver(Observer o) {
		observers.remove(o);
	}
	
	/**
	 * removes all the observers
	 */
	public void removeAllObservers() {
		observers.clear();
	}
	
	private void notifyObservers() {
		for (Observer o : observers) {
			o.updateFractal();
		}
	}
	
	/**
	 * @return the number of the transformations
	 */
	public int transformationCount() {
		return builder.transformationCount();
	}
	
	/**
	 * Adds a Builder of the FlameTransformation given to the list of builders
	 * @param transformation
	 */
	public void addTransformation(FlameTransformation transformation) {
		builder.addTransformation(transformation);
		notifyObservers();
	}
	
	/**
	 * Returns the AffineTransformation at given index
	 * @param index the index given
	 * @return the AffineTransformation
	 */
	public AffineTransformation affineTransformation(int index) {			
		return builder.affineTransformation(index);
	}
	
	/**
	 * Sets the AffineTransformation at index given by a new AffineTransformation
	 * @param index the index of the Transformation to set
	 * @param newTransfo the new AffineTransformation to set to
	 * @throws IndexOutOfBoundsException if the index is greater or equal 
	 * to the amount of transformations or smaller than zero
	 */
	public void setAffineTransformation(int index, AffineTransformation newTransfo) {		
		builder.setAffineTransformation(index, newTransfo);
		notifyObservers();
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
		return builder.variationWeight(index, variation);
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
		builder.setVariationWeight(index, variation, newWeight);
		notifyObservers();
	}
	
	/**
	 * Removes the FlameTransformation.Builder at given index from the builders
	 * @param index the index of the Builder to remove
	 * @throws IndexOutOfBoundsException if the index is greater or equal 
	 * to the amount of transformations or smaller than zero
	 */
	public void removeTransformation(int index) {
		builder.removeTransformation(index);
		notifyObservers();
	}
	
	/**
	 * @return the builded finished Flame fractal
	 */
	public Flame build() {
		return builder.build();
	}
	
	public interface Observer {
		public void updateFractal();
	}
}

