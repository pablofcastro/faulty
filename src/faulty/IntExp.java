package faulty;
import net.sf.javabdd.*;

/**
 * An interface of Integer Expressions
 * @author Pablo
 *
 */
public interface IntExp extends Expression{

/**
 * @param	i	the ith bit to be returned
 * @return the ith bit
 */
public BDD getBit(int i);
 
/**
 * Returns the integer of the expression
 * @return the bits of the integers in an array
 */
public BDD[] getBits();

}