import java.util.*;


import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;
import faulty.*;

/**
 * This class models the Muller circuti using the Faulty language.
 */
public class Te{

	
	
	/**
	 * Prints the set of truth assignments specified by this list.
	 * @param sol
	 */
	private static void  printSolutions( List sol){

		Iterator i = sol.iterator();
		System.out.print("\nSolutions = ");          
		while (i.hasNext()) {     	
			byte[] c1 = (byte[])i.next();

			if (c1 != null){
				System.out.print("\n[");  
				for (int j=0; j < c1.length; j++   ){
					System.out.print(c1[j]); 

					if (j != c1.length -1) //if not is the last element, prints ","
						System.out.print(",");
				} 
				System.out.print("]\n");  
			}
		} 
		System.out.print("\n\n");  
	}
	/*//////////////////////////////// MAIN //////////////////////////////////////////////////////*/    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// we create the program
		// Process
		// te -> te = !te
		BDDModel model = new BDDModel();
		Program myProgram = new Program(4,0,6,0,0,0, model);
		VarBool te = new VarBool("te", model);	
		faulty.Process process = new faulty.Process("example", 1, 3, 3, 1, model);
		process.addVarBool(te);
		ConsBoolExp tt = new ConsBoolExp(true);
		NegBoolExp neg_te = new NegBoolExp(te);
		VarAssign assign = new VarAssign(te,neg_te);
		
		
		Branch branch1 = new Branch(te, assign, false, process);
		
		process.addBranch(branch1);
		process.setInitialCond(te.getBDD());
		process.setNormativeCond(Program.myFactory.one());
		myProgram.addProcess(process);
		
		FormulaElement vte = new Variable("te");
		FormulaElement nte = new Negation("!", vte);
		
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("ff", false);
		
		if (DCTL_MC.mc_algorithm(nte, myProgram.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}		
		
		

 		
		
		//System.out.println("--------------------------- Property : unfairOW(p, ff) -----------------------------------\n");
		//
		//FormulaElement form13 = new OW("O", vp , ff); // the second argument is not needed 
		//
		//if (DCTL_MC.mc_algorithm(form13, m)) {
		//	System.out.println("Property is TRUE in the model.");
		//}
		//else{
		//	System.out.println("Property is FALSE in the model.");
		//}		
		
	}

}
