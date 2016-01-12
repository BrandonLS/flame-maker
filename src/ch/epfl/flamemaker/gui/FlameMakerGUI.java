/*
 *	Author:      Timothée Lottaz & Brandon Le Sann
 *	Date:        9 mars 2013
 */

package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.*;
import ch.epfl.flamemaker.flame.*;
import ch.epfl.flamemaker.geometry2d.*;
import ch.epfl.flamemaker.gui.ObservableFlameBuilder.Observer;

public final class FlameMakerGUI implements ActionListener{
	private static final double TRANSLATION_STEP = 0.2;
	private static final double ROTATION_STEP = 10;
	private static final double SCALING_STEP = 1.1;
	private static final double SHEAR_STEP = 0.1;
	
	private static final java.awt.Color OFF_WHITE = new java.awt.Color(245, 245, 245);
	
	private ObservableFlameBuilder flameBuilder;
	private Color background;
	private Palette palette;
	private Rectangle frame;
	private int density;
	
	private Timer timer;

	/* Since we repaint the FlameBuilder Component with a timer, we need its informations, 
	the accumulator and the flame that is currently built. */ 
	private FlameBuilderPreviewComponent fBuilderComponent;
	private JProgressBar progressBar;
	
	private FlameAccumulator.Builder accuBuilder;
	private Flame actualFlame;
	
	// a set to store the observators of changing fractal
	private final Set<ChangeFractalObserver> changeFractalObserverSet = new HashSet<ChangeFractalObserver>();
	
	private final Set<SelectedTransfoObserver> selectedTransfoObserverSet = new HashSet<SelectedTransfoObserver>();
	
	private int observableSelectedTransformationIndex = 0;
	
