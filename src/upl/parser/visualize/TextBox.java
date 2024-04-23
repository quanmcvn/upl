package upl.parser.visualize;

import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.general.expression.*;
import upl.parser.general.statement.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 2-dimensional text strings, with VT100 line drawing support
 */
public class TextBox implements Expression.Visitor<TextBox>, Statement.Visitor<TextBox> {
	private static final char u = 1, d = 2, l = 4, r = 8, nonline = (char) ~(u+d+l+r); // bitmasks
	private static boolean vt100 = false; // intellij being stupid again
	public static void setEnableVT100Mode(boolean enable) {
		vt100 = enable;
	}
	public static boolean isnonline(char c) {
		return (c & nonline) > 0;
	}
	public static String listToString(List<Character> charList) {
		StringBuilder sb = new StringBuilder();
		for (char c : charList) sb.append(c);
		return sb.toString();
	}
	
	private final List<List<Character>> data = new ArrayList<>();
	
	/**
	 * Set up so that data[y][x] is writeable
	 */
	private void setup(int x, int y) {
		while (y >= data.size()) data.add(new ArrayList<>());
		List<Character> dataY = data.get(y);
		while (x >= dataY.size()) dataY.add(' ');
	}
	/**
	 * Put a string at coordinate
	 */
	public void putLine(String s, int x, int y) {
		setup(x + s.length(), y);
		for (int i = 0; i < s.length(); ++ i) {
			data.get(y).set(x + i, s.charAt(i));
		}
	}
	/**
	 * Put a box at coordinate
	 */
	public void putBox(TextBox box, int x, int y) {
		for (int i = 0; i < box.data.size(); ++ i) {
			putLine(listToString(box.data.get(i)), x, y + i);
		}
	}
	public int height() {
		return data.size();
	}
	
	/**
	 *  Draw a horizontal line
	 * <p>
	 *  If before = true, the line starts from the left edge of the first character cell, otherwise it starts from its center
	 * <p>
	 *  If after  = true, the line ends in the right edge of the last character cell, otherwise it ends in its center
	 */
	public void hLine(int x, int y, int width, boolean before, boolean after) {
//		System.out.printf("called hLine(%d, %d, %d, %s, %s)\n", x, y, width, before, after);
		for (int i = 0; i < width; ++ i) {
			int rx = x + i;
			int ry = y;
			setup(rx, ry);
			char c = data.get(ry).get(rx);
			if (isnonline(c)) c = 0;
			if (i > 0 || before) c |= l;
			if (after || (i + 1) < width) c |= r;
//			System.out.printf("setting data[%d][%d] to %c (%d) (%s)!\n", ry, rx, c, (int) c, Integer.toBinaryString((int) c));
			data.get(ry).set(rx, c);
		}
	}
	
	/** Draw a vertical line
	 * <p>
	 *  If before = true, the line starts from the top edge of the first character cell, otherwise it starts from its center
	 * <p>
	 *  If after = true, the line ends in the bottom edge of the last character cell, otherwise it ends in its center
	 */
	public void vLine(int x, int y, int height, boolean before, boolean after) {
//		System.out.printf("called vLine(%d, %d, %d, %s, %s)\n", x, y, height, before, after);
		for (int i = 0; i < height; ++ i) {
			int rx = x;
			int ry = y + i;
			setup(rx, ry);
			char c = data.get(ry).get(rx);
			if (isnonline(c)) c = 0;
			if (i > 0 || before) c |= u;
			if (after || (i + 1) < height) c |= d;
//			System.out.printf("setting data[%d][%d] to %c (%d) (%s)!\n", ry, rx, c, (int) c, Integer.toBinaryString((int) c));
			data.get(ry).set(rx, c);
		}
	}
	
	@Override
	public TextBox visitBinaryExpression(BinaryExpression expression) {
		TextBox res = new TextBox();
		res.putLine(String.format("binary \"%s\"", expression.operator.lexeme), 0, 0);
		TextBox left = expression.left.accept(this);
		TextBox right = expression.right.accept(this);
		
		res.vLine(0, 1, 1, true, false);
		res.hLine(0, 1, 3, false, false);
		res.putBox(left, 3, 1);
		
		res.vLine(0, 1, 1 + left.height(), true, false);
		res.hLine(0, 1 + left.height(), 3, false, false);
		res.putBox(right, 3, 1 + left.height());
		
		return res;
	}
	
	@Override
	public TextBox visitUnaryExpression(UnaryExpression expression) {
		TextBox res = new TextBox();
		String atom = String.format("unary \"%s\"", expression.operator.lexeme);
		res.putLine(atom, 0, 0);
		TextBox child = expression.expression.accept(this);
		
		res.hLine(atom.length(), 0, 2, false, false);
		res.putBox(child, atom.length() + 2, 0);
		
		return res;
	}
	
