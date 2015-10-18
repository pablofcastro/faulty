package faulty;
import java.util.LinkedList;

import net.sf.javabdd.*;
import mc.*;

/**
 * A class that implements the division between two integers: int1 / int2
 * TO BE IMPLEMENTED, DO NOT USE 
 */
public class DivIntExp implements IntExp{
    IntExp exp1; // left integer
    IntExp exp2; // right integer
    BDD[] bits; // result
    BDDModel model;

    /**
     * Constructor of the class, it takes as paramenter the two integers
     * @param	int1	the left integer
     * @param	int2	the right integer
     */
    public DivIntExp(IntExp int1, IntExp int2, BDDModel model){
    	this.exp1 = int1;
    	this.exp2 = int2;
    }
    
    /**
     * Returns a BDD representing the result of the division
     * @return a BDD
     */
    public BDD getBDD(){
        return  null;
    }
    
    /**
     * Returns the ith bits of the result of hte division
     * @param	the i
     * @return a BDD
     */
    public BDD getBit(int i){
        return null;
    }
    
    
    /**
     * Return the bits Array
     */
    public BDD[] getBits(){
    	return bits;
    }
    
    
    /**
     * Returns the list of channels involved in hte division
     * @return a list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp1.getChannels());
    	result.addAll(exp2.getChannels());
    	return result;
    }
    
}
