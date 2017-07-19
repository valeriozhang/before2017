package a3;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;

import a3.Tokenizer.InvalidExpressionException;

/* ACADEMIC INTEGRITY STATEMENT
 * By submitting this file, we state that all group members associated
 * with the assignment understand the meaning and consequences of cheating, 
 * plagiarism and other academic offenses under the Code of Student Conduct 
 * and Disciplinary Procedures (see www.mcgill.ca/students/srr for more information).
 * 
 * By submitting this assignment, we state that the members of the group
 * associated with this assignment claim exclusive credit as the authors of the
 * content of the file (except for the solution skeleton provided).
 * 
 * In particular, this means that no part of the solution originates from:
 * - anyone not in the assignment group
 * - Internet resources of any kind.
 * 
 * This assignment is subject to inspection by plagiarism detection software.
 * 
 * Evidence of plagiarism will be forwarded to the Faculty of Science's disciplinary
 * officer. 
 */

/**
 * Main class for the calculator: creates the GUI for the calculator and
 * responds to presses of the "Enter" key in the text field and clicking on the
 * button. You do not need to understand or modify the GUI code to complete this
 * assignment. See instructions below the line BEGIN ASSIGNMENT CODE
 * 
 * @author Martin P. Robillard 26 February 2015
 *
 */
@SuppressWarnings("serial")
public class Calculator extends JFrame implements ActionListener {
	private static final Color LIGHT_RED = new Color(214, 163, 182);

	private final JTextField aText = new JTextField(40);

