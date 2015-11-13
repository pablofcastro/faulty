package formula;

public class PWU extends Permission {
	
	private FormulaElement expr3;
	private FormulaElement expr4;
	
	public PWU(String op,FormulaElement e1, FormulaElement e2, FormulaElement e3, FormulaElement e4){
	    super(op, e1, e2);
	    expr3 = e3;
	    expr4 = e4;
	}
		
	@Override
	public void accept(FormulaVisitor v){
	   //  v.visit(this);	
		     
	}
	
	/***
     * 
     * @return Returns the third expression of the formula.
     *    
     * If formula = P( f1 U f2 ~> f3 U f4), returns f3;     
     */
    public FormulaElement getExpr3(){
    	return this.expr3;
    	
    }
    
    /***
     * 
     * @return Returns the fourth expression of the formula.
     *      
     * If formula = P( f1 W f2 ~> f3 U f4), returns f4;     
     */
    public FormulaElement getExpr4(){
    	return this.expr4;
    	
    }

}