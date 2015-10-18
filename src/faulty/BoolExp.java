package faulty;
import net.sf.javabdd.*;
import java.util.*;

// BoolExp it is an interface for Boolean Expression
public interface BoolExp extends Expression{

    public BDD getBDD();
    public LinkedList<Channel> getChannels();
}