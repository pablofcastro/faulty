package graph;

import java.util.*;
import faulty.auxiliar.*;


public class Node implements Comparable{
	HashMap<String,Boolean> state; // Current state of process, valuation of local vars
	boolean visited; // Utility for graph traversal algorithms
	ExplicitModel model; // Process whose this node belongs to
	boolean isFaulty; // Is it a faulty state?

	public Node(){

	}

	public Node(ExplicitModel m){
		state = new HashMap<String,Boolean>();
		model = m;
		for (AuxiliarVar v : model.getVars()){
			state.put(v.getName(),false);
		}
		for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			state.put(v.getName(),false);
		}
		visited = false;
	}

	public Node(ExplicitModel m, AuxiliarExpression e){
		state = new HashMap<String,Boolean>();
		model = m;
		visited = false;
		state.putAll(evalInit(e));
		for (AuxiliarVar v : model.getVars()){
			if (!state.containsKey(v.getName()))
				state.put(v.getName(),false);
		}
		for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			if (!state.containsKey(v.getName()))
				state.put(v.getName(),false);
		}
	}

	public HashMap<String,Boolean> getState(){
		return state;
	}


	public ExplicitModel getModel(){
		return model;
	}

	public boolean getIsFaulty(){
		return isFaulty;
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

	public HashMap<String,Boolean> evalInit(AuxiliarExpression e){
		HashMap<String,Boolean> st = new HashMap<String,Boolean>();
		evalExprInit(e, false, st);
		return st;
	}

	private void evalExprInit(AuxiliarExpression e, boolean neg, HashMap<String,Boolean> st){
		if (e instanceof AuxiliarVar){
			if (neg){
				st.put(((AuxiliarVar)e).getName(),false);
			}
			else{
				st.put(((AuxiliarVar)e).getName(),true);
			}
		}
		if (e instanceof AuxiliarNegBoolExp){
			evalExprInit(((AuxiliarNegBoolExp)e).getExp(),!neg, st);
		}
		if (e instanceof AuxiliarAndBoolExp){
			evalExprInit(((AuxiliarAndBoolExp)e).getExp1(),neg,st);
			evalExprInit(((AuxiliarAndBoolExp)e).getExp2(),neg,st);
		}
		if (e instanceof AuxiliarEqBoolExp){
			st.put(((AuxiliarVar)((AuxiliarEqBoolExp)e).getInt1()).getName(), ((AuxiliarConsBoolExp)((AuxiliarEqBoolExp)e).getInt2()).getValue());
		}
	}

	private boolean evalBoolExpr(AuxiliarExpression e){
		if (e instanceof AuxiliarConsBoolExp){
			return ((AuxiliarConsBoolExp)e).getValue();
		}
		if (e instanceof AuxiliarVar){
			return state.get(((AuxiliarVar)e).getName());
		}
		if (e instanceof AuxiliarNegBoolExp){
			return !evalBoolExpr(((AuxiliarNegBoolExp)e).getExp());
		}
		if (e instanceof AuxiliarAndBoolExp){
			return evalBoolExpr(((AuxiliarAndBoolExp)e).getExp1()) && evalBoolExpr(((AuxiliarAndBoolExp)e).getExp2());
		}
		if (e instanceof AuxiliarOrBoolExp){
			return evalBoolExpr(((AuxiliarOrBoolExp)e).getExp1()) || evalBoolExpr(((AuxiliarOrBoolExp)e).getExp2());
		}
		if (e instanceof AuxiliarEqBoolExp){
			return evalBoolExpr(((AuxiliarEqBoolExp)e).getInt1()) == evalBoolExpr(((AuxiliarEqBoolExp)e).getInt2());
		}
		return false;
	}

	public boolean satisfies(AuxiliarExpression e){
		return evalBoolExpr(e);
	}

	public void checkNormCondition(AuxiliarExpression e){
		if (!satisfies(e))
            isFaulty = true;
	}

	public Node createSuccessor(LinkedList<AuxiliarCode> assigns){
		Node succ = clone();
		for (AuxiliarCode c : assigns){
			if (c instanceof AuxiliarVarAssign){
				String name = (((AuxiliarVarAssign)c).getVar()).getName();
				Boolean value = evalBoolExpr(((AuxiliarVarAssign)c).getExp());
				for (AuxiliarVar v : model.getFullModel().getSharedVars()){ // keep track of assignments to global variables
					if (v.getName().equals(name)){
						model.getGlobalAssignments().add(new Pair(new Pair(this,succ),new Pair(name, value)));
						break;
					}
				}
				succ.getState().put(name, value);
			}
		}
		return succ;
	}

	public Node clone(){
		Node n = new Node();
		n.model = model;
		n.state = new HashMap<String,Boolean>();
		for (AuxiliarVar v : model.getVars()){
			n.state.put(v.getName(),state.get(v.getName()));
		}
		for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			n.state.put(v.getName(),state.get(v.getName()));
		}
		n.visited = false;
		return n;
	}

	public boolean equals(Node n){
		if (!n.getModel().getProcessName().equals(model.getProcessName()))
			return false;
		for (AuxiliarVar var: model.getVars()){
			if (state.get(var.getName()) != n.getState().get(var.getName()))
				return false;
		}
		for (AuxiliarVar var: model.getFullModel().getSharedVars()){
			if (state.get(var.getName()) != n.getState().get(var.getName()))
				return false;
		}
		return true;
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : model.getVars()){
			if (state.get(v.getName()))
			res += model.getProcessName() +""+v.getName() + "_";
		}
		for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			if (state.get(v.getName()))
			res += model.getProcessName() +""+v.getName() + "_";
		}
		return res;
		//return state.toString();
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
	    return Objects.hash(state, visited, model, isFaulty);
	}
}