 /*
  Microdata Tabulator
  https://github.com/mnpopcenter/microdata-tabulator
  Copyright (c) 2012-2017 Regents of the University of Minnesota

  Contributors:
    Alex Jokela, Minnesota Population Center, University of Minnesota
    Pranjul Yadav, Minnesota Population Center, University of Minnesota
 
  This project is licensed under the Mozilla Public License, version 2.0 (the
  "License"). A copy of the License is in the project file "LICENSE.txt",
  and is also available at https://www.mozilla.org/en-US/MPL/2.0/.
 */

package org.terrapop.tabulation.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
/*
    This program reads standard expressions typed in by the user. 
    The program constructs an expression tree to represent the
    expression.  It then prints the value of the tree.  It also uses
    the tree to print out a list of commands that could be used
    on a stack machine to evaluate the expression.
    The expressions can use positive real numbers and
    the binary operators +, -, *, and /.  The unary minus operation
    is supported.  The expressions are defined by the BNF rules:

            <expression>  ::=  [ "-" ] <term> [ [ "+" | "-" ] <term> ]...

            <term>  ::=  <factor> [ [ "*" | "/" ] <factor> ]...

            <factor>  ::=  <number>  |  "(" <expression> ")"

    A number must begin with a digit (i.e., not a decimal point).
    A line of input must contain exactly one such expression.  If extra
    data is found on a line after an expression has been read, it is
    considered an error.

    In addition to the main program class, SimpleParser3, this program
    defines a set of four nested classes for implementing expression trees.

 */
import java.io.UnsupportedEncodingException;
import java.util.Map;

/*
 * 
 * ([var1, "=", 4] AND [var2, "<=", 9]) OR ([var1, "=>", 23] AND [var2, "<=", 10])
 * 
 */

public class Parse {

	private String recordType;
	private String personOrHousehold;
	private Map<String, String> data;
	private TextIO textIO;
	private int debug  = 0;
	
	public Parse(int debug) {
		this.debug = debug;
	}

	public Parse(String rule, Map<String, String> data, String personOrHousehold, String recordType, int debug)
			throws UnsupportedEncodingException {
		textIO = new TextIO();
		textIO.readStream(new ByteArrayInputStream(rule.getBytes("UTF-8")));
		setData(data);
		setPersonOrHousehold(personOrHousehold);
		setRecordType(recordType);
		this.debug = debug;
	}

	public void closeInput() throws IOException {
		textIO.closeInput();
	}

	abstract private static class ExpNode {
		private int debug;
		abstract String value() throws ParseError;

		abstract Integer setDepth(Integer depth);

		abstract Integer getDepth();

		abstract void printStackCommands();

		abstract Boolean hasCondition();
		
		public int setDebug(int debug) {
			this.debug = debug;
			return debug;
		}
		
		public int getDebug() {
			return debug;
		}
	}

	/**
	 * Represents an expression node that holds a number.
	 */
	private static class ConstNode extends ExpNode {
		String number; // The number.
		Integer depth; // recursion depth;

		public ConstNode(String val) {
			// Construct a ConstNode containing the specified number.
			number = val;
		}

		public String value() {
			// The value of the node is the number that it contains.
			return number;
		}

		public void printStackCommands() {
			// On a stack machine, just push the number onto the stack.
			if(getDebug() > 0)
				System.out.println("  Push " + number);
		}

		public Integer setDepth(Integer depth) {
			this.depth = depth;
			return this.depth;
		}

		public Integer getDepth() {
			return this.depth;
		}

		public Boolean hasCondition() {
			return false;
		}
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private static class BracketNode extends ExpNode {
		private String value;
		private Integer depth;
		
		
		public BracketNode(String value, Integer depth, int debug) {
			this.value = value;
			this.depth = depth;
			setDebug(debug);
		}

		public String value() throws ParseError {

			return value;
		}

		public void printStackCommands() {
			if(getDebug() > 0) {
				for (int i = 0; i < depth; ++i) {
					System.out.print("  ");
				}
				
				System.out.println(" [BN] " + value + " (depth: " + depth + ")");
			}
		}

		public Integer setDepth(Integer depth) {
			this.depth = depth;
			return this.depth;
		}

		public Integer getDepth() {
			return this.depth;
		}

		public Boolean hasCondition() {
			return true;
		}
	}

