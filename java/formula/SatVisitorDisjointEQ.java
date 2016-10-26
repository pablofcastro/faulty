package formula;
import java.util.Iterator;
import java.util.*;

import net.sf.javabdd.*;
import mc.BDDModel;
import faulty.*;
/**
 *  This class provides a SAT visitor for the model checking algorithm of
 *  dCTL, no fairness is taken into account, for that the class
 *  FairSatVisitor must be used. Each formula has its corresponding
 *  method (visit), which calculates the sat. A collection of private methods
 *  are used for calculation of the basic temporal propositions.
 *  
 *  In contrast to SatVisitor.java this class uses early quantification over disjoint structures,
 *  quantifiers are pushed inside pf each process
 *  that is the BDD of the program is not build before the model checking process,
 *  but the individual processes are used, which in theory is better.
 *  
 *  NOTE: In contrast to SatVisitorEQ, the quantifiers are pushed inside the process, and 
 *  the ands, using the techniques described in ""
 *   
 * 	@author Ceci and Pablo
 *
 */
public class SatVisitorDisjointEQ implements FormulaVisitor{

	private BDD sat;	// used for storing the result of the sat
	private BDDModel model; // the model of the complete program, it is computed only when needed
	private BDD norm; // A formula representing the normative part of the system
					 // this can be calculated without generating the BDD for all the system
	private LinkedList<BDDModel> models; // the list of model off all the processes, the idea is to avoid generating
										 // the complete model as far as possible.
	private Program program;


	/**
	 * Basic constructor
	 * @param	m	the model
	 */
	public SatVisitorDisjointEQ(Program program){
		this.sat = null;
		this.model = program.getModel();
		models = new LinkedList<BDDModel>();
		this.program = program;
		
		
		
		// we get the BDDs of the processes in the program
		//models = program.buildPartialModels();
		BDD n = Program.myFactory.one();
		for (int i=0; i < program.getProcesses().size(); i++){
			faulty.Process current = program.getProcesses().get(i);
			// we calculate the normative condition
			n = n.and(current.getNorm().getBDD());
		}
		norm = n;
		
	}

	/**
	 * Evaluates a logical variable
	 * @param 	v		the variable to be evaluated
	 */
	public void visit(Variable v) {
		// TO BE FIXED: The idea of BDDModels should be redesigned
		
		String name = v.toString();
		int id = model.getVarID(name);
		//BDD result = Program.myFactory.zero();
		//for(int i = 0; i < models.size(); i++){
		//	int id = models.get(i).getVarID(name);
		//	result = result.or(models.get(i).getFactory().ithVar(id)); //gets the state where the var is true
		//}
		sat = Program.myFactory.ithVar(id);

	}

	/** 
	 * Returns the BDD zero or one, depending if the constant value is True or false, respectively.
	 * @param		c		the constant
	 * */

	public void visit(Constant c) {
		sat = c.getValue() ? Program.myFactory.one() : Program.myFactory.zero();

	}
	
	/**
	 * Calculates the negation of a formula
	 * @param n		the negation
	 */
	@Override
	public void visit(Negation n) {
		n.getExpr1().accept(this);		
		sat = this.getSat().not();
	}

	/**
	 * Calculates an implication
	 * @param i		the implication
	 */
	@Override
	public void visit(Implication i) {
		//BDD mod = model.getTransitions();
		i.getExpr1().accept(this);
		BDD sat_np = this.getSat().not();
		i.getExpr2().accept(this);
		sat = sat_np.or(this.getSat());
	}
    
    /**
	 *
	 * @param c		the comparison ==
	 */
	@Override
	public void visit(EqComparison e) {
		
	}


	/**
	 * Calculates the sat of a conjunction
	 * @param c		the conjunction
	 */
	@Override
	public void visit(Conjunction c) {
		c.getExpr1().accept(this);
		BDD sat_p = this.getSat();
		c.getExpr2().accept(this);
		sat = sat_p.and(this.getSat());
	
	}

	/**
	 * Calculates the sat of a disjunction
	 * @param d		the disjunction
	 */
	@Override
	public void visit(Disjunction d) {
		d.getExpr1().accept(this);
		BDD sat_p = this.getSat();
		d.getExpr2().accept(this);
		sat = sat_p.or(this.getSat());	
	}
	
	/**
	 * DEPRECATED
	 * @param n
	 */
	public void visit(Next n){
		
	}
	
	/**
	 * DEPRECATED
	 * @param u
	 */
	public void visit(Until u){
		
	}
    
    /**
	 * DEPRECATED
	 * @param u
	 */
	public void visit(Weak u){
		
	}


	@Override
	/**
	 * Implements the sat for formula O(Xp -> Xq)
	 * @param 	o	the formula OXX
	 */
	public void visit(OXX o) {
		// We calculate O(Xp->Xq)
        // SAT(O(Xp->Xq)) = ! E(n U n ^ EX(p ^ !q ^ E(n W false))))
        
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;//.and(mod);
        BDD nq = q.not();//.and(mod);
        // get the normative states
        BDD ns = norm;       
        // calculate the sat
        sat = EUntil(ns,ns.and(EX(p.and(nq).and(EX(EWuntil(ns, model.getFactory().zero())))))).not();//.and(mod);
	}


	/**
	 * Implements the sat for formula O(Xp->sUt)
	 * @param 	o	the formula	
	 */
	@Override
	public void visit(OXU o) {
		// We calculate O(Xp->sUt)
        // SAT(O(Xp->sUt)) = 
		// 			! E(n U (n ^ ((!t^!s^EXp V !t ^EX(p ^ E(!t ^ n W !s^!t ^ EGn)))))
        
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;//.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD nots = s.not();//.and(mod);
        BDD nott = s.not();//.and(mod);
        // get the normative states
        BDD ns = norm;
        sat = EUntil(ns, ns.and(nots.and(nott.and(EX(p.and(ns)))).or(nott.and(EX(p.and(EWuntil(nott.and(ns),nott.and(nots).and(EG(ns))))))))).not();//.and(mod);
	}

