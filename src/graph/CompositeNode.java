package graph;

import java.util.*;
import faulty.auxiliar.*;


public class CompositeNode implements Comparable{
	LinkedList<Node> nodes; // Current Local States for each process 
	HashMap<String,Boolean> globalState; // Shared global state
	ExplicitCompositeModel model; // Global model whose this state belongs to
	boolean isFaulty; // Is this state a faulty one?

	public CompositeNode(){

	}

	public CompositeNode(LinkedList<Node> n, ExplicitCompositeModel m){
		nodes = n;
		model = m;
		globalState = new HashMap<String,Boolean>();
		for (AuxiliarVar v : model.getSharedVars()){
			globalState.put(v.getName(),false);
		}
		for (Node v : nodes){
			if (v.getIsFaulty())
				isFaulty = true;
		}
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof CompositeNode)
			if (this.equals((CompositeNode)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return Objects.hash(nodes, globalState, model, isFaulty);
	}

	public LinkedList<Node> getNodes(){
		return nodes;
	}

	public HashMap<String,Boolean> getGlobalState(){
		return globalState;
	}

	public boolean getIsFaulty(){
		return isFaulty;
	}

	//updateGlobalState takes as input a process node n and a process node n_ and updates the global state from assignments in transition <n,n_>
	public void updateGlobalState(Node n, Node n_){
		for (AuxiliarVar v : model.getSharedVars()){
			LinkedList<Pair> l = n.getModel().getGlobalAssignments();
			for (Pair e : l){
				Pair ef = (Pair)e.getFst();
				Pair es = (Pair)e.getSnd();
				Node eff = (Node)ef.getFst();
				Node efs = (Node)ef.getSnd();
				String esf = (String)es.getFst();
				Boolean ess = (Boolean)es.getSnd();
				if (eff.equals(n) && efs.equals(n_) && esf.equals(v.getName()))
					globalState.put(v.getName(),ess);
			}
		}
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : model.getSharedVars()){
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
		for (AuxiliarVar var: model.getSharedVars()){
			if (globalState.get(var.getName()) != n.getGlobalState().get(var.getName()))
				return false;
		}
		return true;
	}

	public CompositeNode clone(){
		CompositeNode n = new CompositeNode();
		n.model = model;
		n.nodes = new LinkedList<Node>();
		n.nodes.addAll(nodes); // this is not deep clone on purpose
		for (Node v : nodes){
			if (v.getIsFaulty())
				n.isFaulty = true;
		}
		n.globalState = new HashMap<String,Boolean>();
		for (AuxiliarVar v : model.getSharedVars()){
			n.globalState.put(v.getName(),globalState.get(v.getName()));
		}
		return n;
	}
}