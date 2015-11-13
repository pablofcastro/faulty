package faulty.auxiliar;



/**
 *  A class to represent the "and" of two boolean expressions
 */
public class AuxiliarAndBoolExp extends AuxiliarExpression{
	AuxiliarExpression exp1; // the left expression
	AuxiliarExpression exp2; // the right expression
	

    
    /**
     * Basic constructor for the class
     * @param 	exp1	the left expression
     * @param	epx2	the right expression
     */
    public AuxiliarAndBoolExp(AuxiliarExpression exp1, AuxiliarExpression exp2){
    	
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
}
