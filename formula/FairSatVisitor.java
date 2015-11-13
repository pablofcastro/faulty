package formula;

import java.util.Iterator;
import java.util.*;

import net.sf.javabdd.*;
import mc.BDDModel;

/**
 *  This class provides a Fair SAT visitor of the model checking algorithm of
 *  dCTL, the attribute goals are the fairness goals of the algorithm
 *  some Permission has not been implemented with fairness (see below),
 *  this will be done in next versions
 * @author Ceci and Pablo
 *
 */

public class FairSatVisitor implements FormulaVisitor {

	private BDD sat;
	private BDDModel model;
	private LinkedList<BDD> goals;


	/**
	 * Basic constructor of the class
	 * @Pre: The model m should be initialized correctly.
	 * @Pos: Initializes a new visitor with the parameters received.
	 */
	public FairSatVisitor(BDDModel m, LinkedList<BDD> goals){
		sat = null;
		model = m;
		this.goals = goals;
	}

	/**
	 * Calculates the sat for a variables
	 * @param v		the variable
	 */
	public void visit(Variable v) {
		String name = v.toString();
		int id = model.getVarID(name);
		sat = model.getFactory().ithVar(id); //gets the state where the var is true

	}

	/** 
	 * Returns the BDD zero or one, depending if the constant value is True or false, respectively
	 * @param c		the constant
	 */
	public void visit(Constant c) {
		sat = c.getValue() ? model.getFactory().one() : model.getFactory().zero();

	}

	/**
	 * Returns the negation of a formula
	 * @param n
	 */
	public void visit(Negation n) {
		n.getExpr1().accept(this);		
		sat = this.getSat().not();

	}

	/**
	 * Returns the implication
	 * @param i
	 */
	public void visit(Implication i) {
		i.getExpr1().accept(this);
		BDD sat_np = this.getSat().not();
		i.getExpr2().accept(this);
		sat = sat_np.or(this.getSat());

	}

	/**
	 * Returns the conjunction
	 * @param c
	 */
	public void visit(Conjunction c) {
		c.getExpr1().accept(this);
		BDD sat_p = this.getSat();
		c.getExpr2().accept(this);
		sat = sat_p.and(this.getSat());
	
	}

	/**
	 * Returns the disjunction
	 * @param d
	 */
	public void visit(Disjunction d) {
		d.getExpr1().accept(this);
		BDD sat_p = this.getSat();
		d.getExpr2().accept(this);
		sat = sat_p.or(this.getSat());	
	}
	
	
	public void visit(Next n){
		
	}
	
	public void visit(Until u){
		
	}
    
    public void visit(Weak u){
		
	}


	@Override
	/**
	 * Implements the sat for formula O(Xp -> Xq)
	 * with fairness
	 * @param o	the formula OXX
	 */
	public void visit(OXX o) {
		// By definition:
        // SAT(O(Xp->Xq)) = ! E(n U n ^ EX(p ^ !q ^ E(n W false))))
		// where all the operators are fair
		
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat.and(mod);
        BDD nq = q.not().and(mod);
        // get the normative states
        BDD ns = model.getNormative();       
        // calculate the sat
        sat = fairEUntil(ns,ns.and(fairEX(p.and(nq).and(fairEX(fairEWuntil(ns, model.getFactory().zero())))))).not().and(mod);
	}

	/**
	 * Implements the sat for formula O(Xp -> s U t)
	 * with fairness
	 * @param o	the formula
	 */
	public void visit(OXU o) {
		// We calculate O(Xp->sUt)
        // SAT(O(Xp->sUt)) = ! E(n U (n ^ (!t ^ EX(p ^ !t ^ n W !s ^ EGn))))
		// where all the operators are fair
        
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD s = this.sat.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD t = this.sat.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD nots = s.not().and(mod);
        BDD nott = s.not().and(mod);
        // get the normative states
        BDD ns = model.getNormative();
        sat = fairEUntil(ns, ns.and(nots.or(nott.and(fairEX(p.and(fairEWuntil(nott.and(ns),nots.and(fairEG(ns))))))))).not().and(mod);
	}

	/**
	 * Calculates fair version of O(pUq -> Xs)
	 * @param o	the formula
	 */
	public void visit(OUX o) {
		// We calculate O(pUq -> Xs)
        // the ctl semantics is O(pUq -> Xs) = !E(n U n ^ (q v (p ^ EX(!s ^ p ^ E(pUq)))))
		// the CTL fair semantics is:
		// O(pUq -> Xs) = !E(n U n ^ ((q ^ (EX(!s ^ EGn))) v (p ^ EX(n ^ !s ^ p ^ E(n ^ p U q ^ EG n) )) ) )
		// where all the operators are fair
    
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD nots = s.not().and(mod);
        // get the normative states
        BDD ns = model.getNormative();
        //sat = fairEUntil(ns, ns.and(q.or(p.and(fairEX(nots.and(p.and(fairEUntil(p,q.and(fairEWuntil(ns,model.getFactory().zero())))))))))).not().and(mod);
        sat = fairEUntil(ns, ns.and(q.and(fairEX(nots.and(fairEG(ns))).or(p.and(fairEX(ns.and(nots.and(p).and(fairEUntil(ns.and(p), q.and(fairEG(ns)))))))))));
	}

