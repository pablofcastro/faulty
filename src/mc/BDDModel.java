package mc;

import java.util.*;
import mc.VarInfo.Type;
import net.sf.javabdd.*;
import faulty.*;

public class BDDModel {
	
	private  BDDFactory factory;
	private  BDD transitions;
	private  BDD init;
	private  BDD normative;
	private  ArrayList<VarInfo> varInfo; // List of vbles, with the name, type. 
	                                     // Their indices represents the identifier. 
	                                     // Vbles are stored according the order of declaration on the model. 
	                                     // Eg. for vbles a,b:BOOL  = [ ("a", Type.BOOL ), ("b" , Type.BOOL)]  
	private LinkedList<BDD> disjuncts;   // many times the model can be represented as a list of disjuncts
										 // in this case instead performing the disjuncts when constructing the model 
										 // we save the disjunct delaying the construction of the model
										 // as lates as possible
	
	private LinkedList<Var> externalVars;
	private LinkedList<Var> internalVars;
	
	/**
	 * A simple constructor, it is useful when we dont want to build the entire model and
	 * only have the disjuncts in this.disjuncts.
	 */
	public BDDModel(){
		this.varInfo = new ArrayList<VarInfo>();
		this.disjuncts = new LinkedList<BDD>();
		//this.normative = Program.myFactory.one();
		//this.factory = Program.myFactory;
	}
	
	
	/** 
	 * Pre: Parameters should not be null;
	 * Post: Initializes a new BDD model with the parameters received.
	 * */
	public BDDModel(BDD trans, BDD in){
		this.factory = trans.getFactory();
		this.init = in;
		this.transitions = trans;
		this.normative = this.factory.one(); //For now all states are normal states.
		this.varInfo = new ArrayList<VarInfo>();
	}
	
	/** 
	 * Pre: Parameters should not be null;
	 * Post: Initializes a new BDD model with the parameters received.
	 * */
	public BDDModel(BDD trans, BDD in, BDD norm){
		this.factory = trans.getFactory();
		this.init = in;
		this.transitions = trans;
		this.normative = norm; 
		this.varInfo = new ArrayList<VarInfo>();
	}
	
	
	
	public void setFactory(BDDFactory f) {
		this.factory = f;
	}


	public void setTransitions(BDD t) {
		this.factory = t.getFactory();
		this.transitions = t;
	}


	public void setInit(BDD i) {
		this.init = i;
	}
	

	public void setNormative(BDD normative) {
		this.normative = normative;
	}
	
	
	public void setDisjuncts(LinkedList<BDD> list){
		this.disjuncts = list;
	}
	
	public void setExternalVars(LinkedList<Var> vars){
		this.externalVars = vars;
	}
	
	public void setInternalVars(LinkedList<Var> vars){
		this.internalVars = vars;
	}
	
	public LinkedList<Var> getExternalVars(){
		return externalVars;
	}
	
	public LinkedList<Var> getInternalVars(){
		return internalVars;
	}
	
	
	public LinkedList<Integer> getExternalVarsIds(){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i = 0; i < externalVars.size(); i++){
			result.addAll(externalVars.get(i).getIds());
		}
		return result;
	}
	
	public LinkedList<Integer> getExternalPrimedVarsIds(){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i = 0; i < externalVars.size(); i++){
			result.addAll(externalVars.get(i).getPrimedIds());
		}
		return result;
	}
	
	public LinkedList<Integer> getInternalVarsIds(){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i = 0; i < internalVars.size(); i++){
			result.addAll(internalVars.get(i).getIds());
		}
		return result;
	}
	
	public LinkedList<Integer> getInternalPrimedVarsIds(){
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(int i = 0; i < internalVars.size(); i++){
			result.addAll(internalVars.get(i).getPrimedIds());
		}
		return result;
	}
	
	public BDDFactory getFactory(){
		return factory;		
	}
	
	/**
	 * 
	 * @return	A BDD representing the transition of the model
	 */
	public BDD getTransitions(){
		return transitions;
		
	}
	
	/**
	 * 
	 * @return	the init predicate
	 */
	public BDD getIni(){
		return init;		
	}
	
	/**
	 * 
	 * @return	the predicate norm
	 */
	public BDD getNormative() {
		return normative;
	}	
	
	/**
	 * 
	 * @return	the list disjuncts
	 */
	public LinkedList<BDD> getDisjuncts(){
		return disjuncts;
	}
	
    /***
     *
     * @param name : Variable name
     * @param type: Variable type (BOOL, INT)
	 * @return Returns true if the variable was inserted correctly at the end of the collection, 
	 * returns false in case that already exists a variable with that name.
     */
	public boolean addVar(String name,Type type){
		boolean exist = existVar(name);
		
		if(!exist){ 
			
			VarInfo e = new VarInfo(name,type);
			varInfo.add(e);
			/*System.out.println("Size ArrayList: " + varInfo.size() + " - ");
			for(int k=0; k< varInfo.size() ; k++){
				
				System.out.println("valor [" + k + "] = " +  varInfo.get(k).getName() + " ------ ");
			}*/
			return true;
		}
		else{		
		    return false;
		} 
	}
	
	/**
	 * Adds a disjunct to the list
	 * @param disj		the dijunct to be added to the list
	 */
	public void addDisjunct(BDD disj){
		this.disjuncts.add(disj);		
	}
	
	/***
     *
     * @param name : Variable name to be deleted.
     * @return Returns true if the variable was deleted correctly,
     * Shifts any subsequent elements to the left (subtracts one from their indices).
	 * Returns false in case that did not exists a variable with that name. 
	 *     */
	public boolean deleteVar(String name){
		int i = 0;
		boolean found = false;
		while ((i< this.varInfo.size()) && (!found)){
			if (this.varInfo.get(i).getName().equals(name)){
				this.varInfo.remove(i);
				found=true;
			}
			i++;
		}		
		return found;
	}

	/***
	 * 
	 * @param name
	 * @return Returns the variable's id, if exist a variable with that name, otherwise returns -1;
	 */
	public int getVarID(String name) {
			
		int i = 0;
		boolean found = false;
		while ((i< this.varInfo.size()) && (!found)){
			if (this.varInfo.get(i).getName().equals(name)){
				found=true;
			}
			i++;
		}		
		if (found){
			    return (i-1);
		}    
		else
			return -1;
	}
	
	
	/***
	 * Precondition: id should exist.
	 * @param name
	 * @return Returns the variable's name, if exist a variable with that id, otherwise returns null.
	 */
	public String getVarName(int id) {		
		return (varInfo.get(id)).getName();		
	}
	
	/***
	 * 
	 * @param name
	 * @return Returns true, if exist a variable with that name, false otherwise.
	 */
	public boolean existVar(String name) {
		
		int i = 0;
		boolean found = false;
		while ((i< this.varInfo.size()) && (!found)){
			if (this.varInfo.get(i).getName().equals(name)){
				found=true;
			}
			i++;
		}
		
		return found;
	}
	
	/***
	 * Precondition: Should exists a variable defined with that name.
	 * @param name
	 * @return Returns true if the variable type is Integer, false otherwise.
	 */
	public boolean isBoolean(String name) {
		
		int i = 0;
		boolean res = false;
		boolean found = false;
		while ((i< this.varInfo.size()) && !found){
			if (this.varInfo.get(i).getName().equals(name)){
				res= (this.varInfo.get(i).getType()== Type.BOOL);
				found =true;
			}
			i++;
		}		
		return res;
		
	}
	
	public int numberVars(){
		return varInfo.size();
	}
	
}
