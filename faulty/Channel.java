package faulty;
import net.sf.javabdd.*;


/**
 * Abstract class for channels. It defines the basic behavior provided by
 * channels, implemented by boolChannel and intChannel.
 */

public abstract class Channel{
	
    
     String name; // A Name for the channel
    
    
    /**
     * A constructor for the class
     * @param 	name 	the name of the channel
     */
    public Channel(String name){
    	this.name = name;
    
    }
    
    /**
	 * Returns the size of the channel
	 * @return the size of the channel
	 */
    abstract public int getSize();
    
    /**
	 * Returns the name of the channel
	 * @return the name of the channel
	 */
    abstract public String getName();
    
     /**
     * Returns BDD respresenting the state of the channel after a get
     * @return BDD representing the channel
     */
    abstract public BDD BDDGet(); // NOTE: move to IntChannel and BoolChannel
    
    ///**
    // * Returns a BDD representation of the state of the channel after an item was inserted
    // * @return BDD representing a put
    // */
    //abstract public BDD BDDPut(BDD item); // Note: Move to IntChannel and BoolChannel
    
    /**
     * Returns a BDD representing an empty channel 
     * @return BDD representing an empty channel
     */
    abstract public BDD getEmpty();
    
    /**
     * Returns a BDD representing 
     * @return a BDD representing a full channel
     */
    abstract public BDD getFull();
    
    /**
     * Returns a BDD respresenting the skip action over the BDD
     * @return BDD representing a skip
     */
    abstract public BDD skipBDD();
    
    
    //abstract public BDD getItem();
    
}


