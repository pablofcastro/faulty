package formula;

/**
 * Simple class for representing formula OW
 * @author Pablo
 *
 */
public class OW extends Obligation{
	
	
	public OW(String op, FormulaElement e1, FormulaElement e2){
          super(op,e1,e2);	
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}
	
	
}