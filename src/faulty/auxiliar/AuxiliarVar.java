package faulty.auxiliar;


public class AuxiliarVar extends AuxiliarExpression {

	String name;
	Type type;
	boolean isDeclaration;

		
	/**
	 * 
	 * @param name
	 * @param t
	 */
	public AuxiliarVar(String name, Type t){
		this.name = name;
		this.type = t;
		this.isDeclaration=true;
	}
	
	/**
	 * 
	 * @param name  
	 */
	public AuxiliarVar(String name){
		this.name = name;
		this.type = Type.UNDEFINED;
		this.isDeclaration=false;


	}

	/**
	 * 
	 * @return Return the type of the variable.
	 */
	public Type getType(){
		
		return this.type;
	}
	
	public boolean isDeclaration(){
    	return this.isDeclaration;
    }

	
	/**
	 * 
	 * @return Return the name of the variable.
	 */
    public String getName(){
		
		return name;
	}
    
    
    /**
	 * 
	 * @return Set the type of the variable.
	 */
	public void setType(Type t){
		
		this.type = t;
	}
	
	/**
	 * 
	 * @return Set the name of the variable.
	 */
    public void setName(String n){
		
		this.name = n;
	}
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
	
}

