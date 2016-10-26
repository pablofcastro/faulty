package mc;

import java.util.*;
import net.sf.javabdd.*;
import formula.*;
import faulty.*;


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
 * 
 * @param form 	formula to be checked
 * @param p		the program to be verified
 * @return
 */
public static boolean mc_algorithm_eq(FormulaElement form, Program p){
	
	
	BDD sat_form = SatEQ(form, p);
	BDD init = p.getInitialCond();
	//sat_form.and(p.getInitialCond()).printSet();
	//System.out.println(p.toString());
	//printSolutions(sat_form.allsat());
	try{
	     if (sat_form.and(init).satCount() > 0 )
		    return true;
	     else
	        return false;
	}
	catch(NullPointerException e){
		return false;
	}
}	

public static boolean mc_algorithm_deq(FormulaElement form, Program p){
	
	
	BDD sat_form = SatDEQ(form, p);
	BDD init = p.getInitialCond();
	
	
	//printSolutions(sat_form.allsat());
	try{
	     if (sat_form.and(init).satCount() > 0 )
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
public static boolean fair_mc_algorithm(FormulaElement form, Program p, LinkedList<BDD> goals){
	
	BDD sat_form = fairSat(form, p, goals);
	BDD init = p.getInitialCond();
	//printSolutions(sat_form.allsat());
	try{
	     if (sat_form.and(init).satCount() > 0 )
		    return true;
	     else
	        return false;
	}
	catch(NullPointerException e){
		return false;
	}
}


/**
 * A private method that calls to sat visitor and returns the result of model checking
 * @param f		the formula to be checked
 * @param m		the model to be checked
 * @return		
 */
private static BDD Sat(FormulaElement f, BDDModel m){	
	
	SatVisitor s = new SatVisitor(m);
    f.accept(s);
	return s.getSat();
}


/**
 * Similar to Sat but using Early  quantification
 * @param f		the formula to be checked
 * @param p		the program to be verified
 * @return
 */
private static BDD SatEQ(FormulaElement f, Program p){	
	//System.out.println(f);
	SatVisitorEQ s = new SatVisitorEQ(p);
    f.accept(s);
	return s.getSat();
}

/**
 * 
 * @param f	the formula to be verified
 * @param p	the program to be verified
 * @return	the result of the model checking using disjoint early quantifications
 */
private static BDD SatDEQ(FormulaElement f, Program p){	
	
	SatVisitorDisjointEQ s = new SatVisitorDisjointEQ(p);
    f.accept(s);
	return s.getSat();
}


/**
 * 
 * @param f
 * @param p
 * @param goals
 * @return
 */
private static BDD fairSat(FormulaElement f, Program p, LinkedList<BDD> goals){
	FairSatVisitor s = new FairSatVisitor(p, goals);
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
