package graph;

import java.util.*;
import faulty.auxiliar.*;


public class Node implements Comparable{
	HashMap<String,Boolean> state; // Current state of process, valuation of local vars, this includes parameters and local version of globals
	//HashMap<String,String> stateEnums;
	boolean visited; // Utility for graph traversal algorithms
	ExplicitModel model; // Process whose this node belongs to
	boolean isFaulty; // Is it a faulty state?

	public Node(){

	}

	public Node(ExplicitModel m){
		state = new HashMap<String,Boolean>();
		//stateEnums = new HashMap<String,String>();
		model = m;
		for (AuxiliarVar v : model.getVars()){
			state.put(v.getName(),false);
		}
		for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			state.put(v.getName(),false);
		}
		for (AuxiliarExpression invParam : model.getInvParams()){ //invParams
			if (invParam instanceof AuxiliarVar)
				state.put(((AuxiliarVar)invParam).getName(),false);
		}
		visited = false;
	}

	public Node(ExplicitModel m, AuxiliarExpression e){
		state = new HashMap<String,Boolean>();
		model = m;
		visited = false;
		state.putAll(evalInit(e));
		//evalInit(e);
		for (AuxiliarVar v : model.getVars()){
			if (!state.containsKey(v.getName()))
				state.put(v.getName(),false);
		}
		for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			if (!state.containsKey(v.getName()))
				state.put(v.getName(),false);
		}
		for (AuxiliarExpression invParam : model.getInvParams()){
			if (invParam instanceof AuxiliarVar){
				if (!state.containsKey(((AuxiliarVar)invParam).getName()))
					state.put(((AuxiliarVar)invParam).getName(),false);
			}
		}
		/*for (AuxiliarParam v : model.getParams()){
			if (!state.containsKey(v.getDeclarationName()))
				state.put(v.getDeclarationName(),false);
		}*/
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
		//HashMap<String,String> stEnum = new HashMap<String,String>();
		evalExprInit(e, false, st);
		//state.putAll(st);
		//stateEnums.putAll(stEnum);
		return st;
	}

	private AuxiliarVar instanciateIfParam(AuxiliarVar v){
		for (int i=0;i < model.getParams().size();i++){
			if (v.getName().equals(model.getParams().get(i)) && model.getInvParams().get(i) instanceof AuxiliarVar){
				v = (AuxiliarVar)model.getInvParams().get(i);
				break;
			}
		}
		return v;
	}

	private void evalExprInit(AuxiliarExpression e, boolean neg, HashMap<String,Boolean> st){
		if (e instanceof AuxiliarVar){
			e = instanciateIfParam((AuxiliarVar)e);
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
			AuxiliarEqBoolExp exp = (AuxiliarEqBoolExp)e;
			String varName = ((AuxiliarVar)exp.getInt1()).getName();
			/*if (e.getInt2() instanceof AuxiliarVar) //then its an enum
				stEnum.put(varName, ((AuxiliarVar)exp.getInt2()).getEnumName());
			else //its a bool*/
				st.put(varName, ((AuxiliarConsBoolExp)exp.getInt2()).getValue());
		}
	}

	private boolean evalBoolExpr(AuxiliarExpression e){
		if (e instanceof AuxiliarConsBoolExp){
			return ((AuxiliarConsBoolExp)e).getValue();
		}
		if (e instanceof AuxiliarVar){
			e = instanciateIfParam((AuxiliarVar)e);
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
		for (AuxiliarParam v : model.getParams()){
			n.state.put(v.getDeclarationName(),state.get(v.getDeclarationName()));
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