package formula;

public abstract class Recovery implements FormulaElement {
	
	private String operator;
	private FormulaElement expr1;
	private FormulaElement expr2;
	
	public Recovery(String op,FormulaElement e1, FormulaElement e2){
		operator = op;
		expr1 = e1;
		expr2 = e2;
	
	}
	
	//@Override
	//public void accept(FormulaVisitor v){
	//	v.visit(this);		
	//}
	
	/***
	 * 
	 * @return Returns the main operator of the formula.
	 * 
	 * If formula = R( f1 ~> f2 ), returns R;
	 */
	//@Override
	public String toString(){
		return operator;
	};
	
	/***
     * 
     * @return Returns the first expression of the formula.
     * 
     * If formula = R( X f1 ~> X f2), returns =  X f1;
     * If formula = R( X f1 ~> f2 U f3), returns = X f1 ;
     * If formula = R( f1 U f2 ~> X f3), returns = f1 U f2;
     * If formula = R( f1 U f2 ~> f3 U f4), returns =  f1 U f2;    
     */
	public FormulaElement getExpr1(){
		return this.expr1;

	}

	/***
     * 
     * @return Returns the second expression of the formula.
     * 
     * If formula = R( X f1 ~> X f2), returns =  X f2;
     * If formula = R( X f1 ~> f2 U f3), returns = f2 U f3 ;
     * If formula = R( f1 U f2 ~> X f3), returns = X f3;
     * If formula = R( f1 U f2 ~> f3 U f4), returns =  f3 U f4;     
     */
	public FormulaElement getExpr2(){
		return this.expr2;

	}
	
	

}
