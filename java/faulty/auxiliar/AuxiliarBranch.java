package faulty.auxiliar;

import java.util.*;

public class AuxiliarBranch extends AuxiliarProgramNode {
	    //AuxiliarBoolExp guard; // the guard of the branch
	    AuxiliarExpression guard; // the guard of the branch
	    LinkedList<AuxiliarCode> assignList;
	    //Code code; // the code of the branch
	    boolean isFaulty; // it says if the branch is a faulty one or not
	    String label;
	    
	    public AuxiliarBranch(AuxiliarExpression guard,  LinkedList<AuxiliarCode> assignList, boolean isFaulty){
	    	
	    	this.guard=guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
	    	
	    }

	    public AuxiliarBranch(AuxiliarExpression guard,  LinkedList<AuxiliarCode> assignList, boolean isFaulty, String label){
	    	
	    	this.guard = guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
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

	    public String getLabel(){
	    	return this.label;
	    }
	    
		public void accept(AuxiliarFaultyVisitor v){
		     v.visit(this);			
		}
}
