package faulty;
import java.util.*;

import net.sf.javabdd.*;
/**
 *  A class to represent the "and" of two boolean expressions
 */
public class AndBoolExp implements BoolExp{
    BoolExp exp1; // the left expression
    BoolExp exp2; // the right expression
    
    /**
     * Basic constructor for the class
     * @param 	exp1	the left expression
     * @param	epx2	the right expression
     */
    public AndBoolExp(BoolExp exp1, BoolExp exp2){
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    /**
     * Returns the BDD representing the expression
     * @return the BDD
     */
    public BDD getBDD(){
        return exp1.getBDD().and(exp2.getBDD());
    }
    
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.addAll(exp1.getVars());
    	result.addAll(exp2.getVars());
    	return result;
    }
    
    /**
     * Return the list of channel occurring in the expression
     * @return the list of channels
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
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	AndBoolExp result = new AndBoolExp((BoolExp) exp1.duplicate(instName, boolMap, intMap, owner), (BoolExp) exp2.duplicate(instName, boolMap, intMap, owner));
    	return result;
    	
    }
    
    /**
     * Another version of duplicate taking into account parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	AndBoolExp result = new AndBoolExp((BoolExp) exp1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (BoolExp) exp2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
    	return result;	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public Expression duplicate(String instName, HashMap<Var, Var> dups,  Process owner){
    	AndBoolExp result = new AndBoolExp((BoolExp) exp1.duplicate(instName, dups, owner), (BoolExp) exp2.duplicate(instName, dups, owner));
    	return result;	
    }
    
    @Override
  	public String toString(){
    
        String andInfo = new String(" ");
		
		String  exp1String= exp1.toString(); 
		String  exp2String= exp2.toString(); 
		
		andInfo= andInfo.concat(exp1String).concat(" && ").concat(exp2String);
        
	 	return andInfo;
	
		
    }

}
