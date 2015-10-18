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
     */
    public BDD skipBDD();
}