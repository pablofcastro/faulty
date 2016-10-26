package faulty;
import net.sf.javabdd.*;

import java.util.*;

/**
*  Models a branch of a process of the style:
* 		Guard -> Command
*  it contains a reference to its process, a guard and a code, the code
*  to be executed when the guard is true
**/
public class Branch {

	String instName; // a name of the instance to which this branch belongs
    BoolExp guard; // the guard of the branch
    Code code; // the code of the branch
    boolean isFaulty; // it says if the branch is a faulty one or not
    Process myProcess; // the process to which the branch belongs

    
    /**
     * Basic constructor for the class
     * @param guard the guard of the branch
     * @param code the code of the branch
     * @param isFaulty a boolean to indicate if the branch is faulty
     * @param myProcess a reference to the main process
     */
    public Branch(String instName, BoolExp guard, Code code, boolean isFaulty, Process myProcess){
    	this.instName = instName;
    	this.guard = guard;
    	this.code = code;
    	this.isFaulty = isFaulty;
    	this.myProcess = myProcess;
    }   
    
    /**
     * @return the guard
     */    
    public BoolExp getGuard(){
    	return guard;
    }
    
    /**
     * @return the code
     */
    public Code getCode(){
    	return code;
    }
    
    /**
     * Builds a duplicate of teh current branch (TO DO: override deepClone for this)
     * @param mapBool	maps original bool vars to their duplicates
     * @param mapInt	maps original int vars to their duplicates
     * @param globalBools	list of global bool variables
     * @param globalInts	list of global int vars
     * @param instName		name of the instance
     * @return	A duplicate of the branch
     */
    public Branch duplicate(String instName, HashMap<VarBool, VarBool> mapBool,HashMap<VarInt,VarInt> mapInt, Process owner){
    	Branch result = new Branch(instName, (BoolExp) guard.duplicate(instName, mapBool, mapInt, owner), code.duplicate(instName, mapBool, mapInt, owner), isFaulty, owner);
    	return result;
    }
    
    /**
     * Another duplicate but with params
     * @param instName
     * @param mapBool
     * @param mapInt
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public Branch duplicate(String instName, HashMap<VarBool, VarBool> mapBool,HashMap<VarInt,VarInt> mapInt, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){    	
    	Branch result = new Branch(instName, (BoolExp) guard.duplicate(instName, mapBool, mapInt, boolPars, intPars, owner), code.duplicate(instName, mapBool, mapInt, boolPars, intPars, owner), isFaulty, owner);
    	return result;
    }
    
    /**
     * Another duplicate but with params
     * @param instName
     * @param mapBool
     * @param mapInt
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public Branch duplicate(String instName, HashMap<Var, Var> dups,Process owner){    	
    	
    	Branch result = new Branch(instName, (BoolExp) guard.duplicate(instName, dups, owner), code.duplicate(instName, dups, owner), isFaulty, owner);
    	return result;
    }
    
    /**
     * Returns the symbolic representation of the branch, it produces a 
     * BDD respresenting the command Guard -> Command
     * @return a BDD representation of the branch
     */
    public BDD getBDD(){
    	BDD result;
    	if (!myProcess.getDisabledBlocking()){
    		// first, we calculate if some guard is stuck
    		BDD stuck_at_guard = Program.myFactory.zero();
    		for(int i=0; i < guard.getChannels().size(); i++){
    			stuck_at_guard = stuck_at_guard.or(guard.getChannels().get(i).getEmpty());
    		}
    	
    	
    		// ERROR: getFull only for right and getEmptyonly for Empty
    		// second, we check if some command is stuck
    		BDD stuck_at_command = Program.myFactory.zero();
    		for(int i=0; i < code.getChannels().size(); i++){
    			// if occurs on the left, then we check if it is full
    			if (code.getChannelsLeft().contains(code.getChannels().get(i))){
    				stuck_at_command = stuck_at_command.or(code.getChannels().get(i).getFull());
    			}
    			else{ // otherwise we check if it is empty
    				stuck_at_command = stuck_at_command.or(code.getChannels().get(i).getEmpty());
    			}
    		}
    		// the branch is not stuck if no command or guard is stuck
    		BDD notStuck = stuck_at_command.not().and(stuck_at_guard.not());
    		
    		// we calculate the normal execution of the branch
    		BDD branch_exec = guard.getBDD().and(code.getBDD());//.or(guard.getBDD().not().and(code.skipBDD()));
    	
    	
    		// BDD corresponding to the branch when no blocking is present
    		BDD stuck_at_branch = notStuck.not().and(myProcess.getStuck_Map().get(this)).and(code.skipBDD());
    			
    		// BDD corresponding to the execution of the branch when a blocking is present
    		BDD not_stuck_at_branch = notStuck.and(branch_exec.and(myProcess.getStuck_Map().get(this).not()));
    	
    		result = stuck_at_branch.or(not_stuck_at_branch);
    		// final result, if the branch is not stuck then we execute it, otherwise we signal to the process that we are stuck at this branch
    		//BDD result = (notStuck.imp(branch_exec.and(myProcess.getStuck_Map().get(this).not()))).and(notStuck.not().imp(myProcess.getStuck_Map().get(this).and(code.skipBDD())));        
    	}
    	else{
    		result = guard.getBDD().and(code.getBDD());  		
    	}
    	
    	
    	return result;
        //return guard.getBDD().imp(code.getBDD());
    }
    
    /**
     * Returns the list of the channels appearing in the branch
     * @return a list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(code.getChannels());
    	result.addAll(guard.getChannels());
    	return result;
    }
    
    /**
     * @return a list of the vars appearing in the branch
     */
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.addAll(code.getVars());    	
    	result.addAll(guard.getVars()); // check this
    	return result;
    	//return code.getVars();
    }
    
    /**
     * 
     * @return	the list of vars in the code
     */
    public LinkedList<Var> getCodeVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.addAll(code.getVars());
    	return result;
    }
    
    /**
     * 
     * @return	the list of vars in the guard
     */
    public LinkedList<Var> getGuardVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.addAll(guard.getVars()); // check this
    	return result;
    }
    
    public LinkedList<Integer> getPrimedIds(){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	LinkedList<Var> vars = this.getVars();
    	for (int i=0; i < vars.size(); i++ ){
    		result.addAll(vars.get(i).getPrimedIds());
    	}
    	return result;
    }
    
    
    @Override
  	public String toString(){
    
        String branchInfo = new String("");
		String spaces = new String ("            ");
		
		
		String  guardString= new String("   "+ guard.toString()); 
		String  CodeString= new String("    --> "+code.toString()); 
		String  faultyString= new String("       ( isFaulty: "+isFaulty+", myProcess: "+ myProcess.getName() + ", instanceName: "+ myProcess.getInstName() +")\n"); 
	        
		
		branchInfo= branchInfo.concat(guardString).concat(CodeString).concat(faultyString);
        
		return branchInfo;
	
		
    }
    

}// end of class
