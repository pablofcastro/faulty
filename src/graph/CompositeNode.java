package graph;

import java.util.*;
import faulty.auxiliar.*;


public class CompositeNode implements Comparable{
	HashMap<String,Boolean> state; // global state
	ExplicitCompositeModel model; // Global model whose this state belongs to
	boolean isFaulty; // Is this state a faulty one?

	public CompositeNode(){

	}

	public CompositeNode(ExplicitCompositeModel m){
		model = m;
		state = new HashMap<String,Boolean>();
		for (AuxiliarVar v : model.getSharedVars()){
			state.put(v.getName(),false);
		}
		for (int i=0; i < model.getProcDecls().size(); i++){
			for (AuxiliarVar v : model.getProcs().get(i).getVarBool()){
				state.put(model.getProcDecls().get(i)+v.getName(),false);
			}
		}
	}

	@Override
	public int compareTo(Object u) {
		if (u instanceof CompositeNode)
			if (this.equals((CompositeNode)u))
				return 0;
		return -1;
	}

	@Override
	public int hashCode(){
	    return Objects.hash(state, model, isFaulty);
	}

	public HashMap<String,Boolean> getState(){
		return state;
	}

	public boolean getIsFaulty(){
		return isFaulty;
	}

	public ExplicitCompositeModel getModel(){
		return model;
	}

	public boolean satisfies(AuxiliarExpression e, int procIndex){
		return evalBoolExpr(e, procIndex);
	}

	public void checkNormCondition(AuxiliarExpression e, int procIndex){
		if (!satisfies(e, procIndex))
            isFaulty = true;
	}

	public String toString(){
		String res = "";
		for (AuxiliarVar v : model.getSharedVars()){
			if (state.get(v.getName()))
				res += v.getName() + "_";
		}
		for (int i=0; i < model.getProcDecls().size(); i++){
			for (AuxiliarVar v : model.getProcs().get(i).getVarBool()){
				if (state.get(model.getProcDecls().get(i)+v.getName()))
					res += model.getProcDecls().get(i)+v.getName() + "_";
			}
		}
		return res;
	}

	public boolean equals(CompositeNode n){
		for (AuxiliarVar var: model.getSharedVars()){
			if (state.get(var.getName()) != n.getState().get(var.getName()))
				return false;
		}
		for (int i=0; i < model.getProcDecls().size(); i++){
			for (AuxiliarVar v : model.getProcs().get(i).getVarBool()){
				if (n.getState().get(model.getProcDecls().get(i)+v.getName()) != state.get(model.getProcDecls().get(i)+v.getName()))
					return false;
			}
		}
		return true;
	}

	public CompositeNode clone(){
		CompositeNode n = new CompositeNode();
		n.model = model;
		n.state = new HashMap<String,Boolean>();
		for (AuxiliarVar v : model.getSharedVars()){
			n.state.put(v.getName(),state.get(v.getName()));
		}
		for (int i=0; i < model.getProcDecls().size(); i++){
			for (AuxiliarVar v : model.getProcs().get(i).getVarBool()){
				n.state.put(model.getProcDecls().get(i)+v.getName(),state.get(model.getProcDecls().get(i)+v.getName()));
			}
		}
		return n;
	}

	public void evalInit(AuxiliarExpression e, int procIndex){
		HashMap<String,Boolean> st = new HashMap<String,Boolean>();
		//HashMap<String,String> stEnum = new HashMap<String,String>();
		evalExprInit(e, false, st, procIndex);
		//state.putAll(st);
		//stateEnums.putAll(stEnum);
		state.putAll(st);
	}

	private AuxiliarVar instanciateIfParam(AuxiliarVar v, int procIndex){
		AuxiliarProcess proc = model.getProcs().get(procIndex);
		String processName = model.getProcDecls().get(procIndex);
		for (int i=0;i < proc.getParamList().size();i++){
			if (v.getName().equals(proc.getParamList().get(i).getDeclarationName()) && proc.getInvkParametersList(processName).get(i) instanceof AuxiliarVar){
				v = (AuxiliarVar)proc.getInvkParametersList(processName).get(i);
				break;
			}
		}
		return v;
	}