	/**
	 * Computes the fair version of O(pUq -> sUt)
	 * @param o
	 */
	public void visit(OUU o) {
		// We calculate O(pUq -> sUt)
        // SAT(O(pUq->sUt)) = ! E(n U \phi1 v \phi2) where:
		// \phi1 = E(n ^ p ^!t U !s ^ !t ^ E(n U q ^ EGn))
        // \phi2 = E(n ^ p ^ !t U n ^ q ^ ((!s ^ !t)   \/ E(!t ^ n W n ^ !s ^ EGn)))
		// where all the operators are fair
		
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
       
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
        BDD not_t = t.not().and(mod);
        BDD not_s = s.not().and(mod);
        BDD n =   model.getNormative();
        BDD phi1 = fairEUntil(n.and(p).and(not_t), not_s.and(not_t).and(fairEUntil(n, q.and(fairEG(n)))));
        BDD phi2 = fairEUntil(n.and(p.and(not_t)), n.and(q.and(not_s.or(fairEWuntil(not_t.and(n), n.and(not_s.and(fairEG(n))))))));
        sat = fairEUntil(n, phi1.or(phi2)).not().and(mod);
        
	}

	/**
	 * Computes the sat for formula O(f1 U f2 -> f3 W f4) with fairness contraints
	 * @param o		the formula
	 */
	public void visit(OUW o){
		// Calculates O(pUq -> sWt)
		// Fair semantics, O(pUq -> sWt)= !E(n U \phi1 v phi2)
		// where:
		// phi1 = E(n^p^!t U n ^ q ^ E(!t ^ n U !s ^ !t ^ EGn))
		// phi2 = E(n ^p ^!t U n ^!t ^ !s ^ E(n^p U q ^EGn))
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
       
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
        BDD not_t = t.not().and(mod);
        BDD not_s = s.not().and(mod);
        BDD n =   model.getNormative();
        BDD phi1 = fairEUntil(n.and(p.and(not_t)), n.and(q).and(fairEUntil(not_t.and(n), not_t.and(not_s).and(fairEG(n)))));
        BDD phi2 = fairEUntil(n.and(p).and(not_t), n.and(not_t).and(not_s).and(fairEUntil(n.and(p), q.and(fairEG(n)))));
		sat = fairEUntil(n, phi1.or(phi2)).not().and(mod);
		
	}
	
	/**
	 * Computes the fair version of O(f1 W f2 -> f3 U f4)
	 * @param o
	 */
	public void visit(OWU o){
		//Fair semantics of O(pWq -> sUt) = !E(n U E(p ^n ^!t W q ^!s ^!t ^ EG(n)) 
		//                                     V E(p ^ !t ^ n U !s ^ !t ^E(n ^ p W q  EG(n))))
		// For the sake of simplicity we use:
		// phi1 = E(p ^n ^!t W q ^!s ^!t ^ EG(n))
		// phi2 = E(p ^ !t ^ n U !s ^ !t ^E(n ^ p W q  EG(n))))
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
       
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
        BDD not_t = t.not().and(mod);
        BDD not_s = s.not().and(mod);
        BDD n =   model.getNormative();
		BDD phi1 = fairEWuntil(p.and(n).and(not_t), q .and(not_s).and(not_t).and(fairEG(n)));
		BDD phi2 = fairEUntil(p.and(not_t).and(n), not_s.and(not_t).and(fairEWuntil(n.and(p), q.and(fairEG(n)))));
		sat = fairEUntil(n, phi1.or(phi2)).not().and(mod);
		
	}
	
	/**
	 * Computes the fair version of O(X f1 -> f2 W f3)
	 * @param o
	 */
	public void visit(OXW o){
		//Fair Semantics: O(Xp -> s W t) = ! E(n U EX(p ^ E(n ^!t U !t ^!s ^EGn))
		
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;
        BDD not_t = t.not().and(mod);
        BDD not_s = s.not().and(mod);
        BDD n =   model.getNormative();
        sat = fairEUntil(n, n.and(fairEX(p.and(n).and(fairEUntil(n.and(not_t), not_t.and(not_s).and(fairEG(n))))))).not().and(mod);
	}
	
	/**
	 * Computes the fair version of O(f1 W f2 -> X f3)
	 * @param o
	 */
	public void visit(OWX o){
		// Fair semantics O(pWq -> Xs) = 
		//								!E(n U n ^ EX(!p ^ n ^ E(pW q^EGn)))
		
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        BDD not_p = p.not().and(mod);
        BDD n =   model.getNormative();
		sat = fairEUntil(n, n.and(fairEX(not_p.and(fairEWuntil(p,q.and(fairEG(n))))))).not().and(mod);
		
	}
	
