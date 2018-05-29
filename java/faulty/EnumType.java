package faulty;
import java.util.*;

import net.sf.javabdd.*;
import mc.*;
/**
 * This class contains all the information about an enumerable type
 * this is achieved by using hashmap that relates enumtypes with their size, and 
 * identifiers with their number
 * @author Pablo
 *
 */
public class EnumType{
	String name; // the name of the enum type
	String[] cons; // the constants belonging to the type
	int size; // the size of the enumerable type 
	int bits; // the number of bits needed to store this enumtype
	Program myProgram; // the program where this enumType resides
	int numVars; // the number of variables of this type in the program
	
	/**
	 * A basic constructor
	 * @param name
	 * @param size
	 */
	public EnumType(String name, int size, Program myProgram){
		this.name = name;
		this.size = size;
		this.numVars = 0;
		this.myProgram = myProgram;
		
		if ((size & -size) == size) // if it is power of two
			bits = ((int) (Math.log(size) / Math.log(2)));
		else
			bits = ((int) (Math.log(size) / Math.log(2)))+1;
			
		cons = new String[size];
	}
	
	/**
	 * Another constructor
	 * @param name
	 * @param size
	 * @param numVars
	 */
	public EnumType(String name, int size, int numVars, Program myProgram){
		this.name = name;
		this.size = size;
		this.numVars = numVars;
		this.myProgram = myProgram;
		
		if ((size & -size) == size) // if it is power of two
			bits = ((int) (Math.log(size) / Math.log(2)));
		else
			bits = ((int) (Math.log(size) / Math.log(2)))+1;
			
		cons = new String[size];
	}
	
	/**
	 * Another constructor
	 * @param name
	 * @param size
	 * @param numVars
	 */
	public EnumType(String name, int size, int numVars){
		this.name = name;
		this.size = size;
		this.numVars = numVars;
		this.myProgram = null;
		
		if ((size & -size) == size) // if it is power of two
			bits = ((int) (Math.log(size) / Math.log(2)));
		else
			bits = ((int) (Math.log(size) / Math.log(2)))+1;
        
		cons = new String[size];
	}
	
	
	/**
	 * 
	 * @return the name of the type
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 
	 * @return the size
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * 
	 * @return	the number of variables of this type
	 */
	public int getNumVars(){
		return numVars;
	}
	
	
	/**
	 * 
	 * @return the number of bits
	 */
	public int getBitsNumber(){
		return bits;
	}
	
	/**
	 * 
	 * @param name
	 * @return the id (integer) corresponding to a given constant in the case of a wrong constant it returns -1
	 * 
	 */
	public int getConsId(String name){
		for (int i=0; i < size; i++){
			if (cons[i].equals(name))
				return i;
		}
		// no id for the name
		return -1;
	}
	
	public String getCons(int i){
		return cons[i];
	}
		
	/**
	 * Adds a constant in the enum type
	 * @param name	the name of the constant
	 * @param pos	the position 
	 * @pre pos < size
	 */
	public void addCons(String name, int pos){
		cons[pos] = name;
	}
    
	/**
	 * @param	id	the name of the constant to build
	 * @return	a ConsEnum corresponding to the constant id
	 */
	public ConsEnum getConsEnum(String id){
		ConsEnum result = new ConsEnum(id, this.name, this.myProgram);
		return result;
	}
	
    @Override
	public String toString(){
        String eType = new String(name);
        eType = eType.concat(" = { ");
        for (int i=0; i < size; i++){
			eType = eType.concat(cons[i]);
            if(i+1 != size){
			    eType = eType.concat(",");
            }
		}
        eType = eType.concat(" } ");
        
		return eType;
        
    }
        
	
	
}
