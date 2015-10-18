package formula;

public abstract class TemporalOp implements FormulaElement {

	private String operator;
	private FormulaElement expr1;
	
	public TemporalOp(String op, FormulaElement e){
		expr1 =e;
		operator = op;		
	}
	
	/***
     * 
     * @return Returns the main operator of the formula.
     * 
     * If formula = X f1, returns X;
     * If formula = f1 U f2, returns U;
     */
	@Override
    public String toString(){    	
    	return operator;
    };
	
	 /***
     * 
     * @return Returns the first expression.
     */
    public FormulaElement getExpr1(){
    	return this.expr1;
    	
    }
    

}
