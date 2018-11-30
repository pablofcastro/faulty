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
public class Process {

    String name; // the name of the process
    String instName; // the name of the instance
    int numberVar; // the number of variables in the process
    int numberChan; // the number of channels used by the process
    int numberBranch; // the number of branches in the process
    Program myProgram; // a reference to the main program
    LinkedList<VarInt> intVars; // it has a collection of local variables of type int
    LinkedList<VarBool> boolVars; // it has a collections of local variables of type boolean
    LinkedList<VarEnum> enumVars; // a list of local vars
    LinkedList<VarBool> globalBoolVars; // the list of boolean global vars
    LinkedList<VarInt>  globalIntVars; // the list of int global variables
    LinkedList<VarEnum> globalEnumVars; // the list of enumerable global variables
    LinkedList<Channel> channels; // a collections of global channels that it can use to interact
    LinkedList<Branch> branches; // a collection of branches (Bi -> Ci)
    LinkedList<ParamBool> boolPars; // a collection of boolean parameters
    LinkedList<ParamInt> intPars; // a collection of int parameters
    LinkedList<ParamEnum> enumPars; // a collection of enum parameters
    BoolExp normCondition; // normative condition for characterising the non-faulty part of the program
    BoolExp initialCond; // the initial valuations of the local variables.
    int numberInst; // used to distinguish different instances
    HashMap<Branch,BDD> stuck; // this map says if a branch is stuck at some branch
    HashMap<Branch,BDD> stuck_; // primed version of stuck
    BDDModel model; // the model representing the program, a parameter
    boolean disabledBlocking; // this attribute is used for efficency reasons, when no channels
    						 // are used then the stucks are not needed
    BDD skipLocalVarsBDD;	// it is used for efficiency reasons, to avoid recomputing the same BDD
    						// several times
    BDD skipAll; // it is used for efficiency reasons, to avoid computing this value twice
    BDD guards; // it keeps a BDD representing the guards of the process
    boolean fairness; // to know whether we are computing fairness bdds
    BDD running; // is a BDD used for fairness, to know if the process is running
    BDD running_; // a primed BDD for running
    
    
    /** 
    * Basic constructor for class Process, it creates the basic structures for the class
    * @param name 			a name for the process, it actually is a 
    * @param instName		this is the name of thw actual instance of the process
    * @param numberVar		the number of int variables
    * @param numberChan		the number of channels used by this process
    * @param numberBranch	the number of branches of the process 
    * @param numberInst		number of instances of this process
    */
    public Process(Program program, String name, String instName, int numberVar, int numberChan, int numberBranch, int numberInst, BDDModel model){
        this.name = name;
        this.instName = instName;
        this.myProgram = program;
        intVars = new LinkedList<VarInt>();
        boolVars = new LinkedList<VarBool>();
        enumVars = new LinkedList<VarEnum>();
        channels = new LinkedList<Channel>();
        branches = new LinkedList<Branch>();
        globalBoolVars = new LinkedList<VarBool>();
        globalIntVars = new LinkedList<VarInt>();
        globalEnumVars = new LinkedList<VarEnum>();
        boolPars = new LinkedList<ParamBool>();
        intPars = new LinkedList<ParamInt>();
        enumPars = new LinkedList<ParamEnum>();
        this.numberInst = numberInst;
        stuck = new HashMap<Branch,BDD>();
        stuck_ = new HashMap<Branch,BDD>();
        this.model = model;
        disabledBlocking = false; // by defaul blocking is enabled
        fairness = false;
        
        
    }
    
    
    
    /** 
     * Another constructor for class Process, it creates the basic structures for the class and 
     * allows to set disableBlocking
     * @param name 			a name for the process, it actually is a 
     * @param instName		a name for the current instance of the process, recall that a process may have several instances
     * @param numberVar		the number of int variables
     * @param numberChan		the number of channels used by this process
     * @param numberBranch	the number of branches of the process 
     * @param numberInst		number of instances of this process
     */
     public Process(Program myProgram, String name, String instName, int numberVar, int numberChan, int numberBranch, int numberInst, BDDModel model, boolean blocking){
         this.name = name;
         this.instName = instName;
         this.numberVar = numberVar;
         this.numberChan = numberChan;
         this.numberBranch = numberBranch;
         this.myProgram = myProgram;
         intVars = new LinkedList<VarInt>();
         boolVars = new LinkedList<VarBool>();
         enumVars = new LinkedList<VarEnum>();
         channels = new LinkedList<Channel>();
         branches = new LinkedList<Branch>();
         globalBoolVars = new LinkedList<VarBool>();
         globalIntVars = new LinkedList<VarInt>();
         globalEnumVars = new LinkedList<VarEnum>();
         boolPars = new LinkedList<ParamBool>();
         intPars = new LinkedList<ParamInt>();
         enumPars = new LinkedList<ParamEnum>();
         this.numberInst = numberInst;
         stuck = new HashMap<Branch,BDD>();
         stuck_ = new HashMap<Branch,BDD>();
         this.model = model;
         disabledBlocking = blocking; // by default blocking is enabled
         fairness = false;
     }
     
     /** 
      * Another constructor for class Process, it creates the basic structures for the class and 
      * allows to set disableBlocking and fairness
      * @param name 			a name for the process, it actually is a 
      * @param instName		a name for the current instance of the process, recall that a process may have several instances
      * @param numberVar		the number of int variables
      * @param numberChan		the number of channels used by this process
      * @param numberBranch	the number of branches of the process 
      * @param numberInst		number of instances of this process
      */
      public Process(Program myProgram, String name, String instName, int numberVar, int numberChan, int numberBranch, int numberInst, BDDModel model, boolean blocking, boolean fair){
          this.name = name;
          this.instName = instName;
          this.numberVar = numberVar;
          this.numberChan = numberChan;
          this.numberBranch = numberBranch;
          this.myProgram = myProgram;
          intVars = new LinkedList<VarInt>();
          boolVars = new LinkedList<VarBool>();
          enumVars = new LinkedList<VarEnum>();
          channels = new LinkedList<Channel>();
          branches = new LinkedList<Branch>();
          globalBoolVars = new LinkedList<VarBool>();
          globalIntVars = new LinkedList<VarInt>();
          globalEnumVars = new LinkedList<VarEnum>();
          boolPars = new LinkedList<ParamBool>();
          intPars = new LinkedList<ParamInt>();
          enumPars = new LinkedList<ParamEnum>();
          this.numberInst = numberInst;
          stuck = new HashMap<Branch,BDD>();
          stuck_ = new HashMap<Branch,BDD>();
          this.model = model;
          disabledBlocking = blocking; // by default blocking is enabled
          fairness = fair;
          
          // if we have set fairness on we must init running
          if (fair){
        	
          	running = Program.myFactory.ithVar(Program.declaredVars);
          	Program.declaredVars++;
          }
    }
    
    /**
     * Observer method returns the name of the process
     * @return the name of the process
     */
    public String getName(){
        return name;
    }
    
    /**
     * 
     * @return	the instance name of the current process
     */
    public String getInstName(){
    	return instName;
    }
    
    /**
     * Observer method to get the vars of int in the program
     * @return the list of Int vars in the process
     */
    public LinkedList<VarInt> getIntVars(){
        return intVars;
    }
    
