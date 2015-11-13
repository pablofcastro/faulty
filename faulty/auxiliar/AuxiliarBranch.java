package faulty.auxiliar;

import java.util.*;

public class AuxiliarBranch extends AuxiliarProgramNode {
	    //AuxiliarBoolExp guard; // the guard of the branch
	    AuxiliarExpression guard; // the guard of the branch
	    LinkedList<AuxiliarCode> assignList;
	    //Code code; // the code of the branch
	    boolean isFaulty; // it says if the branch is a faulty one or not
	    
	    public AuxiliarBranch(AuxiliarExpression guard,  LinkedList<AuxiliarCode> assignList, boolean isFaulty){
	    	
	    	this.guard=guard;
	    	this.assignList = assignList;
	    	this.isFaulty = isFaulty;
	    	
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
	    
		public void accept(AuxiliarFaultyVisitor v){
		     v.visit(this);			
		}
}
