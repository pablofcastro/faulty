package mc;

public class VarInfo {
	
	public enum Type {BOOL, INT} 
	
	private String name; // name of the variable
	private Type type; // type of the variable, 0 -> BOOL , 1 -> INTEGER
	
	public VarInfo(String name, Type type) {
		this.name = name;
		this.type = type;
	}	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}	

}
