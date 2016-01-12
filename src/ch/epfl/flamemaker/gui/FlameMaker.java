package ch.epfl.flamemaker.gui;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public final class FlameMaker {
	public static void main(String[] args) {
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	try {
	            	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
	            	        if ("Nimbus".equals(info.getName())) {
	            	            UIManager.setLookAndFeel(info.getClassName());
	            	            break;
	            	        }
	            	    }
	            	} catch (Exception e) {
	            		System.out.println(e.getMessage());
	            	}
	                new FlameMakerGUI().start();
	            }
	        });
	}
}