	/**
	 * An expression node representing a binary operator.
	 */
	private static class BinOpNode extends ExpNode {
		char op; // The operator.
		ExpNode left; // The expression for its left operand.
		ExpNode right; // The expression for its right operand.
		Integer depth;
				
		public BinOpNode(char op, ExpNode left, ExpNode right, Integer depth, int debug) {
			// Construct a BinOpNode containing the specified data.
			assert op == '+' || op == '-' || op == '*' || op == '/' || op == 'A' || op == 'a' || op == 'o' || op == 'O';
			assert left != null && right != null;
			this.op = op;
			this.left = left;
			this.right = right;
			this.depth = depth;
			setDebug(debug);
		}

		public Integer setDepth(Integer depth) {
			this.depth = depth;
			return this.depth;
		}

		public Integer getDepth() {
			return this.depth;
		}

		public String prettyPrintOperator() {

			switch (op) {
			case '+':
				return "[plus]";
			case '-':
				return "[minus]";
			case '*':
				return "[multiply]";
			case '/':
				return "[divide]";
			case 'A':
			case 'a':
				return "AND";
			case 'O':
			case 'o':
				return "OR";
			default:
				return "[UNKNOWN]";
			}

		}

		public String value() throws ParseError {
			// The value is obtained by evaluating the left and right
			// operands and combining the values with the operator.

			if (isNumeric(left.value()) && isNumeric(right.value())) {

				double x = Double.parseDouble(left.value());
				double y = Double.parseDouble(right.value());
				switch (op) {
				case '+':
					return ((Double) (x + y)).toString();
				case '-':
					return ((Double) (x - y)).toString();
				case '*':
					return ((Double) (x * y)).toString();
				case '/':
					return ((Double) (x / y)).toString();
				default:
					return "NaN"; // Bad operator!
				}
			} else {

				return "(" + left.value() + " " + prettyPrintOperator() + " " + right.value() + ")";

			}
		}

		public void printStackCommands() {
			// To evaluate the expression on a stack machine, first do
			// whatever is necessary to evaluate the left operand, leaving
			// the answer on the stack. Then do the same thing for the
			// second operand. Then apply the operator (which means popping
			// the operands, applying the operator, and pushing the result).
			if(getDebug() > 0) {
				left.printStackCommands();
				right.printStackCommands();
	
				for (int i = 0; i < depth; ++i) {
					System.out.print("  ");
				}
	
				System.out.println("  Operator " + prettyPrintOperator() + " (depth: " + depth + ")");
			}
		}

		public Boolean hasCondition() {
			return false;
		}
	}

	/**
	 * An expression node to represent a unary minus operator.
	 */

	private class UnaryMinusNode extends ExpNode {
		ExpNode operand; // The operand to which the unary minus applies.
		Integer depth;

		UnaryMinusNode(ExpNode operand, Integer depth, int debug) {
			// Construct a UnaryMinusNode with the specified operand.
			assert operand != null;
			this.operand = operand;
			this.depth = depth;
			setDebug(debug);
		}

		String value() throws NumberFormatException, ParseError {
			// The value is the negative of the value of the operand.

			double neg = Double.parseDouble(operand.value());
			return ((Double) (-neg)).toString();
		}

		void printStackCommands() {
			// To evaluate this expression on a stack machine, first do
			// whatever is necessary to evaluate the operand, leaving the
			// operand on the stack. Then apply the unary minus (which means
			// popping the operand, negating it, and pushing the result).
			if(getDebug() > 0) {
				operand.printStackCommands();
				System.out.println("  Unary minus");
			}
		}

		public Integer setDepth(Integer depth) {
			this.depth = depth;
			return this.depth;
		}

		public Integer getDepth() {
			return this.depth;
		}

		public Boolean hasCondition() {
			return false;
		}
	}

	// -------------------------------------------------------------------------------

	/**
	 * An object of type ParseError represents a syntax error found in the
	 * user's input.
	 */
	public static class ParseError extends Exception {
		/**
		* 
		*/
		private static final long serialVersionUID = 4444218998622806831L;

		ParseError(String message) {
			super(message);
		}
	} // end nested class ParseError

