package auxiliar;

// Ejemplo que modela la estructura kripke de la maquina de cafe

import net.sf.javabdd.*;
//import net.sf.javabdd.BDD.BDDToString;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

public class cafe{

    static BDDFactory factory;
    
    
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
    
    
    // en el main simplemente imprimimos el Post del estado 100 (donde solo se cumple moneda)
    public static void main(String[] args){
        BDD moneda,moneda_,cafe, cafe_,te,te_;
        factory = BDDFactory.init(1000,2000);
        factory.setVarNum(6);
        
        // armamos el modelo del cafe
        moneda = factory.ithVar(0);
        cafe = factory.ithVar(1);
        te = factory.ithVar(2);
        moneda_ = factory.ithVar(3);
        cafe_ = factory.ithVar(4);
        te_ = factory.ithVar(5);
        
        BDD g1 =  (moneda.not()).and(cafe.not()).and(te.not()).and(moneda_.and(cafe_.not()).and(te_.not())); // 000 -> 100
		BDD g2 =  (moneda).and(cafe.not()).and(te.not()).and(moneda_.and(cafe_).and(te_.not())); // 100 -> 110
		BDD g3 =  (moneda).and(cafe.not()).and(te.not()).and(moneda_.and(cafe_.not()).and(te_)); // 100 -> 101
		BDD g4 =  (moneda).and(cafe).and(te.not()).and((moneda_.not()).and(cafe_.not()).and(te_.not())); // 110 -> 000
	    BDD g5 =  (moneda).and(cafe.not()).and(te).and((moneda_.not()).and(cafe_.not()).and(te_.not())); // 101 -> 000
		BDD res = g1.or(g2).or(g3).or(g4).or(g5);
        
		
		
        // Aplicamos restrict para quedarnos con los sucesores de 100
        BDD aux = res.restrict(cafe.not().and(te.not()).and(moneda));
        /*aux = aux.restrict(te.not());
        aux = aux.restrict(moneda);*/
    
        // se imprimen el conj. de asignaciones que hacen verdadero el BDD definido.
        aux.printSet();
        mostrarSoluciones(aux.allsat());
        
        //int[] set = aux.scanSet();
        int[] set = aux.scanSet();
        
        int length = set.length;
        
        int j = 0;
        
        while(j<length){
      	  System.out.println("set j =" + j + " = " + set[j]);
      	  j++;
        }
        
        // creamos un conjunto de pares para reemplzar las variables
        BDDPairing pairs = factory.makePair(); 
        pairs.set(3,0);// moneda_ sera reemplazada con moneda
        pairs.set(4,1); // cafe_ sera reemplazada con cafe
        pairs.set(5,2); // te_ sera reemplazada con te
        
        // reemplazamos
         aux.replaceWith(pairs);
          
         mostrarSoluciones(aux.allsat());
        
        // se imprimen el conj. de asignaciones que hacen verdadero el BDD definido.
          aux.printSet();
          
        
        
              
        
    }


}