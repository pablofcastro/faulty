package auxiliar;

import formula.*;

public class AuxiliarMethods {


	private static final String next = "X";
    private static final String weak = "W";

	public static FormulaElement rewriteFormula(String q,FormulaElement op1, FormulaElement op2){

		FormulaElement res=null;

		switch(q.charAt(0)) {
		case 'A': {     
						if (op1.toString().equals(next) ){	// first op is Next		            
							Next n1 = (Next)op1;
							if (op2.toString().equals(next) ){								
								Next n2 = (Next)op2;
								res = new AXX(q, n1.getExpr1(), n2.getExpr1());    
							}
							else{
                                if (op2.toString().equals(weak) ){//Weak
                                    Weak w = (Weak)op2;
                                   // res = new AXW(q, n1.getExpr1(), w.getExpr1(), w.getExpr2());
                                }
                                else{// until
								    Until u = (Until)op2;
							 	    res = new AXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2());
							    }
                            }
						}
						else{
                            
                            if (op1.toString().equals(weak) ){// first op is weak
                                Weak w1 = (Weak)op1;
                                if (op2.toString().equals(next) ){ //next
                                    Next n = (Next)op2;
                                   // res = new AWX(q, w1.getExpr1(), w1.getExpr2(),n.getExpr1());
                                }
                                else{
                                    if (op2.toString().equals(weak) ){//Weak
                                        Weak w2 = (Weak)op2;
                                       // res = new AWW(q, w1.getExpr1(), w1.getExpr2(), w2.getExpr1(), w2.getExpr2());
                                    }
                                    else{// until
                                        Until u = (Until)op2;
                                       // res = new AWU(q, w1.getExpr1(), w1.getExpr2(), u.getExpr1(), u.getExpr2());
                                    }
                                }
                            }
                            else{// first op is until
                                Until u1 = (Until)op1;
                                if (op2.toString().equals(next) ){
                                    Next n =  (Next)op2;
                                    res = new AUX(q, u1.getExpr1(), u1.getExpr2(), n.getExpr1());
                                }
                                else{
                                    if (op2.toString().equals(weak) ){//Weak
                                        Weak w = (Weak)op2;
                                        //res = new AUW(q, u1.getExpr1(), u1.getExpr2(), w.getExpr1(), w.getExpr2());
                                    }
                                    else{// until
                                        Until u2 = (Until)op2;
                                        res = new AUU(q, u1.getExpr1(), u1.getExpr2(), u2.getExpr1(), u2.getExpr2());
                                    }
                                     
                                }
                            
                            }
                            
						}
						break;
		         }
            case 'E':{
                if (op1.toString().equals(next) ){	// first op is Next
                    Next n1 = (Next)op1;
                    if (op2.toString().equals(next) ){
                        Next n2 = (Next)op2;
                        res = new EXX(q, n1.getExpr1(), n2.getExpr1());
                    }
                    else{
                        if (op2.toString().equals(weak) ){//Weak
                            Weak w = (Weak)op2;
                            //res = new EXW(q, n1.getExpr1(), w.getExpr1(), w.getExpr2());
                        }
                        else{// until
                            Until u = (Until)op2;
                            res = new EXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2());
                        }
                    }
                }
                else{
                    
                    if (op1.toString().equals(weak) ){// first op is weak
                        Weak w1 = (Weak)op1;
                        if (op2.toString().equals(next) ){ //next
                            Next n = (Next)op2;
                            //res = new EWX(q, w1.getExpr1(), w1.getExpr2(),n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w2 = (Weak)op2;
                                //res = new EWW(q, w1.getExpr1(), w1.getExpr2(), w2.getExpr1(), w2.getExpr2());
                            }
                            else{// until
                                Until u = (Until)op2;
                                //res = new EWU(q, w1.getExpr1(), w1.getExpr2(), u.getExpr1(), u.getExpr2());
                            }
                        }
                    }
                    else{// first op is until
                        Until u1 = (Until)op1;
                        if (op2.toString().equals(next) ){
                            Next n =  (Next)op2;
                            res = new EUX(q, u1.getExpr1(), u1.getExpr2(), n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w = (Weak)op2;
                                //res = new EUW(q, u1.getExpr1(), u1.getExpr2(), w.getExpr1(), w.getExpr2());
                            }
                            else{// until
                                Until u2 = (Until)op2;
                                res = new EUU(q, u1.getExpr1(), u1.getExpr2(), u2.getExpr1(), u2.getExpr2());
                            }
                            
                        }
                        
                    }
                    
                }
                break;
            }
            
            
            
            
            
            /*{
						if (op1.toString().equals(next) ){
							Next n1 = (Next)op1;
							if (op2.toString().equals(next) ){								
								Next n2 = (Next)op2;
								res = new EXX(q, n1.getExpr1(), n2.getExpr1());    
							}
							else{ // until								
								Until u = (Until)op2;
								res = new EXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2()); 
							}
						}
						else{// first op is until
							Until u = (Until)op1;
							if (op2.toString().equals(next) ){								
								Next n =  (Next)op2;
								res = new EUX(q, u.getExpr1(), u.getExpr2(), n.getExpr1());    
							}
							else{ // until								
								Until u2 = (Until)op2;
								res = new EUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
							}
						}
						break;
		         }*/

            case 'P':{
                if (op1.toString().equals(next) ){	// first op is Next
                    Next n1 = (Next)op1;
                    if (op2.toString().equals(next) ){
                        Next n2 = (Next)op2;
                        res = new PXX(q, n1.getExpr1(), n2.getExpr1());
                    }
                    else{
                        if (op2.toString().equals(weak) ){//Weak
                            Weak w = (Weak)op2;
                            res = new PXW(q, n1.getExpr1(), w.getExpr1(), w.getExpr2());
                        }
                        else{// until
                            Until u = (Until)op2;
                            res = new PXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2());
                        }
                    }
                }
                else{
                    
                    if (op1.toString().equals(weak) ){// first op is weak
                        Weak w1 = (Weak)op1;
                        if (op2.toString().equals(next) ){ //next
                            Next n = (Next)op2;
                            res = new PWX(q, w1.getExpr1(), w1.getExpr2(),n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w2 = (Weak)op2;
                                res = new PWW(q, w1.getExpr1(), w1.getExpr2(), w2.getExpr1(), w2.getExpr2());
                            }
                            else{// until
                                Until u = (Until)op2;
                                res = new PWU(q, w1.getExpr1(), w1.getExpr2(), u.getExpr1(), u.getExpr2());
                            }
                        }
                    }
                    else{// first op is until
                        Until u1 = (Until)op1;
                        if (op2.toString().equals(next) ){
                            Next n =  (Next)op2;
                            res = new PUX(q, u1.getExpr1(), u1.getExpr2(), n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w = (Weak)op2;
                                res = new PUW(q, u1.getExpr1(), u1.getExpr2(), w.getExpr1(), w.getExpr2());
                            }
                            else{// until
                                Until u2 = (Until)op2;
                                res = new PUU(q, u1.getExpr1(), u1.getExpr2(), u2.getExpr1(), u2.getExpr2());
                            }
                            
                        }
                        
                    }
                    
                }
                break;
            }
            
            
            
            
            /*{
						if (op1.toString().equals(next) ){
							Next n1 = (Next)op1;
							if (op2.toString().equals(next) ){
								Next n2 = (Next)op2;
								res = new PXX(q, n1.getExpr1(), n2.getExpr1());    
							}
							else{ // until								
								Until u = (Until)op2;
								res = new PXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2()); 
							}
						}
						else{// first op is until
							Until u = (Until)op1;
							if (op2.toString().equals(next) ){								
								Next n =  (Next)op2;
								res = new PUX(q, u.getExpr1(), u.getExpr2(), n.getExpr1());    
							}
							else{ // until								
								Until u2 = (Until)op2;
								res = new PUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
							}
						}
						break;
		          }*/

            case 'O': {
                if (op1.toString().equals(next) ){	// first op is Next
                    Next n1 = (Next)op1;
                    if (op2.toString().equals(next) ){
                        Next n2 = (Next)op2;
                        res = new OXX(q, n1.getExpr1(), n2.getExpr1());
                    }
                    else{
                        if (op2.toString().equals(weak) ){//Weak
                            Weak w = (Weak)op2;
                            res = new OXW(q, n1.getExpr1(), w.getExpr1(), w.getExpr2());
                        }
                        else{// until
                            Until u = (Until)op2;
                            res = new OXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2());
                        }
                    }
                }
                else{
                    
                    if (op1.toString().equals(weak) ){// first op is weak
                        Weak w1 = (Weak)op1;
                        if (op2.toString().equals(next) ){ //next
                            Next n = (Next)op2;
                            res = new OWX(q, w1.getExpr1(), w1.getExpr2(),n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w2 = (Weak)op2;
                                res = new OWW(q, w1.getExpr1(), w1.getExpr2(), w2.getExpr1(), w2.getExpr2());
                            }
                            else{// until
                                Until u = (Until)op2;
                                res = new OWU(q, w1.getExpr1(), w1.getExpr2(), u.getExpr1(), u.getExpr2());
                            }
                        }
                    }
                    else{// first op is until
                        Until u1 = (Until)op1;
                        if (op2.toString().equals(next) ){
                            Next n =  (Next)op2;
                            res = new OUX(q, u1.getExpr1(), u1.getExpr2(), n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w = (Weak)op2;
                                res = new OUW(q, u1.getExpr1(), u1.getExpr2(), w.getExpr1(), w.getExpr2());
                            }
                            else{// until
                                Until u2 = (Until)op2;
                                res = new OUU(q, u1.getExpr1(), u1.getExpr2(), u2.getExpr1(), u2.getExpr2());
                            }
                            
                        }
                        
                    }
                    
                }
                break;
            }
            
            
            
            /*{
			         if (op1.toString().equals(next) ){
			             Next n1 = (Next)op1;
			             if (op2.toString().equals(next) ){
			            	 Next n2 = (Next)op2;
				             res = new OXX(q, n1.getExpr1(), n2.getExpr1());    
			             }
			             else{ // until			            	 
				             Until u = (Until)op2;
				             res = new OXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2()); 
			             }
		             }
		             else{// first op is until
			             Until u = (Until)op1;
			             if (op2.toString().equals(next) ){			            	
				             Next n =  (Next)op2;
				             res = new OUX(q, u.getExpr1(), u.getExpr2(), n.getExpr1());    
			             }
			             else{ // until			            	 
				             Until u2 = (Until)op2;
				             res = new OUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
			             }
		             }
			         break;
		          }*/

            case 'R':  {
                if (op1.toString().equals(next) ){	// first op is Next
                    Next n1 = (Next)op1;
                    if (op2.toString().equals(next) ){
                        Next n2 = (Next)op2;
                        res = new RXX(q, n1.getExpr1(), n2.getExpr1());
                    }
                    else{
                        if (op2.toString().equals(weak) ){//Weak
                            Weak w = (Weak)op2;
                            res = new RXW(q, n1.getExpr1(), w.getExpr1(), w.getExpr2());
                        }
                        else{// until
                            Until u = (Until)op2;
                            res = new RXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2());
                        }
                    }
                }
                else{
                    
                    if (op1.toString().equals(weak) ){// first op is weak
                        Weak w1 = (Weak)op1;
                        if (op2.toString().equals(next) ){ //next
                            Next n = (Next)op2;
                            res = new RWX(q, w1.getExpr1(), w1.getExpr2(),n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w2 = (Weak)op2;
                                res = new RWW(q, w1.getExpr1(), w1.getExpr2(), w2.getExpr1(), w2.getExpr2());
                            }
                            else{// until
                                Until u = (Until)op2;
                                res = new RWU(q, w1.getExpr1(), w1.getExpr2(), u.getExpr1(), u.getExpr2());
                            }
                        }
                    }
                    else{// first op is until
                        Until u1 = (Until)op1;
                        if (op2.toString().equals(next) ){
                            Next n =  (Next)op2;
                            res = new RUX(q, u1.getExpr1(), u1.getExpr2(), n.getExpr1());
                        }
                        else{
                            if (op2.toString().equals(weak) ){//Weak
                                Weak w = (Weak)op2;
                                res = new RUW(q, u1.getExpr1(), u1.getExpr2(), w.getExpr1(), w.getExpr2());
                            }
                            else{// until
                                Until u2 = (Until)op2;
                                res = new RUU(q, u1.getExpr1(), u1.getExpr2(), u2.getExpr1(), u2.getExpr2());
                            }
                            
                        }
                        
                    }
                    
                }
                break;
            }
            
                      //res = new Recovery(q, op1, op2);
		              //break;
		              //}
		}//end switch
		return res;
	}




    public static FormulaElement rewriteFormula(String q,FormulaElement op){
        
		FormulaElement res=null;
        
		switch(q.charAt(0)) {
            case 'A': {
                if (op.toString().equals(next) ){ //next
                    Next n = (Next)op;
                    res = new AX(q, n.getExpr1());
                }
                else{
                    if (op.toString().equals(weak) ){  //weak
                        Weak w = (Weak)op;
                        res = new AW(q, w.getExpr1(), w.getExpr2());
                    }
                    else{ // Until
                        Until u = (Until)op;
                        res = new AU(q,u.getExpr1(), u.getExpr2());
                    }
                }
                break;
            }
            case 'E': {
                if (op.toString().equals(next) ){ //next
                    Next n = (Next)op;
                    res = new EX(q, n.getExpr1());
                }
                else{
                    if (op.toString().equals(weak) ){  //weak
                        Weak w = (Weak)op;
                        res = new EW(q, w.getExpr1(), w.getExpr2());
                    }
                    else{ // Until
                        Until u = (Until)op;
                        res = new EU(q,u.getExpr1(), u.getExpr2());
                    }
                }
                break;
            }
                
            case 'P': {
                if (op.toString().equals(next) ){ //next
                    Next n = (Next)op;
                    res = new PX(q, n.getExpr1());
                }
                else{
                    if (op.toString().equals(weak) ){  //weak
                        Weak w = (Weak)op;
                        res = new PW(q, w.getExpr1(), w.getExpr2());
                    }
                    else{ // Until
                        Until u = (Until)op;
                        res = new PU(q,u.getExpr1(), u.getExpr2());
                    }
                }
                break;
            }
                
            case 'O': {
                if (op.toString().equals(next) ){ //next
                    Next n = (Next)op;
                    res = new OX(q, n.getExpr1());
                }
                else{
                    if (op.toString().equals(weak) ){  //weak
                        Weak w = (Weak)op;
                        res = new OW(q, w.getExpr1(), w.getExpr2());
                    }
                    else{ // Until
                        Until u = (Until)op;
                        res = new OU(q,u.getExpr1(), u.getExpr2());
                    }
                }
                break;
            }
                
            case 'R': {
                if (op.toString().equals(next) ){ //next
                     Next n = (Next)op;
                     res = new RX(q, n.getExpr1());
                }
                else{
                    if (op.toString().equals(weak) ){  //weak
                        Weak w = (Weak)op;
                        res = new RW(q, w.getExpr1(), w.getExpr2());
                    }
                    else{ // Until
                        Until u = (Until)op;
                        res = new RU(q,u.getExpr1(), u.getExpr2());
                    }
                }
                break;
            }
                
        }//end switch
		
		return res;
	}


}
