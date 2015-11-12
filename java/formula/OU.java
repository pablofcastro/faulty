package formula;

/**
 * Simple class for representing formula O(pUq)
 * This formula can be captures using the more complex ones
 * but for performance issues we include it here
 * @author Pablo
 *
 */
public class OU extends Obligation{
	
	public OU(String op, FormulaElement e1, FormulaElement e2){
          super(op,e1,e2);	
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}
	
}