package formula;

public class PX extends Permission{

	public PX(String op,FormulaElement e1, FormulaElement e2){
        super(op,e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}
}