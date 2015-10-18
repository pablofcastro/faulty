package formula;

public class PWX extends Permission {
	
	private FormulaElement expr3;
	private FormulaElement expr4;
	
	public PWX(String op,FormulaElement e1, FormulaElement e2, FormulaElement e3){
	    super(op, e1, e2);
	    expr3 = e3;
	}
		
	@Override
	public void accept(FormulaVisitor v){
	 //   v.visit(this);	
		     
	}
	
	/***
     * 
     * @return Returns the third expression of the formula.
     *    
     * If formula = P( f1 U f2 ~> Xf3 ), returns f3;     
     */
    public FormulaElement getExpr3(){
    	return this.expr3;
    	
    }
    

}