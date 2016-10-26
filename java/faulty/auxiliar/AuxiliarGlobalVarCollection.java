package faulty.auxiliar;

import java.util.LinkedList;

public class AuxiliarGlobalVarCollection extends AuxiliarProgramNode{
	
	LinkedList<AuxiliarVar> boolVars;
	LinkedList<AuxiliarVar> intVars;
    LinkedList<AuxiliarVar> enumVars;
    


	public AuxiliarGlobalVarCollection(){
		boolVars = new LinkedList<AuxiliarVar>();
		intVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
	}
	
	public int getTotalGlobalBoolVars(){
		return boolVars.size();
	}
	
	
	public int getTotalGlobalIntVars(){
		return intVars.size();
	}

    public int getTotalGlobalEnumVars(){
		return enumVars.size();
	}
	
    
	/**
	 * Precondition: var should be of type Bool
	 * @param v
	 * @return true if the variable was added , false otherwise.
	 */
	public boolean addGlobalBoolVar(AuxiliarVar v){
		return boolVars.add(v);
	}
	
	
	/**
	 * Precondition: var should be of type Int
	 * @param v
	 * @return true if the variable was added , false otherwise.
	 */
	public boolean addGlobalIntVar(AuxiliarVar v){
		return intVars.add(v);
	}
	
	/**
	 * Precondition: var should be of type Enumerated
	 * @param v
	 * @return true if the variable was added , false otherwise.
	 */
	public boolean addGlobalEnumVar(AuxiliarVar var){
        return enumVars.add(var);
	}
	
    
    public LinkedList<AuxiliarVar> getBoolVars(){
		return this.boolVars;
	}
	
	public LinkedList<AuxiliarVar> getIntVars(){
		return this.intVars;
	}

    public LinkedList<AuxiliarVar> getEnumVars(){
		return this.enumVars;
	}


	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}

}
