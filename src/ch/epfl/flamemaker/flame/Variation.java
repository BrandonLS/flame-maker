/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        26 févr. 2013
 */

package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;
import ch.epfl.flamemaker.geometry2d.*;

/**
 * A specific Transformation wich can be : 
 * Linear, Sinusoidal, Spherical, Swirl, Horseshoe or Bubble
 */
public abstract class Variation implements Transformation {
    
	/**
	 * The list of all the Variations possible
	 */
	public final static List<Variation> ALL_VARIATIONS =
            Arrays.asList(
            		new Variation(0, "Linear") {

    					@Override
    					public Point transformPoint(Point p) {
    						return new Point(p.x() , p.y());
    					}
            		},
            				
            		new Variation(1, "Sinusoidal") {

    					@Override
    					public Point transformPoint(Point p) {
    						return new Point(Math.sin(p.x()) , Math.sin(p.y()));
    					}
            		},
            		
            		 new Variation(2, "Spherical") {

    					@Override
    					public Point transformPoint(Point p) {
    						double rSquared = p.r()*p.r();
    						
    						return new Point(p.x()/(rSquared) , p.y()/(rSquared));
    					}
            		 },
            		 
            		 new Variation(3, "Swirl") {
            			 
            			@Override
            			public Point transformPoint(Point p) {
            				double x = p.x();
            				double y = p.y();
            				double r = p.r();
            				double rSquared = r*r;
            				
            				return new Point(x*Math.sin(rSquared) - y*Math.cos(rSquared) , x*Math.cos(rSquared) + y*Math.sin(rSquared));
            			}
            		 },
            		 
            		 new Variation(4, "Horseshoe") {
            			
            			@Override
            			public Point transformPoint(Point p) {
            				double x = p.x();
            				double y = p.y();
            				double r = p.r();
            				
            				return new Point((x - y)*(x + y) / r , 2*x*y / r);
            			}
            		 },
            		 
            		 new Variation(5, "Bubble") {

            			@Override
            			public Point transformPoint(Point p) {
            				double rSquared = p.r()*p.r();
            				
            				return new Point(4*p.x()/(rSquared + 4) , 4*p.y()/(rSquared + 4));
            			} 
            		 }
            			);
	
	private final String name;
    private final int index;
    
    /**
     * Creates a Variation
     * @param index the index of the Variation in the list
     * @param name the name of the Variation
     */
    private Variation(int index, String name) {
    	this.index = index;
    	this.name = name;
    }

    /**
     * @return the name
     */
    public String name() { 
    	return name;
    }
    
    /**
     * @return the index
     */
    public int index() {
    	return index;
    }

    @Override
    abstract public Point transformPoint(Point p);
}
