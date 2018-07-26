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
                    if (toOld == null){
	            		g.addNode(curr_);
	            		g.addEdge(curr,curr_,curr_.getSymbol(), imp.getFaultyActions().get(p)); //add label may not be necessary
	            		iterSet.add(curr_);
	            	}
	            	else{
	            		g.addEdge(curr,toOld,toOld.getSymbol(), imp.getFaultyActions().get(p));
	            	}
            	}
            }
            else{ //if player is verifier we add its possible moves from current state
            	for (CompositeNode succ : spec.getSuccessors(curr.getSpecState())){
	            	Pair p = new Pair(curr.getSpecState(),succ);
	            	if (curr.getSymbol().equals(spec.getLabels().get(p))){
	            		GameNode curr_ = new GameNode(succ,curr.getImpState(),"", "R");
	            		GameNode toOld = g.search(curr_);
	                    if (toOld == null){
		            		g.addNode(curr_);
		            		g.addEdge(curr,curr_,curr_.getSymbol(), spec.getFaultyActions().get(p)); //add label may not be necessary
		            		iterSet.add(curr_);
		            	}
		            	else{
		            		g.addEdge(curr,toOld,toOld.getSymbol(), spec.getFaultyActions().get(p));
		            	}
		            }
            	}
            }
        }
        System.out.println(g.createDot());
	}
	
	public void createDot(){
		g.createDot();
	}
}