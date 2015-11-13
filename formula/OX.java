package formula;

/**
 * Simple class for representing formula O(Xp)
 * @author Pablo
 *
 */
public class OX extends Obligation{
	
	public OX(String op, FormulaElement e1){
          super(op,e1,null);	
	}
    
	@Override
	public void accept(FormulaVisitor v){
		// visit is not implemented for this formula yet
	    v.visit(this);			
	}
	
}