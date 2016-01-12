/*
 *	Author:      TimothÃ©e Lottaz & Brandon Le Sann
 *	Date:        20 fÃ©vr. 2013
 */

package ch.epfl.flamemaker.ifs;

import java.util.*;
import ch.epfl.flamemaker.geometry2d.*;

public final class IFS {
	private final List<AffineTransformation> list = new ArrayList<AffineTransformation>();
	
	public IFS(List<AffineTransformation> pTransformations) {
		for (AffineTransformation i : pTransformations) {
			list.add(i);
		}
	}
	
	public IFSAccumulator compute(Rectangle frame, int width, int height, int density) {
		int iterations = width*height*density;
		
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(frame, width, height);
		
		Point p = new Point(0,0);
		Random random = new Random();
		
		// 20 first hits don't count
		for (int i = 0; i < 20; i++) {
			// a random numer of transformation from the list
			int j = random.nextInt(list.size());
			p = list.get(j).transformPoint(p);
		}
		
		// next hits do count
		for (int i = 20; i < iterations; i++) {
			// a random numer of transformation from the list
			int j = random.nextInt(list.size());
			p = list.get(j).transformPoint(p);
			
			// hitting the accumulator
			builder.hit(p);
		}
		
		return builder.build();
	}
	
}