	public Calculator() {
		setTitle("COMP 250 Calculator");
		setLayout(new GridLayout(2, 1));
		setResizable(false);
		add(aText);
		JButton clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				aText.setText("");
				aText.requestFocusInWindow();
			}
		});
		add(clear);

		aText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
		aText.addActionListener(this);

		aText.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				aText.getHighlighter().removeAllHighlights();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				aText.getHighlighter().removeAllHighlights();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				aText.getHighlighter().removeAllHighlights();
			}
		});

		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Run this main method to start the calculator
	 * 
	 * @param args
	 *            Not used.
	 */
	public static void main(String[] args) {
		new Calculator();
	}

	private String currentExpression = "";

	/*
	 * Responds to events by the user. Do not modify this method.
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (aText.getText().contains("=")) {
			aText.setText("");
		} else {
			Queue<Token> expression = processExpression(aText.getText());
			if (expression != null) {
				String result = evaluateExpression(expression);
				if (result != null) {
					aText.setText(aText.getText() + " = " + result);
				}
			}
		}
	}

	/**
	 * Highlights a section of the text field with a color indicating an error.
	 * Any change to the text field erase the highlights. Call this method in
	 * your own code to highlight part of the equation to indicate an error.
	 * 
	 * @param pBegin
	 *            The index of the first character to highlight.
	 * @param pEnd
	 *            The index of the first character not to highlight.
	 */
	public void flagError(int pBegin, int pEnd) {
		assert pEnd > pBegin;
		try {
			aText.getHighlighter().addHighlight(pBegin, pEnd,
					new DefaultHighlighter.DefaultHighlightPainter(LIGHT_RED));
		} catch (BadLocationException e) {

		}
	}

	/******************** BEGIN ASSIGNMENT CODE ********************/

	/**
	 * Tokenizes pExpression (see Tokenizer, below), and returns a Queue of
	 * Tokens that describe the original mathematical expression in reverse
	 * Polish notation (RPN). Flags errors and returns null if the expression a)
	 * contains any symbol except spaces, digits, round parentheses, or
	 * operators (+,-,*,/) b) contains unbalanced parentheses c) contains
	 * invalid combinations of parentheses and numbers, e.g., 2(3)(4)
	 * 
	 * @param pExpressionn
	 *            A string.
	 * @return The tokenized expression transformed in RPN
	 */
	private Queue<Token> processExpression(String pExpression) {

		if(pExpression.equals("") || pExpression.isEmpty() || pExpression.equals(" "))	
		{
			return null;
		}

		this.currentExpression = pExpression.replaceAll("\\s+", "");
		Queue<Token> tokenQueue = new LinkedList<Token>();
		Stack<Token> tokenStack = new Stack<Token>();

		try 
		{
			String newExpression = pExpression.replaceAll("\\s+", "");
			Tokenizer tokenizer = new Tokenizer(newExpression);
			tokenizer.tokenize(newExpression);

			System.out.println(tokenizer.tokens1.toString());

			System.out.println("Found1");
			System.out.println("STACK: ");
			for(Token t : tokenStack)
			{
				System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
			}
			System.out.println();
			System.out.println("QUEUE: ");
			for(Token t : tokenQueue)
			{
				System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
			}
			System.out.println();

			boolean foundLeftParenthesis = false;

			System.out.println("--While there are tokens to be read--");
			for (Token token : tokenizer.tokens1) 
			{
				System.out.println("Reading a token...");

				char c = newExpression.charAt(token.getStart());
				char c1 = newExpression.charAt(token.getEnd());

				if (tokenizer.isDigit(c)) 
				{
					System.out.println("1. Token is a number");
					tokenQueue.add(token);
					continue;
				}
				if (isAnOperator(token, newExpression, tokenizer)) 
				{
					try
					{
						while(!tokenStack.isEmpty() && isAnOperator(tokenStack.peek(), newExpression, tokenizer) && getPrecedence(c) <= getPrecedence(newExpression.charAt(tokenStack.peek().getStart())))
						{
							tokenQueue.add(tokenStack.pop());
						}
						tokenStack.push(token);
					}
					catch (Exception e) 
					{
						e.printStackTrace();
					}

					System.out.println("Found2");
					System.out.println("STACK: ");
					for(Token t : tokenStack)
					{
						System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
					}
					System.out.println();
					System.out.println("QUEUE: ");
					for(Token t : tokenQueue)
					{
						System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
					}
					System.out.println();
					continue;
				}

				if (c == c1 && newExpression.charAt(token.getStart()) == '(')
				{
					tokenStack.push(token);
					continue;
				}

				System.out.println("Found6");
				System.out.println("STACK: ");
				for(Token t : tokenStack)
				{
					System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
				}
				System.out.println();
				System.out.println("QUEUE: ");
				for(Token t : tokenQueue)
				{
					System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
				}
				System.out.println();

				Token parError = null;
				if (c == c1 && newExpression.charAt(token.getStart()) == ')') 
					if(!tokenStack.isEmpty())
					{
						while(!tokenStack.isEmpty() )
						{
							Token tmp = tokenStack.peek();
							if(newExpression.charAt(tmp.getStart()) == '(')
							{
								System.out.println("Here1");
								foundLeftParenthesis = true;
								tokenStack.pop();
								parError = tmp;
								break;
							}
							else if(isAnOperator(tmp, newExpression, tokenizer))
							{
								System.out.println("Here2");
								tokenQueue.add(tokenStack.pop());
							}
							else
							{
								break;
							}
							foundLeftParenthesis = false;
						}
					}
					else
					{
						flagError(token.getStart(), token.getEnd()+1);
						return null;
					}

				System.out.println("Found7");
				System.out.println("STACK: ");
				for(Token t : tokenStack)
				{
					System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
				}
				System.out.println();
				System.out.println("QUEUE: ");
				for(Token t : tokenQueue)
				{
					System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
				}
				System.out.println();

				if (tokenStack.isEmpty() && !foundLeftParenthesis) 
				{
					flagError(token.getStart(), token.getEnd()+1);
					return null;
				}
			}

			// if there are some operators left in the operator stack, then
			// add them to the output queue
			while (!tokenStack.isEmpty()) 
			{
				if (newExpression.charAt(tokenStack.peek().getStart()) == '('
						|| newExpression.charAt(tokenStack.peek().getEnd()) == ')') 
				{
					flagError(tokenStack.peek().getStart(), tokenStack.peek().getEnd()+1);	
					return null;
				}
				else
				{
					tokenQueue.add(tokenStack.pop());
				}

				System.out.println("Found8");
				System.out.println("STACK: ");
				for(Token t : tokenStack)
				{
					System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
				}
				System.out.println();
				System.out.println("QUEUE: ");
				for(Token t : tokenQueue)
				{
					System.out.print(" " + newExpression.substring(t.getStart(), t.getEnd()+1));					
				}
				System.out.println();
			}

		}	
		catch (InvalidExpressionException e) 
		{
			System.out.println("Error");
			int error = e.getPosition();
			flagError(error, error + 1);
			return null;
		}
		return tokenQueue;
	}

	private int getPrecedence(char operator) throws Exception {

		if (operator == '+' || operator == '-') 
		{
			return 1;
		} 
		else if (operator == '*' || operator == '/') 
		{
			return 2;
		} 
		else 
		{
			throw new Exception("Illegal operator");
		}
	}

	private boolean isAnOperator(Token token, String pExpression, Tokenizer tokenizer) 
	{
		char c = pExpression.charAt(token.getStart());
		char c1 = pExpression.charAt(token.getEnd());
		if (c == c1 && !tokenizer.isDigit(c) 
				&& pExpression.charAt(token.getStart()) != ')' 
				&& pExpression.charAt(token.getStart()) != '(') 
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Assumes pExpression is a Queue of tokens produced as the output of
	 * processExpression. Evaluates the answer to the expression. The division
	 * operator performs a floating-point division. Flags errors and returns
	 * null if the expression is an invalid RPN expression e.g., 1+-
	 * 
	 * @param pExpression
	 *            The expression in RPN
	 * @return A string representation of the answer)
	 */
	private String evaluateExpression(Queue<Token> pExpression) {

		//Hint return String.format("%s", <YOUR ANSWER>);	
		if(pExpression == null)
		{
			return "";
		}

		String returnValue = "";
		String operators = "+-*/";
		Queue<String> tQueue = new LinkedList<String>();
		Stack<String> stack = new Stack<String>();

		for(Token t : pExpression)
		{
			String test = currentExpression.substring(t.getStart(), t.getEnd()+1);
			tQueue.add(test);
		}

		for (String t : tQueue) {
			if (!operators.contains(t)) {
				stack.push(t);
			} else {
				String s1 = stack.pop();
				String s2 = stack.pop();

				double first = Double.parseDouble(s1);
				double second = Double.parseDouble(s2);
				switch (t) {
				case "+":
					stack.push(String.valueOf(first + second));
					break;
				case "-":
					stack.push(String.valueOf(second - first));
					break;
				case "*":
					stack.push(String.valueOf(first * second));
					break;
				case "/":
					stack.push(String.valueOf(second / first));
					break;
				}
			}
		}

		returnValue = stack.pop();
		return String.format("%s", returnValue);
	}

}

