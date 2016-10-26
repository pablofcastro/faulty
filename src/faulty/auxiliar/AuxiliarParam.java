package faulty.auxiliar;


public class AuxiliarParam extends AuxiliarExpression {

	String declarationName; // Name used in the parameter declaration.
    
	Type type;
    String enumName;
    
	boolean isDeclaration;
    private AuxiliarEnumType enumType;

    
		
	/**
	 * 
	 * @param name
	 * @param t
	 */
	public AuxiliarParam(String declName, Type t){
		this.declarationName = declName;
     	this.type = t;
		this.isDeclaration=true;
        this.enumType=null;
        
        Type tVar;
        if(t.isEnumerated()){
            this.enumName = new String(t.getStringValue());
            
            tVar= Type.ENUMERATED;
            tVar.setStringValue(t.getStringValue());
            this.type = tVar;
            //System.out.println("in AuxiliarParam---->  declaration " + this.declarationName + " type "+ this.type.toString() +  " -- enumName: " + this.enumName);
            
            
        }else{
            this.type = t;
        }
	}
	
    
	/**
	 *
	 * @param name
	 * @param t
	 */
	public AuxiliarParam(String declName){
		this.declarationName = declName;
     	this.type = Type.UNDEFINED;
		this.isDeclaration=false;
        this.enumType=null;
        this.enumName=null;
	}
    
    
	/**
	 * 
	 * @return Return the type of the parameter.
	 */
	public Type getType(){
		
		return this.type;
	}
	
	public boolean isDeclaration(){
    	return this.isDeclaration;
    }

	
	/**
	 * 
	 * @return Return the declaration Name of the parameter.
	 */
    public String getDeclarationName(){
		
		return this.declarationName;
	}
    
    
    
    /**
	 * 
	 * @return Set the parameter type.
	 */
	public void setType(Type t){
        
        Type tVar;
        if(t.isEnumerated()){
            
            tVar= Type.ENUMERATED;
            tVar.setStringValue(t.getStringValue());
            this.type = tVar;
           // System.out.println("in AuxiliarParam---->  declaration " + this.declarationName + " type "+ this.type.toString() +  " -- enumName: " + this.enumName);
            
            
        }else{
            this.type = t;
        }
		
		this.type = t;
	}
    
    /**
	 *
	 *  Set the enumerated type parameter type.
	 */
	public void setEnumType(AuxiliarEnumType t){
		
		this.enumType = t;
	}
	
    
    /**
	 *
	 *  return the enumerated type parameter type.
	 */
	public AuxiliarEnumType getEnumType( ){
		
		return this.enumType;
	}
	
    /**
	 *
	 * Set the enumerated name of the variable.
	 */
    public void setEnumName(String n){
		
		this.enumName = n;
	}
    
    /**
     *
     * @return Return the enumerated name of the variable.
     */
    public String getEnumName(){
        return enumName;
    }

	
	/**
	 * 
	 * @return Set the declarationName of the parameter.
	 */
    public void setDeclarationName(String n){
		
		this.declarationName = n;
	}
    
    
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
	
}

