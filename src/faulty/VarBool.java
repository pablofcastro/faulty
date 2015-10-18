package faulty;
import net.sf.javabdd.*;
import java.util.*;
import mc.VarInfo.Type;
import mc.*;

// A class for boolean variables
public class VarBool implements BoolExp, Var{
    String name; // A Name for the var
    BDD value; // represents the value expressed captured in a BDD
    BDD value_; // represents the value after an assignment
    BDDModel model;
    
    // Constructor for the class
    public VarBool(String name, BDDModel model){
    	this.name = name;
    	this.model = model;
    	
    	// a BDD value is created for the variables
        value = Program.myFactory.ithVar(Program.declaredVars);   // It obtain a new bit for the value of the variable.
        Program.declaredVars++;
        model.addVar(name, Type.BOOL);
        
      // old declaration was wrong  
      // value_ = Program.myFactory.ithVar(Program.declaredVars); // it obtains a second bit
      // Program.declaredVars++;
      //  model.addVar(name+"_", Type.BOOL);
    }
    
    /**
     * Builds the prime var, this has to be after the creation of all the variables in the program
     */
    public void initPrimes(){
    	 value_ = Program.myFactory.ithVar(Program.declaredVars+value.var()); // it obtains a second bit
         Program.declaredVars_++;
         model.addVar(name+"_", Type.BOOL);
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
     * It returns a reference to a duplicate version of the current var.
     */
    public VarBool duplicate(String Name){
    	VarBool duplicate = new VarBool(name, model);
    	return duplicate;
    }
}
