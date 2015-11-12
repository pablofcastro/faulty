package formula;

public class Next extends TemporalOp{
	
	public Next(String o, FormulaElement e){
		super (o,e);		
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}
	

}
