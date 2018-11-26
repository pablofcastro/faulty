
package faulty;
import net.sf.javabdd.*;
import java.util.*;
import mc.*;

/**
 * This class defines a FAULTY program, it provides the basic structures:
 * a list of channels, separating in bool and int channels, and a factory to construct the BDD's
 * correspondong to the program. The getBDD method is the way in which one may obtain  the 
 * symbolic representation of the program.
 * 
 * @author	Pablo Castro
 * @author	Ceci Kilmurray
 * @date 7/2015
 * 
 */
public class Program{

	int sizeSpace; 
	int numberIntVars; // number of int vars in the program
	int numberBoolVars; // number of Bool vars int he program
	int numberEnumVars;
	int numberBoolChannels; // number of bool channel in the program
	int numberIntChannels; // number of int channels in the program
	int maxLengthChannels; // maximum length of the channels in the program
	boolean disabledScheduler; // a boolean attribute used for efficency reasons
	boolean generatedPrimes; // a boolean attribute to indicate if the primes of variables have been generated

	//A faulty program has a list of channels and a list of processes, and 
	// lists of global variables
	LinkedList<IntChannel> intChannels;	
	LinkedList<BoolChannel> boolChannels;
	LinkedList<Process> processes;
	LinkedList<VarBool> boolVars;
	LinkedList<VarInt> intVars;
	LinkedList<VarEnum> enumVars;
	
	// A list of enum types in the program
	LinkedList<EnumType> enumTypes;
	
	// we use an array of BDDs to keep track of the scheduler,e.g., 
	// if process i was the last in being executed then scheduler[i] will be true and the rest false,
	// useful for fairness
	BDD[] scheduler;  
 
	// primed version of scheduler
	BDD[] scheduler_;


	// A BDD factory to creates the BDD's
	public static BDDFactory myFactory = null;
	private  BDDModel model;

	public static int declaredVars = 0; // number of declared BDD vars until now
	public static int declaredVars_ = 0; // number of declared BDD for prime vars
	public static int intSize = 4; // the size of ints

	/**
	 * Basic constructor for the class, it creates the required lists and a factory for the BDDs
	 * @param inSize				the size of the integers
	 * @param numberIntVars			the number of int vars
	 * @param numberBoolVars		the number of Bool vars
	 * @param numberEnumVars		the number of enum Vars
	 * @param enumSize				the max size of any enum type
	 * @param numberBoolChannels	the number of boolean channels
	 * @param numberIntChannels		the number of int channels
	 * @param maxLengthChannels		the maximum length of a channels in the program
	 * 
	 * All the information given for the parameters is used for creating the BDD's	
	 */
	// TO DO: change enumSize by LinkedList<TypeEnum>
	public Program(int intSize, int numberIntVars, int numberBoolVars, LinkedList<EnumType> enums, int numberBoolChannels, int numberIntChannels, int maxLengthChannels, BDDModel model){
		this.intSize = intSize;
		this.numberIntVars = numberIntVars;
		this.numberBoolVars = numberBoolVars;
		this.numberEnumVars = numberEnumVars;
		this.numberBoolChannels = numberBoolChannels;
		this.numberIntChannels = numberIntChannels;
		this.maxLengthChannels = maxLengthChannels;
		disabledScheduler = false; // by default it is not disabled
		intChannels = new LinkedList<IntChannel>();
		boolChannels = new LinkedList<BoolChannel>();
		processes = new LinkedList<Process>();
		boolVars = new LinkedList<VarBool>();
		intVars = new LinkedList<VarInt>();
		enumVars = new LinkedList<VarEnum>();
		this.model = model;
		generatedPrimes = false;
		enumTypes = new LinkedList<EnumType>();
		declaredVars = 0;
		declaredVars_ = 0;
		
		
		// we calculate the number of BDD variables needed for the enumTypes
		int numberEnumBits = 0;
		for (int j=0; j < enums.size(); j++){
			EnumType current = enums.get(j);
			numberEnumBits += current.getNumVars() * current.getBitsNumber() * 2; 
		}
		
		// first we create a BDDFactory, for this we calculate the number of the state space
		sizeSpace = (numberIntChannels * maxLengthChannels * intSize) + (numberBoolChannels * maxLengthChannels) + (numberIntVars * intSize * 2) + (numberEnumBits) + (numberBoolVars);
		
		// Initialize with reasonable nodes and cache size and Nx2 variables
		int numberOfNodes =  (int) (Math.pow(4.42, (sizeSpace*2)-6 ))*1000;
		int cacheSize = 2000;
		numberOfNodes = Math.max(1000, numberOfNodes);
		initBDDFactory(numberOfNodes, cacheSize);
		//System.out.println(sizeSpace);
		myFactory.setVarNum(sizeSpace); 	
	}

