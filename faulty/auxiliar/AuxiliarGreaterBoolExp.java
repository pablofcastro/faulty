package faulty.auxiliar;




/**
 * Class implementing the comparation int1 > int2
 */

public class AuxiliarGreaterBoolExp extends AuxiliarBoolExp{
	AuxiliarExpression int1; // left int
	AuxiliarExpression int2; // right int

    /**
     * Constructor, it takes the two integer in the comparison as parameters
     * @param	int1	left integer
     * @param	int2	right integer
     */
    public AuxiliarGreaterBoolExp(AuxiliarExpression int1, AuxiliarExpression int2){
    	super();
        this.int1 = int1;
        this.int2 = int2;
    }

    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
    
}