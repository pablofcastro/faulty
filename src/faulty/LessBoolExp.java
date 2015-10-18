package faulty;
import net.sf.javabdd.*;
import java.util.*;


/**
 * This class implements the comparation int1 < int2
 */
public class LessBoolExp implements BoolExp{
    IntExp int1; // the left integer
    IntExp int2; // the right integer

    /**
     * Basic constructor of the class
     * @param int1	the left integer
     * @param int2	the right integer
     */
    public LessBoolExp(IntExp int1, IntExp int2){
        this.int1 = int1;
        this.int2 = int2;
    }

    /**
     * Returns the BDD representing the BDD
     * @return a BDD 
     */
    public BDD getBDD(){
        BDD result = Program.myFactory.one();
        for (int i =  Program.intSize - 1; i >=0; i--){
            //result = result.or(int2.getBDD().imp(int1.getBDD()));
        	result = result.or(int2.getBit(i).imp(int1.getBit(i)));
        }
        return result;
    }
    
    /**
     * Returns the list of channels participating in the comparison
     * @return the list of channel
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(int1.getChannels());
    	result.addAll(int2.getChannels());
    	return result;
    }
}