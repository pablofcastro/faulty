package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.VarInfo.Type;
import mc.*;

public class VarEnum implements EnumExp, Var{
	String name; // the name of the var
	String instName; // the instance name where the var is located
	String enumType; // the enumerable type corresponding to this var
	EnumType myType;
	int size; // the size of this enum
	Program myProgram; // the to which the var belongs to 
	ConsIntExp value;
	BDD[] bits; // it has an array of bits representing the variable value's
	BDD[] bits_; // the value of the array after any change (var prime)
	BDDModel model; // a reference to the resulting model 
	LinkedList<Integer> ids; // a list of the ids for the variables appearing here
	LinkedList<Integer> ids_; // a list of the ids for the variables appearing here
	boolean initialisedPrime;
	
	 /**
     * A constructor for the class it creates the arrays
     * 
     */
    public VarEnum(BDDModel model, Program myProgram, String enumType){
    	this.model = model;
    	this.enumType = enumType;
    	this.myType = myProgram.getEnum(enumType);
    	this.size = myProgram.getEnum(enumType).getSize();   	 	
    	this.myProgram = myProgram;
    	
        bits = new BDD[myType.getBitsNumber()]; // by default integer size is 4
        bits_ = new BDD[myType.getBitsNumber()];
        ids = new LinkedList<Integer>();
        ids_ = new LinkedList<Integer>();
      
        		
        initialisedPrime = false;  
        initBDD();
        
    }
    
    /**
     * A constructor for the class 
     * @param 	name 	the name of the var
     */
    public VarEnum(String name, String instName, BDDModel model, Program myProgram, String enumType){
        
    	this.model = model;
    	this.name = name;
    	this.instName = instName;
    	this.size = myProgram.getEnum(enumType).getSize();    	
        this.myProgram = myProgram;
    	this.enumType = enumType;
    	this.myType = myProgram.getEnum(enumType);    	
        bits = new BDD[myType.getBitsNumber()];         
        bits_ = new BDD[myType.getBitsNumber()];
        ids = new LinkedList<Integer>();
        ids_ = new LinkedList<Integer>();
        initialisedPrime = false;
        
        initBDD();
        
    }
    
    /**
     * A constructor for the class 
     * @param 	name 	the name of the var
     * @param 	bits	the initial value of the var
     */
    public VarEnum(String name, String instName, BDD[] bits, BDDModel model, Program myProgram, String enumType){
    	this.name = name;
    	this.instName = instName;
    	this.model = model;
    	this.enumType = enumType;
    	this.size = myProgram.getEnum(enumType).getSize();
    	this.myType = myProgram.getEnum(enumType);
    	this.myProgram = myProgram;
    	
        this.bits = new BDD[myType.getBitsNumber()]; // by default integer size is 4
        this.bits_ = new BDD[myType.getBitsNumber()];
        ids = new LinkedList<Integer>();
        ids = new LinkedList<Integer>();
        initialisedPrime = false;
        
        initBDD();
        for (int i = 0; i < myType.getBitsNumber(); i++){
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
     * Return the name of the variable
     * @return the name of the variable "<instanceName>.<varName>"
     */
    public String getCompleteName(){
    	return (new String(this.instName+"."+this.name));
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
     * @return	the enumType of the var
     */
    public EnumType getEnumType(){
    	return this.myType;
    }
    
    
    /**
     * A private method that declares the BDD vars needed
     */
    private void initBDD(){   
    	
        for (int i = 0; i < myType.getBitsNumber(); i++){     	
            bits[i] = Program.myFactory.ithVar(Program.declaredVars);           
            ids.add(new Integer(Program.declaredVars));
            Program.declaredVars++;
            model.addVar("Enum:"+instName+"."+name+"["+i+"]", Type.BOOL);
        }
    }
    
    /**
     * Declares the prime values needed
     */
    public void initPrimes(){
        
       if (!initialisedPrime){
    		initialisedPrime = true;
    		for (int i = 0; i < myType.getBitsNumber(); i++){    			
    			bits_[i] = Program.myFactory.ithVar(Program.declaredVars+bits[i].var());   			
    			ids_.add(new Integer(Program.declaredVars+bits[i].var()));
    			Program.declaredVars_++;
            	model.addVar("Enum:"+instName+"."+name+"["+i+"]_", Type.BOOL);
    		    
            }    		
    	}
    }
    
    public int getBitsSize(){
    
        return myType.getBitsNumber();
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
        for (int i=0; i < myType.getBitsNumber(); i++){
            result = result.and(bits[i]);
        }
        return result;
    }
    
    public void addEnumValue(int indexValue){
    
        this.value = new ConsIntExp(indexValue);
    }
    
    /**
     * 
     * @return	the id's of the variables
     */
    public LinkedList<Integer> getIds(){
    	return ids;
    }
    
    public LinkedList<Integer> getPrimedIds(){
    	return ids_;
    }
    
    
    /**
     * method that updates the value of the var
     * @param	exp	the new value of the var
     * @return the BDD representing the change
     */
    public BDD updatePrime(Expression exp){
        BDD result = Program.myFactory.one();
        EnumExp param = (EnumExp) exp;        
        for (int i = 0; i < myType.getBitsNumber(); i++){	
            result = result.and(bits_[i].biimp(param.getBit(i)));
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
    	for(int i=0; i < myType.getBitsNumber(); i++){
    		result = result.and(bits[i].biimp(bits_[i]));    		
    	}
    	return result;
    	
    }
    
    /**
     * This duplicate must be not used for enum types 
     * @param instName
     * @return
     */
    public VarEnum duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	// we return a duplicate whose name is the name of the var together the name of the instance
    	return null;
    }
    
    /**
     * It creates a duplicate of the current var
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public VarEnum duplicate(String instName, HashMap<VarBool, VarBool>  boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	
    	return this.duplicate(instName, boolMap, intMap, owner);
    	
    }
    
    /**
     * This is the correct duplicate for enum types
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public VarEnum duplicate(String instName, HashMap<Var, Var>  dups, Process owner){
    	
    	if (dups.get(this) != null){
    		return (VarEnum)dups.get(this); // is the variable is in boolMap then we have a clone
    	}
    	else{
    		if (!owner.getGlobalEnumVars().contains(this)){ // if not global
    			//  we clone the var
    			VarEnum result = new VarEnum(name, instName, model, myProgram, enumType);
    			return result;
    		}
    		else{   			
    			return this;
    		}	
    	}
    	
    }
    
    
    /**
     * 
     * @return says whether it is a reference
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
	
    	String varInfo = new String("VarEnum:  ");
    	varInfo = varInfo.concat("name: "+this.name + " - ");
    	varInfo = varInfo.concat("instName: "+this.instName+ " - ");
        varInfo = varInfo.concat("EnumType: "+ this.enumType+ " -");
    	varInfo = varInfo.concat("completeName: " +this.instName + "."+ this.name+" - ");
    	if(this.value!=null){
    	    varInfo = varInfo.concat("value: " +this.value.toString() + "\n\n");
    	}else{
    		varInfo = varInfo.concat("value: UNDEFINED \n\n");
    	}
    	
    	return varInfo;
    }
	
	
	
	
	
	
	
}
