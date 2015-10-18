

import java_cup.runtime.*;
import faulty.*;

%%

%public
%cup
%line
%column
%char
%full

%eofval{
return  new Symbol(sym.EOF,yyline,yycolumn,"");
%eofval}

%class scannerFaulty
%{
	public scannerFaulty(java.io.InputStream r, SymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	private SymbolFactory sf;
%}



digit=[0-9]
char=[a-zA-Z]
id = {char}({char}|{digit})*	
integer = {digit}{digit}*	
comment=("//"(.)*)|"/*"(((([^*/])*"*"[^/])*)|((([^*])*"/"[^*/])*)|([^*/])*)*"*/"
%%


(\n|\t|\r|\f|" ") {}  
{comment} {}

"+" {  System.out.println("SUMA:"+ yytext());      
        //String value=yytext();
        return new Symbol(sym.PLUS,yyline,yycolumn,yytext());
    }
    
"-" {   System.out.println("RESTA:"+ yytext()); 
        //String value=yytext();
        return new Symbol(sym.MINUS,yyline,yycolumn, yytext());
    }
"*" {   System.out.println("PROD:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.ASTERISK,yyline,yycolumn,yytext());
    }
"/" {   System.out.println("DIV:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.SLASH,yyline,yycolumn,yytext());
    }
"(" {
        System.out.println("LPARENT:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.LPARENT,yyline,yycolumn,yytext());
    }
")" {
        System.out.println("RPARENT:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.RPARENT,yyline,yycolumn,yytext());
    }
";" {
        System.out.println("PUNTOYCOMA: "+ yytext());
        //String value=yytext();
        return new Symbol(sym.SEMICOLON,yyline,yycolumn,yytext());
    }
":" {   System.out.println(" DOSPUNTOS: "+ yytext());
        //String value=yytext();
        return new Symbol(sym.COLON,yyline,yycolumn,yytext());
    }
"->" {
        System.out.println("FLECHA: "+ yytext());
        //String value=yytext();
        return new Symbol(sym.RIGHTARROW,yyline,yycolumn,yytext());
     }
"=" {
        System.out.println(" ASIG:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.EQUAL,yyline,yycolumn,yytext());
    }
"==" {
        System.out.println(" IGUAL:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.DOUBLE_EQUAL,yyline,yycolumn,yytext());
     }
"," {
        System.out.println(" COMA:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.COMMA,yyline,yycolumn,yytext());
    }
"||" {
        System.out.println(" OR:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.OR,yyline,yycolumn,yytext());
     }
"&&" {
        System.out.println(" AND:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.AND,yyline,yycolumn,yytext());
     }
">" {
        System.out.println("MAYOR :"+ yytext());
        //String value=yytext();
        return new Symbol(sym.GT,yyline,yycolumn,yytext());
    }
"<" {
        System.out.println(" MENOR :"+ yytext());
        //String value=yytext();
        return new Symbol(sym.LT,yyline,yycolumn,yytext());
    }
"!" {
        System.out.println(" NEG:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.EXCLAMATION,yyline,yycolumn,yytext());
    }
"{" {
        System.out.println(" LLAVEABRE:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.LBRACE,yyline,yycolumn,yytext());
    }
"}" {
        System.out.println(" LLAVECIERRA:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.RBRACE,yyline,yycolumn,yytext());
    }
"[" {
        System.out.println(" CORCHETE ABRE:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.LBRACKET,yyline,yycolumn,yytext());
    }
"]" {
        System.out.println(" CORCHETE CIERRA:"+ yytext());
        //String value=yytext();
        return new Symbol(sym.RBRACKET,yyline,yycolumn,yytext());
    }

"." {
        System.out.println(" PUNTO :"+ yytext());
        //String value=yytext();
        return new Symbol(sym.POINT,yyline,yycolumn,yytext());
    }
"TRUE"  {  System.out.println(" TRUE :"+ yytext()); 
           //Boolean value = new Boolean(true);
           return new Symbol(sym.FALSE,yyline,yycolumn, yytext());
         }
"FALSE"  { System.out.println(" FALSE :"+ yytext());
           //Boolean value = new Boolean(false);
           return new Symbol(sym.FALSE,yyline,yycolumn, yytext());
         }
"BOOL"  {
           System.out.println(" BOOL:"+ yytext());
           //String value=yytext();
           return new Symbol(sym.BOOL,yyline,yycolumn,yytext());
        }
"INT" {
        System.out.println(" INT:"+ yytext());
         //String value=yytext();
         return new Symbol(sym.INT,yyline,yycolumn,yytext());
      }

"INIT" {
          System.out.println(" INIT:"+ yytext());
          //String value=yytext();
          return new Symbol(sym.INIT,yyline,yycolumn,yytext());
       }

"NORMATIVE" {
          System.out.println(" NORMATIVE:"+ yytext());
          //String value=yytext();
          return new Symbol(sym.NORMATIVE,yyline,yycolumn,yytext());
       }


       
"put" {   System.out.println(" PUT :"+ yytext());
          //String value=yytext();
          return new Symbol(sym.PUT,yyline,yycolumn,yytext());
       }

"get" {   System.out.println(" GET:"+ yytext());
          //String value=yytext();
          return new Symbol(sym.GET,yyline,yycolumn,yytext());
       }

"of" {
          System.out.println(" OF :"+ yytext());
          //String value=yytext();
          return new Symbol(sym.OF,yyline,yycolumn,yytext());
       }
       
"Process" {
          System.out.println(" PROCESS :"+ yytext());
          //String value=yytext();
          return new Symbol(sym.PROCESS,yyline,yycolumn,yytext());
       }

"uses" {
          System.out.println(" USES:"+ yytext());
          //String value=yytext();
          return new Symbol(sym.USES,yyline,yycolumn,yytext());
       }
       

"Main" {
          System.out.println(" MAIN :"+ yytext());
          //String value=yytext();
          return new Symbol(sym.MAIN,yyline,yycolumn,yytext());
       }


"Run" {
          System.out.println(" RUN:"+ yytext());
          //String value=yytext();
          return new Symbol(sym.RUN,yyline,yycolumn,yytext());
       }
       
"Channel" { System.out.println(" CHANNEL:"+ yytext()); 
            //String value=yytext();
            return new Symbol(sym.CHANNEL,yyline,yycolumn,yytext());
          }
          
{id}  { System.out.println("ID :"+ yytext());        
        //String value=yytext();
        return new Symbol(sym.ID,yyline,yycolumn,yytext());
      }	
 
       
{integer} { System.out.println(" VALORINT:"+ yytext());
            //int value =Integer.parseInt(yytext());
            Integer value = new Integer(yytext());
            return new Symbol(sym.INTEGER,yyline,yycolumn,value);
          }

.     {   String value=yytext();
          System.out.println("Invalid token: " + value + ", line: " + yyline + ".-");
          //return new Symbol(sym.LEXICAL_ERROR,yyline,yycolumn,yytext());
       }