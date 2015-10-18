package auxiliar;

import formula.*;

public class AuxiliarMethods {


	private static final String next = "X";

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

		case 'R': {   res = new Recovery(q, op1, op2);}
		              break;
		          }//end switch
		
		return res;
	}

}
