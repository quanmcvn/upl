package upl.parser.context;

import upl.CompileTimeError;
import upl.lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Environment {
	public static class EnvironmentEntry {
		private final Token type;
		private EnvironmentEntry(Token type) {
			this.type = type;
		}
		public Token getType() {
			return type;
		}
		@Override
		public String toString() {
			return type.toString();
		}
	}
	private EnvironmentEntry newEntry(Token type ) {
		return new EnvironmentEntry(type);
	}
	public final List<Environment> environmentList = new ArrayList<>();
	public final Environment parent;
	private final Map<String, EnvironmentEntry> values = new HashMap<>();
	public Environment() {
		parent = null;
	}
	public Environment(Environment parent) {
		this.parent = parent;
		if (parent != null) parent.environmentList.add(this);
	}
	public EnvironmentEntry get(Token name) {
		if (values.containsKey(name.getLexeme())) {
			return values.get(name.getLexeme());
		}
		if (parent != null) return parent.get(name);
		throw new CompileTimeError(name, String.format("identifier \"%s\" is undefined", name.getLexeme()));
	}
	
	public void define(Token name, Token type) {
		EnvironmentEntry entry = newEntry(type);
		if (values.containsKey(name.getLexeme())) {
			EnvironmentEntry prev = values.get(name.getLexeme());
			if (prev.type.getLexeme().equals(entry.type.getLexeme())) {
				throw new CompileTimeError(name, String.format("redeclaration of \"%s %s\"\nnote: previously declared at line %d", entry.type.getLexeme(), name.getLexeme(), prev.type.getLine()));
			} else {
				throw new CompileTimeError(name, String.format("conflicting declaration of \"%s %s\"\nnote: previously declared as \"%s %s\" at line %d",  entry.type.getLexeme(), name.getLexeme(), prev.type.getLexeme(), name.getLexeme(), prev.type.getLine()));
			}
		}
		values.put(name.getLexeme(), entry);
	}
	private Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.parent;
		}
		
		return environment;
	}
	@Override
	public String toString() {
		String result = values.toString();
		if (parent != null) {
			result += " -> " + parent;
		}
		
		return result;
	}
}
