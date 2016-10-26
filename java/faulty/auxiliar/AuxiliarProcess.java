package faulty.auxiliar;


import java.util.*;



public class AuxiliarProcess extends AuxiliarProgramNode {

	LinkedList<AuxiliarChannel> channelIds;
    LinkedList<AuxiliarParam> paramList;

	String processName;
	LinkedList<AuxiliarVar> intVars; // it has a collection of local variables of type int
	LinkedList<AuxiliarVar> boolVars; // it has a collections of local variables of type boolean
    LinkedList<AuxiliarVar> enumVars; // it has a collections of local variables of type enumerated
    
	LinkedList<AuxiliarBranch> branches; // a collection of branches (Bi -> Ci)
	AuxiliarExpression normCondition; // normative condition for characterising the non-faulty part of the program
	AuxiliarExpression initialCond; // the initial valuations of the local variables.
	LinkedList<String> processInstanceNames; // Names of the differents process Instances
	LinkedList<AuxiliarInvkProcess> processInvkParameters; // Invocation Parameters of each process instance.
	
	
	public AuxiliarProcess(String name){
		this.processName = name;
		channelIds =new LinkedList<AuxiliarChannel>();
		paramList = new LinkedList<AuxiliarParam>();

        intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		branches = new LinkedList<AuxiliarBranch>();
		normCondition = null;
	    initialCond = null;
	    processInstanceNames= new LinkedList<String>();
        processInvkParameters = new LinkedList<AuxiliarInvkProcess>();
		
	}
	
	
	public AuxiliarProcess(String name,AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarVar> varList,LinkedList<AuxiliarBranch> branchList){
		this.processName = name;
		channelIds =new LinkedList<AuxiliarChannel>();
		paramList = new LinkedList<AuxiliarParam>();
        initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
		processInvkParameters= new LinkedList<AuxiliarInvkProcess>();
		
		 /* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add(varList.get(i));
            }
            else{
                if ( varList.get(i).getType().isINT() ){
                    intVars.add(varList.get(i));
                }else{
                    enumVars.add(varList.get(i));
                }
            }
        }
		
		
	}
	
	public AuxiliarProcess(AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarVar> varList,LinkedList<AuxiliarBranch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<AuxiliarChannel>();
        paramList = new LinkedList<AuxiliarParam>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
        processInvkParameters= new LinkedList<AuxiliarInvkProcess>();
		
		
		/* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add(varList.get(i));
            }
            else{
                if ( varList.get(i).getType().isINT() ){
                    intVars.add(varList.get(i));
                }else{
                    enumVars.add(varList.get(i));
                }
            }
        }
		
		
	}
	
	public AuxiliarProcess(AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarBranch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<AuxiliarChannel>();
        paramList = new LinkedList<AuxiliarParam>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
        processInvkParameters= new LinkedList<AuxiliarInvkProcess>();
		
	}
	
	public String getName(){
		
		return processName;
	}
	
	
    public LinkedList<AuxiliarChannel> getChannelIds(){
		
		return channelIds;
	}
    
    public LinkedList<AuxiliarParam> getParamList(){
		
		return paramList;
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
    
    public LinkedList<AuxiliarVar> getVarEnum(){
        
		return enumVars;
	}


    
    public AuxiliarExpression getInitialCond(){
    	return initialCond;
    	
    }
    
    public AuxiliarExpression getNormativeCond(){
    	return normCondition;
    	
    }
    
    /**
     *
     * @param processName Name of the process.
     * @return return the list with the invocation parameters of the process with that name, an empty list otherwise.-
     */
    
    public LinkedList<AuxiliarExpression> getInvkParametersList(String processName){
        
        
        for(int i=0;i<processInvkParameters.size();i++){
            String pName = processInvkParameters.get(i).getInstanceName();
            
            if(pName.equals(processName)){
                return processInvkParameters.get(i).getInvkValues();
            }
            
        }
        return new LinkedList<AuxiliarExpression>();
        
	}
    
    
    /**
     *
     * @param processName Name of the process.
     * @return return the list with the boolean parameters of the process with that name, an empty list otherwise.-
     */
    
    public LinkedList<AuxiliarExpression> getInvkBoolParamList(String processName){
        
        LinkedList<AuxiliarExpression> boolPar = new LinkedList<AuxiliarExpression>();
        LinkedList<AuxiliarExpression> invkPar = this.getInvkParametersList(processName);
        
        for(int i=0;i<invkPar.size();i++){
            AuxiliarVar var = (AuxiliarVar)invkPar.get(i);
            if(var.getType().isBOOLEAN()){
                boolPar.add(invkPar.get(i));
            }
        }
        
        return boolPar;
	}
    
    
    /**
     *
     * @param processName Name of the process.
     * @return return the list with the integer parameters of the process with that name, an empty list otherwise.-
     */
    
    public LinkedList<AuxiliarExpression> getInvkIntParamList(String processName){
        
        LinkedList<AuxiliarExpression> intPar = new LinkedList<AuxiliarExpression>();
        LinkedList<AuxiliarExpression> invkPar = this.getInvkParametersList(processName);
        
        for(int i=0;i<invkPar.size();i++){
            AuxiliarVar var = (AuxiliarVar)invkPar.get(i);
            
            if(var.getType().isINT()){
                intPar.add(invkPar.get(i));
            }
        }
        
        return intPar;
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
    	
        return ( boolVars.size() + intVars.size() + enumVars.size());
    }
    
    public int getNumVarInt(){
    	
        return ( intVars.size());
    }
   
    public int getNumVarEnumerated(){
    	
        return ( enumVars.size());
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
    
    public void addParam(String parId){
    	paramList.add(new AuxiliarParam(parId));
    	
    }
    
    public void addInvkParam(AuxiliarInvkProcess invP){
    	processInvkParameters.add(invP);
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
    
    
    public void setParamList(LinkedList<AuxiliarParam> parL){
    	paramList = parL;
    }
    
    
    /**
     * Return the list of all names of the boolean variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */                       
    public LinkedList<String> getBoolVarNamesProcessInstances(){
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
    public LinkedList<String> getIntVarNamesProcessInstances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
    	   for(int i = 0; i< intVars.size(); i++){
    		   String nameI = new String (processInstanceNames.get(j) + "." + intVars.get(i).getName());
    		   varNames.add(nameI);

    	   }
   	    }
    	return varNames;
    }
    
    /**
     * Return the list of all names of the enumerated variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */
    public LinkedList<String> getEnumVarNamesProcessInstances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
            for(int i = 0; i< enumVars.size(); i++){
                String nameI = new String (processInstanceNames.get(j) + "." + enumVars.get(i).getName());
                varNames.add(nameI);
                
            }
   	    }
    	return varNames;
    }
    
