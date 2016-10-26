package faulty.auxiliar;

import java.util.*;

public class TableLevel {
	
	private LinkedList<AuxiliarChannel> chanList;
	private LinkedList<AuxiliarVar> varList;
    private LinkedList<AuxiliarParam> parList;
    private LinkedList<AuxiliarEnumType> enumList;
 
	private LinkedList<AuxiliarProcess> processList;
	
	public TableLevel(){
		
		chanList= new LinkedList<AuxiliarChannel>();
		varList = new  LinkedList<AuxiliarVar>();
		parList = new  LinkedList<AuxiliarParam>();
        processList = new LinkedList<AuxiliarProcess>();
        enumList = new LinkedList<AuxiliarEnumType>();
		
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
	
    public boolean addParam(AuxiliarParam par){
		boolean result = false;
		if (!existParam(par.getDeclarationName())){
			result =true;
			parList.add(par);
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
	
    
    public boolean addEnumeratedType(AuxiliarEnumType eType){
		boolean result = false;
		if (!existEnumeratedType(eType.getName())){
			result =true; //added
			enumList.add(eType);
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

    
    public AuxiliarParam getParam(String name){
		for(int i=0; i<parList.size();i++){
			if (parList.get(i).getDeclarationName().equals(name)){
				return parList.get(i);
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
    
    
    public AuxiliarEnumType getEnumeratedType(String name){
		for(int i=0; i<enumList.size();i++){
			if (enumList.get(i).getName().equals(name)){
				return enumList.get(i);
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
     * @return Return a list with all names of enumerated variables in the current level.
     */
    public LinkedList<String> getEnumVarNames(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	// Obtain all names of the global variables of type bool.
        for(int i=0; i<varList.size();i++){
        	if(varList.get(i).getType().isEnumerated()){
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
        		LinkedList<String> processVars = proc.getBoolVarNamesProcessInstances();
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
        		LinkedList<String> processVars = proc.getIntVarNamesProcessInstances();
        		for(int j=0; j<processVars.size();j++){
        			varNames.add(processVars.get(j)); 
        		}
        	}
			
		}
        return varNames;
	}
    
	
    /**
     *
     * @return Return a list with all names of the enumerated variables the process current level.
     */
    public LinkedList<String> getEnumVarNamesProcesses(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	AuxiliarProcess proc =null;
    	// Obtain all names of the variables according the instances names
    	// of the process ( "Process_instanceName" + "." + "VariableName")
        for(int i=0; i<processList.size();i++){
        	proc =processList.get(i);
        	if (proc !=null){
        		LinkedList<String> processVars = proc.getEnumVarNamesProcessInstances();
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
	
	
    public boolean existParam(String parName){
		for(int i=0;i<parList.size();i++){
			if(parList.get(i).getDeclarationName().equals(parName)){
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
    
    public boolean existEnumeratedType(String eName){
		for(int i=0;i<enumList.size();i++){
			if(enumList.get(i).getName().equals(eName)){
				return true;
			}
		}
		return false;
	}
	
	
	public boolean existNameLevel(String name){
		
		if (!existChannel(name) && !existVar(name) && !existParam(name) && !existProcess(name)&& !existEnumeratedType(name)) {
			return false;
		}
		return true;
	}
	
	public void deleteLevel(){
		
		
	}



}
