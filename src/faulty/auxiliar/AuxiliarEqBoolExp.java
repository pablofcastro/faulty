package faulty.auxiliar;




/**
 * A class that implements the boolean expresion int1 = int2
 */

public class AuxiliarEqBoolExp extends AuxiliarExpression{

AuxiliarExpression int1; // the left int
AuxiliarExpression int2; // the right int
boolean createBiimp; // is true in case of comparates two boolean expression,
                     // indicates to the buildervisitor if is necesary to creates a biimp for represent its.
    
boolean isEnumerated; // is true in case of comparates two enumerated expression,
String enumType; // Name of the enumerated type
    
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
    isEnumerated = false;
    enumType = null;
}

public void setCreateBiimp(boolean value){
	this.createBiimp = value;
}

public void setIsEnumerated(boolean value){
        this.isEnumerated = value;
}
    
    
public boolean getCreateBiimp(){
	return this.createBiimp;
}

public boolean isEnumerated(){
        return this.isEnumerated;
}
    
public void setEnumType(String eName){
    this.enumType = eName;
}
    
public String getEnumType(){
    return this.enumType;
}

    
    
@Override
public void accept(AuxiliarFaultyVisitor v){
     v.visit(this);			
}

}