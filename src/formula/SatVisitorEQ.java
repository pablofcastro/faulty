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
 *  In contrast to SatVisitor.java this class uses early quantification,
 *  that is hte BDD of the program is not build before the model checking process,
 *  but the individual processes are used, which in theory is better
 *   
 * 	@author Ceci and Pablo
 *
 */
public class SatVisitorEQ implements FormulaVisitor{

	private BDD sat;	// used for storing the result of the sat
	private BDDModel model; // the model of the complete program, it is computed only when needed
	private BDD norm; // A formula representing the normative part of the system
					 // this can be calculated without generating the BDD for all the system
	private BDD init; // the initial conditions, this can be calculated from the components, similar to norm
	private LinkedList<BDDModel> models; // the list of model of all the processes, the idea is to avoid generating
										 // the complete model as far as possible.
	private Program program;
	private HashMap<FormulaElement, BDD> mem; // we uew memoization for generating hte counterexamples


	/**
	 * Basic constructor
	 * @param	m	the model
	 */
	public SatVisitorEQ(Program program){
		this.sat = null;
		this.model = program.getModel();
		models = new LinkedList<BDDModel>();
		this.program = program;
		
		
		
		// we get the BDDs of the processes in the program
		models = program.buildPartialModels();
		BDD n = Program.myFactory.one();
		for (int i=0; i < models.size(); i++){
			BDDModel current = models.get(i);
			// we calculate the normative condition
			n = n.and(current.getNormative());			
		}
		
		norm = n;
	
		// we calculate the initial condition
		BDD i = Program.myFactory.one();
		for (int j=0; j < models.size(); j++){
			BDDModel current = models.get(j);
			// we calculate the normative condition
			i = i.and(current.getIni());			
		}
		init = i;
		mem = new HashMap<FormulaElement, BDD>();
	}
	

	/**
	 * Evaluates a logical variable
	 * @param 	v		the variable to be evaluated
	 */
	public void visit(Variable v) {
		
		String name = v.toString();
		int id = model.getVarID(name);	
		sat = Program.myFactory.ithVar(id);
		mem.put(v, sat);

	}

	/** 
	 * Returns the BDD zero or one, depending if the constant value is True or false, respectively.
	 * @param		c		the constant
	 * */

	public void visit(Constant c) {
		sat = c.getValue() ? Program.myFactory.one() : Program.myFactory.zero();
		mem.put(c, sat);
	}
	
	/**
	 * Calculates the negation of a formula
	 * @param n		the negation
	 */
	@Override
	public void visit(Negation n) {
		n.getExpr1().accept(this);		
		sat = this.getSat().not();
		mem.put(n, sat);
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
		mem.put(i, sat);
	}
    
    /**
	 *
	 * @param c     the comparison ==
	 */
	@Override
	public void visit(EqComparison e) {
        String sExpr1 = e.getNameExpr1();
        String sExpr2 = e.getNameExpr2();
        
        if(e.isTypeEnum()){
            
            String enumName = e.getEnumNameComparison();
            EnumType eType = this.program.getEnum(enumName);
            
            // var == var
            if(e.isVarExpr1() && e.isVarExpr2()){
                VarEnum varExpr1 = this.program.getVarEnum(sExpr1);
                VarEnum varExpr2 = this.program.getVarEnum(sExpr2);
                //System.out.println("--  var1 size == -- = " + varExpr1.getBitsSize() + "  == var2 size: " + varExpr2.getBitsSize() );
                
                
                for(int i = 0 ; i < varExpr1.getBitsSize();i++){
                    if (i==0){
                        sat = (varExpr1.getBit(i).biimp(varExpr2.getBit(i)));
                    }
                    else{// i >0
                        sat = sat.and(varExpr1.getBit(i).biimp(varExpr2.getBit(i)));
                    }
                    mem.put(e, sat);
                }
                
            }
            
            // constant == var
            if(!e.isVarExpr1() && e.isVarExpr2()){
                ConsEnum expr1 = eType.getConsEnum(sExpr1);
                VarEnum expr2 = this.program.getVarEnum(sExpr2);
                
                for(int i = 0 ; i < expr1.getBitsSize();i++){
                    if (i==0){
                        sat = (expr1.getBit(i).biimp(expr2.getBit(i)));
                    }
                    else{// i >0
                        sat = sat.and(expr1.getBit(i).biimp(expr2.getBit(i)));
                    }
                    mem.put(e, sat);
                }             
            }
            
            // var == constant
            if(e.isVarExpr1() && !e.isVarExpr2()){
            	VarEnum expr1 = this.program.getVarEnum(sExpr1);
                ConsEnum expr2 =eType.getConsEnum(sExpr2);
                for(int i = 0 ; i < expr1.getBitsSize();i++){
                    if (i==0){
                        sat = (expr1.getBit(i).biimp(expr2.getBit(i)));
                    }
                    else{// i >0
                        sat = sat.and(expr1.getBit(i).biimp(expr2.getBit(i)));
                    }
                    mem.put(e, sat);
                }                 	
            }
            
            // constant == constant
            if(!e.isVarExpr1() && !e.isVarExpr2()){
            	ConsEnum expr1 = eType.getConsEnum(sExpr1);
                ConsEnum expr2 =eType.getConsEnum(sExpr2);
                for(int i = 0 ; i < expr1.getBitsSize();i++){
                    if (i==0){
                        sat = (expr1.getBit(i).biimp(expr2.getBit(i)));
                    }
                    else{// i >0
                        sat = sat.and(expr1.getBit(i).biimp(expr2.getBit(i)));
                    }
                    mem.put(e, sat);
                }                      
            }
            
        }
        if(e.isTypeBool()){
            
            e.getExpr1().accept(this);
            BDD sat_expr1 = this.getSat();
            e.getExpr2().accept(this);
            BDD sat_expr2 = this.getSat();
            sat = sat_expr1.biimp(sat_expr2);
            mem.put(e, sat);
        }
    
        /*if(e.isTypeInt()){
        
        }*/
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
		mem.put(c, sat);
	
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
		mem.put(d, sat);
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
        // SAT(O(Xp->Xq)) = !E(n U n ^ EX(p ^ !q ^ E(n W false))))
        
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
        sat = EUntil(ns,ns.and(EX(p.and(nq).and(EX(EWuntil(ns, model.getFactory().zero())))))).not();
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
        sat = EUntil(ns, ns.and(q.and(EX(nots)).or(p.and(EX(nots.and(ns).and(EUntil(p.and(ns),q.and(EG(ns))))))))).not();
        
	}

