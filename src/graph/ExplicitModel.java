package graph;

import java.util.*;
import faulty.auxiliar.*;
import java.io.*;

public class ExplicitModel {
	private HashMap<Node, TreeSet<Node>> succList; // Succesors adjacency list
	private HashMap<Node, TreeSet<Node>> preList; // Predecessors adjacency list
	private HashMap<Pair, String> labels; // Edge labels
	private HashMap<Pair, Boolean> faultyActions; // Faulty transitions
	private LinkedList<AuxiliarVar> vars; // Local variables
	private LinkedList<AuxiliarParam> params;
	private LinkedList<AuxiliarExpression> invParams;
	private Node initial; // Initial State
	private LinkedList<Node> nodes; // States
	private int numNodes;
	private int numEdges;
	private String processName;
	private String processType;
	private ExplicitCompositeModel fullModel; //Program whose this process belongs to
	private LinkedList<Pair> globalAssignments; // Utility for updating the global state, pairs of form <<s,s'>,<gVar,value>>, i.e <transition,assignment>

	public ExplicitModel(String pName, String pType, LinkedList<AuxiliarVar> vs, LinkedList<AuxiliarParam> ps, LinkedList<AuxiliarExpression> ips, ExplicitCompositeModel fm) {
		succList = new HashMap<Node, TreeSet<Node>>();
		preList = new HashMap<Node, TreeSet<Node>>();
		labels = new HashMap<Pair, String>();
		faultyActions = new HashMap<Pair, Boolean>();
		globalAssignments = new LinkedList<Pair>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<Node>();
		processName = pName;
		processType = pType;
		vars = vs;
		params = ps;
		invParams = ips;
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

	public LinkedList<AuxiliarParam> getParams(){
		return params;
	}

	public LinkedList<AuxiliarExpression> getInvParams(){
		return invParams;
	}

	public String getProcessName(){
		return processName;
	}

	public String getProcessType(){
		return processType;
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
			labels.put(new Pair(from,to),processName+""+lbl);
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

	/*public void addEnvTransitions(int i){
		for (Node n : nodes){ //este for deberia estar afuera
			for (int j=i; j<fullModel.getSharedVars().size(); j++){
				addEnvTransitions(i+1);
				Node newState = n.clone();
				//flip(vars[j]) , agregar newState y agregar transicion de n a newState
				addEnvTransitions(i+1);
			}	
		}
	}*/
	

	public String createDot(){
		String res = "digraph model {\n\n";
		for (Node v : nodes){
			if (v.getIsFaulty())
				res += "    STATE"+v.toString()+" [color=\"red\"];\n";
			for (Node u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (faultyActions.get(edge))
					res += "    STATE"+v.toString()+" -> STATE"+ u.toString() +" [color=\"red\",label = \""+labels.get(edge)+"\"]"+";\n";
				else
					res += "    STATE"+v.toString()+" -> STATE"+ u.toString() +" [label = \""+labels.get(edge)+"\"]"+";\n";
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