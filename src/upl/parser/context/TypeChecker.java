package upl.parser.context;

import upl.lexer.Location;
import upl.parser.Parser;
import upl.parser.general.expression.*;
import upl.parser.general.statement.*;

/**
 * Checking type of expression:
 * <br>
 * eg: int + bool results in compile error
 */
public class TypeChecker implements Expression.Visitor<String>, Statement.Visitor<String> {
	public void check(Statement statement) {
		statement.accept(this);
	}
	@Override
	public String visitBinaryExpression(BinaryExpression expression) {
		String left = expression.left.accept(this);
		String right = expression.right.accept(this);
		if (!left.equals(right)) {
			throw Parser.error(expression.operator, String.format("mismatch type: left (%s) vs right (%s)", left, right));
		}
		return switch (expression.operator.getType()) {
			case PLUS, STAR -> {
				if (!left.equals("int")) {
					throw Parser.error(expression.operator, String.format("mismatch type: left and right is %s, expected int", left));
				}
				yield "int";
			}
			case GREATER, GREATER_EQUAL -> {
				if (!left.equals("int")) {
					throw Parser.error(expression.operator, String.format("mismatch type: left and right is %s, expected int", left));
				}
				yield "bool";
			}
			case EQUAL_EQUAL -> "bool";
			default -> left;
		};
	}
	
	@Override
	public String visitUnaryExpression(UnaryExpression expression) {
		return null;
	}
	
	@Override
	public String visitGrouping(Grouping expression) {
		return expression.expression.accept(this);
	}
	
	@Override
	public String visitLiteral(Literal expression) {
		if (expression.value instanceof Integer) {
			return "int";
		}
		if (expression.value instanceof Boolean) {
			return "bool";
		}
		throw Parser.error(expression.location, String.format("Unrecognized literal type: '%s'", expression.value));
	}
	
	@Override
	public String visitVariable(Variable expression) {
		return expression.type.getLexeme();
	}
	
	@Override
	public String visitStatements(Statements statement) {
		for (Statement stmt: statement.statements) {
			stmt.accept(this);
		}
		return "void";
	}
	
	@Override
	public String visitIfThenElse(IfThenElse statement) {
		String type = statement.condition.accept(this);
		if (!type.equals("bool")) {
			Location location = new LocationGetter().getLocation(statement.condition);
			throw Parser.error(location, String.format("expected bool, got '%s' as if's condition", type));
		}
		statement.thenBranch.accept(this);
		if (statement.elseBranch != null) statement.elseBranch.accept(this);
		return null;
	}
	
	@Override
	public String visitDoWhile(DoWhile statement) {
		String type = statement.condition.accept(this);
		if (!type.equals("bool")) {
			Location location = new LocationGetter().getLocation(statement.condition);
			throw Parser.error(location, String.format("expected bool, got '%s' as do while's condition", type));
		}
		statement.body.accept(this);
		return null;
	}
	
	@Override
	public String visitPrint(Print statement) {
		statement.expression.accept(this);
		return null;
	}
	
	@Override
	public String visitDeclaration(Declaration statement) {
		String type = statement.variable.type.getLexeme();
		if (statement.initializer != null) {
			String exprType = statement.initializer.accept(this);
			if (!type.equals(exprType)) {
				throw Parser.error(new LocationGetter().getLocation(statement.initializer),
						String.format("%s is type %s but tried to init expression type %s",
								statement.variable.identifier.getLexeme(),
								type,
								exprType
						));
			}
		}
		return null;
	}
	
	@Override
	public String visitAssignment(Assignment statement) {
		String type = statement.variable.type.getLexeme();
		String exprType = statement.expression.accept(this);
		if (!type.equals(exprType)) {
			throw Parser.error(new LocationGetter().getLocation(statement.expression),
					String.format("%s is type %s but tried to assign expression type %s",
							statement.variable.identifier.getLexeme(),
							type,
							exprType
					));
		}
		return null;
	}
}
