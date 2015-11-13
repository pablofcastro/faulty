package formula;

public class EUX extends Exists {

	private FormulaElement expr3;

	public EUX(String op,FormulaElement e1, FormulaElement e2, FormulaElement e3){
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
     * If formula = E( f1 U f2 ~> f3 U f4), returns f3;     
     */
    public FormulaElement getExpr3(){
    	return this.expr3;
    	
    }


}
