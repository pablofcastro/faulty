package graph;

import java.util.*;
import faulty.auxiliar.*;


public class CompositeNode implements Comparable{
	LinkedList<Node> nodes;

	public CompositeNode(LinkedList<Node> n){
		nodes = n;
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof CompositeNode)
			for (int i=0; i<(((CompositeNode)u).getNodes()).size(); i++)
				if (!nodes.get(i).equals(((CompositeNode)u).getNodes().get(i)))
					return -1;
		return 0;
	}

	public LinkedList<Node> getNodes(){
		return nodes;
	}

	public String toString(){
		return nodes.toString();
	}

	public boolean equals(CompositeNode n){
		for (int i=0;i<nodes.size();i++)
			if (!nodes.get(i).equals(n.getNodes().get(i)))
				return false;
		return true;
	}
}