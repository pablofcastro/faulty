package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.VarInfo.Type;
import mc.*;

public class IntChannel extends Channel{

	int size; // the size of the channel
	BDD[][] els; // the elements of the buffer els[i][j] is the jth bit of the integer in position i
	BDD[][] els_; // similarly as before
	BDD[] oc; // oc[i] says that position i is occupied
	BDD[] oc_; // oc_[i] says that position i is occupied or not after some operation
	BDD full; // it says if the buffer is full
	BDD full_; // BDD to store the state of full after some operation
	BDD empty; // it says if the buffer is empty
	BDD empty_; // BDD to store the state of empty after some operation
	BDDModel model;
	
	
	/**
	 * Basic constructor of the class
     * @param	name	the name of the channel
	 * @param	size	the size of the channel
	 */
	public IntChannel(String name,int size, BDDModel model){
        super(name);
		this.size = size;
		els = new BDD[size][Program.intSize];
		els_ = new BDD[size][Program.intSize];
		oc = new BDD[size];
		oc_ = new BDD[size];
		this.model = model;
	
		// We request the needed BDD variables
		for (int i = 0; i < size; i++){
			for (int j = 0; j < Program.intSize; j++){
				els[i][j] = Program.myFactory.ithVar(Program.declaredVars);
				Program.declaredVars++;
				model.addVar("chan-"+name+"["+i+"]"+"["+j+"]", Type.BOOL);
			//	els_[i][j] = Program.myFactory.ithVar(Program.declaredVars);
			//	Program.declaredVars++;
			//	model.addVar("chan-"+name+"["+i+"]"+"["+j+"]_", Type.BOOL);
			}
			
		}
		
		for (int i = 0; i < size; i++){
			oc[i] = Program.myFactory.ithVar(Program.declaredVars);
			Program.declaredVars++;
			model.addVar("chan-"+name+"-oc["+i+"]", Type.BOOL);
		}
		
	//	for (int i = 0; i < size; i++){
	//		oc_[i] = Program.myFactory.ithVar(Program.declaredVars);
	//		Program.declaredVars++;
	//		model.addVar("chan-"+name+"-oc["+i+"]_", Type.BOOL);
	//	}
     
		full = Program.myFactory.ithVar(Program.declaredVars);
		Program.declaredVars++;
		model.addVar("chan-"+name+"-full", Type.BOOL);
	//	full_ = Program.myFactory.ithVar(Program.declaredVars);
	//	Program.declaredVars++;
	//	model.addVar("chan-"+name+"-full_", Type.BOOL);
		empty = Program.myFactory.ithVar(Program.declaredVars);
		Program.declaredVars++;
		model.addVar("chan-"+name+"-empty", Type.BOOL);
	//	empty_ = Program.myFactory.ithVar(Program.declaredVars);
    // 	Program.declaredVars++;
    //	model.addVar("chan-"+name+"-empty_", Type.BOOL);
	
		
	}

	/**
	 * Generates the primes version of the BDD vars needed
	 */
	public void initPrimes(){
		// We request the needed BDD variables for the primes values
				for (int i = 0; i < size; i++){
					for (int j = 0; j < Program.intSize; j++){
						els_[i][j] = Program.myFactory.ithVar(Program.declaredVars+els[i][j].var());
						Program.declaredVars_++;
						model.addVar("chan-"+name+"["+i+"]"+"["+j+"]_", Type.BOOL);
					}
				}
				
				
				for (int i = 0; i < size; i++){
					oc_[i] = Program.myFactory.ithVar(Program.declaredVars+oc[i].var());
					Program.declaredVars_++;
					model.addVar("chan-"+name+"-oc["+i+"]_", Type.BOOL);
				}
		     
				full_ = Program.myFactory.ithVar(Program.declaredVars+full.var());
				Program.declaredVars_++;
				model.addVar("chan-"+name+"-full_", Type.BOOL);
				empty_ = Program.myFactory.ithVar(Program.declaredVars+empty.var());
		     	Program.declaredVars_++;
		    	model.addVar("chan-"+name+"-empty_", Type.BOOL);
	}
    
    
    
