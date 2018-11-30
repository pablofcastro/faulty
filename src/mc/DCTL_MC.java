package mc;

import java.util.*;
import net.sf.javabdd.*;
import formula.*;
import faulty.*;


public class DCTL_MC{

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

	public static String getWitness(FormulaElement form, Program p){
		//FormulaElement negatedForm = new Negation("!",form);
		//BDD sat_form = SatEQ(negatedForm, p);
		SatVisitorEQ s = new SatVisitorEQ(p);
		form.accept(s);
		BDD sat_form = s.getSat();
		BDD init = p.getInitialCond();
		try{
			if (sat_form.and(init).satCount() > 0 ){ // then witness exists
				//return p.getRuns(sat_form, k);
				LinkedList<BDD> witness = new LinkedList<BDD>();
				boolean witnessFound = s.generateWitness(form, init, witness);
				return p.getRuns(witness);
			}
			else{ // else witness does not exist
				return "";
			}
		}
		catch(NullPointerException e){
			return "";
		}
		
	}
	
	/**
	 * This method returns a description of a run of a given process that witness a properties
	 * @param form		the formula to be model checked
	 * @param p			the program
	 * @param process	a given process
	 * @return			a witness for the given process that satisfies the property, it only shows the states of the process (enums)
	 */
	public static LinkedList<String> getWitnessForProcess(FormulaElement form, Program p, String process){
		SatVisitorEQ s = new SatVisitorEQ(p);
		form.accept(s);
		BDD sat_form = s.getSat();
		BDD init = p.getInitialCond();
		try{	
			if (sat_form.and(init).satCount() > 0 ){ // then witness exists
				LinkedList<BDD> witness = new LinkedList<BDD>();
				boolean witnessFound = s.generateWitness(form, init, witness);
				return p.getRunForProcess(witness, process);
			}
			else{ // else witness does not exist
				return new LinkedList<String>();
			}
		}
		catch(NullPointerException e){
			System.out.println("Error while generating counterexamples...");
			return new LinkedList<String>();
		}
	}
	
	public static LinkedList<HashMap<String,String>> getWitnessAsMaps(FormulaElement form, Program p, String process){
		SatVisitorEQ s = new SatVisitorEQ(p);
		form.accept(s);
		BDD sat_form = s.getSat();
		BDD init = p.getInitialCond();
		try{	
			if (sat_form.and(init).satCount() > 0 ){ // then witness exists
				LinkedList<BDD> witness = new LinkedList<BDD>();
				boolean witnessFound = s.generateWitness(form, init, witness);
				return p.getRunsAsMaps(witness);
			}
			else{ // else witness does not exist
				return new LinkedList<HashMap<String, String>>();
			}
		}
		catch(NullPointerException e){
			System.out.println("Error while generating counterexamples...");
			e.printStackTrace();
			return new LinkedList<HashMap<String, String>>();
		}
	}
	
	public static LinkedList<HashMap<String,String>> getBoundedWitnessAsMaps(FormulaElement form, Program p, String process, int n){
		SatVisitorEQ s = new SatVisitorEQ(p);
		form.accept(s);
		BDD sat_form = s.getSat();
		BDD init = p.getInitialCond();
		try{	
			if (sat_form.and(init).satCount() > 0 ){ // then witness exists
				LinkedList<BDD> witness = new LinkedList<BDD>();
				boolean witnessFound = s.generateBoundedWitness(form, init, witness, n);
				return p.getRunsAsMaps(witness);
			}
			else{ // else witness of size n does not exist
				return new LinkedList<HashMap<String, String>>();
			}
		}
		catch(NullPointerException e){
			System.out.println("Error while generating counterexamples...");
			e.printStackTrace();
			return new LinkedList<HashMap<String, String>>();
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
	 * 
	 * @param f	an input CTL formula
	 * @return	the formula is translated to NNF, it is used for the model checking algorithm and the counterexample search
	 */
	public static FormulaElement toNNF(FormulaElement f){
		if ((f instanceof Constant) || (f instanceof Var)) // f constant or var
			return f;
		if (f instanceof Negation){ // negation case
			FormulaElement subForm = ((Negation) f).getExpr1();
			if ((subForm instanceof Constant) || (subForm instanceof Var)) // f negation of constant and var
				return f;
			if (subForm instanceof Negation) // f = !!g
				return ((Negation) subForm).getExpr1();
			if (subForm instanceof Conjunction){
				FormulaElement conj1 = ((Conjunction) subForm).getExpr1();
				FormulaElement conj2 = ((Conjunction) subForm).getExpr2();
				Disjunction d = new Disjunction("|", toNNF(new Negation("!",conj1)), toNNF(new Negation("!", conj2)));
				return d;
			}
			if (subForm instanceof Disjunction){
				FormulaElement disj1 = ((Disjunction) subForm).getExpr1();
				FormulaElement disj2 = ((Disjunction) subForm).getExpr2();
				Conjunction c = new Conjunction("|", toNNF(new Negation("!",disj1)), toNNF(new Negation("!", disj2)));
				return c;
			}	
			if (subForm instanceof EX){ // f = !EX g
				FormulaElement g = ((EX) subForm).getExpr1();
				return new AX("AX", toNNF(new Negation("!", g)));
			}
			if (subForm instanceof AX){ // f = !EX g
				FormulaElement g = ((AX) subForm).getExpr1();
				return new EX("EX", toNNF(new Negation("!", g)));
			}
			if (subForm instanceof AU){ // !A(p U q)
				FormulaElement p = ((AU) subForm).getExpr1();
				FormulaElement q = ((AU) subForm).getExpr2();
				return new EW("EW", toNNF(new Negation("!",q)), toNNF(new Conjunction("&",new Negation("!",p), new Negation("!",q))));
			}
			if (subForm instanceof AW){ // !A(p U q)
				FormulaElement p = ((AW) subForm).getExpr1();
				FormulaElement q = ((AW) subForm).getExpr2();
				return new EU("EU", toNNF(new Negation("!",q)), toNNF(new Conjunction("&",new Negation("!",p), new Negation("!",q))));
			}
			if (subForm instanceof EU){ // !A(p U q)
				FormulaElement p = ((EU) subForm).getExpr1();
				FormulaElement q = ((EU) subForm).getExpr2();
				return new AW("AW", toNNF(new Negation("!",q)), toNNF(new Conjunction("&",new Negation("!",p), new Negation("!",q))));
			}
			if (subForm instanceof EW){ // !A(p U q)
				FormulaElement p = ((EW) subForm).getExpr1();
				FormulaElement q = ((EW) subForm).getExpr2();
				return new AU("AU", toNNF(new Negation("!",q)), toNNF(new Conjunction("&",new Negation("!",p), new Negation("!",q))));
			}
		}
		if (f instanceof Conjunction){ // f = p & q
			FormulaElement p =  ((Conjunction) f).getExpr1();
			FormulaElement q =  ((Conjunction) f).getExpr2();
			return new Conjunction("&", toNNF(p), toNNF(q));
		}
		if (f instanceof Disjunction){ // f = p & q
			FormulaElement p =  ((Disjunction) f).getExpr1();
			FormulaElement q =  ((Disjunction) f).getExpr2();
			return new Disjunction("&", toNNF(p), toNNF(q));
		}
		return f; // if we have deontic formulas then we just returnn the same formula
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
