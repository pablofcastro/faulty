package formula;

public class Variable implements FormulaElement {
	
	private String name;
	
	public Variable(String n){
		name = n;
	}
	
	@Override	
	public void accept(FormulaVisitor visitor){
		 visitor.visit(this);
		 
	 }
	
	@Override
    public String toString(){    	
    	return name;
    };
    
    public String getName(){
    	return name;
    }

}
