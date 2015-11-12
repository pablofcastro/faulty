package faulty.auxiliar;


import java.util.*;



public class AuxiliarProcess extends AuxiliarProgramNode {

	LinkedList<AuxiliarChannel> channelIds;
	String processName;
	LinkedList<AuxiliarVar> intVars; // it has a collection of local variables of type int
	LinkedList<AuxiliarVar> boolVars; // it has a collections of local variables of type boolean
	LinkedList<AuxiliarBranch> branches; // a collection of branches (Bi -> Ci)
	AuxiliarExpression normCondition; // normative condition for characterising the non-faulty part of the program
	AuxiliarExpression initialCond; // the initial valuations of the local variables.
	LinkedList<String> processInstanceNames; // Names of the differents process Instances
	
	
	public AuxiliarProcess(String name){
		this.processName = name;
		channelIds =new LinkedList<AuxiliarChannel>();
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
		branches = new LinkedList<AuxiliarBranch>();
		normCondition = null;
	    initialCond = null;
	    processInstanceNames= new LinkedList<String>();
		
	}
	
	
	public AuxiliarProcess(String name,AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarVar> varList,LinkedList<AuxiliarBranch> branchList){
		this.processName = name;
		channelIds =new LinkedList<AuxiliarChannel>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
		
		
		 /* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add(varList.get(i));
            }
            else{
            	intVars.add(varList.get(i));
            }
            
        }
		
		
	}
	
	public AuxiliarProcess(AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarVar> varList,LinkedList<AuxiliarBranch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<AuxiliarChannel>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
		
		 /* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add(varList.get(i));
            }
            else{
            	intVars.add(varList.get(i));
            }
            
        }
		
		
	}
	
	public AuxiliarProcess(AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarBranch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<AuxiliarChannel>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
	}
	
	public String getName(){
		
		return processName;
	}
	
	
    public LinkedList<AuxiliarChannel> getChannelIds(){
		
		return channelIds;
	}
    
    public LinkedList<AuxiliarBranch> getBranches(){
		
		return branches;
	}
    
    public LinkedList<AuxiliarVar> getVarInt(){
		
		return intVars;
	}
 
    public LinkedList<AuxiliarVar> getVarBool(){
		
    	
		return boolVars;
	}

    
    public AuxiliarExpression getInitialCond(){
    	return initialCond;
    	
    }
    
    public AuxiliarExpression getNormativeCond(){
    	return normCondition;
    	
    }
    
    /**
     * 
     * @param i ith intance position
     * @return return the name of the ith instance, null if not exist.-
     */
    public String getProcessInstanceName(int i){
    	
    	if ( i < processInstanceNames.size()){
    		return processInstanceNames.get(i);
    	}
    	else{
    		return null;
    	}
    	
    }
    
    public int getNumVar(){
    	
        return ( boolVars.size() + intVars.size());
    }
    
    public int getNumVarInt(){
    	
        return ( intVars.size());
    }
    
    public int getNumVarBool(){
    	
    	int auxVars =0;
    	if (this.channelIds.size()>0){ //Means that the process use at least one channel
    		
    		for(int i=0; i< this.branches.size();i++){
    			auxVars++; //needs 1 boolean variable extra for each branch 
    		}
    		
    	}
    	return ( boolVars.size() + auxVars);
    }
    
    public int getNumBranches(){
    	
        return branches.size();
    }

    public int getNumInstances(){
    	
        return processInstanceNames.size();
    }


    
    public int getNumChannelsUsed(){
	
    return  channelIds.size();
   }
	
    public void addInstanceName(String instanceName){
    	processInstanceNames.add(instanceName);
    }
    
    public void addChannelId(String chId){
    	channelIds.add(new AuxiliarChannel(chId,true));
    	
    }
    
    public void addBranchList(LinkedList<AuxiliarBranch> list){
    	branches = list;
    	
    }
    
    public void setInitialCond(AuxiliarExpression ini){
    	initialCond = ini;
    	
    }
    
    public void setNormativeCond(AuxiliarExpression norm){
    	normCondition = norm;
    	
    }
    
    public void setName(String name){
    	processName = name;
    	
    }
    
    public void setChannelIds(LinkedList<String> chId){
    	LinkedList<AuxiliarChannel> list = new LinkedList<AuxiliarChannel>();
    	for(int i=0;i<chId.size();i++){
    		list.add(new AuxiliarChannel( chId.get(i), true ) );
    	}
    	channelIds = list;
    	
    }
    
    /**
     * Return the list of all names of the boolean variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */
    public LinkedList<String> getBoolVarNamesProcessIntances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
    	   for(int i = 0; i< boolVars.size(); i++){
    		   String nameB = new String (processInstanceNames.get(j) + "." + boolVars.get(i).getName());
    		   varNames.add(nameB);
    	   }
   	    }
    	return varNames;
    }
    
    
    
    /**
     * Return the list of all names of the integer variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */
    public LinkedList<String> getIntVarNamesProcessIntances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
    	   for(int i = 0; i< intVars.size(); i++){
    		   String nameI = new String (processInstanceNames.get(j) + "." + intVars.get(i).getName());
    		   varNames.add(nameI);

    	   }
   	    }
    	return varNames;
    }
    
    
    
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
    
    
}
