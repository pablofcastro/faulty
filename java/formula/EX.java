package formula;

public class EX extends Exists {

	public EX(String op,FormulaElement e1){
        super(op,e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		 v.visit(this);			
	}
	
}
