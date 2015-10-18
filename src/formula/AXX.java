package formula;

public class AXX extends ForAll {
	
	public AXX(String op,FormulaElement e1, FormulaElement e2){
        super(op,e1,e2);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
}
