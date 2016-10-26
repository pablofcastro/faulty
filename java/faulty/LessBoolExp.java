package faulty;
import net.sf.javabdd.*;

import java.util.*;


/**
 * This class implements the comparation int1 < int2
 */
public class LessBoolExp implements BoolExp{
    IntExp int1; // the left integer
    IntExp int2; // the right integer

    /**
     * Basic constructor of the class
     * @param int1	the left integer
     * @param int2	the right integer
     */
    public LessBoolExp(IntExp int1, IntExp int2){
        this.int1 = int1;
        this.int2 = int2;
    }

    /**
     * Returns the BDD representing the BDD
     * @return a BDD 
     */
    public BDD getBDD(){
        BDD result = Program.myFactory.one();
        for (int i =  Program.intSize - 1; i >=0; i--){
            //result = result.or(int2.getBDD().imp(int1.getBDD()));
        	result = result.or(int2.getBit(i).imp(int1.getBit(i)));
        }
        return result;
    }
    
    /**
     * 
     * @return
     */
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	return result;
    }
    
    /**
     * Returns the list of channels participating in the comparison
     * @return the list of channel
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(int1.getChannels());
    	result.addAll(int2.getChannels());
    	return result;
    }
    
    public LessBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	LessBoolExp result = new LessBoolExp((IntExp) int1.duplicate(instName, boolMap, intMap, owner), (IntExp) int2.duplicate(instName, boolMap, intMap, owner));
    	return result;
    	
    }
    
    public LessBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	LessBoolExp result = new LessBoolExp((IntExp) int1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (IntExp) int2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
    	return result;
    	
    }
    
    public LessBoolExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	LessBoolExp result = new LessBoolExp((IntExp) int1.duplicate(instName, dups, owner), (IntExp) int2.duplicate(instName, dups, owner));
    	return result;
    	
    }
    
    @Override
    public String toString(){
        
        String lessInfo = new String(" ");
    	
    	String  exp1String= int1.toString(); 
    	String  exp2String= int2.toString(); 
    	
    	lessInfo= lessInfo.concat(exp1String).concat("<").concat(exp2String);
        
     	return lessInfo;
    }
}