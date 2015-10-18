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
};
























