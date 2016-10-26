package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.*;

/**
 * Class representing the multiplication of two UNSIGNED integers: int1 * int2 
 * TO BE IMPLEMENTED, DO NOT USE
 */
public class MultIntExp implements IntExp{
    IntExp exp1; // the left int
    IntExp exp2; // the right int
    BDD[] bits;  // the bits of the result
    BDD[][] shifted; // we use an array bidimensional to keep track of the shifted numbers BDD[i][j] jth bits of ith number
    IntExp multResult; // we calculate the multiplication as a serie of sums 
    BDDModel model;
    
    /**
     * Basic constructor of the class
     * @param	exp1	the left integer
     * @param	exp2	the right integer 
     */
    public MultIntExp(IntExp exp1, IntExp exp2, BDDModel model){
    	this.exp1 = exp1;
    	this.exp2 = exp2;
    	this.model = model;
    	bits = new BDD[Program.intSize];
    	shifted = new BDD[Program.intSize][Program.intSize];
    	// request vars for the bits of the expression
    	for (int i = 0; i < Program.intSize; i++){
            bits[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
        }
    	calculateMult();
    }
    
    /**
     * Private method to calculate the multiplication
     * it calculates the multiplication using a series of sums
     * NOTE: improve this naif algorithm we something better, in
     * space and time.
     */
    private void calculateMult(){
    	// To calculate the multiplication, recall that in the case of more digits than those supported
    	// we will obtain an overflow so an erronous result.
   	
    	// first we calculate the shifted numbers
    	
    	
    	for (int i = 0; i < Program.intSize; i++){
    		for (int j = Program.intSize - 1; j > i; j--){
    			shifted[i][j] = Program.myFactory.zero();
    		}	
    		for (int j = i; j >= 0; j--){
    			shifted[i][j] = bits[j];
    		}	
    	}
    	// if the integers have more than one bit then we use succesive sums
    	if (Program.intSize > 1){
    		
    		
    		multResult = new SumIntExp(new ConsIntExp(shifted[0]), new ConsIntExp(shifted[1]), model);
    		
    		SumIntExp old_sum = (SumIntExp) multResult;
    		for (int i = 2; i < Program.intSize; i++){
    			multResult = new SumIntExp(old_sum, new ConsIntExp(shifted[i]), model);
    			old_sum = (SumIntExp) multResult;
    		}
    	}
    	else{ // otherwise the result is only a boolean and
    		BDD[] oneBit = new BDD[1];
    		oneBit[0] = exp1.getBits()[0].and(exp2.getBits()[0]);
    		multResult = new ConsIntExp(oneBit);
    	}
    }
    
    /**
     * Returns the i bit of the integer
     * @return the ith bit
     */
    public BDD getBit(int i){
        return multResult.getBit(i);
    }

    /**
     * Returns the list of the channel occurring in the expression
     * @return a list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp1.getChannels());
    	result.addAll(exp2.getChannels());
    	return result;
    }
    
    /**
     * Returns the BDD representing the resulting expression
     * @return a BDD
     */
    public BDD getBDD(){
    	return multResult.getBDD();
    }
    
    /**
     * Returns the binary representation of the expression
     */
    public BDD[] getBits(){
    	return multResult.getBits();
    }
    
    /**
     * It duplicates the expression
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public MultIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	MultIntExp result = new MultIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, owner), model);
    	return result;
    	
    }
    
    /**
     * Another duplicate to take into account process parameters
     * @param instName
     * @param boolMap
     * @param intMap
     * @param boolPars
     * @param intPars
     * @param owner
     * @return
     */
    public MultIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	MultIntExp result = new MultIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, boolPars, intPars,  owner), model);
    	return result;
    	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public MultIntExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	MultIntExp result = new MultIntExp((IntExp) exp1.duplicate(instName, dups, owner), (IntExp) exp2.duplicate(instName, dups,  owner), model);
    	return result;
    	
    }
    
    @Override
    public String toString(){
        
        String multInfo = new String(" ");
    	
    	String  exp1String= exp1.toString(); 
    	String  exp2String= exp2.toString(); 
    	
    	multInfo= multInfo.concat(exp1String).concat(" * ").concat(exp2String);
        
     	return multInfo;
    }
    
}// end class
