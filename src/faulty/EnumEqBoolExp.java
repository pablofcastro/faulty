package faulty;
import java.util.*;

import net.sf.javabdd.*;
public class EnumEqBoolExp implements BoolExp{
	EnumExp exp1; // the left exp
	EnumExp exp2; // the right exp
	EnumType type;

	/**
	 * Constructor, it takes the two integers composing hte equality
	 * @param	exp1	the left integer
	 * @param	exp2	the right integer
	 */
	public EnumEqBoolExp(EnumExp exp1, EnumExp exp2, EnumType type){
	    this.exp1 = exp1;
	    this.exp2 = exp2;
	    this.type = type;
	}

	/**
	 * Returns the BDD representing the equality
	 * @return the BDD
	 */
	public BDD getBDD(){
	    BDD result = Program.myFactory.one();
	    for(int i = 0; i < type.getSize(); i++){
	        result = result.and(exp1.getBit(i).biimp(exp2.getBit(i)));
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
		result.addAll(exp1.getChannels());
		result.addAll(exp2.getChannels());
		return result;
	}

	/**
	 * 
	 * @param instName	the name of the new instance 
	 * @return	a duplicate of the current expression
	 */
	public EnumEqBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
		EnumEqBoolExp result = new EnumEqBoolExp((EnumExp) exp1.duplicate(instName, boolMap, intMap, owner), (EnumExp) exp2.duplicate(instName, boolMap, intMap, owner), this.type);
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
	public EnumEqBoolExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
		EnumEqBoolExp result = new EnumEqBoolExp((EnumExp) exp1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (EnumExp) exp2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), this.type);
		return result;
	}

	/**
	 * 
	 * @param instName
	 * @param dups
	 * @param owner
	 * @return
	 */
	public EnumEqBoolExp duplicate(String instName, HashMap<Var, Var> dups,  Process owner){
		EnumEqBoolExp result = new EnumEqBoolExp((EnumExp) exp1.duplicate(instName, dups, owner), (EnumExp) exp2.duplicate(instName, dups, owner), this.type);
		return result;
	}

	@Override
	public String toString(){
	    
	    String eqInfo = new String(" ");
		
		String  exp1String= exp1.toString(); 
		String  exp2String= exp2.toString(); 
		
		eqInfo= eqInfo.concat(exp1String).concat("==").concat(exp2String);
	    
	 	return eqInfo;
	}
	

}
