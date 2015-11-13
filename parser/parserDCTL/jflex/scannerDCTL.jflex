package parserFormula;

import java_cup.runtime.*;
%%

%public
%cup
%line
%column
%char
%full


%eofval{
return  new Symbol(symDCTL.EOF,yyline,yycolumn,"");
%eofval}

%class scannerDCTL
%{
	public scannerDCTL(java.io.InputStream r, SymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	private SymbolFactory sf;
%}

comment=("//"(.)*)|"/*"(((([^*/])*"*"[^/])*)|((([^*])*"/"[^*/])*)|([^*/])*)*"*/"

%%

	{comment} {}
";" { return new Symbol(symDCTL.SEMI,yyline,yycolumn,yytext()); }
"A" { return new Symbol(symDCTL.FORALL,yyline,yycolumn,yytext()); }
"E" { return new Symbol(symDCTL.EXIST, yyline,yycolumn,yytext()); }
"P" { return new Symbol(symDCTL.PERMISSION,yyline,yycolumn,yytext()); }
"O" { return new Symbol(symDCTL.OBLIGATION,yyline,yycolumn,yytext()); }
"R" { return new Symbol(symDCTL.RECOVERY,yyline,yycolumn,yytext()); }
"(" { return new Symbol(symDCTL.LPAREN,yyline,yycolumn,yytext()); }
")" { return new Symbol(symDCTL.RPAREN,yyline,yycolumn,yytext()); }
"X" { return new Symbol(symDCTL.NEXT,yyline,yycolumn,yytext()); }
"U" { return new Symbol(symDCTL.UNTIL,yyline,yycolumn,yytext()); }
"W" { return new Symbol(symDCTL.WEAK,yyline,yycolumn,yytext()); }
"true" {return new Symbol(symDCTL.TRUE,yyline,yycolumn,yytext()); }
"false" {return new Symbol(symDCTL.FALSE,yyline,yycolumn,yytext()); }
[a-z][_.a-z0-9]* { return new Symbol(symDCTL.ID,yyline,yycolumn,yytext()); }
"!" { return new Symbol(symDCTL.NEG,yyline,yycolumn,yytext());}
"->" { return new Symbol(symDCTL.IMPLIES,yyline,yycolumn,yytext());}
"&" { return new Symbol(symDCTL.AND,yyline,yycolumn,yytext());}
"|" { return new Symbol(symDCTL.OR,yyline,yycolumn,yytext());}
"~>" { return new Symbol(symDCTL.IMPLIESTEMP,yyline,yycolumn,yytext()); }
[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+ yytext() + " - line: " + yyline + " - column: " + yycolumn) ; }

