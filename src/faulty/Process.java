package faulty;
import net.sf.javabdd.*;
import java.util.*;
import mc.VarInfo.Type;
import mc.*;

/**
 * Process is a basic class to capture the behavior of a process, it contains the basic information of a process:
 *  a list of vars, the channels used, etc. It also provides methods to produce the corresponding BDD.
 *  @author Pablo Castro 
 *  @author Ceci Kilmurray
 *  @version 0.1
 *  @date 06/2015
 *  
 */
public class Process{

    String name; // the name of the process
    LinkedList<VarInt> intVars; // it has a collection of local variables of type int
    LinkedList<VarBool> boolVars; // it has a collections of local variables of type boolean
    LinkedList<VarBool> globalBoolVars; // the list of boolean global vars
    LinkedList<VarInt>  globalIntVars; // the list of int global variables
    LinkedList<Channel> channels; // a collections of global channels that it can use to interact
    LinkedList<Branch> branches; // a collection of branches (Bi -> Ci)
    BDD normCondition; // normative condition for characterising the non-faulty part of the program
    BDD initialCond; // the initial valuations of the local variables.
    int numberInst; // used to distinguish different instances
    HashMap<Branch,BDD> stuck; // this map says if a branch is stuck at some branch
    HashMap<Branch,BDD> stuck_; // primed version of stuck
    BDDModel model; // the model representing the program, a parameter
    boolean disabledBlocking; // this attribute is used for efficency reasons, when no channels
    						 // are used then the stucks are not needed
    
    /** 
    * Basic constructor for class Process, it creates the basic structures for the class
    * @param name 			a name for the process, it actually is a 
    * @param numberVar		the number of int variables
    * @param numberChan		the number of channels used by this process
    * @param numberBranch	the number of branches of the process 
    * @param numberInst		number of instances of this process
    */
    public Process(String name, int numberVar, int numberChan, int numberBranch, int numberInst, BDDModel model){
        this.name = name;
        intVars = new LinkedList<VarInt>();
        boolVars = new LinkedList<VarBool>();
        channels = new LinkedList<Channel>();
        branches = new LinkedList<Branch>();
        globalBoolVars = new LinkedList<VarBool>();
        globalIntVars = new LinkedList<VarInt>();
        this.numberInst = numberInst;
        stuck = new HashMap<Branch,BDD>();
        stuck_ = new HashMap<Branch,BDD>();
        this.model = model;
        disabledBlocking = false; // by defaul blocking is enabled
    }
    
    /** 
     * Another constructor for class Process, it creates the basic structures for the class and 
     * allows to set disableBlocking
     * @param name 			a name for the process, it actually is a 
     * @param numberVar		the number of int variables
     * @param numberChan		the number of channels used by this process
     * @param numberBranch	the number of branches of the process 
     * @param numberInst		number of instances of this process
     */
     public Process(String name, int numberVar, int numberChan, int numberBranch, int numberInst, BDDModel model, boolean blocking){
         this.name = name;
         intVars = new LinkedList<VarInt>();
         boolVars = new LinkedList<VarBool>();
         channels = new LinkedList<Channel>();
         branches = new LinkedList<Branch>();
         globalBoolVars = new LinkedList<VarBool>();
         globalIntVars = new LinkedList<VarInt>();
         this.numberInst = numberInst;
         stuck = new HashMap<Branch,BDD>();
         stuck_ = new HashMap<Branch,BDD>();
         this.model = model;
         disabledBlocking = blocking; // by default blocking is enabled
     }
    
    /**
     * Observer method returns the name of the process
     * @return the name of the process
     */
    public String getName(){
        return name;
    }
    
    /**
     * Observer method to get the vars of int in the program
     * @return the list of Int vars in the process
     */
    public LinkedList<VarInt> getIntVars(){
        return intVars;
    }
    
    public LinkedList<VarBool> getBoolVars(){
        return boolVars;
    }
    
    public LinkedList<VarBool> getGlobalBoolVars(){
        return globalBoolVars;
    }
    
    public LinkedList<VarInt> getGlobalIntVars(){
        return globalIntVars;
    }
    
    /**
     * Observer methods to ge the list of channels in the program
     * @return the list of channels in the process
     */
    public LinkedList<Channel> getChannels(){
        return channels;
    }
    
    /**
     * Observer method that returns the list of branches i the program
     * @return list of branches
     */
    public LinkedList<Branch> getBranches(){
        return branches;
    }
    
    /**
     *	Observer method that returns then number of int vars in the process
     *	@return the number of int vars
     */
    public int getNumberIntVars(){
        return intVars.size();
    }
    
