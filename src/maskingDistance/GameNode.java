package maskingDistance;

import java.util.*;
import graph.*;
import java.io.*;

public class GameNode implements Comparable{

	CompositeNode specState;
	CompositeNode impState;
	String symbol;
	String player;
	boolean mask;

	public GameNode(){

	}

	public GameNode(CompositeNode s, CompositeNode i, String sym, String p){
		specState = s;
		impState = i;
		symbol = sym;
		player = p;
	}

	public CompositeNode getSpecState(){
		return specState;
	}

	public CompositeNode getImpState(){
		return impState;
	}

	public String getSymbol(){
		return symbol;
	}

	public String getPlayer(){
		return player;
	}

	public boolean getMask(){
		return mask;
	}

	public void setMask(boolean m){
		mask = m;
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof GameNode)
			if (this.equals((GameNode)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return 1;
	}


	public String toString(){
		String res = specState.toString()+impState.toString()+"___"+player;
		return res;
	}

	public boolean equals(GameNode n){
		return specState.equals(n.getSpecState()) && impState.equals(n.getImpState()) && symbol.equals(n.getSymbol()) && player.equals(n.getPlayer());
	}

	/*public CompositeNode clone(){		
		return new GameNode(specState,impState,symbol,player);
	}*/
	
}