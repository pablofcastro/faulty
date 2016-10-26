package faulty;
import net.sf.javabdd.*;

import java.util.*;

// OrBoolClass a class to represent and boolean expressions
public class OrBoolExp implements BoolExp{
   // BoolOp op;
    BoolExp exp1;
    BoolExp exp2;
    
    // Constructor for the class
    public OrBoolExp(BoolExp exp1, BoolExp exp2){
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.addAll(exp1.getVars());
    	result.addAll(exp2.getVars());
    	return result;
    }
    
    public BDD getBDD(){
        return exp1.getBDD().or(exp2.getBDD());
    }
    
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
    public OrBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	OrBoolExp result = new OrBoolExp((BoolExp) exp1.duplicate(instName, boolMap, intMap, owner), (BoolExp) exp2.duplicate(instName, boolMap, intMap, owner));
    	return result;
    	
    }
    
    /**
     * 
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public OrBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	OrBoolExp result = new OrBoolExp((BoolExp) exp1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (BoolExp) exp2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
    	return result;
    	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public OrBoolExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	OrBoolExp result = new OrBoolExp((BoolExp) exp1.duplicate(instName, dups, owner), (BoolExp) exp2.duplicate(instName, dups, owner));
    	return result;
    }
    
    @Override
    public String toString(){
        
        String orInfo = new String(" ");
    	
    	String  exp1String= exp1.toString(); 
    	String  exp2String= exp2.toString(); 
    	
    	orInfo= orInfo.concat(exp1String).concat(" || ").concat(exp2String);
        
     	return orInfo;
    }

}
