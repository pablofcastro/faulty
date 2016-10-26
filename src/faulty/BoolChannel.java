package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.VarInfo.Type;
import mc.*; 

/**
* A simple class modelling the behavior of bool channel by means of using BDDs
* the channel is modeled in the following way:
* el_n -> el_n-1 -> ... -> el_0
* The elements are stored in an array els, being els_ used for storing their primed versions,
* array oc (and oc_) serve for indicating if an given position of the channel is occupied or not.
**/

public class BoolChannel extends Channel{

int size;
BDD[] els; // an array containing the bits of the buffer
BDD[] els_; // an array containing the bits after a put or a get
BDD[] oc; // oc[i] says whether buffer[i] is occuped
BDD[] oc_; // similar to els_
BDD full; // it says if the buffer is full
BDD full_; // BDD to store the state of full after some operation
BDD empty; // it says if the buffer is empty
BDD empty_; // BDD to store the state of empty after some operation
BDDModel model; // the model representing hte program, this will be used in Program to build the actual model



/**
 * The constructor of the class it creates all the BDD's needed for the channel
 * @param	name	the name of the channel
 * @param	size	the size of the channel
 */
public BoolChannel(String name,int size, BDDModel model){
    super(name);
	this.size = size;
	els = new BDD[size];
	els_ = new BDD[size];
	oc = new BDD[size];
	oc_ = new BDD[size];
	this.model = model;
     for (int i = 0; i < size; i++){
         els[i] = Program.myFactory.ithVar(Program.declaredVars);
         Program.declaredVars++;
         model.addVar("chan-"+name+"["+i+"]", Type.BOOL);
     }
    // for (int i = 0; i < size; i++){
    //     els_[i] = Program.myFactory.ithVar(Program.declaredVars);
    //     Program.declaredVars++;
    //     model.addVar("chan-"+name+"["+i+"]_", Type.BOOL);
     //}
     for (int i = 0; i < size; i++){
         oc[i] = Program.myFactory.ithVar(Program.declaredVars);
         Program.declaredVars++;
         model.addVar("chan-"+name+"["+i+"]-oc", Type.BOOL);
     }
     //for (int i = 0; i < size; i++){
     //    oc_[i] = Program.myFactory.ithVar(Program.declaredVars);
     //    Program.declaredVars++;
     //    model.addVar("chan-"+name+"["+i+"]-oc_", Type.BOOL);
     //}
     full = Program.myFactory.ithVar(Program.declaredVars);
     Program.declaredVars++;
     model.addVar("chan_"+name+"_full", Type.BOOL);
     //full_ = Program.myFactory.ithVar(Program.declaredVars);
     //Program.declaredVars++;
     //model.addVar("chan_"+name+"_full_", Type.BOOL);
     empty = Program.myFactory.ithVar(Program.declaredVars);
     Program.declaredVars++;
     model.addVar("chan_"+name+"_empty", Type.BOOL);
     //empty_ = Program.myFactory.ithVar(Program.declaredVars);
     //Program.declaredVars++;
     //model.addVar("chan_"+name+"_empty_", Type.BOOL);
}

