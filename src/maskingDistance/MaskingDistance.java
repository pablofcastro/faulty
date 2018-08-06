package maskingDistance;

import java.util.*;
import graph.*;
import java.io.*;
import faulty.auxiliar.*;

public class MaskingDistance{

	private GameGraph g; // The masking distance game graph, undefined until buildGraph is called

	public MaskingDistance(){

	}

	public GameGraph getG(){
		return g;
	}

	public void buildGraph(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
		//This method builds a game graph for the Masking Distance Game, there are two players: the Refuter(R) and the Verifier(V)
		//The refuter plays with the implementation(imp), this means choosing any action available (faulty or not)
		//and the verifier plays with the specification, he tries to match the action played by the refuter, if he can't then an error state is reached.
		ExplicitCompositeModel spec,imp;
		spec = specProgram.toGraph();
		imp = impProgram.toGraph();

		g = new GameGraph();

        //calculate initial state
        GameNode init = new GameNode(spec.getInitial(), imp.getInitial(), "","R");
        g.addNode(init);
        g.setInitial(init);

        TreeSet<GameNode> iterSet = new TreeSet<GameNode>();
        iterSet.add(g.getInitial());

        //build the game graph
        while(!iterSet.isEmpty()){
            GameNode curr = iterSet.pollFirst();
            if (curr.getPlayer() == "R"){ //if player is refuter we add its possible moves from current state
            	for (CompositeNode succ : imp.getSuccessors(curr.getImpState())){
            		Pair p = new Pair(curr.getImpState(),succ);
            		GameNode curr_ = new GameNode(curr.getSpecState(),succ,imp.getLabels().get(p), "V");
            		GameNode toOld = g.search(curr_);
            		boolean f = imp.getFaultyActions().get(p);
                    if (toOld == null){
	            		g.addNode(curr_);
	            		if (f)
	            			curr_.setMask(true);
	            		g.addEdge(curr,curr_,curr_.getSymbol(), f); 
	            		iterSet.add(curr_);
	            	}
	            	else{
	            		g.addEdge(curr,toOld,toOld.getSymbol(), f);
	            	}
            	}
            }
            else{ //if player is verifier we add its matching move from current state or err transition if can't match
            	if (curr.getMask()){ //this means the state has to mask a previous fault
            		GameNode curr_ = new GameNode(curr.getSpecState(),curr.getImpState(),"", "R");
            		GameNode toOld = g.search(curr_);
                    if (toOld == null){
	            		g.addNode(curr_);
	            		g.addEdge(curr,curr_,"M"+curr.getSymbol(), false); //add label may not be necessary
	            		iterSet.add(curr_);
	            	}
	            	else{
	            		g.addEdge(curr,toOld,"M"+curr.getSymbol(), false);
	            	}
	            }
	            else{
	            	boolean foundSucc = false;
	            	for (CompositeNode succ : spec.getSuccessors(curr.getSpecState())){
		            	Pair p = new Pair(curr.getSpecState(),succ);
		            	if (curr.getSymbol().equals(spec.getLabels().get(p))){
		            		GameNode curr_ = new GameNode(succ,curr.getImpState(),"", "R");
		            		GameNode toOld = g.search(curr_);
		                    if (toOld == null){
			            		g.addNode(curr_);
			            		g.addEdge(curr,curr_,spec.getLabels().get(p), spec.getFaultyActions().get(p)); //add label may not be necessary
			            		iterSet.add(curr_);
			            	}
			            	else{
			            		g.addEdge(curr,toOld,spec.getLabels().get(p), spec.getFaultyActions().get(p));
			            	}
			            	foundSucc = true;
			            	break;
			            }
	            	}
	            	if (!foundSucc){
	        			g.addEdge(curr,g.errState(),"ERR", false);
	            	}
	            }
            }
        }
        //System.out.println(g.createDot());
	}