    /**
     * @return Return the total number of enumVars of a single Process.
     * 
     */
    public int getNumEnumVarsProcess(String enumName){
        
        int numberVars =0;
        int numberParam = 0;
        
        
        for(int i = 0; i< enumVars.size(); i++){
            if(enumVars.get(i).getEnumName().equals(enumName) ){
                numberVars++;
            }
        }
        
        for(int i = 0; i< paramList.size(); i++){
            if(paramList.get(i).getEnumName().equals(enumName) ){
                numberParam++;
            }
        }
        
        
    	return (numberVars+numberParam);
    }
    
    
    /**
     * @return Return the total number of enumVars according the number of process instances.
     *
     */
    public int getNumEnumProcessInstances(String enumName){
        
        
        int numberVars =0;
        int numberInstances=0;
        int numberParam = 0;
        
        for(int i = 0; i< enumVars.size(); i++){
            if(enumVars.get(i).getEnumName().equals(enumName) ){
                numberVars++;
            }
        }
        
        
        for(int i = 0; i< paramList.size(); i++){
            if (paramList.get(i).getType().isEnumerated()){//Only compare if the type of parameter is Enum.
                if(paramList.get(i).getEnumName().equals(enumName) ){
                    numberParam++;
                }
            }
        }
        
        numberInstances = processInstanceNames.size();
        
       return ((numberParam + numberVars) * numberInstances);
    }
    
    
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
    
    
}
