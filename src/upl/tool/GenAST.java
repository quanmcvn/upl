package upl.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

// Generate AST, automate plz
public class GenAST {
	public void defineAST(String packageName, String superType, List<String> types, List<String> additionalImportPackage) {
		String outputDirectory = convertPackageToOutputDirectory(packageName);
		String packageStatement = String.format("package %s;", packageName);
		try (PrintWriter writer = new PrintWriter(String.format("%s/%s.java", outputDirectory, superType), StandardCharsets.UTF_8)) {
			writer.println(packageStatement);
			writer.println();
			for (String importPackage : additionalImportPackage) {
				writer.printf("import %s;\n", importPackage);
			}
			writer.println();
			
			writer.printf("public abstract class %s {\n", superType);
			
			defineVisitor(writer, superType, types);
			
			writer.printf("}\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim(); // very robust :v
			defineType(outputDirectory, packageStatement, superType, className, fields, additionalImportPackage);
		}
	}
	
	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println("\tpublic interface Visitor<R> {");
		
		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.printf("\t\tR visit%s(%s %s);\n", typeName, typeName, baseName.toLowerCase());
		}
		
		writer.println("\t}");
		writer.println("\tpublic abstract <R> R accept(Visitor<R> visitor);");
	}
	
	private void defineType(String outputDirectory, String packageStatement,
	                        String baseName, String className,
	                        String fieldList, List<String> additionalImportPackage) {
		String[] fields = fieldList.split(", ");
		try (PrintWriter writer = new PrintWriter(String.format("%s/%s.java", outputDirectory, className), StandardCharsets.UTF_8)) {
			writer.println(packageStatement);
			writer.println();
			for (String importPackage : additionalImportPackage) {
				writer.printf("import %s;\n", importPackage);
			}
			writer.println();
			
			writer.printf("public class %s extends %s {\n", className, baseName);
			
			// Fields
			for (String field : fields) {
				writer.printf("\tpublic final %s;\n", field);
			}
			
			// Constructor
			writer.printf("\tpublic %s (%s) {\n", className, fieldList);
			for (String field : fields) {
				String name = field.split(" ")[1];
				writer.printf("\t\tthis.%s = %s;\n", name,  name);
			}
			writer.printf("\t}\n");
			
			// Visitor
			writer.println("\t@Override");
			writer.println("\tpublic <R> R accept(Visitor<R> visitor) {");
			writer.printf("\t\treturn visitor.visit%s(this);\n", className);
			writer.println("\t}");
			
			// end
			writer.printf("}\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String convertPackageToOutputDirectory(String packageName) {
		return ("src/" + packageName.replace('.', '/'));
	}
	
	public static void main(String[] args) {
//		System.out.println(convertPackageToOutputDirectory("upl.parser.general.expression"));
		GenAST genAST = new GenAST();
		genAST.defineAST("upl.parser.general.expression", "Expression", Arrays.asList(
				"BinaryExpression : Expression left, Token operator, Expression right",
				"UnaryExpression  : Token operator, Expression expression",
				"Grouping         : Expression expression",
				"Literal          : Object value",
				"Variable         : Token type, Token identifier"
		), Arrays.asList(
				"upl.lexer.Token"
		));
		genAST.defineAST("upl.parser.general.statement", "Statement", Arrays.asList(
				"Statements  : List<Statement> statements",
				"IfThenElse  : Expression condition, Statements thenBranch, Statements elseBranch",
				"DoWhile     : Statements body, Expression condition",
				"Print       : Expression expression",
				"Declaration : Variable variable, Expression initializer",
				"Assignment  : Variable variable, Expression expression"
		), Arrays.asList(
				"java.util.List",
				"upl.lexer.Token",
				"upl.parser.general.expression.Expression",
				"upl.parser.general.expression.Variable"
		));
	}
}
