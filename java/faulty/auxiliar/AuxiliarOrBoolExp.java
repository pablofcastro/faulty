package faulty.auxiliar;




// OrBoolClass a class to represent and boolean expressions
public class AuxiliarOrBoolExp extends AuxiliarExpression{
   
    AuxiliarExpression exp1;
    AuxiliarExpression exp2;
    
    // Constructor for the class
    public AuxiliarOrBoolExp(AuxiliarExpression exp1, AuxiliarExpression exp2){
    	super();
        this.exp1 = exp1;
        this.exp2 = exp2;
    } 
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
}
