
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
	int numberBoolChannels; // number of bool channel in the program
	int numberIntChannels; // number of int channels in the program
	int maxLengthChannels; // maximum length of the channels in the program
	boolean disabledScheduler; // a boolean attribute used for efficency reasons

	//A faulty program has a list of channels and a list of processes, and 
	// lists of global variables
	LinkedList<IntChannel> intChannels;	
	LinkedList<BoolChannel> boolChannels;
	LinkedList<Process> processes;
	LinkedList<VarBool> boolVars;
	LinkedList<VarInt> intVars;
	
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
	 * @param numberBoolChannels	the number of boolean channels
	 * @param numberIntChannels		the number of int channels
	 * @param maxLengthChannels		the maximum length of a channels in the program
	 * 
	 * All the information given for the paramenters is used for creating the BDD's	
	 */

	public Program(int intSize, int numberIntVars, int numberBoolVars, int numberBoolChannels, int numberIntChannels, int maxLengthChannels, BDDModel model){
		this.intSize = intSize;
		this.numberIntVars = numberIntVars;
		this.numberBoolVars = numberBoolVars;
		this.numberBoolChannels = numberBoolChannels;
		this.numberIntChannels = numberIntChannels;
		this.maxLengthChannels = maxLengthChannels;
		disabledScheduler = false; // by default it is not disabled
		intChannels = new LinkedList<IntChannel>();
		boolChannels = new LinkedList<BoolChannel>();
		processes = new LinkedList<Process>();
		boolVars = new LinkedList<VarBool>();
		intVars = new LinkedList<VarInt>();
		this.model = model;
		
		// first we create a BDDFactory, for this we calculate the number of the state space
		sizeSpace = (numberIntChannels * maxLengthChannels * intSize) + (numberBoolChannels * maxLengthChannels) + (numberIntVars * intSize) + (numberBoolVars);
		
		// Initialize with reasonable nodes and cache size and Nx2 variables
		int numberOfNodes =  (int) (Math.pow(4.42, (sizeSpace*2)-6 ))*1000;
		int cacheSize = 2000;
		numberOfNodes = Math.max(1000, numberOfNodes);
		initBDDFactory(numberOfNodes, cacheSize);
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

	public Program(int intSize, int numberIntVars, int numberBoolVars, int numberBoolChannels, int numberIntChannels, int maxLengthChannels, BDDModel model, boolean scheduler){
		this.intSize = intSize;
		this.numberIntVars = numberIntVars;
		this.numberBoolVars = numberBoolVars;
		this.numberBoolChannels = numberBoolChannels;
		this.numberIntChannels = numberIntChannels;
		this.maxLengthChannels = maxLengthChannels;
		disabledScheduler = scheduler; // by default it is not disabled
		intChannels = new LinkedList<IntChannel>();
		boolChannels = new LinkedList<BoolChannel>();
		processes = new LinkedList<Process>();
		boolVars = new LinkedList<VarBool>();
		intVars = new LinkedList<VarInt>();
		this.model = model;
		
		// first we create a BDDFactory, for this we calculate the number of the state space
		sizeSpace = (numberIntChannels * maxLengthChannels * intSize) + (numberBoolChannels * maxLengthChannels) + (numberIntVars * intSize) + (numberBoolVars);
		
		// Initialize with reasonable nodes and cache size and Nx2 variables
		int numberOfNodes =  (int) (Math.pow(4.42, (sizeSpace*2)-6 ))*1000;
		int cacheSize = 2000;
		numberOfNodes = Math.max(1000, numberOfNodes);
		initBDDFactory(numberOfNodes, cacheSize);
		myFactory.setVarNum(sizeSpace); 	   
	}
	/**
	 * This is a private method to create the BDD factory, it only calculates the number of boolean variables needed 
	 * for the program.
	 */
	private static void initBDDFactory(int numberOfNodes, int cacheSize){
		myFactory = BDDFactory.init(numberOfNodes, cacheSize);
	}

	/**
	 * Returns the initial states, get from the initial states of the processes
	 * needed by the model checker
	 */
	public BDD getInitialCond(){
		BDD result = Program.myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			result = result.and(processes.get(i).getInitialCond());
		}
		return result;
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
	 * Returns the BDD respresenting the program, it does not us any scheduling, thus any interleaving is posible
	 * the fairness issue must be deal with when model checking the properties, it only uses the scheduler to indicate
	 * which process was executed
	 */
	 public BDD getBDD(){
		 	// we initialize the scheduler
			initScheduler();
			
			// init the primes
			initPrimes();
		
			BDD processes_bdd = myFactory.zero();
			for (int i=0; i < processes.size(); i++){
				BDD executeProcess = processes.get(i).getBDD().and(skipOthersThan(i).and(skipGlobalVarsNotIn(i)).and(skipChannelsOthersThan(processes.get(i).getChannels()))).and(processExecuted(i));
				processes_bdd = processes_bdd.or(executeProcess); 
			}
	    
			// restart the round
			//processes_bdd = processes_bdd.and();
			//processes_bdd = processes_bdd.or(allExecuted().and(startRound()).and(skipBDD()));
	    
			// we generate the invariants for the channels
			BDD channelsInv = Program.myFactory.one();
			for (int i = 0; i < boolChannels.size(); i++){
				channelsInv = channelsInv.and(boolChannels.get(i).inv());
			}
	    
			// TBD the same for intChannels
			return processes_bdd.and(channelsInv);
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
	 *  Methof adding a global boolvar
	 */
	public void addBoolVar(VarBool var){
		boolVars.add(var);
	}
	
	/**
	 *  Method adding a global int var
	 */
	public void addIntVar(VarInt var){
		intVars.add(var);
	}
	
	/**
	 * Calculates the BDD corresponding to all processes performing a skip,
	 * except for process i
	 */
	private BDD skipOthersThan(int i){
		BDD result = Program.myFactory.one();
		for (int j = 0; j < processes.size(); j++){
			if (j != i){
				result = result.and(processes.get(j).skipBDD());
			}
		}
		return result;
	}
	
	private BDD skipGlobalVarsNotIn(int i){
		BDD result = Program.myFactory.one();
		for (int j = 0; j < boolVars.size(); j++){
			if (!processes.get(i).getGlobalBoolVars().contains(boolVars.get(j))){
				result = result.and(boolVars.get(j).skipBDD());
			}
		}
		for (int j = 0; j < intVars.size(); j++){
			if (!processes.get(i).getGlobalIntVars().contains(intVars.get(j))){
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
	 * A private method to initialize the scheculer, the shceduler
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
	
	public BDDModel buildModel(){
		model.setTransitions(this.getBDD());
		
		// we calculate the initial condition
		BDD init =  myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			init = init.and(processes.get(i).getInitialCond());
		}
		
		// we calculate the normative condition
		BDD norm =  myFactory.one();
		for (int i = 0; i < processes.size(); i++){
			norm = norm.and(processes.get(i).getNormCond());
		}
		
		model.setInit(init);
		model.setNormative(norm);
		
		return model;
	}
	
}// end of class