	/**
	 * Computes the fair version of O(f1 W f2 -> f3 W f4)
	 * @param o
	 */
	public void visit(OWW o){
		// Fair semantics O(pWq -> sWt)=
		//			!E(n U phi1 v phi2)
		// where:
		// phi1 = E(!t ^p ^ n U q ^ E(!t ^ n U !t ^ !s ^ EG(n)))
		// phi2 = E(!t  ^p U !t ^!s ^E(p ^ n W q ^ EG(n)))
		
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
       
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
        BDD not_t = t.not().and(mod);
        BDD not_s = s.not().and(mod);
        BDD n =   model.getNormative();
        BDD phi1 = fairEUntil(not_t.and(p).and(n), n.and(q).and(fairEUntil(not_t.and(n),not_t.and(not_s).and(fairEG(n)))));
        BDD phi2 = fairEUntil(not_t.and(p), not_t.and(not_s).and(fairEWuntil(p.and(n), q.and(fairEG(n)))));
		sat = fairEUntil(n, phi1.or(phi2)).not().and(mod);
	}
	
	/**
	 * Computes fair version of O(Xp), this can be calculated
	 * we the operator above, but for simplicity and preformance 
	 * reasons we include an implementation here.
	 * @param o	the params of O(Xp) (p)
	 */
	public void visit(OX o){
		// Fair semantics O(Xp) = !E(n U n ^ EX(!p ^ EGn))
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        BDD n =   model.getNormative();
        
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        BDD not_p = p.not().and(mod);
        sat = fairEUntil(n, n.and(fairEX(not_p.and(fairEG(n))))).not().and(mod);
	}
	
	/**
	 * Computes fair version of O(pWq), this also can be calculated using the operation above 
	 */
	public void visit(OW o){
		// Fair semantics O(pWq)=!E(n U E(n ^!q U !p ^ !q  ^ EGn))
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
       
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        BDD n =   model.getNormative();
        
        BDD not_p = p.not().and(mod);
        BDD not_q = q.not().and(mod);
        sat = fairEUntil(n, fairEUntil(n, fairEUntil(n.and(not_q), not_p.and(not_q).and(fairEG(n)) ))).not().and(mod);
		
	}
	
	public void visit(OU o){
		// Fair semantics: O(p U q) = !E(n U E(n ^ !q W !p ^ !q ^ EGn))
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
       
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        BDD n =   model.getNormative();
        
        BDD not_p = p.not().and(mod);
        BDD not_q = q.not().and(mod);
        
        sat = fairEUntil(n, fairEWuntil(n.and(not_q), not_p.and(not_q).and(fairEG(n)))).not().and(mod);
		
	}
	
	/**
	 * Computes fair version of P(Xp->Xq)
	 * @param args
	 */
	public void visit(PXX args) {
		// we calculate P(Xp -> Xq)
		// Fair Semantics SAT(P(Xp -> Xq)) = vX . n ^ <> ((! p v q ) ^ (u Y . n ^ (Pk^  X v <>(!p v q) ^ Y) ))  
        // NOTE: Can this be expressed as a CTL formula???
		
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = args.getExpr1();
        expr1.accept(this);
        BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = args.getExpr2();
        expr2.accept(this);
        BDD q = this.sat.and(mod);
        BDD n =   model.getNormative();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        BDD X = mod;
        BDD X_old = model.getFactory().zero();
        BDD notp = p.not().and(mod);
        // calculate the GFP
        while (!X.biimp(X_old).isOne()){
            X_old = X;
            // calculates the conjuntction of all the goals
            BDD satGoals = model.getFactory().one();
            for (int i = 0; i < goals.size(); i++){
            	BDD Y = model.getFactory().zero();
            	BDD Y_old = model.getFactory().one();
            	while (! Y.biimp(Y_old).isOne()){
            		Y_old = Y;
            		Y = goals.get(i).and(X).or(NWeakPrevious(notp.or(q).and(Y)));
            	}
            	satGoals = satGoals.and(Y);
            }
            X = NWeakPrevious(X.and(notp.or(q)).and(satGoals));
        }
        // return the GFP
        sat = X;
	}

	/**
	 * TO BE DONE!
	 * @param args
	 */
	@Override
	public void visit(PXU args) {
		// We calculate P(xp -> sUt)
		// Semantics: SAT(P(Xp -> s U t)) = vY.(uX.n^((t^<>Y)\/(s^<>((!p \/ X)^Y)))\/ (<>!p^Y))
		// Fair Semantics: SAT(P(Xp -> s U t)) = TBD
		
		
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
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
        BDD n =   model.getNormative();
        BDD Y = mod;
        BDD Y_old = null;
        BDD X = model.getFactory().zero();
        BDD X_old = null;
        BDD result = null;
        BDD notp = p.not().and(mod);
       // BDD result =
        while (!Y.biimp(Y_old).isOne()){
            Y_old = Y;
            while (!X.biimp(X_old).isOne()){
                X_old = X;
                X = n.and(t.and(WeakPrevious(Y)).or(s.and(WeakPrevious((notp.or(X)).and(Y)))));
            }
            Y = X.or(WeakPrevious(notp.and(Y))).and(n);
            
        }
        // we should test this!!!
        sat = X;
    }

