package upl.lexer;

import upl.Main;
import static upl.lexer.TokenType.*;

%%
%type Token
%line
%column
%char
%class JFlexLexer
%{
%}
%eofval{
    System.out.println("EOF");
	return new Token(EOF, "", yyline + 1);
%eofval}

InputCharacter = [^\r\n]

Letter =  [A-Za-z]
Digit  =  [0-9]
Identifier  =  {Letter}+{Digit}*
Number = {Digit}+
TypeSpecifier = int|bool

LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment   = "/*"~"*/"
EndOfLineComment     = "//" {InputCharacter}*

%%
"begin" { return new Token(BEGIN, yytext(), yyline + 1); }
"end" { return new Token(END, yytext(), yyline + 1); }
"if" { return new Token(IF, yytext(), yyline + 1); }
"then" { return new Token(THEN, yytext(), yyline + 1); }
"else" { return new Token(ELSE, yytext(), yyline + 1); }
"print" { return new Token(PRINT, yytext(), yyline + 1); }
{TypeSpecifier} { 
	if (yytext().equals("int")) return new Token(INT, yytext(), yyline + 1); 
	else if (yytext().equals("bool")) return new Token(BOOL, yytext(), yyline + 1); 
	Main.error(yyline + 1, yycolumn,"Unexpected " + yytext()); System.exit(1);
}
">" { return new Token(GREATER, yytext(), yyline + 1);  }
">=" { return new Token(GREATER_EQUAL, yytext(), yyline + 1);  }
"==" { return new Token(EQUAL_EQUAL, yytext(), yyline + 1);  }
"=" { return new Token(EQUAL, yytext(), yyline + 1);  }
"(" { return new Token(LEFT_PAREN, yytext(), yyline + 1);  }
")" { return new Token(RIGHT_PAREN, yytext(), yyline + 1);  }
"{" { return new Token(LEFT_BRACE, yytext(), yyline + 1);  }
"}" { return new Token(RIGHT_BRACE, yytext(), yyline + 1);  }
";" { return new Token(SEMICOLON, yytext(), yyline + 1); }
"+" { return new Token(PLUS, yytext(), yyline + 1); }
"*" { return new Token(STAR, yytext(), yyline + 1); }
{Identifier} { return new Token(IDENTIFIER, yytext(), yyline + 1);}
{Number} { return new Token(NUMBER, yytext(), yyline + 1);}
{WhiteSpace} { /* ignore white space. */ }
{Comment} { System.out.printf("Comment: %s\n", yytext()); }
. { Main.error(yyline + 1, yycolumn,"Unexpected " + yytext()); return null; }