package formula;

public class PXW extends Permission {
	
	private FormulaElement expr3;
	private FormulaElement expr4;
	
	public PXW(String op,FormulaElement e1, FormulaElement e2, FormulaElement e3){
	    super(op, e1, e2);
	    expr3 = e3;
	}
		
	@Override
	public void accept(FormulaVisitor v){
	  //  v.visit(this);	
		     
	}
	
	/***
     * 
     * @return Returns the third expression of the formula.
     *    
     * If formula = P( Xf1 ~> f3 W f4), returns f3;     
     */
    public FormulaElement getExpr3(){
    	return this.expr3;
    	
    }
    
}