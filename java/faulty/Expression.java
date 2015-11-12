package faulty;
import net.sf.javabdd.*;
import java.util.*;

/**
 * A basic interface that provides the basic description of an expression, 
 * Implemented by: BoolExp, IntExp, etc
 */
public interface Expression{

	/**
	 * Basic methods returning the BDD representing the expression
	 */
    public BDD getBDD();
    
    /**
     * Returns the channels involved in the expression
     */
    public LinkedList<Channel> getChannels(); 
    
    /**
     * builds a duplicate of the curent expression
     * @param instName	the name of the process instance where the expression belongs
     * @param boolMap	maps bool local vars to its duplicates
     * @param intMap	maps int local vars to its duplicates
     * @param owner		the proces which is being duplicated
     * @return
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner);
};
