	@Override
	public TextBox visitGrouping(Grouping expression) {
		TextBox res = new TextBox();
		String atom = "grouping";
		res.putLine(atom, 0, 0);
		TextBox child = expression.expression.accept(this);
		
		res.hLine(atom.length(), 0, 2, false, false);
		res.putBox(child, atom.length() + 2, 0);
		
		return res;
	}
	@Override
	public TextBox visitLiteral(Literal expression) {
		TextBox res = new TextBox();
		String atom = "lit";
		res.putLine(atom, 0, 0);
		res.putLine(expression.value.toString(), atom.length() + 1, 0);
		return res;
	}
	
	@Override
	public TextBox visitVariable(Variable expression) {
		TextBox res = new TextBox();
		String atom = String.format("ident `%s`", expression.type.lexeme);
		res.putLine(atom, 0, 0);
		res.putLine(String.format("\"%s\"",expression.identifier.lexeme), atom.length() + 1, 0);
		return res;
	}
	
	@Override
	public TextBox visitStatements(Statements statement) {
		TextBox res = new TextBox();
		String atom = "block";
		res.putLine(atom, 0, 0);
		
		int curY = 1;
		for (Statement stmt : statement.statements) {
			TextBox child = stmt.accept(this);
			res.vLine(0, 1, curY, true, false);
			res.hLine(0, curY, 3, false, false);
			res.putBox(child, 3, curY);
			curY += child.height();
		}
		
		return res;
	}
	
	@Override
	public TextBox visitIfThenElse(IfThenElse statement) {
		TextBox res = new TextBox();
		String atom = "if_then_else";
		res.putLine(atom, 0, 0);
		TextBox cond = statement.condition.accept(this);
		TextBox thenBody = statement.thenBranch.accept(this);
		
		int curY = 1;
		
		res.vLine(0, 1, curY, true, false);
		res.hLine(0, curY, 3, false, false);
		String ifstr = "if";
		res.putLine(ifstr, 3, curY);
		res.putBox(cond, 3 + ifstr.length() + 1, curY);
		curY += cond.height();
		
		res.vLine(0, 1, curY, true, false);
		res.hLine(0, curY, 3, false, false);
		String thenstr = "then";
		res.putLine(thenstr, 3, curY);
		res.putBox(thenBody, 3 + thenstr.length() + 1, curY);
		curY += thenBody.height();
		
		if (statement.elseBranch != null) {
			TextBox elseBody = statement.elseBranch.accept(this);
			res.vLine(0, 1, curY, true, false);
			res.hLine(0, curY, 3, false, false);
			String elsestr = "else";
			res.putLine(elsestr, 3, curY);
			res.putBox(elseBody, 3 + elsestr.length() + 1, curY);
		}
		
		return res;
	}
	
	@Override
	public TextBox visitDoWhile(DoWhile statement) {
		TextBox res = new TextBox();
		String atom = "do_while";
		res.putLine(atom, 0, 0);
		TextBox body = statement.body.accept(this);
		TextBox cond = statement.condition.accept(this);
		
		res.vLine(0, 1, 1, true, false);
		res.hLine(0, 1, 3, false, false);
		res.putBox(body, 3, 1);
		
		res.vLine(0, 1, 1 + body.height(), true, false);
		res.hLine(0, 1 + body.height(), 3, false, false);
		res.putBox(cond, 3, 1 + body.height());
		return res;
	}
	
	@Override
	public TextBox visitPrint(Print statement) {
		TextBox res = new TextBox();
		String atom = "print";
		res.putLine(atom, 0, 0);
		TextBox child = statement.expression.accept(this);
		
		res.hLine(atom.length(), 0, 2, true, true);
		res.putBox(child, atom.length() + 2, 0);
		
		return res;
	}
	
	@Override
	public TextBox visitDeclaration(Declaration statement) {
		TextBox res = new TextBox();
		String atom = "decl";
		res.putLine(atom, 0, 0);
		TextBox ident = statement.variable.accept(this);
		
		res.vLine(0, 1, 1, true, false);
		res.hLine(0, 1, 3, false, false);
		res.putBox(ident, 3, 1);
		
		if (statement.initializer != null) {
			TextBox init = statement.initializer.accept(this);
			res.vLine(0, 1, 1 + ident.height(), true, false);
			res.hLine(0, 1 + ident.height(), 3, false, false);
			String initstr = "init";
			res.putLine(initstr, 3, 1 + ident.height());
			res.putBox(init, 3 + initstr.length() + 1, 1 + ident.height());
		}
		
		return res;
	}
	
	@Override
	public TextBox visitAssignment(Assignment statement) {
		TextBox res = new TextBox();
		String atom = "assign";
		res.putLine(atom, 0, 0);
		TextBox ident = statement.variable.accept(this);
		TextBox value = statement.expression.accept(this);
		
		res.vLine(0, 1, 1, true, false);
		res.hLine(0, 1, 3, false, false);
		res.putBox(ident, 3, 1);
		
		res.vLine(0, 1, 1 + ident.height(), true, false);
		res.hLine(0, 1 + ident.height(), 3, false, false);
		res.putBox(value, 3, 1 + ident.height());
		
		
		return res;
	}
	
