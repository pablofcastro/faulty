package mc;

import java.util.*;
import net.sf.javabdd.*;
import formula.*;


public class DCTL_MC {

/**
 * A method to calculate the model checking sat of a formula, wihtout fairness
 * @param form
 * @param m
 * @return
 */
public static boolean mc_algorithm(FormulaElement form, BDDModel m ){
	
	BDD sat_form = Sat(form, m);
	//printSolutions(sat_form.allsat());
	try{
	     if (sat_form.and(m.getIni()).satCount() > 0 )
		    return true;
	     else
	        return false;
	}
	catch(NullPointerException e){
		return false;
	}
}	

/**
 * A method to calculate the model checking of a given formula with fairness
 * @param form
 * @param m
 * @return
 */
public static boolean fair_mc_algorithm(FormulaElement form, BDDModel m, LinkedList<BDD> goals){
	
	BDD sat_form = fairSat(form, m, goals);
	printSolutions(sat_form.allsat());
	try{
	     if (sat_form.and(m.getIni()).satCount() > 0 )
		    return true;
	     else
	        return false;
	}
	catch(NullPointerException e){
		return false;
	}
}	

private static BDD Sat(FormulaElement f, BDDModel m){	
	
	SatVisitor s = new SatVisitor(m);
    f.accept(s);
	return s.getSat();
}


private static BDD fairSat(FormulaElement f, BDDModel m, LinkedList<BDD> goals){
	FairSatVisitor s = new FairSatVisitor(m, goals);
	f.accept(s);
	return s.getSat();
}


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

}
