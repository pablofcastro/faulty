package faulty;
import net.sf.javabdd.*;

import java.util.*;

/**
 * Class implementing the comparation int1 > int2
 */

public class GreaterBoolExp implements BoolExp{
    IntExp int1; // left int
    IntExp int2; // right int

    /**
     * Constructor, it takes the two integer in the comparison as parameters
     * @param	int1	left integer
     * @param	int2	right integer
     */
    public GreaterBoolExp(IntExp int1, IntExp int2){
        this.int1 = int1;
        this.int2 = int2;
    }

    /**
     * Returns the BDD representing the expression
     * @return the BDD
     */
    public BDD getBDD(){
        BDD result = Program.myFactory.one();
        for (int i =  Program.intSize - 1; i >= 0; i--){
            //result = result.or(int1.getBDD().imp(int2.getBDD()));
        	result = result.or(int1.getBit(i).imp(int2.getBit(i)));
        }
        return result;
    }
    
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	return result;
    }
    
    /**
     * Returns a list of channels participating in the comparison
     * @return a list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(int1.getChannels());
    	result.addAll(int2.getChannels());
    	return result;
    }
    
    /**
     * 
     * @param instName	the name of the new instance
     * @return	a reference to a duplicate of the current expression
     */
    public GreaterBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	GreaterBoolExp result = new GreaterBoolExp((IntExp) int1.duplicate(instName, boolMap, intMap, owner), (IntExp) int2.duplicate(instName, boolMap, intMap, owner));
    	return result;
    	
    }
    
    /**
     * Another version of duplicate taking into account process parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public GreaterBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	GreaterBoolExp result = new GreaterBoolExp((IntExp) int1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (IntExp) int2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
    	return result;
    	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public GreaterBoolExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	GreaterBoolExp result = new GreaterBoolExp((IntExp) int1.duplicate(instName, dups, owner), (IntExp) int2.duplicate(instName, dups, owner));
    	return result;
    	
    }
    
    
    @Override
    public String toString(){
        
        String greatInfo = new String(" ");
    	
    	String  exp1String= int1.toString(); 
    	String  exp2String= int2.toString(); 
    	
    	greatInfo= greatInfo.concat(exp1String).concat(">").concat(exp2String);
        
     	return greatInfo;
    }
}