	/**
	 * TBD
	 * @param args
	 */
	public void visit(PUX args) {
		// We calculate P(s U t -> Xp)
        // Semantics: SAT(P(sUt -> Xp)) = vY . (v X . (!s \/ <>(X^Y^!t))) \/ (<>p^Y)
		// FAir Semantics TBD
        
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
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
        BDD n =   model.getNormative();
        BDD Y = mod;
        BDD Y_old = null;
        BDD X = model.getFactory().zero();
        BDD X_old = null;
        BDD nots = s.not().and(mod);
        BDD nott = t.not().and(mod);
        
        while(!Y.biimp(Y_old).isOne()){
            Y_old = Y;
            while (!X.biimp(X_old).isOne()){
                X_old = X;
                X = nots.or(WeakPrevious(X.and(Y).and(nott)));
            }
            Y = (X.and(n)).or(WeakPrevious(p.and(Y)));
        }
        sat = Y;
	}

	/**
	 * TBD
	 * @param args
	 */
	public void visit(PUU args) {
        // TO DO: Modificar!
		// We calculate P(p U q -> s U t)
        // Semantics: SAT(P(p U q -> s U t) = vY . n ^ ((uX . !q \/ <>(!p ^ X ^ Y) \/ vZ . s \/ <>(t ^ Z ^ Y) )
        // Fair semantics TBD
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
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
        BDD n =   model.getNormative();
        BDD Y = mod;
        BDD Y_old = null;
        BDD X = model.getFactory().zero();
        BDD X_old = null;
        BDD Z = model.getNormative();
        BDD Z_old = null;
        
        BDD notq = q.not().and(mod);
        BDD notp = p.not().and(mod);
        
        
	}

	
	public void visit(PXW p){
		// TBD
	}
	
	public void visit(PWX p){
		// TBD
	}
	
	public void visit(PWU p){
		// TBD
	}
	
	public void visit(PUW p){
		// TBD
	}
	
	public void visit(PWW p){
		// TBD
	}
	
	/** 
	 * Calculates the sat for PXp with fairness requirements 
	 * @param p
	 */
	public void visit(PX args){
		// Fair semantics PXp = EX(n^p^EGn)
		int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
			 
		FormulaElement expr1 = args.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		BDD n =   model.getNormative();
		sat = fairEX(n.and(p).and(fairEG(n)));
	}
	
	/**
	 * Calculates the formula P(sUt) with fairness requirements
	 * @param p
	 */
	public void visit(PU p){
		// Fair Semantics P(sUt) =
		// 				  vX . n^(t^(A uY. (Pk^X) v <> n ^(svt) ^Y))
		//					   v
		//					   s ^ (A uY . Pk^Z v <> n ^ s ^ Z)  
		// it uses 3 nested fixed points, must be O(S^3)
		
		int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
			        
		// we get the parameters
		FormulaElement expr1 = p.getExpr1();
		expr1.accept(this);
		BDD s = this.sat;
		FormulaElement expr2 = p.getExpr2();
		expr2.accept(this);
		BDD t = this.sat;
		BDD n =   model.getNormative();
		
		// we calculate the fixed points
		BDD X = model.getFactory().one();
		BDD X_old = model.getFactory().zero();
		while (!X.biimp(X_old).isOne()){
			X_old = X;
			BDD Z = model.getFactory().zero();
			BDD Z_old = model.getFactory().one();
			while (!Z.biimp(Z_old).isOne()){
				Z_old = Z;
				BDD goalConjunct1 = model.getFactory().one();
				for (int i = 0; i < goals.size(); i++){
					BDD Y = model.getFactory().zero();
					BDD Y_old = model.getFactory().one();
					while (!Y.biimp(Y_old).isOne()){
						Y_old = Y;
						Y = goals.get(i).and(X).and(n).or(WeakPrevious(n.and(s.or(t).and(Y))));
					}
					goalConjunct1 = goalConjunct1.and(Y);
				}
				BDD goalConjunct2 = model.getFactory().one();
				for (int i = 0; i < goals.size(); i++){
					BDD Y = model.getFactory().zero();
					BDD Y_old = model.getFactory().one();
					while (!Y.biimp(Y_old).isOne()){
						Y_old = Y;
						Y = goals.get(i).and(Z).and(n).or(WeakPrevious(n.and(s.or(t).and(Y))));
					}
					goalConjunct2 = goalConjunct2.and(Y);
				}
				Z = n.and(t).and(goalConjunct1).or(s.and(goalConjunct2));
			}
			X = Z;
		}
		sat = X;
	}
	
