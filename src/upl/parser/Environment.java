package upl.parser;

import upl.CompileTimeError;
import upl.lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
	public static class EnvironmentEntry {
		Token type;
		
		public EnvironmentEntry(Token type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return type.toString();
		}
	}
	public final Environment enclosing;
	private final Map<String, EnvironmentEntry> values = new HashMap<>();
	public Environment() {
		enclosing = null;
	}
	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}
	EnvironmentEntry get(Token name) {
		if (values.containsKey(name.lexeme)) {
			return values.get(name.lexeme);
		}
		if (enclosing != null) return enclosing.get(name);
		throw new CompileTimeError(name, String.format("identifier \"%s\" is undefined", name.lexeme));
	}
	
	void define(Token name, EnvironmentEntry entry) {
		if (values.containsKey(name.lexeme)) {
			EnvironmentEntry prev = values.get(name.lexeme);
			if (prev.type.lexeme.equals(entry.type.lexeme)) {
				throw new CompileTimeError(name, String.format("redeclaration of \"%s %s\"\nnote: previously declare at line %d", entry.type.lexeme, name.lexeme, prev.type.line));
			} else {
				throw new CompileTimeError(name, String.format("conflicting declaration of \"%s %s\"\nnote: previously declare as \"%s %s\" at line %d",  entry.type.lexeme, name.lexeme, prev.type.lexeme, name.lexeme, prev.type.line));
			}
		}
		values.put(name.lexeme, entry);
	}
	Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}
		
		return environment;
	}
	Object getAt(int distance, String name) {
		return ancestor(distance).values.get(name);
	}
	@Override
	public String toString() {
		String result = values.toString();
		if (enclosing != null) {
			result += " -> " + enclosing;
		}
		
		return result;
	}
}
