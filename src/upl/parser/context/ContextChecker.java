package upl.parser.context;

import upl.CompileTimeError;
import upl.lexer.Token;
import upl.lexer.TokenType;
import upl.parser.Parser;
import upl.parser.general.expression.*;
import upl.parser.general.statement.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Checking the context:
 * <br>
 * Variable x have to be defined before use (either in this scope or outer, if any)
 * <br>
 * Create a whole new tree again, from given tree (because why not)
 */
public class ContextChecker implements Expression.Visitor<Expression>, Statement.Visitor<Statement> {
	private Environment environment = null;
	private final Statement program;
	public Environment getEnvironment() {
		return environment;
	}
	
	public ContextChecker(Statement program) {
		this.program = program;
	}
	public Statements check() {
		return (Statements) program.accept(this);
	}
	@Override
	public Expression visitBinaryExpression(BinaryExpression expression) {
		Expression left = expression.left.accept(this);
		Expression right = expression.right.accept(this);
		if (left == expression.left && right == expression.right) {
			return expression;
		}
		return new BinaryExpression(left, expression.operator, right);
	}
	
	@Override
	public Expression visitUnaryExpression(UnaryExpression expression) {
		Expression child = expression.expression.accept(this);
		if (child == expression.expression) {
			return expression;
		}
		return new UnaryExpression(expression.operator, expression);
	}
	
	@Override
	public Expression visitGrouping(Grouping expression) {
		Expression child = expression.expression.accept(this);
		if (child == expression.expression) {
			return expression;
		}
		return new Grouping(expression);
	}
	
	@Override
	public Expression visitLiteral(Literal expression) {
		return expression;
	}
	
	@Override
	public Expression visitVariable(Variable expression) {
		if (expression.type.getLexeme().equals(Parser.magicKeyword)) {
			try {
				Token type = environment.get(expression.identifier).getType();
				return new Variable(type, expression.identifier);
			} catch (CompileTimeError error) {
				return new Variable(new Token(TokenType.IDENTIFIER, "undefined"), expression.identifier);
			}
		} else return expression;
	}
	
	@Override
	public Statement visitStatements(Statements statement) {
		environment = new Environment(environment);
		List<Statement> statements = new ArrayList<>();
		boolean same = true;
		for (Statement stmt : statement.statements) {
			Statement ret = stmt.accept(this);
			statements.add(ret);
			if (ret != stmt) {
				same = false;
			}
		}
		
		environment = environment.parent;
		
		if (same) return statement;
		return new Statements(statements);
	}
	
	@Override
	public Statement visitIfThenElse(IfThenElse statement) {
		Expression condition = statement.condition.accept(this);
		Statements thenBranch = (Statements) statement.thenBranch.accept(this);
		Statements elseBranch = null;
		if (statement.elseBranch != null) {
			elseBranch = (Statements) statement.elseBranch.accept(this);
		}
		if (condition == statement.condition && thenBranch == statement.thenBranch && elseBranch == statement.elseBranch) return statement;
		return new IfThenElse(condition, thenBranch, elseBranch);
	}
	
	@Override
	public Statement visitDoWhile(DoWhile statement) {
		Statements body = (Statements) statement.body.accept(this);
		Expression cond = statement.condition.accept(this);
		if (body == statement.body && cond == statement.condition) {
			return statement;
		}
		return new DoWhile(body, cond);
	}
	
	@Override
	public Statement visitPrint(Print statement) {
		Expression expression = statement.expression.accept(this);
		if (expression == statement.expression) {
			return statement;
		}
		return new Print(expression);
	}
	
	@Override
	public Statement visitDeclaration(Declaration statement) {
		if (statement.initializer == null) {
			environment.define(statement.variable.identifier, statement.variable.type);
			return statement;
		}
		Expression expression = statement.initializer.accept(this);
		Variable variable = (Variable) statement.variable.accept(this);
		try {
			environment.define(statement.variable.identifier, statement.variable.type);
		} catch (CompileTimeError ignored) {} // to get as many error as possible
		if (variable == statement.variable && expression == statement.initializer) {
			return statement;
		}
		return new Declaration(variable, expression);
	}
	
	@Override
	public Statement visitAssignment(Assignment statement) {
		Variable variable = (Variable) statement.variable.accept(this);
		Expression expression = statement.expression.accept(this);
		if (variable == statement.variable && expression == statement.expression) {
			return statement;
		}
		return new Assignment(variable, expression);
	}
}
