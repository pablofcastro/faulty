package faulty.auxiliar;




/**
 * Class representing  the subtraction of two integers: exp1 - exp2
 * 
 */
public class AuxiliarNegIntExp extends AuxiliarExpression{
	AuxiliarExpression exp1; // the first integer
	AuxiliarExpression exp2; // the second integer
    

    
    /**
     * Basic constructor of the class
     * @param exp1	the left int 
     * @param exp2	the right int
     */
    public AuxiliarNegIntExp(AuxiliarExpression exp1, AuxiliarExpression exp2){
    	
    	this.exp1 = exp1;
    	this.exp2 = exp2;
    	
    }
    
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
    
    
}
