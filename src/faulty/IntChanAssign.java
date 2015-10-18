package faulty;
import net.sf.javabdd.*;
import java.util.*;

/**
 * A class that represents an assignation to an Int channel, that is, a value is 
 * put into the channel. 
 */

public class IntChanAssign implements Code{

    IntChannel chan; // the channel 
    IntExp exp; // the expression whose value is put into the channel
    
    
    /**
     * Basic constructor.
     * @param	channel 	the channel in which the value is inserted
     * @param	exp			the expression whose value is put into de channel
     */
    public IntChanAssign(IntChannel chan, IntExp exp){
    	this.chan = chan;
    	this.exp = exp;
    	
    }
    
    /**
     * Returns the representation of this code as a BDD
     * @return	the BDD respresenting the assignation
     */
    public BDD getBDD(){
        BDD result = chan.BDDPut(exp.getBits());
        return result;
    }
    
    /**
     * Returns the channels involved in the assignation (just chan)
     * @return the list of channels involved
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.add(chan);
    	return result;
    }
    
    /**
     * Returns the vars involved
     * @return the vars involved in the assignation
     */
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	return result;
    }

    /**
     * Returns a BDD representing a Skip
     * @return the BDD
     */
    public BDD skipBDD(){
    	BDD result = chan.skipBDD();
    	return result;
    }
    
    /**
     * Returns the list of channels ocurring at the left og the assignation chan := E (just chan)
     * @return the channel
     */
    public LinkedList<Channel> getChannelsLeft(){
    	LinkedList<Channel> result = new LinkedList();
    	result.add(chan);
    	return result;
    }
    
    /**
     * Returns the list of channels ocurring on the right of the assignation (empty)
     * @return the channels occurring on the right
     */
    public LinkedList<Channel> getChannelsRight(){
    	return exp.getChannels();
    }
    
    
}
