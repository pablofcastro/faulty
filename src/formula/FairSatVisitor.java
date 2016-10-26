package formula;

import java.util.Iterator;
import java.util.*;

import net.sf.javabdd.*;
import mc.BDDModel;
import faulty.*;


/**
 *  This class provides a Fair SAT visitor of the model checking algorithm of
 *  dCTL, the attribute goals are the fairness goals of the algorithm
 *  some Permissions have not been implemented with fairness (see below),
 *  this will be done in next versions. The implementation can be improved using 
 *  inheritance or agreggation, however, most of the algorithms need to be changed,
 *  then code reuse is not useful here 
 * @author Ceci and Pablo
 *
 */

public class FairSatVisitor implements FormulaVisitor {

	private BDD sat;
	private BDDModel model;
	private LinkedList<BDD> goals;
	
	private BDD norm; // A formula representing the normative part of the system
	 // this can be calculated without generating the BDD for all the system
	private LinkedList<BDDModel> models; // the list of model off all the processes, the idea is to avoid generating
										 // the complete model as far as possible.
	private Program program; // a reference to the main program


	/**
	 * Basic constructor of the class
	 * @Pre: The model m should be initialized correctly.
	 * @Pos: Initializes a new visitor with the parameters received.
	 */
	public FairSatVisitor(Program program, LinkedList<BDD> goals){
		sat = null;
		model = program.getModel();
		this.goals = goals;
		models = new LinkedList<BDDModel>();
		this.program = program;
		
		// we get the BDDs of the processes in the program
		models = program.buildPartialModels();
		
		// we calculate the normative conditions
		BDD n = Program.myFactory.one();
		for (int i=0; i < models.size(); i++){
			BDDModel current = models.get(i);
			// we calculate the normative condition
			n = n.and(current.getNormative());			
		}		
		norm = n;			
	}

	/**
	 * Calculates the sat for a variables
	 * @param v		the variable
	 */
	public void visit(Variable v) {
		String name = v.toString();
		int id = model.getVarID(name);		
		sat = Program.myFactory.ithVar(id);
	}

	/** 
	 * Calculates the BDD zero or one, depending if the constant value is True or false, respectively
	 * @param c		the constant
	 */
	public void visit(Constant c) {
		sat = c.getValue() ? model.getFactory().one() : model.getFactory().zero();

	}

	/**
	 * Calculates the negation of a formula
	 * @param n
	 */
	public void visit(Negation n) {
		n.getExpr1().accept(this);		
		sat = this.getSat().not();

	}

	public void visit(EqComparison e) {
    }
    
	/**
	 * Calculates the implication
	 * @param i
	 */
	public void visit(Implication i) {
		i.getExpr1().accept(this);
		BDD sat_np = this.getSat().not();
		i.getExpr2().accept(this);
		sat = sat_np.or(this.getSat());

	}

	/**
	 * Calculates the conjunction
	 * @param c
	 */
	public void visit(Conjunction c) {
		c.getExpr1().accept(this);
		BDD sat_p = this.getSat();
		c.getExpr2().accept(this);
		sat = sat_p.and(this.getSat());
	
	}

	/**
	 * Calculates the disjunction
	 * @param d
	 */
	public void visit(Disjunction d) {
		d.getExpr1().accept(this);
		BDD sat_p = this.getSat();
		d.getExpr2().accept(this);
		sat = sat_p.or(this.getSat());	
	}
	
	
	public void visit(Next n){
		// not needed
	}
	
	public void visit(Until u){
		// not needed
	}
    