	/**
	 * Implements the sat for fomula O(pUq->Xs)
	 * @param 	o	the formula
	 */
	@Override
	public void visit(OUX o) {
		// We calculate O(pUq -> Xs)
        // the ctl semantics is O(pUq -> Xs) = !(E(n U n ^ (q ^EX!s) V (p ^EX(!s ^ E(p ^ n U q ^EG n))))
    
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;//.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD nots = s.not();//.and(mod);
        // get the normative states
        BDD ns = norm;
        sat = EUntil(ns, ns.and(q.and(EX(nots)).or(p.and(EX(nots.and(ns).and(EUntil(p.and(ns),q.and(EG(ns))))))))).not();//.and(mod);
        
	}

	/**
	 * Implements the sat for formula O(pUq -> sUt)
	 * @param 	o	the formula
	 */
	@Override
	public void visit(OUU o) {
		// We calculate O(pUq -> sUt)
        // SAT(O(pUq->sUt)) = ! E(n U \phi) where:
        // \phi = E(n ^ p ^ !t U n ^ q ^ (!s \/ E(!t ^ n W n ^ !s ^ EGn)))
		
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
		//TO DO: a function must be done to obtain the states of the model
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = o.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        BDD nt = t.not();//.and(mod);
        BDD ns = t.not();//.and(mod);
        BDD n =   norm;
        BDD phi = EUntil(n.and(p.and(nt)), n.and(q.and(ns.or(EWuntil(nt.and(n), n.and(ns.and(EG(n))))))));
        sat = EUntil(n, phi).not();//.and(mod);
        
	}
	
	/**
	 * Implements the sat for formula O(xp->sUt)
	 * @param 	o	the formula
	 */
	public void visit(OXW o){
		// We calculate O(Xp->sUt)
        // SAT(O(Xp->sWt)) = 
		// 			! E(n U (n ^ ((!t^!s^EXp V !t ^EX(p ^ E(!t ^ n U !s^!t ^ EGn)))))
        
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
		// start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;//.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD nots = s.not();//.and(mod);
        BDD nott = s.not();//.and(mod);
        // get the normative states
        BDD ns = norm;
        sat = EUntil(ns, ns.and(nots.and(nott.and(EX(p.and(ns)))).or(nott.and(EX(p.and(EUntil(nott.and(ns),nott.and(nots).and(EG(ns))))))))).not();//.and(mod);
	}
	
	public void visit(OWX o){
		// We calculate O(pUq -> Xs)
        // the ctl semantics is O(pUq -> Xs) = !(E(n U n ^ (q ^EX!s) V (p ^EX(!s ^ E(p ^ n W q ^EG n))))
    
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
		// start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;//.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD nots = s.not();//.and(mod);
        // get the normative states
        BDD ns = norm;
        sat = EUntil(ns, ns.and(q.and(EX(nots)).or(p.and(EX(nots.and(ns).and(EWuntil(p.and(ns),q.and(EG(ns))))))))).not();//.and(mod);
	}
	
	public void visit(OWU o){
		//Semantics: O(pWq -> sUq) =
		//                         !E(n U n ^ (q ^ phi1 v (p ^ (phi2 v phi3)) )
		// where:
		// phi1 = E(!t ^n W !s ^ !t ^ EGn)
		// phi2 = E(p^!t ^n W q ^!t ^E(!t ^n W !t ^!s ^EGn))
		// phi3 = E(p ^!t W !t ^!s ^E(p ^n W q ^EGn))
		
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = o.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        BDD nt = t.not();//.and(mod);
        BDD ns = t.not();//.and(mod);
        BDD n =   norm;
        
        BDD phi1 = EWuntil(nt.and(n), ns.and(nt).and(EG(n)));
        BDD phi2 = EWuntil(p.and(nt).and(n), q.and(nt).and(EWuntil(nt.and(n), nt.and(ns).and(EG(n)))));
        BDD phi3 = EWuntil(p.and(nt).and(n), nt.and(ns).and(EWuntil(p.and(n), q.and(EG(n)))));
        sat = EUntil(n, n.and(q.and(phi1)).or(p.and(phi2.or(phi3)))).not();//.and(mod);
        		
	}
	
	public void visit(OUW o){
		//Semantics: O(pUq -> sWt) =
		//                         !E(n U n ^ (q ^ phi1 v (p ^ (phi2 v phi3)) )
		// where:
		// phi1 = E(!t ^n U !s ^ !t ^ EGn)
		// phi2 = E(p^!t ^n U q ^!t ^E(!t ^n U !t ^!s ^EGn))
		// phi3 = E(p ^!t U !t ^!s ^E(p ^n U q ^EGn))
			
		//int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		//TO DO: a function must be done to obtain the states of the model
		
		FormulaElement expr1 = o.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = o.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		FormulaElement expr3 = o.getExpr3();
		expr3.accept(this);
		BDD s = this.sat;
		FormulaElement expr4 = o.getExpr4();
		expr4.accept(this);
		BDD t = this.sat;
		BDD nt = t.not();//.and(mod);
		BDD ns = t.not();//.and(mod);
		BDD n =   norm;
		       
		BDD phi1 = EUntil(nt.and(n), ns.and(nt).and(EG(n)));
		BDD phi2 = EUntil(p.and(nt).and(n), q.and(nt).and(EUntil(nt.and(n), nt.and(ns).and(EG(n)))));
		BDD phi3 = EUntil(p.and(nt).and(n), nt.and(ns).and(EUntil(p.and(n), q.and(EG(n)))));
		sat = EUntil(n, n.and(q.and(phi1)).or(p.and(phi2.or(phi3)))).not();//.and(mod);
		        		
	}
	
