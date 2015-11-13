package mc;

import java.io.*;
import java.util.LinkedList;

import formula.*;
import parserFormula.*;
import java_cup.runtime.*;
import faulty.auxiliar.*;

/**
 * This class represents the compiler.
 */
public class FormulaParser {
    
    private parserDCTL parserForm;	// Parser
    private static SymbolsTable symbolsTable;
    private static FileReader formulaFile;
    private static LinkedList<faulty.auxiliar.Error> listError;

	
    public FormulaParser(SymbolsTable table){
        formulaFile=null;
        symbolsTable = table;
    }
    
    public FormulaElement parse(String NameFile){
        FormulaElement formulaDCTL=null;
        try {
        	
            formulaFile = new FileReader(NameFile);
   			// Read file
            parserForm = new parserDCTL(new scannerDCTL(formulaFile));
            
 			formulaDCTL = (FormulaElement)parserForm.parse().value;
 			
 			//checkTypes
 			if(formulaDCTL!=null){
 				Type result = checkTypes(formulaDCTL);
 				
 				if(result == Type.ERROR){
 					for(int i=0;i<listError.size();i++){
 						System.out.println(listError.get(i).getErrorMsg()); 
 					}
 				}
 			}
 			
        }
        catch (Exception e) {
             System.out.println("Formula Error");
        }
    
        return formulaDCTL;
    }
    
    /**
 	 * Check types
 	 */
 	private static Type checkTypes(FormulaElement property) {
        formula.TypeCheckerVisitor typeV = new formula.TypeCheckerVisitor(symbolsTable);
        property.accept(typeV);
        Type result = typeV.getType();
        listError = typeV.getErrorList();
        return result;
 	}
    
    
}