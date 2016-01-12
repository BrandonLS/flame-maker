package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import ch.epfl.flamemaker.geometry2d.*;
import ch.epfl.flamemaker.gui.ObservableFlameBuilder.Observer;

@SuppressWarnings("serial")
public final class AffineTransformationsComponent extends JComponent {
	private final static int PREFFERED_WIDTH = 400;
	private final static int PREFFERED_HEIGHT = 200;
	
	private ObservableFlameBuilder builder;
	private Rectangle frame;
	private int highlightedTransformationIndex;

	public AffineTransformationsComponent(ObservableFlameBuilder b, Rectangle f) {
		builder = b;
		frame = f;
		builder.addObserver(new Observer() {
			
			@Override
			public void updateFractal() {
				repaint();
			}
		});
	}
	
	public int getHighlightedTransformationIndex() {
		return highlightedTransformationIndex;
	}
	
	public void setHighlightedTransformationIndex(int index) {
		if(index < 0 || index >= builder.transformationCount()) {
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		}
		highlightedTransformationIndex = index;
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREFFERED_WIDTH, PREFFERED_HEIGHT);
	}
	
	@Override
	public void paintComponent(Graphics g0){
		Graphics2D g2D = (Graphics2D) g0;
		
		// Creates a frame with the ajusted size of the component with the same ratio as the image
		Rectangle newFrame = frame.expandToAspectRatio(((double)getWidth()) / getHeight());
		
		// Default color is grey
		g2D.setColor(new Color(0.9f, 0.9f, 0.9f));
		
		// The transformation to the coordinates in the component
		AffineTransformation toComponent = new AffineTransformation(getWidth() / newFrame.width(), 0, 0, 0, getHeight() / newFrame.height(), 0);
		toComponent = toComponent.composeWith(new AffineTransformation(1, 0, 0, 0, -1, 0));
		toComponent = toComponent.composeWith(new AffineTransformation(1, 0, -newFrame.left(), 0, 1, -newFrame.top()));
		
		// Draws vertical lines of the grid
		for(int x = (int)Math.ceil(newFrame.left()); x < newFrame.right(); x++) {
			Point p1 = toComponent.transformPoint(new Point(x, newFrame.bottom()));
			Point p2 = toComponent.transformPoint(new Point(x, newFrame.top()));
			
			if(x == 0) {
				g2D.setColor(Color.WHITE);
				g2D.draw(new Line2D.Double(p1.x(), p1.y(), p2.x(), p2.y()));
				g2D.setColor(new Color(0.9f, 0.9f, 0.9f));
			}
			
			else {
				g2D.draw(new Line2D.Double(p1.x(), p1.y(), p2.x(), p2.y()));
			}
		}
		
		// Draws horizontal lines of the grid
		for(int y = (int)Math.ceil(newFrame.bottom()); y < newFrame.top(); y++) {
			Point p1 = toComponent.transformPoint(new Point(newFrame.left(), y));
			Point p2 = toComponent.transformPoint(new Point(newFrame.right(), y));
			
			if(y == 0) {
				g2D.setColor(Color.WHITE);
				g2D.draw(new Line2D.Double(p1.x(), p1.y(), p2.x(), p2.y()));
				g2D.setColor(new Color(0.9f, 0.9f, 0.9f));
			}
			
			else {
				g2D.draw(new Line2D.Double(p1.x(), p1.y(), p2.x(), p2.y()));
			}
		}
		
		for (int i = 0; i < builder.transformationCount(); i++) {
			AffineTransformation affineTransfo = builder.affineTransformation(i);
			
			g2D.setColor(Color.BLACK);
			
			if(i != highlightedTransformationIndex) {
				// The arrow initially on the x-axis
				Point head = new Point(1, 0);
				Point head2 = new Point(0, 1);
				Point tail = new Point(-1, 0);
				Point tail2 = new Point(0, -1);
				Point left = new Point(0.9, 0.1);
				Point left2 = new Point(-0.1, 0.9);
				Point right = new Point(0.9, -0.1);
				Point right2 = new Point(0.1, 0.9);
				
				head = affineTransfo.transformPoint(head);
				head = toComponent.transformPoint(head);				
				
				tail = affineTransfo.transformPoint(tail);
				tail = toComponent.transformPoint(tail);
				
				left = affineTransfo.transformPoint(left);
				left = toComponent.transformPoint(left);
				
				right = affineTransfo.transformPoint(right);
				right = toComponent.transformPoint(right);
				
				head2 = affineTransfo.transformPoint(head2);
				head2 = toComponent.transformPoint(head2);				
				
				tail2 = affineTransfo.transformPoint(tail2);
				tail2 = toComponent.transformPoint(tail2);
				
				left2 = affineTransfo.transformPoint(left2);
				left2 = toComponent.transformPoint(left2);
				
				right2 = affineTransfo.transformPoint(right2);
				right2 = toComponent.transformPoint(right2);
				
				g2D.draw(new Line2D.Double(head.x(), head.y(), tail.x(), tail.y()));
				g2D.draw(new Line2D.Double(head.x(), head.y(), left.x(), left.y()));
				g2D.draw(new Line2D.Double(head.x(), head.y(), right.x(), right.y()));
				g2D.draw(new Line2D.Double(head2.x(), head2.y(), tail2.x(), tail2.y()));
				g2D.draw(new Line2D.Double(head2.x(), head2.y(), left2.x(), left2.y()));
				g2D.draw(new Line2D.Double(head2.x(), head2.y(), right2.x(), right2.y()));
			}
		}
		
		// The highlighted transformation must be drawn at the end
		AffineTransformation affineTransfo = builder.affineTransformation(highlightedTransformationIndex);
		
		g2D.setColor(Color.RED);
		
		// The arrow initially on the x-axis
		Point head = new Point(1, 0);
		Point head2 = new Point(0, 1);
		Point tail = new Point(-1, 0);
		Point tail2 = new Point(0, -1);
		Point left = new Point(0.9, 0.1);
		Point left2 = new Point(-0.1, 0.9);
		Point right = new Point(0.9, -0.1);
		Point right2 = new Point(0.1, 0.9);
		
		head = affineTransfo.transformPoint(head);
		head = toComponent.transformPoint(head);				
		
		tail = affineTransfo.transformPoint(tail);
		tail = toComponent.transformPoint(tail);
		
		left = affineTransfo.transformPoint(left);
		left = toComponent.transformPoint(left);
		
		right = affineTransfo.transformPoint(right);
		right = toComponent.transformPoint(right);
		
		head2 = affineTransfo.transformPoint(head2);
		head2 = toComponent.transformPoint(head2);				
		
		tail2 = affineTransfo.transformPoint(tail2);
		tail2 = toComponent.transformPoint(tail2);
		
		left2 = affineTransfo.transformPoint(left2);
		left2 = toComponent.transformPoint(left2);
		
		right2 = affineTransfo.transformPoint(right2);
		right2 = toComponent.transformPoint(right2);
		
		g2D.draw(new Line2D.Double(head.x(), head.y(), tail.x(), tail.y()));
		g2D.draw(new Line2D.Double(head.x(), head.y(), left.x(), left.y()));
		g2D.draw(new Line2D.Double(head.x(), head.y(), right.x(), right.y()));
		g2D.draw(new Line2D.Double(head2.x(), head2.y(), tail2.x(), tail2.y()));
		g2D.draw(new Line2D.Double(head2.x(), head2.y(), left2.x(), left2.y()));
		g2D.draw(new Line2D.Double(head2.x(), head2.y(), right2.x(), right2.y()));
	}

	/**
	 * Tells the component the fractal has changed to another fractal
	 * Updates the component so that the changes work
	 * @param b the new observable flame builder
	 */
	public void changeFractalBuilder(ObservableFlameBuilder b, Rectangle f) {
		builder = b;
		frame = f;
		
		// adding an observer in order to repaint on any change in the new builder
		builder.addObserver(new Observer() {
			
			@Override
			public void updateFractal() {
				repaint();
			}
		});

		setHighlightedTransformationIndex(0);
		repaint();
	}
}
