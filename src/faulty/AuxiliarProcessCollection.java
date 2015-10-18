package faulty;
import java.util.*;

public class AuxiliarProcessCollection {
	
	
	LinkedList<AuxiliarProcess> processList;
	int totalIntVars;
	int totalBoolVars;
	
	public AuxiliarProcessCollection(){
		
		processList = new LinkedList<AuxiliarProcess>();
		totalIntVars=0;
		totalBoolVars = 0;
	
	}
	
	
	 /**
     * Adds a  process to the list
     * @param	proc 	the new proc
     */
    public void addProcess(AuxiliarProcess proc){
    	processList.add(proc);
    	totalIntVars += proc.getNumVarInt();
    	totalBoolVars += proc.getNumVarBool();
    }
    
    public int getTotalIntVars(){
    	return totalIntVars;
    }
    
    public int getTotalBoolVars(){
    	return totalBoolVars;
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
		
		while ( i < processList.size() && !found ){
			if (processList.get(i).equals(name)){
				proc = processList.get(i);
				found =true;
			}
			i++;
	    }
		
		return proc;
		
	} 

}
