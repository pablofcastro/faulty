package auxiliar;

import java.util.Iterator;
import java.util.List;

import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;

public class pruebatk {

	static BDDFactory B1;
	static boolean TRACE;
	static BDD n0;	
	static BDD n0_;	
	static BDD n1;	
	static BDD n1_;
	static BDD n2;	
	static BDD n2_;	
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
		numberOfNodes = Math.max(100000, numberOfNodes);
		System.out.println("nodos:" + numberOfNodes);
		B = BDDFactory.init(numberOfNodes, cacheSize);
		//B = BDDFactory.init("java", numberOfNodes, cacheSize);
		if (B.varNum() < (vn*2)) B.setVarNum((vn*2));
		System.out.println("factory varnum:" + B.varNum());
		return B;
	}    
	
	private static void  mostrarSoluciones(List sol){

		//System.out.println(" iterator size:" + sol.size());
		Iterator i = sol.iterator();
		System.out.print("\nSoluciones = ");          
		while (i.hasNext()) {     	
			byte[] c1 = (byte[])i.next();

			if (c1 != null){
				System.out.print("\n[");  
				for (int j=0; j < c1.length; j++   ){
					System.out.print(c1[j]); 

					if (j != c1.length -1) //sino es el ult pone ","
						System.out.print(",");
				} 
				System.out.print("]\n");  
			}
		} 
		System.out.print("\n\n");  
	}
	
	/**
	 * Arma y retorna el BDD del modelo generado a mano, caso por caso.
	 * @return
	 */
	private static BDD armarSolHarc(){
		
		B1= inicializarBDDFactory(varNum); 
		System.out.println("B1 varnum:" + B1.varNum());
		
		n0 = B1.ithVar(0); 
		n1 = B1.ithVar(1);
		n2 = B1.ithVar(2);
		//vble primadas.
		n0_ = B1.ithVar(3);
		n1_ = B1.ithVar(4);
		n2_ = B1.ithVar(5);
		
		
		cond_inicial = (n0).and(n1.not()).and(n2.not()); 
		
		BDD res =null;

		BDD g1 =  ((n0).and(n1.not()).and(n2.not())).and((n0_.not()).and(n1_).and(n2_.not())); // 100 -> 010
		BDD g2 =  ((n0.not()).and(n1).and(n2.not())).and((n0_.not()).and(n1_.not()).and(n2_)); // 010 -> 001
		BDD g3 =  ((n0.not()).and(n1.not()).and(n2)).and((n0_).and(n1_.not()).and(n2_.not())); // 001 -> 100
		BDD g4 =  ((n0).and(n1.not()).and(n2.not())).and((n0_.not()).and(n1_.not()).and(n2_.not())); // 100 -> 000
		BDD g5 =  ((n0.not()).and(n1).and(n2.not())).and((n0_.not()).and(n1_.not()).and(n2_.not())); // 010 -> 000
	    BDD g6 =  ((n0.not()).and(n1.not()).and(n2)).and((n0_.not()).and(n1_.not()).and(n2_.not())); // 001 -> 000
		BDD g7 =  ((n0.not()).and(n1.not()).and(n2.not())).and((n0_).and(n1_.not()).and(n2_.not())); // 001 -> 100
		res = g1.or(g2).or(g3).or(g4).or(g5).or(g6).or(g7);
		//res.printDot();
		//mostrarSoluciones(res.allsat()); 
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
		m.addVar("n0", Type.BOOL);
		m.addVar("n1", Type.BOOL);
		m.addVar("n2", Type.BOOL);
		
		System.out.println("--------------------------- Propiedad : n0 -----------------------------------\n");		
		
		FormulaElement vble = new Variable("n1");
		FormulaElement form = new Negation("!",vble);
		FormulaElement form2 = new Negation("!",form);
		FormulaElement form3 = new Negation("!",form2);
		
		if (DCTL_MC.mc_algorithm(vble, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}

		System.out.println("--------------------------- Otra Propiedad: moneda -----------------------------------\n");		
		
		
		if (DCTL_MC.mc_algorithm(form, m)) {
			System.out.println("La propiedad Vale");
		}
		else{
			System.out.println("La propiedad NO VALE");
		}

		
	}

}