    /**
     * Observer method
     * @return	the list of local bool vars
     */
    public LinkedList<VarBool> getBoolVars(){
        return boolVars;
    }
    
    /**
     * 
     * @return the list of enum local vars
     */
    public LinkedList<VarEnum> getEnumVars(){
    	return enumVars;
    }
    
    /**
     * Observer method 
     * @return the list of global boolean vars
     */
    public LinkedList<VarBool> getGlobalBoolVars(){
        return globalBoolVars;
    }
    
    /**
     * Observer method
     * @return	the list of global int vars
     */
    public LinkedList<VarInt> getGlobalIntVars(){
        return globalIntVars;
    }
    
    public LinkedList<VarEnum> getGlobalEnumVars(){
    	return globalEnumVars;
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
     * 
     * @return	the number of enumerable var in the process
     */
    public int getNumberEnumVars(){
    	return enumVars.size();
    }
    
   /**
    * 
    * @return the list of boolean parameters of the process 
    */
    public LinkedList<ParamBool> getBoolPars(){
    	return boolPars;
    }
   
    /**
     * 
     * @return the list of integer parameters of the process
     */
    public LinkedList<ParamInt> getIntPars(){
    	return intPars;
    }
    
    /**
     * 
     * @return the list of enumerable parameters
     */
    public LinkedList<ParamEnum> getEnumPars(){
    	return enumPars;
    }
    
    /**
     * 
     * @return	the running var of the current process
     */
    public BDD getRunning(){
    	return running;
    }
    
    /**
     * 
     * @return the running primed var of the current process
     */
    public BDD getRunning_(){
    	return running_;
    }
    
    /**
     * Set the boolean parameters of the process
     * @param pars	the boolran params to be added
     */
    public void setBoolParams(LinkedList<ParamBool> pars){
    	this.boolPars = pars;
    }
    
    /**
     * Set the int parameters of the process
     * @param pars the int params to be added
     */
    public void setIntParams(LinkedList<ParamInt> pars){
    	this.intPars = pars; 
    }
    
    
    public void setEnumParams(LinkedList<ParamEnum> pars){
    	this.enumPars = pars;
    }
    
    /**
     * A program may have several running instances of a process, this method constructs a new instance of a process
     * for that a instName is needed, it calls recursively to all components of the process and duplicates them, except
     * the global vars and the channels
     * @returns a duplicate of the current process with a new name
     */
    public Process duplicate(String instName){ 
    	LinkedList<ParamEnum> epars = new LinkedList<ParamEnum>();
    	LinkedList<ParamBool> bpars = new LinkedList<ParamBool>();
    	LinkedList<ParamInt> ipars = new LinkedList<ParamInt>();
    	return duplicate(instName, bpars, ipars, epars);
    	//Process result = new Process(myProgram, name, instName, numberVar, numberBranch, numberChan, numberInst+1, model, disabledBlocking, fairness);
    	// we use hash mapping to record the duplicates of each one and to obtain the branches, expression
    	// and code
    	
    	//HashMap<VarBool, VarBool> boolDups = new HashMap<VarBool,VarBool>();
    	//HashMap<VarInt, VarInt> intDups = new HashMap<VarInt, VarInt>();
    	
    	
    	// we duplicate the local int vars
    	//for (int i = 0; i < intVars.size(); i++){
    	//	VarInt dup = (VarInt) intVars.get(i).duplicate(instName, boolDups, intDups, this);
    	//	result.addVarInt(dup);
    	//	intDups.put(intVars.get(i), dup);
    	//}
    	
    	// we duplicate the local bool vars
    	//for (int i = 0; i < boolVars.size(); i++){
    	//	VarBool dup = (VarBool) boolVars.get(i).duplicate(instName, boolDups, intDups, this);
    	//	result.addVarBool(dup);
    	//	boolDups.put(boolVars.get(i), dup);
    	//}
    	
    	// we duplicate the local enum vars
    	//for (int i = 0; i < enumVars.size(); i++){
    	//	VarEnum dup = (VarEnum) enumVars.get(i).duplicate(instName, dups, this);
    	//	result.addVarEnum(dup);
    	//	dups.put(enumVars.get(i), dup);
    	//}
    	
    	// we add the global bool vars (we do not need to duplicate them since they are global!)
    	//for (int i = 0; i < globalBoolVars.size(); i++){
    	//	result.addGlobalVarBool(globalBoolVars.get(i));
    	//}
    	
    	// we add the global int vars (we do not need to duplicate them since they are global!)
    	//for (int i = 0; i < globalIntVars.size(); i++){
    	//	result.addGlobalVarInt(globalIntVars.get(i));
    	//}
    	
   	
    	// we duplicate the branches
    	//for (int i = 0; i < branches.size(); i++){
    	//	result.addBranch(branches.get(i).duplicate(instName, boolDups, intDups, this));
    	//}
    
    	// we add the channels 
    	//for (int i = 0; i < channels.size(); i++){
    	//	result.addChannel(channels.get(i));
    	//}	

    	// TO DO: fix these lines
    	//result.setInitialCond((BoolExp) initialCond.duplicate(instName, boolDups, intDups, this));
    	//result.setNormativeCond((BoolExp) normCondition.duplicate(instName, boolDups, intDups, this));
    	
    	
    	//result.initPrimes();
    	//return result;
    }
    
    /**
     * Another version of duplicate for those processes that have  parameters
     * @param instName
     * @param bpars
     * @param ipars
     * @return
     */
    public Process duplicate(String instName, LinkedList<ParamBool> bpars, LinkedList<ParamInt> ipars){
    	// there is no enumVar we create one
    	LinkedList<ParamEnum> epars = new LinkedList<ParamEnum>();
    	return duplicate(instName, bpars, ipars, epars);
    	
    	
   // 	Process result = new Process(myProgram, name, instName, numberVar, numberBranch, numberChan, numberInst+1, model, disabledBlocking, fairness);
    	// we use hash mapping to record the duplicates of each one and to obtain the branches, expression
    	// and code
   // 	HashMap<VarBool, VarBool> boolDups = new HashMap<VarBool,VarBool>();
   // 	HashMap<VarInt, VarInt> intDups = new HashMap<VarInt, VarInt>();
   // 	HashMap<VarEnum, VarEnum> enumDups = new HashMap<VarEnum, VarEnum>();
   // 	HashMap<ParamBool, ParamBool> boolParamDups = new HashMap<ParamBool, ParamBool>();
   // 	HashMap<ParamInt, ParamInt> intParamDups = new HashMap<ParamInt, ParamInt>();
    	// note that there is no enumvars
    	
    	// we duplicate the local int vars
   // 	for (int i = 0; i < intVars.size(); i++){
   // 		VarInt dup = (VarInt) intVars.get(i).duplicate(instName, boolDups, intDups, this);
   // 		result.addVarInt(dup);
   // 		intDups.put(intVars.get(i), dup);
   // 	}
    	
    	
    	// we duplicate the local bool vars
   // 	for (int i = 0; i < boolVars.size(); i++){
   // 		VarBool dup = (VarBool) boolVars.get(i).duplicate(instName, boolDups, intDups, this);
   // 		result.addVarBool(dup);   		    		
   // 		boolDups.put(boolVars.get(i), dup);
   // 	}
    	
    	// we duplicate the local enum vars
   // 	for (int i = 0; i < enumVars.size(); i++){
   // 		VarEnum dup = (VarBool) enumVars.get(i).duplicate(instName, boolDups, intDups, enumDups, this);
   // 		result.addVarEnum(dup);   		    		
   // 		enumDups.put(enumVars.get(i), dup);
   // 	}
    	
    	// we add the global bool vars (we do not need to duplicate them since they are global!)
   // 	for (int i = 0; i < globalBoolVars.size(); i++){
   // 		result.addGlobalVarBool(globalBoolVars.get(i));
   // 	}
    	
    	// we add the global int vars (we do not need to duplicate them since they are global!)
   // 	for (int i = 0; i < globalIntVars.size(); i++){
   // 		result.addGlobalVarInt(globalIntVars.get(i));
   // 	}
    	
    	// we add the global enum vars (we do not need to duplicate them since they are global!)
   // 	for (int i = 0; i < globalEnumVars.size(); i++){
   // 		result.addGlobalVarEnum(globalEnumVars.get(i));
   // 	}
    	
    	// we duplicate the boolean parameters
   // 	for (int i = 0; i < this.boolPars.size(); i++){
    		
    		//ParamBool dup = (ParamBool) boolPars.get(i).duplicate(instName, boolDups, intDups, boolParamDups, intParamDups, this);    		
   // 		for (int j=0; j < bpars.size(); j++){    			   			
   // 			if (boolPars.get(i).getName().equals(bpars.get(j).getName())){      				
   // 				result.addBoolPar(bpars.get(j));
   // 				boolParamDups.put(boolPars.get(i), bpars.get(j));
   // 			}
   // 		}   		
   // 	}
    	    	
    	
    	// we duplicate the int parameters
   // 	for (int i = 0; i < intPars.size(); i++){
   // 		for (int j=0; j < ipars.size(); j++){
   // 			if (intPars.get(i).getName().equals(ipars.get(j).getName())){
   // 				result.addIntPar(ipars.get(j));
   // 				intParamDups.put(intPars.get(i), ipars.get(j));
   // 			}
   // 		}   	
   // 	}
    	
    	// we duplicate the branches
   // 	for (int i = 0; i < branches.size(); i++){
   // 		result.addBranch(branches.get(i).duplicate(instName, boolDups, intDups, boolParamDups, intParamDups, this));
   // 	}
    
    	// we add the channels 
   // 	for (int i = 0; i < channels.size(); i++){
   // 		result.addChannel(channels.get(i));
   // 	}	

    	// TO DO: fix these lines
   // 	result.setInitialCond((BoolExp) initialCond.duplicate(instName, boolDups, intDups, boolParamDups, intParamDups, this));
   // 	result.setNormativeCond((BoolExp) normCondition.duplicate(instName, boolDups, intDups, boolParamDups, intParamDups, this));
    	
    	
    	//result.initPrimes();
   // 	return result;
    }
       
    /**
     * More general version taking into account enumerable types
     * @param instName
     * @param bpars
     * @param ipars
     * @return
     */
    public Process duplicate(String instName, LinkedList<ParamBool> bpars, LinkedList<ParamInt> ipars, LinkedList<ParamEnum> epars){
    	
    	Process result = new Process(myProgram, name, instName, numberVar, numberBranch, numberChan, numberInst+1, model, disabledBlocking, fairness);
    	// we use hash mapping to record the duplicates of each one and to obtain the branches, expression
    	// and code
    	HashMap<Var, Var> dups = new HashMap<Var,Var>();
    	
    	// we duplicate the local int vars
    	for (int i = 0; i < intVars.size(); i++){
    		VarInt dup = (VarInt) intVars.get(i).duplicate(instName, dups, this);
    		result.addVarInt(dup);
    		dups.put(intVars.get(i), dup);
    	}
    	
    	// we duplicate the local bool vars
    	for (int i = 0; i < boolVars.size(); i++){
    		VarBool dup = (VarBool) boolVars.get(i).duplicate(instName, dups, this);
    		result.addVarBool(dup);
    		dups.put(boolVars.get(i), dup);
    	}
    	
    	// we duplicate the local enum vars
    	for (int i = 0; i < enumVars.size(); i++){
    		VarEnum dup = (VarEnum) enumVars.get(i).duplicate(instName, dups, this);
    		result.addVarEnum(dup);
    		dups.put(enumVars.get(i), dup);
    	}
    	
    	// we add the global bool vars (we do not need to duplicate them since they are global!)
    	for (int i = 0; i < globalBoolVars.size(); i++){
    		result.addGlobalVarBool(globalBoolVars.get(i));
    	}
    	
    	// we add the global int vars (we do not need to duplicate them since they are global!)
    	for (int i = 0; i < globalIntVars.size(); i++){
    		result.addGlobalVarInt(globalIntVars.get(i));
    	}
    	
    	// we add the global enum vars (we do not need to duplicate them since they are global!)
    	for (int i = 0; i < globalEnumVars.size(); i++){
    		result.addGlobalVarEnum(globalEnumVars.get(i));
    	}
    	
    	// we duplicate the boolean parameters
    	for (int i = 0; i < this.boolPars.size(); i++){
    		
    		//ParamBool dup = (ParamBool) boolPars.get(i).duplicate(instName, boolDups, intDups, boolParamDups, intParamDups, this);    		
    		for (int j=0; j < bpars.size(); j++){    			   			
    			if (boolPars.get(i).getName().equals(bpars.get(j).getName())){ 
    				result.addBoolPar(bpars.get(j));
    				dups.put(boolPars.get(i), bpars.get(j));
    			}
    		}   		
    	}
    	
    	// we duplicate the int parameters
    	for (int i = 0; i < intPars.size(); i++){
    		for (int j=0; j < ipars.size(); j++){
    			if (intPars.get(i).getName().equals(ipars.get(j).getName())){
    				result.addIntPar(ipars.get(j));
    				dups.put(intPars.get(i), ipars.get(j));
    			}
    		}   	
    	}
    	
    	// we duplicate the enum parameters
    	for (int i = 0; i < enumPars.size(); i++){
    		for (int j=0; j < epars.size(); j++){
    			if (enumPars.get(i).getName().equals(epars.get(j).getName())){
    				result.addEnumPar(epars.get(j));
    				dups.put(enumPars.get(i), epars.get(j));
    			}
    		}   	
    	}
    	
    	// we duplicate the branches
    	for (int i = 0; i < branches.size(); i++){
    		result.addBranch(branches.get(i).duplicate(instName, dups, this));
    	}
    
    	// we add the channels 
    	for (int i = 0; i < channels.size(); i++){
    		result.addChannel(channels.get(i));
    	}	

    	// TO DO: fix these lines
    	result.setInitialCond((BoolExp) initialCond.duplicate(instName, dups, this));
    	result.setNormativeCond((BoolExp) normCondition.duplicate(instName, dups, this));
    	
    	
    	//result.initPrimes();
    	return result;
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
    public BoolExp getInitialCond(){
    	return initialCond;
    }
    
    public BoolExp getNormCond(){
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
    
    /**
     * Adds a new enumerable var to the process
     * @param var	
     */
    public void addVarEnum(VarEnum var){
    	enumVars.add(var);
    }
    
    /**
     * Adds a global boolean var
     * @param var
     */
    public void addGlobalVarBool(VarBool var){
    	globalBoolVars.add(var);
    }
    
    /**
     * Adds an integer global var
     * @param var
     */
    public void addGlobalVarInt(VarInt var){
    	globalIntVars.add(var);
    }
    
    /**
     * Adds an enumerable global var 
     * @param var
     */
    public void addGlobalVarEnum(VarEnum var){
    	globalEnumVars.add(var);
    }
    
    /**
     * Adds a boolean parameter
     * @param par
     */
    public void addBoolPar(ParamBool par){
    	this.boolPars.add(par);
    }
    
    /**
     * Adds a integer parameter
     * @param par
     */
    public void addIntPar(ParamInt par){
    	this.intPars.add(par);
    }
    
    /**
     * Adds an enumerable parameter
     * @param par
     */
    public void addEnumPar(ParamEnum par){
    	this.enumPars.add(par);
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
    	
    	
    	
    	//create primes for enum vars
    	for (int i = 0; i < enumVars.size(); i++){
    		enumVars.get(i).initPrimes();
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
    	
    	// create running prime
    	if (fairness){   		
    		running_ = Program.myFactory.ithVar(Program.declaredVars+running.var());
    		Program.declaredVars_++;
    		model.addVar(instName+"."+"running"+"_", Type.BOOL);
    	}
    	
    }
    
    /**
     * This is a simple method to initialise the info needed to verify
     * any property with fairness over the model, it can be used after 
     * any consturctor was used to craete the process
     */
    public void initFairness(){
    	// fairness is set on
    	fairness = true;
    	
    	// the BDDs are created
    	running = Program.myFactory.ithVar(Program.declaredVars);
    	model.addVar(instName+"."+"running", Type.BOOL);
      	Program.declaredVars++;
    	
    	running_ = Program.myFactory.ithVar(Program.declaredVars+running.var());
		Program.declaredVars_++;
		model.addVar(instName+"."+"running"+"_", Type.BOOL);
    }
    
    /**
     * Sets an initial condition
     * @param	init	A BDD representing the initial condition
     */
    public void setInitialCond(BoolExp init){
    	initialCond = init;
    }
    
    /**
     * Sets an normative condition
     * @param	norm	A BDD representing the normative condition
     */
    public void setNormativeCond(BoolExp norm){
    	this.normCondition = norm;
    }
    
    
    /**
     * Calculates the BDD representing the current process, it takes into account the 
     * branching in the code and the stuck variables that indicate whether a process is
     * stuck or not.  
     * Below there is a more efficient version of this method
     */
    public BDD getBDD(){
    	// first, we calculate the BDD for the branching without taking into account the possible stuck at channels
        BDD process_bdd = Program.myFactory.zero();
        for (int i = 0; i < branches.size(); i++){     	
        		 process_bdd = process_bdd.or(branches.get(i).getBDD().and(skipChannelsNotIn(branches.get(i)).and(skipVarsNotIn(branches.get(i)))).and(skipOtherStuck(branches.get(i))));       
        }
        if (fairness){
        	process_bdd.and(running_).and(myProgram.otherProcessesNotRunning(this));
        }
        
        // we calculate a BDD that says if some guard is on, otherwise the process gets blocked.
      
        BDD guards = Program.myFactory.zero();
        for (int i = 0; i < branches.size(); i++){
        	guards = guards.or(branches.get(i).getGuard().getBDD());
        
        }
 
        // If some guard is on then some is executed, otherwise the process getsblocked until some guard gets true blocked.
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
     * It calculates the BDD of the process with a different approach to that of the above method,
     * the main idea is to push the conjunction as deep as possible
     * @param program	the program where the process lives
     * @param index	 it is the index of the current process (this simplifies the algo) in the program,
     * @return the BDD representing the program
     */
    public BDD getBDD(Program program, Integer index){
    	
    	// If there is no channels then we do not care about blockings
    	if (disabledBlocking){    	
    		BDD result = Program.myFactory.zero();
    		
    		// skipOthers BDD represents a skip done by the other processes different from the actual one.
    		// an interesting point is that  any global var 
    		// that is not in the present process and it is in any other process
    		//  it will be skipped by this method
    		BDD skipOthers = program.skipOthersThan(index);
    		
    		// for each branch we calculate the variables not there	
    		BDD process_bdd = Program.myFactory.zero();	
    		for (int i=0; i < branches.size(); i++){    			
    			//process_bdd = process_bdd.or(branches.get(i).getBDD().and(skipVarsNotIn(branches.get(i))).and(program.skipOthersThan(index)));    		    		
    			process_bdd = process_bdd.or(branches.get(i).getBDD().and(skipLocalVarsNotIn(branches.get(i))).and(skipGlobalVarsInProcessNotIn(branches.get(i))).and(skipOthers));
    		}
    		    
    		// we calculate a bdd that is true when some guard is true
    		BDD guards = Program.myFactory.zero();
    		for (int i = 0; i < branches.size(); i++){
    			guards = guards.or(branches.get(i).getGuard().getBDD());  	
    		}
    		
    		// If some guard is on then some is executed, otherwise the process gets blocked until some guard 
    		// gets true blocked.
    		//result = guards.and(process_bdd).or(guards.not().and(skipBDD()));  
    		// the version above is inefficient
    		//result = process_bdd.or(guards.not().and(skipBDD()));    	
    		result = process_bdd;
    		
    		this.guards = guards;
    		
    		// in the case of fairness we set running on
    		if (fairness){
    			result = result.and(running_.and(myProgram.otherProcessesNotRunning(this)));
    		}
    		
    		return result;
    	}
    	else{
    		// improve treatment of channels, this only calls to the
    		// standard getBDD methods
    		return getBDD(); 
    	}
    	
    }
    
    /**
    * 
    * @param program
    * @param index
    * @return	The symbolic representation of the program as a list of disjunct BDD 
    */
    public LinkedList<BDD> getPartialBDD(Program program, Integer index){
    	
    	
    	LinkedList<BDD> result = new LinkedList<BDD>();
    	// If there is no channels then we do not care about blockings
    	if (disabledBlocking){    	
    		//BDD result = Program.myFactory.zero();   		
    		// skipOthers BDD represents a skip done by the other processes different from the actual one.
    		// an interesting point is that  any global var 
    		// that is not in the present process and it is in any other process
    		//  it will be skipped by this method
    		
    		//BDD skipOthers = program.skipOthersThan(index);
    		
    		// for each branch we calculate the variables not there	
    		//BDD process_bdd = Program.myFactory.zero();	
    		for (int i=0; i < branches.size(); i++){    			    					    		
    			//process_bdd = process_bdd.or(branches.get(i).getBDD().and(skipLocalVarsNotIn(branches.get(i))).and(skipGlobalVarsInProcessNotIn(branches.get(i))).and(skipOthers));
    			Branch current = branches.get(i);
    			result.add(current.getBDD().and(skipLocalVarsNotIn(current)).and(skipGlobalVarsInProcessNotIn(current)));//.and(skipOthers));    			
    		}    		
    		
    		
    		// we calculate a bdd that is true when some guard is true
    		BDD guards = Program.myFactory.zero();
    		for (int i = 0; i < branches.size(); i++){
    			guards = guards.or(branches.get(i).getGuard().getBDD());  	
    		}
    		
    		// If some guard is on then some is executed, otherwise the process gets blocked until some guard 
    		// gets true blocked.
    		//result = guards.and(process_bdd).or(guards.not().and(skipBDD()));  
    		// the version above is inefficient
    		//result = process_bdd.or(guards.not().and(skipBDD()));
    		//if (!fairness)
    		//	result.add(guards.not().and(skipBDD()));
    		//else
    		if (fairness)
    			result.add(guards.not().and(skipBDD()).and(running_.and(myProgram.otherProcessesNotRunning(this))));
    		
    		return result;
    	}
    	else{
    		// improve treatment of channels, this only calls to the
    		// standard getBDD methods
    		result.add(getBDD());
    		return result; 
    	}
    	
    }
    
    /**
     * This method is similar to the one given above, but in contrast to the
     * implementation above, this method takes a codomain of the implicit
     * relation of the process: R(x0,...,xn,x0',...,xn') and obtain the 
     * preimage by this codomain. This is useful to calculate the WeakPrevious for formulas
     * and fixed points,
     * @param codomain	the codomain from which the preimage will be computed
     * @return 	the preimage of the relation of the process considering the codomain
     */
    public BDD preImageBDD(BDD codomain, Program program, int index){
    	
    	BDD result = Program.myFactory.zero();
    	for (int i=0; i < branches.size(); i++){    			    					    		
    		Branch current = branches.get(i);
    		
    		//		result.add(current.getBDD().and(skipLocalVarsNotIn(current)).and(skipGlobalVarsInProcessNotIn(current)).and(skipOthers));
    	
    		// gets the ids of the vars corresponding to the current branch which
    		// will be quantified
    		LinkedList<Integer> qvars = current.getPrimedIds();
    		
    		BDD branch = current.getBDD().and(codomain);
    		for (int j = 0;  j < qvars.size(); j++){
    			int k = qvars.get(j).intValue();
    			branch = branch.exist(Program.myFactory.ithVar(k));
    		}
    		branch = branch.and(skipLocalVarsNotIn(current));
    	
    		
    		qvars = getPrimedLocalVarsNotIn(current);
    		for (int j = 0;  j < qvars.size(); j++){
    			int k = qvars.get(j).intValue();
    			branch = branch.exist(Program.myFactory.ithVar(k));
    		}   	
    		branch = branch.and(skipGlobalVarsInProcessNotIn(current));
    		
    		qvars = getPrimedGlobalVarsInProcessNotIn(current);
    		for (int j = 0;  j < qvars.size(); j++){
    			int k = qvars.get(j).intValue();
    			branch = branch.exist(Program.myFactory.ithVar(k));
    		}
    		branch = branch.and(program.skipOthersThan(index));
    		
    		qvars = program.getPrimedVarsOthersThan(index);    		
    		for (int j = 0;  j < qvars.size(); j++){
    			int k = qvars.get(j).intValue();
    			branch = branch.exist(Program.myFactory.ithVar(k));
    		}		
    		result = result.or(branch);
    	}

    	return result;
    }
    
    
    /**
     * Returns the BDD representing the normative part of the process
     * @return	the BDD of the normative part of the process
     */
    public BoolExp getNorm(){
    	return normCondition;
    }
    
    /**
     * 
     * @return	the atribute disableBlocking
     */
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
    	if (skipLocalVarsBDD == null){
    	//BDD result = Program.myFactory.one();
    		skipLocalVarsBDD = Program.myFactory.one();
    		
    		for (int i = 0; i < intVars.size(); i++){    		
    			skipLocalVarsBDD = skipLocalVarsBDD.and(intVars.get(i).skipBDD());
    		}
    	
    		for (int i = 0; i < boolVars.size(); i++){
    			skipLocalVarsBDD = skipLocalVarsBDD.and(boolVars.get(i).skipBDD());
    		}
    		
    		for (int i = 0; i < enumVars.size(); i++){
    			skipLocalVarsBDD = skipLocalVarsBDD.and(enumVars.get(i).skipBDD());
    		}
    	}
    	
    		// commented by efficiency reasons 
    		// check it!
    		//for (int i = 0; i < globalIntVars.size(); i++){
    		//	result = result.and(globalIntVars.get(i).skipBDD());
    		//}
    	
    	for (int i = 0; i < getGlobalBoolVarsInProcess().size(); i++){    		
    		result = result.and(getGlobalBoolVarsInProcess().get(i).skipBDD());
    	}
 
    	if (!disabledBlocking){
    		for (int i = 0; i < branches.size(); i++){
    			Branch actual = branches.get(i);
    			skipLocalVarsBDD = skipLocalVarsBDD.and(stuck.get(actual).biimp(stuck_.get(actual)));
    		}
    	}
    	
    	
    	return result.and(skipLocalVarsBDD);
    }
    
    /**
     * 
     * @param boolVars
     * @param intVars
     * @return	a BDD representation of the action of skip for the current process
     */
    public BDD skipBDD(LinkedList<VarBool> boolVars, LinkedList<VarInt> intVars){
    	BDD result = Program.myFactory.one();
    	LinkedList<VarBool> myBoolVars = this.getGlobalBoolVarsInProcess(); 
    	LinkedList<VarInt> myIntVars = this.getGlobalIntVarsInProcess(); 
    	LinkedList<VarEnum> myEnumVars = this.getGlobalEnumVarsInProcess();
    	
    	if (skipLocalVarsBDD == null){    		
    		skipLocalVarsBDD = Program.myFactory.one();
    		// skip the local int vars
    		
    		
    		for (int i = 0; i < intVars.size(); i++){	
    			skipLocalVarsBDD = skipLocalVarsBDD.and(intVars.get(i).skipBDD());
    		}
    	
    		// skip the local bool vars    		
    		for (int i = 0; i < this.boolVars.size(); i++){
    			skipLocalVarsBDD = skipLocalVarsBDD.and(this.boolVars.get(i).skipBDD());
    		}		
    		
    		// skip the local enum vars
    		for (int i = 0; i < this.enumVars.size(); i++){
    			skipLocalVarsBDD = skipLocalVarsBDD.and(this.enumVars.get(i).skipBDD());
    		}
    	}
    	
    	// we use skipLocalVars for efficency reasons.
    	//result = result.and(skipLocalVarsBDD);
    	
    	// boolVars 
    	
    	// skip the boolean global vars used in the process do not appearing
    	// in boolVars 
    	for (int i=0; i < myBoolVars.size(); i++){    			
    			if (!boolVars.contains(myBoolVars.get(i))){   	
    				result = result.and(myBoolVars.get(i).skipBDD());   		
    			}
    	}
    	
    	// skip the int global vars used in the process do not appearing
    	// in boolVars
    	for (int i=0; i < myIntVars.size(); i++){
    		if (!intVars.contains(myIntVars.get(i))){
    			result = result.and(myIntVars.get(i).skipBDD());
    		}
    	}
    	
    	// Skip the enum global vars used in the process that do not appear
    	// in enumvars
    	for (int i=0; i < myEnumVars.size(); i++){
    		if (!enumVars.contains(myEnumVars.get(i))){
    			result = result.and(myEnumVars.get(i).skipBDD());
    		} 
    	}
    	
    	return result.and(skipLocalVarsBDD);
    }
    
    public BDD quantifiedSkipBDD(BDD range, LinkedList<VarBool> boolVars, LinkedList<VarInt> intVars){
    	BDD result = Program.myFactory.one();
    	LinkedList<VarBool> myBoolVars = this.getGlobalBoolVarsInProcess(); 
    	LinkedList<VarInt> myIntVars = this.getGlobalIntVarsInProcess(); 
    	
    	// TO DO: add int and enum vars, this methods is deprectaed anyway
    	
    	result = result.and(range);
    	
    	// skip the local bool vars
		for (int i = 0; i < this.boolVars.size(); i++){
			result = result.and(this.boolVars.get(i).skipBDD());
			int k = this.boolVars.get(i).getPrimedIds().getFirst();
			result = result.exist(Program.myFactory.ithVar(k));
		}	
    	
    	
    	// skip the boolean global vars used in the process do not appearing
    	// in boolVars 
    	for (int i=0; i < myBoolVars.size(); i++){
    			if (!boolVars.contains(myBoolVars.get(i))){   		
    				result = result.and(myBoolVars.get(i).skipBDD());  
    				int k = myBoolVars.get(i).getPrimedIds().getFirst();
    				result = result.exist(Program.myFactory.ithVar(k));
    			}
    	}
    	
    	
    	return result;
    }
    
    
    /**
     * 
     * @param boolVars
     * @param intVars
     * @return		the list of id corresponding the the vars in the current process and different to boolVars and intVars
     */
    public LinkedList<Integer> getPrimedVarsExceptFor(LinkedList<VarBool> boolVars, LinkedList<VarInt> intVars){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	
    	LinkedList<VarBool> myBoolVars = this.getGlobalBoolVarsInProcess(); 
    	for (int i=0; i < myBoolVars.size(); i++){
    		if (!boolVars.contains(myBoolVars.get(i))){
    			result.addAll(myBoolVars.get(i).getPrimedIds());
    		}
    	}
    	
    	LinkedList<VarInt> myIntVars = this.getGlobalIntVarsInProcess(); 
    	for (int i=0; i < myIntVars.size(); i++){
    		if (!intVars.contains(myIntVars.get(i))){
    			result.addAll(myIntVars.get(i).getPrimedIds());
    		}
    	}
    	
    	return result;
    }
    		
    
    /**
     * 
     * @param boolVars
     * @param intVars
     * @param enumVars
     * @return		the list of id corresponding the the vars in the current process and different to boolVars and intVars
     */
    public LinkedList<Integer> getPrimedVarsExceptFor(LinkedList<VarBool> boolVars, LinkedList<VarInt> intVars, LinkedList<VarEnum> enumVars){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	
    	LinkedList<VarBool> myBoolVars = this.getGlobalBoolVarsInProcess(); 
    	for (int i=0; i < myBoolVars.size(); i++){
    		if (!boolVars.contains(myBoolVars.get(i))){
    			result.addAll(myBoolVars.get(i).getPrimedIds());
    		}
    	}
    	
    	LinkedList<VarInt> myIntVars = this.getGlobalIntVarsInProcess(); 
    	for (int i=0; i < myIntVars.size(); i++){
    		if (!intVars.contains(myIntVars.get(i))){
    			result.addAll(myIntVars.get(i).getPrimedIds());
    		}
    	}
    	
    	LinkedList<VarEnum> myEnumVars = this.getGlobalEnumVarsInProcess(); 
    	for (int i=0; i < myEnumVars.size(); i++){
    		if (!enumVars.contains(myEnumVars.get(i))){
    			result.addAll(myEnumVars.get(i).getPrimedIds());
    		}
    	}
    	
    	return result;
    }
 
    
    /**
     * A BDD representation of the guards
     */
    public BDD getGuards(){
    	if (this.guards==null){
    		BDD guards = Program.myFactory.zero();
    		for (int i = 0; i < branches.size(); i++){
    			guards = guards.or(branches.get(i).getGuard().getBDD());
    		}
    		return guards; 
    	}
    	else
    		return this.guards;
    }
    
    /**
     * 
     * @return	a list of the boolean global variables occuring in the process
     */
    public LinkedList<VarBool> getGlobalBoolVarsInProcess(){
    	LinkedList<VarBool> result = new LinkedList<VarBool>();   	
    	for (int j=0; j < branches.size(); j++){    
    		for (int i=0; i < globalBoolVars.size(); i++){       			
    			if (branches.get(j).getVars().contains(globalBoolVars.get(i)) && !result.contains(globalBoolVars.get(i))){			
    				result.add(globalBoolVars.get(i));
    			}
    		}
    		
    	//	for (int i=0; i < boolPars.size(); i++){
    	//		if (branches.get(j).getVars().contains(boolPars.get(i).getReference())){			
    	//			result.add(boolPars.get(i).getReference());
    	//		}
    	//	}
    		
    	}
    	return result;
    }
    

    /**
     * 
     * @return	a list of the int global variables occuring in the process
     */
    public LinkedList<VarInt> getGlobalIntVarsInProcess(){
    	LinkedList<VarInt> result = new LinkedList<VarInt>();
    	for (int j=0; j < branches.size(); j++){    
    		for (int i=0; i < globalIntVars.size(); i++){   			
    			if (branches.get(j).getVars().contains(globalIntVars.get(i))){  				
    				result.add(globalIntVars.get(i));
    			}
    		}
    		
    		// params are global boolvars in process
    		for (int i=0; i< intPars.size(); i++){
    			if (branches.get(j).getVars().contains(intPars.get(i).getReference())){			
    				result.add(intPars.get(i).getReference());
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * 
     * @return	a list of the enum global enum vars appearing in the process
     */
    public LinkedList<VarEnum> getGlobalEnumVarsInProcess(){
    	LinkedList<VarEnum> result = new LinkedList<VarEnum>();
    	for (int j=0; j < branches.size(); j++){    
    		for (int i=0; i < globalEnumVars.size(); i++){   			
    			if (branches.get(j).getVars().contains(globalEnumVars.get(i))){  				
    				result.add(globalEnumVars.get(i));
    			}
    		}
    		
    		// params are global boolvars in process
    		for (int i=0; i< enumPars.size(); i++){
    			if (branches.get(j).getVars().contains(enumPars.get(i).getReference())){			
    				result.add(enumPars.get(i).getReference());
    			}
    		}
    	}
    	return result;
    }
    
    
    /**
     * 
     * @return	A list of global boolean variables occurring in the process
     */
    public LinkedList<VarBool> getGlobalVarsNotInProcess(){
    	LinkedList<VarBool> result = new LinkedList<VarBool>();
    	LinkedList<VarBool> varsIn = this.getGlobalBoolVarsInProcess();
    	for (int i=0; i < globalBoolVars.size(); i++){
    		if (!varsIn.contains(globalBoolVars.get(i))){
    			result.add(globalBoolVars.get(i));
    		}
    	}
    	return result;
    }
    
    /**
     * 
     * @return	the global int vars do not appearing in the process
     */
    public LinkedList<VarInt> getGlobalIntVarsNotInProcess(){
    	LinkedList<VarInt> result = new LinkedList<VarInt>();
    	LinkedList<VarInt> varsIn = this.getGlobalIntVarsInProcess();
    	for (int i=0; i < globalIntVars.size(); i++){
    		if (!varsIn.contains(globalIntVars.get(i))){
    			result.add(globalIntVars.get(i));
    		}
    	}
    	return result;
    }
    
    /**
     * 
     * @return	the global enum vars do not appearing in the process
     */
    public LinkedList<VarEnum> getGlobalEnumVarsNotInProcess(){
    	LinkedList<VarEnum> result = new LinkedList<VarEnum>();
    	LinkedList<VarEnum> varsIn = this.getGlobalEnumVarsInProcess();
    	for (int i=0; i < globalEnumVars.size(); i++){
    		if (!varsIn.contains(globalEnumVars.get(i))){
    			result.add(globalEnumVars.get(i));
    		}
    	}
    	return result;
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
     * 
     * @param 	b the branch to be evaluated
     * @return	returns the BDD capturing the skip of the global vars in the process but not in
     * 			the current branch
     */
    private BDD skipGlobalVarsInProcessNotIn(Branch branch){
    	BDD result = Program.myFactory.one(); 	
    	LinkedList<VarBool> boolGlobalVars = getGlobalBoolVarsInProcess();    
    	LinkedList<VarInt> intGlobalVars = getGlobalIntVarsInProcess(); 
    	LinkedList<VarEnum> enumGlobalVars = getGlobalEnumVarsInProcess();
    	
    	// skip bool vars
    	for (int i = 0; i < boolGlobalVars.size(); i++){   		
    		// we just take into account the vars in the code of the branch
    		if (!branch.getCodeVars().contains(boolGlobalVars.get(i))){
    			
    			result = result.and(boolGlobalVars.get(i).skipBDD());
    		}
    	}
    	
    	// skip int vars
    	for (int i = 0; i < intGlobalVars.size(); i++){   		
    		// we just take into account the vars in the code of the branch
    		if (!branch.getCodeVars().contains(intGlobalVars.get(i))){			
    			result = result.and(intGlobalVars.get(i).skipBDD());
    		}
    	}
    	
    	// skip enum vars
    	for (int i = 0; i < enumGlobalVars.size(); i++){   		
    		// we just take into account the vars in the code of the branch
    		if (!branch.getCodeVars().contains(enumGlobalVars.get(i))){			
    			result = result.and(enumGlobalVars.get(i).skipBDD());
    		}
    	}
    	
    	return(result);
    }
    
    /**
     * 
     * @param branch
     * @return A list of ids corresponding to the global vars in the process but not in the parameter (branch)
     */
    private LinkedList<Integer> getPrimedGlobalVarsInProcessNotIn(Branch branch){
	   	LinkedList<Integer> result = new LinkedList<Integer>();
	   	LinkedList<VarBool> boolGlobalVars = getGlobalBoolVarsInProcess();
	   	LinkedList<VarInt> intGlobalVars = getGlobalIntVarsInProcess();
	   	LinkedList<VarEnum> enumGlobalVars = getGlobalEnumVarsInProcess();
	   	
	   	// boolean vars
	   	for (int i = 0; i < boolGlobalVars.size(); i++){
	   		if (!branch.getVars().contains(boolGlobalVars.get(i))){
	   			result.addAll(boolGlobalVars.get(i).getPrimedIds());
	   		}
	   	}
	   	
	   	// int vars
		for (int i = 0; i < intGlobalVars.size(); i++){
	   		if (!branch.getVars().contains(intGlobalVars.get(i))){
	   			result.addAll(intGlobalVars.get(i).getPrimedIds());
	   		}
	   	}
		
		// enum vars
		for (int i = 0; i < enumGlobalVars.size(); i++){
	   		if (!branch.getVars().contains(enumGlobalVars.get(i))){
	   			result.addAll(enumGlobalVars.get(i).getPrimedIds());
	   		}
	   	}
		// the result
	   	return result;
   }
    
    /**
     * 
     * @return	a BDD stating that global variables outside the process do not change
     */
    private BDD skipGlobalVarsNotInProcess(){
    	BDD result = Program.myFactory.one();
    	LinkedList<VarBool> boolGlobalVars = getGlobalVarsNotInProcess();
    	LinkedList<VarInt> intGlobalVars = getGlobalIntVarsNotInProcess();
    	LinkedList<VarEnum> enumGlobalVars = getGlobalEnumVarsNotInProcess();
    	
    	for (int i = 0; i < boolGlobalVars.size(); i++){
    		result = result.and(boolGlobalVars.get(i).skipBDD());
    	}
    	for (int i = 0; i < intGlobalVars.size(); i++){
    		result = result.and(intGlobalVars.get(i).skipBDD());
    	}
    	for (int i = 0; i < enumGlobalVars.size(); i++){
    		result = result.and(enumGlobalVars.get(i).skipBDD());
    	}
    	return result;
    }
    
    
    private BDD skipLocalVarsNotIn(Branch branch){
    	BDD result = Program.myFactory.one();
    	
    	
    	for (int i = 0; i < intVars.size(); i++){
    		if (!branch.getCodeVars().contains(intVars.get(i))){
    			result = result.and(intVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < boolVars.size(); i++){
    		
    		if (!branch.getCodeVars().contains(boolVars.get(i))){
    			result = result.and(boolVars.get(i).skipBDD());       		
    		}
    	}
    	
    	for (int i = 0; i < enumVars.size(); i++){
    		if (!branch.getCodeVars().contains(enumVars.get(i))){
    			result = result.and(enumVars.get(i).skipBDD());
    		}
    	}
    	
    	
    	
    	// CHECK THIS
    	//for (int i = 0; i < boolPars.size(); i++){
    	//	if (!branch.getCodeVars().contains(globalBoolVars.get(i))){    			
    	//		result = result.and(boolPars.get(i).skipBDD());
    	//	}    		
    	//}
    
    	//for (int i = 0; i < intPars.size(); i++){
    	//	if (!branch.getCodeVars().contains(intPars.get(i))){
    	//		result = result.and(intPars.get(i).skipBDD());
    	//	}
    	//}
    
    	return result;
    }
    
    /**
     * 
     * @param branch
     * @return	A list of ids corresponding to the primed version of the vars not in the branch
     */
    private LinkedList<Integer> getPrimedLocalVarsNotIn(Branch branch){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	for (int i = 0; i < intVars.size(); i++){
    		if (!branch.getVars().contains(intVars.get(i))){
    			result.addAll(intVars.get(i).getIds());
    		}
    	}
    	
    	for (int i = 0; i < boolVars.size(); i++){
    		if (!branch.getVars().contains(boolVars.get(i))){
    			result.addAll(boolVars.get(i).getIds());
    		}
    	}
    	
    	for (int i = 0; i < enumVars.size(); i++){
    		if (!branch.getVars().contains(enumVars.get(i))){
    			result.addAll(enumVars.get(i).getIds());
    		}
    	}
    	
    	return result;
    }
    
    /**
     * 
     * @param branch
     * @return	A list of ids corresponding to the the vars not in the branch
     */
    private LinkedList<Integer> getLocalVarsNotIn(Branch branch){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	for (int i = 0; i < intVars.size(); i++){
    		if (!branch.getVars().contains(intVars.get(i))){
    			result.addAll(intVars.get(i).getPrimedIds());
    		}
    	}
    	
    	for (int i = 0; i < boolVars.size(); i++){
    		if (!branch.getVars().contains(boolVars.get(i))){
    			result.addAll(boolVars.get(i).getPrimedIds());
    		}
    	}
    	
    	for (int i = 0; i < enumVars.size(); i++){
    		if (!branch.getVars().contains(enumVars.get(i))){
    			result.addAll(enumVars.get(i).getPrimedIds());
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
    	
    	
    	for (int i = 0; i < globalBoolVars.size(); i++){
    		if (!branch.getCodeVars().contains(globalBoolVars.get(i))){      			
    			result = result.and(globalBoolVars.get(i).skipBDD());    		
    		}
    	}
    	
    	for (int i = 0; i < intVars.size(); i++){
    		if (!branch.getCodeVars().contains(intVars.get(i))){
    			result = result.and(intVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < boolVars.size(); i++){
    		if (!branch.getCodeVars().contains(boolVars.get(i))){
    			result = result.and(boolVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < enumVars.size(); i++){
    		if (!branch.getCodeVars().contains(enumVars.get(i))){
    			result = result.and(enumVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < globalIntVars.size(); i++){
    		if (!branch.getCodeVars().contains(globalIntVars.get(i))){
    			result = result.and(globalIntVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < globalEnumVars.size(); i++){
    		if (!branch.getCodeVars().contains(globalEnumVars.get(i))){
    			result = result.and(globalEnumVars.get(i).skipBDD());
    		}
    	}
    	
    	for (int i = 0; i < boolPars.size(); i++){
    		if (!branch.getCodeVars().contains(boolPars.get(i).getReference())){    			
    			result = result.and(boolPars.get(i).skipBDD());
    		}    		
    	}
    
    	for (int i = 0; i < intPars.size(); i++){
    		if (!branch.getCodeVars().contains(intPars.get(i).getReference())){
    			result = result.and(intPars.get(i).skipBDD());
    		}
    	} 
    	
    	for (int i = 0; i < enumPars.size(); i++){
    		if (!branch.getCodeVars().contains(enumPars.get(i).getReference())){
    			result = result.and(enumPars.get(i).skipBDD());
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
    
    
    @Override
	public String toString(){
		
		String procInfo = new String("========== Process Info ====================================== \n\n");
		String spaces = new String ("        ");
		
		
		String nameS = new String (spaces +"- name: " + name +"\n"); // the name of the process
        String instNameS = new String(spaces + "- instName: "+ instName + "\n"); // the name of the instance
        String numberVarS = new String(spaces +"- numberVar: "+ numberVar + "\n"); 
        String numberChanS = new String(spaces +"- numberChan: "+numberChan+ "\n"); 
        String numberBranchS= new String(spaces +"- numberBranch: "+numberBranch+"\n");
        
        
        String boolVarsS= new String(spaces +"- boolVars: "+"\n"); 
        for(VarBool v: boolVars){
        	boolVarsS = boolVarsS.concat(new String(spaces + spaces + "+" + v.toStringComplete() ) );
        	
        }
        boolVarsS = boolVarsS.concat("\n");
        
        String intVarsS= new String(spaces +"- intVars: "+"\n");
        for(VarInt v: intVars){
        	intVarsS = intVarsS.concat(new String(spaces + spaces +  "+" + v.toStringComplete() ) );
        	
        }
        intVarsS = intVarsS.concat("\n");

        String enumVarsS= new String(spaces +"- enumVars: "+"\n");
        for(VarEnum v: enumVars){
        	enumVarsS = enumVarsS.concat(new String(spaces + spaces +  "+" + v.toStringComplete() ) );
        	
        }
        enumVarsS = enumVarsS.concat("\n");

        
        
        String boolParamS= new String(spaces +"- boolParameters: "+"\n");
        for(ParamBool p: boolPars){
        	boolParamS = boolParamS.concat(new String(spaces + spaces + "+" + p.toStringComplete() ) );
        	
        }
        boolParamS = boolParamS.concat("\n");
        
        String intParamS= new String(spaces +"- intParameters: "+"\n");
        for(ParamInt p: intPars){
        	intParamS = intParamS.concat(new String(spaces + spaces +  "+" + p.toStringComplete() ) );
        	
        }
        intParamS = intParamS.concat("\n");
        
        
        String enumParamS= new String(spaces +"- enumParameters: "+"\n");
        for(ParamEnum p: enumPars){
        	enumParamS = enumParamS.concat(new String(spaces + spaces +  "+" + p.toStringComplete() ) );
        	
        }
        enumParamS = enumParamS.concat("\n");
        
        
        String globalBoolVarsS= new String(spaces +"- globalBoolVars: " +"\n");
        for(VarBool v: globalBoolVars){
        	globalBoolVarsS = globalBoolVarsS.concat(new String(spaces + spaces + "+"+ v.toStringComplete() ) );
        	
        }
        globalBoolVarsS = globalBoolVarsS.concat("\n");
        
        String  globalIntVarsS= new String(spaces +"- globalIntVars: "+"\n"); 
        for(VarInt v: globalIntVars){
        	globalIntVarsS = globalIntVarsS.concat(new String(spaces + spaces + "+"+ v.toStringComplete() ) );
        	
        }
        globalIntVarsS = globalIntVarsS.concat("\n");
        
        
        String  globalEnumVarsS= new String(spaces +"- globalEnumVars: "+"\n");
        for(VarEnum v: globalEnumVars){
        	globalEnumVarsS = globalEnumVarsS.concat(new String(spaces + spaces + "+"+ v.toStringComplete() ) );
        	
        }
        globalEnumVarsS = globalEnumVarsS.concat("\n");
        
        
        
        String channelsS= new String(spaces +"- channels: "+"\n"); 
        for(Channel ch: channels){
        	channelsS = channelsS.concat(new String(spaces + "+"+ ch.toString() ) );
        	
        }
        channelsS = channelsS.concat("\n");

        String branchesS= new String(spaces +"- branches: \n\n");
        for(Branch br: branches){
        	branchesS = branchesS.concat(new String(spaces + "+"+ br.toString() ) );
        	
        }
        branchesS = branchesS.concat("\n");

        String normConditionS = new String(spaces +"- normCondition: " +normCondition.toString()+"\n");
        String initialCondS= new String(spaces +"- initialCond: " +initialCond.toString()+"\n");
        String numberInstS= new String(spaces +"- numberInst: "+numberInst +"\n"); 
        String disabledBlockingS= new String(spaces +"- disabledBlocking: "+disabledBlocking +"\n\n"); 
       
        procInfo = procInfo.concat(nameS).concat(instNameS).concat(numberVarS).concat(numberChanS).concat(numberBranchS);
        procInfo= procInfo.concat(intVarsS).concat(boolVarsS).concat(enumVarsS).concat(intParamS).concat(boolParamS).concat(enumParamS).concat(globalBoolVarsS);
        procInfo= procInfo.concat(globalIntVarsS).concat(globalEnumVarsS).concat(channelsS).concat(branchesS).concat(normConditionS).concat(initialCondS);
        procInfo= procInfo.concat(numberInstS).concat(disabledBlockingS);
        procInfo= procInfo.concat("     ================================================================ \n\n");
        
        
		return procInfo;
	
    }
}// end of class