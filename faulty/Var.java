package faulty;
import java.util.HashMap;

import net.sf.javabdd.*;

public interface Var{

	public String getName();
    public BDD getBDD();
    public BDD updatePrime(Expression exp);
    public BDD skipBDD();
    public Var duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner);
   
}