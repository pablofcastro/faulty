package faulty;
import java.util.*;
import net.sf.javabdd.*;
/**
 *  A class to represent the "and" of two boolean expressions
 */
public class AndBoolExp implements BoolExp{
    BoolExp exp1; // the left expression
    BoolExp exp2; // the right expression
    
    /**
     * Basic constructor for the class
     * @param 	exp1	the left expression
     * @param	epx2	the right expression
     */
    public AndBoolExp(BoolExp exp1, BoolExp exp2){
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    /**
     * Returns the BDD representing the expression
     * @return the BDD
     */
    public BDD getBDD(){
        return exp1.getBDD().and(exp2.getBDD());
    }
    
    /**
     * Return the list of channel occurring in the expression
     * @return the list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp1.getChannels());
    	result.addAll(exp2.getChannels());
    	return result;
    }
    
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	AndBoolExp result = new AndBoolExp((BoolExp) exp1.duplicate(instName, boolMap, intMap, owner), (BoolExp) exp2.duplicate(instName, boolMap, intMap, owner));
    	return result;
    	
    }
    
    
}
