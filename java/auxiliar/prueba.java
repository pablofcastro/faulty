package auxiliar;

import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;

public class prueba {

	static BDDFactory B1;
	static boolean TRACE;
	static BDD moneda;	
	static BDD moneda_;	
	static BDD cafe;	
	static BDD cafe_;
	static BDD te;	
	static BDD te_;	
	static BDD cond_inicial; 
	static BDD transiciones;
	//static BDD modelo;
	 
	static int varNum = 3;


	/*//////////////////////////////// Metodos auxiliares //////////////////////////////////////////////////////*/    

	/**
	 * Inicializa una BDD Factory con la cantidad de Variables pasada como parametro
	 */
	private static BDDFactory inicializarBDDFactory(int vn){
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
	 * Arma y retorna el BDD del modelo generado a mano, caso por caso.
	 * @return
	 */
	private static BDD armarSolHarc(){
		
		B1= inicializarBDDFactory(varNum); 
		System.out.println("B1 varnum:" + B1.varNum());
		
		moneda = B1.ithVar(0); 
		cafe = B1.ithVar(1);
		te = B1.ithVar(2);
		//vble primadas.
		moneda_ = B1.ithVar(3);
		cafe_ = B1.ithVar(4);
		te_ = B1.ithVar(5);
		
		
		cond_inicial = (moneda.not()).and(cafe.not()).and(te.not()); 
		
		BDD res =null;

		BDD g1 =  (moneda.not()).and(cafe.not()).and(te.not()).and(moneda_.and(cafe_.not()).and(te_.not())); // 000 -> 100
		BDD g2 =  (moneda).and(cafe.not()).and(te.not()).and(moneda_.and(cafe_).and(te_.not())); // 100 -> 110
		BDD g3 =  (moneda).and(cafe.not()).and(te.not()).and(moneda_.and(cafe_.not()).and(te_)); // 100 -> 101
		BDD g4 =  (moneda).and(cafe).and(te.not()).and((moneda_.not()).and(cafe_.not()).and(te_.not())); // 110 -> 000
	    BDD g5 =  (moneda).and(cafe.not()).and(te).and((moneda_.not()).and(cafe_.not()).and(te_.not())); // 101 -> 000
		res = g1.or(g2).or(g3).or(g4).or(g5);
		//res.printDot();
		 
		return res;	
	}
	
	
	/*//////////////////////////////// MAIN //////////////////////////////////////////////////////*/    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
 		BDD trans = armarSolHarc();
 		BDD init = cond_inicial;
		BDDModel m = new BDDModel(trans , init);
		//Agrego vbles..
		m.addVar("moneda", Type.BOOL);
		m.addVar("cafe", Type.BOOL);
		m.addVar("te", Type.BOOL);
		
		System.out.println("--------------------------- Propiedad : E ( X moneda -> X ( cafe or te)) -----------------------------------\n");		
		
		FormulaElement mon = new Variable("moneda");
		FormulaElement caf = new Variable("cafe");
		FormulaElement tea = new Variable("te");
		FormulaElement caf_or_te = new Disjunction("|", caf, tea);
		FormulaElement form3 = new EXX("E",mon, caf_or_te); // Sat = 100 110 101
		
		if (DCTL_MC.mc_algorithm(form3, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}		
		
		
		/*System.out.println("--------------------------- Propiedad : P( X TRUE -> X cafe ) -----------------------------------\n");		
		
        FormulaElement t = new Constant("T" , true);
		FormulaElement form4 = new PXX("P",t, caf); // SAT = 100
		
		if (DCTL_MC.mc_algorithm(form4, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}		
		*/
		
        System.out.println("--------------------------- Propiedad : A ( X moneda -> ( moneda U (cafe or te) ) ) -----------------------------------\n");		
		
		
		
		FormulaElement form5 = new AXU("A",mon,mon,caf_or_te); // Sat = 100 101 110
		
		if (DCTL_MC.mc_algorithm(form5, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}		
				
        System.out.println("--------------------------- Propiedad : A ( ( moneda U (cafe or te) ) -> X moneda ) -----------------------------------\n");		
		
		
		
		FormulaElement form6 = new AUX("A",mon,caf_or_te, mon); // Sat = 000 100 
		
		if (DCTL_MC.mc_algorithm(form6, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}		
		
		
        System.out.println("--------------------------- Propiedad : A ( ( moneda U (cafe or te) ) -> X moneda ) -----------------------------------\n");		
		
		
		
		FormulaElement form7 = new AUU("A", mon, mon,caf_or_te, mon); // Sat = 000 100 
		
		if (DCTL_MC.mc_algorithm(form7, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}		
		
		
	}

}
