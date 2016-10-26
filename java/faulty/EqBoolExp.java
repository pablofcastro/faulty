package faulty;
import java.util.*;

import net.sf.javabdd.*;

/**
 * A class that implements the boolean expresion int1 = int2
 */

public class EqBoolExp implements BoolExp{

IntExp int1; // the left int
IntExp int2; // the right int

/**
 * Constructor, it takes the two integers composing hte equality
 * @param	int1	the left integer
 * @param	int2	the right integer
 */
public EqBoolExp(IntExp int1, IntExp int2){
    this.int1 = int1;
    this.int2 = int2;
}

/**
 * Returns the BDD representing the equality
 * @return the BDD
 */
public BDD getBDD(){
    BDD result = Program.myFactory.one();
    for(int i = 0; i < Program.intSize; i++){
        result = result.and(int1.getBit(i).biimp(int2.getBit(i)));
    }
    return result;
}

public LinkedList<Var> getVars(){
	LinkedList<Var> result = new LinkedList<Var>();
	return result;
}

/**
 * Returns a list of channels partipating in th equality
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
 * @return	a duplicate of the current expression
 */
public EqBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
	EqBoolExp result = new EqBoolExp((IntExp) int1.duplicate(instName, boolMap, intMap, owner), (IntExp) int2.duplicate(instName, boolMap, intMap, owner));
	return result;
}

/**
 * Another version of duplicate that takes into account process parameters
 * @param instName
 * @param boolMap
 * @param intMap
 * @param owner
 * @return
 */
public EqBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
	EqBoolExp result = new EqBoolExp((IntExp) int1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (IntExp) int2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner));
	return result;
}

/**
 * 
 * @param instName
 * @param dups
 * @param owner
 * @return
 */
public EqBoolExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
	EqBoolExp result = new EqBoolExp((IntExp) int1.duplicate(instName, dups, owner), (IntExp) int2.duplicate(instName, dups, owner));
	return result;
}

@Override
public String toString(){
    
    String eqInfo = new String(" ");
	
	String  exp1String= int1.toString(); 
	String  exp2String= int2.toString(); 
	
	eqInfo= eqInfo.concat(exp1String).concat("==").concat(exp2String);
    
 	return eqInfo;
}


}