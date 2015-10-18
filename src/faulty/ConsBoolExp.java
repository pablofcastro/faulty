package faulty;
import net.sf.javabdd.*;
import java.util.*;

/**
 * A class that represents a boolean constant, it could be true or false, represented by a 
 * constant BDD
 */
public class ConsBoolExp implements BoolExp{

    boolean value; // the value
    
    /**
     * Constructor of this class
     * @param value		the value of the constant: true or false
     */
    public ConsBoolExp(boolean value){
        this.value = value;
    }
    
    
    /**
     * Returns the BDD representing the constant
     * @return the corresponding BDD
     */
    public BDD getBDD(){
        if (value)
            return Program.myFactory.one();
        else
            return Program.myFactory.zero();
    }

    /**
     * Returns a list of channels occuring in the expression (empty)
     * return	the empty list
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
}
