import java.util.*;


import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;

public class TestFairness{

	static BDDFactory B1;
	static boolean TRACE;
	static BDD p;	
	static BDD p_;	
	static BDD q;	
	static BDD q_;
	static BDD initial; 
	static BDD transitions;
	static BDD normal;
	 
	static int varNum = 2;


	/*//////////////////////////////// AUXILIAR Methods //////////////////////////////////////////////////////*/    

	/**
	 * Initialize a BDDFactory with the number of variables received as parameter.
	 */
	private static BDDFactory initializeBDDFactory(int vn){
		System.out.println("varNum:" + vn);
		BDDFactory B;
		/* Initialize with reasonable nodes and cache size and Nx2 variables */
		int numberOfNodes =  (int) (Math.pow(4.42, (vn*2)-6 ))*1000; 	
		System.out.println("primer nro nodos:" + numberOfNodes);
		int cacheSize = 2000;	        
		numberOfNodes = Math.max(1000, numberOfNodes);
		System.out.println("nodos:" + numberOfNodes);
		B = BDDFactory.init(numberOfNodes, cacheSize);

		if (B.varNum() < (vn*2)) B.setVarNum((vn*2));
		System.out.println("factory varnum:" + B.varNum());
		return B;
	}    
	
	/**
	 * Create a model ad hoc.
	 * @return
	 */
	private static BDD makeModel(){
		
		B1= initializeBDDFactory(varNum); 
		System.out.println("B1 varnum:" + B1.varNum());
		
		p = B1.ithVar(0); 
		q = B1.ithVar(1);
		//r = B1.ithVar(2);
		//s = B1.ithVar(3);
		
		//primed variables
		p_ = B1.ithVar(2); 
		q_ = B1.ithVar(3);
		//r_ = B1.ithVar(6);
		//s_ = B1.ithVar(7);
		
		// Model:
		// t2: 00->01
		// t3: 00->10
		// t4: 10->11
		// t5: 10->10
		// t6: 11->10
		// t7: 01->01
	
		
		initial = (p.not()).and(q.not());
		
		normal = p;
		
		BDD res = null;

		
		BDD t2 = p.not().and(q.not()).and(p_.not()).and(q_); // 00->01
		BDD t3 = p.not().and(q.not()).and(p_).and(q_.not()); // 00->10
		BDD t4 = p.and(q.not()).and(p_).and(q_); // 10 ->11
		BDD t5 = p.and(q.not()).and(p_).and(q_.not()); // 10 -> 10
		BDD t6 = p.and(q).and(p_).and(q_.not()); // 11->10
		BDD t7 = p.not().and(q).and(p_.not()).and(q_); // 01->01
		
		
		res = t2.or(t3).or(t4).or(t5).or(t6).or(t7);
		//res.printDot();
		 
		
		return res;	
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
	/*//////////////////////////////// MAIN //////////////////////////////////////////////////////*/    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
 		BDD trans = makeModel();
 		BDD init = initial;
 		BDD norm = normal;
 		
		BDDModel m = new BDDModel(trans, init, norm);
		m.addVar("p", Type.BOOL);
		m.addVar("q", Type.BOOL);
		
		// basic formulae
		FormulaElement vp = new Variable("p");
		FormulaElement vq = new Variable("q");
	
		
		FormulaElement nq = new Negation("!", vq);
		FormulaElement np = new Negation("!", vp);
		
		FormulaElement p_or_q= new Disjunction("|", vp,vq);	
		FormulaElement p_and_q = new Conjunction("&", vp, vq);
		FormulaElement np_and_nq = new Conjunction("&", np, nq);
		FormulaElement np_and_q = new Conjunction("&", np, vq);
		FormulaElement nq_and_p = new Conjunction("&", vp, nq);
	
		FormulaElement ff = new Constant("f", false);
		FormulaElement tt = new Constant("t", true);
		
		
		System.out.println("--------------------------- Property : EXp -----------------------------------\n");		
		
		FormulaElement form0 = new AX("E",vq, null); 
		
		m.getTransitions().printSet();
		if (DCTL_MC.mc_algorithm(form0, m)) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}		
		
		
		System.out.println("--------------------------- Property : unfairE(ttUp) -----------------------------------\n");		
		
	}

}