	public void initPrimes(){	  
		     for (int i = 0; i < size; i++){
		         els_[i] = Program.myFactory.ithVar(Program.declaredVars+els[i].var());
		         Program.declaredVars_++;
		         model.addVar("chan-"+name+"["+i+"]_", Type.BOOL);
		     }
		     for (int i = 0; i < size; i++){
		         oc_[i] = Program.myFactory.ithVar(Program.declaredVars+oc[i].var());
		         Program.declaredVars_++;
		         model.addVar("chan-"+name+"["+i+"]-oc_", Type.BOOL);
		     }		    
		     full_ = Program.myFactory.ithVar(Program.declaredVars+full.var());
		     Program.declaredVars_++;
		     model.addVar("chan_"+name+"_full_", Type.BOOL);
		     empty_ = Program.myFactory.ithVar(Program.declaredVars+empty.var());
		     Program.declaredVars_++;
		     model.addVar("chan_"+name+"_empty_", Type.BOOL);
	}
    
    
    /**
     * The constructor of the class it creates all the BDD's needed for the channel
     * @param	size	the size of the channel
     */
    public BoolChannel(int size, BDDModel model){
        super("");
        this.size = size;
        els = new BDD[size];
        els_ = new BDD[size];
        oc = new BDD[size];
        oc_ = new BDD[size];
        for (int i = 0; i < size; i++){
            els[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("chan-"+name+"["+i+"]", Type.BOOL);
        }
        for (int i = 0; i < size; i++){
            els_[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("chan-"+name+"["+i+"]_", Type.BOOL);
        }
        for (int i = 0; i < size; i++){
            oc[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("chan-"+name+"["+i+"]-oc", Type.BOOL);
        }
        for (int i = 0; i < size; i++){
            oc_[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("chan-"+name+"["+i+"]-oc_", Type.BOOL);
        }
        full = Program.myFactory.ithVar(Program.declaredVars);
        Program.declaredVars++;
        model.addVar("chan_"+name+"_full", Type.BOOL);
        full_ = Program.myFactory.ithVar(Program.declaredVars);
        Program.declaredVars++;
        model.addVar("chan_"+name+"_full_", Type.BOOL);
        empty = Program.myFactory.ithVar(Program.declaredVars);
        Program.declaredVars++;
        model.addVar("chan_"+name+"_empty", Type.BOOL);
        empty_ = Program.myFactory.ithVar(Program.declaredVars);
        Program.declaredVars++;
        model.addVar("chan_"+name+"_empty_", Type.BOOL);
    }

/**
 * Observer method, it returns the size
 * @return the size of the bool channel
 */
public int getSize(){
    return size;
}

/**
 * Observer method, it returns the name
 * @return the name of the channel
 */
public String getName(){
	return name;
}

/**
 * Returns the BDD representing the channel, it is the most important methods, used for model checking
 * @return the BDD representing the channel
 */
public BDD getBDD(){
	// it returns the boolean element in the first position of the channel
    return els[0];
}

/**
 * Returns the BDD representing the ith position of the buffer
 * @param	i	the position of the buffer
 */
public BDD getBit(int i){
	// DEPRECATED
	return null;
}

/**
 * Gets a BDD representation of the ith bit of the channel
 * @param 	i	the ith 
 * @return the BDD of the ith bit
 */
public BDD getEl(int i){
	return els[i];
}

/**
 * Returns the ith element of teh array el_ representing the change of the ith element after some operation
 * @return the ith item of els_ 
 */
public BDD getEl_(int i){
	return els_[i];
}

/**
 * Returns the ith element of the the Array, oc[i] says if bit i is occupied
 * @return oc[i]
 */
public BDD getOc(int i){
	return oc[i];
}

/**
 * Returns the it element of the array oc_, oc_[i] says if bit i is acoopied after some operation
 * @return oc_[i]
 */
public BDD getOc_(int i){
	return oc_[i];
}


/**
 * Returns the BDD full, stating if the channel is full or not,
 * return full
 */
public BDD getFull(){
	return full;
}

/**
 * Returns the BDD full_, representing the state of full after a change
 * @return full
 */

public BDD getFull_(){
	return full_;
}

/**
 * Returns the BDD empty, it says if the channel is empty or not
 * @return the state of empty
 */
public BDD getEmpty(){
	return empty;
}



/**
 * 
 */
public BDD getEmpty_(){
	return empty_;
}

/**
 *Calculates the state of the channel after a get
 */
public BDD BDDGet(){
	BDD result = Program.myFactory.one();		
	result = (empty.imp(skip())).and(empty.not().imp(get()));
	
	result = result.and(oc_[size-1].biimp(full_));
	result = result.and(oc[size-1].biimp(full));
	result = result.and(oc_[0].not().biimp(empty_));
	result = result.and(oc[0].not().biimp(empty));
	
	return result;
}


/**
 * Calculates the state of the channel after a put
 */
public BDD BDDPut(BDD item){
	BDD result = Program.myFactory.one();	
	result = put(item);//(full.imp(skip())).and(full.not().imp(put(item)));
	return result;
}

/**
 * Returns the BDD corresponding to the item in the top of the channel
 */
public BDD getItem(){
	// if not empty then it returns the first element in the queue
	//BDD result = empty.not().and(els[0]);
	BDD result = els[0];
	return result;
}

/**
 * Returns the BDD corresponding to a Skip BDD
 */
public BDD skipBDD(){
	
	BDD result = skip();
	
	//for (int i = 0; i < size; i++){
	//	result = result.and(els[i]).(els_[i]).and(oc[i]).and(oc_[i]);
	//}
	
	//result = result.and(full.biimp(full_)).and(empty.biimp(empty_));
	return result;
}


/**
 * private method to calculate the BDD that results from no change after a put (full channel) or a get (empty channel)
 */
private BDD skip(){
	BDD result = Program.myFactory.one();
	for(int i = 0; i < size; i++){
			result = result.and(els[i].biimp(els_[i]));
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
		result = result.and(els_[k].biimp(els[k]));
	}
	
	for (int k = i - 1; k >= 0; k--){
		result = result.and(els_[k].biimp(els[k]));
	}
	
	return result;
}

/**
 * Remember the representation of the channel is as follows:
 *  el_n -> el_n-1 -> ... -> el_0
 *  the private method get calculates the state of the channel after a get.
 */
private BDD get(){
	BDD updateOc = Program.myFactory.one();
	
	// the items are moved 
	for (int i = size-1; i > 0; i--){
		updateOc = updateOc.and(els_[i-1].biimp(els[i]));
	}
	
	// particular case of channel of size 1
	if (size == 1){
		updateOc = updateOc.and(els_[0].biimp(els[0]));
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
	// if the first position is not occupied then the channel is empty.
	// result = result.and(oc[0].not().imp(empty_)).and(empty.imp(empty_));
	//BDD result = empty.imp(skip()).and(empty.not().imp(updateOc));
	
	
	return updateOc;
}


/**
 * Private method that put the argument in the correct place in the channel
 * @param	bit		The item to be added to the channel
 */
private BDD put(BDD bit){
	BDD result = Program.myFactory.one();
	
	// we calculate a BDD to say that: ! oc[i-1] => !oc[i],
	// perhaps not needed, since this must be ensured by the program execution ?
	
	BDD monotony = Program.myFactory.one();
	for (int i = 0; i < size - 1; i++){
		monotony = monotony.and(oc[i].not().imp(oc[i+1].not())); 
	}
	
	for (int i = size -1; i >= 0; i--){
		if (i == 0){
			result = result.and(oc[i].not().imp(els_[i].biimp(bit).and(oc_[i]))).and(oc[i].imp(els_[i].biimp(els[i]).and(oc_[i])));
			//result = result.and(oc[i].not().imp(els_[i].biimp(bit).and(oc_[i])).and(onlyChangesOc(i))).and(oc[i].imp(els_[i].biimp(els[i]).and(oc_[i])));
			// we dont need onlyChangesOc!
		}
		if(i > 0){
			result = result.and(oc[i].not().and(oc[i-1]).imp(els_[i].biimp(bit).and(oc_[i]).and(onlyChangesOc(i)))).and(oc[i].or(oc[i-1].not()).imp(oc_[i].biimp(oc[i]).and(els_[i].biimp(els[i]))));
		}
	}
	result = result.and(oc_[size-1].biimp(full_));
	result = result.and(oc[size-1].biimp(full));
	result = result.and(oc_[0].not().biimp(empty_));
	result = result.and(oc[0].not().biimp(empty));
	result = result.and(monotony);
	//result.printSet();
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
	
	
	//result = result.and(full.imp(empty.not()));
	//result = result.and(empty.imp(full.not()));
	//result = result.and(oc[0].imp(empty.not()));
	//result = result.and(oc[0].not().imp(empty));
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
	channelInfo= channelInfo.concat(chanString).concat("[").concat(sizeS).concat("] of BOOL");
	channelInfo= channelInfo.concat("\n\n");
 	return channelInfo;
}


}// end of BoolChannel