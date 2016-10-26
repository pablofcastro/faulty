package formula;

public class EqComparison extends LogicalOp {
	
	private FormulaElement expr2;
	private String enumName;
    private String nameExpr1;
    private String nameExpr2;
    
    //true if the expression is a variable, false in case of contants
    private boolean isVarExpr1;
    private boolean isVarExpr2;
    
    private boolean isTypeEnum;
    private boolean isTypeBool;
    private boolean isTypeInt;
    
    
	public EqComparison(String op,FormulaElement e1, FormulaElement e2){
		super(op,e1);
		expr2 = e2;
        this.enumName=null;
        this.isVarExpr1=false;
        this.isVarExpr2 =false;
        this.isTypeEnum=false;
        this.isTypeInt=false;
        this.isTypeBool=false;
	}
    
    public void setIsVarExpr1(boolean isVar){
        this.isVarExpr1 = isVar;
    }
    
    public void setIsVarExpr2(boolean isVar){
        this.isVarExpr2 = isVar;
    }
    
    public void setTypeEnum(boolean isTEnum){
        this.isTypeEnum = isTEnum;
        this.isTypeBool=false;
        this.isTypeInt=false;

    }
    
    public void setTypeBool(boolean isTBool){
        this.isTypeBool = isTBool;
        this.isTypeEnum=false;
        this.isTypeInt=false;

    }
    
    public void setTypeInt(boolean isTInt){
        this.isTypeInt = isTInt;
        this.isTypeBool=false;
        this.isTypeEnum=false;

    }
    
    public void setEnumNameComparison(String enumNameExpr){
        this.enumName= new String(enumNameExpr);
    }
    
    
    public void setNameExpr1(String var1){
        this.nameExpr1 = new String(var1);
    }

	
    public void setNameExpr2(String var2){
        this.nameExpr2 = new String(var2);
    }
    
    public boolean isVarExpr1(){
        return this.isVarExpr1;
    }
    
    public boolean isVarExpr2(){
        return this.isVarExpr2;
    }
    
    public boolean isTypeEnum(){
        return this.isTypeEnum;
    }
    
    public boolean isTypeBool(){
        return this.isTypeBool;
    }
    
    public boolean isTypeInt(){
        return this.isTypeInt;
    }
    
    public String getEnumNameComparison(){
        return this.enumName;
    }
    
    
    public String getNameExpr1(){
        return this.nameExpr1;
    }
    
	
    public String getNameExpr2(){
        return this.nameExpr2;
    }
    
    
    
	@Override
	public void accept(FormulaVisitor v){
		v.visit(this);		
	}
	
	/***
     * 
     * @return Returns the second expression of the formula.
     *     
     * If formula = f1 == f2, returns f2;    
     */
    public FormulaElement getExpr2(){
    	return this.expr2;
    	
    }
	

}
