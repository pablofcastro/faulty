package formula;

public class EX extends Exists {

	public EX(String op,FormulaElement e1, FormulaElement e2){
        super(op,e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		 v.visit(this);			
	}
	
}
