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

	public void buildGraph(AuxiliarProgram specProgram, AuxiliarProgram impProgram, boolean deadlockIsError){
		//This method builds a game graph for the Masking Distance Game, there are two players: the Refuter(R) and the Verifier(V)
		//The refuter plays with the implementation(imp), this means choosing any action available (faulty or not)
		//and the verifier plays with the specification(spec), he tries to match the action played by the refuter, if he can't then an error state is reached.
		ExplicitCompositeModel spec,imp;
		spec = specProgram.toGraph(false);
		imp = impProgram.toGraph(false);
		System.out.println("* Models Generated");
		//imp.createDot();
		imp.saturate();
		spec.saturate();
		imp.createDot();
		System.out.println("* Models Saturated");
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
            if (deadlockIsError && imp.getSuccessors(curr.getImpState()).isEmpty()){ // this is a special deadlock case
            		g.addEdge(curr,g.getErrState(),"ERR", false);
            }
            if (curr.getPlayer() == "R"){ //if player is refuter we add its possible moves from current state
            	for (CompositeNode succ : imp.getSuccessors(curr.getImpState())){
            		Pair p = new Pair(curr.getImpState(),succ);
            		if (imp.getLabels().get(p) != null){
            			for (int i=0; i < imp.getLabels().get(p).size(); i++){
            				String lbl = imp.getLabels().get(p).get(i);
            				if (imp.getTauActions().get(p) != null && imp.getTauActions().get(p).get(i))
            					lbl = "$";
            				GameNode curr_ = new GameNode(curr.getSpecState(),succ,lbl, "V");
		            		GameNode toOld = g.search(curr_);
		            		boolean f = imp.getFaultyActions().get(p).get(i);
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
            	}
            }
            else{ //if player is verifier we add its matching move from current state or err transition if can't match
            	/*if (spec.getSuccessors(curr.getSpecState()).size() == 1 && spec.getSuccessors(curr.getSpecState()).first() == curr.getSpecState()
            		&& curr.getSpecState().getIsFaulty()){
            		g.addEdge(curr,g.getErrState(),"ERR", false);
            	}*/
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
		            	if (spec.getLabels().get(p) != null){
	            			for (int i=0; i < spec.getLabels().get(p).size(); i++){
	            				String lbl = spec.getLabels().get(p).get(i);
	            				if (spec.getTauActions().get(p) != null && spec.getTauActions().get(p).get(i))
	            					lbl = "$";
	            				if (curr.getSymbol().equals(spec.getLabels().get(p).get(i))){
				            		GameNode curr_ = new GameNode(succ,curr.getImpState(),"", "R");
				            		GameNode toOld = g.search(curr_);
				                    if (toOld == null){
					            		g.addNode(curr_);
					            		g.addEdge(curr,curr_,lbl, spec.getFaultyActions().get(p).get(i)); //add label may not be necessary
					            		iterSet.add(curr_);
					            	}
					            	else{
					            		g.addEdge(curr,toOld,lbl, spec.getFaultyActions().get(p).get(i));
					            	}
					            	foundSucc = true;
					            	break;
					            }
	            			}
		            	}
	            	}
	            	if (!foundSucc){
	        			g.addEdge(curr,g.getErrState(),"ERR", false);
	            	}
	            }
            }
        }
        //System.out.println(g.createDot());
	}

	/*public void buildGraphOptimized(AuxiliarProgram specProgram, AuxiliarProgram impProgram){
		//This method builds a game graph for the Masking Distance Game, there are two players: the Refuter(R) and the Verifier(V)
		//The refuter plays with the implementation(imp), this means choosing any action available (faulty or not)
		//and the verifier plays with the specification(spec), he tries to match the action played by the refuter, if he can't then an error state is reached.
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
            		if (imp.getLabels().get(p) != null){
            			for (int i=0; i < imp.getLabels().get(p).size(); i++){
            				boolean f = imp.getFaultyActions().get(p).get(i);
		            		GameNode curr_;
		            		if (f)
		            			curr_ = new GameNode(curr.getSpecState(),succ,"", "R");
		            		else
		            			curr_ = new GameNode(curr.getSpecState(),succ,imp.getLabels().get(p).get(i), "V");
		            		GameNode toOld = g.search(curr_);
		                    if (toOld == null){
			            		g.addNode(curr_);
			            		g.addEdge(curr,curr_,imp.getLabels().get(p).get(i), f); 
			            		iterSet.add(curr_);
			            	}
			            	else{
			            		g.addEdge(curr,toOld,imp.getLabels().get(p).get(i), f);
			            	}
            			}
            		}
            	}
            }
            else{ //if player is verifier we add its matching move from current state or err transition if can't match
	            	boolean foundSucc = false;
	            	for (CompositeNode succ : spec.getSuccessors(curr.getSpecState())){
		            	Pair p = new Pair(curr.getSpecState(),succ);
		            	if (imp.getLabels().get(p) != null){
	            			for (int i=0; i < imp.getLabels().get(p).size(); i++){
	            				if (curr.getSymbol().equals(spec.getLabels().get(p).get(i))){
				            		GameNode curr_ = new GameNode(succ,curr.getImpState(),"", "R");
				            		GameNode toOld = g.search(curr_);
				                    if (toOld == null){
					            		g.addNode(curr_);
					            		g.addEdge(curr,curr_,spec.getLabels().get(p).get(i), spec.getFaultyActions().get(p).get(i)); //add label may not be necessary
					            		iterSet.add(curr_);
					            	}
					            	else{
					            		g.addEdge(curr,toOld,spec.getLabels().get(p).get(i), spec.getFaultyActions().get(p).get(i));
					            	}
					            	foundSucc = true;
					            	break;
					            }
	            			}
	            		}
	            	}
	            	if (!foundSucc){
			            g.addEdge(curr,g.getErrState(),"ERR", false);
	            	}
	            
            }
        }
        //System.out.println(g.createDot());
	}*/

    public double calculateDistance(AuxiliarProgram specProgram, AuxiliarProgram impProgram, boolean deadlockIsError){
		// We use dijsktra's algorithm to find the shortest path to an error state
		// This is the main method of this class
    	buildGraph(specProgram,impProgram,deadlockIsError);

        for(GameNode n : g.getNodes()){
        	n.setDistanceValue(Integer.MAX_VALUE);
        	n.setVisited(false);
        	n.setPreviousNodeInPath(null);
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
                	Pair p = new Pair(from,to);
                	int addedCost = 0;
                	for (int i=0; i < g.getFaultyActions().get(p).size(); i++)
	            		if (g.getFaultyActions().get(p).get(i))
							addedCost = 1;
                	if (from.getDistanceValue()+addedCost < to.getDistanceValue()){
                    	to.setDistanceValue(from.getDistanceValue() + addedCost);
                    	to.setPreviousNodeInPath(from);
                    }
                }
            }
        }
        int minDistance = g.getErrState().getDistanceValue();
        
        double res= Math.round((double)1/(1+minDistance) * Math.pow(10, 3)) / Math.pow(10, 3);
		return res;
    }
	
	public void printTraceToError(){
		System.out.println("\n·····ERROR PATH·····\n");
		GameNode curr = g.getErrState();
		int i = 0;
		while (curr != null){
			System.out.println(i+"."+curr.toString());
			curr = curr.getPreviousNodeInPath();
			i++;
		}
	}

	public void createDot(){
		g.createDot();
	}

	public void simulateGame(){
		GameNode curr = g.getInitial();
		Scanner sc = new Scanner(System.in);
		String c = "";
		System.out.println("\n·····SIMULATION·····\n");
		while (!c.equals("X") && !c.equals("x")){
			System.out.println("CURRENT STATE: ["+curr+"] , choose an action... (action : [nextstate])");
			Integer i = 0;
			for (GameNode succ : g.getSuccessors(curr)){
				Pair p = new Pair(curr,succ);
				if (g.getLabels().get(p) != null){
	            	for (int j=0; j < g.getLabels().get(p).size(); j++){
						System.out.println(i+". "+g.getLabels().get(p).get(j)+": "+"["+succ+"]");
						i++;
					}
				}
			}
			System.out.println("X. EXIT");
			c = sc.next();

			i = 0;
			for (GameNode succ : g.getSuccessors(curr)){
				Pair p = new Pair(curr,succ);
				if (g.getLabels().get(p) != null){
	            	for (int j=0; j < g.getLabels().get(p).size(); j++){
						if (c.equals(i.toString()))
							curr = succ;
						i++;
					}
				}
			}
			/*for (GameNode succ : g.getSuccessors(curr)){
				if (c.equals(i.toString()))
					curr = succ;
				i++;
			}*/
		}
	}
}