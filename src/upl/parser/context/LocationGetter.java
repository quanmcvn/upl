package upl.parser.context;

import upl.lexer.Location;
import upl.parser.general.expression.*;

public class LocationGetter implements Expression.Visitor<Location> {
	public Location getLocation(Expression expression) {
		return expression.accept(this);
	}
	@Override
	public Location visitBinaryExpression(BinaryExpression expression) {
		return expression.left.accept(this);
	}
	
	@Override
	public Location visitUnaryExpression(UnaryExpression expression) {
		return expression.operator.getLocation();
	}
	
	@Override
	public Location visitGrouping(Grouping expression) {
		return expression.expression.accept(this);
	}
	
	@Override
	public Location visitLiteral(Literal expression) {
		return expression.location;
	}
	
	@Override
	public Location visitVariable(Variable expression) {
		return expression.identifier.getLocation();
	}
}
