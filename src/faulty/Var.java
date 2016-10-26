package faulty;
import java.util.HashMap;
import java.util.*;

import net.sf.javabdd.*;

public interface Var{

	public String getName();
    public BDD getBDD();
    public BDD updatePrime(Expression exp);
    public String getType();
    public BDD skipBDD();
    public Var duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner);    
    public Var duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, HashMap<ParamBool, ParamBool> boolPars, HashMap<ParamInt, ParamInt> intPars, Process owner);
    public Var duplicate(String instName, HashMap<Var, Var> dups, Process owner);
    
    // returns the list of ids corresponding to the variable
    public LinkedList<Integer> getIds();
    public LinkedList<Integer> getPrimedIds();
    
    // says if it is a reference
    public boolean isReference();
    
}