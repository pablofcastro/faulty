package faulty;
import net.sf.javabdd.*;

import java.util.*;

/**
 * A class that represents a boolean constant, it could be true or false, represented by a 
 * constant BDD
 */
public class ConsBoolExp implements BoolExp{

    boolean value; // the value
    
    /**
     * Constructor of this class
     * @param value		the value of the constant: true or false
     */
    public ConsBoolExp(boolean value){
        this.value = value;
    }
    
    
    /**
     * Returns the BDD representing the constant
     * @return the corresponding BDD
     */
    public BDD getBDD(){
        if (value)
            return Program.myFactory.one();
        else
            return Program.myFactory.zero();
    }

    /**
     * Returns a list of channels occuring in the expression (empty)
     * return	the empty list
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    /**
     * 
     * @return the listo of vars appearing in the contant, null
     */
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	return result;
    }
    
    /**
     * A duplicate of a constant is the same constant
     * @param instName
     * @return "a duplicate" 
     */
    public ConsBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	return this;
    }
    
    /**
     * Another duplicate that takes into ccount parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public ConsBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars,  Process owner){
    	return this;
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public ConsBoolExp duplicate(String instName, HashMap<Var, Var> dups,  Process owner){
    	return this;
    }
    
    @Override
    public String toString(){
    	
        String consInfo;
        
	 	if (value){
	 		consInfo = new String("true");
	 	}else{
	 		consInfo = new String("false");
	 	}
        
        return consInfo;
	}

}