	private boolean checkGlobalVar(AuxiliarVar v){
		for (AuxiliarVar gv : model.getSharedVars()){
			if (v.getName().equals(gv.getName()))
				return true;
		}
		return false;
	}

	private void evalExprInit(AuxiliarExpression e, boolean neg, HashMap<String,Boolean> st, int procIndex){
		if (e instanceof AuxiliarVar){
			e = instanciateIfParam((AuxiliarVar)e,procIndex);
			if (checkGlobalVar((AuxiliarVar)e)){ //global
				if (neg)
					st.put(((AuxiliarVar)e).getName(),false);
				else
					st.put(((AuxiliarVar)e).getName(),true);
			}
			else{ //local
				if (neg)
					st.put(model.getProcDecls().get(procIndex)+((AuxiliarVar)e).getName(),false);
				else
					st.put(model.getProcDecls().get(procIndex)+((AuxiliarVar)e).getName(),true);
			}
		}
		if (e instanceof AuxiliarNegBoolExp){
			evalExprInit(((AuxiliarNegBoolExp)e).getExp(),!neg, st,procIndex);
		}
		if (e instanceof AuxiliarAndBoolExp){
			evalExprInit(((AuxiliarAndBoolExp)e).getExp1(),neg,st,procIndex);
			evalExprInit(((AuxiliarAndBoolExp)e).getExp2(),neg,st,procIndex);
		}
		if (e instanceof AuxiliarEqBoolExp){
			AuxiliarEqBoolExp exp = (AuxiliarEqBoolExp)e;
			String varName = model.getProcDecls().get(procIndex)+((AuxiliarVar)exp.getInt1()).getName();
			/*if (e.getInt2() instanceof AuxiliarVar) //then its an enum
				stEnum.put(varName, ((AuxiliarVar)exp.getInt2()).getEnumName());
			else //its a bool*/
				st.put(varName, ((AuxiliarConsBoolExp)exp.getInt2()).getValue());
		}
	}

	private boolean evalBoolExpr(AuxiliarExpression e, int procIndex){
		if (e instanceof AuxiliarConsBoolExp){
			return ((AuxiliarConsBoolExp)e).getValue();
		}
		if (e instanceof AuxiliarVar){
			e = instanciateIfParam((AuxiliarVar)e,procIndex);
			if (checkGlobalVar((AuxiliarVar)e)){
				return state.get(((AuxiliarVar)e).getName());
			}
			else
				return state.get(model.getProcDecls().get(procIndex)+((AuxiliarVar)e).getName());
		}
		if (e instanceof AuxiliarNegBoolExp){
			return !evalBoolExpr(((AuxiliarNegBoolExp)e).getExp(),procIndex);
		}
		if (e instanceof AuxiliarAndBoolExp){
			return evalBoolExpr(((AuxiliarAndBoolExp)e).getExp1(),procIndex) && evalBoolExpr(((AuxiliarAndBoolExp)e).getExp2(),procIndex);
		}
		if (e instanceof AuxiliarOrBoolExp){
			return evalBoolExpr(((AuxiliarOrBoolExp)e).getExp1(),procIndex) || evalBoolExpr(((AuxiliarOrBoolExp)e).getExp2(),procIndex);
		}
		if (e instanceof AuxiliarEqBoolExp){
			return evalBoolExpr(((AuxiliarEqBoolExp)e).getInt1(),procIndex) == evalBoolExpr(((AuxiliarEqBoolExp)e).getInt2(),procIndex);
		}
		return false;
	}

	public CompositeNode createSuccessor(LinkedList<AuxiliarCode> assigns, int procIndex){
		CompositeNode succ = clone();
		for (AuxiliarCode c : assigns){
			if (c instanceof AuxiliarVarAssign){
				AuxiliarVarAssign assign = (AuxiliarVarAssign)c;
				AuxiliarVar var = instanciateIfParam(assign.getVar(),procIndex);
				String name = var.getName();
				Boolean value = evalBoolExpr(assign.getExp(),procIndex);
				if (checkGlobalVar(var))
					succ.getState().put(name,value);
				else
					succ.getState().put(model.getProcDecls().get(procIndex)+name,value);
			}
		}
		return succ;
	}

}