	/**
	 * Calculates the formula P(sWt) with fairness requirements
	 * @param p
	 */
	public void visit(PW p){
		// Fair Semantics P(sWt) =
		// 				  vX .nZ. n^(t^(A uY. (Pk^X) v <> n ^(svt) ^Y))
		//					   v
		//					   s ^ (A uY . Pk^Z v <> n ^ s ^ Z)  
		// it uses 3 nested fixed points, must be O(S^3)
			
		int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
			        
		// we get the parameters
		FormulaElement expr1 = p.getExpr1();
		expr1.accept(this);
		BDD s = this.sat;
		FormulaElement expr2 = p.getExpr2();
		expr2.accept(this);
		BDD t = this.sat;
		BDD n =   model.getNormative();
				
		// we calculate the fixed points
		BDD X = model.getFactory().one();
		BDD X_old = model.getFactory().zero();
		while (!X.biimp(X_old).isOne()){
			X_old = X;
			BDD Z = model.getFactory().one();
			BDD Z_old = model.getFactory().zero();
			while (!Z.biimp(Z_old).isOne()){
				Z_old = Z;
				BDD goalConjunct1 = model.getFactory().one();
				for (int i = 0; i < goals.size(); i++){
					BDD Y = model.getFactory().zero();
					BDD Y_old = model.getFactory().one();
					while (!Y.biimp(Y_old).isOne()){
						Y_old = Y;
						Y = goals.get(i).and(X).and(n).or(WeakPrevious(n.and(s.or(t).and(Y))));
					}
					goalConjunct1 = goalConjunct1.and(Y);
				}
				BDD goalConjunct2 = model.getFactory().one();
				for (int i = 0; i < goals.size(); i++){
					BDD Y = model.getFactory().zero();
					BDD Y_old = model.getFactory().one();
					while (!Y.biimp(Y_old).isOne()){
						Y_old = Y;
						Y = goals.get(i).and(Z).and(n).or(WeakPrevious(n.and(s.or(t).and(Y))));
					}
					goalConjunct2 = goalConjunct2.and(Y);
				}
				Z = n.and(t).and(goalConjunct1).or(s.and(goalConjunct2));
			}
			X = Z;
		}
		sat = X;
	}

	
	public void visit(Recovery r){
		// TBD
	}
	
	
	/**
	 * Calculates the sat for RXp
	 * @param r
	 */
	public void visit(RX r) {
		

	}

	/**
	 * Calculates the sat for R(pUq)
	 * @param r
	 */
	public void visit(RU r) {
		/*
		 *  TO DO
		 */

	}
	
	/**
	 * Calculates the sat for R(pWq)
	 * @param r
	 */
	public void visit(RW r) {
		/*
		 *  TO DO
		 */
	}
	
	/**
	 * Calculates fair sat for AXp
	 * @param a
	 */
	public void visit(AX a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		sat = fairAX(p);
	}
	
	/**
	 * Calculates fair sat for A(pUq)
	 * @param a
	 */
	public void visit(AU a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = a.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = fairAUntil(p, q);
	}
	
	/**
	 * Calculates fair sat for A(pWq)
	 * @param a
	 */
	public void visit(AW a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = a.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = fairAWuntil(p, q);
	}
	
	/**
	 * Calculates fair sat for EXp
	 * @param e
	 */
	public void visit(EX e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		sat = fairEX(p);
	}
	
	/**
	 * Calculates fair sat for E(pUq)
	 * @param e
	 */
	public void visit(EU e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = e.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = fairEUntil(p, q);
	}
	
	/**
	 * Calculates fair sat for E(pWq)
	 * @param e
	 */
	public void visit(EW e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		FormulaElement expr2 = e.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		sat = fairEWuntil(p, q);
	}
	
	
	/**
	 * The following methods provide ways of calculating the
	 * SAT of CTL formulas of type A(pUq -> s U t), since
	 * we are not using them in the model checker they are not needed
	 * only implemented for testing purposes.
	 * @param a
	 */
	public void visit(AXX a) {
        //Sat(A(Xp ~> Xq) = StrongPrevious(p->q)
        
       	//calculates Sat (p->q)
		BDD sat_imp = satImplies(a.getExpr1(),a.getExpr2());
        
        int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		mod = Post(mod).and(sat_imp);	//calculates all reachable states that satisfies the implication.
        
        sat = StrongPrevious(mod); // calculate the strong previous of the states that satisfies the implication
	}

	@Override
	public void visit(AXU a) {
        // we calculate A(Xp->sUt)
        // A(Xp->sUt) = ! E(Xp ^ !(sUt)) =! ((!s ^ EXp) \/ (!t ^ EX(p ^ E(!tU!s))) \/ (!t ^ EX(p ^ EG!t))  )
        // Get the states of the model in mod
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = a.getExpr1();
        expr1.accept(this);
        BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = a.getExpr2();
        expr2.accept(this);
        BDD s = this.sat.and(mod);
        FormulaElement expr3 = a.getExpr3();
        expr3.accept(this);
        BDD t = this.sat.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD ns = s.not().and(mod);
        BDD nt = t.not().and(mod);
        sat = (ns.and(EX(p)).or(nt.and(EUntil(nt,ns))).or(nt.and(EX(p.and(EWuntil(nt,model.getFactory().zero())))))).not().and(mod);
    }

	@Override
	public void visit(AUX a) {
        // we calculate A(sUt->Xp)
        // A(sUt->Xp) = ! E((sUt) ^ X!p) = !( t^EX(!p) \/ (p ^ EX(!p ^ E(sUt))))
        
        // Get the states of the model in mod
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = a.getExpr1();
        expr1.accept(this);
        BDD s = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = a.getExpr2();
        expr2.accept(this);
        BDD t = this.sat.and(mod);
        FormulaElement expr3 = a.getExpr3();
        expr3.accept(this);       
        BDD p = this.sat.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD np = p.not().and(mod);
       
        sat = (t.and(EX(np)).or(p.and(EX(np.and(EUntil(s,t)))))).not().and(mod);
    }
    

