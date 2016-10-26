package faulty;
import net.sf.javabdd.*;

import java.util.*;

import mc.*;
import mc.VarInfo.Type;



/**
 * Class representing  the subtraction of two integers: exp1 - exp2
 * The result is stored in an array of bits (BDD)
 * Note that this operation in for UNSIGNED Ints,then no negative numbers are used
 * We use a complement method to calculate the subtraction, using the implemented
 * sum, more efficient (in space) methods need to be implemented
 */
public class NegIntExp implements IntExp{
    IntExp exp1; // the first integer
    IntExp exp2; // the second integer
    BDD[] bits; // the result
    BDDModel model;

    
    /**
     * Basic constructor of the class
     * @param exp1	the left int 
     * @param exp2	the right int
     */
    public NegIntExp(IntExp exp1, IntExp exp2, BDDModel model){
    	this.exp1 = exp1;
    	this.exp2 = exp2;
    	BDD[] bits = new BDD[Program.intSize];
    	this.model = model;
    	for (int i = 0; i < Program.intSize; i++){
            bits[i] = Program.myFactory.ithVar(Program.declaredVars);
            Program.declaredVars++;
            model.addVar("NegIntExp"+Program.declaredVars, Type.BOOL);
        }
    	calculateSub();
    }
    
    /**
     * Private method to calculate the subtraction, we use constants and
     * the implemented sum. An inplace method will be more efficient in terms
     * of space.
     */
    private void calculateSub(){
    	BDD[] bits1 = exp1.getBits();
    	BDD[] bits2 = exp2.getBits();
    	BDD[] complement = getComplement(bits2);
    	ConsIntExp exp2Comp = new ConsIntExp(complement);
    	ConsIntExp one = new ConsIntExp(1);
    	SumIntExp twoComplement = new SumIntExp(exp2Comp, one, model);
    	SumIntExp subtraction = new SumIntExp(exp1, twoComplement, model);
    	bits = subtraction.getBits();
    }
    
    /**
     * Private method to calculate the complement
     */
    private BDD[] getComplement(BDD[] bits){
    	BDD[] result = new BDD[Program.intSize];
    	for (int i = 0; i < Program.intSize; i++){
    		result[i] = bits[i].not();
    	}
    	return result;
    }
    
    /**
     * Returns a BDD representation of the result of the operation
     */
    public BDD getBDD(){
    	BDD result = Program.myFactory.one();
    	for (int i  = 0; i < Program.intSize; i++){
    		result = result.and(bits[i]);
    	}
    	return result;
    }
    
    /**
     * Returns the ith bit of the operation
     */
    public BDD getBit(int i){
        return bits[i];
    }
    
    /**
     * Returns the bits reprsenting the number
     * 
     */
    public BDD[] getBits(){
    	return bits;
    }
    
    /**
     * Returns a list of the channels involved in the operation
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	result.addAll(exp1.getChannels());
    	result.addAll(exp2.getChannels());
    	return result;
    }
    
    /**
     * 
     * @param instName
     * @param boolMap
     * @param intMap
     * @param owner
     * @return
     */
    public NegIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	NegIntExp result = new NegIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, owner), model);
    	return result;
    	
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
    public NegIntExp duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner){
    	NegIntExp result = new NegIntExp((IntExp) exp1.duplicate(instName, boolMap, intMap, boolPars, intPars,  owner), (IntExp) exp2.duplicate(instName, boolMap, intMap, boolPars, intPars, owner), model);
    	return result;
    	
    }
    
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public NegIntExp duplicate(String instName, HashMap<Var, Var> dups, Process owner){
    	NegIntExp result = new NegIntExp((IntExp) exp1.duplicate(instName, dups,  owner), (IntExp) exp2.duplicate(instName, dups, owner), model);
    	return result;
    	
    }
    
    @Override
    public String toString(){
        
        String multInfo = new String(" ");
    	
    	String  exp1String= exp1.toString(); 
    	String  exp2String= exp2.toString(); 
    	
    	multInfo= multInfo.concat(exp1String).concat(" - ").concat(exp2String);
        
     	return multInfo;
    }

}
