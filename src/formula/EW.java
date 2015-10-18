package formula;

public class EW extends Exists {

	public EW(String op,FormulaElement e1, FormulaElement e2){
        super(op,e1,e2);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		 v.visit(this);			
	}
	
}
