package graph;

import java.util.*;
import faulty.auxiliar.*;
import java.io.*;

public class ExplicitModel {
	private HashMap<Node, TreeSet<Node>> succList; //succesors adjacency list
	private HashMap<Node, TreeSet<Node>> preList; //predecessors adjacency list
	private HashMap<Pair, String> labels; //edge labels
	private HashMap<Pair, Boolean> faultyActions;
	private LinkedList<AuxiliarVar> vars;
	private Node initial;
	private LinkedList<Node> nodes;
	private static final TreeSet<Node> EMPTY_SET = new TreeSet<Node>();
	private int numNodes;
	private int numEdges;
	private String processName;
	private ExplicitCompositeModel fullModel;
	private LinkedList<Pair> globalAssignments; //this is needed for updating the global state

	/**
	 * Construct empty Graph
	 */
	public ExplicitModel(String pName, LinkedList<AuxiliarVar> vs, ExplicitCompositeModel fm) {
		succList = new HashMap<Node, TreeSet<Node>>();
		preList = new HashMap<Node, TreeSet<Node>>();
		labels = new HashMap<Pair, String>();
		faultyActions = new HashMap<Pair, Boolean>();
		globalAssignments = new LinkedList<Pair>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<Node>();
		processName = pName;
		vars = vs;
		fullModel = fm;

	}

	public void setInitial(Node v){
		initial = v;
	}

	public ExplicitCompositeModel getFullModel(){
		return fullModel;
	}

	public Node getInitial(){
		return initial;
	}

	public HashMap<Pair, String> getLabels(){
		return labels;
	}

	public HashMap<Pair, Boolean> getFaultyActions(){
		return faultyActions;
	}

	public LinkedList<AuxiliarVar> getVars(){
		return vars;
	}

	public String getProcessName(){
		return processName;
	}

	public LinkedList<Pair> getGlobalAssignments(){
		return globalAssignments;
	}

	public void addNode(Node v) {
		nodes.add(v);
		succList.put(v, new TreeSet<Node>());
		preList.put(v, new TreeSet<Node>());
		numNodes += 1;
	}

	public Node search(Node v) {
		for (Node node : nodes){
			if (node.equals(v))
				return node;
		};
		return null;
	}

	public boolean hasNode(Node v) {
		return nodes.contains(v);
	}


	public boolean hasEdge(Node from, Node to) {

		if (!hasNode(from) || !hasNode(to))
			return false;
		return succList.get(from).contains(to);
	}


	public void addEdge(Node from, Node to, String lbl, Boolean faulty) {
		if (to != null){
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			labels.put(new Pair(from,to),processName+"."+lbl);
			faultyActions.put(new Pair(from,to),faulty);
		}
	}

	public LinkedList<Node> getNodes(){
		return nodes;
	}

	public TreeSet<Node> getSuccessors(Node v){
		return succList.get(v);
	}

	public TreeSet<Node> getPredecessors(Node v){
		return preList.get(v);
	}

	public void resetVisited(){
		for (Node v : nodes){
			v.resetVisited();
		}
	}
	
	public String toString(){
		String res = "";
		for (Node v : nodes){
			res += v.toString() + "\n";
			res += "    -->"+ succList.get(v).toString() +"\n";
		}
		return res;
	}

	public String createDot(){
		String res = "digraph model {\n\n";
		for (Node v : nodes){
			if (v.getIsFaulty())
				res += "    "+v.toString()+" [color=\"red\"];\n";
			for (Node u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (faultyActions.get(edge))
					res += "    "+v.toString()+" -> "+ u.toString() +" [color=\"red\",label = \""+labels.get(edge)+"\"]"+";\n";
				else
					res += "    "+v.toString()+" -> "+ u.toString() +" [label = \""+labels.get(edge)+"\"]"+";\n";
			}
		}
		res += "\n}";
		try{
            File file = new File("../out/" + processName +".dot");
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