	/**
	 * Implements the sat for formula O(pUq -> sUt)
	 * @param 	o	the formula
	 */
	@Override
	public void visit(OUU o) {
		// We calculate O(pUq -> sUt)
        // SAT(O(pUq->sUt)) = ! E(n U \phi) where:
        // \phi = E(n ^ p ^ !t ^ !s U n ^ q ^ (!s ^ !t \/ E(!t ^ n W n ^ !s ^ !t ^ EGn)))
		// Mu-Calculus semantics: TBD
		
	
        FormulaElement expr1 = o.getExpr1();
        expr1.accept(this);
        BDD s = this.sat;
        FormulaElement expr2 = o.getExpr2();
        expr2.accept(this);
        BDD t = this.sat;
        FormulaElement expr3 = o.getExpr3();
        expr3.accept(this);
        BDD p = this.sat;
        FormulaElement expr4 = o.getExpr4();
        expr4.accept(this);
        BDD q = this.sat;      
        BDD n =   norm;
        BDD phi = EWuntil(t.not().and(n), n.and(s.not()).and(t.not()).and(EG(n))); 
        sat = EUntil(n, EUntil(n.and(p).and(t.not()).and(s.not()), n.and(q.and((s.not().and(t.not())).or(phi))))).not(); 
	}
	
	/**
	 * Implements the sat for formula O(xp->sUt)
	 * @param 	o	the formula
	 */
	public void visit(OXW o){
		// We calculate O(Xp->sUt)
        // SAT(O(Xp->sWt)) = 
		// 			! E(n U (n ^ ((!t^!s^EXp V !t ^EX(p ^ E(!t ^ n U !s^!t ^ EGn)))))
        // Mu-calculus semantics: TBD
		
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
        BDD nots = s.not();//
        // get the normative states
        BDD ns = norm;
        sat = EUntil(ns, ns.and(q.and(EX(nots)).or(p.and(EX(nots.and(ns).and(EWuntil(p.and(ns),q.and(EG(ns))))))))).not();
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
		//							^
								
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
		BDD nt = t.not();
		BDD ns = s.not();
		BDD n =   norm;
		BDD alln = EG(n);       
		
		BDD phi1 = EUntil(nt.and(n), ns.and(nt).and(alln));
		BDD phi2 = EUntil(p.and(nt).and(n), q.and(nt).and(EUntil(nt.and(n), nt.and(ns).and(alln))));
		BDD phi3 = EUntil(p.and(nt).and(n), nt.and(ns).and(EUntil(p.and(n), q.and(alln))));
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
		//Semantics O(sWt) = !E(n U n ^ E(!t ^ n U !s ^ EGn)) 
		// fixed point semantics: O(sWt) = !(u Y . f \/ n ^ EX(Y))
		// where: f = uX . (!s ^EGn \/ !t ^ n ^ EX(X)) 
				
		// start obtaining the BDDs of the subexpressions	
		FormulaElement expr1 = o.getExpr1();
		expr1.accept(this);
		BDD s = this.sat;//.and(mod);
		FormulaElement expr2 = o.getExpr2();		
		expr2.accept(this);
		BDD t = this.sat;//.and(mod);
		// OLD semantics commente
		// we calculate the result using its CTL semantics
		// the negations are calculated taking into account the model
		//BDD nots = s.not();//.and(mod);
		//BDD nott = t.not();//.and(mod);		
		// get the normative states
		BDD ns = norm;		
		//EUntil(ns, ns.and(nots).and(nott).or(nott.and(EUntil(nott.and(ns),nots.and(nott).and(EG(ns)))))).printSet();
	//	sat = EUntil(ns, ns.and(EUntil(nott.and(ns),nots.and(EG(ns))))).not();
		
		BDD aux = EG(ns);
		BDD X = Program.myFactory.zero();
		BDD X_old = Program.myFactory.one();
		while (!X.biimp(X_old).isOne()){
			X_old = X;
			X = s.not().and(t.not()).and(aux).or(t.not().and(ns).and(EX(X)));
		}
		BDD Y = X;
		BDD Y_old = Program.myFactory.one();
		while (!Y.biimp(Y_old).isOne()){
			Y_old = Y;
			Y = X.or(ns.and(EX(Y)));
		}
		sat = Y.not();		
	}
	
