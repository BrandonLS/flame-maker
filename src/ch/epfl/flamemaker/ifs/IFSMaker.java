/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        25 févr. 2013
 */

package ch.epfl.flamemaker.ifs;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSMaker {
	
	public static void main(String[] args) {
		// initialises a list for each fractal
		ArrayList<AffineTransformation> listSierpinskiTriangle = new ArrayList<AffineTransformation>();
		ArrayList<AffineTransformation> listFern = new ArrayList<AffineTransformation>();
		ArrayList<AffineTransformation> listSierpinskiCarpet = new ArrayList<AffineTransformation>();
		ArrayList<AffineTransformation> listDragon = new ArrayList<AffineTransformation>();
		ArrayList<AffineTransformation> listTree = new ArrayList<AffineTransformation>();
		
		// fills the lists with the differents transformations associated to the fractal 
		listSierpinskiTriangle.add(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0));
		listSierpinskiTriangle.add(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0));
		listSierpinskiTriangle.add(new AffineTransformation(0.5, 0, 0.25, 0, 0.5, 0.5));
		
		listFern.add(new AffineTransformation(0, 0, 0, 0, 0.16, 0));
		listFern.add(new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6));
		listFern.add(new AffineTransformation(-0.15, 0.28, 0, 0.26, 0.24, 0.44));
		listFern.add(new AffineTransformation(0.85, 0.04, 0, -0.04, 0.85, 1.6));
		
		double oneThird = 1.0/3.0;
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, 0, 0, oneThird, 0));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, oneThird, 0, oneThird, 0));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, 0, 0, oneThird, oneThird));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, 2*oneThird, 0, oneThird, 0));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, 0, 0, oneThird, 2*oneThird));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, oneThird, 0, oneThird, 2*oneThird));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, 2*oneThird, 0, oneThird, oneThird));
		listSierpinskiCarpet.add(new AffineTransformation(oneThird, 0, 2*oneThird, 0, oneThird, 2*oneThird));
		
		listDragon.add(new AffineTransformation(0.5, 0.5, 0.125, -0.5, 0.5, 0.625));
		listDragon.add(new AffineTransformation(0.5, 0.5, -0.125, -0.5, 0.5, 0.375));
		
		listTree.add(new AffineTransformation(0.42, 0.42, 0, -0.42, 0.42, 0.2));
		listTree.add(new AffineTransformation(0.42, -0.42, 0, 0.42, 0.42, 0.2));
		listTree.add(new AffineTransformation(0.1, 0, 0, 0, 0.1, 0.2));
		listTree.add(new AffineTransformation(0, 0, 0, 0, 0.5, 0));
		
		// creates the fractals with associated list
		IFS ifsSierpinskiTriangle = new IFS(listSierpinskiTriangle);
		IFS ifsFern = new IFS(listFern);
		IFS ifsSierpinskiCarpet= new IFS(listSierpinskiCarpet);
		IFS ifsDragon = new IFS(listDragon);
		IFS ifsTree = new IFS(listTree);
		
		// computes the accumulators
		IFSAccumulator accuSierpinskiTriangle = ifsSierpinskiTriangle.compute(new Rectangle(new Point(0.5,0.5), 1, 1), 300, 300, 1);
		IFSAccumulator accuFern = ifsFern.compute(new Rectangle(new Point(0,4.5), 6, 10), 120, 200, 150);
		IFSAccumulator accuSierpinskiCarpet = ifsSierpinskiCarpet.compute(new Rectangle(new Point(0.5,0.5), 1, 1), 300, 300, 50);
		IFSAccumulator accuDragon = ifsDragon.compute(new Rectangle(new Point(0.5,0.5), 1, 1), 300, 300, 50);
		IFSAccumulator accuTree = ifsTree.compute(new Rectangle(new Point(0,0), 1, 1), 300, 300, 10);
		
		
		// draws the fractal in a .pbm file
		PBM(accuSierpinskiTriangle, "sierpinskiTriangle.pbm");
		PBM(accuFern, "fern.pbm");
		PBM(accuSierpinskiCarpet, "sierpinskiCarpet.pbm");
		PBM(accuDragon, "dragon.pbm");
		PBM(accuTree, "tree.pbm");
	}
	
	private static void PBM(IFSAccumulator accumulator, String fileName){
		try{
			PrintStream outPut = new PrintStream("fractales/PBM/" + fileName);
			
			outPut.println("P1");
			outPut.println(accumulator.width() + " " + accumulator.height());
			
			for (int i = 0; i < accumulator.height(); i++) {
				for (int j = 0; j < accumulator.width(); j++) {
					boolean b = accumulator.isHit(j, i);
					if(b){
						outPut.print("1 ");
					}
					
					else{
						outPut.print("0 ");
					}
				}
				outPut.println();
			}
			
			outPut.close();
			System.out.println("\"" + fileName + "\" was sucessfully written");
		}
		
		catch(FileNotFoundException e){
			System.out.println("Error when creating or modifying the file : " + e.getMessage());
		}
		
	}
}
