package faulty.auxiliar;



public class AuxiliarChannel extends AuxiliarProgramNode {

	
	String name;
	int size;
	Type typeChannel;
	boolean isDeclaration;
	
	public AuxiliarChannel(String name,int size, Type t){
		this.name = name;
		this.size = size;
		this.typeChannel = t;
		this.isDeclaration= true;
	}
	
	public AuxiliarChannel(String name){
		this.name = name;
		this.size = 0;
		this.typeChannel = Type.UNDEFINED;
		this.isDeclaration=false;
	}
	
	public AuxiliarChannel(String name,boolean isDecl){
		this.name = name;
		this.size = 0;
		this.typeChannel = Type.UNDEFINED;
		this.isDeclaration=isDecl;
	}
	
	
	public Type getType(){
		
		return this.typeChannel;
	}
	
	
    public String getName(){
		
		return this.name;
	}
    
    public int getSize(){
		
		return this.size;
	}
    
    public boolean isDeclaration(){
    	return this.isDeclaration;
    }

    
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
	
}
