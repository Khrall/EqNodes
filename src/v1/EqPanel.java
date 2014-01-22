package v1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EqPanel extends JPanel {
	
	public static final int START_X = 40, START_Y = 125;
	public static final float[] FONT_SIZES = {30f, 20f, 12f, 8f};
	public static final int[] DEPTH_OFFSETS = {5, 2, 1, 0};
	public Font AsanaMath;
	
	EqNode equation = new EqNode("");
	
	public EqPanel(String equation) {
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.equation = new EqNode(equation);
		
		// Try to create font
		try {
			AsanaMath = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res/Asana-Math.ttf"));
		} catch (FontFormatException | IOException e) {
			AsanaMath = new Font("Arial", Font.PLAIN, 12);
			e.printStackTrace();
		}
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(540, 240);
	}
	
	
	public void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
		
		super.paintComponent(g);
		drawEquation(g);
	}

	private void drawEquation(Graphics g) {
		
		EqNode root = equation.getRootNode();
		int[] cursor = {START_X, START_Y};
		int depth = 0;
		
		drawNode(root, cursor, depth, g);
	}

	private void drawNode(EqNode root, int[] cursor, int depth, Graphics g) {
		Font f = AsanaMath.deriveFont(FONT_SIZES[depth]);
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();
		
		if(root.operands.isEmpty()) { 
			g.drawString(root.contents, cursor[0], cursor[1]);
			cursor[0] += fm.stringWidth(root.contents);
		} else {
			
			switch(root.operator) {
			
			case EqNode.plus: case EqNode.minus:
				drawNode(root.operands.get(0), cursor, depth, g);
				String opString = " " + String.valueOf(root.operator) + " ";
				g.drawString(opString, cursor[0], cursor[1]);
				cursor[0] += fm.stringWidth(opString);
				drawNode(root.operands.get(1), cursor, depth, g);
				break;
			
			case EqNode.parenthesis:
				switch(root.stateOfParenthesis) {
				
				case EqNode.OPERAND_INSIDE:
					g.drawString("(", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth("(");
					drawNode(root.operands.get(0), cursor, depth, g);
					g.drawString(")", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth(")");
					break;
				
				case EqNode.OPERAND_BEFORE:
					drawNode(root.operands.get(0), cursor, depth, g);
					g.drawString("(", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth("(");
					drawNode(root.operands.get(1), cursor, depth, g);
					g.drawString(")", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth(")");
					break;
					
				case EqNode.OPERAND_AFTER:
					g.drawString("(", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth("(");
					drawNode(root.operands.get(0), cursor, depth, g);
					g.drawString(")", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth(")");
					drawNode(root.operands.get(1), cursor, depth, g);
					break;
					
				case EqNode.ALL_OPERANDS:
					drawNode(root.operands.get(0), cursor, depth, g);
					g.drawString("(", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth("(");
					drawNode(root.operands.get(1), cursor, depth, g);
					g.drawString(")", cursor[0], cursor[1]);
					cursor[0] += fm.stringWidth(")");
					drawNode(root.operands.get(2), cursor, depth, g);
					break;
				}
				break;	
				
			case EqNode.brackets:
				switch(root.stateOfBrackets) {
					case EqNode.OPERAND_INSIDE:
						drawOperation(root.operands.get(0), root.operands.get(1), cursor, depth, g);
						break;
					
					case EqNode.OPERAND_BEFORE:
						drawNode(root.operands.get(0), cursor, depth, g);
						drawOperation(root.operands.get(1), root.operands.get(2), cursor, depth, g);
						break;
						
					case EqNode.OPERAND_AFTER:
						drawOperation(root.operands.get(0), root.operands.get(1), cursor, depth, g);
						drawNode(root.operands.get(2), cursor, depth, g);
						break;
						
					case EqNode.ALL_OPERANDS:
						drawNode(root.operands.get(0), cursor, depth, g);
						drawOperation(root.operands.get(1), root.operands.get(2), cursor, depth, g);
						drawNode(root.operands.get(3), cursor, depth, g);
						break;
					
				}
				break;
			
			}
		}
		
	}
	
	private void drawOperation(EqNode op1, EqNode op2, int[] cursor, int depth, Graphics g) {
		Font f = AsanaMath.deriveFont(FONT_SIZES[depth]);
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();
		
		drawNode(op1, cursor, depth, g);
		
		int yIncrease = fm.getHeight() * 5/9;
		cursor[1] -= yIncrease;
		cursor[0] += DEPTH_OFFSETS[depth];
		drawNode(op2, cursor, depth + 1, g);
		cursor[1] += yIncrease;
		
	}
}
