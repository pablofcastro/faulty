package formula;

public class AX extends ForAll {
	
	public AX(String op,FormulaElement e1){
        super(op,e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
}
