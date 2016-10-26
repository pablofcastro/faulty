package faulty;
import java.util.*;

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
    
    /**
     * 
     * @param instName
     * @return	a reference to a duplicate expression
     */
    public DivIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	DivIntExp result = new DivIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, owner), model);
    	return result;	
    }
    
    /**
     * Another duplicate taking into account parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public DivIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	DivIntExp result = new DivIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), model);
    	return result;	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public DivIntExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	DivIntExp result = new DivIntExp((IntExp) exp1.duplicate(instName, dups, owner), (IntExp) exp2.duplicate(instName, dups, owner), model);
    	return result;	
    }
    
    @Override
    public String toString(){
        
        String divInfo = new String(" ");
		
		String  exp1String= exp1.toString(); 
		String  exp2String= exp2.toString(); 
		
		divInfo= divInfo.concat(exp1String).concat("/").concat(exp2String);
        
	 	return divInfo;
	}

    
}
