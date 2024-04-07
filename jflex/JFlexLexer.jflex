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
	return new Token(EOF, "", null, yyline + 1);
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
"begin" { return new Token(BEGIN, yytext(), null, yyline + 1); }
"end" { return new Token(END, yytext(), null, yyline + 1); }
"if" { return new Token(IF, yytext(), null, yyline + 1); }
"then" { return new Token(THEN, yytext(), null, yyline + 1); }
"else" { return new Token(ELSE, yytext(), null, yyline + 1); }
"print" { return new Token(PRINT, yytext(), null, yyline + 1); }
"do" { return new Token(DO, yytext(), null, yyline + 1); }
"while" { return new Token(WHILE, yytext(), null, yyline + 1); }
{TypeSpecifier} {
	if (yytext().equals("int")) return new Token(INT, yytext(), null, yyline + 1);
	else if (yytext().equals("bool")) return new Token(BOOL, yytext(), null, yyline + 1);
	Main.error(yyline + 1, yycolumn, "Unexpected " + yytext()); System.exit(1);
}
">" { return new Token(GREATER, yytext(), null, yyline + 1);  }
">=" { return new Token(GREATER_EQUAL, yytext(), null, yyline + 1);  }
"==" { return new Token(EQUAL_EQUAL, yytext(), null, yyline + 1);  }
"=" { return new Token(EQUAL, yytext(), null, yyline + 1);  }
"(" { return new Token(LEFT_PAREN, yytext(), null, yyline + 1);  }
")" { return new Token(RIGHT_PAREN, yytext(), null, yyline + 1);  }
"{" { return new Token(LEFT_BRACE, yytext(), null, yyline + 1);  }
"}" { return new Token(RIGHT_BRACE, yytext(), null, yyline + 1);  }
";" { return new Token(SEMICOLON, yytext(), null, yyline + 1); }
"+" { return new Token(PLUS, yytext(), null, yyline + 1); }
"*" { return new Token(STAR, yytext(), null, yyline + 1); }
{Identifier} { return new Token(IDENTIFIER, yytext(), null, yyline + 1);}
{Number} { return new Token(NUMBER, yytext(), Integer.parseInt(yytext()), yyline + 1);}
{WhiteSpace} { /* ignore white space. */ return null; }
{Comment} { /* System.out.printf("Comment: %s\n", yytext()); */ return null; }
. { Main.error(yyline + 1, yycolumn,"Unexpected " + yytext()); return null; }