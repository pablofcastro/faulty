package faulty.auxiliar;



/**
 * Class representing the multiplication of two UNSIGNED integers: int1 * int2 
 * TO BE IMPLEMENTED, DO NOT USE
 */
public class AuxiliarMultIntExp extends AuxiliarExpression{
	AuxiliarExpression exp1; // the left int
	AuxiliarExpression exp2; // the right int
   
    
    /**
     * Basic constructor of the class
     * @param	exp1	the left integer
     * @param	exp2	the right integer 
     */
    public AuxiliarMultIntExp(AuxiliarExpression exp1, AuxiliarExpression exp2){
    	
    	this.exp1 = exp1;
    	this.exp2 = exp2;
    	
    }
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
      
}// end class
