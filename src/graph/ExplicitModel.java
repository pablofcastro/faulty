package graph;

import java.util.*;
import faulty.auxiliar.*;

public class ExplicitModel {
	private HashMap<Node, TreeSet<Node>> adjList;
	private LinkedList<Node> nodes;
	private static final TreeSet<Node> EMPTY_SET = new TreeSet<Node>();
	private int numNodes;
	private int numEdges;

	/**
	 * Construct empty Graph
	 */
	public ExplicitModel() {
		adjList = new HashMap<Node, TreeSet<Node>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<Node>();

	}


	public void addNode(Node v) {
		nodes.add(v);
		adjList.put(v, new TreeSet<Node>());
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
		return adjList.get(from).contains(to);
	}


	public void addEdge(Node from, Node to) {
		if (to != null){
			if (hasEdge(from, to))
				return;
			numEdges += 1;
			adjList.get(from).add(to);
		}
	}

	public LinkedList<Node> getnodes(){
		return nodes;
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
			res += "    ->"+ adjList.get(v).toString() +"\n";
		}
		return res;
	}
}