    /**
	 * Basic constructor of the class
	 * @param	size	the size of the channel
	 */
	public IntChannel(int size, BDDModel model){
        super("");
		this.size = size;
		els = new BDD[size][Program.intSize];
		els_ = new BDD[size][Program.intSize];
		oc = new BDD[size];
		oc_ = new BDD[size];
		this.model = model;
		
		// We request the needed BDD variables
		for (int i = 0; i < size; i++){
			for (int j = 0; j < Program.intSize; j++){
				els[i][j] = Program.myFactory.ithVar(Program.declaredVars);
				Program.declaredVars++;
				model.addVar("chan-"+name+"["+i+"]"+"["+j+"]", Type.BOOL);
				els_[i][j] = Program.myFactory.ithVar(Program.declaredVars);
				Program.declaredVars++;
				model.addVar("chan-"+name+"["+i+"]"+"["+j+"]_", Type.BOOL);
			}
		}
		
		for (int i = 0; i < size; i++){
			oc[i] = Program.myFactory.ithVar(Program.declaredVars);
			Program.declaredVars++;
			model.addVar("chan-"+name+"-oc["+i+"]", Type.BOOL);
		}
		
		for (int i = 0; i < size; i++){
			oc_[i] = Program.myFactory.ithVar(Program.declaredVars);
			Program.declaredVars++;
			model.addVar("chan-"+name+"-oc["+i+"]_", Type.BOOL);
		}
        
		full = Program.myFactory.ithVar(Program.declaredVars);
		Program.declaredVars++;
		model.addVar("chan-"+name+"-full", Type.BOOL);
		full_ = Program.myFactory.ithVar(Program.declaredVars);
		Program.declaredVars++;
		model.addVar("chan-"+name+"-full_", Type.BOOL);
		empty = Program.myFactory.ithVar(Program.declaredVars);
		Program.declaredVars++;
		model.addVar("chan-"+name+"-empty", Type.BOOL);
		empty_ = Program.myFactory.ithVar(Program.declaredVars);
     	Program.declaredVars++;
     	model.addVar("chan-"+name+"-empty_", Type.BOOL);
     	
	}
    
	/**
	 * Returns the size fo the channel
	 */
	public int getSize(){
		return size;
	}
	
	/**
	  * Returns the name of the channel
      */
    public String getName(){
    	return name;
    }

	/**
	 * Returns the full atribute
	 */
	public BDD getFull(){
		return full;
	}

	/**
	 * Returns the empty atributte
	 */
	public BDD getEmpty(){
		return empty;
	}

	/**
	 * Returns the full_ atributte
	 */
	public BDD getFull_(){
		return full_;
	}

	/**
	 * Returns the empty_ atributte
	 */
	public BDD getEmpty_(){
		return empty_;
	}

	/**
	 * Returns the BDD representing the channel
	 */
	public BDD getBDD(){
		//TBD
		return null;
			}

	/**
	 * Returns the ith item of the first element in the channel
	 */
	public BDD getItem(int i){
		return  els[0][i];
	}

	/**
	 * Calculates the state of hte channel after a get
	 */
	public BDD BDDGet(){
		// A bdd for the result
		BDD result = Program.myFactory.one();		
		
		// if empty then skip, otherwise we perform a get
		result = (empty.imp(skip())).and(empty.not().imp(get()));
		
		// some invariant of the channels that shrinks the size of sats
		result = result.and(oc_[size-1].biimp(full_));
		result = result.and(oc[size-1].biimp(full));
		result = result.and(oc_[0].not().biimp(empty_));
		result = result.and(oc[0].not().biimp(empty));	
		return result;	
	}

	/**
	 * Calculates the states of the channel after an insertion
	 */
	public BDD BDDPut(BDD[] item){
		BDD result = Program.myFactory.one();	
		result = put(item);
		return result;
	}

	/**
	 *	Returns the skip BDD
	 */
	public BDD skipBDD(){
		BDD result = skip();
		return result;	
	}
	
	
	/**
	 * private method to calculate the BDD that results from no change after a put (full channel) or a get (empty channel)
	 */
	private BDD skip(){
		BDD result = Program.myFactory.one();
		
		for(int i = 0; i < size; i++){
			for(int j = 0; j < Program.intSize; j++){
				result = result.and(els[i][j].biimp(els_[i][j]));
			}
		}
		
		for(int i = 0; i < size; i++){
			result = result.and(oc[i].biimp(oc_[i]));
		}
		result = result.and(full.biimp(full_));
		result = result.and(empty.biimp(empty_));
		return result;
	}
	
	
	/**
	 * Returns a BDD stating that the occupied places after i are not changed.
	 */
	private BDD onlyChangesOc(int i){
		
		BDD result = Program.myFactory.one();
		for (int k = size - 1; k > i; k--){
			result = result.and(oc_[k].biimp(oc[k]));
		}
		
		for (int k = i - 1; k >= 0; k--){
			result = result.and(oc_[k].biimp(oc[k]));
		}
		
		return result;
	}


