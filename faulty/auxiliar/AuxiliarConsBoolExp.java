package faulty.auxiliar;


import java.util.*;

/**
 * A class that represents a boolean constant, it could be true or false, represented by a 
 * constant BDD
 */
public class AuxiliarConsBoolExp extends AuxiliarExpression{

    boolean value; // the value
    
    /**
     * Constructor of this class
     * @param value		the value of the constant: true or false
     */
    public AuxiliarConsBoolExp(boolean value){
    	super();
        this.value = value;
    }
    
    
    /**
     * Returns a list of channels occuring in the expression (empty)
     * return	the empty list
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
