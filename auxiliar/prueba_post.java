package auxiliar;

import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;
import net.sf.javabdd.BDD.BDDToString;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;


public class prueba_post {

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
	static List<BDD> solutions;  
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
	
	
	/**
	 * Recorre y muestra los vectores de las soluciones del BDD pasado por parametro
	 * @param sol
	 */
	private static void  mostrarSoluciones ( List sol){

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

	
	/*//////////////////////////////// MAIN //////////////////////////////////////////////////////*/    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
 		BDD trans = armarSolHarc();
 		BDD init = cond_inicial;
 		        
		System.out.println("--------------------------- restrict -----------------------------------\n");		
		BDD result = trans.restrict(moneda.and(cafe.not()).and(te.not()));
		mostrarSoluciones(result.allsat());
				
		System.out.println("--------------------------- COFACTOR -----------------------------------\n");		
		BDD result2 =  trans.constrain(moneda.and(cafe.not()).and(te.not()));
		mostrarSoluciones(result2.allsat());
		
		System.out.println("------------------------------------------------replace -------------------------------\n");
		
		BDDPairing p = B1.makePair(0, 3);
		Iterator i = result.iterator(result);	
		BigInteger range = BigInteger.valueOf(1);
		System.out.println("longitud dominio-------" + range.bitCount() );
		
	/*
		
		System.out.println("longitud arreglo------------------------> " + arregloInt.length);
		
		int k =0;
		while (k< arregloInt.length){
			System.out.println("probandoo------------------------>   "+ arregloInt[k].bitLength() );
			arregloInt[k].shiftLeft(3);
			k++;		
		}
		
		//B1.swapVar(0, 3);
		//p.set(0,3);
		//result= result.replace(p);
	
		//mostrarSoluciones(result.allsat());*/
	}

}
