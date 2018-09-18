package graph;

import java.util.*;
import faulty.auxiliar.*;


public class Node implements Comparable{
	HashMap<String,Boolean> state; // Current state of process, valuation of local vars, this includes parameters and local version of globals
	//HashMap<String,String> stateEnums;
	boolean visited; // Utility for graph traversal algorithms
	boolean isFaulty; // Is it a faulty state?
	AuxiliarProcess proc;
	String processName;
	CompositeNode parent;

	public Node(){

	}

	/*public Node(){
		state = new HashMap<String,Boolean>();
		//stateEnums = new HashMap<String,String>();
		for (AuxiliarVar v : proc.getVarBool()){
			state.put(v.getName(),false);
		}
		for (AuxiliarVar v : parent.getModel().getSharedVars()){
			parent.getGlobalState().put(v.getName(),false);
		}
		visited = false;
	}*/

	public Node(AuxiliarProcess p, String pName, CompositeNode par, AuxiliarExpression e){
		state = new HashMap<String,Boolean>();
		visited = false;
		processName = pName;
		proc = p;
		parent = par;
		state.putAll(evalInit(e));
		//evalInit(e);
		for (AuxiliarVar v : proc.getVarBool()){
			if (!state.containsKey(v.getName()))
				state.put(v.getName(),false);
		}
		for (AuxiliarVar v : parent.getModel().getSharedVars()){
			if (!parent.getGlobalState().containsKey(v.getName()))
				parent.getGlobalState().put(v.getName(),false);
		}
		/*for (AuxiliarExpression invParam : model.getInvParams()){
			if (invParam instanceof AuxiliarVar){
				if (!state.containsKey(((AuxiliarVar)invParam).getName()))
					state.put(((AuxiliarVar)invParam).getName(),false);
			}
		}*/
	}

	public HashMap<String,Boolean> getState(){
		return state;
	}

	public boolean getIsFaulty(){
		return isFaulty;
	}

	public CompositeNode getParent(){
		return parent;
	}

	public void setParent(CompositeNode par){
		parent = par;
	}

	public AuxiliarProcess getProcess(){
		return proc;
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

	public HashMap<String,Boolean> evalInit(AuxiliarExpression e){
		HashMap<String,Boolean> st = new HashMap<String,Boolean>();
		//HashMap<String,String> stEnum = new HashMap<String,String>();
		evalExprInit(e, false, st);
		//state.putAll(st);
		//stateEnums.putAll(stEnum);
		return st;
	}

	private AuxiliarVar instanciateIfParam(AuxiliarVar v){
		for (int i=0;i < proc.getParamList().size();i++){
			if (v.getName().equals(proc.getParamList().get(i).getDeclarationName()) && proc.getInvkParametersList(processName).get(i) instanceof AuxiliarVar){
				v = (AuxiliarVar)proc.getInvkParametersList(processName).get(i);
				break;
			}
		}
		return v;
	}

	private boolean checkGlobalVar(AuxiliarVar v){
		for (AuxiliarVar gv : parent.getModel().getSharedVars()){
			if (v.getName().equals(gv.getName()))
				return true;
		}
		return false;
	}

	private void evalExprInit(AuxiliarExpression e, boolean neg, HashMap<String,Boolean> st){
		if (e instanceof AuxiliarVar){
			e = instanciateIfParam((AuxiliarVar)e);
			if (checkGlobalVar((AuxiliarVar)e)){ //global
				if (neg)
					parent.getGlobalState().put(((AuxiliarVar)e).getName(),false);
				else
					parent.getGlobalState().put(((AuxiliarVar)e).getName(),true);
			}
			else{ //local
				if (neg)
					st.put(((AuxiliarVar)e).getName(),false);
				else
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
			if (checkGlobalVar((AuxiliarVar)e)){
				//if (parent.getGlobalState().get(((AuxiliarVar)e).getName()))
				//	System.out.println("PARENT:"+parent.toString());
				//System.out.println(((AuxiliarVar)e).getName()+":"+parent.getGlobalState().get(((AuxiliarVar)e).getName()));
				return parent.getGlobalState().get(((AuxiliarVar)e).getName());
			}
			else
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

	public Node createSuccessor(CompositeNode par, LinkedList<AuxiliarCode> assigns, int procIndex){
		Node succ = clone();
		succ.setParent(par);
		for (AuxiliarCode c : assigns){
			if (c instanceof AuxiliarVarAssign){
				AuxiliarVarAssign assign = (AuxiliarVarAssign)c;
				AuxiliarVar var = instanciateIfParam(assign.getVar());
				String name = var.getName();
				Boolean value = evalBoolExpr(assign.getExp());
				/*for (AuxiliarVar v : proc.getProgram().getSharedVars()){ // keep track of assignments to global variables
					if (v.getName().equals(name)){
						proc.getGlobalAssignments().add(new Pair(new Pair(this,succ),new Pair(name, value)));
						break;
					}
				}*/
				if (checkGlobalVar(var))
					succ.getParent().getGlobalState().put(name,value);
				else
					succ.getState().put(name,value);
			}
		}
		//check if succ already exists
		for (int j = 0; j < parent.getModel().getProcessesNodes().get(procIndex).size(); j++){
			Node old = parent.getModel().getProcessesNodes().get(procIndex).get(j);
			if (succ.equals(old)){
				old.setParent(par);
				return old;
			}
		}
		parent.getModel().getProcessesNodes().get(procIndex).add(succ); //add new local node
		return succ;
	}

	public Node clone(){
		Node n = new Node();
		n.proc = proc;
		n.processName = processName;
		n.parent = parent;
		n.state = new HashMap<String,Boolean>();
		for (AuxiliarVar v : proc.getVarBool()){
			n.state.put(v.getName(),state.get(v.getName()));
		}
		/*for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			n.state.put(v.getName(),state.get(v.getName()));
		}
		for (AuxiliarParam v : model.getParams()){
			n.state.put(v.getDeclarationName(),state.get(v.getDeclarationName()));
		}*/
		n.visited = false;
		return n;
	}

	public boolean equals(Node n){
		if (!n.getProcessName().equals(processName))
			return false;
		for (AuxiliarVar var: proc.getVarBool()){
			if (state.get(var.getName()) != n.getState().get(var.getName()))
				return false;
		}
		/*for (AuxiliarVar var: model.getFullModel().getSharedVars()){
			if (state.get(var.getName()) != n.getState().get(var.getName()))
				return false;
		}*/
		return true;
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : proc.getVarBool()){
			if (state.get(v.getName()))
				res += processName +""+v.getName() + "_";
		}
		/*for (AuxiliarVar v : model.getFullModel().getSharedVars()){
			if (state.get(v.getName()))
			res += model.getProcessName() +""+v.getName() + "_";
		}*/
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
	    return Objects.hash(state, visited, proc, processName, isFaulty);
	}
}