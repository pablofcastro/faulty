package faulty.auxiliar;


import java.util.*;

/**
 * A class representing an integer constant
 */
// Intcons it represents a constant of type Int
public class AuxiliarConsIntExp extends AuxiliarExpression{
    
	Integer value;
    /**
     * Basic constructor for the class, it constructs the number expressed as a BDD
     * @param	i	the integer
     */
    public AuxiliarConsIntExp(Integer i){
      super();
      value = i;
    }
    

    /**
     * Returns a list of channels occurring in the expression.
     */
    public LinkedList<AuxiliarChannel> getChannels(){
    	LinkedList<AuxiliarChannel> result = new LinkedList<AuxiliarChannel>();
    	return result;
    }
    
    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
}