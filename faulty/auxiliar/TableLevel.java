package faulty.auxiliar;

import java.util.*;

public class TableLevel {
	
	private LinkedList<AuxiliarChannel> chanList;
	private LinkedList<AuxiliarVar> varList;
	private LinkedList<AuxiliarProcess> processList;
	
	public TableLevel(){
		
		chanList= new LinkedList<AuxiliarChannel>();
		varList = new  LinkedList<AuxiliarVar>();
		processList = new LinkedList<AuxiliarProcess>();
		
	}
	
	
	public boolean addChannel(AuxiliarChannel ch){
		boolean result = false;
		if (!existChannel(ch.getName())){
			result =true;
			chanList.add(ch);
		}
		return result;
	}
	
	public boolean addVar(AuxiliarVar var){
		boolean result = false;
		if (!existVar(var.getName())){
			result =true;
			varList.add(var);
		}
		return result;
	}
	
	public boolean addProcess(AuxiliarProcess proc){
		boolean result = false;
		if (!existProcess(proc.getName())){
			result =true;
			processList.add(proc);
		}
		return result;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public AuxiliarChannel getChannel(String name){
		for(int i=0; i<chanList.size();i++){
			if (chanList.get(i).getName().equals(name)){
				return chanList.get(i);
			}
		}
		
		return null;
	}
	
    public AuxiliarVar getVar(String name){
		for(int i=0; i<varList.size();i++){
			if (varList.get(i).getName().equals(name)){
				return varList.get(i);
			}
		}
		return null;
	}

    public AuxiliarProcess getProcess(String name){
		for(int i=0; i<processList.size();i++){
			if (processList.get(i).getName().equals(name)){
				return processList.get(i);
			}
		}
		return null;
	}
    
    /**
     * 
     * @return Return a list with all names of boolean variables in the current level.
     */
    public LinkedList<String> getBoolVarNames(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	// Obtain all names of the global variables of type bool. 
        for(int i=0; i<varList.size();i++){
        	if(varList.get(i).getType().isBOOLEAN()){
			    varNames.add(varList.get(i).getName());
			}
		}
        return varNames;
	}
    
    /**
     * 
     * @return Return a list with all names of integer variables in the current level.
     */
    public LinkedList<String> getIntVarNames(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	// Obtain all names of the global variables of type bool. 
        for(int i=0; i<varList.size();i++){
        	if(varList.get(i).getType().isINT()){
			    varNames.add(varList.get(i).getName());
			}
		}
        return varNames;
	}

    /**
     * 
     * @return Return a list with all names of the boolean variables the process current level.
     */
    public LinkedList<String> getBoolVarNamesProcesses(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	AuxiliarProcess proc =null;
    	// Obtain all names of the variables according the instances names 
    	// of the process ( "Process_instanceName" + "." + "VariableName")
        for(int i=0; i<processList.size();i++){
        	proc =processList.get(i);
        	if (proc !=null){
        		LinkedList<String> processVars = proc.getBoolVarNamesProcessIntances();
        		for(int j=0; j<processVars.size();j++){
        			varNames.add(processVars.get(j)); 
        		}
        	}
			
		}
        return varNames;
	}
    
    
    /**
     * 
     * @return Return a list with all names of the integer variables the process current level.
     */
    public LinkedList<String> getIntVarNamesProcesses(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	AuxiliarProcess proc =null;
    	// Obtain all names of the variables according the instances names 
    	// of the process ( "Process_instanceName" + "." + "VariableName")
        for(int i=0; i<processList.size();i++){
        	proc =processList.get(i);
        	if (proc !=null){
        		LinkedList<String> processVars = proc.getIntVarNamesProcessIntances();
        		for(int j=0; j<processVars.size();j++){
        			varNames.add(processVars.get(j)); 
        		}
        	}
			
		}
        return varNames;
	}
    
	public boolean existChannel(String chName){
		for(int i=0;i<chanList.size();i++){
			if(chanList.get(i).getName().equals(chName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean existVar(String varName){
		for(int i=0;i<varList.size();i++){
			if(varList.get(i).getName().equals(varName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean existProcess(String procName){
		for(int i=0;i<processList.size();i++){
			if(processList.get(i).getName().equals(procName)){
				return true;
			}
		}
		return false;
	}
	
	public boolean existNameLevel(String name){
		
		if (!existChannel(name) && !existVar(name) && !existProcess(name)) {
			return false;
		}
		return true;
	}
	
	public void deleteLevel(){
		
		
	}



}
