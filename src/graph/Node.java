package graph;

import java.util.*;
import faulty.auxiliar.*;


public class Node implements Comparable{
	HashMap<String,Boolean> state;
	LinkedList<AuxiliarVar> vars;
	boolean visited;
	//CompositeNode superNode; // the node that englobes this one
	String processName;

	public Node(){

	}

	public Node(LinkedList<AuxiliarVar> vars){
		state = new HashMap<String,Boolean>();
		this.vars = vars;
		for (AuxiliarVar v : vars){
			state.put(v.getName(),false);
		}
		visited = false;
	}

	public Node(LinkedList<AuxiliarVar> vars, AuxiliarExpression e){
		state = new HashMap<String,Boolean>();
		this.vars = vars;
		visited = false;
		state.putAll(eval(e));
		for (AuxiliarVar v : vars){
			if (!state.containsKey(v.getName()))
				state.put(v.getName(),false);
		}
	}

	public HashMap<String,Boolean> getState(){
		return state;
	}

	public String getProcessName(){
		return processName;
	}

	public void resetVisited(){
		visited = false;
	}

	public void markVisited(){
		visited = true;
	}

	public boolean isVisited(){
		return visited;
	}

	public HashMap<String,Boolean> eval(AuxiliarExpression e){
		HashMap<String,Boolean> st = new HashMap<String,Boolean>();
		evalExpr(e, false, st);
		return st;
	}

	private void evalExpr(AuxiliarExpression e, boolean neg, HashMap<String,Boolean> st){
		if (e instanceof AuxiliarVar){
			if (neg){
				st.put(((AuxiliarVar)e).getName(),false);
			}
			else{
				st.put(((AuxiliarVar)e).getName(),true);
			}
		}
		if (e instanceof AuxiliarNegBoolExp){
			evalExpr(((AuxiliarNegBoolExp)e).getExp(),!neg, st);
		}
		if (e instanceof AuxiliarAndBoolExp){
			evalExpr(((AuxiliarAndBoolExp)e).getExp1(),neg,st);
			evalExpr(((AuxiliarAndBoolExp)e).getExp2(),neg,st);
		}
		if (e instanceof AuxiliarEqBoolExp){
			st.put(((AuxiliarVar)((AuxiliarEqBoolExp)e).getInt1()).getName(), ((AuxiliarConsBoolExp)((AuxiliarEqBoolExp)e).getInt2()).getValue());
		}
	}

	public boolean satisfies(AuxiliarExpression e){
		HashMap<String,Boolean> eState = eval(e);
		for (AuxiliarVar v : vars){
			if (eState.containsKey(v.getName()) && eState.get(v.getName()) != state.get(v.getName()))
				return false;
		}
		return true;
	}

	public Node createSuccessor(LinkedList<AuxiliarCode> assigns){
		Node succ = clone();
		for (AuxiliarCode c : assigns){
			if (c instanceof AuxiliarVarAssign){
				if (((AuxiliarVarAssign)c).getExp() instanceof AuxiliarConsBoolExp)
					succ.getState().put((((AuxiliarVarAssign)c).getVar()).getName(), ((AuxiliarConsBoolExp)((AuxiliarVarAssign)c).getExp()).getValue());
			}
		}
		return succ;
	}

	public Node clone(){
		Node n = new Node();
		n.vars = vars;
		n.state = new HashMap<String,Boolean>();
		for (AuxiliarVar v : vars){
			n.state.put(v.getName(),state.get(v.getName()));
		}
		n.processName = processName;
		n.visited = false;
		return n;
	}

	public boolean equals(Node n){
		if (!n.getProcessName().equals(processName))
			return false;
		for (AuxiliarVar var: vars){
			if (state.get(var.getName()) != n.getState().get(var.getName()))
				return false;
		}
		return true;
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : vars){
			if (state.get(v.getName()))
			res += processName +""+v.getName() + "_";
		}
		return res;
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof Node)
			if (this.equals((Node)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return 1;
	}

	public void setProcessName(String pName){
		processName = pName;
	}
}