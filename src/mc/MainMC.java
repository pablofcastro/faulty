package mc;

import java.io.*;

import faulty.*;
import formula.*;
import net.sf.javabdd.*;

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
       
       
       
      //  try{
        	if (args.length == 2){
                model = prog.parse(args[0]);
                
                if(model!=null){
                	//for debugging purposes
                	//System.out.println(printModelInformation(model));
                    
                    formula = new FormulaParser(prog.getSymbolsTable(), model); //Creates the parser for the formula with the symbol table
                    property = formula.parse(args[1]);
                    
                    //model.buildModel();//.getTransitions().printSet();
                    if(model!=null && property!=null){
                         if (DCTL_MC.mc_algorithm(property, model.buildModel())) {
                    	  //   System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
				    	     System.out.println("Property "+ printFormula(property)  + " is TRUE in the model.");
                         }
                         else{
                    	     System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n"); 
					         System.out.println("Property "+ printFormula(property) + " is FALSE in the model.");
                         }
                      
                     }
                     else{
            	          System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
                          System.out.println("Property or Model have errors.");
                     }
                     Program.myFactory.done(); // we reset the factory
               }
                      	           
            }
        	if (args.length == 3){
        		
        		// This option uses the 
        		if (args[0].equals("-eq")){
        			
        			 model = prog.parse(args[1]);
                     
                     if(model!=null){
                     	//for debugging purposes
                     	//System.out.println(printModelInformation(model));
                    	
                         formula = new FormulaParser(prog.getSymbolsTable(), model); //Creates the parser for the formula with the symbol table 
                         property = formula.parse(args[2]);
                         
                         // negated formula in NNF 
                         FormulaElement NNFform = DCTL_MC.toNNF(new Negation("!", property));
                         //PrettyPrintVisitor print = new PrettyPrintVisitor();
                         //NNFform.accept(print);
                         //print.printFormula();
                         //model.buildModel().getTransitions().printSet();
                         //for (int i=0; i< model.buildPartialModels().size();i++){                     	 
                         //	 System.out.println(model.buildPartialModels().get(i).getDisjuncts());
                         //}
                         
                         if(model!=null && property!=null){
                              if (DCTL_MC.mc_algorithm_eq(NNFform, model)){ // the formula is false
     				    	     System.out.println("Property  is FALSE in the model.");
     				    	     System.out.println("Counterexample:");
     				    	     System.out.println(DCTL_MC.getWitness(NNFform, model));
                              }
                              else{ // otherwise the property is true
     					         System.out.println("Property  is TRUE in the model.");
     					         DCTL_MC.mc_algorithm_eq(property, model);
     					         System.out.println("Witness:");
     					         System.out.println(DCTL_MC.getWitness(property, model));
     					         // prints counterexample
     					         //System.out.println("Counterexample:");
     					         //System.out.println(DCTL_MC.getWitness(new Negation("!", property), model)); // remove in
                              }
                           
                          }
                          else{
                 	           //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
                               System.out.println("Property or Model have errors.");
                          }
                         Program.myFactory.done(); // the BDDFactory is reset
                    }		
        		}
        		if (args[0].equals("-deq")){ // the case for disjoint early quantification
        			
       			 model=prog.parse(args[1]);
                    
                    if(model!=null){
                    	//for debugging purposes
                    	//System.out.println(printModelInformation(model));
                        
                        formula = new FormulaParser(prog.getSymbolsTable(), model); //Creates the parser for the formula with the symbol table 
                        property = formula.parse(args[2]);
                       
                        //model.buildModel().getTransitions().printSet();
                        if(model!=null && property!=null){
                             if (DCTL_MC.mc_algorithm_deq(property, model)) {
                        	     //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
    				    	     System.out.println("Property  is TRUE in the model.");
                             }
                             else{
                        	     //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n"); 
    					         System.out.println("Property  is FALSE in the model.");
                             }
                          
                         }
                         else{
                	           //System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
                              System.out.println("Property or Model have errors.");
                         }
                   }		
        		}
        	}
        	else{
            	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n\n");
            	System.out.println("usage: faulty-check <options> <model file> <property File>.");
           }
            
      //  }
      //  catch(Exception e){
      //      System.out.println("Faulty Model checker fail: " + e.getMessage() );
      //  }
      //  System.out.println("\n\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
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
    
    /**
     * 
     * @param model that specify the current faultyProgram
     * @return show the information of this model.-
     */
    private static String printModelInformation(Program model){
    	return model.toString();
 	}

   
 	
}