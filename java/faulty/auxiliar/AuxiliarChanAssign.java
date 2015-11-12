package faulty.auxiliar;

public class AuxiliarChanAssign extends AuxiliarCode{
	
	
    AuxiliarChannel chanName; //  channel name
    AuxiliarExpression exp; 
    
    
    /**
     * Basic constructor.
     * @param	channel 	the channel in which the value is inserted
     * @param	exp			the expression whose value is put into de channel
     */
    public AuxiliarChanAssign(String chanName, AuxiliarExpression exp){
    	this.chanName = new AuxiliarChannel(chanName);
    	this.exp = exp;
    	
    }
    

    @Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
}
