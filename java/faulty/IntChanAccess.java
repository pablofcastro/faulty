package faulty;
import java.util.LinkedList;

import net.sf.javabdd.*;

import java.util.*;

/**
 * A class representing the access to an Int channel.
 */
public class IntChanAccess implements IntExp{
    IntChannel chan; // the channel to be accessed
    
    /**
     * Constructor of the class
     * @param	ch	the channel to be accessed
     */
    public IntChanAccess(IntChannel ch){
        this.chan = ch;
    }
    
    /**
     * Returns the ith BDD representing the ith digit of the expression
     * @return the ith bit of the expression
     */
    public BDD getBit(int i){
    	// to be implemented
        return chan.getItem(i);
    }
    
    
    public BDD[] getBits(){
    	BDD[] result = new BDD[Program.intSize];
    	for (int i = 0; i < Program.intSize; i++){
    		result[i] = chan.getItem(i);
    	}
    	return result;
    }
    
    /**
     * Returns the list of channels occurring in the expression
     * @return the list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.add(chan);
    	return result;
    }
    
    /**
     * Returns the BDD representing the expression
     */
    public BDD getBDD(){
    	
    	 BDD result = Program.myFactory.one();
         for (int i=0; i < Program.intSize; i++){
             result = result.and(chan.getItem(i));
         }
         return result;
    }
    
    /**
     * The duplicate of a IntChannelAcces is itself, it is a global var
     * @param instName	the name of the new instance
     * @return	a reference	to this
     */
    public IntChanAccess duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	return this;    	
    }
    
    /**
     * Another duplicate to take into account process parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public IntChanAccess duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	return this;    	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public IntChanAccess duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	return this;    	
    }
    
    @Override
    public String toString(){
        
        String channelInfo = new String(" ");
    	
    	String  chanString= chan.getName(); 
    	channelInfo= channelInfo.concat(chanString).concat(".get( ) ");
        
     	return channelInfo;
    }


    
}