    public void visit(Weak u){
		// not needed
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
		
		 // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        // dont know if we really ned to do the intersection with the states of the model!
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        BDD nq = q.not();
        // get the normative states
        BDD ns = norm;       
        // calculate the sat
        sat = fairEUntil(ns,ns.and(fairEX(p.and(nq).and(fairEX(fairEWuntil(ns, model.getFactory().zero())))))).not();
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
        sat = fairEUntil(ns, ns.and(nots.and(nott.and(fairEX(p.and(ns)))).or(nott.and(fairEX(p.and(fairEWuntil(nott.and(ns),nott.and(nots).and(fairEG(ns))))))))).not();    
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
          
        // start obtaining the BDDs of the subexpressions
		FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;//.and(mod);
        
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;//.and(mod);
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;//.and(mod);
        
        // we calculate the result using its CTL semantics
        BDD nots = s.not();
        
        // get the normative states
        BDD ns = norm;
        sat = fairEUntil(ns, ns.and(q.and(fairEX(nots)).or(p.and(fairEX(nots.and(ns).and(fairEUntil(p.and(ns),q.and(fairEG(ns))))))))).not();
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
        BDD not_t = t.not();
        BDD not_s = s.not();
        BDD n =   norm;
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
		
   //     int varNum = model.getFactory().varNum();
   //     BDD mod = model.getTransitions();
        // start obtaining the BDDs of the subexpressions
   //		FormulaElement expr1 = args.getExpr1();
   //     expr1.accept(this);
  //      BDD p = this.sat.and(mod);
        // dont know if we really ned to do the intersection with the states of the model!
  //       FormulaElement expr2 = args.getExpr2();
  //      expr2.accept(this);
  //      BDD q = this.sat.and(mod);
  //      BDD n =   model.getNormative();
  //      mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
  //      BDD X = mod;
 //       BDD X_old = model.getFactory().zero();
 //       BDD notp = p.not().and(mod);
        // calculate the GFP
 //       while (!X.biimp(X_old).isOne()){
 //           X_old = X;
            // calculates the conjuntction of all the goals
 //           BDD satGoals = model.getFactory().one();
 //           for (int i = 0; i < goals.size(); i++){
 //           	BDD Y = model.getFactory().zero();
 //           	BDD Y_old = model.getFactory().one();
 //           	while (! Y.biimp(Y_old).isOne()){
//            		Y_old = Y;
//            		Y = goals.get(i).and(X).or(NWeakPrevious(notp.or(q).and(Y)));
 //           	}
 //           	satGoals = satGoals.and(Y);
 //           }
 //           X = NWeakPrevious(X.and(notp.or(q)).and(satGoals));
 //       }
        // return the GFP
 //       sat = X;
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
		
		
  //      int varNum = model.getFactory().varNum();
  //      BDD mod = model.getTransitions();
  //      mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
  //      FormulaElement expr1 = args.getExpr1();
  //      expr1.accept(this);
  //      BDD p = this.sat;
  //      FormulaElement expr2 = args.getExpr2();
  //      expr2.accept(this);
  //      BDD s = this.sat;
  //      FormulaElement expr3 = args.getExpr3();
  //      expr3.accept(this);
  //      BDD t = this.sat;
        
        // we calculate the fixed points
  //      BDD n =   model.getNormative();
  //      BDD Y = mod;
 //       BDD Y_old = null;
 //       BDD X = model.getFactory().zero();
 //       BDD X_old = null;
 //       BDD result = null;
 //       BDD notp = p.not().and(mod);
       // BDD result =
 //       while (!Y.biimp(Y_old).isOne()){
 //           Y_old = Y;
 //           while (!X.biimp(X_old).isOne()){
 //               X_old = X;
 //               X = n.and(t.and(WeakPrevious(Y)).or(s.and(WeakPrevious((notp.or(X)).and(Y)))));
 //           }
  //          Y = X.or(WeakPrevious(notp.and(Y))).and(n);
            
  //      }
        // we should test this!!!
  //      sat = X;
    }

