package mc;

import java.io.*;

import faulty.*;
import formula.*;


/**
 * This class represents the compiler.
 */
public class MainMC {
	
	private static Program model;	
    private static FormulaElement property;
   
    
    public static void main(String[] args)
    {
       ProgramParser prog = new ProgramParser();
       FormulaParser formula;
       
       
        try{
        	if (args.length == 2){
                model=prog.parse(args[0]);
                
                if(model!=null){
                    formula = new FormulaParser(prog.getSymbolsTable()); //Creates the parser for the formula with the symbol table 
                    property = formula.parse(args[1]);
            
                    if(model!=null && property!=null){
                         if (DCTL_MC.mc_algorithm(property, model.buildModel())) {
                    	     System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
				    	     System.out.println("Property "+ printFormula(property)  + " is TRUE in the model.");
                         }
                         else{
                    	     System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n"); 
					         System.out.println("Property "+ printFormula(property) + " is FALSE in the model.");
                         }
                      
                     }
                     else{
            	          System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
                          System.out.println("Property or Model have some errors.");
                     }
               }

           }else{
            	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
            	System.out.println("usage: java -jar dCTL_checker.jar <model file> <property File>.");
           }
            
        }
        catch(Exception e){
            System.out.println("Faulty Model checker fail: " + e.getMessage() );
            
        }
        System.out.println("\n\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
    
    
    /**
     * 
     * @param prop formula that specify the current property
     * @return Return an string that represents this formula.-
     */
    private static String printFormula(FormulaElement prop){
       PrettyPrintVisitor printVisitor = new PrettyPrintVisitor();
 	   prop.accept(printVisitor);
 	   return printVisitor.getPrettyFormula(); 
 	}
   
 	
}