    /**
     * Observer method that return the number of bool vars of the process
     * @return the number of bool vars in the program
     */
    public int getNumberBoolVars(){
        return boolVars.size();
    }
    
    /**
     * Returns the mapping from branch to BDD
     * @return a hashmap relating branches with BDD
     */
    public HashMap<Branch,BDD> getStuckMap(){
    	return stuck;
    }
    
    /**
     * returns the mapping between branchs and BDD representing the change state that each branch produces
     * @return stuck
     */
    public HashMap<Branch,BDD> getStuck_Map(){
    	return stuck_;
    }
    
    /**
     * Gets the initial condition of the process
     * @return the initial condition
     */
    public BDD getInitialCond(){
    	return initialCond;
    }
    
    public BDD getNormCond(){
    	return normCondition;
    }
    
    /**
     * It sets a new name for the process
     * @param 	name 	the new name
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * Adds a new boolean var to the process
     * @param	var		a new boolean var
     */
    public void addVarBool(VarBool var){
    	boolVars.add(var);
    }
    
    /**
     * Adds a new int var to the process
     * @param var	the new var
     */
    public void addVarInt(VarInt var){
        intVars.add(var);
    }
    
    public void addGlobalVarBool(VarBool var){
    	globalBoolVars.add(var);
    }
    
    
    public void addGlobalVarInt(VarInt var){
    	globalIntVars.add(var);
    }
    
    /**
     * Adds a new channel to the list of channel in the process
     * @param	chan 	the new channel
     */
    public void addChannel(Channel chan){
        channels.add(chan);
       
    }
    
    /**
     * Adds a new branch to the process
     * @param branch	the new branch
     */
    public void addBranch(Branch branch){
        branches.add(branch);
        if (!disabledBlocking){
        	BDD stuck_branch = Program.myFactory.ithVar(Program.declaredVars);
        	Program.declaredVars++;
        	model.addVar("Process"+name+"stuck", Type.BOOL);
        	stuck.put(branch, stuck_branch);
        	//BDD stuck_branch_ = Program.myFactory.ithVar(Program.declaredVars);
        	//Program.declaredVars++;
        	//model.addVar("Process"+name+"stuck_", Type.BOOL);
        	//stuck_.put(branch, stuck_branch_);
        }
    }
    
    /**
     * Creates the primes for all the variables and branches in the program
     * primes for global variables are created in Program and also for channels
     */
    public void initPrimes(){
    	// create primes for int vars
    	for (int i = 0; i < intVars.size(); i++){
    		intVars.get(i).initPrimes();
    	}
    	
    	//create primes for bool vars
    	for (int i = 0; i < boolVars.size(); i++){
    		boolVars.get(i).initPrimes();
    	}
    	
    	// create primes for the branches
    	if (!disabledBlocking){
    		 for (int i = 0; i < branches.size(); i++){
    			BDD stuck_branch_ = Program.myFactory.ithVar(Program.declaredVars + stuck.get(branches.get(i)).var());
    	        Program.declaredVars_++;
    	        model.addVar("Process"+name+"stuck_", Type.BOOL);
    	        stuck_.put(branches.get(i), stuck_branch_);
    		 }
    	 }
    }
    
    /**
     * Sets an initial condition
     * @param	init	A BDD representing the initial condition
     */
    public void setInitialCond(BDD init){
    	initialCond = init;
    }
    
    /**
     * Sets an normative condition
     * @param	norm	A BDD representing the normative condition
     */
    public void setNormativeCond(BDD norm){
    	this.normCondition = norm;
    }
    
    
    /**
     * Calculates the BDD the represent the current process, it takes into account the 
     * bbranching in the code and the stuck variables that indicate whether a process is
     * stuck or not.  
     */
    public BDD getBDD(){
    	
    	// first, we calculate the BDD for the branching without taking into account the possible stuck at channels
        BDD process_bdd = Program.myFactory.zero();
        for (int i = 0; i < branches.size(); i++){
            process_bdd = process_bdd.or(branches.get(i).getBDD().and(skipChannelsNotIn(branches.get(i)).and(skipVarsNotIn(branches.get(i)))).and(skipOtherStuck(branches.get(i))));
        	//process_bdd = process_bdd.or(branches.get(i).getBDD().and(skipVarsNotIn(branches.get(i))));
        }
        
        // we calculate a BDD that says if some guard is on, otherwise the process gets blocked.
        BDD guards = Program.myFactory.zero();
        for (int i = 0; i < branches.size(); i++){
        	guards = guards.or(branches.get(i).getGuard().getBDD());
        }
 
        // If some guard is on then some is execued, otherwise the process getsblocked until some guard gets true blocked.
        process_bdd = guards.and(process_bdd).or(guards.not().and(skipBDD()));
        BDD result = process_bdd;
        if (!disabledBlocking){    
        	// now, we calculate the BDD corresponding to get stuck at some point (channel)
        	BDD isStuck = Program.myFactory.zero();
        	BDD stuckAt = Program.myFactory.zero(); // it creates the implication stuck(i) -> Gi -> Ci
        	for (int i=0; i < branches.size(); i++){
        		isStuck = isStuck.or(stuck.get(branches.get(i)));
        		stuckAt = stuckAt.or(stuck.get(branches.get(i)).and(branches.get(i).getBDD().and(skipChannelsNotIn(branches.get(i)).and(skipVarsNotIn(branches.get(i)))).and(skipOtherStuck(branches.get(i)))));
        	}
        
        	result =  isStuck.and(stuckAt).or(isStuck.not().and(process_bdd));//.and(initialCond);             
        }
        return result;
    }
    
