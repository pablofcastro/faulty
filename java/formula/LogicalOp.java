package formula;

public abstract class LogicalOp implements FormulaElement{
	
	private String operator;
	private FormulaElement expr1;
	
	public LogicalOp(String op, FormulaElement e){
		expr1 =e;
		operator = op;		
	}
	
	
	/***
     * 
     * @return Returns the main operator of the formula.
     * 
     * If formula = !f1, returns !;
     * If formula = f1 & f2, returns &;
     * If formula = f1 | f2, returns |;
     * If formula = f1 -> f2, returns ->; 
     * If formula = f1 == f2, returns ==;
     */
	@Override
	public String toString(){
        return operator;
    };
    
    /***
     * 
     * @return Returns the first expression of the formula.
     * 
     * If formula = !f1, returns f1;
     * If formula = f1 & f2, returns f1;
     * If formula = f1 | f2, returns f1;
     * If formula = f1 -> f2, returns f1;
     * If formula = f1 == f2, returns f1;
     *
     */
    public FormulaElement getExpr1(){
    	return this.expr1;
    	
    }

}
