package faulty.auxiliar;

import java.util.*;


public class AuxiliarMain extends AuxiliarProgramNode{
	
	LinkedList<AuxiliarProcessDecl> processDecl;
	LinkedList<String> processInvk;
	
	public AuxiliarMain(){
		processDecl = new LinkedList<AuxiliarProcessDecl>();
		processInvk = new LinkedList<String>();
		
	}
	

	public AuxiliarMain(LinkedList<AuxiliarProcessDecl> pDecl,LinkedList<String> pInvk){
		processDecl = pDecl;
		processInvk = pInvk;
		
	}
	
	/**
	 * 
	 * @param name
	 * @return the type of the process with that name, null otherwise.
	 */
	public String getProcessType(String name){
		boolean found =false;
		int i = 0;
		
		
		while ( i < processDecl.size() && !found ){
			
			if (processDecl.get(i).getName().equals(name)){
				found =true;
				return processDecl.get(i).getType();
			}
			i++;
	    }
		
		return null;
		
	} 
	
	public boolean existProcessDecl(String name){
		boolean found =false;
		int i = 0;
		
		while ( i < processDecl.size() && !found ){
			if (processDecl.get(i).getName().equals(name)){
				found =true;
			}
			i++;
	    }
		
		return found;
		
	} 
	
	public boolean isCorrectInvk(){
		boolean correct =true;
		int i = 0;
		
		while ( i < processInvk.size() ){
			
			correct = correct && existProcessDecl(processInvk.get(i));
			i++;
	    }
		
		return correct;
		
	} 
	
	public boolean isCorrectDeclProcess(AuxiliarProcessCollection processCollection){
		boolean correct =true;
		int i = 0;
		
		while ( i < processDecl.size() ){
			
			correct = correct && processCollection.existProcess(processDecl.get(i).getType());
			i++;
	    }
		
		return correct;
		
	}
	
	public LinkedList<String> getProcessInvk(){
		return processInvk;	
	}
	
	
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
	
	
}