	public String print(Expression expression) {
		return expression.accept(this).toString();
	}
	
	public String print(Statement statement) {
		return statement.accept(this).toString();
	}
	
	static class ToStringHelper {
		public static String applyColor(String s) {
			return "\033[" + s + "m";
		}
		public static final String green = "0;32";
		public static final String blue = "1;34";
		public static final String pink = "1;38;5;165";
		public static final String gray = "0;38;5;246";
		public static final String yellow = "1;33";
		public static final String white = "1;37";
		private final List<List<Character>> data;
		private String res = "";
		// is drawing ├ ─ ┬ │ ┐ └ ?
		private boolean drawing = false;
		// is under quote ?
		private boolean quote = false;
		private boolean bquote = false;
		private boolean space = true;
		private String currentAttribute = "";
		// magic vt100
		// xxxqjkuqmltqvwn
		// │││─┘┐┤─└┌├─┴┬┼
		private static final char[] lineCharacters = vt100 ?
				"xxxqjkuqmltqvwn".toCharArray() :
				"│││─┘┐┤─└┌├─┴┬┼".toCharArray()
				;
		
		private void applyAttribute(String attr) {
			if (!currentAttribute.equals(attr)) {
				res += applyColor(attr);
				currentAttribute = attr;
			}
		}
		private void append(boolean v, char c) {
			if (vt100) {
				String attr = null;
				boolean num = false;
				if (v && !drawing) {
					drawing = true;
					attr = green;
					res += "\33)0\16"; // draw ├ ─ ┬ │ ┐ └
				} else if (!v && drawing) {
					drawing = false;
					attr = "";
					res += "\33)B\17"; // undraw
				}
				if (!v && c == '"') {
					quote = !quote;
					if (quote) {
						attr = blue;
					}
				}
				if (!v && c == '`') {
					bquote = !bquote;
					if (bquote) {
						attr = yellow;
					}
				}
				
				if (!v && !quote && ((c>='0' && c<='9') || c=='-')) {
					if (space) {
						attr = pink;
					} else {
						attr = gray;
					}
					num = true;
				}
				if(!v && !quote && !bquote && ((c>='a' && c<='z') || c=='_')) {
					attr = white;
				}
				if (attr != null) applyAttribute(attr);
				if (!num) {
					space = (c == ' ');
				}
			}
			if (c != '`') res += c;
		}
		ToStringHelper(List<List<Character>> data) {
			this.data = data;
		}
		@Override
		public String toString() {
			for (List<Character> line : data) {
				for (char c : line) {
					if (0 < c && c < 16) {
						append(true, lineCharacters[c - 1]);
					} else {
						append(false, c);
					}
				}
				applyAttribute("");
				append(false, '\n');
			}
			return res;
		}
	}
	
	@Override
	public String toString() {
		return (new ToStringHelper(data)).toString();
	}
	
	public TextBox() {}
	
	public static void main(String[] args) {
//		Expression expression = new BinaryExpression(
//				new Literal(123),
//				new Token(TokenType.STAR, "*", null, 1),
//				new Grouping(
//						new Literal(45.67)));
//
//		TextBox temp = new TextBox();
//		temp = expression.accept(temp);
//		System.out.println((int) temp.data.get(1).get(0));
//		System.out.println(temp);
	
		Statement statement = new DoWhile(
				new Statements(Arrays.asList(
					new Print(
							new BinaryExpression(
									new Literal(123),
									new Token(TokenType.STAR, "*"),
									new Grouping(
											new Literal(45.67)))
					),
					new Print(
							new Literal("Hello world!")
					)
				)
				),
				new BinaryExpression(
						new BinaryExpression(
								new Literal(1),
								new Token(TokenType.STAR, "*"),
								new Literal(1)
						),
						new Token(TokenType.EQUAL_EQUAL, "=="),
						new Literal(2)
				)
		);
		
		System.out.println((new TextBox()).print(statement));
//		Statement statement = new IfThenElse(
//				new BinaryExpression(
//						new Variable(
//								new Token(TokenType.INT, "int", null, 1),
//								new Token(TokenType.IDENTIFIER, "x", null, 1)
//						),
//						new Token(TokenType.GREATER_EQUAL, ">=", null, 1),
//						new Literal(10)
//				),
//				new Statements(Arrays.asList(
//						new Print(
//								new Literal(1)
//						)
//				)
//				),
//				new Statements(Arrays.asList(
//						new Print(
//								new Literal(2)
//						)
//				)
//				)
//		);

//		System.out.println((new TextBox()).print(statement));
	}
}

