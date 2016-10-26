package faulty;
import net.sf.javabdd.*;
import java.util.*;

// BoolExp it is an interface for Boolean Expression
public interface BoolExp extends Expression{

	/**
	 * @return the BDD representing the BDD
	 */
    public BDD getBDD();
    
    /**
     * @return the channels in hte expression
     */
    public LinkedList<Channel> getChannels();
    
    /**
     * @return Duplicates the expression
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process  owner);
   
    
    /**
     * @return Duplicates the expression taking into account parameters
     */
    public Expression duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process  owner);
   
    /**
     * 
     * @param instName
     * @param dups
     * @param owner
     * @return
     */
    public Expression duplicate(String instName, HashMap<Var, Var> dups, Process  owner);
    
    /**
     * 
     * @return the vars in expression
     */
    public LinkedList<Var> getVars();
}