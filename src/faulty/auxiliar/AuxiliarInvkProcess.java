package faulty.auxiliar;

import java.util.*;

	public class AuxiliarInvkProcess extends AuxiliarProgramNode {

		String nameProcess;
		LinkedList<AuxiliarExpression> invkValues;
		
		public AuxiliarInvkProcess(String n, LinkedList<AuxiliarExpression> invkV){
			this.nameProcess = n;
			this.invkValues = invkV;
		}
		
        public AuxiliarInvkProcess(String n){
			this.nameProcess = n;
			this.invkValues = new LinkedList<AuxiliarExpression>();
		}
        
		public LinkedList<AuxiliarExpression> getInvkValues(){
			return invkValues;
		}
		
		
	    public String getInstanceName(){
			return nameProcess;
		}
	    
	    
		public void accept(AuxiliarFaultyVisitor v){
		     v.visit(this);			
		}
		
	}
	

