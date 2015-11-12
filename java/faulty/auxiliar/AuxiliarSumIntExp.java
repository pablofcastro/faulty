package faulty.auxiliar;

/**
 * Provides the basic behavior of an expression
 * adding two integers
*/
public class AuxiliarSumIntExp extends AuxiliarExpression{
	AuxiliarExpression exp1; // the left integer
	AuxiliarExpression exp2; // the right integer

    /**
     * basic constructor of the class
     * @param	exp1	the left integer
     * @param	exp2	the right integer
     */
    public AuxiliarSumIntExp(AuxiliarExpression exp1, AuxiliarExpression exp2){
    	this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}    
}