    /**
     * Returns the BDD representing the normative part of the process
     * @return	the BDD of hte normative part of the process
     */
    public BDD getNorm(){
    	return normCondition;
    }
    
    public boolean getDisabledBlocking(){
    	return disabledBlocking;
    }
    
    /**
     * It returns a BDD representing a skip step for the process, this
     * is needed for the interleaving semantics. The returned BDD represents
     * not changed for the process
     * TBD: What about deadlock?
     */
    public BDD skipBDD(){
    	BDD result = Program.myFactory.one();
    	
    	for (int i = 0; i < intVars.size(); i++){
    		result = result.and(intVars.get(i).skipBDD());
    	}
    	
    	for (int i = 0; i < boolVars.size(); i++){
    		result = result.and(boolVars.get(i).skipBDD());
    	}
    	
    	for (int i = 0; i < globalIntVars.size(); i++){
    		result = result.and(globalIntVars.get(i).skipBDD());
    	}
    	
    	for (int i = 0; i < globalBoolVars.size(); i++){
    		result = result.and(globalBoolVars.get(i).skipBDD());
    	}
    	
    	
    	if (!disabledBlocking){
    		for (int i = 0; i < branches.size(); i++){
    			Branch actual = branches.get(i);
    			result = result.and(stuck.get(actual).biimp(stuck_.get(actual)));
    		}
    	}
    	
    	return result;
    }
    
    /**
     * A BDD respresentation of the guards
     */
    public BDD getGuards(){
    	BDD guards = Program.myFactory.zero();
    	for (int i = 0; i < branches.size(); i++){
    		guards = guards.or(branches.get(i).getGuard().getBDD());
    	}
    
    	return guards; 
    }
    
    /**
     * Private method that calculates a BDD stating that all the channel not in a given branch do not change
     * @param	branch	the branch 
     */
    private BDD skipChannelsNotIn(Branch branch){
    	BDD result = Program.myFactory.one();
    	for (int i = 0; i < channels.size(); i++){
    		if (!branch.getChannels().contains(channels.get(i))){
    			result = result.and(channels.get(i).skipBDD());
    		}
    	}
    	return result;
    	
    }
    
    /**
     * Private method that calculates a BDD stating that the vars not in a given branch do not change
     * @param	branch	the branch
     */
    private BDD skipVarsNotIn(Branch branch){
    	BDD result = Program.myFactory.one();
    	for (int i = 0; i < intVars.size(); i++){
    		if (!branch.getVars().contains(intVars.get(i))){
    			result = result.and(intVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < boolVars.size(); i++){
    		if (!branch.getVars().contains(boolVars.get(i))){
    			result = result.and(boolVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < globalIntVars.size(); i++){
    		if (!branch.getVars().contains(globalIntVars.get(i))){
    			result = result.and(globalIntVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < globalBoolVars.size(); i++){
    		if (!branch.getVars().contains(globalBoolVars.get(i))){
    			result = result.and(globalBoolVars.get(i).skipBDD());
    		}
    	}
    	
    	
    	return result;
    }
    
    
    /**
     * Private method that calculates a BDD stating that the image of every branch different to a given one by stuck do not change
     * @param branch	the branch
     */
    private BDD skipOtherStuck(Branch branch){
    	BDD result = Program.myFactory.one();
    	if (!disabledBlocking){
    		for (int i = 0; i < branches.size(); i++){
    			if (branches.get(i) != branch){
    				result = result.and(stuck.get(branches.get(i)).biimp(stuck_.get(branches.get(i))));
    			}
    		}
    	}
    	return result;
    }
    
}// end of class