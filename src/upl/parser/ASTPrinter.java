package upl.parser;

import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.expression.*;
import upl.parser.statement.*;

import java.util.Arrays;
import java.util.List;

public class ASTPrinter implements Expression.Visitor<String>, Statement.Visitor<String> {
	private int depth = 0;
	public String print(Expression expression) {
		return expression.accept(this);
	}
	
	public String print(Statement statement) {
		return statement.accept(this);
	}
	@Override
	public String visitBinaryExpression(BinaryExpression expression) {
		return parenthesize(expression.operator.lexeme, expression.left, expression.right);
	}
	
	@Override
	public String visitUnaryExpression(UnaryExpression expression) {
		return parenthesize(expression.operator.lexeme, expression.expression);
	}
	
	@Override
	public String visitGrouping(Grouping expression) {
		return parenthesize("group", expression.expression);
	}
	
	@Override
	public String visitLiteral(Literal expression) {
		return expression.value.toString();
	}
	
	@Override
	public String visitVariable(Variable expression) {
		return expression.identifier.lexeme;
	}
	
	@Override
	public String visitStatements(Statements statement) {
		StringBuilder builder = new StringBuilder();
		builder.append("(block ");
		++depth;
		for (Statement stmt : statement.statements) {
			builder.append(stmt.accept(this));
		}
		--depth;
		
		builder.append(")");
		return builder.toString();
	}
	
	@Override
	public String visitIfThenElse(IfThenElse statement) {
		if (statement.elseBranch == null) {
			return parenthesize("if", statement.condition, statement.thenBranch);
		}
		
		return parenthesize("if-else", statement.condition, statement.thenBranch, statement.elseBranch);
	}
	
	@Override
	public String visitDoWhile(DoWhile statement) {
		return parenthesize("do-while", statement.body, statement.expression);
	}
	
	@Override
	public String visitPrint(Print statement) {
		return parenthesize("print", statement.expression);
	}
	
	@Override
	public String visitDeclaration(Declaration statement) {
		if (statement.initializer == null) {
			return parenthesize(statement.variable.type.lexeme, statement.variable.identifier.lexeme);
		}
		
		return parenthesize(statement.variable.type.lexeme, statement.variable.identifier.lexeme, "=", statement.initializer);
	}
	
	@Override
	public String visitAssignment(Assignment statement) {
		return parenthesize(statement.variable.identifier.lexeme, "=", statement.expression);
		
	}
	
	private String parenthesize(String name, Object... parts) {
		StringBuilder builder = new StringBuilder();
		
		builder.append('\n').append("\t".repeat(depth)).append("(").append(name);
		++ depth;
		transform(builder, parts);
		-- depth;
		builder.append('\n').append("\t".repeat(depth)).append(")");
		
		return builder.toString();
	}
	
	private void transform(StringBuilder builder, Object... parts) {
		for (Object part : parts) {
			builder.append("\n");
			if (part instanceof Expression) {
				builder.append("\t".repeat(depth)).append(((Expression)part).accept(this));
			} else if (part instanceof Statement) {
				builder.append("\t".repeat(depth)).append(((Statement) part).accept(this));
			} else if (part instanceof Token) {
				builder.append("\t".repeat(depth)).append(((Token) part).lexeme);
			} else if (part instanceof List) {
				transform(builder, ((List) part).toArray());
			} else {
				builder.append("\t".repeat(depth)).append(part);
			}
		}
	}
	
	public static void main(String[] args) {
//		Expression expression = new BinaryExpression(
//				new Literal(123),
//				new Token(TokenType.STAR, "*", null, 1),
//				new Grouping(
//						new Literal(45.67)));
//
//		System.out.println(new ASTPrinter().print(expression));
		
		Statement statement = new IfThenElse(
				new BinaryExpression(
						new Variable(
								new Token(TokenType.INT, "int", null, 1),
								new Token(TokenType.IDENTIFIER, "x", null, 1)
						),
						new Token(TokenType.GREATER_EQUAL, ">=", null, 1),
						new Literal(10)
				),
				new Statements(Arrays.asList(
						new Print(
									new Literal(1)
							)
						)
				),
				new Statements(Arrays.asList(
						new Print(
									new Literal(2)
							)
						)
				)
		);

		System.out.println(new ASTPrinter().print(statement));
	}
}