	/**
	 * Another constructor for the class, it creates the required lists and a factory for the BDDs
	 * @param inSize				the size of the integers
	 * @param numberIntVars			the number of int vars
	 * @param numberBoolVars		the number of Bool vars
	 * @param numberBoolChannels	the number of boolean channels
	 * @param numberIntChannels		the number of int channels
	 * @param maxLengthChannels		the maximum length of a channels in the program
	 * 
	 * All the information given for the paramenters is used for creating the BDD's	
	 */

	public Program(int intSize, int numberIntVars, int numberBoolVars, LinkedList<EnumType> enums, int numberBoolChannels, int numberIntChannels, int maxLengthChannels, BDDModel model, boolean scheduler){
		this.intSize = intSize;
		this.numberIntVars = numberIntVars;
		this.numberBoolVars = numberBoolVars;
		this.numberEnumVars = numberEnumVars;
		this.numberBoolChannels = numberBoolChannels;
		this.numberIntChannels = numberIntChannels;
		this.maxLengthChannels = maxLengthChannels;
		disabledScheduler = scheduler; // by default it is not disabled
		intChannels = new LinkedList<IntChannel>();
		boolChannels = new LinkedList<BoolChannel>();
		processes = new LinkedList<Process>();
		boolVars = new LinkedList<VarBool>();
		intVars = new LinkedList<VarInt>();
		enumVars = new LinkedList<VarEnum>();
		this.model = model;
		generatedPrimes = false;
		enumTypes = new LinkedList<EnumType>();
		declaredVars = 0;
		declaredVars_ = 0;
	
		// we calculate the number of BDD variables needed for the enumTypes
		int numberEnumBits = 0;
		for (int j=0; j < enums.size(); j++){
			EnumType current = enums.get(j);
			numberEnumBits += current.getNumVars() * current.getBitsNumber() * 2; 
		}
		sizeSpace = (numberIntChannels * 4) + (numberBoolChannels*4) + (numberIntChannels * (4*maxLengthChannels) * intSize) + (4* numberBoolChannels * maxLengthChannels)  + (2*numberIntVars * intSize) + (numberEnumBits) + (2*numberBoolVars);		

		// Initialize with reasonable nodes and cache size and Nx2 variables,
		// if the number of nodes is low then the garbage collector will slow the procedure.
		// on the other hand, a big number of nodes for smaller case studies will slow the model checker
		int numberOfNodes = sizeSpace*1000; //(int) (Math.pow(4.42, (sizeSpace*2)-6 ))*10000;
		int cacheSize = 2000000;
		numberOfNodes = Math.max(20000000, numberOfNodes);
		initBDDFactory(numberOfNodes, cacheSize);
		//System.out.println(sizeSpace);
		myFactory.setVarNum(sizeSpace); 
	}
	
	/**
	 * This is a private method to create the BDD factory, it only calculates the number of boolean variables needed 
	 * for the program.
	 */
	private static void initBDDFactory(int numberOfNodes, int cacheSize){
		if (myFactory == null){
			myFactory = BDDFactory.init(numberOfNodes, cacheSize);
		}
		else{
			myFactory.done();
			myFactory = BDDFactory.init(numberOfNodes, cacheSize);
			
		}
	}