	/**
	 * Returns a BDD stating that the els after i are not changed.
	 */
	private BDD onlyChangesEl(int i){
		
		BDD result = Program.myFactory.one();
		for (int k = size - 1; k > i; k--){
			for (int j = 0; j < Program.intSize; j++){
				result = result.and(els_[k][j].biimp(els[k][j]));
			}
		}
		
		for (int k = i - 1; k >= 0; k--){
			for (int j = 0; j < Program.intSize; j++){
				result = result.and(els_[k][j].biimp(els[k][j]));
			}
		}
		
		return result;
	}
	
	/**
	 * 	Following the representation of the channel is as follows:
	 *  el_n -> el_n-1 -> ... -> el_0
	 *  the private method get calculates the state of the channel after a get.
	 */
	private BDD get(){
		BDD updateOc = Program.myFactory.one();
		
		// the items are moved 
		for (int j = 0 ; j < Program.intSize; j++){
			for (int i = size-1; i > 0; i--){
				updateOc = updateOc.and(els_[i-1][j].biimp(els[i][j]));
			}
		}
		
		// particular case of channel of size 1
		if (size == 1){
			for (int j = 0; j < Program.intSize; j++){
				updateOc = updateOc.and(els_[0][j].biimp(els[0][j]));
			}
		}
		
		// the channel is not full after a get
		updateOc = updateOc.and(full_.not());
		
		// the following "frees" the corresponding bit
		for (int i = 0; i < size; i++){	
			if (i != size-1){
				updateOc = updateOc.and(oc[i].and(oc[i+1].not()).imp(onlyChangesOc(i).and(oc_[i].not())));
			}
			else{
				updateOc = updateOc.and(oc[i].imp(oc_[i].not().and(onlyChangesOc(i))));
			}
		}
		return updateOc;
	}
	
	/**
	 * Private method that put the argument in the correct place in the channel
	 * @param	bits		The number to be added to the channel
	 */
	private BDD put(BDD[] bits){
		BDD result = Program.myFactory.one();
		
		// we calculate a BDD to say that: ! oc[i-1] => !oc[i],
		// perhaps not needed, since this must be ensured by the program execution ?
		
		BDD monotony = Program.myFactory.one();
		for (int i = 0; i < size - 1; i++){
			monotony = monotony.and(oc[i].not().imp(oc[i+1].not())); 
		}
		
		for (int i = size -1; i >= 0; i--){
			if (i == 0){
				for (int j = 0; j < Program.intSize; j++){
					result = result.and(oc[i].not().imp(els_[i][j].biimp(bits[j]).and(oc_[i]))).and(oc[i].imp(els_[i][j].biimp(els[i][j]).and(oc_[i])));
				}
			}
			if(i > 0){
				for (int j = 0; j < Program.intSize; j++){
					result = result.and(oc[i].not().and(oc[i-1]).imp(els_[i][j].biimp(bits[j]).and(oc_[i]).and(onlyChangesOc(i)))).and(oc[i].or(oc[i-1].not()).imp(oc_[i].biimp(oc[i]).and(els_[i][j].biimp(els[i][j]))));
				}
			}
		}
		result = result.and(oc_[size-1].biimp(full_));
		result = result.and(oc[size-1].biimp(full));
		result = result.and(oc_[0].not().biimp(empty_));
		result = result.and(oc[0].not().biimp(empty));
		result = result.and(monotony);
		return result;
	}
	
	/**
	 * This method return invariant properties of channels, it allows to shrink the resulting BDD
	 * @return the BDD representing the invariant of the program
	 */
	public BDD inv(){
		BDD result = Program.myFactory.one();
		result = result.and(oc_[size-1].biimp(full_));
		result = result.and(oc[size-1].biimp(full));
		result = result.and(oc_[0].not().biimp(empty_));
		result = result.and(oc[0].not().biimp(empty));
		result = result.and(monotony());
		
		return result;
	}

	/**
	 * Private method that returns a BDD stating the monotony of channels,
	 * !oc[i-1] => !oc[i]
	 */
	private BDD monotony(){
		BDD monotony = Program.myFactory.one();
		for (int i = 0; i < size - 1; i++){
			monotony = monotony.and(oc[i].not().imp(oc[i+1].not())); 
		}
		return monotony;
	}
	
	@Override
	public String toString(){
	    
	    String channelInfo = new String("       Channel ");
		
		String  chanString= name; 
		String sizeS = (new Integer(size)).toString();
		channelInfo= channelInfo.concat(chanString).concat("[").concat(sizeS).concat("] of INT");
		channelInfo= channelInfo.concat("\n\n");
	 	return channelInfo;
	}

}// end class
