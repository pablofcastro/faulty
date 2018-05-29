package mc;

import java_cup.runtime.*;
import java.io.*;
import java.util.LinkedList;
import faulty.*;
import faulty.auxiliar.*;
import parserProgram.*;

/**
 * This class represents the compiler.
 */
public class ProgramParser {
	
	private parserFaulty parser;	// Parser
	private static LinkedList<faulty.auxiliar.Error> errorList; // Errors
    private static SymbolsTable symbolsTable;
    private static FileReader programFile;
    
    
    public ProgramParser(){
    	this.programFile=null;
    	this.symbolsTable =null;
    	this.errorList= null;
    }
    
    public Program parse(String NameFile){
        Program faultyProg=null;
        try {
            programFile = new FileReader(NameFile);
           

            // Read file
 			parser = new parserFaulty(new scannerFaulty(programFile));
 			AuxiliarProgram program = (AuxiliarProgram)parser.parse().value;

 			 
 			
 			// Check Types
 			Type result = checkTypes(program);
 			if(result == Type.ERROR){
                for(int i=0; i<errorList.size(); i++){
                    System.out.println(errorList.get(i).getErrorMsg());
                }               
            }
            else{ // If not errors, build the concrete Faulty program    
            	
                faultyProg = buildProgram(program);
            }    
        } catch (Exception e) {        	
 			System.out.println("Program Error." + e.getMessage());
            e.printStackTrace(System.out);
 		}
        
        return faultyProg;
 		
 	}

    
    public Program parseFromString(String myProgram){
        Program faultyProg=null;
        try {
            //programFile = new FileReader(NameFile);
          
            // Read the string
 			parser = new parserFaulty(new scannerFaulty(new java.io.StringReader(myProgram)));
 			AuxiliarProgram program = (AuxiliarProgram) parser.parse().value;

 			 
 			
 			// Check Types
 			Type result = checkTypes(program);
 			if(result == Type.ERROR){
                for(int i=0; i<errorList.size(); i++){
                    System.out.println(errorList.get(i).getErrorMsg());
                }               
            }
            else{ // If not errors, build the concrete Faulty program    
            	
                faultyProg = buildProgram(program);
            }    
        } catch (Exception e) {        	
 			System.out.println("Program Error." + e.getMessage());
            e.printStackTrace(System.out);
 		}
        
        return faultyProg;	
 	}
    
    /**
     * 
     * @return Return the symbols table of the model.
     */
    public static SymbolsTable getSymbolsTable(){
    	return symbolsTable;
    }
    
 	/**
 	 * Build the Concrete Faulty Program
  	 */
 	private static Program buildProgram(AuxiliarProgram prog) {
 		
        BuilderVisitor builder = new BuilderVisitor(symbolsTable);        
        prog.accept(builder);
       
        return  builder.getProgram();
    }

 	/**
 	 * Check types
 	 */
 	private static Type checkTypes(AuxiliarProgram prog) {
        TypeCheckerVisitor typeV = new TypeCheckerVisitor();
        prog.accept(typeV);
        Type result = typeV.getType();
        errorList = typeV.getErrorList();
        symbolsTable =typeV.getSymbolTable();
        return result;
 	}
 	
}