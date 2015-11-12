package formula;

public class AXU extends ForAll {
	
	private FormulaElement expr3;

	public AXU(String op,FormulaElement e1, FormulaElement e2, FormulaElement e3){
        super(op,e1,e2);	
        expr3 = e3;
	}
	
	@Override
	public void accept(FormulaVisitor v){
	     v.visit(this);			
	}	
    
    /***
     * 
     * @return Returns the third expression of the formula.
     *    
     * If formula = A( X f1 ~> f2 U f3), returns f3;       
     */
    public FormulaElement getExpr3(){
    	return this.expr3;
    	
    }

}