	public void visit(OWW o){
		//Semantics: O(pWq -> sWt) =
		//                         !E(n U n ^ (q ^ phi1 v (p ^ (phi2 v phi3)) )
		// where:
		// phi1 = E(!t ^n U !s ^ !t ^ EGn)
		// phi2 = E(p^!t ^n W q ^!t ^E(!t ^n U !t ^!s ^EGn))
		// phi3 = E(p ^!t U !t ^!s ^E(p ^n W q ^EGn))
					
		//int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		
		FormulaElement expr1 = o.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = o.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		FormulaElement expr3 = o.getExpr3();
		expr3.accept(this);
		BDD s = this.sat;
		FormulaElement expr4 = o.getExpr4();
		expr4.accept(this);
		BDD t = this.sat;
		BDD nt = t.not();//.and(mod);
		BDD ns = t.not();//.and(mod);
		BDD n =   norm;
				       
		BDD phi1 = EUntil(nt.and(n), ns.and(nt).and(EG(n)));
		BDD phi2 = EWuntil(p.and(nt).and(n), q.and(nt).and(EUntil(nt.and(n), nt.and(ns).and(EG(n)))));
		BDD phi3 = EUntil(p.and(nt).and(n), nt.and(ns).and(EWuntil(p.and(n), q.and(EG(n)))));
		sat = EUntil(n, n.and(q.and(phi1)).or(p.and(phi2.or(phi3)))).not();//.and(mod);
	}
	
	/** 
	 * Calculates formula O(pWq), it can be calculated with the function above, but by
	 * efficency reasons we include an implementation
	 * @param o		the formula
	 */
	public void visit(OW o){
		//Semantics  O(sUt) = !E(n U n ^ ((!s ^!t) v (!t ^ E(!t ^ n U n ^ !s ^EG(n) )))))
		
		//int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		
		
		
		
		// start obtaining the BDDs of the subexpressions	
		FormulaElement expr1 = o.getExpr1();
		expr1.accept(this);
		BDD s = this.sat;//.and(mod);
		FormulaElement expr2 = o.getExpr2();		
		expr2.accept(this);
		BDD t = this.sat;//.and(mod);
		// we calculate the result using its CTL semantics
		// the negations are calculated taking into account the model
		BDD nots = s.not();//.and(mod);
		BDD nott = t.not();//.and(mod);		
		// get the normative states
		BDD ns = norm;			
		sat = EUntil(ns, ns.and(nots).and(nott).or(nott.and(EUntil(nott.and(ns),nots.and(nott).and(EG(ns)))))).not();//.and(mod);		
	}
	
	/**
	 * 
	 * Calculates formula O(pUq), it can be calculated with the function above, but by
	 * efficency reasons we include an implementation
	 * @param o		the formula
	 */
	public void visit(OU o){

		// OLD Semantics  O(sUt) = !E(n U n ^ ((!s ^!t) v (!t ^ E(!t ^ n W n ^ !s ^EG(n) )))))
		//
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD s = this.sat;//.and(mod);    
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD t = this.sat;//.and(mod);
     
     // with fixed points
        BDD alln = EG(norm);
        // this is a simple optimization we can start X with EGn, since the fixpoint is a subset of this
		BDD X = alln;
		BDD X_old = Program.myFactory.zero();		
		//BDD alln = EG(norm);
		while (!X.biimp(X_old).isOne()){
			X_old = X;
				
			X = norm.and(s.not().and(t.not()).and(alln).or(t.not().and(EX(X))));
		}
		sat = X.not();
        
        // this is the old semantics, the translation to CTL, lees efficient
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        //BDD nots = s.not();//.and(mod);
        //BDD nott = t.not();//.and(mod);
        // get the normative states
        //BDD ns = norm;
        //sat = EUntil(ns, ns.and(nots).and(nott).or(nott.and(EWuntil(nott.and(ns), nots.and(nott).and(EG(ns)))))).not();
	}
	
	/**
	 * Calculates formula OXp, it can be calculated with the function above, but by
	 * efficency reasons we include an implementation
	 * @param o		the formula
	 */
	public void visit(OX o){
		//Semantics  O(Xp) = !EX(n ^ !p)
		
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		
        // dont know if we really need to do the intersection with the states of the model!
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        BDD notp = p.not();//.and(mod);
        BDD n = norm;
        sat = EX(n.and(notp)).not();//.and(mod);
	}
	
	@Override
	public void visit(PXX args) {
		// we calculate P(Xp -> Xq)
        // SAT(P(Xp -> Xq)) = vX . n ^ <>((!p \/ q) ^ X)
        // NOTE: Can this be expressed as a CTL formula???
       
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        // start obtaining the BDDs of the subexpressions
		
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;//.and(mod);
        BDD n =   norm;
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        BDD X = Program.myFactory.one();
        BDD X_old = Program.myFactory.zero();
        BDD notp = p.not();//.and(mod);
        // calculate the GFP
        while (!X.biimp(X_old).isOne()){
            X_old = X;
            X = EX(X.and(notp.or(q))).and(n);
        }
        // return the GFP
        sat = X;
	}

	@Override
	public void visit(PXU args) {
		// We calculate P(xp -> sUt)
		// Semantics: SAT(P(Xp -> s U t)) = vY.(uX.n^((t^<>Y)\/(s^<>(!p \/ X)))\/ (<>!p^Y))
		
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = null;
        BDD X = Program.myFactory.zero();
        BDD X_old = null;
        BDD result = null;
        BDD notp = p.not();//.and(mod);
       // BDD result =
        while (!Y.biimp(Y_old).isOne()){
            Y_old = Y;
            while (!X.biimp(X_old).isOne()){
                X_old = X;
                X = n.and(t.and(EX(Y)).or(s.and(EX((notp.or(X))))));
            }
            Y = X.or(EX(notp.and(Y))).and(n);
            
        }
        // we should test this!!!
        sat = X;
    }

