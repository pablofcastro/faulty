package faulty;
import net.sf.javabdd.*;

public interface Var{

    public BDD getBDD();
    public BDD updatePrime(Expression exp);
    public BDD skipBDD();
    
}