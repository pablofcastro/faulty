package auxiliar;

import java.util.Iterator;
import java.util.List;

import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;

public class tests_forAll {

	static BDDFactory B1;
	static boolean TRACE;
	static BDD p;	
	static BDD p_;	
	static BDD q;	
	static BDD q_;
	static BDD r;	
	static BDD r_;
	static BDD s;	
	static BDD s_;	
	static BDD initial; 
	static BDD transitions;
	static BDD normal;
	 
	static int varNum = 4;


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
		r = B1.ithVar(2);
		s = B1.ithVar(3);
		
		//primed variables
		p_ = B1.ithVar(4); 
		q_ = B1.ithVar(5);
		r_ = B1.ithVar(6);
		s_ = B1.ithVar(7);
		
		
		initial = (p.not()).and(q.not()).and(r.not()).and(s.not()); 
		
		normal= r.not();
		
		BDD res =null;

		BDD g1 =  (p.not()).and(q.not()).and(r.not()).and(s.not()).and(p_.and(q_.not()).and(r_.not()).and(s_.not())); // 0000 -> 1000
		BDD g2 =  (p.not()).and(q.not()).and(r.not()).and(s.not()).and(p_.not().and(q_).and(r_.not()).and(s_.not())); // 0000 -> 0100
		BDD g3 =  (p).and(q.not()).and(r.not()).and(s.not()).and(p_.not().and(q_).and(r_.not()).and(s_)); // 1000 -> 0101
		BDD g4 =  (p.not()).and(q).and(r.not()).and(s).and(p_.and(q_).and(r_.not()).and(s_.not())); // 0101 -> 1100
		BDD g5 =  (p).and(q).and(r.not()).and(s.not()).and(p_.and(q_).and(r_.not()).and(s_.not())); // 1100 -> 1100
		BDD g6 =  (p.not()).and(q).and(r.not()).and(s.not()).and(p_.and(q_).and(r_.not()).and(s_.not())); // 0100 -> 1100
		BDD g7 =  (p.not()).and(q.not()).and(r.not()).and(s.not()).and((p_.not()).and(q_.not()).and(r_).and(s_.not())); // 0000 -> 0010
		BDD g8 =  (p.not()).and(q.not()).and(r).and(s.not()).and(p_.and(q_).and(r_).and(s_)); // 0010 -> 1111
		BDD g9 =  (p).and(q).and(r).and(s).and(p_.and(q_).and(r_).and(s_)); // 1111 -> 1111
		BDD g10 =  (p.not()).and(q.not()).and(r).and(s.not()).and(p_.and(q_.not()).and(r_.not()).and(s_.not())); // 0010 -> 1000
		res = g1.or(g2).or(g3).or(g4).or(g5).or(g6).or(g7).or(g8).or(g9).or(g10);
		res.printDot();
		 
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
		m.addVar("r", Type.BOOL);
		m.addVar("s", Type.BOOL);
		
		// basic formulae
		FormulaElement vp = new Variable("p");
		FormulaElement vq= new Variable("q");
		FormulaElement nq= new Negation("!", vq);
		FormulaElement vs= new Variable("s");
		FormulaElement p_or_q= new Disjunction("|", vp,vq);		
		FormulaElement s_or_q= new Disjunction("|", vs,vq);
		FormulaElement ns= new Negation("!", vs);
		
		System.out.println("--------------------------- Property : A ( X p -> X q)) -----------------------------------\n");		
		
		
		
		FormulaElement form1 = new AXX("A",vp, vq); // Sat = 0100, 0101, 1100, 1000, 1111 y 0010  
		                                            // Note that 1111 y 0010 are anormal states.. that don't have any normative trace..
		
		if (DCTL_MC.mc_algorithm(form1, m)) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}		
		
		
		
        System.out.println("--------------------------- Property : O ( X p -> !q U s)) -----------------------------------\n");		
      
		
		FormulaElement form2 = new AXU("A",vp, nq,vs); 
		                                           
		
		if (DCTL_MC.mc_algorithm(form2, m)) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}				
		
		
		
		System.out.println("--------------------------- Property : O ( X (p or q) -> !q U (s or q)) -----------------------------------\n");		
		
		
		FormulaElement form3 = new AXU("A",p_or_q, nq, s_or_q); 
		                                            
		
		if (DCTL_MC.mc_algorithm(form3, m)) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}		
		
		
		System.out.println("--------------------------- Property : O ( !s U q -> X (p or q)) -----------------------------------\n");		

		
		FormulaElement form4 = new AUX("A",ns, vq, p_or_q); 
		
		if (DCTL_MC.mc_algorithm(form4, m)) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}	
		
		
		System.out.println("--------------------------- Property : O ( !s U q -> !s U q) -----------------------------------\n");		

		
		FormulaElement form5 = new AUU("A",ns, vq, ns, vq); 
		
		if (DCTL_MC.mc_algorithm(form5, m)) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}		
		
	}

}