	@Override
	public void visit(AUU a) {
        // we calculate A(pUq -> sUt)
        // the semantics is: A(pUq->sUt) = E(p ^ !t U (q ^!t ^ EG!t)) \/ E(p ^ !t U (q ^ E(!t U !s))) \/ E(p ^ !t U !s ^ E(pUq))
		int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
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
        BDD nt = t.not().and(mod);
        BDD ns = t.not().and(mod);
        sat= (EUntil(p.and(nt),q.and(nt).and(EWuntil(nt,model.getFactory().zero()))).or(EUntil(p.and(nt), q.and(EUntil(nt,ns)))).or(EUntil(p.and(nt), ns.and(EUntil(p,q))))).not().and(mod);
        
        //BDD notq_until_not_p = AWuntil(q.not(),p.not());
        //BDD s_until_q = AUntil(s,t);
        //BDD impl = notq_until_not_p.or(s_until_q); // this BBD captures the implication !pUq -> sUt
        // we calculate the greatest fixed point
        // this is not need it as A(->) is different from O
        //BDD result = mod;
        //BDD result_old = model.getFactory().zero();
        //while (!result.biimp(result_old).isOne()){
        //    result_old = result;
        //    result = StrongPrevious(result).and(impl); // those states satisfying the implication
        //}
        //sat = impl;
        
	}

	@Override 
	public void visit(EXX e) {
		// Sat( E(Xp ~> Xq) ) = {s in S/ Post(s) intersection Sat(!p or q) != Empty}
		// calculates Sat (p->q)
		BDD sat_imp = satImplies(e.getExpr1(),e.getExpr2());
		
		int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();		
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
    	mod = Post(mod).and(sat_imp);	//calculates all reachable states that satisfies the implication.
		
    	sat = WeakPrevious(mod); // calculate the weak previous of the states that satisfies the implication
	}

	@Override
	public void visit(EXU e) {
		// We calculate E(Xp -> sUt)
        // the CTL semantics is E(Xp->sUt) = EX(!p) \/ E(sUt)
        
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD s = this.sat.and(mod);
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD t = this.sat.and(mod);
        // we calculate the result using its CTL semantics
        // the negations are calculated taking into account the model
        BDD np = p.not().and(mod);
        sat = EX(np).or(EUntil(s,t));

	}

	@Override
	public void visit(EUX e) {
		// We calculate E(sUt -> Xp)
        // the CTL semantics is E(sUt->Xp) = E(!tU!s) \/ EG(s) \/ EXp
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD s = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD t = this.sat.and(mod);
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD p = this.sat.and(mod);
        BDD nt = t.not().and(mod);
        BDD ns = s.not().and(mod);
        
        sat = EUntil(nt,ns).or(EWuntil(s,model.getFactory().zero())).or(EX(p));

	}

