package faulty.auxiliar;

import java.util.*;

public class AuxiliarBranch extends AuxiliarProgramNode {
	    //AuxiliarBoolExp guard; // the guard of the branch
	    AuxiliarExpression guard; // the guard of the branch
	    LinkedList<AuxiliarCode> assignList;
	    //Code code; // the code of the branch
	    boolean isFaulty; // is the branch faulty?
	    boolean isTau;  // is the branch internal?
	    String label;
	    
	    public AuxiliarBranch(AuxiliarExpression guard,  LinkedList<AuxiliarCode> assignList, boolean isFaulty, boolean isTau){
	    	
	    	this.guard=guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
	    	this.isTau = isTau;
	    	this.label = "";
	    	
	    }

	    public AuxiliarBranch(AuxiliarExpression guard,  LinkedList<AuxiliarCode> assignList, boolean isFaulty, boolean isTau, String label){
	    	
	    	this.guard = guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
	    	this.isTau = isTau;
	    	this.label = label;
	    	
	    }
	    
	    public AuxiliarExpression getGuard(){
	    	return this.guard;
	    }
	    
	    public LinkedList<AuxiliarCode> getAssignList(){
	    	return this.assignList;
	    }
	
	    public boolean getIsFaulty(){
	    	return this.isFaulty;
	    }

	    public boolean getIsTau(){
	    	return this.isTau;
	    }

	    public String getLabel(){
	    	return this.label;
	    }
	    
		public void accept(AuxiliarFaultyVisitor v){
		     v.visit(this);			
		}
}
