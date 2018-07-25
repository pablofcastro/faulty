package graph;

import java.util.*;
import faulty.auxiliar.*;


public class CompositeNode implements Comparable{
	LinkedList<Node> nodes;
	HashMap<String,Boolean> globalState;
	LinkedList<AuxiliarVar> sharedVars;

	public CompositeNode(){

	}

	public CompositeNode(LinkedList<Node> n, LinkedList<AuxiliarVar> vars){
		nodes = n;
		sharedVars = vars;
		globalState = new HashMap<String,Boolean>();
		for (AuxiliarVar v : sharedVars){
			globalState.put(v.getName(),false);
		}
	}

	@Override
	public int compareTo(Object u) {
		/*if (u instanceof CompositeNode)
			for (int i=0; i<(((CompositeNode)u).getNodes()).size(); i++)
				if (!nodes.get(i).equals(((CompositeNode)u).getNodes().get(i)))
					return -1;
		return 0;*/
		if (u instanceof CompositeNode)
			if (this.equals((CompositeNode)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return 1;
	}

	public LinkedList<Node> getNodes(){
		return nodes;
	}

	public HashMap<String,Boolean> getGlobalState(){
		return globalState;
	}

	public void updateState(Node n){
		for (AuxiliarVar v : sharedVars){
			Boolean value = n.getState().get(v.getName());
			if (value != null){
				globalState.put(v.getName(),value);
			}
		}
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : sharedVars){
			if (globalState.get(v.getName()))
				res += v.getName() + "_";
		}
		for (Node n : nodes){
			res += n.toString();
		}
		return res;
	}

	public boolean equals(CompositeNode n){
		for (int i=0;i<nodes.size();i++)
			if (!nodes.get(i).equals(n.getNodes().get(i)))
				return false;
		for (AuxiliarVar var: sharedVars){
			if (globalState.get(var.getName()) != n.getGlobalState().get(var.getName()))
				return false;
		}
		return true;
	}

	public CompositeNode clone(){
		CompositeNode n = new CompositeNode();
		n.sharedVars = sharedVars;
		n.nodes = new LinkedList<Node>();
		/*for (Node v : nodes){
			n.nodes.add(v.clone());
		}*/
		n.nodes.addAll(nodes);
		n.globalState = new HashMap<String,Boolean>();
		for (AuxiliarVar v : sharedVars){
			n.globalState.put(v.getName(),globalState.get(v.getName()));
		}
		return n;
	}
}