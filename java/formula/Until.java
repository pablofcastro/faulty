package formula;

public class Until extends TemporalOp{

	private FormulaElement expr2;
	
	public Until(String op,FormulaElement e1, FormulaElement e2){
		super(op,e1);
		expr2 = e2;
	}
	
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}		
           
    /***
     * 
     * @return Returns the second expression of the formula.
     *     
     * If formula = f1 U f2, returns f2;    
     */
    public FormulaElement getExpr2(){
    	return this.expr2;
    	
    }
	
}
