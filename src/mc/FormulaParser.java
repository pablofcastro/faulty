package mc;

import java.io.*;
import java.util.LinkedList;

import formula.*;
import parserFormula.*;
import java_cup.runtime.*;
import faulty.auxiliar.*;
import faulty.*;

/**
 * This class represents the compiler.
 */
public class FormulaParser {
    
    private parserDCTL parserForm;	// Parser
    private static SymbolsTable symbolsTable;
    private static FileReader formulaFile;
    private static LinkedList<faulty.auxiliar.Error> listError;
    private static Program model;
	
    public FormulaParser(SymbolsTable table, Program mod){
        this.formulaFile=null;
        this.symbolsTable = table;
        this.model =mod;
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
                    formulaDCTL=null; // set formula as null to stop the modelchecking due prescense of errors.
 				}
 			}
 			
        }
        catch (Exception e) {
             System.out.println("Formula Error");
        }
    
        return formulaDCTL;
    }
    
    public FormulaElement parseFromString(String myFormula){
        FormulaElement formulaDCTL=null;
        try {
            //formulaFile = new FileReader(NameFile);
   			// Read file
            parserForm = new parserDCTL(new scannerDCTL(new java.io.StringReader(myFormula)));
            
 			formulaDCTL = (FormulaElement)parserForm.parse().value;
 			
 			//checkTypes
 			if(formulaDCTL!=null){
 				Type result = checkTypes(formulaDCTL);			
 				if(result == Type.ERROR){
                    for(int i=0;i<listError.size();i++){
 						System.out.println(listError.get(i).getErrorMsg()); 
 					}
                    formulaDCTL=null; // set formula as null to stop the modelchecking due prescense of errors.
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
        formula.TypeCheckerVisitor typeV = new formula.TypeCheckerVisitor(symbolsTable, model);
        property.accept(typeV);
        Type result = typeV.getType();
        listError = typeV.getErrorList();
        return result;
 	}
    
    
}