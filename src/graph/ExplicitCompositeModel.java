package graph;

import java.util.*;
import faulty.auxiliar.*;
import java.io.*;

public class ExplicitCompositeModel {
	private HashMap<CompositeNode, TreeSet<CompositeNode>> succList; // Succesors adjacency list
	private HashMap<CompositeNode, TreeSet<CompositeNode>> preList; // Predecessors adjacency list
	private HashMap<Pair, LinkedList<String>> labels; // Edge labels
	private HashMap<Pair, LinkedList<Boolean>> faultyActions; // Faulty transitions
	private HashMap<Pair, LinkedList<Boolean>> tauActions; // Internal transitions
	private CompositeNode initial; // Initial State
	private LinkedList<AuxiliarVar> sharedVars; // Global variables
	private LinkedList<CompositeNode> nodes; // Global states
	private int numNodes;
	private int numEdges;
	private LinkedList<AuxiliarProcess> procs;
	private LinkedList<String> procDecls;
	private boolean isWeak;

	public ExplicitCompositeModel(LinkedList<AuxiliarVar> svs) {
		sharedVars = svs;
		succList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		preList = new HashMap<CompositeNode, TreeSet<CompositeNode>>();
		labels = new HashMap<Pair, LinkedList<String>>();
		faultyActions = new HashMap<Pair, LinkedList<Boolean>>();
		tauActions = new HashMap<Pair, LinkedList<Boolean>>();
		numNodes = numEdges = 0;
		nodes = new LinkedList<CompositeNode>();
		procs = new LinkedList<AuxiliarProcess>();
		procDecls = new LinkedList<String>();
		isWeak = false;
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

	public HashMap<Pair, LinkedList<Boolean>> getTauActions(){
		return tauActions;
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


	public boolean hasEdge(CompositeNode from, CompositeNode to, String lbl) {

		if (!hasNode(from) || !hasNode(to))
			return false;
		Pair transition = new Pair(from,to);
		if (labels.get(transition) == null)
			return false;
		return succList.get(from).contains(to) && labels.get(transition).contains(lbl);
	}


	public void addEdge(CompositeNode from, CompositeNode to, String lbl, Boolean faulty, Boolean internal) {
		if (to != null){
			if (hasEdge(from, to, lbl))
				return;
			numEdges += 1;
			succList.get(from).add(to);
			preList.get(to).add(from);
			Pair transition = new Pair(from,to);
			if (labels.get(transition) == null){
				labels.put(transition,new LinkedList<String>());
				faultyActions.put(transition,new LinkedList<Boolean>());
				tauActions.put(transition,new LinkedList<Boolean>());
			}
			labels.get(transition).add(lbl);
			faultyActions.get(transition).add(faulty);
			tauActions.get(transition).add(internal);
			if (internal)
				isWeak = true;
			//check if label already added
			/*boolean addLabel = true;
			for (String l : labels.get(transition)){
				if (l.equals(lbl))
					addLabel = false;
			}
			if (addLabel){
				numEdges += 1;
				labels.get(transition).add(lbl);
				faultyActions.get(transition).add(faulty);
			}*/
		}
	}

	public void rmEdge(CompositeNode from, CompositeNode to, int pos) {
		Pair t = new Pair(from,to);
		tauActions.get(t).remove(pos);
		faultyActions.get(t).remove(pos);
		labels.get(t).remove(pos);
		if (labels.get(t).isEmpty()){
			/*faultyActions.put(t,null);
			tauActions.put(t,null);
			labels.put(t,null);*/
			succList.get(from).remove(to);
			preList.get(to).remove(from);
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
				res += "    STATE"+v.toStringDot()+" [color=\"red\"];\n";
			for (CompositeNode u : succList.get(v)){
				Pair edge = new Pair(v,u);
				if (labels.get(edge) != null)
					for (int i=0; i < labels.get(edge).size(); i++)			
						if (faultyActions.get(edge).get(i))
							res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [color=\"red\",label = \""+labels.get(edge).get(i)+"\"]"+";\n";
						else
							res += "    STATE"+v.toStringDot()+" -> STATE"+ u.toStringDot() +" [label = \""+labels.get(edge).get(i)+"\"]"+";\n";
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


	public void saturate(){
		if (!isWeak)
			return;

		//Add tau self-loops
		for (CompositeNode p : nodes){
			addEdge(p,p,"",false,true); // p -> p is internal
		}

		boolean change = true;
		//this lists will share the same size
		LinkedList<CompositeNode> fsts;
		LinkedList<CompositeNode> snds;
		LinkedList<String> lbls;
		LinkedList<Boolean> isFs;


		//Saturate graph
		while (change){
			change = false;
			fsts = new LinkedList();
			snds = new LinkedList();
			lbls = new LinkedList();
			isFs = new LinkedList();

			for (CompositeNode p : nodes){
				for (CompositeNode p_ : succList.get(p)){
					Pair t0 = new Pair(p,p_);
					if (tauActions.get(t0) != null){
						for (int i = 0; i < tauActions.get(t0).size(); i++){
							if (tauActions.get(t0).get(i)){ // p -> p_ is internal
								for (CompositeNode q_ : succList.get(p_)){
									Pair t1 = new Pair(p_,q_);
									if (tauActions.get(t1) != null){
										for (int j = 0; j < tauActions.get(t1).size(); j++){
											if (!tauActions.get(t1).get(j)){ // p_ -> q_ is external
												String lbl = labels.get(t1).get(j);
												Boolean isF = faultyActions.get(t1).get(j);
												for (CompositeNode q : succList.get(q_)){
													Pair t2 = new Pair(q_,q);
													if (tauActions.get(t2) != null){
														for (int k = 0; k < tauActions.get(t2).size(); k++){
															if (tauActions.get(t2).get(k)){ // q_ -> q is internal
																//add transition for later update
																if (!hasEdge(p,q,lbl)){
																	fsts.add(p);
																	snds.add(q);
																	lbls.add(lbl);
																	isFs.add(isF);
																	change = true;
																}	
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			//update transition system
			for (int i = 0; i < fsts.size(); i++){
				addEdge(fsts.get(i), snds.get(i), lbls.get(i), isFs.get(i), false);
			}
		}

		//Remove all taus
		/*for (CompositeNode p : nodes){
			for (CompositeNode q : nodes){
				Pair t = new Pair(p,q);
				if (tauActions.get(t) != null){
					for (int i = 0; i < tauActions.get(t).size(); i++){
						if (tauActions.get(t).get(i)){
							rmEdge(p,q,i);
						}
					}
				}
			}
		}*/
	}
	
}