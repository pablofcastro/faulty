package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.VarInfo.Type;
import mc.*;

// A class for boolean variables
public class VarBool implements BoolExp, Var{
    String name; // A Name for the var
    String instName; // a name of the instance of the variable
    BDD value; // represents the value expressed captured in a BDD
    BDD value_; // represents the value after an assignment
    BDDModel model;
    
    // Constructor for the class
    public VarBool(String name, String instName, BDDModel model){
    	this.name = name;
    	this.instName = instName;
    	this.model = model;
    	
    	// a BDD value is created for the variables
        value = Program.myFactory.ithVar(Program.declaredVars);   // It obtain a new bit for the value of the variable.
        Program.declaredVars++;
        model.addVar(instName+"."+name, Type.BOOL);
      
      // old declaration was wrong  
      // value_ = Program.myFactory.ithVar(Program.declaredVars); // it obtains a second bit
      // Program.declaredVars++;
      //  model.addVar(name+"_", Type.BOOL);
    }
    
    /**
     * Return the name of the variable
     * @return the name of the variable
     */
    public String getName(){
    	return this.name;
    }

    
    /**
     * Builds the prime var, this has to be after the creation of all the variables in the program
     */
    public void initPrimes(){
    	 
    	 value_ = Program.myFactory.ithVar(Program.declaredVars+value.var()); // it obtains a second bit
         Program.declaredVars_++;
         model.addVar(instName+"."+name+"_", Type.BOOL);
         
    }
    
    
    public BDD getBDD(){
        return value;
    }
    
    public BDD getBDD_(){
    	return value_;
    }
    /* It returns the value corresponding to the primed variables
    * when we assign to the variable the value in Exp
    **/
    public BDD updatePrime(Expression exp){
       BoolExp bexp = (BoolExp) exp;
       return value_.biimp(bexp.getBDD());
    	
    }
    
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	return result;
    }
    
    public BDD skipBDD(){
    	BDD result = value.biimp(value_);
    	return result;
    }
 
    /**
     * 
     * @param instName	the name of the instance to which the var will be added (only for local variables)
     * @return	a duplicate of this var
     */
    public VarBool duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	
    	// we return a duplicate whose name is the name of the var together the name of the instance
    	if (boolMap.get(this) != null){
    		return boolMap.get(this); // is the variable is in boolMap then we have a clone
    	}
    	else{
    		if (!owner.getGlobalBoolVars().contains(this)){ // if not global
    			//  we clone the var
    			VarBool result = new VarBool(name, instName, model);
    			return result;
    		}
    		else{
    			return this;
    		}
    	}
    }
}
 