	/**
	 * TBD
	 * @param args
	 */
	public void visit(PUX args) {
		// We calculate P(s U t -> Xp)
        // Semantics: SAT(P(sUt -> Xp)) = vY . (v X . (!s \/ <>(X^Y^!t))) \/ (<>p^Y)
		// FAir Semantics TBD
        
  //      int varNum = model.getFactory().varNum();
  //      BDD mod = model.getTransitions();
  //      mod = addExists(varNum/2, varNum, mod ,model.getFactory()); // obtain all states of the model.
        //TO DO: a function must be done to obtain the states of the model
  //      FormulaElement expr1 = args.getExpr1();
  //      expr1.accept(this);
  //      BDD s = this.sat;
  //      FormulaElement expr2 = args.getExpr2();
  //      expr2.accept(this);
  //      BDD t = this.sat;
  //      FormulaElement expr3 = args.getExpr3();
  //      expr3.accept(this);
  //      BDD p = this.sat;
        
        // we calculate the fixed points
  //      BDD n =   model.getNormative();
  //      BDD Y = mod;
  //      BDD Y_old = null;
  //      BDD X = model.getFactory().zero();
  //      BDD X_old = null;
  //      BDD nots = s.not().and(mod);
  //      BDD nott = t.not().and(mod);
        
  //      while(!Y.biimp(Y_old).isOne()){
  //          Y_old = Y;
  //          while (!X.biimp(X_old).isOne()){
  //              X_old = X;
  //              X = nots.or(WeakPrevious(X.and(Y).and(nott)));
  //          }
  //          Y = (X.and(n)).or(WeakPrevious(p.and(Y)));
  //      }
  //      sat = Y;
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
		// TBD
        
        
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
						Y = goals.get(i).and(X).and(n).or(EX(n.and(s.or(t).and(Y))));
					}
					goalConjunct1 = goalConjunct1.and(Y);
				}
				BDD goalConjunct2 = model.getFactory().one();
				for (int i = 0; i < goals.size(); i++){
					BDD Y = model.getFactory().zero();
					BDD Y_old = model.getFactory().one();
					while (!Y.biimp(Y_old).isOne()){
						Y_old = Y;
						Y = goals.get(i).and(Z).and(n).or(EX(n.and(s.or(t).and(Y))));
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
						Y = goals.get(i).and(X).and(n).or(EX(n.and(s.or(t).and(Y))));
					}
					goalConjunct1 = goalConjunct1.and(Y);
				}
				BDD goalConjunct2 = model.getFactory().one();
				for (int i = 0; i < goals.size(); i++){
					BDD Y = model.getFactory().zero();
					BDD Y_old = model.getFactory().one();
					while (!Y.biimp(Y_old).isOne()){
						Y_old = Y;
						Y = goals.get(i).and(Z).and(n).or(EX(n.and(s.or(t).and(Y))));
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
       // SAT version of these operators are not implemented in this stage
	}

	@Override
	public void visit(AXU a) {
       // TBD
    }

	@Override
	public void visit(AUX a) {
       // TBD
    }
    

	@Override
	public void visit(AUU a) {
        // TBD
        
	}

	@Override 
	public void visit(EXX e) {
		// TBD
	}

	@Override
	public void visit(EXU e) {
		// TBD

	}

	@Override
	public void visit(EUX e) {
		// TBD

	}

	@Override
	public void visit(EUU e) {
		// TBD

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
        
    	BDD result = Program.myFactory.zero();
        BDD result_old = Program.myFactory.one();
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

    /**
     * 
     * @param q
     * @return a BDD representing EF(q)
     */
    public BDD EF(BDD q){
    	BDD result = EUntil(Program.myFactory.one(), q);
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
            result = EX(result).and(p).or(q);          
        }
        return result;
    }
    
    /**
     * Fair Statement, it captures EGFair formula
     * saying that there is a fair path satisfying the goals
     * it uses Emerson characterization of fairness
     * @param	goals	the goals to be hold
     */
    private BDD fair(){
    	
        BDD result = model.getFactory().one();
        BDD result_old = model.getFactory().zero();
        while(!result.biimp(result_old).isOne()){
        	result_old = result;
        		BDD conjunct = model.getFactory().one();
        		for(int i = 0; i < goals.size(); i++){
        			conjunct = conjunct.and(EX(EUntil(model.getFactory().one(), result.and(goals.get(i)))));
        		}
        		result = conjunct;
        }
        return result;        
    }
    
    /**
     * Fair Statement, it captures AGFair formula
     * saying that there is a fair path satisfying the goals
     * @param	goals	the goals to be hold
     */
    private BDD allFair(){
    	
        BDD result = model.getFactory().one();
        BDD result_old = model.getFactory().zero();
        while(!result.biimp(result_old).isOne()){
        	result_old = result;
        		for(int i = 0; i < goals.size(); i++){
        			result = result.and(AX(AUntil(model.getFactory().one(), result.and(goals.get(i)))));
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

    	BDD result = fairEUntil(p,q).or(fairEG(p.and(q.not())));
        return result;
    }
    
    /**
     * Fair version of AX
     * @param p		the param of AX
     * @param goals	the goals to be	satisfied by the fairness requirement
     * @return	the BDD respresenting fairAX
     */
    public BDD fairAX(BDD p){
		BDD result = fairEX(p.not()).not();
		return result;	
    }
    
    /**
     * Fair version of AU
     * @param p		first operand of AU
     * @param q		second operand of AU
     * @param goals		the fairness foals
     * @return	the BDD respresenting the formula
     */
    public BDD fairAUntil(BDD p, BDD q){   	
		BDD result = fairEWuntil(q.not(), p.not().and(q.not())).not();
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
		BDD result = fairEUntil(q.not(), p.not().and(q.not())).not();
		return result;
    }
    
    /**
     * Fair version of EG
     * similar to fair 
     */
    public BDD fairEG(BDD p){
    	 BDD result = model.getFactory().one();
         BDD result_old = model.getFactory().zero();
         while(!result.biimp(result_old).isOne()){
         	result_old = result;
         		BDD conjunct = model.getFactory().one();
         		for(int i = 0; i < goals.size(); i++){
         			conjunct = conjunct.and(EX(EUntil(p, result.and(goals.get(i)))));
         		}
         		result = conjunct;
         }
         return result;   
    }
    
    
    
    /***
     * This implementation of EX uses early quantification, the idea is to avoid generating the
     * BDD of the complete program, instead we use each process BDD Ri, that is, for
     * calculating EXp, we use the following expression:
     *  \/Ri^p' 
     * @return returns the set of states that hold EXp
     * @param p: the first formula of EXp
     */
    private BDD EX(BDD p){
    	// for calculating the EX we use early quantification that is
    	// we calculate EX for each process and then we calculate the big or
    	
    	BDD result = Program.myFactory.zero();
    	int varNum = Program.myFactory.varNum(); 	
    	for (int i=0;  i < models.size(); i++){
    		BDDModel current = models.get(i);
    		   		
    		LinkedList<BDD> listBranches = current.getDisjuncts();   	
    		// we loop the branches    		
    		for (int j = 0; j < listBranches.size(); j++){
    			//get the BDD representation
    			BDD mod = listBranches.get(j);//.and(skipExternalVars);
    			// calculate the result with WeakPrevious
    			result = result.or(WeakPrevious(p, mod,current));
    		}
    	}    		  	
    	return result;       
    }

    /***
     * @return returns the set of states that hold AXp using early quantification
     * @param p: the first formula of AXp
     */
    private BDD AX(BDD p){
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
		
		// we remove the non-primed variables
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
	private BDD WeakPrevious(BDD b, BDD model, BDDModel bddmodel){
		
		
		// first we calculate the post
		int varNum = Program.myFactory.varNum();  
		BDD target = addExists(Program.myFactory.varNum()/2, varNum, b, Program.myFactory); 
		
		
		boolean r_primed = false;  //flag to indicate that we changed all variables v by v'
		BDDPairing pairs = makePairsToReplace(r_primed);    
		
		// we change in b all variables v by v'
		target = target.replaceWith(pairs); 
        
		// All the primed internal vars of the process are removed
		BDD result = addExists(bddmodel.getInternalPrimedVarsIds(), target.and(model));
        
		// The following code deals with external vars, any external var (say z)
		// must keep its value, that is z=z'. We avoid to calculate this by means of
		// subtitutions, each z is replaced by z', this does not affect the calculation
		// sin external vars are not restricted by the process's BDD.
        result = addExists(bddmodel.getExternalVarsIds(), result);     
        
       
        // We replace v -> v'
        r_primed = true;
        BDDPairing externalSubs = makePairsToReplaceFromList(bddmodel.getExternalVarsIds(), r_primed);          
        result = result.replaceWith(externalSubs);
       
        // primed vars are removed
        result = addExists(Program.myFactory.varNum()/2, varNum, result, Program.myFactory); 
       
        // the result
        return result;
        
	}
	
	/***
	 * 
	 * @return returns the set of previous states of the states "b", in which all successors are
	 * included in the states given as parameter.-
	 */
	private BDD StrongPrevious(BDD b, BDD model, BDDModel bddmodel){		
		//calculates the weak previous of not b
		BDD npre = WeakPrevious(b.not(), model, bddmodel); 
		// calculates the weak previous of b
        BDD pre = WeakPrevious(b, model, bddmodel); 
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

	/**
	 * 
	 * @param vars
	 * @param r_primed
	 * @return	A BDDPairing for replacing vars by primed vars or viceversa
	 */
	private BDDPairing makePairsToReplaceFromList(LinkedList<Integer> vars, boolean r_primed){
		BDDPairing pairs = Program.myFactory.makePair();
		int varNum = Program.myFactory.varNum()/2;
		if (r_primed){// changes v' by v
			for (int i=0; i < vars.size(); i++){
				int k = vars.get(i).intValue();
				pairs.set(varNum+k, k);
			}
		}
		else{// changes v by v'
			for (int i=0; i < vars.size(); i++){
				int k = vars.get(i).intValue();
				pairs.set(k, varNum+k);
			}
		}
		return pairs;
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
	
	/**
	 * 
	 * @param vars
	 * @param b
	 * @return	quantifies all the variables in var on expression b
	 */
	private BDD addExists(LinkedList<Integer> vars, BDD b){
		BDD var;
		for(int i=0; i < vars.size(); i++){		
			int k = vars.get(i).intValue();
			var = Program.myFactory.ithVar(k);
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
