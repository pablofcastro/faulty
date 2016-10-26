package faulty;
import net.sf.javabdd.*;

import java.util.*;


/**
 * A class representing an assignation to a var: var := Expr
 * it implements the interface code
 */
public class VarAssign implements Code{

    Var var; // the var in the left part
    Expression exp; // the expression of the right part
    LinkedList<Channel> channels; // the list of channels occurring in the expressions 
    
    
    /**
     * Basic constructor of the class
     * @param		var 	the var in the assignment
     * @param		exp		the expression in the (right part of the) assignment
     */
    public VarAssign(Var var, Expression exp){
    	// Note: the var and the expression have to be of the same type
    	this.var = var;
    	this.exp = exp;
    	channels = exp.getChannels();
    }
    
    /**
     * Returns the BDD representing the assignment
     * @return	the BDD representing the assignment
     */
    public BDD getBDD(){
    	// BDD result = var.getBDD().and(var.updatePrime(exp));
    	 BDD result = var.updatePrime(exp);
    	
    	// the following loop calculates the state of the involved channels after a get
    	for (int i = 0; i < channels.size(); i++){
    		result = result.and(channels.get(i).BDDGet());
    	}
    
        return result;
    }
    
    /**
     * Returns the list of channel participating in the assignment
     * @return	the list of channels appearing in the assignment
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp.getChannels());
    	return result;	
    }
    
    /**
     * Returns the list of vars occuring in the assignment (only the left var)
     * @reutrn hte list of vars 
     */
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();    
    	// if it is a reference we must add the reference to the list
    	if (var.isReference()){
    		if (var.getType().equals("bool"))
    			result.add(((ParamBool) var).getReference());
    		if (var.getType().equals("int"))
    			result.add(((ParamInt) var).getReference());
    		if (var.getType().equals("enum"))
    			result.add(((ParamEnum) var).getReference());
    	}
    	else{ // otherwise we add the var
    		result.add(var);
    	}
    	return result;
    }
    
    /**
     * Returns a BDD representing a skip
     * @return a skip bDD
     */
    public BDD skipBDD(){
    	BDD result = Program.myFactory.one();
    	
    	for (int i = 0; i < channels.size(); i++){
    		result = result.and(channels.get(i).skipBDD());
    	}
    	result = var.skipBDD().and(result);
    
    	return result;
    	
    }
    
    /**
     * Returns the list of channels occurring on the left
     * @return the list of channels
     */
    public LinkedList<Channel> getChannelsLeft(){
    	LinkedList<Channel> empty = new LinkedList<Channel>();
    	return empty;
    }
    
    /**
     * Returns the list of channel occurring on the right of the assignment
     * @return the list of channels
     */
    public LinkedList<Channel> getChannelsRight(){
    	return this.getChannels();
    }
    
    /**
     *It duplicates the current code calling recursively to its components
     * @return a reference to a duplicate
     */
    public Code duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	
    	VarAssign result = new VarAssign(var.duplicate(instName, boolMap, intMap, owner), exp.duplicate(instName, boolMap, intMap, owner));
    	return result;
    }
    
    public Code duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){ 	
    	VarAssign result = new VarAssign(var.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), exp.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
    	return result;
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public Code duplicate(String instName, HashMap<Var, Var> dups, Process owner){ 	
    	VarAssign result = new VarAssign(var.duplicate(instName, dups, owner), exp.duplicate(instName, dups, owner));
    	return result;
    }
    
    
    
    
    @Override
    public String toString(){
        String assignInfo = new String("");
    	String  varString= var.toString(); 
    	String exprString = exp.toString();
    	assignInfo= assignInfo.concat(varString).concat(" = ").concat(exprString);
    	
     	return assignInfo;
    }
    
}
