package faulty.auxiliar;



/**
 * A class that represent the negation of a boolean expression
 */
public class AuxiliarNegBoolExp extends AuxiliarExpression{
   
	AuxiliarExpression exp; // the expresison to be negated
    
    /**
     * Constructor of the class
     * @param	exp	the expression in the negation
     */
    public AuxiliarNegBoolExp(AuxiliarExpression exp){
    
        this.exp = exp;
    }
    
       
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
}
