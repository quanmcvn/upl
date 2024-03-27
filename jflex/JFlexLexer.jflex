package upl.lexer;


%%
%type int
%line
%column
%char
%class JFlexLexer
%{

	
%}
%eofval{
    System.out.println("EOF"); return 1;
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
"begin" { System.out.println("<BEGIN, ''> "); return 0;}
"end" { System.out.println("<END, ''> "); return 0;}
"if" { System.out.println("<IF, ''> "); return 0;}
"then" { System.out.println("<THEN, ''> "); return 0;}
"else" { System.out.println("<ELSE, ''> "); return 0;}
{TypeSpecifier} { System.out.printf("<Type, '%s'> \n", yytext()); return 0;}
">" { System.out.println("<GT, ''> "); return 0; }
">=" { System.out.println("<GTE, ''> "); return 0; }
"==" { System.out.println("<EQUAL, ''> "); return 0; }
"=" { System.out.println("<ASSIGN, ''> "); return 0; }
"(" { System.out.println("<LEFTBRACKET, ''> "); return 0; }
")" { System.out.println("<RIGHTBRACKET, ''> "); return 0; }
"{" { System.out.println("<LEFTBRACE, ''> "); return 0; }
"}" { System.out.println("<RIGHTBRACE, ''> "); return 0; }
";" { System.out.println("<SEMI, ''> "); return 0; }
"+" { System.out.println("<PLUS, ''> "); return 0;}
"*" { System.out.println("<TIMES, ''> "); return 0;}
{Identifier} { System.out.println("<ID, '" + yytext() + "'> "); return 0;}
{Number} { System.out.println("<NUM, '" + yytext() + "'> "); return 0;}
{WhiteSpace} { /* ignore white space. */ }
{Comment} { System.out.printf("Comment: %s\n", yytext()); }
. { System.out.println("Illegal character: '"+yytext()+ "' at " + (yyline + 1) + ":" + (yycolumn)); }