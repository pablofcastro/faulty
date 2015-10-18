package formula;

public class Constant implements FormulaElement {
	private String name;
	private boolean value; 
	
	public Constant(String s, boolean v){
	   name = s;
	   value =v;
	}
	
		
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);		 
	 }	
		
	@Override
    public String toString(){    	
    	return name;
    };
    
    /***
     * 
     * @return Return the boolean value of the constant.
     */
    public boolean getValue(){
		 return value;		 
	}	
}