	@Override
	public void visit(PUX args) {
		// We calculate P(s U t -> Xp)
        // Semantics: SAT(P(sUt -> Xp)) = vY . n^(v X . (!s^!t^Y \/ !t^<>X)) \/ (<>n^p^Y)
        
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
       
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD s = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD t = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD p = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = null;
        BDD X = Program.myFactory.zero();
        BDD X_old = null;
        BDD nots = s.not();//.and(mod);
        BDD nott = t.not();//.and(mod);
        
        while(!Y.biimp(Y_old).isOne()){
            Y_old = Y;
            while (!X.biimp(X_old).isOne()){
                X_old = X;
                X = nots.and(nott).and(Y).or(nott.and(EX(X)));
            }
            Y = n.and(X.or(EX(n.and(p).and(Y))));
        }
        sat = Y;
	}

	@Override
	public void visit(PUU args) {
        // TO DO: Modificar!
		// We calculate P(p U q -> s U t)
        // Semantics: SAT(P(p U q -> s U t) = vY . n ^ ((uX . (!q ^ !p ^ Y) \/ !p ^ <>(n ^ X) \/ vZ . (s ^ Y) \/ t ^ <>n ^  Z )
      
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = args.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        BDD X = Program.myFactory.zero();
        BDD X_old = Program.myFactory.one();
        BDD Z = Program.myFactory.one();
        BDD Z_old = Program.myFactory.zero();
        
        BDD notq = q.not();//.and(mod);
        BDD notp = p.not();//.and(mod);
        while (! Y.biimp(Y_old).isOne()){
        	while (! X.biimp(X_old).isOne()){
        		X = notq.and(notp).and(Y).or(notp.and(EX(X.and(n))));
        		
        	}
        	while (!Z.biimp(Z_old).isOne()){
    			Z = s.and(Y).or(t.and(EX(Z.and(n))));
    		}
        	Y = n.and(X).and(Z);
        }
        sat = Y;
        
	}
	
	public void visit(PXW args){
		// We calculate P(Xp -> sWt)
		// Semantics: SAT(P(Xp -> s W t)) = vY.(vX.n^((t^<>Y)\/(s^<>(!p \/ X)))\/ (<>!p^Y))
		
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        BDD X = Program.myFactory.one();
        BDD X_old = Program.myFactory.zero();
        BDD notp = p.not();//.and(mod);
       
        while (!Y.biimp(Y_old).isOne()){
            Y_old = Y;
            while (!X.biimp(X_old).isOne()){
                X_old = X;
                X = n.and(t.and(EX(Y)).or(s.and(EX(notp.or(X)))));
            }
            Y = X.or(EX(notp.and(Y))).and(n);
        }
        sat = Y;
        
	}
	
	public void visit(PWX args){
		// We calculate P(s U t -> Xp)
        // Semantics: SAT(P(sUt -> Xp)) = vY .n ^ (v X . (!s ^ !t ^ Y \/ !t ^ <>nX) \/ (<>n^p^Y))
        
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD s = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD t = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD p = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        BDD X = Program.myFactory.one();
        BDD X_old = Program.myFactory.zero();
        BDD nots = s.not();//.and(mod);
        BDD nott = t.not();//.and(mod);
        
        while(!Y.biimp(Y_old).isOne()){
            Y_old = Y;
            while (!X.biimp(X_old).isOne()){
                X_old = X;
                X = nots.and(nott).and(Y).or(nott.and(EX(X)));
            }
            Y = n.and(X.or(EX(n.and(p).and(Y))));
        }
        sat = Y;
	}
	
	public void visit(PWU args){
		// We calculate P(p W q -> s U t)
        // Semantics: SAT(P(p W q -> s U t) = 
		//			vY . n ^ ((uX . (!q ^ !p ^ Y) \/ !q ^ <>(n ^ X) \/ uZ . (t ^ Y) \/ s ^ <>n ^  Z )
      
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = args.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        
        // we calculate the fixed points
        BDD n =  norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        BDD X = Program.myFactory.zero();
        BDD X_old = Program.myFactory.one();
        BDD Z = Program.myFactory.zero();
        BDD Z_old = Program.myFactory.one();
        
        BDD notq = q.not();//.and(mod);
        BDD notp = p.not();//.and(mod);
        while (! Y.biimp(Y_old).isOne()){
        	while (! X.biimp(X_old).isOne()){
        		X = notq.and(notp).and(Y).or(notq.and(EX(X.and(n))));
        		
        	}
        	while (!Z.biimp(Z_old).isOne()){
    			Z = t.and(Y).or(s.and(EX(Z.and(n))));
    		}
        	Y = n.and(X).or(Z);
        }
        sat = Y;
	}
	
	public void visit(PUW args){
		// We calculate P(p U q -> s W t)
        // Semantics: SAT(P(p U q -> s W t) = 
		//			vY . n ^ ((vX . (!q ^ !p ^ Y) \/ !q ^ <>(n ^ X) \/ uZ . (t ^ Y) \/ s ^ <>n ^  Z )
      
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = args.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm;
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        BDD X = Program.myFactory.one();
        BDD X_old = Program.myFactory.zero();
        BDD Z = Program.myFactory.one();
        BDD Z_old = Program.myFactory.zero();
        
        BDD notq = q.not();//.and(mod);
        BDD notp = p.not();//.and(mod);
        while (! Y.biimp(Y_old).isOne()){
        	while (! X.biimp(X_old).isOne()){
        		X = notq.and(notp).and(Y).or(notq.and(EX(X.and(n))));
        		
        	}
        	while (!Z.biimp(Z_old).isOne()){
    			Z = t.and(Y).or(s.and(EX(Z.and(n))));
    		}
        	Y = n.and(X).or(Z);
        }
        sat = Y;
	}
	
