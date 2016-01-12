/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        21 févr. 2013
 */

package ch.epfl.flamemaker.ifs;

public final class IFSAccumulator {
	private final boolean[][] isHit;
	
	public IFSAccumulator(boolean[][] isHit) {
		this.isHit = isHit.clone();
	}
	
	public int width() {
		return isHit.length;
	}
	
	public int height() {
		return isHit[0].length;
	}
	
	public boolean isHit(int x, int y) {
		
		if(x >= width() || x < 0){
			throw new IndexOutOfBoundsException("Invalid x coordinate");
		}
		
		if(y >= height() || y < 0){
			throw new IndexOutOfBoundsException("Invalid y coordinate");
		}
		
		return isHit[x][y];
	}
}

