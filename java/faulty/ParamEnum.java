package faulty;
import net.sf.javabdd.*;
import java.util.*;


public class ParamEnum implements EnumExp, Var {
		String name; // the name of the parameter
		VarEnum reference; // the reference to the actual parameter
		
		/**
		 * @param name	the name of the param
		 * @param reference	the reference bound to the parameter
		 */
		public ParamEnum(String name, VarEnum reference){
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
	    	return "enum";
	    }
		
		/**
		 * 
		 * @return the reference of the parameter
		 */
		public VarEnum getReference(){
			return reference;
		}
		
		  /**
	     * Builds the prime var, this has to be after the creation of all the variables in the program
	     */
	    public void initPrimes(){
	    	 // We assume the prime of the actual parameter was already generated
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
	    //public BDD getBDD_(){
	    //	return reference.getBDD_();
	    //}
	    
	    /* It returns the value corresponding to the primed variables
	    * when we assign to the variable the value in Exp
	    **/
	    public BDD updatePrime(Expression exp){
	       EnumExp eexp = (EnumExp) exp;
	       return reference.updatePrime(eexp);
	    	
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
	    public ParamEnum duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, Process owner){    	
	    	//return reference.duplicate(instName, boolMap, intMap, owner);
	    	return new ParamEnum(this.name, this.reference);
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
	    public ParamEnum duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){    	
	    	// this duplicate must not be used for enum types
	    	
	    	return null;
	    }
	    
	    /**
	     * 
	     * @param instName
	     * @param dups
	     * @param owner
	     * @return
	     */
	    public ParamEnum duplicate(String instName, HashMap<Var, Var>  dups,  Process owner){    	
	   
	        if (dups.get(this) != null){
	    		return (ParamEnum) dups.get(this); // is the variable is in boolMap then we have a clone
	    	}
	    	else{
	    		 return new ParamEnum(this.name, this.reference);
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
		
	    	String varInfo = new String("Parameter:"+ this.name+" " + this.reference.toString() + "--");
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

	    
		/**
		 * @param	i	the ith bit to be returned
		 * @return the ith bit
		 */
		public BDD getBit(int i){
			return reference.getBit(i);
		}
		 
		
		/**
		 * Returns the integer of the expression
		 * @return the bits of the integers in an array
		 */
		public BDD[] getBits(){
			return reference.getBits();
		}
		
		/**
		 * 
		 * @return	the ids appearing in the expression
		 */
		public LinkedList<Integer> getIds(){
			LinkedList<Integer> result = reference.getIds();
			return result;		
		}
	    
		/**
		 * 
		 * @return the ids of the primed variables appearing in the expression
		 */
		public LinkedList<Integer> getPrimedIds(){
			LinkedList<Integer> result = reference.getPrimedIds();
			return result;
		}

		
}// end of class
	
	

