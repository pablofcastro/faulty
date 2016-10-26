package faulty;
import net.sf.javabdd.*;

import java.util.*;

/**
 * A class representing an integer constant
 */
// Intcons it represents a constant of type Int
public class ConsIntExp implements IntExp{
    BDD[] bits; // the bits representing the constant
    
    /**
     * Basic constructor for the class, it constructs the number expressed as a BDD
     * @param	i	the integer
     */
    public ConsIntExp(int i){
    	bits = new BDD[Program.intSize];
        int j = Program.intSize-1;
        while(j >= 0){
            if (i % 2 == 0){          	
                bits[j] = Program.myFactory.zero(); 
                j--;
                i = i / 2;
            }
            if (i % 2 != 0){
            	
                bits[j] = Program.myFactory.one();
                j--;
                i = i / 2;
            }
        }
        //bits[j] = Program.myFactory.one();
    }
    
    /**
     * Other constructor that takes as param the binary representation of the constant
     * @param bits	the binary representation
     */
    public ConsIntExp(BDD[] bits){
    	this.bits = bits;
    }
    /**
     * Returns the ith bit of the integer
     * @return	the ith bit
     */
    public BDD getBit(int i){
        return bits[i];
    }
    
    /**
     * @return The array bits
     */
    public BDD[] getBits(){
    	return bits;
    }
    
    /**
     * Returns a BDD representing the Constant
     * @return the BDD
     */
    public BDD getBDD(){
        BDD result = Program.myFactory.one();
        for (int i=0; i < Program.intSize; i++){
        	result = result.and(bits[i]);
        }
        return result;
    }

    /**
     * Returns a list of channels occurring in the expression.
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    /**
     * The duplicate of a Constant is itself
     * @param instName
     * @return	a reference to this
     */
    public ConsIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	return this;
    }
    
    /**
     * The duplicate of a Constant is itself, this version takes into account the parameters
     * @param instName
     * @return	a reference to this
     */
    public ConsIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	return this;
    }
    
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public ConsIntExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	return this;
    }
    
    
    @Override
    public String toString(){
        String consInfo = new String("anINTEGER");
	 	return consInfo;
	}


}