	public void visit(PWW args){
		// We calculate P(p W q -> s W t)
        // Semantics: SAT(P(p W q -> s W t) = 
		//			vY . n ^ ((uX . (!q ^ !p ^ Y) \/ !q ^ <>(n ^ X) \/ nZ . (t ^ Y) \/ s ^ <>n ^  Z )
      
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = args.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = args.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        
        // we calculate the fixed points
        BDD n =   norm; // just to reduce the size of formulas
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        BDD X = Program.myFactory.zero();
        BDD X_old = Program.myFactory.one();
        BDD Z = Program.myFactory.one();
        BDD Z_old = Program.myFactory.zero();
        
        BDD notq = q.not();//.and(mod);
        BDD notp = p.not();//.and(mod);
        while (! Y.biimp(Y_old).isOne()){
        	while (! X.biimp(X_old).isOne()){
        		X = notq.and(notp).and(Y).or(notq.and(EX(X.and(n))));
        		
        	}
        	while (!Z.biimp(Z_old).isOne()){
    			Z = t.and(Y).or(s.and(EX(Z.and(n))));
    		}
        	Y = n.and(X).or(Z);
        }
        sat = Y;
		
	}
	
	/**
	 * Calculates the sat for PXp
	 * @param args	the formula
	 */
	public void visit(PX args){
		// Semantics PXp = EX(n ^ p ^ EGn)
		
		 //int varNum = model.getFactory().varNum();
	     //BDD mod = model.getTransitions();
	     //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
	        
	     //TO DO: a function must be done to obtain the states of the model
	     FormulaElement expr1 = args.getExpr1();
	     expr1.accept(this);
	     BDD p = this.sat;
	     //BDD n =   model.getNormative();
	     sat = EX(norm.and(p).and(EG(norm)));  
	}
	
	/**
	 * Calculates the sat for P(sUt)
	 * @param p
	 */
	public void visit(PU p){
		// Semantics P(sUt) =  vX . n^(uY.(t^X) v s ^ <> n  ^ Y)
		
		//int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
			        
		// we get the parameters
		FormulaElement expr1 = p.getExpr1();
		expr1.accept(this);
		BDD s = this.sat;
		FormulaElement expr2 = p.getExpr2();
		expr2.accept(this);
		BDD t = this.sat;
		//BDD n =   model.getNormative();
		
		BDD X =Program.myFactory.one();
		//BDD X = model.getFactory().zero();
		BDD X_old = Program.myFactory.zero();
		while (!X.biimp(X_old).isOne()){
			X_old = X;
			BDD Y = Program.myFactory.zero();
			BDD Y_old = Program.myFactory.one();
			while (!Y.biimp(Y_old).isOne()){
				Y_old = Y;
				//Y = t.and(X).or(s.and(NWeakPrevious(n.and(Y))));
				Y = t.and(X).or(s.and(EX(Y).and(norm)));
			}
			X = norm.and(Y);
		}
		sat = X;
		
		// NOTE: check if pushing inside the and the algorithms is faster
	}
	
	/**
	 * Calculates sat for P(sWt)
	 * @param p
	 */
	public void visit(PW p){
		// Semantics P(sWt) =  vX . n^(uY.(t^X) v s ^ <> n  ^ Y)
		
		//int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
					        
		// we get the parameters
		FormulaElement expr1 = p.getExpr1();
		expr1.accept(this);
		BDD s = this.sat;
		FormulaElement expr2 = p.getExpr2();
		expr2.accept(this);
		BDD t = this.sat;
		//BDD n =   model.getNormative();
				
		BDD X = Program.myFactory.one();
		BDD X_old = Program.myFactory.zero();
		while (!X.biimp(X_old).isOne()){
			X_old = X;
			BDD Y = model.getFactory().one();
			BDD Y_old = model.getFactory().zero();
			while (!Y.biimp(Y_old).isOne()){
				Y_old = Y;
				Y = norm.and(t).and(X).or(norm.and(s.and(EX(norm.and(Y))))); //FIX ME
				
			}
			//X = norm.and(Y);
			X = Y;
		}
		sat = X;
	}
	
	public void visit(RX r){
		// TBD
	}
	
	public void visit(RU r){
		// TBD
	}
	
	public void visit(RW r){
		//TBD
	}
    
    @Override
	public void visit(RWW e) {
    }
	
    @Override
	public void visit(RWU e) {
    }
    
	@Override
	public void visit(RWX e) {
    }
    
    @Override
	public void visit(RUW e) {
    }
    
    @Override
	public void visit(RXW e) {
    }
    
    @Override
	public void visit(RUU e) {
    }
    
    @Override
	public void visit(RXX e) {
    }
    
    @Override
	public void visit(RXU e) {
    }
	
    @Override
	public void visit(RUX e) {
    }
	
	public void visit(AX a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		sat = AX(p);
	}
	
	/**
	 * Computes the formula A(pUq)
	 * @param a		the formula
	 */
	public void visit(AU a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = a.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = AUntil(p, q);
	}
	
	/**
	 * Computes the formula A(pWq)
	 * @param a		the formula
	 */
	public void visit(AW a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = a.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = AWuntil(p, q);
	}
	
	/**
	 * Computes the formula EXp
	 * @param e		the formula
	 */
	public void visit(EX e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		sat = EX(p);
	}
	
	/**
	 * Computes the formula E(pUq)
	 * @param e		the formula
	 */
	public void visit(EU e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = e.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = EUntil(p, q);
	}
	
	/**
	 * Computes the formula E(pWq)
	 * @param e		the formula
	 */
	public void visit(EW e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = e.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = EWuntil(p, q);
	}
	