/**
 * Use this class as the root class of a hierarchy of token classes that can
 * represent the following types of tokens: a) Integers (e.g., "123" "4", or
 * "345") Negative numbers are not allowed as inputs b) Parentheses '(' or ')'
 * c) Operators '+', '-', '/', '*' Hint: consider using the Comparable interface
 * to support comparing operators for precedence
 */
class Token {
	private int aStart;
	private int aEnd;

	/**
	 * @param pStart
	 *            The index of the first character of this token in the original
	 *            expression.
	 * @param pEnd
	 *            The index of the last character of this token in the original
	 *            expression
	 */
	public Token(int pStart, int pEnd) {
		aStart = pStart;
		aEnd = pEnd;
	}

	public int getStart() {
		return aStart;
	}

	public int getEnd() {
		return aEnd;
	}

	public String toString() {
		return "{" + aStart + "," + aEnd + "}";
	}
}

/**
 * Partial implementation of a tokenizer that can convert any valid string into
 * a stream of tokens, or detect invalid strings. Do not change the signature of
 * the public methods, but you can add private helper methods. The signature of
 * the private methods is there to help you out with basic ideas for a design
 * (it is strongly recommended to use them). Hint: consider making your
 * Tokenizer an Iterable<Token>
 */
class Tokenizer {
	public ArrayList<Token> tokens1;
	public String testString;

