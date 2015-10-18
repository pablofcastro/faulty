package faulty;
import java.util.*;

public class AuxiliarVar {

	Var var;
	Type typeVar;
	
	public AuxiliarVar(Var v, Type t){
		this.var = v;
		this.typeVar = t;
	}
	
	public Type getType(){
		
		return typeVar;
	}
	
	
    public Var getVar(){
		
		return var;
	}
	
}

