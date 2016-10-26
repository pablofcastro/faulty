package faulty;
import java.util.LinkedList;

import net.sf.javabdd.*;

import java.util.*;

/**
 * A class that represent the access to a BoolChannel, for instance, 
 * x := chan.get()
 * the right part is a Chan Access, used as an expression in right part of assignaments
 */
public class BoolChanAccess implements BoolExp{
    BoolChannel  chan; // the channel
    
    /**
     * Basic constructor
     * @param	chan	the channel to be used
     */
    public BoolChanAccess(BoolChannel chan){
        this.chan = chan;
    }
    
    /**
     * Returns a BDD representing the element get from the channel, the change of state of the channel
     * is manage through the class channel and its methods
     */
    public BDD getBDD(){
    //    return chan.getFirstAsBDD();
        return chan.getItem();
    }
    
    /**
     * Returns the channel involved in the expression
     * @return	the channel
     */
    public BoolChannel getChannel(){
    	return chan;
    }

    /**
     * Returns a list of channel involved in the expression (only one)
     * @return	the list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.add(chan);
    	return result;
    }
    
    /**
     * 
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public BoolChanAccess duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	//BoolChanAcces result = new BoolChanAccess(exp1.duplicate(instName, boolMap, intMap, owner), exp2.duplicate(instName, boolMap, intMap, owner));
    	return this;
    	
    }
    
    
    /**
     * Another version of duplicata that takes into account parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public BoolChanAccess duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	//BoolChanAcces result = new BoolChanAccess(exp1.duplicate(instName, boolMap, intMap, owner), exp2.duplicate(instName, boolMap, intMap, owner));
    	return this;
    }
    
    public BoolChanAccess duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	//BoolChanAcces result = new BoolChanAccess(exp1.duplicate(instName, boolMap, intMap, owner), exp2.duplicate(instName, boolMap, intMap, owner));
    	return this;
    }
    
    
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList();
    	return result;
    }
    
    @Override
    public String toString(){
        
        String channelInfo = new String(" ");
    	
    	String  chanString= chan.getName(); 
    	channelInfo= channelInfo.concat(chanString).concat(".get( ) ");
        
     	return channelInfo;
    }
}
