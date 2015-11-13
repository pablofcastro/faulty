package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.VarInfo.Type;
import mc.*;

/**
 * This class  implements the basics for UNSIGNED integer variables
 * the class SInteger, provides the behavior ofr signed integers,
 * by implementation unsigned integers use less BDD vars, thus are
 * more amenable for symbolic model checking.
 */
public class VarInt implements IntExp, Var{
    String name; // the name of the var
    String instName; // the instance name where the var is located
    ConsIntExp value; 
    BDD[] bits; // it has an array of bits representing the variable value's
    BDD[] bits_; // the value of the array after any change (var prime)
    BDDModel model; // a reference to the resulting model 
    /**
     * A constructor for the class it creates the arrays
     * 
     */
    public VarInt(BDDModel model){
    	this.model = model;
        bits = new BDD[Program.intSize]; // by default integer size is 4
        
        bits_ = new BDD[Program.intSize];
       
        initBDD();
    }
    
    /**
     * A constructor for the class 
     * @param 	name 	the name of the var
     */
    public VarInt(String name, String instName, BDDModel model){
    	this.model = model;
    	this.name = name;
    	this.instName = instName;
        bits = new BDD[Program.intSize]; // by default integer size is 4
       
        bits_ = new BDD[Program.intSize];
       
        initBDD();
    }
    
    /**
     * A constructor for the class 
     * @param 	name 	the name of the var
     * @param 	bits	the initial value of the var
     */
    public VarInt(String name, String instName, BDD[] bits, BDDModel model){
    	this.name = name;
    	this.instName = instName;
    	this.model = model;
        this.bits = new BDD[Program.intSize]; // by default integer size is 4
      
        this.bits_ = new BDD[Program.intSize];
        
        initBDD();
        for (int i = 0; i < Program.intSize; i++){
        	this.bits[i] = bits[i];
        }
        
    }
    
    
    /**
     * Return the name of the variable
     * @return the name of the variable
     */
    public String getName(){
    	return this.name;
    }


    
    /**
     * A constructor for the class
     * @deprecated	Not needed
     */
    public VarInt(int size){
        bits = new BDD[Program.intSize];
        initBDD();
    }
    
    /**
     * A private method that declares the BDD vars needed
     */
    private void initBDD(){
        for (int i = 0; i < Program.intSize; i++){
            bits[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++; 
            model.addVar("Int:"+instName+"."+name+"["+i+"]", Type.BOOL);
           
        }
      
    }
    
    /**
     * Declares the prime values needed
     */
    public void initPrimes(){
    	for (int i = 0; i < Program.intSize; i++){
            bits_[i] = Program.myFactory.ithVar(Program.declaredVars+bits[i].var());
            Program.declaredVars_++;
            model.addVar("Int:"+instName+"."+name+"["+i+"]_", Type.BOOL);
        }
    }
    
    /**
     * Returns the array with the bits represented by BDDs
     */
    public BDD getBit(int i){
        return bits[i];
    }
    
    /**
     * Returns a bit of the var after some change
     */
    public BDD getBit_(int i){
    	return bits_[i];
    }
    
    /**
     * Returns the array bits
     */
    public BDD[] getBits(){
    	return bits;
    }
    
    /**
     * Returns a BDD representing the var
     */
    public BDD getBDD(){
        BDD result = Program.myFactory.one();
        for (int i=0; i < Program.intSize; i++){
            result = result.and(bits[i]);
        }
        return result;
    }
    
    /**
     * method that updates the value of the var
     * @param	exp	the new value of the var
     * @return the BDD representing the change
     */
    public BDD updatePrime(Expression exp){
        BDD result = Program.myFactory.one();
        IntExp iexp = (IntExp) exp;
        for (int i = 0; i < Program.intSize; i++){
            result = result.and(bits_[i].biimp(iexp.getBit(i)));
        }
        return result;
    }
    
    /**
     * Returns a list of channels 
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    /**
     * Returns a BDD representing a skip
     */
    public BDD skipBDD(){
    	BDD result = Program.myFactory.one();
    	for(int i=0; i < Program.intSize; i++){
    		result = result.and(bits[i].biimp(bits_[i]));    		
    	}
    	return result;
    	
    }
    
    /**
     *  
     * @param instName
     * @return
     */
    public VarInt duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	// we return a duplicate whose name is the name of the var together the name of the instance
    	if (intMap.get(this) != null){
    		return intMap.get(this); // is the variable is in boolMap then we have a clone
    	}
    	else{
    		// otherwise we clone the var
    		VarInt result = new VarInt(name, instName, model);
    		return result;
    	}
    }
}