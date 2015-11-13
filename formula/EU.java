package formula;

public class EU extends Exists {

	public EU(String op,FormulaElement e1, FormulaElement e2){
        super(op,e1,e2);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		 v.visit(this);			
	}
	
}
