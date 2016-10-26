package faulty;
import net.sf.javabdd.*;

import java.util.*;

/**
 * A simple interface that provides the basic specification of any code,
 * getBDD is the main methods that returns the BDD representation of the Code
 * implemented by Assign, and MultAssign
 */
public interface Code{

	/**
	 * Returns the BDD representation of the code
	 */
    public BDD getBDD();
    
    /**
     * Returns the channels involved in the code
     */
    public LinkedList<Channel> getChannels();
    
    /**
     * returns a list of the channels appearing on the left of an assignation
     */
    public LinkedList<Channel> getChannelsLeft();
    
    /**
     * Returns a list of channel appearing at the right of the code (we assume the code is somethign like Var := F)
     */
    public LinkedList<Channel> getChannelsRight();
    
    /**
     * Returns the vars involved in the code
     */
    public LinkedList<Var> getVars();
    
    /**
     * Returns a BDD representing a skip, useful when some other part of the program is executed
     * @return A BDD representing a skip
     */
    public BDD skipBDD();
    
    /**
     * Duplicates the given reference
     * @param instName	
     * @return	the duplicate
     */
    public Code duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner);
    
    /**
     * Similar to above but taking into account parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public Code duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner);
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public Code duplicate(String instName, HashMap<Var, Var> dups, Process owner);
    	
}