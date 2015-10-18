package faulty;
import java.util.LinkedList;
import net.sf.javabdd.*;

/**
 * A class that represent the access to a BoolChannel, for instance, 
 * x := chan.get()
 * the left part is a Chan Access, used as an expression in left part of assignaments
 */
public class BoolChanAccess implements BoolExp{
    BoolChannel  chan; // the channel
    
    /**
     * Basic constructor
     * @param	chan	the channel to be used
     */
    public BoolChanAccess(BoolChannel chan){
        this.chan = chan;
    }
    
    /**
     * Returns a BDD representing the element get from the channel, the change of state of the channel
     * is manage through the class channel and its methods
     */
    public BDD getBDD(){
    //    return chan.getFirstAsBDD();
        return chan.getItem();
    }
    
    /**
     * Returns the channel involved in the expression
     * @return	the channel
     */
    public BoolChannel getChannel(){
    	return chan;
    }

    /**
     * Returns a list of channel involved in the expression (only one)
     * @return	the list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.add(chan);
    	return result;
    }
}