	public FlameMakerGUI() {
		// initialising flameBuilder with a Shark-Fin Fractal
		flameBuilder = new ObservableFlameBuilder(Flame.createSharkFin());
		
		// initialising background
		background = Color.BLACK;
		
		// initialising palette
		palette = new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE));
		
		// initialising frame
		frame = new Rectangle(new Point(-0.25, 0), 5, 4);
		
		// initialising density
		density = 50;
		
		// initialising the accumulator with FBuilderComponent's preferred dimensions
		int accuWidth = FlameBuilderPreviewComponent.PREFERRED_WIDTH;
		int accuHeight = FlameBuilderPreviewComponent.PREFERRED_HEIGHT;
		double ratio = ((double)accuWidth) / accuHeight;
		accuBuilder = new FlameAccumulator.Builder(frame.expandToAspectRatio(ratio), accuWidth, accuHeight);
		
		/* initialising the FBuilderComponent with our Accumulator Builder, 
		so that we can update the display from FlameMakerGUI */
		fBuilderComponent = new FlameBuilderPreviewComponent(accuBuilder, background, palette);
		
		// initialising a progress bar
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		// initialising a timer, that will compute part of the fractal and refresh the display, every 10 ms
		timer = new Timer(10, this);
		timer.start();
		
		// an observer is necessary in order to update the fractal
		flameBuilder.addObserver(new Observer() {
			
			@Override
			public void updateFractal() {
				restartBuildingProcess();
			}
		});
	}
	
	/**
	 * Restarts the building process. Does nothing if the fractal display doesn't fit in the window
	 * (width or height <= 0). Clears the accumulator, resizing it if necessary, and restarts the timer.
	 */
	private void restartBuildingProcess() {
		int width = fBuilderComponent.getWidth();
		int height = fBuilderComponent.getHeight();
		
		if(width <= 0 || height <= 0) {
			timer.stop();
		}
		
		else {
			double ratio = ((double)width) / height;
			
			accuBuilder.clear(frame.expandToAspectRatio(ratio), fBuilderComponent.getWidth(), fBuilderComponent.getHeight());
			timer.restart();
		}
	}
	
	/**
	 * Changes the actual fractal to another, doing all the changes needed and notifying all the observers
	 * @param newFlame the new fractal
	 * @param newFrame the new frame
	 * @param newDensity the new density
	 */
	private void changeFractal(Flame newFlame, Rectangle newFrame, int newDensity) {
		// stops the building of the current fractal if needed
		timer.stop();
		
		// removing observers on old flameBuilder before "deleting" it
		flameBuilder.removeAllObservers();
		flameBuilder = new ObservableFlameBuilder(newFlame);

		// setting new frame and density
		frame = newFrame;
		density = newDensity;
		
		// an observer is necessary in order to update the fractal
		flameBuilder.addObserver(new Observer() {
			
			@Override
			public void updateFractal() {
				restartBuildingProcess();
			}
		});
		
		// we set the selected transformation to the first one
		setSelectedTransformationIndex(0);
		
		// notifying observers of change fractal
		notifyChangeFractalObservers();
		restartBuildingProcess();
	}
	
	/**
	 * Changes the actual fractal to "shark-fin" fractal
	 */
	private void changeFractalToSharkFin() {
		changeFractal(Flame.createSharkFin(), new Rectangle(new Point(-0.25, 0), 5, 4), 50);
	}

	/**
	 * Changes the actual fractal to "turbulence" fractal
	 */
	private void changeFractalToTurbulence() {
		changeFractal(Flame.createTurbulence(), new Rectangle(new Point(0.1, 0.1), 3, 3), 50);
	}
	
	/**
	 * Changes the actual fractal to "Sierpinski-Triangle" fractal
	 */
	private void changeFractalToTriangle() {
		changeFractal(Flame.createTriangle(), new Rectangle(new Point(0.5, 0.5), 1.25, 1.25), 10);
	}
	
	/**
	 * Notifies all the observers of fractal changing
	 */
	private void notifyChangeFractalObservers() {
		for (ChangeFractalObserver o : changeFractalObserverSet) {
			o.updateChangeFractal();
		}
	}
	
	/**
	 * Adds an observer of change fractal
	 * @param o the new observer
	 */
	public void addChangeFractalObservers(ChangeFractalObserver o) {
		changeFractalObserverSet.add(o);
	}
	
	/**
	 * Removes all the observers of change fractal
	 */
	public void removeChangeFractalObservers(ChangeFractalObserver o) {
		changeFractalObserverSet.remove(o);
	}
	
	/**
	 * An interface for change of fractal observers
	 *
	 */
	private interface ChangeFractalObserver {
		/**
		 * updates the observer after a fractal change
		 */
		public void updateChangeFractal();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Builds the fractal to display if the building process just started
		if(accuBuilder.buildingPercent() == 0) {
			actualFlame = flameBuilder.build();
		}
		
		// Computes a part of the fractal, then repaints the fractal display
		boolean b = actualFlame.computePart(density * fBuilderComponent.getWidth() * fBuilderComponent.getHeight(), accuBuilder);
		int percentage = accuBuilder.buildingPercent();
		progressBar.setValue(percentage);
		progressBar.setString("Loading... " + percentage + "%");
		fBuilderComponent.repaint();
		
		/* If the building is finished, the timer stops and the accumulator builder percentage 
		 is put to zero */ 
		if(b) {
			accuBuilder.setBuildingPercentToZero();
			timer.stop();
			progressBar.setString("Done !");
		}
	}

	public int selectedTransformationIndex() {
		return observableSelectedTransformationIndex;
	}
	
	public void setSelectedTransformationIndex(int index) {
		if(index < 0 || index >= flameBuilder.transformationCount()) {
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		}
		observableSelectedTransformationIndex = index;
		notifySelectedTransfoObservers();
	}
	
	public void start() {
		// Main panel initialisation
		JFrame mainPanel = new JFrame("Flame Maker");
		mainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				restartBuildingProcess();
			}
		});
		
		// The top part of the window, a grid containing the fractal and transformations display
		JPanel displayGrid = new JPanel();
		displayGrid.setLayout(new GridLayout(1, 2));
		
		// Adding a transformation display panel and a fractal display panel
		displayGrid.add(createTransfoDisplayPanel());
		displayGrid.add(createFractalDisplayPanel());
		
		// We add the grid in the center of contentPane
		mainPanel.getContentPane().setLayout(new BorderLayout());
		mainPanel.getContentPane().add(displayGrid, BorderLayout.CENTER);
		
		// We add the bottom panel at the bottom of contentPane
		mainPanel.getContentPane().add(createBottomPanel(), BorderLayout.PAGE_END);	
		
		// We add the fractal preset selector at the top of contentPane
		mainPanel.getContentPane().add(createFractalPresetSelector(), BorderLayout.PAGE_START);
		
		mainPanel.setBackground(OFF_WHITE);
		mainPanel.pack();
		mainPanel.setVisible(true);
	}

	private interface SelectedTransfoObserver {
		public void updateSelectedTransformationIndex();
	}

	public void addSelectedTransfoObserver(SelectedTransfoObserver o) {
		selectedTransfoObserverSet.add(o);
	}

	public void removeSelectedTransfoObserver(SelectedTransfoObserver o) {
		selectedTransfoObserverSet.remove(o);
	}

	private void notifySelectedTransfoObservers() {
		for (SelectedTransfoObserver o : selectedTransfoObserverSet) {
			o.updateSelectedTransformationIndex();
		}
	}

	private JPanel createTransfoDisplayPanel() {
		JPanel transfoPanel = new JPanel();
		transfoPanel.setLayout(new BorderLayout());
		transfoPanel.setBorder(BorderFactory.createTitledBorder("Affine Transformations"));
		
		final AffineTransformationsComponent transformationComponent = new AffineTransformationsComponent(flameBuilder, frame);
		transfoPanel.add(transformationComponent);
		
		addChangeFractalObservers(new ChangeFractalObserver() {
			
			@Override
			public void updateChangeFractal() {
				transformationComponent.changeFractalBuilder(flameBuilder, frame);
			}
		});
		
		addSelectedTransfoObserver(
				new SelectedTransfoObserver() {	
					@Override
					public void updateSelectedTransformationIndex() {
						transformationComponent.setHighlightedTransformationIndex(selectedTransformationIndex());
					}
				}
		);
		transfoPanel.setBackground(OFF_WHITE);
		return transfoPanel;
	}

	private JPanel createFractalDisplayPanel() {
		JPanel fractalPanel = new JPanel();
		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractal"));
		

		fractalPanel.add(fBuilderComponent);
		fractalPanel.add(progressBar, BorderLayout.PAGE_END);
		fractalPanel.setBackground(OFF_WHITE);
		return fractalPanel;
	}

	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

		bottomPanel.add(createEditTransfoListPanel());		
		bottomPanel.add(createEditCurrentTransfo());
		
		return bottomPanel;
	}

	private JPanel createEditTransfoListPanel() {
		JPanel editTransfoListPanel = new JPanel();
		editTransfoListPanel.setBackground(OFF_WHITE);
		editTransfoListPanel.setLayout(new BorderLayout());
		editTransfoListPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		
		final TranformationListModel transfoListModel = new TranformationListModel();
		final JList<String> transfoJList = createTransfoJList(transfoListModel);
		
		final JScrollPane transfoScrollPane = new JScrollPane(transfoJList);
		editTransfoListPanel.add(transfoScrollPane, BorderLayout.CENTER);
		
		editTransfoListPanel.add(createAddDeletePanel(transfoListModel, transfoJList), BorderLayout.PAGE_END);
		
		// adding the transfoList model as an observer of fractal change
		addChangeFractalObservers(transfoListModel);
		
		// adding an observer of fractal change to select the first element of the list after fractal changing
		addChangeFractalObservers(new ChangeFractalObserver() {
			
			@Override
			public void updateChangeFractal() {
				// to avoid the state where no transformation is selected, which leads to a bug
				transfoJList.setSelectedIndex(0);
			}
		});
		
		return editTransfoListPanel;
	}

	@SuppressWarnings("serial")
	private final class TranformationListModel extends AbstractListModel<String> implements ChangeFractalObserver {
		
		/**
		 * Adds an Identity transformation with only the linear weight, at the end of the list
		 */
		public void addTransformation() {
			double[] weights = {1,0,0,0,0,0};
			flameBuilder.addTransformation(new FlameTransformation(AffineTransformation.IDENTITY, weights));
			fireIntervalAdded(this, getSize() - 1, getSize() - 1);
		}

		public void removeTransformation(int index) {
			flameBuilder.removeTransformation(index);
			fireIntervalRemoved(this, index, index);
		}
		
		@Override
		public String getElementAt(int index) {
			if(index < 0 || index >= getSize()) {
				throw new IndexOutOfBoundsException("Invalid index: " + index);
			}
			
			return "Transformation n°" + (index + 1);
		}
	
		@Override
		public int getSize() {
			return flameBuilder.transformationCount();
		}

		@Override
		public void updateChangeFractal() {
			// updates the listModel after a change
			fireContentsChanged(this, 0, getSize() - 1);
		}
	}

	private JList<String> createTransfoJList(final TranformationListModel transfoListModel) {
		
		final JList<String> transfoJList = new JList<String>(transfoListModel);
		transfoJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transfoJList.setVisibleRowCount(3);
		transfoJList.setSelectedIndex(0);
		transfoJList.addListSelectionListener(
				new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						// we change the selectedIndex with the actual selected index in the JList
						setSelectedTransformationIndex(transfoJList.getSelectedIndex());
						notifySelectedTransfoObservers();
					}
				}
		);
		
		return transfoJList;
	}

	private JPanel createAddDeletePanel(final TranformationListModel transfoListModel, final JList<String> transfoJList) {
		// The buttons to add and delete transformations
		JPanel addDeletePanel = new JPanel();
		addDeletePanel.setBackground(OFF_WHITE);
		addDeletePanel.setLayout(new GridLayout(1, 2));
		final JButton removeButton = new JButton("Delete");
		JButton addButton = new JButton("Add");
	
		removeButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int indexToDelete = selectedTransformationIndex();
				int indexToSelect = selectedTransformationIndex();
				if(indexToSelect == transfoListModel.getSize() - 1) {
					indexToSelect--;
				}
				else {
					indexToSelect++;
				}
				transfoJList.setSelectedIndex(indexToSelect);
				transfoListModel.removeTransformation(indexToDelete);
				if(transfoListModel.getSize() == 1) {
					removeButton.setEnabled(false);
				}
			}
		});
		
		addButton.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(transfoListModel.getSize() == 1) {
					removeButton.setEnabled(true);
				}
				transfoListModel.addTransformation();		
				transfoJList.setSelectedIndex(transfoListModel.getSize() - 1);
			}
		});
		
		addDeletePanel.add(removeButton);
		addDeletePanel.add(addButton);
		
		/* adding an observer of fractal changing so that the remove button 
		 * be enabled or not after fractal changing */
		addChangeFractalObservers(new ChangeFractalObserver() {
			
			@Override
			public void updateChangeFractal() {
				if(flameBuilder.transformationCount() == 1) {
					removeButton.setEnabled(false);
				}
				else {
					removeButton.setEnabled(true);
				}
			}
		});
		
		return addDeletePanel;
	}

	private JPanel createEditCurrentTransfo() {
		JPanel editCurrentTransfo = new JPanel();
		editCurrentTransfo.setLayout(new BoxLayout(editCurrentTransfo, BoxLayout.PAGE_AXIS));
		editCurrentTransfo.setBorder(BorderFactory.createTitledBorder("Current Transformation"));
		
		editCurrentTransfo.add(createAffineEditCurrentTransfo());
		// Separator between the editors of the Affine part and the weights
		editCurrentTransfo.add(new JSeparator());
		editCurrentTransfo.add(createWeightEditPanel());
		
		editCurrentTransfo.setBackground(OFF_WHITE);
		return editCurrentTransfo;
	}

	private JPanel createAffineEditCurrentTransfo() {
		// Edit panel of the Affine part of the Transformation
		JPanel affineEdit = new JPanel();
		GroupLayout affineEditLayout = new GroupLayout(affineEdit);
		affineEdit.setLayout(affineEditLayout);
		
		// Creating buttons and stuff TODO check comment
		JLabel translationLabel = new JLabel("Translation");
		JLabel rotationLabel = new JLabel("Rotation");
		JLabel scalingLabel = new JLabel("Scaling");
		JLabel shearLabel = new JLabel("Shear");
		
		final JFormattedTextField translationField = new JFormattedTextField(new DecimalFormat("#0.##"));
		final JFormattedTextField rotationField = new JFormattedTextField(new DecimalFormat("#0.##"));
		final JFormattedTextField scalingField = new JFormattedTextField(new DecimalFormat("#0.##"));
		final JFormattedTextField shearField = new JFormattedTextField(new DecimalFormat("#0.##"));
		translationField.setValue(TRANSLATION_STEP);
		rotationField.setValue(ROTATION_STEP);
		scalingField.setValue(SCALING_STEP);
		shearField.setValue(SHEAR_STEP);
		translationField.setHorizontalAlignment(SwingConstants.RIGHT);
		rotationField.setHorizontalAlignment(SwingConstants.RIGHT);
		scalingField.setHorizontalAlignment(SwingConstants.RIGHT);
		shearField.setHorizontalAlignment(SwingConstants.RIGHT);

		scalingField.setInputVerifier(new InputVerifier() {
			
			@Override
			public boolean verify(JComponent input) {
				try {
					double value = ((Number)scalingField.getFormatter().stringToValue(scalingField.getText())).doubleValue();
					if(value == 0) {
						throw new IllegalArgumentException();
					}
					else {
						scalingField.setText(value + "");
					}
				} catch(Exception e) {
					scalingField.setText(scalingField.getValue().toString());
				}
			
				return true;
			}
		});
		
		
		
		JButton translationLeft = new JButton("←");
		JButton translationRight = new JButton("→");
		JButton translationUp = new JButton("↑");
		JButton translationDown = new JButton("↓");
		
		JButton rotationCounterClockwise = new JButton("↺");
		JButton rotationClockwise = new JButton("↻");
		
		JButton scalingLarger = new JButton("+ ↔");
		JButton scalingThinner = new JButton("- ↔");
		JButton scalingTaller = new JButton("+ ↕");
		JButton scalingSmaller = new JButton("- ↕");
		
		JButton shearLeft = new JButton("←");
		JButton shearRight = new JButton("→");
		JButton shearUp = new JButton("↑");
		JButton shearDown = new JButton("↓");
		
		setButtonAction(translationLeft, translationField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newTranslation(-delta, 0);
			}

			@Override
			public boolean invertComposeWith() {
				return false;
			}
		});
		
		setButtonAction(translationRight, translationField, new ButtonStrategy() {
			
			@Override
			public boolean invertComposeWith() {
				return false;
			}
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newTranslation(delta, 0);
			}
		});
		
		setButtonAction(translationUp, translationField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newTranslation(0, delta);
			}

			@Override
			public boolean invertComposeWith() {
				return false;
			}
		});
			
		setButtonAction(translationDown, translationField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newTranslation(0, -delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return false;
			}
		});
		
		setButtonAction(rotationCounterClockwise, rotationField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				double thetaRadian = delta / 180.0 * Math.PI; 
				return AffineTransformation.newRotation(thetaRadian);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(rotationClockwise, rotationField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				double thetaRadian = delta / 180.0 * Math.PI; 
				return AffineTransformation.newRotation(-thetaRadian);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(scalingLarger, scalingField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newScaling(delta, 1);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
	
		setButtonAction(scalingThinner, scalingField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newScaling(1.0/delta, 1);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(scalingTaller, scalingField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newScaling(1, delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(scalingSmaller, scalingField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newScaling(1, 1.0/delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(shearLeft, shearField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newShearX(-delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});

		setButtonAction(shearRight, shearField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newShearX(delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(shearUp, shearField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newShearY(delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		
		setButtonAction(shearDown, shearField, new ButtonStrategy() {
			
			@Override
			public AffineTransformation buttonEffect(double delta) {
				return AffineTransformation.newShearY(-delta);
			}
			
			@Override
			public boolean invertComposeWith() {
				return true;
			}
		});
		

		// Filling the Edit panel of the Affine part with the buttons
		
		// TODO factoriser code, essayer de tout mettre dans des tableaux, commenter
		// Horizontal Group
		GroupLayout.SequentialGroup affineHorizontalGroup = affineEditLayout.createSequentialGroup();
		affineEditLayout.setHorizontalGroup(affineHorizontalGroup);
		GroupLayout.ParallelGroup affineH1 = affineEditLayout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		GroupLayout.ParallelGroup affineH2 = affineEditLayout.createParallelGroup();
		GroupLayout.ParallelGroup affineH3 = affineEditLayout.createParallelGroup();
		GroupLayout.ParallelGroup affineH4 = affineEditLayout.createParallelGroup();
		GroupLayout.ParallelGroup affineH5 = affineEditLayout.createParallelGroup();
		GroupLayout.ParallelGroup affineH6 = affineEditLayout.createParallelGroup();
		
		affineHorizontalGroup.addGroup(affineH1);
		affineHorizontalGroup.addGroup(affineH2);
		affineHorizontalGroup.addGroup(affineH3);
		affineHorizontalGroup.addGroup(affineH4);
		affineHorizontalGroup.addGroup(affineH5);
		affineHorizontalGroup.addGroup(affineH6);
		
		affineH1.addComponent(translationLabel);
		affineH1.addComponent(rotationLabel);
		affineH1.addComponent(scalingLabel);
		affineH1.addComponent(shearLabel);
		
		affineH2.addComponent(translationField);
		affineH2.addComponent(rotationField);
		affineH2.addComponent(scalingField);
		affineH2.addComponent(shearField);
		
		affineH3.addComponent(translationLeft);
		affineH3.addComponent(rotationCounterClockwise);
		affineH3.addComponent(scalingLarger);
		affineH3.addComponent(shearLeft);
		
		affineH4.addComponent(translationRight);
		affineH4.addComponent(rotationClockwise);
		affineH4.addComponent(scalingThinner);
		affineH4.addComponent(shearRight);
		
		affineH5.addComponent(translationUp);
		affineH5.addComponent(scalingTaller);
		affineH5.addComponent(shearUp);
		
		affineH6.addComponent(translationDown);
		affineH6.addComponent(scalingSmaller);
		affineH6.addComponent(shearDown);
		
		// Vertical Group
		GroupLayout.SequentialGroup affineVerticalGroup = affineEditLayout.createSequentialGroup();
		affineEditLayout.setVerticalGroup(affineVerticalGroup);
		GroupLayout.ParallelGroup affineV1 = affineEditLayout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		GroupLayout.ParallelGroup affineV2 = affineEditLayout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		GroupLayout.ParallelGroup affineV3 = affineEditLayout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		GroupLayout.ParallelGroup affineV4 = affineEditLayout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		affineVerticalGroup.addGroup(affineV1);
		affineVerticalGroup.addGroup(affineV2);
		affineVerticalGroup.addGroup(affineV3);
		affineVerticalGroup.addGroup(affineV4);
		
		affineV1.addComponent(translationLabel);
		affineV1.addComponent(translationField);
		affineV1.addComponent(translationLeft);
		affineV1.addComponent(translationRight);
		affineV1.addComponent(translationUp);
		affineV1.addComponent(translationDown);
		
		affineV2.addComponent(rotationLabel);
		affineV2.addComponent(rotationField);
		affineV2.addComponent(rotationCounterClockwise);
		affineV2.addComponent(rotationClockwise);
		
		affineV3.addComponent(scalingLabel);
		affineV3.addComponent(scalingField);
		affineV3.addComponent(scalingLarger);
		affineV3.addComponent(scalingThinner);
		affineV3.addComponent(scalingTaller);
		affineV3.addComponent(scalingSmaller);
		
		affineV4.addComponent(shearLabel);
		affineV4.addComponent(shearField);
		affineV4.addComponent(shearLeft);
		affineV4.addComponent(shearRight);
		affineV4.addComponent(shearUp);
		affineV4.addComponent(shearDown);
		
		
		affineEdit.setBackground(OFF_WHITE);
		return affineEdit;
	}
	
	private void setButtonAction(JButton button, final JFormattedTextField field, final ButtonStrategy strategy) {
		
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(strategy.invertComposeWith()) {
					flameBuilder.setAffineTransformation(
							observableSelectedTransformationIndex, 
							(flameBuilder.affineTransformation(observableSelectedTransformationIndex)).composeWith(strategy.buttonEffect(((Number)field.getValue()).doubleValue()))
							);
				}
				
				else {
					flameBuilder.setAffineTransformation(
						observableSelectedTransformationIndex, 
						strategy.buttonEffect(((Number)field.getValue()).doubleValue()).composeWith(flameBuilder.affineTransformation(observableSelectedTransformationIndex))
						);
				}
				
			}
		});
		
	}

	private interface ButtonStrategy {
		public AffineTransformation buttonEffect(double delta);
		boolean invertComposeWith();
	}

	private JPanel createWeightEditPanel() {
		// Edit panel of the Affine part of the Transformation
		JPanel weightEdit = new JPanel();
		GroupLayout layout = new GroupLayout(weightEdit);
		weightEdit.setLayout(layout);
		
		
		// Creating buttons and stuff TODO check comment
		
		// Horizontal Group
		GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
		layout.setHorizontalGroup(horizontalGroup);
		// Vertical Group
		GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
		layout.setVerticalGroup(verticalGroup);
		
		final List<JLabel> labels = new ArrayList<JLabel>(); 
		final List<JFormattedTextField> fields = new ArrayList<JFormattedTextField>();
		final List<GroupLayout.ParallelGroup> horizontalGroupList = new ArrayList<GroupLayout.ParallelGroup>();
		final List<GroupLayout.ParallelGroup> verticalGroupList = new ArrayList<GroupLayout.ParallelGroup>();
		
		for (int i = 0; i < 6; i++) {
			horizontalGroupList.add(layout.createParallelGroup(GroupLayout.Alignment.TRAILING));
			horizontalGroup.addGroup(horizontalGroupList.get(i));
			if(i % 2 == 1) {
				horizontalGroup.addPreferredGap(ComponentPlacement.UNRELATED);
			}
		}
		
		for (int i = 0; i < 2; i++) {
			verticalGroupList.add(layout.createParallelGroup(GroupLayout.Alignment.BASELINE));
			verticalGroup.addGroup(verticalGroupList.get(i));
		}
		
		for (Variation v : Variation.ALL_VARIATIONS) {
			labels.add(new JLabel(v.name()));
			fields.add(new JFormattedTextField(new DecimalFormat("#0.##")));
		}
		
		// First line
		horizontalGroupList.get(0).addComponent(labels.get(0));
		verticalGroupList.get(0).addComponent(labels.get(0));
		horizontalGroupList.get(1).addComponent(fields.get(0));
		verticalGroupList.get(0).addComponent(fields.get(0));
		
		
		horizontalGroupList.get(2).addComponent(labels.get(1));
		verticalGroupList.get(0).addComponent(labels.get(1));
		horizontalGroupList.get(3).addComponent(fields.get(1));
		verticalGroupList.get(0).addComponent(fields.get(1));
		
		horizontalGroupList.get(4).addComponent(labels.get(2));
		verticalGroupList.get(0).addComponent(labels.get(2));
		horizontalGroupList.get(5).addComponent(fields.get(2));
		verticalGroupList.get(0).addComponent(fields.get(2));
		
		// Second line
		horizontalGroupList.get(0).addComponent(labels.get(3));
		verticalGroupList.get(1).addComponent(labels.get(3));
		horizontalGroupList.get(1).addComponent(fields.get(3));
		verticalGroupList.get(1).addComponent(fields.get(3));
		
		horizontalGroupList.get(2).addComponent(labels.get(4));
		verticalGroupList.get(1).addComponent(labels.get(4));
		horizontalGroupList.get(3).addComponent(fields.get(4));
		verticalGroupList.get(1).addComponent(fields.get(4));
		
		horizontalGroupList.get(4).addComponent(labels.get(5));
		verticalGroupList.get(1).addComponent(labels.get(5));
		horizontalGroupList.get(5).addComponent(fields.get(5));
		verticalGroupList.get(1).addComponent(fields.get(5));
			
		addSelectedTransfoObserver(new SelectedTransfoObserver() {
			
			@Override
			public void updateSelectedTransformationIndex() {
				for (int i = 0; i < fields.size(); i++) {
					final JFormattedTextField field = fields.get(i);
					final double actualWeight = flameBuilder.variationWeight(observableSelectedTransformationIndex, Variation.ALL_VARIATIONS.get(i));
					field.setValue(actualWeight);
				}
			}
		});
		
		for (int i = 0; i < fields.size(); i++) {
			final JFormattedTextField field = fields.get(i);
			final Variation actualVariation = Variation.ALL_VARIATIONS.get(i);
			final double initialWeight = flameBuilder.variationWeight(observableSelectedTransformationIndex, actualVariation);
			
			field.setValue(initialWeight);
			
			field.addPropertyChangeListener("value", new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent arg0) {
					double newValue = ((Number)field.getValue()).doubleValue();
					if(!(newValue == flameBuilder.variationWeight(observableSelectedTransformationIndex, actualVariation)))
					flameBuilder.setVariationWeight(observableSelectedTransformationIndex, actualVariation, newValue);
				}
			});
		}
		
		weightEdit.setBackground(OFF_WHITE);
		return weightEdit;
	}

	private JPanel createFractalPresetSelector() {
		JPanel fractalPresetSelector = new JPanel();
		fractalPresetSelector.setLayout(new BorderLayout());
		
		String[] fractalNames = {"Shark-Fin", "Turbulence", "Sierpinski-Triangle"};
		final JComboBox<String> comboBox = new JComboBox<String>(fractalNames);
		
		fractalPresetSelector.add(comboBox, BorderLayout.LINE_END);
		
		comboBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = comboBox.getSelectedIndex();
				
				switch (selectedIndex) {
				case 0:
					changeFractalToSharkFin();
					break;
					
				case 1:
					changeFractalToTurbulence();
					break;
				
				case 2:
					changeFractalToTriangle();
					break;
				default:
					throw new IllegalArgumentException("Selected index in comboBox invalid : " + selectedIndex);
				}
			}

		});
		
		fractalPresetSelector.setBackground(OFF_WHITE);
		return fractalPresetSelector;
	}
}