	@Override
	public void visit(AXX a) {
        //Sat(A(Xp ~> Xq) = StrongPrevious(p->q) = ! (EX(p&!q))
		
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = a.getExpr2();
		BDD q = this.sat;
		
		BDD result = EX(p.and(q.not())).not();
				
        
       	//calculates Sat (p->q)
		//BDD sat_imp = satImplies(a.getExpr1(),a.getExpr2());
        
        //int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		//mod = Post(mod).and(sat_imp);	//calculates all reachable states that satisfies the implication.
        
        //sat = StrongPrevious(mod); // calculate the strong previous of the states that satisfies the implication
	}

	@Override
	public void visit(AXU a) {
        // we calculate A(Xp->sUt)
        // A(Xp->sUt) = ! E(Xp ^ !(sUt)) =! ((!s ^ EXp) \/ (!t ^ EX(p ^ E(!tU!s))) \/ (!t ^ EX(p ^ EG!t))  )
       
		// Get the states of the model in mod
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = a.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really need to do the intersection with the states of the model!
        
        FormulaElement expr2 = a.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;//.and(mod);
        FormulaElement expr3 = a.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD ns = s.not();//.and(mod);
        BDD nt = t.not();//.and(mod);
        sat = (ns.and(EX(p)).or(nt.and(EUntil(nt,ns)))).or(nt.and(EX(p.and(EWuntil(nt,Program.myFactory.zero()))))).not();
    }

	@Override
	public void visit(AUX a) {
        // we calculate A(sUt->Xp)
        // A(sUt->Xp) = ! E((sUt) ^ X!p) = !( t^EX(!p) \/ (p ^ EX(!p ^ E(sUt))))
        
        // Get the states of the model in mod
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = a.getExpr1();
        expr1.accept(this);
        BDD s = this.sat;//.and(mod);
        
        FormulaElement expr2 = a.getExpr2();
        expr2.accept(this);
        BDD t = this.sat;//.and(mod);
        FormulaElement expr3 = a.getExpr3();
        expr3.accept(this);       
        BDD p = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD np = p.not();//.and(mod);
       
        sat = (t.and(EX(np)).or(p.and(EX(np.and(EUntil(s,t)))))).not();
    }
    

	@Override
	public void visit(AUU a) {
        // we calculate A(pUq -> sUt)
        // the semantics is: A(pUq->sUt) = E(p ^ !t U (q ^!t ^ EG!t)) \/ E(p ^ !t U (q ^ E(!t U !s))) \/ E(p ^ !t U !s ^ E(pUq))
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
		//TO DO: a function must be done to obtain the states of the model
        FormulaElement expr1 = a.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = a.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = a.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = a.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        BDD nt = t.not();//.and(mod);
        BDD ns = t.not();//.and(mod);
        sat= (EUntil(p.and(nt),q.and(nt).and(EWuntil(nt,Program.myFactory.zero()))).or(EUntil(p.and(nt), q.and(EUntil(nt,ns)))).or(EUntil(p.and(nt), ns.and(EUntil(p,q))))).not();//.and(mod);
  
	}

	@Override 
	public void visit(EXX e) {
		// Sat( E(Xp ~> Xq) ) = {s in S/ Post(s) intersection Sat(!p or q) != Empty} = !A(Xp&!q)
		// calculates Sat (p->q)
		
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = e.getExpr2();
		BDD q = this.sat;
		
		sat = AX(p.and(q).not()).not();
		
		
		//BDD sat_imp = satImplies(e.getExpr1(),e.getExpr2());
		
		//int varNum = model.getFactory().varNum();
		//BDD mod = model.getTransitions();		
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
    	//mod = Post(mod).and(sat_imp);	//calculates all reachable states that satisfies the implication.
		
    	//sat = WeakPrevious(mod); // calculate the weak previous of the states that satisfies the implication
	}

	@Override
	public void visit(EXU e) {
		// We calculate E(Xp -> sUt)
        // the CTL semantics is E(Xp->sUt) = EX(!p) \/ E(sUt)
        
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
        
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;//.and(mod);
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;//.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD np = p.not();//;.and(mod);
        sat = EX(np).or(EUntil(s,t));

	}

	@Override
	public void visit(EUX e) {
		// We calculate E(sUt -> Xp)
        // the CTL semantics is E(sUt->Xp) = E(!tU!s) \/ EG(s) \/ EXp
       
		//int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
        //mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
		// start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD s = this.sat;//.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD t = this.sat;//.and(mod);
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD p = this.sat;//.and(mod);
        BDD nt = t.not();//.and(mod);
        BDD ns = s.not();//.and(mod);
        
        sat = EUntil(nt,ns).or(EWuntil(s,Program.myFactory.zero())).or(EX(p));

	}

	@Override
	public void visit(EUU e) {
		// We calculate E(pUq -> sUt)
        // the semantics is E(pUq -> sUt) = E(!qU!p) \/ EG(!q) \/ E(sUt)
    //    int varNum = model.getFactory().varNum();
    //    BDD mod = model.getTransitions();
    //    mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
        FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        FormulaElement expr4 = e.getExpr4();
        expr4.accept(this);
        BDD t = this.sat;
        //BDD np = p.not().and(mod);
        //BDD nq = q.not().and(mod);
        
        sat = EUntil(q.not(),p.not()).or(EWuntil(q.not(),Program.myFactory.zero())).or(EUntil(s,t));

	}

	/** 
	 * Returns the BDD that the represents the set of states of the model that satisfy the current formula.
	 * */
	public BDD getSat(){
		return sat;		
	}

