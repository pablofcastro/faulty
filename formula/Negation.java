package formula;

public class Negation extends LogicalOp {
	
	public Negation(String op,FormulaElement e){
		super(op,e);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}		

}
