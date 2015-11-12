package auxiliar;

import formula.*;

public class AuxiliarMethods {


	private static final String next = "X";
    private static final String weak = "W";

	public static FormulaElement rewriteFormula(String q,FormulaElement op1, FormulaElement op2){

		FormulaElement res=null;

		switch(q.charAt(0)) {
		case 'A': {     
						if (op1.toString().equals(next) ){			            
							Next n1 = (Next)op1;
							if (op2.toString().equals(next) ){								
								Next n2 = (Next)op2;
								res = new AXX(q, n1.getExpr1(), n2.getExpr1());    
							}
							else{ // until								
								Until u = (Until)op2;
								res = new AXU(q, n1.getExpr1(), u.getExpr1(), u.getExpr2()); 
							}
						}
						else{// first op is until
							Until u = (Until)op1;
							if (op2.toString().equals(next) ){								
								Next n =  (Next)op2;
								res = new AUX(q, u.getExpr1(), u.getExpr2(), n.getExpr1());    
							}
							else{ // until								
								Until u2 = (Until)op1;
								res = new AUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
							}
						}
						break;
		         }
		case 'E': {    
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
								Until u2 = (Until)op1;
								res = new EUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
							}
						}
						break;
		         }

		case 'P': {     
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
								Until u2 = (Until)op1;
								res = new PUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
							}
						}
						break;
		          }

		case 'O': { 
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
				             Until u2 = (Until)op1;
				             res = new OUU(q,u.getExpr1(), u.getExpr2(), u2.getExpr1(), u2.getExpr2()); 
			             }
		             }
			         break;
		          }

		case 'R': {   //res = new Recovery(q, op1, op2);
		              break;
		          }
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
