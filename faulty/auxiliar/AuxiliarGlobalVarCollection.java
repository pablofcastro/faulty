package faulty.auxiliar;

import java.util.LinkedList;

public class AuxiliarGlobalVarCollection extends AuxiliarProgramNode{
	
	LinkedList<AuxiliarVar> boolVars;
	LinkedList<AuxiliarVar> intVars;


	public AuxiliarGlobalVarCollection(){
		boolVars = new LinkedList<AuxiliarVar>();
		intVars = new LinkedList<AuxiliarVar>();
	}
	
	public int getTotalGlobalBoolVars(){
		return boolVars.size();
	}
	
	
	public int getTotalGlobalIntVars(){
		return intVars.size();
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
	 * Precondition: var should be of type Bool
	 * @param v
	 * @return true if the variable was added , false otherwise.
	 */
	public boolean addGlobalIntVar(AuxiliarVar v){
		return intVars.add(v);
	}
	
	public LinkedList<AuxiliarVar> getBoolVars(){
		return this.boolVars;
	}
	
	public LinkedList<AuxiliarVar> getIntVars(){
		return this.intVars;
	}


	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}

}
