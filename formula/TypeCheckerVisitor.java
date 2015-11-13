package formula;

import java.util.LinkedList;
import faulty.auxiliar.*;


public class TypeCheckerVisitor implements FormulaVisitor  {
	
	 private LinkedList<String> boolVarsNames;
	 private LinkedList<String> intVarsNames;
	 private Type type;
	 private LinkedList<faulty.auxiliar.Error> listError;
	 
	 
	 /**
	  * Basic constructor
	  */
	public TypeCheckerVisitor(SymbolsTable sTable){
		type = Type.UNDEFINED;
	    listError = new LinkedList<faulty.auxiliar.Error>();
	    boolVarsNames = this.obtainBoolVarNames(sTable);
	    intVarsNames = this.obtainIntVarNames(sTable);
	}

	@Override
	public void visit(Variable v) {
		String varName = v.getName();
		
		int i=0;
		boolean foundB =false;
		this.type = Type.ERROR;
		
		//Search the variable in the boolean var names list.
		while(i<boolVarsNames.size() && !foundB){
			if( boolVarsNames.get(i).equals(varName)){
				foundB =true;
				//this.type = faulty.auxiliar.Type.BOOL;
				this.type = Type.BOOL;
			}
			i++;
		}

		boolean foundI =false;
		
		if(!foundB){ //Search the variable in the integer var names list.
            int j=0;
			
			while(j<intVarsNames.size() && !foundI){
				if( intVarsNames.get(j).equals(varName)){
					foundI =true;
					//this.type = faulty.auxiliar.Type.BOOL;
					this.type = Type.ERROR;
					listError.add(new faulty.auxiliar.Error("ERROR Formula:  Var: " + intVarsNames.get(j) + " of type INT - (Formula does not support INT types).-" ));
				}
				j++;
			}			
		}
		
		if(!foundB && !foundI){
			this.type = Type.ERROR;
			listError.add(new faulty.auxiliar.Error("ERROR Formula:  Var: " + varName + " not found in the model.-" ));
		
		}
		
	}
    
	@Override
	public void visit(Constant c) {
		this.type = Type.BOOL;
		
	}
    
	@Override
	public void visit(Negation n) {
		visitUnaryOp(n.getExpr1());
	}
    
	@Override
	public void visit(Implication i) {
		FormulaElement f1 = i.getExpr1();
		FormulaElement f2 = i.getExpr2();
		visitBinaryOp(f1,f2);
		
	}
    
	@Override
	public void visit(Conjunction c) {
		FormulaElement f1 = c.getExpr1();
		FormulaElement f2 = c.getExpr2();
		visitBinaryOp(f1,f2);
		
	}
    
	@Override
	public void visit(Disjunction d) {
		FormulaElement f1 = d.getExpr1();
		FormulaElement f2 = d.getExpr2();
		visitBinaryOp(f1,f2);
		
	}
    
	@Override
	public void visit(Next n) {
		visitUnaryOp (n.getExpr1());
	}
    
	@Override
	public void visit(Until u) {
		visitBinaryOp(u.getExpr1(),u.getExpr2());
	}
    
    
    
	@Override
	public void visit(Weak u) {
		visitBinaryOp(u.getExpr1(),u.getExpr2());
	}
    
    
	@Override
	public void visit(OXX o) {
		visitBinaryOp(o.getExpr1(),o.getExpr2());
	}
    
	@Override
	public void visit(OXU o) {
		visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
		
	}
    
	@Override
	public void visit(OUX o) {
		visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
		
	}
    
	@Override
	public void visit(OUU o) {
		visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
		
	}
	
	public void visit(OX o) {
		visitUnaryOp(o.getExpr1());
	}
	
	public void visit(OU o) {
		visitBinaryOp(o.getExpr1(),o.getExpr2());
		
	}
    
	public void visit(OW o) {
		visitBinaryOp(o.getExpr1(),o.getExpr2());
	}
	
	@Override
	public void visit(PXX p) {
		visitBinaryOp(p.getExpr1(),p.getExpr2());
	}
    
	@Override
	public void visit(PXU p) {
		visitTernaryOp(p.getExpr1(),p.getExpr2(), p.getExpr3());
		
	}
    
	@Override
	public void visit(PUX p) {
		visitTernaryOp(p.getExpr1(),p.getExpr2(), p.getExpr3());
		
	}
    
	@Override
	public void visit(PUU p) {
		visitQuaternaryOp(p.getExpr1(),p.getExpr2(), p.getExpr3(), p.getExpr4());
		
	}
    
	public void visit(PX p){
		visitUnaryOp(p.getExpr1());		}
	
	public void visit(PU p){
		visitBinaryOp(p.getExpr1(),p.getExpr2());
	}
	
	public void visit(PW p){
		visitBinaryOp(p.getExpr1(),p.getExpr2());	}
	
	@Override
	public void visit(Recovery r) {
		visitBinaryOp(r.getExpr1(),r.getExpr2());
	}
	
	public void visit(RX r) {
		visitUnaryOp(r.getExpr1());
	}
	
	public void visit(RU r) {
		visitBinaryOp(r.getExpr1(),r.getExpr2());
		
	}
	
	public void visit(RW r) {
		visitBinaryOp(r.getExpr1(),r.getExpr2());
	}
    