	public void buildGraphOptimized(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
		//This method builds a game graph for the Masking Distance Game, there are two players: the Refuter(R) and the Verifier(V)
		//The refuter plays with the implementation(imp), this means choosing any action available (faulty or not)
		//and the verifier plays with the specification, he tries to match the action played by the refuter, if he can't then an error state is reached.
		//This version has some optimizations, namely: Got rid of M transitions
		ExplicitCompositeModel spec,imp;
		spec = specProgram.toGraph();
		imp = impProgram.toGraph();

		g = new GameGraph();

        //calculate initial state
        GameNode init = new GameNode(spec.getInitial(), imp.getInitial(), "","R");
        g.addNode(init);
        g.setInitial(init);

        TreeSet<GameNode> iterSet = new TreeSet<GameNode>();
        iterSet.add(g.getInitial());

        //build the game graph
        while(!iterSet.isEmpty()){
            GameNode curr = iterSet.pollFirst();
            if (curr.getPlayer() == "R"){ //if player is refuter we add its possible moves from current state
            	for (CompositeNode succ : imp.getSuccessors(curr.getImpState())){
            		Pair p = new Pair(curr.getImpState(),succ);
            		boolean f = imp.getFaultyActions().get(p);
            		GameNode curr_;
            		if (f)
            			curr_ = new GameNode(curr.getSpecState(),succ,"", "R");
            		else
            			curr_ = new GameNode(curr.getSpecState(),succ,imp.getLabels().get(p), "V");
            		GameNode toOld = g.search(curr_);
                    if (toOld == null){
	            		g.addNode(curr_);
	            		g.addEdge(curr,curr_,imp.getLabels().get(p), f); 
	            		iterSet.add(curr_);
	            	}
	            	else{
	            		g.addEdge(curr,toOld,imp.getLabels().get(p), f);
	            	}
            	}
            }
            else{ //if player is verifier we add its matching move from current state or err transition if can't match
	            	boolean foundSucc = false;
	            	for (CompositeNode succ : spec.getSuccessors(curr.getSpecState())){
		            	Pair p = new Pair(curr.getSpecState(),succ);
		            	if (curr.getSymbol().equals(spec.getLabels().get(p))){
		            		GameNode curr_ = new GameNode(succ,curr.getImpState(),"", "R");
		            		GameNode toOld = g.search(curr_);
		                    if (toOld == null){
			            		g.addNode(curr_);
			            		g.addEdge(curr,curr_,spec.getLabels().get(p), spec.getFaultyActions().get(p)); //add label may not be necessary
			            		iterSet.add(curr_);
			            	}
			            	else{
			            		g.addEdge(curr,toOld,spec.getLabels().get(p), spec.getFaultyActions().get(p));
			            	}
			            	foundSucc = true;
			            	break;
			            }
	            	}
	            	if (!foundSucc){
			            g.addEdge(curr,g.errState(),"ERR", false);
	            	}
	            
            }
        }
        //System.out.println(g.createDot());
	}

    public double calculateDistance(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
		// We use dijsktra's algorithm to find the shortest path to an error state
		// This is the main method of this class
    	buildGraphOptimized(specProgram,impProgram);

        for(GameNode n : g.getNodes()){
        	n.setDistanceValue(Integer.MAX_VALUE);
        	n.setVisited(false);
        }
        g.getInitial().setDistanceValue(0);

        // Find shortest path for all vertices
        for (int count = 0; count < g.getNodes().size(); count++){
        	int min = Integer.MAX_VALUE;
        	int minIndex = 0;
        	GameNode from;
        	for (int i = 0;i<g.getNodes().size();i++){
        		if (!g.getNodes().get(i).getVisited() && g.getNodes().get(i).getDistanceValue() < min){
        			min = g.getNodes().get(i).getDistanceValue();
        			minIndex = i;
        		}
        	}
            from = g.getNodes().get(minIndex);
            from.setVisited(true);
            for (GameNode to : g.getSuccessors(from)){
                if (!to.getVisited()){
                	int addedCost = g.getFaultyActions().get(new Pair(from,to)) ? 1 : 0;
                	if (from.getDistanceValue()+addedCost < to.getDistanceValue()){
                    	to.setDistanceValue(from.getDistanceValue() + addedCost);
                    }
                }
            }
        }
        int minDistance = g.errState().getDistanceValue();
        
        double res= Math.round((double)1/(1+minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
		createDot();
		return res;
    }
	
	public void createDot(){
		g.createDot();
	}
}