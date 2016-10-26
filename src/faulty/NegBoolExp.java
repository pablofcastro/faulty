package faulty;
import net.sf.javabdd.*;

import java.util.*;


/**
 * A class that represent the negation of a boolean expression
 */
public class NegBoolExp implements BoolExp{
   
    BoolExp exp; // the expresison to be negated
    
    /**
     * Constructor of the class
     * @param	exp	the expression in the negation
     */
    public NegBoolExp(BoolExp exp){
        this.exp = exp;
    }
    
    /**
     * Returns a BDD of representing the expression
     */
    public BDD getBDD(){
        return exp.getBDD().not();
    }
    
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.addAll(exp.getVars());
    	return result;
    }
    
    /**
     * Returns the list of channels occurring in the expression
     */
    public LinkedList<Channel> getChannels(){
    	return exp.getChannels();
    }
    
    /**
     * Duplicates the expressions
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public NegBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	NegBoolExp result = new NegBoolExp((BoolExp) exp.duplicate(instName, boolMap, intMap, owner));
    	return result;
    	
    }
    
    /**
     * Another duplicate for process with parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public NegBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	NegBoolExp result = new NegBoolExp((BoolExp) exp.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
    	return result;
    	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public NegBoolExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	NegBoolExp result = new NegBoolExp((BoolExp) exp.duplicate(instName, dups, owner));
    	return result;
    	
    }
    
    @Override
    public String toString(){
        
        String negInfo = new String("");
		
		String  expString= exp.toString(); 
		
		negInfo= negInfo.concat("!").concat(expString);
        
	 	return negInfo;
	
		
    }


}
