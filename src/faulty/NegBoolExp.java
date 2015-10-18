package faulty;
import net.sf.javabdd.*;
import java.util.*;


/**
 * A class that represent the negation of a boolean expression
 */
public class NegBoolExp implements BoolExp{
   
    BoolExp exp; // the expresison to be negated
    
    /**
     * Constructor of the class
     * @param	exp	the expression in the negation
     */
    public NegBoolExp(BoolExp exp){
        this.exp = exp;
    }
    
    /**
     * Returns a BDD of representing the expression
     */
    public BDD getBDD(){
        return exp.getBDD().not();
    }
    
    /**
     * Returns the list of channels occurring in the expression
     */
    public LinkedList<Channel> getChannels(){
    	return exp.getChannels();
    }

}
