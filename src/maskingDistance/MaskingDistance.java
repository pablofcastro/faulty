package maskingDistance;

import java.util.*;
import graph.*;
import java.io.*;
import faulty.auxiliar.*;

public class MaskingDistance{

	private GameGraph g;

	public MaskingDistance(){

	}

	public GameGraph getG(){
		return g;
	}

	public void buildGraph(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
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
	            		//System.out.println("AAAAA");
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
	            		//System.out.println("AAAAA");
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



	/*private int minDistance(int dist[], boolean sptSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index=-1;
 
        for (int v = 0; v < g.getNodes().size(); v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }
 
        return min_index;
    }

	public double calculateDistance2(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
		//We use dijsktra's algorithm to find the shortest path to an error state
    	buildGraphOptimized(specProgram,impProgram);
    	int n = g.getNodes().size();
        int dist[] = new int[n];
        boolean sptSet[] = new boolean[n];
        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < n; i++){
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }
        // Distance of source vertex from itself is always 0
        dist[1] = 0; // 1 is initial and 0 is errState
        // Find shortest path for all vertices
        for (int count = 0; count < n; count++){
            int u = minDistance(dist, sptSet);
            System.out.println(u);
            sptSet[u] = true;
            GameNode from = g.getNodes().get(u);
            for (int v = 0; v < n; v++){
            	GameNode to = g.getNodes().get(v);
                boolean adj = g.getSuccessors(from).contains(to);
                if (!sptSet[v] && adj ){
                	//System.out.println(to);
                	int addedCost = g.getFaultyActions().get(new Pair(from,to)) ? 1 : 0;
                	if (dist[u]+addedCost < dist[v]){
                		//System.out.println(to);
                    	dist[v] = dist[u] + addedCost;
                    	//System.out.print(addedCost);
                    	//System.out.println(g.getLabels().get(new Pair(from,to)));
                    }
                }
            }
        }

        int minDistance = dist[0];
        
        double res= Math.round((double)1/(1+minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
		createDot();
		return res;
    }*/

    public double calculateDistance(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
		//We use dijsktra's algorithm to find the shortest path to an error state
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