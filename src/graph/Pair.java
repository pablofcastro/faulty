package graph;
import java.util.*;

public class Pair {
	
	private Object fst;
	private Object snd;

	public Pair(Object f, Object s){
	  fst = f;
	  snd = s;
	}

	public Object getFst(){
		return fst;
	}

	public Object getSnd(){
		return snd;
	}

	@Override
	public boolean equals(Object o){
		if (o instanceof Pair){
			Pair p = (Pair)o;
			if (fst instanceof Node && snd instanceof Node && p.getFst() instanceof Node && p.getSnd() instanceof Node)
				return ((Node)fst).equals((Node)p.getFst()) && ((Node)snd).equals((Node)p.getSnd());
			if (fst instanceof CompositeNode && snd instanceof CompositeNode && p.getFst() instanceof CompositeNode && p.getSnd() instanceof CompositeNode)
				return ((CompositeNode)fst).equals((CompositeNode)p.getFst()) && ((CompositeNode)snd).equals((CompositeNode)p.getSnd());
		}
		return false;
	}

	public String toString(){
		return "("+fst.toString()+","+snd.toString()+")";
	}


	@Override
	public int hashCode(){
	    return 1;
	}

}