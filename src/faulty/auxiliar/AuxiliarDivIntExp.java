package faulty.auxiliar;




/**
 * A class that implements the division between two integers: int1 / int2
 * TO BE IMPLEMENTED, DO NOT USE 
 */
public class AuxiliarDivIntExp extends AuxiliarExpression{
	AuxiliarExpression exp1; // left integer
	AuxiliarExpression exp2; // right integer
     
	/**
     * Constructor of the class, it takes as paramenter the two integers
     * @param	int1	the left integer
     * @param	int2	the right integer
     */
    public AuxiliarDivIntExp(AuxiliarExpression int1, AuxiliarExpression int2){
    	
    	this.exp1 = int1;
    	this.exp2 = int2;
    }
    
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
   
    
}
