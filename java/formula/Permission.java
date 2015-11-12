package formula;

public abstract class Permission implements FormulaElement{
	
	private String operator;
	private FormulaElement expr1;
	private FormulaElement expr2;
		
	public Permission(String op,FormulaElement e1, FormulaElement e2){
		    operator = op;
			expr1 = e1;
			expr2 = e2;		
    }
	
	/***
	 * 
	 * @return Returns the main operator of the formula.
	 * 
	 * If formula = P( f1 ~> f2 ), returns P;
	 */
	@Override
	public String toString(){
		return operator;
	};

	/***
     * 
     * @return Returns the first expression of the formula.
     * 
     * If formula = P( X f1 ~> X f2), returns f1;
     * If formula = P( X f1 ~> f2 U f3), returns f1;
     * If formula = P( f1 U f2 ~> X f3), returns f1;
     * If formula = P( f1 U f2 ~> f3 U f4), returns f1;     
     */
	public FormulaElement getExpr1(){
		return this.expr1;

	}

	/***
     * 
     * @return Returns the second expression of the formula.
     * 
     * If formula = P( X f1 ~> X f2), returns f2;
     * If formula = P( X f1 ~> f2 U f3), returns f2;
     * If formula = P( f1 U f2 ~> X f3), returns f2;
     * If formula = P( f1 U f2 ~> f3 U f4), returns f2;     
     */
	public FormulaElement getExpr2(){
		return this.expr2;

	}
	
	
}
