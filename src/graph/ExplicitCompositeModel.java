package graph;

import java.util.*;
import faulty.auxiliar.*;
import java.io.*;

public class ExplicitCompositeModel {
	private HashMap<CompositeNode, TreeSet<CompositeNode>> succList; // Succesors adjacency list
	private HashMap<CompositeNode, TreeSet<CompositeNode>> preList; // Predecessors adjacency list
	private HashMap<Pair, LinkedList<String>> labels; // Edge labels
	private HashMap<Pair, LinkedList<Boolean>> faultyActions; // Faulty transitions
	private CompositeNode initial; // Initial State
	private LinkedList<AuxiliarVar> sharedVars; // Global variables
	private LinkedList<CompositeNode> nodes; // Global states
	private int numNodes;
	private int numEdges;
	private LinkedList<AuxiliarProcess> procs;
	private LinkedList<String> procDecls;

	public ExplicitCompositeModel(LinkedList<AuxiliarVar> svs) {
		sharedVars = svs;
		succList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		preList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		labels = new HashMap<Pair, LinkedList<String>>();
		faultyActions = new HashMap<Pair, LinkedList<Boolean>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<CompositeNode>();
		procs = new LinkedList<AuxiliarProcess>();
		procDecls = new LinkedList<String>();
	}

	public void setInitial(CompositeNode v){
		initial = v;
	}

	public CompositeNode getInitial(){
		return initial;
	}

	public LinkedList<AuxiliarVar> getSharedVars(){
		return sharedVars;
	}

	public HashMap<Pair, LinkedList<String>> getLabels(){
		return labels;
	}

	public HashMap<Pair, LinkedList<Boolean>> getFaultyActions(){
		return faultyActions;
	}

	public LinkedList<AuxiliarProcess> getProcs(){
		return procs;
	}

	public LinkedList<String> getProcDecls(){
		return procDecls;
	}

	public void addNode(CompositeNode v) {
		nodes.add(v);
		succList.put(v, new TreeSet<CompositeNode>());
		preList.put(v, new TreeSet<CompositeNode>());
		numNodes += 1;
	}

	public CompositeNode search(CompositeNode v) {
		for (CompositeNode node : nodes){
			if (node.equals(v))
				return node;
		};
		return null;
	}

	public boolean hasNode(CompositeNode v) {
		return nodes.contains(v);
	}


	public boolean hasEdge(CompositeNode from, CompositeNode to) {

		if (!hasNode(from) || !hasNode(to))
			return false;
		return succList.get(from).contains(to);
	}


	public void addEdge(CompositeNode from, CompositeNode to, String lbl, Boolean faulty) {
		if (to != null){
			//if (hasEdge(from, to))
			//	return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			Pair transition = new Pair(from,to);
			if (labels.get(transition) == null){
				labels.put(transition,new LinkedList<String>());
				faultyActions.put(transition,new LinkedList<Boolean>());
			}
			labels.get(transition).add(lbl);
			faultyActions.get(transition).add(faulty);
		}
	}

	public LinkedList<CompositeNode> getNodes(){
		return nodes;
	}

	public TreeSet<CompositeNode> getSuccessors(CompositeNode v){
		return succList.get(v);
	}

	public TreeSet<CompositeNode> getPredecessors(CompositeNode v){
		return preList.get(v);
	}

	public String createDot(){
		String res = "digraph model {\n\n";
		for (CompositeNode v : nodes){
			if (v.getIsFaulty())
				res += "    STATE"+v.toString()+" [color=\"red\"];\n";
			for (CompositeNode u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (labels.get(edge) != null)
					for (int i=0; i < labels.get(edge).size(); i++)			
						if (faultyActions.get(edge).get(i))
							res += "    STATE"+v.toString()+" -> STATE"+ u.toString() +" [color=\"red\",label = \""+labels.get(edge).get(i)+"\"]"+";\n";
						else
							res += "    STATE"+v.toString()+" -> STATE"+ u.toString() +" [label = \""+labels.get(edge).get(i)+"\"]"+";\n";
			}
		}
		res += "\n}";
		try{
            File file = new File("../out/" + "FullModel" +".dot");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(res);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
		return res;
	}
	
}