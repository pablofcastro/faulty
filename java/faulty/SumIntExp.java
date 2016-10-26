package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.*;
import mc.VarInfo.Type;

/**
 * Provides the basic behavior of an expression
 * adding two integers
*/
public class SumIntExp implements IntExp{
    IntExp exp1; // the left integer
    IntExp exp2; // the right integer
    BDD[] bits; // bits for saving the result of the sum
    BDD[] carry;  // one bit for the carry
    BDDModel model;
    
    /**
     * basic constructor of the class
     * @param	exp1	the left integer
     * @param	exp2	the right integer
     */
    public SumIntExp(IntExp exp1, IntExp exp2, BDDModel model){
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.model = model;
        bits = new BDD[Program.intSize];
        carry = new BDD[Program.intSize];
        for (int i = 0; i < Program.intSize; i++){
            bits[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("SumIntExp"+Program.declaredVars, Type.BOOL);
        }
        for (int i = 0; i < Program.intSize; i++){
            carry[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("Carry_SumIntExp"+Program.declaredVars, Type.BOOL);
        }
        calculateSum();
    }
    
    /**
     * Calculates the sum of two integers, the less significant bit is the last i in bit[i]
     */
    public void calculateSum(){
        
        // first, we calculate the carry
        for (int i = Program.intSize - 1; i >= 0; i--){
                carry[i] = exp1.getBit(i).and(exp2.getBit(i));
        }
        // now the expression that calculates the sum
        for (int i = Program.intSize - 1; i >= 0; i--){
            if (i == Program.intSize - 1){
                bits[i] = exp1.getBit(i).xor(exp2.getBit(i));
            }
            else{
                bits[i] = exp1.getBit(i).xor(exp2.getBit(i)).xor(carry[i+1]);
            }
        }
    }
    
    /**
     * Returns the ith bit of the integer resulting from the sum
     * @param 	the i
     */
    public BDD getBit(int i){       
       return bits[i];
    }
    
    /**
     * Returns a list of the channels occurring in the sum
     * @return	the list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp1.getChannels());
    	result.addAll(exp2.getChannels());
    	return result;
    }
    
    /**
     * Returns the representation of the result in an array of bits (BDD)
     */
    public BDD[] getBits(){
    	return bits;
    }
    
    /**
     * Return a BDD representing the sum
     * @return	the BDD representing the sum
     */
    public BDD getBDD(){
    	BDD result = Program.myFactory.one();
    	for (int i  = 0; i < Program.intSize; i++){
    		result = result.and(bits[i]);
    	}
    	return result;
    }
    
    /**
     * 
     * @param instName	the name of the instance 
     * @return	a reference to a duplicate of the current expression
     */
    public SumIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	SumIntExp result = new SumIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, owner), model);
    	return result;
    }
    
    /**
     * 
     * @param instName
     * @param boolMap
     * @param intMap
     * @param pbools
     * @param pints
     * @param owner
     * @return
     */
    public SumIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> pbools, HashMap<ParamInt, ParamInt> pints, Process owner){
    	SumIntExp result = new SumIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, pbools, pints, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, pbools, pints, owner), model);
    	return result;
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public SumIntExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	SumIntExp result = new SumIntExp((IntExp) exp1.duplicate(instName, dups, owner), (IntExp) exp2.duplicate(instName, dups, owner), model);
    	return result;
    }
    
    
   
    @Override
    public String toString(){
        
        String multInfo = new String(" ");
    	
    	String  exp1String= exp1.toString(); 
    	String  exp2String= exp2.toString(); 
    	
    	multInfo= multInfo.concat(exp1String).concat(" + ").concat(exp2String);
        
     	return multInfo;
    }

    
}