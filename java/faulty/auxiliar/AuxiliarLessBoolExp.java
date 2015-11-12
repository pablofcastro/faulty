package faulty.auxiliar;



/**
 * This class implements the comparation int1 < int2
 */
public class AuxiliarLessBoolExp extends AuxiliarExpression{
	AuxiliarExpression int1; // the left integer
	AuxiliarExpression int2; // the right integer

    /**
     * Basic constructor of the class
     * @param int1	the left integer
     * @param int2	the right integer
     */
    public AuxiliarLessBoolExp(AuxiliarExpression int1, AuxiliarExpression int2){
    	super();
        this.int1 = int1;
        this.int2 = int2;
    }

   
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
}