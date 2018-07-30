package maskingDistance;

import java.util.*;
import graph.*;
import java.io.*;

public class GameGraph{

	private HashMap<GameNode, TreeSet<GameNode>> succList;
	private HashMap<GameNode, TreeSet<GameNode>> preList;
	private HashMap<Pair, String> labels; //edge labels, may not be necessary
	private HashMap<Pair, Boolean> faultyActions;
	private GameNode initial;
	private LinkedList<GameNode> nodes;
	private static final TreeSet<GameNode> EMPTY_SET = new TreeSet<GameNode>();
	private int numNodes;
	private int numEdges;

	public GameGraph() {
		succList = new HashMap<GameNode, TreeSet<GameNode>>();
		preList = new HashMap<GameNode, TreeSet<GameNode>>();
		labels = new HashMap<Pair, String>();
		faultyActions = new HashMap<Pair, Boolean>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<GameNode>();

	}

	public void setInitial(GameNode v){
		initial = v;
	}

	public GameNode getInitial(){
		return initial;
	}

	public HashMap<Pair, String> getLabels(){
		return labels;
	}

	public void addNode(GameNode v) {
		nodes.add(v);
		succList.put(v, new TreeSet<GameNode>());
		preList.put(v, new TreeSet<GameNode>());
		numNodes += 1;
	}

	public GameNode search(GameNode v) {
		for (GameNode node : nodes){
			if (node.equals(v))
				return node;
		}
		return null;
	}

	public boolean hasNode(GameNode v) {
		return nodes.contains(v);
	}


	public boolean hasEdge(GameNode from, GameNode to) {

		if (!hasNode(from) || !hasNode(to))
			return false;
		return succList.get(from).contains(to);
	}


	public void addEdge(GameNode from, GameNode to, String lbl, Boolean faulty) {
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

	public LinkedList<GameNode> getNodes(){
		return nodes;
	}

	public TreeSet<GameNode> getSuccessors(GameNode v){
		return succList.get(v);
	}

	public TreeSet<GameNode> getPredecessors(GameNode v){
		return preList.get(v);
	}

	
	public String toString(){
		String res = "";
		for (GameNode v : nodes){
			res += "\n" + v.toString() + "\n";
			for (GameNode u : succList.get(v))
				res += "    -->"+ u.toString() +"\n";
		}
		return res;
	}

	public String createDot(){
		String res = "digraph model {\n\n";
		for (GameNode v : nodes){
			for (GameNode u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (labels.get(edge).split("|")[0].equals("M"))
					res += "    "+v.toString()+" -> "+ u.toString() +" [color=\"green\",label = \""+labels.get(edge)+"\"]"+";\n";
				else
					if (faultyActions.get(edge))
						res += "    "+v.toString()+" -> "+ u.toString() +" [color=\"red\",label = \""+labels.get(edge)+"\"]"+";\n";
					else
						res += "    "+v.toString()+" -> "+ u.toString() +" [label = \""+labels.get(edge)+"\"]"+";\n";
			}
		}
		res += "\n}";
		try{
            File file = new File("../out/" + "GameGraph" +".dot");
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