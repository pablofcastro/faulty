package faulty;
import java.util.*;

import net.sf.javabdd.BDD;


public class AuxiliarProcess {

	LinkedList<String> channelIds;
	String processName;
	LinkedList<VarInt> intVars; // it has a collection of local variables of type int
	LinkedList<VarBool> boolVars; // it has a collections of local variables of type boolean
	LinkedList<Branch> branches; // a collection of branches (Bi -> Ci)
	BDD normCondition; // normative condition for characterising the non-faulty part of the program
	BDD initialCond; // the initial valuations of the local variables.

	
	public AuxiliarProcess(String name){
		this.processName = name;
		channelIds =new LinkedList<String>();
		intVars = new LinkedList<VarInt>();
		boolVars = new LinkedList<VarBool>();
		branches = new LinkedList<Branch>();
		normCondition = null;
	    initialCond = null;
		
	}
	
	
	public AuxiliarProcess(String name,BDD iniC, BDD normC,LinkedList<AuxiliarVar> varList,LinkedList<Branch> branchList){
		this.processName = name;
		channelIds =new LinkedList<String>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<VarInt>();
		boolVars = new LinkedList<VarBool>();
		
		
		
		 /* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add((VarBool)varList.get(i).getVar());
            }
            else{
            	intVars.add((VarInt)varList.get(i).getVar());
            }
            
        }
		
		
	}
	
	public AuxiliarProcess(BDD iniC, BDD normC,LinkedList<AuxiliarVar> varList,LinkedList<Branch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<String>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<VarInt>();
		boolVars = new LinkedList<VarBool>();
		
		
		 /* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add((VarBool)varList.get(i).getVar());
            }
            else{
            	intVars.add((VarInt)varList.get(i).getVar());
            }
            
        }
		
		
	}
	
	public String getName(){
		
		return processName;
	}
	
	
    public LinkedList<String> getChannelIds(){
		
		return channelIds;
	}
    
    public LinkedList<Branch> getBranches(){
		
		return branches;
	}
    
    public LinkedList<VarInt> getVarInt(){
		
		return intVars;
	}
 
    public LinkedList<VarBool> getVarBool(){
		
		return boolVars;
	}

    
    public BDD getInitialCond(){
    	return initialCond;
    	
    }
    
    public BDD getNormativeCond(){
    	return normCondition;
    	
    }
    
    public int getNumVar(){
    	
        return ( boolVars.size() + intVars.size());
    }
    
    public int getNumVarInt(){
    	
        return ( intVars.size());
    }
    
    public int getNumVarBool(){
    	
        return ( boolVars.size());
    }
    
    public int getNumBranches(){
    	
        return branches.size();
    }

    public int getNumChannelsUsed(){
	
    return  channelIds.size();
   }
	
    public void addChannelId(String chId){
    	channelIds.add(chId);
    	
    }
    
    public void addBranchList(LinkedList<Branch> list){
    	branches = list;
    	
    }
    
    public void setInitialCond(BDD ini){
    	initialCond = ini;
    	
    }
    
    public void setNormativeCond(BDD norm){
    	normCondition = norm;
    	
    }
    
    public void setName(String name){
    	processName = name;
    	
    }
    
    public void setChannelIds(LinkedList<String> chId){
    	channelIds = chId;
    	
    }
    
    
    
    
    
}
