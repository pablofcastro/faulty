package graph;

import java.util.*;
import faulty.auxiliar.*;

public class ExplicitCompositeModel {
	private HashMap<CompositeNode, TreeSet<CompositeNode>> succList;
	private HashMap<CompositeNode, TreeSet<CompositeNode>> preList;
	private HashMap<Pair, String> labels; //edge labels
	private CompositeNode initial;
	private LinkedList<AuxiliarVar> sharedVars;
	private LinkedList<CompositeNode> nodes;
	private static final TreeSet<CompositeNode> EMPTY_SET = new TreeSet<CompositeNode>();
	private int numNodes;
	private int numEdges;

	/**
	 * Construct empty Graph
	 */
	public ExplicitCompositeModel(LinkedList<AuxiliarVar> svs) {
		sharedVars = svs;
		succList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		preList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		labels = new HashMap<Pair, String>();
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


	public void addNode(CompositeNode v) {
		nodes.add(v);
		succList.put(v, new TreeSet<CompositeNode>());
		preList.put(v, new TreeSet<CompositeNode>());
		numNodes += 1;
	}

	public CompositeNode search(CompositeNode v) {
		for (CompositeNode node : nodes){
			//System.out.println("node:"+node.toString());
			//System.out.println("v:"+v.toString());
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


	public void addEdge(CompositeNode from, CompositeNode to, String lbl) {
		if (to != null){
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			labels.put(new Pair(from,to),lbl);
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

	
	public String toString(){
		String res = "";
		for (CompositeNode v : nodes){
			res += "\n" + v.toString() + "\n";
			for (CompositeNode u : succList.get(v))
				res += "    -->"+ u.toString() +"\n";
		}
		return res;
	}

	public String createDot(){
		String res = "digraph model {\n\n";
		for (CompositeNode v : nodes){
			for (CompositeNode u : succList.get(v))
				res += "    "+v.toString()+" -> "+ u.toString() +" [label = \""+labels.get(new Pair(v,u))+"\"]"+";\n";
		}
		res += "\n}";
		return res;
	}

	/*public ExplicitModel flatten(){
		ExplicitModel res = new ExplicitModel();
		res.setInitial(union(initial));
		TreeSet<CompositeNode> set = new TreeSet<CompositeNode>();
		TreeSet<Node> setFlat = new TreeSet<Node>();
		set.add(initial);
		setFlat.add(res.getInitial());

		while (!set.isEmpty()){
			CompositeNode curr = set.pollFirst();
			Node currFlat = setFlat.pollFirst();
			for (CompositeNode n : succList.get(curr)){
				res.addEdge(currFlat, union(n));
			}
		}
		return res; 
	}

	private Node union(CompositeNode n){
		Node res = new Node();
		HashMap<String,Boolean> state = n.getFirst().getState();
		LinkedList<AuxiliarVar> vars = n.getFirst().getVars();
		for (int i)
		return res;
	}*/
}