    /***
     * @return returns the set of states
     * @param p: the first formula of A(pUq)
     * @param q: the second formula of A(pUq)
     */
    private BDD AUntil(BDD p, BDD q){
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //q = q.and(mod); // get those states that hold q
        BDD result = q;
        BDD result_old = model.getFactory().zero();
        // this loop calculates the least fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            result = AX(result).and(p).or(q);
        }
        return result;
    }
    
    
    /***
     * @return returns the set of states satisfying A(pWq)
     * @param p: the first formula of A(pWq)
     * @param q: the second formula of A(pWq)
     */
    public BDD AWuntil(BDD p, BDD q){
        //int varNum = model.getFactory().varNum();
        //BDD mod = model.getTransitions();
		//mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //q = q.and(mod); // get those states that hold q
        //p = p.and(mod); // get those states that hold p
        BDD result = Program.myFactory.one();
        BDD result_old = Program.myFactory.zero();
        // this loop calculates the greatest fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            
            result = AX(result).and(p).or(q);
        }
        return result;
    }

    
    /***
     * @return returns the set of states
     * @param p: the first formula of AGp
     */
    public BDD AG(BDD p){
    //    int varNum = model.getFactory().varNum();
    //    BDD mod = model.getTransitions();
	//	mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
    //    p = p.and(mod); // get those states that hold p
        return AWuntil(p, Program.myFactory.zero());
    }
    

    
    /***
     * @return returns the set of states
     * @param p: the first formula of E(pWq)
     * @param q: the second formula of E(pWq)
     */
    public BDD EWuntil(BDD p, BDD q){
     //   int varNum = model.getFactory().varNum();
    //    BDD mod = model.getTransitions();
	//	mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
    //    q = q.and(mod); // get those states that hold q
    //    p = p.and(mod); // get those states that hold p
    	
        BDD result = Program.myFactory.one();
        BDD result_old = Program.myFactory.zero();
        // this loop calculates the greatest fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result; 
            result = EX(result).and(p).or(q);
        }
        return result;
    }

    
    /***
     * @return returns the set of states
     * @param p: the first formula of EGp
     */
    public BDD EG(BDD p){
      //  int varNum = model.getFactory().varNum();
      //  BDD mod = model.getTransitions();
	//	mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
     //   p = p.and(mod); // get those states that hold p   
     //   return EWuntil(p, Program.myFactory.zero()); old version calculting the greatest fix point is better
    	 BDD result = Program.myFactory.one();
         BDD result_old = Program.myFactory.zero();
         // this loop calculates the greatest fixed point
         while (!result.biimp(result_old).isOne()){
             result_old = result; 
             result = EX(result).and(p);
         }
         return result;
    }
    
    
    /***
     * @return returns the set of states
     * @param p: the first formula of E(pUq)
     * @param q: the second formula of E(pUq)
     */
    private BDD EUntil(BDD p, BDD q){
     //   int varNum = model.getFactory().varNum();
     //   BDD mod = model.getTransitions();
	//	mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
    //    q = q.and(mod); // get those states that hold q
    	
    	
    	
    	
        BDD result = Program.myFactory.zero();
        BDD result_old = Program.myFactory.one();
        // this loop calculates the least fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            result = (EX(result).and(p)).or(q);
        }
        return result;
    }
    
     /***
     * @return returns the set of states that hold EXp
     * @param p: the first formula of EXp
     */
    private BDD EX(BDD p){
    	// for calculating the EX we use early quantification that is
    	// we calculate EX for each process and then we calculate the big or
    	
    	BDD result = Program.myFactory.zero();
    	int varNum = Program.myFactory.varNum();
    	
		boolean r_primed = false;  //flag to indicate that we changed all variables v by v'
		BDDPairing pairs = makePairsToReplace(r_primed);         		
        BDD target = p.replaceWith(pairs); // we change in b all variables v by v'   
       
        BDD model = program.preImageBDD(target);
        // we calculate b.and(model) this returns the preimage of model with b
        //return model;
        //return addExists(varNum/2, varNum, model, Program.myFactory);
        return model;
  //  	return result;       
    }

    
 //   private BDD oldEX(BDD p){
 //   	BDD result = Program.myFactory.zero();
 //    	for (int i=0;  i < models.size(); i++){
 //   		
 //   		int varNum = Program.myFactory.varNum();
 //   		BDD mod = models.get(i).getTransitions();
 //   		mod = addExists(varNum/2, varNum, mod, Program.myFactory); // obtain all states of the model.
 //   		result = result.or(WeakPrevious(p.and(mod), models.get(i)));    		
 //   	}
 //   	
 //   	return result; 
 //   }
    
    
    /***
     * @return returns the set of states that hold AXp using early quantification
     * @param p: the first formula of AXp
     */
    private BDD AX(BDD p){
    	/**
    	 * OLD VERSION WITHOUT EARLY QUANTIFICATION
    	 * int varNum = model.getFactory().varNum();
    	 * BDD mod = model.getTransitions();
    	 * mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
    	 * p = p.and(mod); // get those states that hold p
    	 * BDD result = StrongPrevious(p); // the result is the strong previous
    	 * return result;
    	 */
    	 BDD result = EX(p.not()).not();
    	 return result;
    }
    
    
    
	/***
	 * @param model		a BDD representing the Kripke structure		
	 * @param s			a 	BDD representing the state
	 * @return returns the set of successors states, reached in a step from the state "s", i.e., Post(s).
	 */
	private BDD Post(BDD s, BDD model){
		// we obtain the set of successors of s
		BDD rest = model.and(s);
		
		// we eliminate the non-primed variables
		rest = addExists(0, Program.myFactory.varNum()/2, rest, Program.myFactory); 

		// we change v' by v, primed by unprimed
		boolean r_primed = true;  //flag to indicate that it will change all variables v' by v
		BDDPairing pairs = makePairsToReplace(r_primed);
		rest = rest.replaceWith(pairs);//rename each variable: v' -> v
		
		// that is the result
		return 	rest;	
	}


	/***
	 * 
	 * @return returns the set of successors states("normal"), reached in a step from the state b.
	 */
	private BDD Post_N(BDD b, BDD model, BDD norm){

		//BDD trans = model.getTransitions();
		// calculates the set of successors states, reached in a step from the state "b"
		BDD post = Post(b, model);
		return post.and(norm);		
	}
	
	
	/***
	 * @param
	 * @return returns the set of previous states of the states "b".
	 */
	private BDD WeakPrevious(BDD b, BDD model){
		int varNum = Program.myFactory.varNum();  
		boolean r_primed = true;  //flag to indicate that we changed all variables v by v'
		BDDPairing pairs = makePairsToReplace(r_primed);         
        BDD target = b.replaceWith(pairs); // we change in b all variables v by v'
        // we calculate b.and(model) this returns the preimage of model with b
        return addExists(varNum/2, varNum, b.and(model), Program.myFactory);
		
		
    //    int varNum = Program.myFactory.varNum();        
    //      //BDD mod = model;//.getTransitions();
    //		BDD mod = addExists(varNum/2, varNum, model, Program.myFactory); // obtain all states of the model	
	//	
	//	
	//	mod = Post(mod, model).and(b);	//calculates those  states that are succesors of someone and hold b .
	//	
    //	boolean r_primed = false;  //flag to indicate that we changed all variables v by v'
	//	BDDPairing pairs = makePairsToReplace(r_primed);         
    //    
    //    //rename each variable: v' -> v
	//    mod = mod.replaceWith(pairs);	       
	//    mod = model.and(mod);
   //     return addExists(varNum/2, varNum, mod, Program.myFactory);

	}
	
	/***
	 * 
	 * @return returns the set of previous states of the states "b", in which all successors are
	 * included in the states given as parameter.-
	 */
	private BDD StrongPrevious(BDD b, BDD model){		
		//calculates the weak previous of not b
		BDD npre = WeakPrevious(b.not(), model); 
		// calculates the weak previous of b
        BDD pre = WeakPrevious(b, model); 
     // the result is given by those sets in pre and not in npre
        return (pre.and(npre.not()));         
	}

    /***
	 *
	 * @return returns the set of normative previous states of the states "b".
	 */
	private BDD NWeakPrevious(BDD b, BDD model, BDD norm){
        //BDD norm = model.getNormative();
        int varNum = Program.myFactory.varNum();
		//BDD mod = model.getTransitions();
		BDD mod = addExists(varNum/2, varNum, model, Program.myFactory); // obtain all states of the model.
		mod = Post(mod, model).and(b);	//calculates those  states that are succesors of someone and hold b .

		boolean r_primed = false;  //flag to indicates that will be changes all variables v by v'
		BDDPairing pairs = makePairsToReplace(r_primed);
		
        /*BDDPairing pairs = model.getFactory().makePair();
        for (int i=0; i<varNum/2; i++)
            pairs.set(i,(varNum/2)+i);            
        */
		
        //rename each variable: v' -> v
	    mod = mod.replaceWith(pairs);
	          
	    //mod = model.getTransitions().and(mod).and(norm);
        return addExists(varNum/2, varNum, norm,Program.myFactory);
	}
	
	
	/***
	 *
	 * @return returns the set of normative previous states of the states "b", in which all successors are
	 * included in the states given as parameter.-
	 */
	private BDD NStrongPrevious(BDD b, BDD model, BDD norm){
        BDD npre = NWeakPrevious(b.not(), model, norm); //calculates the weak previous of not b
        BDD pre = NWeakPrevious(b, model, norm); // calculates the weak previous of b
        return (pre.and(npre.not())); // the result is given by those sets in pre and not in npre
    }

    

	/***
	 * 
	 * @param f
	 * @return Compute the set Sat( P (X f) )
	 */
	private void SatPX(FormulaElement f, BDD model, BDD norm){
		// Sat( P(X f) ) = {s in Normatives / Post_N (s) intersection Sat(f) != Empty}
		
		f.accept(this);
		BDD sat_f = this.getSat();				
		sat = (Post_N(norm, model, norm).and(sat_f));
	}


    /**
     * 
     * @param r_primed  Boolean flag to indicate it will replace all primed variables
     *  (in which case r_primed = true) or all common variables (r_primed =false).-
     * @return creates pairs to replace primed (v' -> v) or common variables (v-> v')
     */
	private BDDPairing makePairsToReplace(boolean r_primed){      
		//obtain the identifier of each variable of the BDD.
		int varNum = Program.myFactory.varNum() / 2;
		BDDPairing pairs = Program.myFactory.makePair();		

		if(r_primed){// creates pairs to changes variables v' by v , i.e. v -> v'		
			for (int i=0; i<varNum; i++){
				pairs.set((varNum)+i, i);		
			}   
		}
		else{// creates pairs to changes variables v by v',  i.e. v' -> v		
			for (int i=0; i<varNum; i++){
				pairs.set(i,(varNum)+i);		
			}   
		} 
		return pairs;
	}	
	
	
	/**
	 * Prints the set of truth assignments specified by this list.
	 * @param sol
	 */
	private void  printSolutions ( List sol){

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


	/**
	 * 
	 * @param from
	 * @param until
	 * @param b
	 * @param f
	 * @return 
	 */
	private BDD addExists(int from, int until, BDD b, BDDFactory f){
		
		BDD var;

		for(int k=from; k< until; k++){			 	
			var = f.ithVar(k);
			b = b.exist(var);
		}		
		return b;		
	}
	
	/**
	 * 
	 * @param p: first argument of the implication
	 * @param q: second argument of the implication
	 * @return computes "sat" of the implication, p->q
	 */
	private BDD satImplies(FormulaElement p, FormulaElement q){
		
		// rewrite the implication like a disjunction : p->q = (!p or q)
		Negation nExpr1 = new Negation("!", p);
		Disjunction or = new Disjunction("|", nExpr1, q );
		or.accept(this); //compute "sat" of this implication
		return this.sat;
	}
	
	
	
}


