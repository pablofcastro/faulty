package faulty.auxiliar;


/**
 * A class representing an assignation to a var: var := Expr
 * it implements the interface code
 */
public class AuxiliarVarAssign extends AuxiliarCode{

    AuxiliarVar var; // the var in the left part
    AuxiliarExpression exp; 
    
    
    /**
     * Basic constructor of the class
     * @param		var 	the var in the assignment
     * @param		exp		the expression in the (right part of the) assignment
     */
    public AuxiliarVarAssign(AuxiliarVar var, AuxiliarExpression exp){
    	// Note: the var and the expression have to be of the same type
    	this.var = var;
    	this.exp = exp;
    	
    }
    
        
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
}
