package graph;

import java.util.*;
import faulty.auxiliar.*;
import java.io.*;

public class ExplicitCompositeModel {
	private HashMap<CompositeNode, TreeSet<CompositeNode>> succList; // Succesors adjacency list
	private HashMap<CompositeNode, TreeSet<CompositeNode>> preList; // Predecessors adjacency list
	private HashMap<Pair, String> labels; // Edge labels
	private HashMap<Pair, Boolean> faultyActions; // Faulty transitions
	private CompositeNode initial; // Initial State
	private LinkedList<AuxiliarVar> sharedVars; // Global variables
	private LinkedList<CompositeNode> nodes; // Global states
	private int numNodes;
	private int numEdges;

	public ExplicitCompositeModel(LinkedList<AuxiliarVar> svs) {
		sharedVars = svs;
		succList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		preList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		labels = new HashMap<Pair, String>();
		faultyActions = new HashMap<Pair, Boolean>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<CompositeNode>();

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

	public HashMap<Pair, String> getLabels(){
		return labels;
	}

	public HashMap<Pair, Boolean> getFaultyActions(){
		return faultyActions;
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
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			labels.put(new Pair(from,to),lbl);
			faultyActions.put(new Pair(from,to),faulty);
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
				if (faultyActions.get(edge))
					res += "    STATE"+v.toString()+" -> STATE"+ u.toString() +" [color=\"red\",label = \""+labels.get(edge)+"\"]"+";\n";
				else
					res += "    STATE"+v.toString()+" -> STATE"+ u.toString() +" [label = \""+labels.get(edge)+"\"]"+";\n";
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