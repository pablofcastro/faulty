package formula;

public class RXU extends Recovery {
	
	private FormulaElement expr3;
	
	public RXU(String op,FormulaElement e1, FormulaElement e2, FormulaElement e3){
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
     * If formula = R( X f1 ~> f2 U f3), returns f3;       
     */
    public FormulaElement getExpr3(){
    	return this.expr3;
    	
    }
}
