package faulty;
import java.util.*;

import net.sf.javabdd.*;
import mc.*;
/**
 * A simple interface for Enum expressions
 * @author Pablo
 *
 */


public interface EnumExp extends Expression{

	/**
	 * @param	i	the ith bit to be returned
	 * @return the ith bit
	 */
	public BDD getBit(int i);
	 
	/**
	 * Returns the integer of the expression
	 * @return the bits of the integers in an array
	 */
	public BDD[] getBits();
	
	
	/**
	 * @return the BDD representing the expression
	 */
    public BDD getBDD();
    
    /**
     * @return the channels in hte expression
     */
    public LinkedList<Channel> getChannels();
    
    /**
     * @return Duplicates the expression
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process  owner);
   
    
    /**
     * @return Duplicates the expression taking into account parameters
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process  owner);
   
    /**
     * @return Duplicates the expression taking into account parameters
     */
    public Expression duplicate(String instName, HashMap<Var, Var> dups, Process  owner);
    
}