	/**
	 * 
	 * Calculates formula O(pUq), it can be calculated with the function above, but by
	 * efficency reasons we include an implementation
	 * @param o		the formula
	 */
	public void visit(OU o){

		// OLD Semantics  O(sUt) = !E(n U n ^ ((!s ^!t) v (!t ^ E(!t ^ n W n ^ !s ^EG(n) )))))
		// FixPoint Semantics = ! (u Y . (v X . (EG(n) ^ !s ^ !t) \/ (!t ^ n ^ <>X) \/ n ^ <>Y ))
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
        // set of states
		BDD X = alln.and(s.not()).and(t.not()); //EG(norm);
		BDD X_old = Program.myFactory.zero();	
		//s.not().and(t.not()).printSet();
		////BDD alln = EG(norm);
		while (!X.biimp(X_old).isOne()){
			X_old = X;				
			X = s.not().and(t.not()).and(alln).or(norm.and(t.not().and(EX(X))));
		}
		
		BDD Y = X;
		BDD Y_old = norm;
		while (!Y.biimp(Y_old).isOne()){
			Y_old = Y;
			Y = X.or(norm.and(EX(Y)));
		}
		sat = Y.not();
            
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
		// Semantics P(sUt) =  vX . n^(uY.(t^<>X) v s ^ <> n  ^ Y)
		// Improved semantics: 
		// \phi = u X . (t ^ n) \/ (p^n <> X)
		// P(sUt) = vY. \phi ^ <>Y
		
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
		
		//BDD X =Program.myFactory.one();
		
		//BDD X_old = Program.myFactory.zero();
		//while (!X.biimp(X_old).isOne()){
		//	X_old = X;
		//	BDD Y = Program.myFactory.zero();
		//	BDD Y_old = Program.myFactory.one();
		//	while (!Y.biimp(Y_old).isOne()){
		//		Y_old = Y;
				//Y = t.and(X).or(s.and(NWeakPrevious(n.and(Y))));
		//		Y = t.and(X).or(s.and(EX(Y).and(norm)));
		//	}
		//	X = norm.and(Y);
		//}
		//sat = X;
		
		
		//BDD X = norm;//Program.myFactory.one();
		//BDD X = model.getFactory().zero();
		//BDD X_old = Program.myFactory.zero();
		//while (!X.biimp(X_old).isOne()){
		//	X_old = X;
		//	BDD Y = t.and(EX(X));//Program.myFactory.zero();
		//	BDD Y_old = Program.myFactory.one();
		//	while (!Y.biimp(Y_old).isOne()){
		//		Y_old = Y;
		//		Y = Y.or(s.and(EX(Y).and(norm)));
		//	}
		//	X = norm.and(Y);
		//}
		
		BDD X = Program.myFactory.zero();
		BDD X_old = norm;
		while (!X.biimp(X_old).isOne()){
			X_old = X;
			X = t.and(norm).or(s.and(norm).and(EX(X)));
		}
		//sat = X;
		
		BDD Y = norm;
		BDD Y_old = Program.myFactory.zero();
		while (!(Y.biimp(Y_old).isOne())){
			Y_old = Y;
			Y= X.and(EX(Y));	
		}
		sat = Y;
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
	
	public void visit(Recovery r){
		//TBD
	}
	
	public void visit(RX r){
		// TBD
	}
	
	public void visit(RU r){
		// TBD
		// Semantics: R(pUq) = vX . (n^[]X) \/ (!n ^ (uY. (q^[]X \/ p ^ []Y)))
		FormulaElement expr1 = r.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		
		FormulaElement expr2 = r.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		
		BDD X = Program.myFactory.one();
		BDD X_old = Program.myFactory.zero();
		
		while(!X.biimp(X_old).isOne()){
			X_old = X;
			BDD Y = Program.myFactory.zero();
			BDD Y_old = Program.myFactory.one();
			while (!Y.biimp(Y_old).isOne()){
				Y_old = Y;
				Y = q.and(AX(X)).or(p.and(AX(Y)));
			}
			X = norm.and(AX(X)).or(norm.not().and(Y));
		}
		
		sat= X;
	}
	
	public void visit(RW r){
		// TBD
		// Semantics: R(pWq) = vX . (n^[]X) \/ (!n ^ (vY. (q^[]X \/ p^[]Y)))
		FormulaElement expr1 = r.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		
		FormulaElement expr2 = r.getExpr2();
		expr2.accept(this);
		BDD q = this.sat;
		
		BDD X = Program.myFactory.one();
		BDD X_old = Program.myFactory.zero();
		
		while(!X.biimp(X_old).isOne()){
			X_old = X;
			BDD Y = Program.myFactory.one();
			BDD Y_old = Program.myFactory.zero();
			while (!Y.biimp(Y_old).isOne()){
				Y_old = Y;
				Y = q.and(AX(X)).or(p.and(AX(Y)));
			}
			X = norm.and(AX(X)).or(norm.not().and(Y));
		}
		
		sat= X;
	}
	
	public void visit(AX a){
		FormulaElement expr1 = a.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		sat = AX(p);
		mem.put(a, sat);
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
		mem.put(a, sat);
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
		mem.put(a, sat);
	}
	
	/**
	 * Computes the formula EXp
	 * @param e		the formula
	 */
	public void visit(EX e){
		FormulaElement expr1 = e.getExpr1();
		expr1.accept(this);
		BDD p = this.sat;
		//p.printSet();
		
		sat = EX(p);
		mem.put(e, sat);
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
		mem.put(e, sat);
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
		mem.put(e, sat);
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

    
    
    @Override
	public void visit(RUU e) {
    	// CTL* Semantics: R(pUq -> sUt) = A(G!n ->(pUq -> sUt)) 
        // u-calculus semantics:
    	// R(pUq -> sUt) = !(u X . phi \/ <> X)
    	// where: 
    	// phi = !n ^  uY.(q ^ (!s \/ E(!t W !s))) \/ p ^ !t ^ <>Y \/ !s ^ E(pUq)
    	// obviously all the CTL formulas there can be calculated with fixed points.
    	
    	
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
         
         // we calculate the subformula phi first
         // some auxiliar subformulas
         BDD ew_nott_nots = EWuntil(t.not(), s.not());
         BDD eu_p_q = EUntil(p, q);
         
         BDD Y = Program.myFactory.zero();
         BDD Y_old = Program.myFactory.one();
         while (! Y.biimp(Y_old).isOne()){
        	 Y_old = Y;
        	 Y = q.and(s.not().or(ew_nott_nots)).or(p.and(t.not().and(EX(Y)))).or(s.not().and(eu_p_q));
         }
    	 sat = EF(norm.not().and(Y)).not();
    }
    
    
    @Override
	public void visit(RWW e) {
    	// CTL* Semantics: R(pWq->sWt) = A(G(!n -> (pWq -> sWt)))
    	// u-calculus semantics: !(uX . phi \/ <>X)
    	// where:
    	// phi = !n ^ uX . q ^ (!s \/ E(!tU!s)) \/ !t^p^<>X \/ !s^E(pWq) 
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
        
        // let us calculate phi and some useful subformulas
        BDD eu_nott_nots = EUntil(t.not(), s.not());
        BDD ew_p_q = EWuntil(p, q);
        BDD Y = Program.myFactory.zero();
        BDD Y_old = Program.myFactory.one();
        while (! Y.biimp(Y_old).isOne()){
        	Y_old = Y;
        	Y = q.and(s.not().or(eu_nott_nots)).or(t.not().and(p).and(EX(Y)).or(s.not().and(ew_p_q)));
        }
        sat = EF(norm.not().and(Y)).not();		
    }
    
    @Override
	public void visit(RWU e) {
    	// Semantics: R(pWq -> sUt) = A(G(!n -> (pWq -> sUt))) = !EF(!n ^ )
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
            
        //System.out.println(t);
        // we calculate phi and some auxiliar subformulas
        BDD ew_nott_nots = EWuntil(t.not(), s.not().and(t.not()));    
        //BDD ew_p_q = EWuntil(p,q);
        BDD Y = Program.myFactory.one();
        BDD Y_old = Program.myFactory.zero();
        
        while (! Y.biimp(Y_old).isOne()){              	
        	Y_old = Y;
            Y = q.and(ew_nott_nots).or(t.not().and(p).and(EX(Y)));//.or(s.not().and(t.not()).and(ew_p_q)));   
        }
        
        // we calculate the reachable states from the init
        BDD reach = init;
        BDD reach_old = Program.myFactory.one();
        while (! reach.biimp(reach_old).isOne()){
        	reach_old = reach;
        	reach = reach.or(Post(reach));
        }
        
        // the state to avoid are those do not satisfying pWq->sUt
        BDD avoid = norm.not().and(Y);
       
        // sat is true when the intersection between reach and avoid is empty
        sat = reach.and(avoid).biimp(Program.myFactory.zero());	
        
        		
        		
        		
        //sat = EF(norm.not().and(Y)).not();	
        //sat = AG(Y.not());		
        //sat = EF(norm.not().and(EWuntil(p.and(t.not()), q.and(EWuntil(t.not(), s.not().and(t.not())))).or(EWuntil(p.and(t.not()), s.not().and(t.not()).and(EWuntil(p,q)))))).not();      
        //sat = EUntil(Program.myFactory.one(), (norm.not().and(EG(p.and(t.not()))))).not();
    }
    
    @Override
	public void visit(RWX e) {
    	// Semantics R(pWq -> X!s) = A(G(!n -> (pWq -> X!p)))
    	// u-calculus: R(pWq -> X!s) = !(u X. phi \/ <>X)
    	// where:
    	// phi = !n ^ ((q ^ EX(!p)) \/ p ^ EX(!s^E(pWq)) 
    	
    	FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        
        BDD ew_p_q = EWuntil(p,q);
        sat = EF(norm.not().and(q.and(EX(p.not()).or(p.and(EX(s.not().and(ew_p_q))))))).not();
    }
    
    
    @Override
	public void visit(RUW e) {
    	// CTL* semantics: A(G(!n ->(pUq -> sWt))) 
    	// u-calculus semantics: !u X.(phi \/ <>X)
    	// where:
    	// phi = !n ^ u Y. (q ^ (!s \/ E(!t U !s))) \/ (!s ^ E(p U q)) \/ !t^p^<>Y) 
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
        
        // we calculate phi
        // some auxiliar subformulas
        BDD eu_nott_nots = EUntil(t.not(), s.not());
        BDD eu_p_q = EUntil(p,q);
        BDD Y = Program.myFactory.zero();
        BDD Y_old = Program.myFactory.one();
        while (!(Y.biimp(Y_old).isOne())){
        	Y_old = Y;
        	Y = q.and(s.not().or(eu_nott_nots)).or(s.not().and(eu_p_q)).or(t.not().and(p).and(EX(Y)));
        }
    	sat = EF(Y.and(norm.not())).not();
    }
    
    
    
    @Override
	public void visit(RUX e) {
    	FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD s = this.sat;
        
        BDD eu_p_q = EUntil(p,q);
        sat = EF(norm.not().and(q.and(EX(p.not()).or(p.and(EX(s.not().and(eu_p_q))))))).not();
    	
    }
    
    
    @Override
	public void visit(RXU e) {
        // Semantics: R(Xp -> sUt) = A(G(!n-> (Xp -> sUt)))
    	// u-calculus Semantics: R(Xp -> sUt) = !EF(!n ^ (!s^ EXp) \/ (!t ^ EX(p ^ E(!t W !s))
    	FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;
        
        BDD ew_t_s = EWuntil(t.not(),s.not());
        sat = EF(norm.not().and(t).and(EX(p)).and(t.not().and(EX(p.and(ew_t_s))))).not();    	
    }
    
    
    @Override
	public void visit(RXX e) {
    	// CTL* Semantics: R(Xp -> Xq) = A(G(!n ->(Xp -> Xq))) 
    	// CTL semantics:
        // R(Xp -> Xq) = !EF(!n ^ EX(p^!q))
    	FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD q = this.sat;
        
        sat = EF(norm.not().and(EX(p.and(q.not())))).not();
        
    }
    
    
    @Override
	public void visit(RXW e) {
    	// Semantics: R(Xp -> sWt) = A(G(!n-> (Xp -> sWt)))
    	// u-calculus Semantics: R(Xp -> sWt) = !EF(!n ^ (!s^ EXp) \/ (!t ^ EX(p ^ E(!t U !s))
    	
    	FormulaElement expr1 = e.getExpr1();
        expr1.accept(this);
        BDD p = this.sat;
        FormulaElement expr2 = e.getExpr2();
        expr2.accept(this);
        BDD s = this.sat;
        FormulaElement expr3 = e.getExpr3();
        expr3.accept(this);
        BDD t = this.sat;
        
        BDD eu_t_s = EUntil(t.not(),s.not());
        sat = EF(norm.not().and(t).and(EX(p)).and(t.not().and(EX(p.and(eu_t_s))))).not();  
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
    
    /**
     * A simple method to generate counterexamples
     * @pre 	the formula must be in NNF
     * @param 	f	the formula to which we generate the conuterexample
     * @param 	actualState	the actual state, at beginning it must be the initial state
     * @return	true if a trace was found, false othwerwise
     */
    public boolean generateWitness(FormulaElement f, BDD actualState, LinkedList<BDD> previous){
    	//LinkedList<BDD> result = new LinkedList<BDD>();
    	if (f instanceof Variable){
    		//f.accept(this);
    		//BDD p = this.sat;
    		BDD p = this.mem.get(f);
    		if (p.and(actualState).satCount()>0){
    			previous.addLast(p.and(actualState));
    			return true;
    		}
    		return false;
    	}
    	if (f instanceof Conjunction){
    		FormulaElement f1 = ((Conjunction) f).getExpr1();
			FormulaElement f2 = ((Conjunction) f).getExpr2();
			BDD p = this.mem.get(f1);
			BDD q = this.mem.get(f2);
			if (p.and(actualState).satCount()>0 && q.and(actualState).satCount()>0){
				// in this case we just return the actual state, since  p and 1 could be witnessed by differents traces
				previous.addLast(actualState);
				return true;
			}
			return false;
    	}
    	if (f instanceof Disjunction){
    		FormulaElement p = ((Disjunction) f).getExpr1();
			FormulaElement q = ((Disjunction) f).getExpr2();
			LinkedList<BDD> previousQ = new LinkedList<BDD>();
			LinkedList<BDD> previousP = new LinkedList<BDD>();
			// in this case we return some of them
			if (generateWitness(p, actualState, previousP)){
				previous.addAll(previousP);
				return true;
			}
			if (generateWitness(q, actualState, previousQ)){
				previous.addAll(previousQ);
				return true;
			}
			return false;
    	}
    	if (f instanceof Negation){
    		// the formula must be in NNF then the unique case is given by the negation of a var
    		BDD p = this.mem.get(f);
        	previous.addLast(p.and(actualState));
        	return true;	
    	}
    	if (f instanceof AX){
    		//((AX) f).getExpr1().accept(this);
    		//BDD next = this.sat;
    		
    		// we obtain the states satisfying p where AXp is the actual formula
    		BDD next = this.mem.get(((AX) f).getExpr1());
    		
    		// we inspect all the processes
    		for (int i=0; i<this.models.size();i++){
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			BDDModel bddmodel = models.get(i);
    			
    			// and the branches of each process
    			for (int j=0; j<branches.size();j++){
    				
    				// we remove the primed variables
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){ // for those states satisfying the guard
    					
    					// the following lines obtain the command corresponding to the actual branch
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    					// if the actual state satisfies the guard and the nest hte command then we return the cex
    					if (guard.and(actualState).satCount()>0 && command.and(next).satCount()>0){
    						previous.addLast(actualState);	
    						previous.addLast(command.and(next));
    						return true;
    					}
    				}
    			}
    		}
    		return false;	
    	}
    	if (f instanceof EX){
    		// it is similar to the above case
    		BDD next = this.mem.get(((EX) f).getExpr1());
    		
    		//LinkedList<BDD> succ = generateCounterExamples(f.expr1());
    		for (int i=0; i<this.models.size();i++){
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			BDDModel bddmodel = models.get(i);
    			for (int j=0; j<branches.size();j++){
    				// we remove the primed variables
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
        					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
        					command = addExists(bddmodel.getInternalVarsIds(), command);
        					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
        					command = command.replaceWith(internalSubs);
        					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    					
    					if (guard.and(actualState).satCount()>0 && command.and(next).satCount()>0){
    						previous.addLast(actualState);	
    						previous.addLast(command.and(next));
    						return true;
    					}
    				}
    			}
    		}
    		return false;
    	}
    	if (f instanceof AU){
    		BDD p = this.mem.get(((AU) f).getExpr1());
    		BDD q = this.mem.get(((AU) f).getExpr2());
    		BDD puq = this.mem.get(f);
    		
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				// we obtain the guard
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);
    				// if the actual state satisfies the guard
    				if (guard.and(actualState).satCount()>0){
    					
    					// we obtain the command
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			    
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState.and(q));
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						k++;
    					}
    					// if no cycle it is a candidate   				
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(guard.and(actualState));	
    						boolean r = generateWitness(f, command, previous);
    						if (r)
    							return true;
    						previous.removeLast(); // the added node is removed
    					}	
    				}
    			}
    		}
    		return false; // no path found!
    	}
    	if (f instanceof AW){
    		//((AW) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((AW) f).accept(this);
    		//BDD puq = this.sat;
    		
    		BDD q = this.mem.get(((AW) f).getExpr2());
    		//System.out.println(this.mem.get(f));
    		BDD puq = this.mem.get(f);
    		
    		
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			   	
    					// if q holds we are done
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState.and(q));
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						k++;
    					}
    					if (cycle){
    						previous.addLast(command); // in this case we obtain a cycle and the property holds in the cycle
    						return true;
    					}
    					// if no cycle it is a candidate
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(guard.and(actualState));	
    						boolean r = generateWitness(f, command, previous);
    						if (r)
    							return true;
    						else
    							previous.removeLast();
    					}	
    				}
    			}
    			
    		}
    		return false;	
    	}
    	if (f instanceof EU){
    		//((EU) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((EU) f).accept(this);
    		//BDD puq = this.sat;
    		BDD q = this.mem.get(((EU) f).getExpr2());
    		BDD puq = this.mem.get(f);
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);
    				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			    
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState.and(q));
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						if (cycle)
    							break;
    						k++;
    					}
    					// if no cycle it is a candidate   				
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(guard.and(actualState));	
    						boolean r = generateWitness(f, command, previous);
    						if (r)
    							return true;
    						previous.removeLast(); // the added node is removed
    					}	
    				}
    			}
    		}
    		return false; // no path found!
    	}
    	if (f instanceof EW){
    		//((EW) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((EW) f).accept(this);
    		//BDD puq = this.sat;
    		
    		BDD q = this.mem.get(((EW) f).getExpr2());
    		BDD puq = this.mem.get(f);
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    								
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    					// if q holds we are done
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState.and(q));
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					BDD endOfCycle = Program.myFactory.zero();
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						if (cycle)
    							endOfCycle = previous.get(k).and(command);
    						k++;
    					}
    					if (cycle){
    						previous.addLast(actualState.and(guard));
    						previous.addLast(endOfCycle); // in this case we obtain a cycle and the property holds in the cycle
    						return true;
    					}
    					// if no cycle it is a candidate
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(guard.and(actualState));	
    						boolean r = generateWitness(f, command, previous);
    						if (r)
    							return true;
    						else
    							previous.removeLast();
    					}	
    				}
    			}
    			
    		}
    		return false;
    	}
    	else{
    		return false;
    	}
    }
    
    /**
     * A bounded version of genrateWitness, 
     * @param 	f	the formula to which we generate the conuterexample
     * @param 	actualState	the actual state, at beginning it must be the initial state
     * @param   the maximum size of the witness
     * @return	true if a trace was found, false othwerwise
     */
    public boolean generateBoundedWitness(FormulaElement f, BDD actualState, LinkedList<BDD> previous, int n){
    	//LinkedList<BDD> result = new LinkedList<BDD>();
    	if (n==0) // base case witnesses of size 0 do not exists
    		return false;
    	if (f instanceof Variable){
    		//f.accept(this);
    		//BDD p = this.sat;
    		BDD p = this.mem.get(f);
    		//previous.addAll(c);
    		if (p.and(actualState).satCount()>0){
    			previous.addLast(p.and(actualState));
    			return true;
    		}
    		return false;
    	}
    	if (f instanceof Conjunction){
    		
    	}
    	if (f instanceof Disjunction){
    		FormulaElement p = ((Disjunction) f).getExpr1();
			FormulaElement q = ((Disjunction) f).getExpr2();
			LinkedList<BDD> previousQ = new LinkedList<BDD>();
			LinkedList<BDD> previousP = new LinkedList<BDD>();
			if (generateBoundedWitness(p, actualState, previousP, n)){
				previous.addAll(previousP);
				return true;
			}
			if (generateBoundedWitness(q, actualState, previousQ, n)){
				previous.addAll(previousQ);
				return true;
			}
			return false;
    	}
    	if (f instanceof Negation){
    		// hte idea is to push negations inside
    		FormulaElement f1 = ((Negation) f).getExpr1();
    		if (f1 instanceof Variable){
    			//f.accept(this);
        		//BDD p = this.sat;
    			BDD p = this.mem.get(f);
        		previous.addLast(p.and(actualState));
        		return true;
    		}
    		if (f1 instanceof Conjunction){
    			return generateBoundedWitness(new Disjunction("|",new Negation("!", ((Conjunction) f1).getExpr1()), new Negation("!", ((Conjunction) f1).getExpr2())), actualState, previous, n);
    		}
    		if (f1 instanceof Disjunction){
    			return generateBoundedWitness(new Conjunction("&",new Negation("!", ((Disjunction) f1).getExpr1()), new Negation("!", ((Disjunction) f1).getExpr2())), actualState, previous, n);
    		}
    		if (f1 instanceof Negation){
    			// if negation of a negation, we remove the two
    			return generateBoundedWitness(((Negation) f1).getExpr1(), actualState, previous, n);
    		}
    		if (f1 instanceof EX){
    			return generateBoundedWitness(new AX("AX",new Negation("!",((EX) f).getExpr1())), actualState, previous, n);
    		}
    		if (f1 instanceof AX){
    			return generateBoundedWitness(new EX("EX",new Negation("!",((EX) f).getExpr1())), actualState, previous, n);
    		}
    		if (f1 instanceof AU){
    			// assume f = A(p U q)
    			FormulaElement p = ((AU) f1).getExpr1();
    			FormulaElement q = ((AU) f1).getExpr2();
    			return generateBoundedWitness(new EW("EW",new Negation("!",q), new Conjunction("&",new Negation("!",p), new Negation("!",q))), actualState, previous, n);
    		}
    		if (f1 instanceof EU){
    			// assume f = A(p U q)
    			FormulaElement p = ((EU) f1).getExpr1();
    			FormulaElement q = ((EU) f1).getExpr2();
    			return generateBoundedWitness(new AW("AW", new Negation("!",q), new Conjunction("&",new Negation("!",p), new Negation("!",q))), actualState, previous, n);
    		}
    		if (f1 instanceof EW){
    			// assume f = A(p U q)
    			FormulaElement p = ((EW) f1).getExpr1();
    			FormulaElement q = ((EW) f1).getExpr2();
    			return generateBoundedWitness(new AU("AU",new Negation("!",q), new Conjunction("&",new Negation("!",p), new Negation("!",q))), actualState, previous, n);
    		}
    		if (f1 instanceof AW){
    			// assume f = A(p U q)
    			FormulaElement p = ((AW) f1).getExpr1();
    			FormulaElement q = ((AW) f1).getExpr2();
    			return generateBoundedWitness(new EU("EU",new Negation("!",q), new Conjunction("&",new Negation("!",p), new Negation("!",q))), actualState, previous, n);
    		}	
    	}
    	if (f instanceof AX){
    		//((AX) f).getExpr1().accept(this);
    		//BDD next = this.sat;
    		BDD next = this.mem.get(((AX) f).getExpr1());
    		//LinkedList<BDD> succ = generateCounterExamples(f.expr1());
    		for (int i=0; i<this.models.size();i++){
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			BDDModel bddmodel = models.get(i);
    			for (int j=0; j<branches.size();j++){
    				// we remove the non-primed variables
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    					if (guard.and(actualState).satCount()>0 && command.and(next).satCount()>0){
    						previous.addLast(actualState);	
    						previous.addLast(next);
    						return true;
    					}
    				}
    			}
    		}
    		return false;	
    	}
    	if (f instanceof EX){
    		//((EX) f).getExpr1().accept(this);
    		//BDD next = this.sat;
    		BDD next = this.mem.get(((EX) f).getExpr1());
    		
    		//LinkedList<BDD> succ = generateCounterExamples(f.expr1());
    		for (int i=0; i<this.models.size();i++){
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			BDDModel bddmodel = models.get(i);
    			for (int j=0; j<branches.size();j++){
    				// we remove the non-primed variables
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    					if (guard.and(actualState).satCount()>0 && command.and(next).satCount()>0){
    						previous.addLast(actualState);	
    						previous.addLast(next);
    						return true;
    					}
    				}
    			}
    		}
    		return false;
    	}
    	if (f instanceof AU){
    		//BDD p = this.accept(f.getExpr1());
    		//((AU) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((AU) f).accept(this);
    		//BDD puq = this.sat;
    		BDD p = this.mem.get(((AU) f).getExpr1());
    		BDD q = this.mem.get(((AU) f).getExpr2());
    		BDD puq = this.mem.get(f);
    		
    		
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);
    				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			    
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState);
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						k++;
    					}
    					// if no cycle it is a candidate   				
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(actualState);	
    						boolean r = generateBoundedWitness(f, command, previous, n-1);
    						if (r)
    							return true;
    						previous.removeLast(); // the added node is removed
    					}	
    				}
    			}
    		}
    		return false; // no path found!
    	}
    	if (f instanceof AW){
    		//((AW) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((AW) f).accept(this);
    		//BDD puq = this.sat;
    		
    		BDD q = this.mem.get(((AW) f).getExpr2());
    		//System.out.println(this.mem.get(f));
    		BDD puq = this.mem.get(f);
    		
    		
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			   	
    					// if q holds we are done
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState);
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						k++;
    					}
    					if (cycle){
    						previous.addLast(command); // in this case we obtain a cycle and the property holds in the cycle
    						return true;
    					}
    					// if no cycle it is a candidate
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(actualState);	
    						boolean r = generateBoundedWitness(f, command, previous, n-1);
    						if (r)
    							return true;
    						else
    							previous.removeLast();
    					}	
    				}
    			}
    			
    		}
    		return false;	
    	}
    	if (f instanceof EU){
    		//((EU) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((EU) f).accept(this);
    		//BDD puq = this.sat;
    		BDD q = this.mem.get(((EU) f).getExpr2());
    		BDD puq = this.mem.get(f);
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);
    				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			    
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState);
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						if (cycle)
    							break;
    						k++;
    					}
    					// if no cycle it is a candidate   				
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(actualState);	
    						boolean r = generateBoundedWitness(f, command, previous, n-1);
    						if (r)
    							return true;
    						previous.removeLast(); // the added node is removed
    					}	
    				}
    			}
    		}
    		return false; // no path found!
    	}
    	if (f instanceof EW){
    		//((EW) f).getExpr2().accept(this);
    		//BDD q = this.sat;
    		//((EW) f).accept(this);
    		//BDD puq = this.sat;
    		
    		BDD q = this.mem.get(((EW) f).getExpr2());
    		BDD puq = this.mem.get(f);
    		for (int i=0; i<this.models.size();i++){
    			BDDModel bddmodel = models.get(i);
    			LinkedList<BDD> branches = this.models.get(i).getDisjuncts();
    			for (int j=0; j<branches.size();j++){
    				BDD guard = addExists(Program.myFactory.varNum()/2, Program.myFactory.varNum(), branches.get(j), Program.myFactory);				
    				if (guard.and(actualState).satCount()>0){
    					BDD command = addExists(bddmodel.getExternalPrimedVarsIds(), branches.get(j).and(actualState));
    					command = addExists(bddmodel.getInternalVarsIds(), command);
    					BDDPairing internalSubs = makePairsToReplaceFromList(bddmodel.getInternalVarsIds(), true); 
    					command = command.replaceWith(internalSubs);
    					command = addExists(Program.myFactory.varNum()/2,Program.myFactory.varNum(), command, Program.myFactory);
    			   	
    					// if q holds we are done
    					if (actualState.and(q).satCount()>0){
    						previous.addLast(actualState);
    						return true;
    					}
    					// otherwise, first we have to avoid cycles
    					boolean cycle = actualState.and(command).satCount() >0;
    					int k = 0;
    					while ( k < previous.size() && !cycle){
    						cycle = previous.get(k).and(command).satCount() >0;
    						k++;
    					}
    					if (cycle){
    						previous.addLast(actualState);
    						previous.addLast(command); // in this case we obtain a cycle and the property holds in the cycle
    						return true;
    					}
    					// if no cycle it is a candidate
    					if (!cycle && guard.and(actualState).satCount()>0 && command.and(puq).satCount() > 0){
    						previous.addLast(actualState);	
    						boolean r = generateBoundedWitness(f, command, previous, n-1);
    						if (r)
    							return true;
    						else
    							previous.removeLast();
    					}	
    				}
    			}
    			
    		}
    		return false;
    	}
    	else{
    		return false;
    	}
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
    			result = result.or(WeakPrevious(p, mod, current));
    		}
    	}    		  	
    	return result;       
    }
    
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
    
    /**
     * Calculates Post(s), where s represents a set of states
     * @param s
     * @return
     */
    private BDD Post(BDD s){
    	// NEED TO BE TESTED
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
    			result = result.or(Post(s, mod));
    		}
    	}    		  	
    	return result;  
    }
    
	/**
	 * Calculates Post(s, model, bddmodel) for a specific bddmodel
	 * @param s
	 * @param model
	 * @param bddmodel
	 * @return
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
		// since external vars are not restricted by the process's BDD.
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

		if(r_primed){// creates pairs to change variables v' by v , i.e. v -> v'		
			for (int i=0; i<varNum; i++){
				pairs.set((varNum)+i, i);		
			}   
		}
		else{// creates pairs to change variables v by v',  i.e. v' -> v		
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