	public Boolean parseExpression() {

		try {
			ExpNode exp = expressionTree(0);
			textIO.skipBlanks();
			if (textIO.glance() != '\n')
				throw new ParseError("Extra data after end of expression.");
			textIO.getLine();
			
			if(debug > 0) {
				System.out.println("\nValue is " + exp.value());
				System.out.println("\nOrder of postfix evaluation is:\n");
				exp.printStackCommands();
			}
			
			return true;
		} catch (ParseError e) {
			System.out.println("\n*** Error in input:    " + e.getMessage());
			System.out.println("*** Discarding input:  " + textIO.getLine());
		}

		return false;
	}

	private ExpNode squareBracketTree(Integer depth) throws ParseError {
		String bracketedString = "";

		while (textIO.glance() != ']') {

			bracketedString += textIO.getAnyChar();

		}

		textIO.getAnyChar();

		return new BracketNode("[" + bracketedString + "]", depth, debug);
	}

	private ExpNode expressionTree(Integer depth) throws ParseError {
		textIO.skipBlanks();

		boolean negative; // True if there is a leading minus sign.
		negative = false;
		if (textIO.glance() == '-') {
			textIO.getAnyChar();
			negative = true;
		}
		ExpNode exp; // The expression tree for the expression.
		exp = termTree(depth); // Start with the first term.
		if (negative)
			exp = new UnaryMinusNode(exp, depth, debug);

		textIO.skipBlanks();
		while (textIO.glance() == '-' || textIO.glance() == '+' || textIO.glance() == '*' || textIO.glance() == '/'
				|| textIO.glance() == 'A' || textIO.glance() == 'a' || textIO.glance() == 'O' || textIO.glance() == 'o') {
			// Read the next term and combine it with the
			// previous terms into a bigger expression tree.
			char op = textIO.getAnyChar();

			if (textIO.glance() == 'N' || textIO.glance() == 'n' || textIO.glance() == 'R' || textIO.glance() == 'r') {
				if (textIO.glance() == 'N' || textIO.glance() == 'n') {
					textIO.getAnyChar();
				}
				textIO.getAnyChar();
			}

			ExpNode nextTerm = termTree(depth);
			exp = new BinOpNode(op, exp, nextTerm, depth, debug);

			textIO.skipBlanks();
		}
		return exp;
	} // end expressionTree()


	private ExpNode termTree(Integer depth) throws ParseError {
		textIO.skipBlanks();
		ExpNode term; // The expression tree representing the term.
		term = factorTree(depth);
		textIO.skipBlanks();
		while (textIO.glance() == '*' || textIO.glance() == '/') {
			// Read the next factor, and combine it with the
			// previous factors into a bigger expression tree.
			char op = textIO.getAnyChar();
			ExpNode nextFactor = factorTree(depth);
			term = new BinOpNode(op, term, nextFactor, depth, debug);
			textIO.skipBlanks();
		}
		return term;
	} // end termValue()

	private ExpNode factorTree(Integer depth) throws ParseError {
		textIO.skipBlanks();
		char ch = textIO.glance();
		if (Character.isDigit(ch)) {
			// The factor is a number. Return a ConstNode.
			String num = ((Double) textIO.getDouble()).toString();
			return new ConstNode(num);
		} else if (ch == '(') {
			// The factor is an expression in parentheses.
			// Return a tree representing that expression.
			textIO.getAnyChar(); // Read the "("
			ExpNode exp = expressionTree(depth + 1);
			textIO.skipBlanks();
			if (textIO.glance() != ')')
				throw new ParseError("Missing right parenthesis.");
			textIO.getAnyChar(); // Read the ")"
			return exp;
		} else if (ch == '[') {

			textIO.getAnyChar();
			ExpNode exp = squareBracketTree(depth);
			textIO.skipBlanks();

			return exp;
		} else if (ch == '\n') {
			throw new ParseError("End-of-line encountered in the middle of an expression.");
		} else if (ch == ']') {
			throw new ParseError("Extra right square bracket.");
		} else if (ch == ')') {
			throw new ParseError("Extra right parenthesis.");
		} else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == 'A' || ch == 'a' || ch == 'O'
				|| ch == 'o') {
			throw new ParseError("Misplaced operator.");
		} else {
			throw new ParseError("Unexpected character \"" + ch + "\" encountered.");
		}
	} // end factorTree()

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public String getPersonOrHousehold() {
		return personOrHousehold;
	}

	public void setPersonOrHousehold(String personOrHousehold) {
		this.personOrHousehold = personOrHousehold;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

} // end class SimpleParser3
