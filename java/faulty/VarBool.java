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
    int id; // the id of this bool
    int id_; // the id of the primed version 
    BDD skip; // just a BDD representing a skip, used for efficiency reasons
    boolean initialisedPrime; // a variable to keep track whether the primed has been initialised
    
    // Constructor for the class
    public VarBool(String name, String instName, BDDModel model){
    	this.name = name;
    	this.instName = instName;
    	this.model = model;
    	
    	// a BDD value is created for the variables
        value = Program.myFactory.ithVar(Program.declaredVars);   // It obtain a new bit for the value of the variable.
        this.id = Program.declaredVars;
        Program.declaredVars++;
        model.addVar(instName+"."+name, Type.BOOL);
        initialisedPrime = false;
      
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
     * 
     * @return	the type of the var
     */
    public String getType(){
    	return "bool";
    }
    
    /**
     * Builds the prime var, this has to be after the creation of all the variables in the program
     */
    public void initPrimes(){
    	 if (!initialisedPrime){
    		value_ = Program.myFactory.ithVar(Program.declaredVars+value.var()); // it obtains a second bit
    	 	id_ = Program.declaredVars+value.var();   	 	
    	 	Program.declaredVars_++;
    	 	model.addVar(instName+"."+name+"_", Type.BOOL);
    	 	initialisedPrime = true;
    	 }
    }
    
    
    public BDD getBDD(){
        return value;
    }
    
    public BDD getBDD_(){
    	return value_;
    }
    
    /**
     * @return	the list of id corresponding to this var
     */
    public LinkedList<Integer> getIds(){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	result.add(new Integer(this.id));
    	return result;
    }
    
    /**
     * 
     * @return the list of ids corresponding to the primed var
     */
    public LinkedList<Integer> getPrimedIds(){
    	LinkedList<Integer> result = new LinkedList<Integer>();
    	result.add(new Integer(this.id_));
    	return result;
    }
    
    /** It returns the value corresponding to the primed variables
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
    
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	result.add(this);
    	return result;
    }
    
    public BDD skipBDD(){
    	if (skip == null){
    		skip = value.biimp(value_);
    	}
    	return skip;
    }
 
    /**
     * 
     * @param instName	the name of the instance to which the var will be added (only for local variables)
     * @return	a duplicated var
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
    			System.out.println(this.getName());
    			return this;
    		}
    	}
    }
    
    /**
     * It creates a clone of the current var
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return A duplicate of the current variable
     */
    public VarBool duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	// since it is not a param, it only calls to the other version of duplicate
    	return this.duplicate(instName, boolMap, intMap, owner);
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public VarBool duplicate(String instName, HashMap<Var, Var>  dups, Process owner){
    	if (dups.get(this) != null){   		
    		return (VarBool) dups.get(this); // is the variable is in boolMap then we have a clone
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
   
    /**
     * 
     * @return	says whether is reference
     */
    public boolean isReference(){
    	return false;
    }
    
    @Override
    
	public String toString(){
	
    	String varInfo = new String(this.instName + "."+ this.name+" ");
    	return varInfo;
    }
    
    
	public String toStringComplete(){
	
    	String varInfo = new String("VarBool:   ");
    	varInfo = varInfo.concat("name: "+this.name + " - ");
    	varInfo = varInfo.concat("instName: "+this.instName+ " - ");
    	varInfo = varInfo.concat("completeName: " +this.instName + "."+ this.name+"\n");
    	return varInfo;
    }
	

    
}
 