	@Override
	public void visit(EUU e) {
		// We calculate E(pUq -> sUt)
        // the semantics is E(pUq -> sUt) = E(!qU!p) \/ EG(!q) \/ E(sUt)
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
        mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
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
        BDD np = p.not().and(mod);
        BDD nq = q.not().and(mod);
        
        sat = EUntil(nq,np).or(EWuntil(nq,model.getFactory().zero())).or(EUntil(s,t));

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
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        q = q.and(mod); // get those states that hold q
        BDD result = q;
        BDD result_old = model.getFactory().zero();
        // this loop calculates the least fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            result = StrongPrevious(result).and(p).or(q);
        }
        return result;
    }
    
    
    /***
     * @return returns the set of states
     * @param p: the first formula of A(pWq)
     * @param q: the second formula of A(pWq)
     */
    public BDD AWuntil(BDD p, BDD q){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        q = q.and(mod); // get those states that hold q
        p = p.and(mod); // get those states that hold p
        BDD result = mod;
        BDD result_old = model.getFactory().zero();
        // this loop calculates the greatest fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            result.printSet();
            result = StrongPrevious(result).and(p).or(q);
        }
        return result;
    }

    
    /***
     * @return returns the set of states
     * @param p: the first formula of AGp
     */
    public BDD AG(BDD p){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        p = p.and(mod); // get those states that hold p
        return AWuntil(p,model.getFactory().zero());
    }
    

    
    /***
     * @return returns the set of states
     * @param p: the first formula of E(pWq)
     * @param q: the second formula of E(pWq)
     */
    public BDD EWuntil(BDD p, BDD q){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        q = q.and(mod); // get those states that hold q
        p = p.and(mod); // get those states that hold p
        BDD result = mod;
        BDD result_old = model.getFactory().zero();
        // this loop calculates the greatest fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            result = WeakPrevious(result).and(p).or(q);
        }
        return result;
    }

    
    /***
     * @return returns the set of states
     * @param p: the first formula of EGp
     */
    public BDD EG(BDD p){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        p = p.and(mod); // get those states that hold p
        return EWuntil(p,model.getFactory().zero());
    }
    

    
    /***
     * @return returns the set of states
     * @param p: the first formula of E(pUq)
     * @param q: the second formula of E(pUq)
     */
    private BDD EUntil(BDD p, BDD q){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        q = q.and(mod); // get those states that hold q
        BDD result = model.getFactory().zero();
        BDD result_old = model.getFactory().one();
        // this loop calculates the least fixed point
        while (!result.biimp(result_old).isOne()){
            result_old = result;
            result = WeakPrevious(result).and(p).or(q);
        }
        return result;
    }
    
    /**
     * Fair Statement, it captures EGFair formula
     * saying that there is a fair path satisfying the goals
     * @param	goals	the goals to be hold
     */
    private BDD fair(){
    	int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        
        BDD result = model.getFactory().one();
        BDD result_old = model.getFactory().zero();
        while(!result.biimp(result_old).isOne()){
        	result_old = result;
        		BDD conjunct = model.getFactory().one();
        		for(int i = 0; i < goals.size(); i++){
        			conjunct = conjunct.and(WeakPrevious(EUntil(model.getFactory().one(), result.and(goals.get(i)))));
        		}
        		result = conjunct;
        }
        //return result;
        return goals.get(0);
    }
    
    /**
     * Fair Statement, it captures AGFair formula
     * saying that there is a fair path satisfying the goals
     * @param	goals	the goals to be hold
     */
    private BDD allFair(){
    	int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        	
        BDD result = model.getFactory().one();
        BDD result_old = model.getFactory().zero();
        while(!result.biimp(result_old).isOne()){
        	result_old = result;
        		for(int i = 0; i < goals.size(); i++){
        			result = result.and(StrongPrevious(AUntil(model.getFactory().one(), result.and(goals.get(i)))));
        		}
        }
        return result;
    }
    
    /**
     * Fair version of the EUntil operator
     * @param 	p	the first operand of the formula
     * @param	q	the second operand of the formula
     */
    public BDD fairEUntil(BDD p, BDD q){
        BDD result = EUntil(p, q.and(fair()));
        return result;
    }

    /**
     * Fair version of next 
     * @param p			the property EXp
     * @param goals		the goals to be satisified in fairs paths
     * @return	the BDD respresenting FairEXp
     */
    public BDD fairEX(BDD p){
    	return EX(p.and(fair()));
    	
    }
    
    /**
     * Fair version of EW
     * @param p		first subformula of EW
     * @param q		second subformula of EW
     * @param goals	the fairness goals
     * @return	the BDD representing fairEW(p,q)
     */
    public BDD fairEWuntil(BDD p, BDD q){

    	int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        	
        BDD result = model.getFactory().one();
        BDD result_old = model.getFactory().zero();
        
        while (!result.biimp(result_old).isOne()){
        	result_old = result;
        	BDD conjunct = model.getFactory().one();
        	for (int i = 0; i < goals.size(); i++){
        		conjunct = conjunct.and(WeakPrevious(EUntil(p, result.and(goals.get(i)))));
        	}
        	result = q.or(p.and(conjunct));
        }  
        return result;
    }
    
    /**
     * Fair version of AX
     * @param p		the param of AX
     * @param goals	the goals to be	satisfied by the fairness requirement
     * @return	the BDD respresenting fairAX
     */
    public BDD fairAX(BDD p){
    	int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		
		BDD not_p = p.not().and(mod); 
		
		BDD result = fairEX(not_p).not().and(mod);
		return result;
    	//return AX(p.and(allFair()));
		
    }
    
    /**
     * Fair version of AU
     * @param p		first operand of AU
     * @param q		second operand of AU
     * @param goals		the fairness foals
     * @return	the BDD respresenting the formula
     */
    public BDD fairAUntil(BDD p, BDD q){
    	int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		
		BDD not_p = p.not().and(mod);
		BDD not_q = p.not().and(mod);
		BDD result = fairEWuntil(not_q, not_p).not().and(mod);
		return result;
    }
    
    /**
     * Fair version of AW
     * @param p		first operand of AW
     * @param q		second operand of AW
     * @param goals	the goals of the fairness
     * @return		the BDD respresenting the property
     */
    public BDD fairAWuntil(BDD p, BDD q){
    	int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		
		BDD not_p = p.not().and(mod);
		BDD not_q = p.not().and(mod);
		BDD result = fairEUntil(not_q, not_p).not().and(mod);
		return result;
    }
    
    /**
     * Fair version of EG
     */
    public BDD fairEG(BDD p){
    	return fairAWuntil(p, model.getFactory().zero());
    }
    
    /***
     * @return returns the set of states that hold EXp
     * @param p: the first formula of EXp
     */
    private BDD EX(BDD p){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        p = p.and(mod); // get those states that hold p
        BDD result = WeakPrevious(p); // the result is the weak previous
        return result;
    }

    /***
     * @return returns the set of states that hold AXp
     * @param p: the first formula of AXp
     */
    private BDD AX(BDD p){
        int varNum = model.getFactory().varNum();
        BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        p = p.and(mod); // get those states that hold p
        BDD result = StrongPrevious(p); // the result is the strong previous
        return result;
    }
    
    
	/***
	 * 
	 * @return returns the set of successors states, reached in a step from the state "s".
	 */
	private BDD Post(BDD s){
		BDD trans = model.getTransitions();
		BDD rest = trans.and(s);	
		rest = addExists(0, model.getFactory().varNum()/2, rest,model.getFactory()); 

		boolean r_primed = true;  //flag to indicates that will be changes all variables v' by v
		BDDPairing pairs = makePairsToReplace(r_primed);
		rest = rest.replaceWith(pairs);//rename each variable: v' -> v
		
		return 	rest;	
	}


	/***
	 * 
	 * @return returns the set of successors states("normal"), reached in a step from the state b.
	 */
	private BDD Post_N(BDD b){

		BDD trans = model.getTransitions();
		// calculates the set of successors states, reached in a step from the state "b"
		BDD post = Post(b);
		return post.and(model.getNormative());		
	}
	
	
	/***
	 * @param
	 * @return returns the set of previous states of the states "b".
	 */
	private BDD WeakPrevious(BDD b){
        
        int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		mod = Post(mod).and(b);	//calculates those  states that are succesors of someone and hold b .
 
    	boolean r_primed = false;  //flag to indicates that will be changes all variables v by v'
		BDDPairing pairs = makePairsToReplace(r_primed);
		
       /* BDDPairing pairs = model.getFactory().makePair();             
          for (int i=0; i<varNum/2; i++)
            pairs.set(i,(varNum/2)+i);      
       */       
        
        //rename each variable: v' -> v
	    mod = mod.replaceWith(pairs);	       
	    mod = model.getTransitions().and(mod);
        return addExists(varNum/2, varNum, mod ,model.getFactory());

	}
	
	/***
	 * 
	 * @return the set of previous states of the states "b", in which all successors are
	 * included in the states given as parameter.-
	 */
	private BDD StrongPrevious(BDD b){
        BDD npre = WeakPrevious(b.not()); //calculates the weak previous of not b
        BDD pre = WeakPrevious(b); // calculates the weak previous of b
        return (pre.and(npre.not())); // the result is given by those sets in pre and not in npre
	}

    /***
	 *
	 * @return the set of normative previous states of the states "b".
	 */
	private BDD NWeakPrevious(BDD b){
        BDD norm = model.getNormative();
        int varNum = model.getFactory().varNum();
		BDD mod = model.getTransitions();
		mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
		mod = Post(mod).and(b);	//calculates those  states that are succesors of someone and hold b .

		boolean r_primed = false;  //flag to indicates that will be changes all variables v by v'
		BDDPairing pairs = makePairsToReplace(r_primed);
		
        /*BDDPairing pairs = model.getFactory().makePair();
        for (int i=0; i<varNum/2; i++)
            pairs.set(i,(varNum/2)+i);            
        */
		
        //rename each variable: v' -> v
	    mod = mod.replaceWith(pairs);
	          
	    mod = model.getTransitions().and(mod).and(norm);
        return addExists(varNum/2, varNum, mod ,model.getFactory());
	}
	
	/***
	 *
	 * @return the set of normative previous states of the states "b", in which all successors are
	 * included in the states given as parameter.-
	 */
	private BDD NStrongPrevious(BDD b){
        BDD npre = NWeakPrevious(b.not()); //calculates the weak previous of not b
        BDD pre = NWeakPrevious(b); // calculates the weak previous of b
        return (pre.and(npre.not())); // the result is given by those sets in pre and not in npre
    }

    

	/***
	 * 
	 * @param f
	 * @return Compute the set Sat( P (X f) )
	 */
	private void SatPX(FormulaElement f){
		// Sat( P(X f) ) = {s in Normatives / Post_N (s) intersection Sat(f) != Empty}		
		f.accept(this);
		BDD sat_f = this.getSat();				
		sat =(Post_N(model.getNormative()).and(sat_f));
	}


	/***
	 * 
	 * @param f
	 * @return Compute the greatest fixed point of the formula 
	 */
	private void GFP(FormulaElement f){
		f.accept(this);
		sat = this.sat;

	}

	/***
	 * 
	 * @param f
	 * @return Compute the Least fixed point of the formula 
	 */
	private void LFP(FormulaElement f){
		f.accept(this);
		sat = this.sat;

	}

    /****
     * 
     * @param r_primed : Boolean flag to indicates if will be replaced all primed variables
     *  (in which case r_primed = true) or all common variables (r_primed =false).-
     * @return creates pairs to replace primed (v' -> v) or common variables (v-> v')
     */
	private BDDPairing makePairsToReplace(boolean r_primed){      

		//obtain the identifier of each variable of the BDD.
		int varNum = model.getFactory().varNum() / 2;
		BDDPairing pairs = model.getFactory().makePair();		

		if(r_primed){// creates pairs to changes variables v' by v , is to mean v -> v'
		//	System.out.println("Pairs to change v' by v ");
			for (int i=0; i<varNum; i++){
				pairs.set((varNum)+i, i);
		//		System.out.println("pair ( " + ((varNum)+i) + ", " + i + " )" );
			}   
		}else{// creates pairs to changes variables v by v',  is to mean v' -> v
		//	System.out.println("Pairs to change v by v' ");
			for (int i=0; i<varNum; i++){
				pairs.set(i,(varNum)+i);
		//		System.out.println("pair ( " + i + ", " + ((varNum)+i) + " )" );
			}   

		} 

		return pairs;
	}	
	
	
	/**
	 * Prints the set of truth assignments specified by this list.
	 * @param sol
	 */
	private void  printSolutions (List sol){

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


	/****
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
	
	/****
	 * 
	 * @param p: first argument of the implies.
	 * @param q: second argument of the implies.
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