	public void visit(AX a) {
		visitUnaryOp(a.getExpr1());
	}
	
	public void visit(AW a) {
		visitBinaryOp(a.getExpr1(),a.getExpr2());
	}
	
	public void visit(AU a) {
		visitBinaryOp(a.getExpr1(),a.getExpr2());
	}
	
	public void visit(EX e) {
		visitUnaryOp(e.getExpr1());
	}
	
	public void visit(EW e) {
		visitBinaryOp(e.getExpr1(),e.getExpr2());
    }
	
	public void visit(EU e) {
		visitBinaryOp(e.getExpr1(),e.getExpr2());
	}
	
	
	@Override
	public void visit(AXX a) {
		visitBinaryOp(a.getExpr1(),a.getExpr2());
		
	}
    
	@Override
	public void visit(AXU a) {
		visitTernaryOp(a.getExpr1(),a.getExpr2(), a.getExpr3());
		
	}
    
	@Override
	public void visit(AUX a) {
		visitTernaryOp(a.getExpr1(),a.getExpr2(), a.getExpr3());
		
	}
    
	@Override
	public void visit(AUU a) {
		visitQuaternaryOp(a.getExpr1(),a.getExpr2(), a.getExpr3(), a.getExpr4());
	}
    
	@Override
	public void visit(EXX e) {
		visitBinaryOp(e.getExpr1(),e.getExpr2());
		
	}
    
	@Override
	public void visit(EXU e) {
		visitTernaryOp(e.getExpr1(),e.getExpr2(), e.getExpr3());
		
	}
    
	@Override
	public void visit(EUX e) {
		visitTernaryOp(e.getExpr1(),e.getExpr2(), e.getExpr3());
		
	}
    
	@Override
	public void visit(EUU e) {
		visitQuaternaryOp(e.getExpr1(),e.getExpr2(), e.getExpr3(), e.getExpr4());
	}
	
	
	/**
	 * Creates a list with all variables names of the model.
	 */
	private LinkedList<String> obtainBoolVarNames(SymbolsTable symbolsTable){
		
		LinkedList<String> varNames =new LinkedList<String>();
		
		TableLevel mainLevel = symbolsTable.getLevelSymbols(0);
		LinkedList<String> processVar = mainLevel.getBoolVarNamesProcesses();
		LinkedList<String> globalVar = mainLevel.getBoolVarNames();
		
		//  Add the prefix "global." for each global variable of the main level.
		for(int i=0; i<globalVar.size();i++){
 		    String nameG = new String ( "global." + globalVar.get(i));
 		    varNames.add(nameG);
		}
		
		for(int i=0; i<processVar.size();i++){
 		    varNames.add(processVar.get(i));
		}
        
		return varNames;
		
	}	
	
	
	/**
	 * Creates a list with all the names of integer variables of the model.
	 */
	private LinkedList<String> obtainIntVarNames(SymbolsTable symbolsTable){
		
		LinkedList<String> varNames =new LinkedList<String>();
		
		TableLevel mainLevel = symbolsTable.getLevelSymbols(0);
		LinkedList<String> processVar = mainLevel.getIntVarNamesProcesses();
		LinkedList<String> globalVar = mainLevel.getIntVarNames();
		
		//  Add the prefix "global." for each global variable of the main level.
		for(int i=0; i<globalVar.size();i++){
 		    String nameG = new String ( "global." + globalVar.get(i));
 		    varNames.add(nameG);
		}
		
		for(int i=0; i<processVar.size();i++){
 		    varNames.add(processVar.get(i));
		}
        
		return varNames;
		
	}
	
	/**
	 * Return the type involved in the object.
	 * @return type 
	 */
    public Type getType(){
    	return this.type;
    }
    
    public LinkedList<faulty.auxiliar.Error> getErrorList(){
    	return this.listError;
    }


    private void visitUnaryOp(FormulaElement f1){
		
		f1.accept(this);
		Type t1 =  this.getType();
		if(t1==Type.ERROR ){
			this.type= Type.ERROR;
		}
	}

    private void visitBinaryOp(FormulaElement f1, FormulaElement f2 ){
		
		f1.accept(this);
		Type t1 =  this.getType();
		f2.accept(this);
		Type t2 =  this.getType();
		
		if(t1==Type.ERROR || t2==Type.ERROR){
			this.type= Type.ERROR;
		}
	}
	
		
	private void visitTernaryOp(FormulaElement f1, FormulaElement f2, FormulaElement f3){
		f1.accept(this);
		Type t1 =  this.getType();
		f2.accept(this);
		Type t2 =  this.getType();
		f3.accept(this);
		Type t3 =  this.getType();
		
		if(t1==Type.ERROR || t2==Type.ERROR || t3==Type.ERROR){
			this.type= Type.ERROR;
		}
	}
	
	
	private void visitQuaternaryOp(FormulaElement f1, FormulaElement f2, FormulaElement f3, FormulaElement f4){
		f1.accept(this);
		Type t1 =  this.getType();
		f2.accept(this);
		Type t2 =  this.getType();
		f3.accept(this);
		Type t3 =  this.getType();
		f4.accept(this);
		Type t4 =  this.getType();
		
		if(t1==Type.ERROR || t2==Type.ERROR || t3==Type.ERROR || t4==Type.ERROR){
			this.type= Type.ERROR;
		}
    }
	
		
	


}
