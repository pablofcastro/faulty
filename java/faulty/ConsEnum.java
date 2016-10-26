package faulty;
import java.util.*;

import net.sf.javabdd.*;
import mc.*;

/**
 * this class represents a enum constant, it has the info needed
 * to deal with a constant of an enumerable type
 * @author Pablo
 *
 */
public class ConsEnum implements EnumExp{
	String name; // the name of the constant	
	Program myProgram;
	EnumType myType;
	BDD[] bits; // the value
	
	
	/**
	 * 
	 * @param name
	 * @param type
	 * @param myProgram	the program where the enums live
	 */
	public ConsEnum(String name, String type, Program myProgram){
		this.name = name;		
		this.myProgram = myProgram;
		
		// we get the enum type corresponding to this cons
		this.myType = myProgram.getEnum(type);
		int size = myType.getSize();
		int id = myType.getConsId(name);
	
		bits = new BDD[myType.getBitsNumber()];

        
        // we set the correct value to the constant
		
		int i = id;
		int j = myType.getBitsNumber()-1;
        while(j >= 0){
            if (i % 2 == 0){          	
                bits[j] = Program.myFactory.zero(); 
                j--;
                i = i / 2;
            }
            if (i % 2 != 0){   	
                bits[j] = Program.myFactory.one();
                j--;
                i = i / 2;
            }
        }
		
	}
	
	 /**
     * Returns the ith bit of the integer
     * @return	the ith bit
     */
    public BDD getBit(int i){
        return bits[i];
    }
    
    /**
     * @return The array bits
     */
    public BDD[] getBits(){
    	return bits;
    }
    
    /**
     * 
     * @return	the size of the bits array used to represent the constant
     */
    public int getBitsSize(){
    	return bits.length;
    }
    
    /**
     * Returns a BDD representing the Constant
     * @return the BDD
     */
    public BDD getBDD(){
        BDD result = Program.myFactory.one();
        int size = myType.getSize();
        for (int i=0; i < size; i++){
        	result = result.and(bits[i]);
        }
        return result;
    }

    /**
     * Returns a list of channels occurring in the expression.
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    /**
     * The duplicate of a Constant is itself
     * @param instName
     * @return	a reference to this
     */
    public ConsEnum duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	return this;
    }
    
    /**
     * The duplicate of a Constant is itself, this version takes into account the parameters
     * @param instName
     * @return	a reference to this
     */
    public ConsEnum duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	return this;
    }
    
    /**
     * 
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public ConsEnum duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	return this;
    }
    
    
    @Override
    public String toString(){
        String consInfo = new String(name);
	 	return consInfo;
	}


	
	

}
