package faulty.auxiliar;

public class AuxiliarChanAccess extends AuxiliarExpression{
	
	AuxiliarChannel channel;
	
	public AuxiliarChanAccess(String name){
		
		this.channel=new AuxiliarChannel(name,false);
	}
	
   public  AuxiliarChannel getChannel(){
		
		return this.channel;
	}

   public  void setChannel(AuxiliarChannel ch){
		
		this.channel = ch;
	}
	
	@Override
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}

}
