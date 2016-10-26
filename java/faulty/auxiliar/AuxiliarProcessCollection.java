package faulty.auxiliar;


import java.util.*;

public class AuxiliarProcessCollection extends AuxiliarProgramNode {
	
	
	LinkedList<AuxiliarProcess> processList;
	int totalIntVars;
	int totalBoolVars;
    int totalEnumVars;
	
	public AuxiliarProcessCollection(){
		
		processList = new LinkedList<AuxiliarProcess>();
		totalIntVars=0;
		totalBoolVars = 0;
        totalEnumVars=0;
	
	}
	
	
	 /**
     * Adds a  process to the list
     * @param	proc 	the new proc
     */
    public void addProcess(AuxiliarProcess proc){
    	processList.add(proc);
    	totalIntVars += proc.getNumVarInt();
    	totalBoolVars += proc.getNumVarBool();
        totalEnumVars += proc.getNumVarEnumerated();
    }
    
    public int getTotalIntVars(){
    	return totalIntVars;
    }
    
    public int getTotalBoolVars(){
    	return totalBoolVars;
    }
    
    public int getTotalEnumVars(){
    	return totalEnumVars;
    }
    
    
    public LinkedList<AuxiliarProcess> getProcessList(){
    	return processList;
    }
    
    public boolean existProcess(String name){
		boolean found =false;
		int i = 0;
		
		while ( i < processList.size() && !found ){
			if (processList.get(i).equals(name)){
				found =true;
			}
			i++;
	    }
		
		return found;
		
	} 
    
    
    public AuxiliarProcess getProcess(String name){
		boolean found =false;
		AuxiliarProcess proc = null;
		
		int i = 0;
		
		//System.out.println("  ++++++++++++++++++++++++++++++++++++++++++++++"  );
		
		while ( i < processList.size() && !found ){
			//System.out.println("  -- process Name :" + processList.get(i).getName() + " - process searched :" + name );
			
			if (processList.get(i).getName().equals(name)){
				proc = processList.get(i);
				found =true;
			}
			i++;
	    }
		//System.out.println("  -- process Name returned :" + proc.getName()  );
		//System.out.println("  ++++++++++++++++++++++++++++++++++++++++++++++"  );
		
		return proc;
		
	} 
    
    
    
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}

}