	public Tokenizer(String testString)
	{
		this.testString = testString;
		tokens1 = new ArrayList<Token>();
	}

	/**
	 * Converts pExpression into a sequence of Tokens that are retained in a
	 * data structure in this class that can be made available to other objects.
	 * Every call to tokenize should clear the structure and start fresh. White
	 * spaces are tolerated but should be ignored (not converted into tokens).
	 * The presence of any illegal character should raise an exception.
	 * 
	 * @param pExpression
	 *            An expression to tokenize. Can contain invalid characters.
	 * @throws InvalidExpressionException
	 *             If any invalid character is detected or if parentheses are
	 *             misused as operators.
	 */
	public void tokenize(String pExpression) throws InvalidExpressionException {
		String test = pExpression;
		String theString = "";
		Token temp = null;
		int digitStart = 0;
		int digitEnd = 0;
		boolean isDigitString = false;
		String currDigit = "";

		//ignore all white space
		Scanner tokenize = new Scanner(test);
		while (tokenize.hasNext()) 
		{
			theString = theString + tokenize.next();
		}

		//detect invalid characters
		for (int j = 0; j < pExpression.length(); j++) 
		{
			if (!isDigit(pExpression.charAt(j)) && theString.charAt(j) != '('
					&& theString.charAt(j) != '+' && theString.charAt(j) != ')'
					&& theString.charAt(j) != '-' && theString.charAt(j) != '/'
					&& theString.charAt(j) != '*') 
			{
				throw new InvalidExpressionException(j);
			}
		}

		//add valid characters to the tokenQueue called tokens1
		for (int i = 0; i < theString.length(); i++) 
		{

			if (theString.charAt(i) == '(') 
			{
				temp = new Token(i, i);
				tokens1.add(temp);
			}

			if (theString.charAt(i) == ')') 
			{
				temp = new Token(i, i);
				tokens1.add(temp);
			}

			if (theString.charAt(i) == '+') 
			{
				temp = new Token(i, i);
				tokens1.add(temp);
			}

			if (theString.charAt(i) == '-') 
			{
				temp = new Token(i, i);
				tokens1.add(temp);
			}

			if (theString.charAt(i) == '/') 
			{
				temp = new Token(i, i);
				tokens1.add(temp);
			}

			if (theString.charAt(i) == '*') 
			{
				temp = new Token(i, i);
				tokens1.add(temp);
			}

			//start position of the number
			if(i == 0 && isDigit(theString.charAt(i)))
			{
				digitStart = i;
			}

			else if(i != 0 && isDigit(theString.charAt(i)) && !isDigit(theString.charAt(i-1)))
			{
				digitStart = i;
			}

			//grouping consecutive digits together
			if(isDigit(theString.charAt(i))) 
			{
				currDigit = currDigit + test.charAt(i);
				isDigitString = true;
			}
			//if position is not a digit, add the digits we have grouped together to the tokenQueue
			else if (isDigitString)
			{
				digitEnd = i - 1;
				isDigitString = false;
				currDigit = "";
				temp = new Token(digitStart, digitEnd);
				tokens1.add(temp);
			}
		}

		//if last position is a digit, add it to the tokenQueue
		if(isDigitString)
		{
			tokens1.add(new Token(digitStart, theString.length()-1));
		}

		//sort the tokenQueue
		int start;
		Token temp1;
		for(int i = 0; i < tokens1.size(); i++)
		{
			//start position at 0
			start = i;
			for(int j = i + 1; j < tokens1.size(); j++)
			{
				//if the element at i+1 is greater than the previous element at i
				//then the starting element is the element at i+1
				if(tokens1.get(j).getStart() < tokens1.get(start).getStart())
				{
					start = j;
				}
			}
			//swap the elements of i and i+1
			temp1 = tokens1.get(start);
			tokens1.set(start, tokens1.get(i));
			tokens1.set(i, temp1);
		}
		validate();
	}

