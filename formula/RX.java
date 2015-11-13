package formula;

public class RX extends Recovery{

	public RX(String op,FormulaElement e1){
        super(op,e1,null);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}
}