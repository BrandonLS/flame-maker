package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ch.epfl.flamemaker.color.*;
import ch.epfl.flamemaker.flame.*;

@SuppressWarnings("serial")
public final class FlameBuilderPreviewComponent extends JComponent {
	public final static int PREFERRED_WIDTH = 400;
	public final static int PREFERRED_HEIGHT = 200;
	
	private final Color background;
	private final Palette palette;
	
	private FlameAccumulator.Builder accuBuilder;
	

	public FlameBuilderPreviewComponent(FlameAccumulator.Builder b, Color backg, Palette p) {
		accuBuilder = b;
		background = backg;
		palette = p;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT);
	}
	
	@Override
	public void paintComponent(Graphics g0) {
		Graphics2D g2D = (Graphics2D) g0;
		
		// Creates a buffered image
		BufferedImage bImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		
		FlameAccumulator accumulator = accuBuilder.build();
		// Fills in the image with the right colors, packed in an Integer
		for (int i = 0; i < accumulator.height(); i++) {
			for (int j = 0; j < accumulator.width(); j++) {
				Color c = accumulator.color(palette, background, j, i);
				bImage.setRGB(j, i, c.asPackedRGB());
			}
		}
		
		// Draws the image
		g2D.drawImage(bImage, 0, 0, null);
	}
}
