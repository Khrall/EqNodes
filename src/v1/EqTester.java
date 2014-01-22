package v1;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class EqTester {
	
	/*
	 * Guide to formatting equations:
	 * 
	 * 		1. 	Do not use spacings, EqNode will create do this for you
	 * 		2. 	Type in your equation as you would normally, (for example: 3x+2)
	 * 		3. 	Operations that need formatting are initiated with underscore,
	 * 			followed by curly brackets to wrap parameters divided by commas.
	 * 			
	 * 			General: _[function]{parameter,parameter,...,parameter} 
	 * 			Example: _pow{e,x} should display e^x formatted
	 * 			
	 * 			!NOTE: Do not forget to omit spaces.
	 * 
	 * 			A list of operations follows:
	 * 				i)		Power (pow, base, exponent)
	 * 						example: _pow{2,x}
	 * 				
	 * 				ii)		TODO: Fraction 
	 * 			
	 * 		4. 	Operation nesting is supported to a maximum depth of four, for
	 * 			example,
	 * 
	 * 			_pow{x,3y+_pow{2,(_pow{t,2}+3)}}
	 * 
	 * 			will output just fine, while
	 * 
	 * 			_pow{x,3y+_pow{2,(_pow{t,_pow{2,y}}+3)}}
	 * 
	 * 			will cause errors.
	 * 
	 */
	
	// INPUT YOUR TEST EQUATION HERE:
	public static final String EQUATION = "_frac{_pow{sin,2}(x),_pow{cos,2}(x)} = _pow{tan,2}(x)";
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		
	}
	
	private static void createAndShowGUI() {
		JFrame f = new JFrame("Mathematical equation to image");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new EqPanel(EQUATION));
		f.pack();
		f.setVisible(true);
	}
}