package faulty;
import net.sf.javabdd.*;
import java.util.*;

// ParamBool is a class for capturing a parameter of a process, 
// parameters are passed by reference
public class ParamBool implements BoolExp, Var{
	String name; // the name of the parameter
	VarBool reference; // the parameters are passed by reference, then it is a reference to a global var
	
	/**
	 * A Basic Constructor for the Class
	 * @param name the name of the parameter
	 * @param reference the reference to which the parameter points to
	 */
	public ParamBool(String name, VarBool reference){
		this.name = name;
		this.reference = reference;
	}
	
	/**
	 * 
	 * @return the name of the parameter
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
	 * 
	 * @return the reference of the parameter
	 */
	public VarBool getReference(){
		return reference;
	}
	
	  /**
     * Builds the prime var, this has to be after the creation of all the variables in the program
     */
    public void initPrimes(){
    	 reference.initPrimes();
    }
    
    /**
     * 
     * @return the BDD representing the parameter
     */
    public BDD getBDD(){
        return reference.getBDD();
    }
    
    /**
     * 
     * @return a BDD representing the primed parameter
     */
    public BDD getBDD_(){
    	return reference.getBDD_();
    }
    
    /* It returns the value corresponding to the primed variables
    * when we assign to the variable the value in Exp
    **/
    public BDD updatePrime(Expression exp){
       BoolExp bexp = (BoolExp) exp;
       return reference.updatePrime(bexp);
    	
    }
    
    public LinkedList<Channel> getChannels(){
    	return reference.getChannels();
    }
    
    public BDD skipBDD(){
    	return reference.skipBDD();
    }
 
    /**
     * 
     * @param instName	the name of the instance  (only for local variables)
     * @return	a duplicate of this var
     */
    public ParamBool duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, Process owner){    	
    	//return reference.duplicate(instName, boolMap, intMap, owner);
        
    	return new ParamBool(this.name, this.reference);
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
    public ParamBool duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){    	
    	
        if (boolPars.get(this) != null){         	
    		return boolPars.get(this); // is the variable is in boolMap then we have a clone
    	}
    	else{
    		return new ParamBool(this.name, this.reference); // otherwise we create one
    	}
    }
    
    
    public ParamBool duplicate(String instName, HashMap<Var, Var>  dups, Process owner){    	
    	
        if (dups.get(this) != null){         	
    		return (ParamBool) dups.get(this); // is the variable is in boolMap then we have a clone
    	}
    	else{
    		return new ParamBool(this.name, this.reference); // otherwise we create one
    	}
    }
    
    
    /**
     * 
     * @return says whether it is a reference
     */
    public boolean isReference(){
    	return true;
    }
    
    /**
     * 
     * @return a string describing the parameter
     */
	public String toString(){
	
    	//String varInfo = new String("Parameter:"+ this.name+" " + this.reference.toString() + "--");
        String varInfo = new String("Parameter:"+ this.name+" ");
    	
    	return varInfo;
    }
    
    /**
     * 
     * @return a complete string description of the parameter
     */
	public String toStringComplete(){
    	String parInfo = new String("Parameter:   ");
    	parInfo = parInfo.concat("name: "+this.name + " - ");
        parInfo = parInfo.concat("reference: "+this.reference.toStringComplete()+ " - ");
    	return parInfo;
    }

   public LinkedList<Var> getVars(){
    	return reference.getVars();   	
    }
	
   
   /**
	 * 
	 * @return	the ids appearing in the expression
	 */
	public LinkedList<Integer> getIds(){
		return reference.getIds();
		
	}
   
	/**
	 * 
	 * @return the ids of the primed variables appearing in the expression
	 */
	public LinkedList<Integer> getPrimedIds(){
		return reference.getPrimedIds();
	}
    
    

	
	
	
}
