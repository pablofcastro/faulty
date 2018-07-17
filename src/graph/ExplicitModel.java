package graph;

import java.util.*;
import faulty.auxiliar.*;

public class ExplicitModel {
	private HashMap<Node, TreeSet<Node>> succList;
	private HashMap<Node, TreeSet<Node>> preList;
	private Node initial;
	private LinkedList<Node> nodes;
	private static final TreeSet<Node> EMPTY_SET = new TreeSet<Node>();
	private int numNodes;
	private int numEdges;

	/**
	 * Construct empty Graph
	 */
	public ExplicitModel() {
		succList = new HashMap<Node, TreeSet<Node>>();
		preList = new HashMap<Node, TreeSet<Node>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<Node>();

	}

	public void setInitial(Node v){
		initial = v;
	}

	public Node getInitial(){
		return initial;
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


	public void addEdge(Node from, Node to) {
		if (to != null){
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
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
}