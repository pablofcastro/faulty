package faulty;
import net.sf.javabdd.*;
import java.util.*;

// OrBoolClass a class to represent and boolean expressions
public class OrBoolExp implements BoolExp{
   // BoolOp op;
    BoolExp exp1;
    BoolExp exp2;
    
    // Constructor for the class
    public OrBoolExp(BoolExp exp1, BoolExp exp2){
        this.exp1 = exp1;
        this.exp2 = exp2;
    }
    
    public BDD getBDD(){
        return exp1.getBDD().or(exp2.getBDD());
    }
    
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp1.getChannels());
    	result.addAll(exp2.getChannels());
    	return result;
    }
}
