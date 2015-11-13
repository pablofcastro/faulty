package faulty;
import net.sf.javabdd.*;

import java.util.*;


/**
 * A class the implements code and represents a multiple assignment
 */
public class ListAssign implements Code{

    LinkedList<Code> assigns; // the list of assignments
    
    
    public ListAssign(LinkedList<Code> l){
    	this.assigns = l;
    }
    
    /**
     * Basic method that returns the BDD representing the list of assignments
     * @return the BDD representing the assignment
     */
    public BDD getBDD(){
       BDD result = Program.myFactory.one();
       
       for (int i = 0; i < assigns.size(); i++){
    	   result = result.and(assigns.get(i).getBDD());
       }
       return result;
    }
    
    /**
     * Returns the channels involved in the assignment
     * @return a list of channels
     */
    public LinkedList<Channel> getChannels(){
    	LinkedList<Channel> result = new LinkedList<Channel>();
    	    	
    	for (int i=0; i < assigns.size(); i++){
    		result.addAll(assigns.get(i).getChannels());
    	}
    	return result;
    }
    
    /**
     * Returns the list of vars involved in the assignment
     * @return the list of vars
     */
    public LinkedList<Var> getVars(){
    	LinkedList<Var> result = new LinkedList<Var>();
    	for (int i = 0; i < assigns.size(); i++){
    		result.addAll(assigns.get(i).getVars());
    	}
    	return result;
    }
    
    /**
     * Returns a BDD representing the skip BDD
     * @return the skip BDD
     */
    public BDD skipBDD(){
    	BDD result = Program.myFactory.one();
    	for (int i = 0; i < assigns.size(); i++){
    		result = result.and(assigns.get(i).skipBDD());
    	}
    	return result;
    }
    
    /**
     * Returns the list of channels occuring on the left of the assignment
     * @return the list of channels
     */
    public LinkedList<Channel> getChannelsLeft(){
    	LinkedList<Channel> result = new LinkedList();
    	for (int i = 0; i < assigns.size(); i++){
    		result.addAll(assigns.get(i).getChannelsLeft());
    	}   	
    	return result;
    }
    
    /**
     * Returns the list of channels occurring on the right of the assignation
     * @return a list of channels
     */
    public LinkedList<Channel> getChannelsRight(){
    	LinkedList<Channel> result = new LinkedList();
    	for (int i = 0; i < assigns.size(); i++){
    		result.addAll(assigns.get(i).getChannelsRight());
    	}   	
    	return result;
    }
    
    public Code duplicate(String instName, HashMap<VarBool, VarBool> boolMap, HashMap<VarInt, VarInt> intMap, Process owner){
    	LinkedList<Code> listAsg = new LinkedList<Code>();
    	
    	// we duplicates all the assignation and add them in a list
    	for (int i = 0; i < assigns.size(); i++){
    		listAsg.add((VarAssign) assigns.get(i).duplicate(instName, boolMap, intMap, owner));
    	}
    	
    	ListAssign result = new ListAssign(listAsg);
    	return result;
    	
    }
}