	/**
	 * Returns the initial states, get from the initial states of the processes
	 * needed by the model checker
	 */
	public BDD getInitialCond(){
		BDD result = Program.myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			result = result.and(processes.get(i).getInitialCond().getBDD());
		}
		return result;
	}
    
    /**
	 * Returns the list of enum types declared in the program.
     */
    public LinkedList<EnumType>  getEnumTypeList(){
        return this.enumTypes;
    }


	/**
	 * Returns the BDD representing the program, we use a simple scheduler: [1,..,1] one position
	 * for each process, one a process is executed we set the corresponding position to 0 and continue with the following one.
	 */
	public BDD getBDDwithScheduler(){
		
		// we initialize the scheduler
		initScheduler();
		
		
		BDD processes_bdd = myFactory.zero();
		for (int i=0; i < processes.size(); i++){
			//BDD executeProcess = processes.get(i).getGuards().and(processes.get(i).getBDD()).and(skipOthersThan(i).and(skipChannelsOthersThan(processes.get(i).getChannels())));
			BDD executeProcess = nextProcess(i).and(processes.get(i).getBDD()).and(skipOthersThan(i).and(skipChannelsOthersThan(processes.get(i).getChannels()))).and(processExecuted(i));
			processes_bdd = processes_bdd.or(executeProcess); 
		}
    
		// restart the round
		//processes_bdd = processes_bdd.and();
		processes_bdd = processes_bdd.or(allExecuted().and(startRound()).and(skipBDD()));
    
		// we generate the invariants for the channels
		BDD channelsInv = Program.myFactory.one();
		for (int i = 0; i < boolChannels.size(); i++){
			channelsInv = channelsInv.and(boolChannels.get(i).inv());
		}
    
		// TBD the same for intChannels
		return processes_bdd.and(channelsInv);
	}

	/**
	 * 
	 * @return the model with basic information, number of variables, etc
	 */
	public BDDModel getModel(){
		return model;
	}
	
	/**
	 * 
	 * @param name	the name of the enum to get
	 * @return	the enum corresponding to the name, or null in the case the name does not exist
	 */
	public EnumType getEnum(String name){
		for(int i=0; i < enumTypes.size(); i++){
            if (enumTypes.get(i).getName().equals(name)){
				return enumTypes.get(i);
			}
		}
		return null;
	}
	
	/**
	 * A simple method to add new enumerable types to the program
	 * @param newType	the type to be added
	 */
	public void addEnumType(EnumType newType){
		enumTypes.add(newType);
	}
	
	/**
	 * Returns the BDD representing the program, it does not us any scheduling, thus any interleaving is possible
	 * the fairness issue must be deal with when model checking the properties, it only uses the scheduler to indicate
	 * which process was executed
	 */
	 public BDD getBDD(){
		 	// we initialize the scheduler
			//initScheduler();
			
			// init the primes
		 	if (!generatedPrimes){
		 		initPrimes();
		 		generatedPrimes = true;
		 	}
		
			BDD processes_bdd = myFactory.zero();
			for (int i=0; i < processes.size(); i++){
				BDD executeProcess = processes.get(i).getBDD().and(skipOthersThan(i).and(skipGlobalVarsNotIn(i)).and(skipChannelsOthersThan(processes.get(i).getChannels()))).and(processExecuted(i));
				processes_bdd = processes_bdd.or(executeProcess); 
			}
	    
			// we generate the invariants for the channels
			BDD channelsInv = Program.myFactory.one();
			for (int i = 0; i < boolChannels.size(); i++){
				channelsInv = channelsInv.and(boolChannels.get(i).inv());
			}
	    
			
			// TBD the same for intChannels
			return processes_bdd.and(channelsInv);
	 }
	
	/**
	 *  
	 * @return	the list of processes of the program
	 */
	public LinkedList<Process> getProcesses(){
		return processes;
	}
	
    /**
     * Return the varEnum if exist, null otherwise.-
     */
    public VarEnum getVarEnum(String name){
        
        //search the var in global list
        for(int i=0; i<enumVars.size();i++ ){
            if(enumVars.get(i).getCompleteName().equals(name)){
                return enumVars.get(i);
            }
        }
        
        //search the var in each process.
        LinkedList<VarEnum> procEnumVars;
        for(int i=0; i<processes.size();i++ ){
            procEnumVars = processes.get(i).getEnumVars();
            
            for(int j=0; j<procEnumVars.size();j++){
                if(procEnumVars.get(j).getCompleteName().equals(name)){
                    return procEnumVars.get(j);
                }
            }
        }
        
        return null;
    }
	
	/**
	 * Method to add a process to the program
	 */
	public void addProcess(Process process){
		processes.add(process);
	}

	/**
	 * Method to add a BoolChannel to the program
	 */
	public void addBoolChannel(BoolChannel chan){
		boolChannels.add(chan);
	}

	/**
	 * Method to add an IntChannel to the program
	 */
	public void addIntChannel(IntChannel chan){
		intChannels.add(chan);
	}

	/**
	 *  Method for adding a global boolvar
	 */
	public void addBoolVar(VarBool var){
		boolVars.add(var);
	}
	
	/**
	 *  Method for adding a global int var
	 */
	public void addIntVar(VarInt var){
		intVars.add(var);
	}
	
	/**
	 * Method for adding a enumerable var
	 * @param var
	 */
	public void addEnumVar(VarEnum var){
		enumVars.add(var);
	}
	
	/**
	 * Calculates the BDD corresponding to all processes performing a skip,
	 * except process i
	 */
	public BDD skipOthersThan(int i){
		BDD result = Program.myFactory.one();
		
		LinkedList<VarBool> skipBoolVars = processes.get(i).getGlobalBoolVarsInProcess();	
		LinkedList<VarInt> skipIntVars = processes.get(i).getGlobalIntVarsInProcess();
		//LinkedList<VarBool> usedBoolVars = new LinkedList<VarBool>(skipBoolVars);
		for (int j = 0; j < processes.size(); j++){
			if (j != i){			
				result = result.and(processes.get(j).skipBDD(skipBoolVars, skipIntVars));
				//usedBoolVars.addAll(processes.get(j).getGlobalBoolVarsInProcess());
			}					
			//result.and(skipGlobalVarsNotIn(skipBoolVars, skipIntVars));
		}		
		return result;
	}
	
	/**
	 * A quantified version of the earlier method
	 * @param range
	 * @param i
	 * @return
	 */
	public BDD quantifiedSkipOthersThan(BDD range, int i){
		BDD result = range;		
		LinkedList<VarBool> skipBoolVars = processes.get(i).getGlobalBoolVarsInProcess();	
		LinkedList<VarInt> skipIntVars = processes.get(i).getGlobalIntVarsInProcess();
		for (int j = 0; j < processes.size(); j++){
			if (j != i){			
				result = result.and(processes.get(j).quantifiedSkipBDD(range, skipBoolVars, skipIntVars));
				
			}
			//result.and(skipGlobalVarsNotIn(skipBoolVars, skipIntVars));
		}
		return result;
	}
	
	/**
	 * 
	 * @param i
	 * @return
	 */
	public LinkedList<Integer> getPrimedVarsOthersThan(int i){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for (int j = 0; j < processes.size(); j++){
			if (j != i){
				result.addAll(processes.get(j).getPrimedVarsExceptFor(processes.get(i).getGlobalBoolVarsInProcess(), processes.get(i).getGlobalIntVarsInProcess())); 			
			}
		}
		return result;
	}
	
	
	private BDD skipGlobalVarsNotIn(int i){
		BDD result = Program.myFactory.one();
		for (int j = 0; j < boolVars.size(); j++){
			if (!processes.get(i).getGlobalBoolVarsInProcess().contains(boolVars.get(j))){
				result = result.and(boolVars.get(j).skipBDD());
			}
		}
		for (int j = 0; j < intVars.size(); j++){
			if (!processes.get(i).getGlobalIntVarsInProcess().contains(intVars.get(j))){
				result = result.and(intVars.get(j).skipBDD());
			}
		}
		return result;
		
	}

	/**
	 * Private method that returns a skip BDD for the channels not in the list,
	 * @param channels 	A list of channels
	 * @return the list of the channels do not belonging to the parameter
	 */
	private BDD skipChannelsOthersThan(LinkedList<Channel> channels){
		BDD result = Program.myFactory.one();
		for (int i = 0; i < boolChannels.size(); i++){
			if (!channels.contains(boolChannels.get(i))){
				result = result.and(boolChannels.get(i).skipBDD());
			}
		}
		
		for (int i = 0; i < intChannels.size(); i++){
			if (!channels.contains(intChannels.get(i))){
				result = result.and(intChannels.get(i).skipBDD());
			}
		}	
		return result;
	}

	/**
	 * 
	 * @param boolVars
	 * @param intVars
	 * @return
	 */
	private BDD skipGlobalVarsNotIn(LinkedList<VarBool> exBoolVars, LinkedList<VarInt> exIntVars){
		//LinkedList<VarBool> myBoolVars = this.getGlobalBoolVarsInProcess();
		BDD result = Program.myFactory.one();
    	for (int i=0; i < boolVars.size(); i++){
    		if (!exBoolVars.contains(boolVars.get(i))){
    			result = result.and(boolVars.get(i).skipBDD());
    		}
    	}
    	
    	// skip the int global vars used in the process do not appearing
    	// in boolVars
    	//LinkedList<VarInt> myIntVars = this.getGlobalIntVarsInProcess(); 
    	for (int i=0; i < intVars.size(); i++){
    		if (!exIntVars.contains(intVars.get(i))){
    			result = result.and(intVars.get(i).skipBDD());
    		}
    	}
    	return result;
	}
	
	/**
	 * A private method to initialize the scheduler, the scheduler
	 * has to be initialized when we create the BDD (getBDD()), since 
	 * the number of processes in know at that point
	 */
	private void initScheduler(){
		if (!disabledScheduler){
			scheduler = new BDD[processes.size()];
			for (int i = 0; i < processes.size(); i++){
				scheduler[i] =  Program.myFactory.ithVar(Program.declaredVars);
				Program.declaredVars++;
			}
		//	scheduler_ = new BDD[processes.size()];
		//	for (int i = 0; i < processes.size(); i++){
		//		scheduler_[i] =  Program.myFactory.ithVar(Program.declaredVars);
		//		Program.declaredVars++;
		//	}
		}
	}
	
	/**
	 * Creates the primes for all the program, call init primes for processes (they create primes for local variables),
	 * creates primes for global variables, create primes for channels
	 */
	public void initPrimes(){
		
		// create primes for bool global variables
		for (int i = 0; i < boolVars.size(); i++){
			boolVars.get(i).initPrimes();
		}
		
		// create primes for int global variables
		for (int i = 0; i < intVars.size(); i++){
			intVars.get(i).initPrimes();
		}
		
		// create primes for int global enum vars
		for (int i = 0; i < enumVars.size(); i++){
			enumVars.get(i).initPrimes();			
		}
		
		// create primes for bool channels
		for (int i = 0; i < boolChannels.size(); i++){
			boolChannels.get(i).initPrimes();
		}
	
		// create primes for int channels
		for (int i = 0; i < intChannels.size(); i++){
			intChannels.get(i).initPrimes();
		}
		
		// create primes for processes
		for (int i = 0; i < processes.size(); i++){
			processes.get(i).initPrimes();
		}
		
		// create the primes for the schedules, in case it is needed
		if (!disabledScheduler){
			scheduler_ = new BDD[processes.size()];
			for (int i = 0; i < processes.size(); i++){
				scheduler_[i] =  Program.myFactory.ithVar(Program.declaredVars+scheduler[i].var());
				Program.declaredVars_++;
			}
		}
	}
	
	/**
	 * Initializes fairness for every process
	 */
	public void initFairness(){
		for (int j=0; j < processes.size(); j++){
			processes.get(j).initFairness();
		}
	}

	/**
	 * 
	 * @param i		the current process
	 * @return	the bdd representing teh fact that other processes are not set on 
	 */
	public BDD otherProcessesNotRunning(Process current){
		BDD result = Program.myFactory.one();
		for (int j = 0; j < processes.size(); j++){
			if (processes.get(j) != current)
				result = result.and(processes.get(j).getRunning_().not());
		}
		return result;
	}
	
	/** 
	 * A private method that returns a BDD formula stating that process ith is 
	 * the next in the scheculer
	 */
	private BDD nextProcess(int i){
		BDD result = Program.myFactory.one();
		for (int j = 0; j < i; j++){
			result = result.and(scheduler[j].not());
		}
		result = result.and(scheduler[i]);
		return result;
	}

	/**
	 * Private method for managing the scheduler, it sets that the ith process
	 * was executed
	 */
	private BDD processExecuted(int i){
		if (!disabledScheduler){
			BDD result = Program.myFactory.one();
			for (int j = 0; j < processes.size(); j++){
				if (i != j){
					//result = result.and(scheduler_[j].biimp(scheduler[j]));
					result = result.and(scheduler_[j].biimp(Program.myFactory.one()));		
				}
			}
			result = result.and(scheduler_[i].biimp(Program.myFactory.one()));
			return result;
		}
		else{
			return myFactory.one();
		}
	}

	/**
	 * Private method that states that all the processes were executed
	 */
	private BDD allExecuted(){
		BDD result = Program.myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			result = result.and(scheduler[i].biimp(Program.myFactory.zero()));
		}
		return result;	
	}

	/**
	 * A private method that starts a round of the scheduler
	 */
	private BDD startRound(){
		BDD result = Program.myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			result = result.and(scheduler_[i].biimp(Program.myFactory.one()));
		}
		return result;
	}

	/**
	 * A private method that skips all for the program
	 */
	private BDD skipBDD(){
		BDD result = Program.myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			result = result.and(processes.get(i).skipBDD());
		}
		
		for (int i = 0; i < boolChannels.size(); i++){
			result = result.and(boolChannels.get(i).skipBDD());
		}
		
		for (int i = 0; i < intChannels.size(); i++){
			result = result.and(intChannels.get(i).skipBDD());
		}
		
		return result;
	}
	
	
	/**
	 * 
	 * @return a linked list with the models of all the processes
	 */
	public LinkedList<BDDModel> buildModels(){
		
		LinkedList<BDDModel> models = new LinkedList<BDDModel>();
		
		if (!generatedPrimes){
				initPrimes();
				generatedPrimes = true;
		}
		
		
		//processes.get(0).getBDD(this,0);
		
		// we loop the list creating the models of each process
		
		for (int i=0; i < processes.size(); i++){
			
			// we obtain the current process
			Process current = processes.get(i);			
			// A model is generated for this process
			BDDModel model = new BDDModel(current.getBDD(this, i), current.getInitialCond().getBDD(), current.getNorm().getBDD());
		
			// the transitions of each process are calculated using the BDD and stating that 
			// other stuff do not change
			//BDD transitions = current.getBDD().and(skipOthersThan(i).and(skipGlobalVarsNotIn(i)).and(skipChannelsOthersThan(processes.get(i).getChannels())));
			//BDD transitions = processes.get(i).getBDD(this, i);
			//BDDModel model = new BDDModel(current.getInitialCond().getBDD(), current.getNorm().getBDD());
			
			model.setExternalVars(this.getGlobalVarsNotIn(i));
			models.add(model);
			
		}
		
		return models;
	}
	
	/**
	 * 
	 * @return	a linked list of the models, these are not complete models, but are models represented
	 * 			by a list of disjuncts, the model will be computed at model checking.
	 */
	public LinkedList<BDDModel> buildPartialModels(){
		LinkedList<BDDModel> models = new LinkedList<BDDModel>();
		//BDD guardsNeg = Program.myFactory.zero();
		if (!generatedPrimes){
			initPrimes();
			generatedPrimes = true;
		}
		for (int i=0; i < processes.size(); i++){
			// we obtain the current process
			Process current = processes.get(i);
			BDDModel model = new BDDModel();
			model.setInit(current.getInitialCond().getBDD());
			model.setNormative(current.getNorm().getBDD());						
			model.setDisjuncts(current.getPartialBDD(this, i));
			
			// add the external vars
			LinkedList<Var> externalVars = new LinkedList<Var>();
			externalVars.addAll(getGlobalVarsNotIn(i));
			externalVars.addAll(getLocalVarsNotIn(i));
			model.setExternalVars(externalVars);
			
			// add the internal vars
			LinkedList<Var> internalVars = new LinkedList<Var>();
			internalVars.addAll(processes.get(i).getGlobalBoolVarsInProcess());			
			internalVars.addAll(processes.get(i).getBoolVars());
			internalVars.addAll(processes.get(i).getEnumVars());
			model.setInternalVars(internalVars);
			// TO DO: add int vars
			
			models.add(model);
		}
		
		// an additional process is added to capture the situation in which all the process are blocked
		//BDDModel deadlockProcess = new BDDModel();
			
		return models;
	}
	
	/**
	 * 
	 * @return builds a model representing the complete program
	 */
	public BDDModel buildModel(){
		model.setTransitions(this.getBDD());
		
		// we calculate the initial condition
		BDD init =  myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			init = init.and(processes.get(i).getInitialCond().getBDD());
		}
		
		// we calculate the normative condition
		BDD norm =  myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			norm = norm.and(processes.get(i).getNormCond().getBDD());
		}
		
		model.setInit(init);
		model.setNormative(norm);
		
		return model;
	}
	
	/**
	 * Calculates the preimage of the relation R(x0,..,xn,x0',..,xn') wrt
	 * a range, to do this uses the corresponding preImage of each process
	 * and applies the or to them. This method is basically useful to calculate
	 * the WeakPrevious
	 * @param range		the set used to calculate the preimage
	 * @return
	 */
	public BDD preImageBDD(BDD range){
		
		if (!generatedPrimes){
			initPrimes();
			generatedPrimes = true;
		}
		
		BDD result = Program.myFactory.zero();		
		for (int i=0; i < processes.size(); i++){
			int varNum = Program.myFactory.varNum();
			BDD processBDD = processes.get(i).preImageBDD(range, this, i);
		//	for(int k=varNum/2; k< varNum; k++){			 	
		//		BDD var = Program.myFactory.ithVar(k);
		//		processBDD = processBDD.exist(var);
		//	}					
			result = result.or(processBDD);
		}
		return result;
	}
	
	public LinkedList<Var> getGlobalVarsNotIn(int index){
		LinkedList<Var> result = new LinkedList<Var>();		
		for (int i = 0; i < boolVars.size(); i++){
			if (!processes.get(index).getGlobalBoolVarsInProcess().contains(boolVars.get(i)))
				result.add(boolVars.get(i));
		}
		return result;
			
	}
	
	public LinkedList<Var> getLocalVarsNotIn(int index){
		LinkedList<Var> result = new LinkedList<Var>();
		for (int i = 0; i < processes.size(); i++){		
			if (i != index){	
					result.addAll(processes.get(i).getBoolVars());
					result.addAll(processes.get(i).getEnumVars());
			}		
		}
		return result;
			
	}
	
	/**
	 * Returns the runs of the program satisfying the property, it can be used to generat counterexamples
	 * @param sat
	 * @param length the length of the run
	 * @return	a string describing the possible run(s) of the program satisfying the property
	 */
	public String getRuns(LinkedList<BDD> sats){
		String result = "";
		//BDD currentState = this.getInitialCond();
		for (int j=0; j< sats.size(); j++){	
			BDD currentState = sats.get(j);
			result += "State "+ j + ":\n[Global: ";
			//For now we only print the global vars
			for (int i=0; i<this.boolVars.size(); i++){		
				if (currentState.and(this.boolVars.get(i).getBDD()).satCount()>0){
					result += boolVars.get(i).getName() + ",";
					currentState = currentState.and(this.boolVars.get(i).getBDD());
				}
				else{
					currentState = currentState.and(this.boolVars.get(i).getBDD().not());
				}
			}
			result += "]\n";

			for (int i=0; i<this.processes.size();i++){
				result += "[Process "+processes.get(i).getInstName()+": ";
				LinkedList<VarBool> bvars = processes.get(i).getBoolVars();
				for (int k=0; k<bvars.size(); k++){
					if (currentState.and(bvars.get(k).getBDD()).satCount()>0)
						result += bvars.get(k).getName() + ",";
				}
				LinkedList<VarEnum> evars = processes.get(i).getEnumVars();
				for (int k=0; k < evars.size(); k++){
					EnumType myType = evars.get(k).getEnumType();
					BDD[] bits = evars.get(k).getBits();
										
					int cons = 0;
					boolean holds = false;
					int pos = 0;
					for (int h=bits.length-1; h>=0; h--){
						if (currentState.and(bits[h]).satCount()>0)
							cons = cons + ((int) (Math.pow(2, pos)));
						pos++;
							
					}
					result += myType.getCons(cons) + ", ";
				}	
				result += "]\n";
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param sats	A list of BDDs representing a run
	 * @return	A list of hashmaps  [Process->State] identifying the states that each process in very step.
	 */
	public LinkedList<HashMap<String,String>> getRunsAsMaps(LinkedList<BDD> sats){
		LinkedList<HashMap<String, String>> result = new LinkedList<HashMap<String,String>>();
		for (int j=0; j< sats.size(); j++){	
			HashMap<String, String> currentMap = new HashMap<String, String>();
			BDD currentState = sats.get(j);
			for (int i=0; i<this.processes.size();i++){
				LinkedList<VarEnum> evars = processes.get(i).getEnumVars();
				for (int k=0; k < evars.size(); k++){
					EnumType myType = evars.get(k).getEnumType();
					BDD[] bits = evars.get(k).getBits();
										
					int cons = 0;
					boolean holds = false;
					int pos = 0;
					for (int h=bits.length-1; h>=0; h--){
						if (currentState.and(bits[h]).satCount()>0)
							cons = cons + ((int) (Math.pow(2, pos)));
						pos++;
							
					}
					currentMap.put(processes.get(i).getInstName(), myType.getCons(cons));
				}	
			}
			result.add(currentMap);
		}
		return result;
	}
	/**
	 * 
	 * @param sats	A description of the run
	 * @param process	A given process for which the run will be computed
	 * @return	a list of the different states (enums) of the process during the run
	 */
	public LinkedList<String> getRunForProcess(LinkedList<BDD> sats, String process){
		LinkedList<String> result = new LinkedList<String>();
		for (int i=0; i<this.processes.size();i++){
			Process actualProcess = processes.get(i);
			if (actualProcess.getInstName().equals(process)){				
				for (int j=0; j< sats.size(); j++){	
					BDD currentState = sats.get(j);
					LinkedList<VarEnum> evars = processes.get(i).getEnumVars();
					for (int k=0; k < evars.size(); k++){
						EnumType myType = evars.get(k).getEnumType();
						BDD[] bits = evars.get(k).getBits();
									
						int cons = 0;
						boolean holds = false;
						int pos = 0;
						for (int h=bits.length-1; h>=0; h--){
							if (currentState.and(bits[h]).satCount()>0)
								cons = cons + ((int) (Math.pow(2, pos)));
							pos++;
						
						}
						result.add(myType.getCons(cons));
					}	
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param from
	 * @param until
	 * @param b
	 * @param f
	 * @return 
	 */
	private BDD addExists(int from, int until, BDD b, BDDFactory f){
		BDD var;

		for(int k=from; k< until; k++){			 	
			var = f.ithVar(k);
			b = b.exist(var);
		}		
		return b;		
	}
	
	
	/**
     * 
     * @param r_primed  Boolean flag to indicate it will replace all primed variables
     *  (in which case r_primed = true) or all common variables (r_primed =false).-
     * @return creates pairs to replace primed (v' -> v) or common variables (v-> v')
     */
	private BDDPairing makePairsToReplace(boolean r_primed){      
		//obtain the identifier of each variable of the BDD.
		int varNum = Program.myFactory.varNum() / 2;
		BDDPairing pairs = Program.myFactory.makePair();		

		if(r_primed){// creates pairs to changes variables v' by v , i.e. v -> v'		
			for (int i=0; i<varNum; i++){
				pairs.set((varNum)+i, i);		
			}   
		}
		else{// creates pairs to changes variables v by v',  i.e. v' -> v		
			for (int i=0; i<varNum; i++){
				pairs.set(i,(varNum)+i);		
			}   
		} 
		return pairs;
	}	
	
	@Override
	public String toString(){
		
		String progInfo = new String("-------------------------------- Program ------------------------- \n");
		String size = new String( "- sizeSpace:" + sizeSpace + "\n");
		String intV =new String("- numberIntVars:"+  numberIntVars + "\n");
		String boolV =new String("- numberBoolVars:" + numberBoolVars + "\n");
		String boolC =new String("- numberBoolChannels:" + numberBoolChannels + "\n");
		String intC =new String("- numberIntChannels:" + numberIntChannels + "\n");
        String disab =new String("- disabledScheduler:" + disabledScheduler + "\n");
		
        progInfo = progInfo.concat(size).concat(intV).concat(boolV).concat(boolC).concat(intC).concat(disab);
		
        String intCH = new String("- IntChannels:\n\n");
        for(IntChannel ch: intChannels){
        	intCH = intCH.concat(new String("    + " + ch.toString() ) );
        	
        }
        
        String boolCH = new String("- BoolChannels:\n\n");
        for(BoolChannel ch: boolChannels){
        	boolCH = boolCH.concat(new String("    + " + ch.toString() ) );
        	
        }
        
        String proc = new String("- Processes:\n\n");
        for(Process p: processes){
        	proc = proc.concat(new String("    + " + p.toString() ) );
        	
        }

        String bVar = new String("- Global BoolVars:\n\n");
        for(VarBool v: boolVars){
        	bVar = bVar.concat(new String("    + " + v.toStringComplete() ) );
        	
        }
        bVar = bVar.concat("\n");
        
        String iVar = new String("- Global IntVars:\n\n");
        for(VarInt v: intVars){
        	iVar = iVar.concat(new String("    + " + v.toStringComplete() ) );
        	
        }
        iVar = iVar.concat("\n");
        
        
        String eVar = new String("- Global EnumVars:\n\n");
        for(VarEnum v: enumVars){
        	eVar = eVar.concat(new String("    + " + v.toStringComplete() ) );
        	
        }
        eVar = eVar.concat("\n");
        
        String eType = new String("-  EnumTypes:\n\n");
        for(EnumType t: enumTypes){
        	eType = eType.concat(new String("    + " + t.toString() ) );
        	
        }
        eType = eType.concat("\n");
        
        
        progInfo = progInfo.concat(eType).concat(intCH).concat(boolCH).concat(proc).concat(bVar).concat(iVar).concat(eVar);
		
        return progInfo;
	}
	
}// end of class