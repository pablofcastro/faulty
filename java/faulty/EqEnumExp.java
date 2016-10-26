package faulty;
import java.util.*;
import net.sf.javabdd.*;

/**
 * This class contains the functionality needed for representing an equality enum == enum in Faulty,
 * it is similar to EqBoolExp
 * @author Pablo
 *
 */
public class EqEnumExp implements BoolExp{
	
	EnumExp enum1; // the left enum
	EnumExp enum2; // the right enum
	EnumType etype; // an object containing all the information wrt the enumerable type corresponding to the expression (size, name, etc)

	/**
	 * Constructor, it takes the two enums of the equality
	 * @param	enum1	the left integer
	 * @param	int2	the right integer
	 */
	public EqEnumExp(EnumExp enum1, EnumExp enum2, EnumType type){
	    this.enum1 = enum1;
	    this.enum2 = enum2;
	    this.etype = type;
	}

	/**
	 * Returns the BDD representing the equality
	 * @return the BDD
	 */
	public BDD getBDD(){
	    BDD result = Program.myFactory.one();
        int numBits = etype.getBitsNumber(); // number of bits = log_2(size)
               
	    for(int i = 0; i < numBits; i++){        
            result = result.and(enum1.getBit(i).biimp(enum2.getBit(i)));
	    }
	    return result;
	}

	/**
	 * 
	 * @return	The list of vars occurring in the current expression
	 */
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
		result.addAll(enum1.getChannels());
		result.addAll(enum2.getChannels());
		return result;
	}

	/**
	 * 
	 * @param instName	the name of the new instance 
	 * @return	a duplicate of the current expression
	 */
	public EqEnumExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
        EqEnumExp result = new EqEnumExp((EnumExp) enum1.duplicate(instName, boolMap, intMap, owner), (EnumExp) enum2.duplicate(instName, boolMap, intMap, owner), etype);
		return result;
	}

	/**
	 * Another version of duplicate that takes into account process parameters
	 * @param instName
	 * @param boolMap
	 * @param intMap
	 * @param owner
	 * @return	A duplicated of the current equation
	 */
	public EqEnumExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
		EqEnumExp result = new EqEnumExp((EnumExp) enum1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (EnumExp) enum2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), etype);
		return result;
	}

	/**
	 * 
	 * @param instName
	 * @param dups
	 * @param owner
	 * @return	A duplicated of the current equation
	 */
	public EqEnumExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
        
        EqEnumExp result = new EqEnumExp((EnumExp) enum1.duplicate(instName, dups, owner), (EnumExp) enum2.duplicate(instName, dups, owner), etype);
		return result;
	}
	
	/**
	 * 
	 * @return	a string representing the current expression
	 */
	@Override
	public String toString(){
	    
	    String eqInfo = new String(" ");
	
		String  exp1String= enum1.toString(); 
		String  exp2String= enum2.toString(); 
		
		eqInfo= eqInfo.concat(exp1String).concat("==").concat(exp2String);
	    
	 	return eqInfo;
	}

	

}
