package formula;

public class RW extends Permission{

	public RW(String op,FormulaElement e1, FormulaElement e2){
        super(op,e1,e2);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}
}
