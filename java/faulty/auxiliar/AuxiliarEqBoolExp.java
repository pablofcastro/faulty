package faulty.auxiliar;




/**
 * A class that implements the boolean expresion int1 = int2
 */

public class AuxiliarEqBoolExp extends AuxiliarExpression{

AuxiliarExpression int1; // the left int
AuxiliarExpression int2; // the right int
boolean createBiimp; // is true in case of comparates two boolean expression, 
                     // indicates to the buildervisitor if is necesary to creates a biimp for represent its.
/**
 * Constructor, it takes the two integers composing hte equality
 * @param	int1	the left integer
 * @param	int2	the right integer
 */
public AuxiliarEqBoolExp(AuxiliarExpression int1, AuxiliarExpression int2){
	super();
    this.int1 = int1;
    this.int2 = int2;
    createBiimp = false;
}

public void setCreateBiimp(boolean value){
	this.createBiimp = value;
}

public boolean getCreateBiimp(){
	return this.createBiimp;
}

@Override
public void accept(AuxiliarFaultyVisitor v){
     v.visit(this);			
}

}