package graph;

import java.util.*;
import faulty.auxiliar.*;


public class CompositeNode implements Comparable{
	LinkedList<Node> nodes;
	HashMap<String,Boolean> globalState;
	LinkedList<AuxiliarVar> sharedVars;

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
		if (u instanceof CompositeNode)
			for (int i=0; i<(((CompositeNode)u).getNodes()).size(); i++)
				if (!nodes.get(i).equals(((CompositeNode)u).getNodes().get(i)))
					return -1;
		return 0;
	}

	public LinkedList<Node> getNodes(){
		return nodes;
	}

	public void updateState(Node n){
		for (AuxiliarVar v : sharedVars){
			Boolean value = n.getState().get(v.getName());
			if (value != null)
				globalState.put(v.getName(),value);
		}
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : sharedVars){
			res += v.getName() + "="+ globalState.get(v.getName()) + ",";
		}
		return res+nodes.toString();
	}

	public boolean equals(CompositeNode n){
		for (int i=0;i<nodes.size();i++)
			if (!nodes.get(i).equals(n.getNodes().get(i)))
				return false;
		return true;
	}

	public CompositeNode clone(){
		return new CompositeNode((LinkedList<Node>)nodes.clone(), (LinkedList<AuxiliarVar>)sharedVars.clone());
	}
}