	public boolean isDigit(char c) {
		if ((int) c > 47 && (int) c < 58) {
			return true;
		}
		return false;
	}

	private void consume(char pChar) throws InvalidExpressionException {
		// Consume a single character in the input expression and deals with
		// it appropriately.
	}

	/**
	 * Detects if parentheses are misused
	 * 
	 * @throws InvalidExpressionException
	 */
	private void validate() throws InvalidExpressionException {
		// An easy way to detect if parentheses are misused is
		// to look for any opening parenthesis preceded by a token that
		// is neither an operator nor an opening parenthesis, and for any
		// closing parenthesis that is followed by a token that is
		// neither an operator nor a closing parenthesis. Don't check for
		// unbalanced parentheses here, you can do it in processExpression
		// directly as part of the Shunting Yard algorithm.
		// Call this method as the last statement in tokenize.

		//validate isn't checking for misused parenthesis
		//processExpression is partially working, but crashes for more complex problems

		String newString = testString;
		//System.out.println(newString);

		//checks for the misuse of parenthesis
		for (int i = 1; i < newString.length(); i++)
		{

			if (newString.charAt(i) == '(') 
			{
				if (isDigit(newString.charAt(i - 1)) )
				{
					throw new InvalidExpressionException(i);
				}

				if( newString.charAt(i - 1) == ')') 
				{
					throw new InvalidExpressionException(i);
				}
			}
		}

		//checks for the misuse of parenthesis
		for (int j = 0; j < newString.length()-1; j++) 
		{
			if (newString.charAt(j) == ')') 
			{
				if (isDigit(newString.charAt(j + 1))
						|| newString.charAt(j + 1) == '(')
				{
					throw new InvalidExpressionException(j);
				}
			}
		}

		//checks if consecutive positions have functions 2+-3
		for(int h = 1; h < newString.length(); h++)
		{
			if(newString.charAt(h) == '+' || newString.charAt(h) == '-'	|| newString.charAt(h) == '*'
					|| newString.charAt(h) == '/' )
			{
				if(newString.charAt(h-1) == '+' || newString.charAt(h-1) == '-' || newString.charAt(h-1) == '*'
						|| newString.charAt(h-1) == '/')
				{
					throw new InvalidExpressionException(h);
				}
			}
		}

		//checks for empty parenthesis ()
		for(int k = 1; k < newString.length(); k++)
		{
			if(newString.charAt(k-1) == '(' && newString.charAt(k) == ')')
			{
				throw new InvalidExpressionException(k);
			}
		}

		//checks if starting position is an operator +2-3
		for(int i = 0; i < newString.length(); i++)
		{
			if(newString.charAt(0) == '+' || newString.charAt(0) == '-' || newString.charAt(0) == '/'
					|| newString.charAt(0) == '*')
			{
				throw new InvalidExpressionException(0);
			}
		}

		//checks if last operator is an operator 2+3-
		for(int i = 0; i < newString.length(); i++)
		{
			if(newString.charAt(newString.length()-1) == '+' || newString.charAt(newString.length()-1) == '-' 
					|| newString.charAt(newString.length()-1) == '/' || newString.charAt(newString.length()-1) == '*')
			{
				throw new InvalidExpressionException(newString.length()-1);
			}
		}

	}

	/**
	 * Thrown by the Tokenizer if an expression is detected to be invalid. You
	 * don't need to modify this class.
	 */
	@SuppressWarnings("serial")
	class InvalidExpressionException extends Exception {
		private int aPosition;

		public InvalidExpressionException(int pPosition) {
			aPosition = pPosition;
		}

		public int getPosition() {
			return aPosition;
		}
	}
}