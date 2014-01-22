package v1;

import java.util.ArrayList;

public class EqNode {
	
	public final static int NO_OPERAND = 0, OPERAND_INSIDE = 1, OPERAND_AFTER = 2, OPERAND_BEFORE = 3, ALL_OPERANDS = 4; 
	
	public final static String 
	NO_OPERATION = "", 
	POW = "pow", //To the power of 
	FRAC = "frac"; //Fraction
	
	public final static char 
		plus = '+', minus = '-', //Obvious is Obvious
		oP = '(', cP = ')', //Opening- and closing parenthesis
		parenthesis = 'P', //Simply parenthesis
		oB = '{', cB = '}', //Opening- and closing brackets
		brackets = 'B', //Simply brackets
		comma = ',', //For splitting operations
		OP_STARTER = '_'; //Starts an operation, such as _pow{x,2}
	
	public EqNode parent = null;
	public ArrayList<EqNode> operands = new ArrayList<EqNode>();
	public char operator = ' ';
	public String contents = "";
	
	public int stateOfParenthesis = NO_OPERAND;
	public int stateOfBrackets = NO_OPERAND;
	public String bracketOperation = NO_OPERATION;
	
	public EqNode(String input) {
		init(false, input);
	}
	
	public EqNode(EqNode child, char operator, String restInput) {
		
		this.operator = operator;
		operands.add(child);
		init(true, restInput);
		
		
	}
	
	private void init(boolean encountered_plus_minus, String input) {
		
		int parentheses = 0; // Parentheses is the plural of parenthesis
		int bracketDepth = 0;
		int tail = 0;
		
		for(int head = 0; head < input.length(); head++) {
			char c = input.charAt(head);
			
			if(c == oP) { parentheses += 1; }
			else if(c == cP) { parentheses -= 1; }
			else if(c == oB) { bracketDepth += 1; }
			else if(c == cB) { bracketDepth -= 1; }
			
			else if( (c == plus || c == minus) && parentheses == 0 && bracketDepth == 0) {
				
				EqNode operand = new EqNode(input.substring(tail, head));
				operand.parent = this;
				operands.add(operand);
				tail = head + 1;
				
				if(encountered_plus_minus) {
					
					parent = new EqNode(this, c, input.substring(tail));
					return;
					
				} else {
					
					operator = c;
					encountered_plus_minus = true;
					
				}
				
			}
		}
		
		if(encountered_plus_minus) {
			EqNode operand = new EqNode(input.substring(tail));
			operand.parent = this;
			operands.add(operand);
		} else {
			
			int pOpen = input.indexOf("(");
			int bOpen = input.indexOf("{");
			
			if(-1 < pOpen && (pOpen < bOpen || bOpen < 0)) {
				operator = parenthesis;
				stateOfParenthesis = OPERAND_INSIDE;
				
				int close = -1, depth = 1;
				for(int i = pOpen + 1; i < input.length(); i++) {
					char c = input.charAt(i);
					if(c == oP) { depth += 1;
					}  else if(c == cP) { 
						close = i;
						depth -= 1;
						if(depth == 0) { break; }
					}
				}
				
				if(pOpen > 0) { 
					EqNode beforeParenthesis = new EqNode(input.substring(0, pOpen));
					beforeParenthesis.parent = this;
					operands.add(beforeParenthesis);
					stateOfParenthesis = OPERAND_BEFORE;
				}
				
				EqNode insideParenthesis = new EqNode(input.substring(pOpen + 1, close));
				insideParenthesis.parent = this;
				operands.add(insideParenthesis);
				
				if(close < input.length()-1) {
					EqNode afterParenthesis = new EqNode(input.substring(close+1));
					afterParenthesis.parent = this;
					operands.add(afterParenthesis);
					stateOfParenthesis += 1; //OPERAND_INSIDE -> OPERAND_AFTER, OPERAND_BEFORE -> ALL_OPERANDS
				}
					
			} else if(bOpen > -1) {
				
				operator = brackets;
				stateOfBrackets = OPERAND_INSIDE;
				
				//Search for operation
				int operationStart = -1;
				for(int i = bOpen; i >= 0; i--) {
					if(input.charAt(i) == OP_STARTER) {
						operationStart = i;
						bracketOperation = input.substring(operationStart + 1, bOpen);
						break;
					}
				}

				//Search for closing bracket
				int close = -1, split = -1, depth = 1;
				for(int i = bOpen + 1; i < input.length(); i++) {
					char c = input.charAt(i);
					if(c == oB) { depth += 1; }
					else if(c == cB) {
						close = i;
						depth -= 1;
						if(depth == 0) { break; }
					} else if(c == comma && depth == 1) { split = i; }
				}

				if(operationStart > 0) {
					EqNode beforeOperation = new EqNode(input.substring(0, operationStart));
					beforeOperation.parent = this;
					operands.add(beforeOperation);
					stateOfBrackets = OPERAND_BEFORE;
				}
				
				EqNode insideBrackets1 = new EqNode(input.substring(bOpen + 1, split));
				insideBrackets1.parent = this;
				operands.add(insideBrackets1);
				
				EqNode insideBrackets2 = new EqNode(input.substring(split + 1, close));
				insideBrackets2.parent = this;
				operands.add(insideBrackets2);
				
				if(close < input.length()-1) {
					EqNode afterBrackets = new EqNode(input.substring(close+1));
					afterBrackets.parent = this;
					operands.add(afterBrackets);
					stateOfBrackets += 1; //OPERAND_INSIDE -> OPERAND_AFTER, OPERAND_BEFORE -> ALL_OPERANDS
				}
			}
			
		}
		

		if(operands.isEmpty()) {
			contents = input;
		}
		
		
	}
	
	public String toString() {
		String equation = "";
		if(operands.isEmpty()) { equation += contents; }
		else {
			
			if(stateOfParenthesis != NO_OPERAND) {
				switch(stateOfParenthesis) {
				
				case OPERAND_INSIDE:
					equation += "(" + operands.get(0) + ")";
					break;
				
				case OPERAND_BEFORE:
					equation += operands.get(0) + "(" + operands.get(1) + ")";
					break;
					
				case OPERAND_AFTER:
					equation += "(" + operands.get(0) + ")" + operands.get(1);
					break;
				
				case ALL_OPERANDS:
					equation += operands.get(0) + "(" + operands.get(1) + ")" + operands.get(2);
					break;
	 			}
			}
			
			else if(stateOfBrackets != NO_OPERAND) {
				switch(stateOfBrackets) {
								
				case OPERAND_INSIDE:
					equation += operationToString(operands.get(0), operands.get(1));
					break;
				
				case OPERAND_BEFORE:
					equation += operands.get(0) + operationToString(operands.get(1), operands.get(2)); 
					break;
					
				case OPERAND_AFTER:
					equation += operationToString(operands.get(0), operands.get(1)) + operands.get(2);
					break;
					
				case ALL_OPERANDS:
					equation += operands.get(0) + operationToString(operands.get(1), operands.get(2)) + operands.get(3); 
					break;
					
				}
			}
			
			else {
				for(EqNode operand : operands) { equation += operand + String.valueOf(operator); }
				equation = equation.substring(0, equation.length()-1);
			}
		}
		return equation;
	}
	
	private String operationToString(EqNode op1, EqNode op2) {
		switch(bracketOperation) {
		
		case POW:
			return "(" + op1 + ")^(" + op2 + ")";
			
		}
		return "";
	}

	public EqNode getRootNode() {
		if(parent != null) { return parent.getRootNode(); }
		else { return this; }
	}
	
	
}
