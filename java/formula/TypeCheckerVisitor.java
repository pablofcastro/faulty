package formula;

import java.util.LinkedList;
import faulty.auxiliar.*;
import faulty.*;

public class TypeCheckerVisitor implements FormulaVisitor  {
	
	 private LinkedList<String> boolVarsNames;
	 private LinkedList<String> intVarsNames;
	 private LinkedList<String> enumVarsNames;
     private Type type;
	 private LinkedList<faulty.auxiliar.Error> listError;
     private SymbolsTable sTable;
     private Program model;
     private EnumType enumTypeExpr;
     private Boolean isVariable; //0: isVariable , 1: isConstant
     private String varName;
    
	 
	 
	 /**
	  * Basic constructor
	  */
	public TypeCheckerVisitor(SymbolsTable table, Program mod){
		type = Type.UNDEFINED;
	    listError = new LinkedList<faulty.auxiliar.Error>();
	    boolVarsNames = this.obtainBoolVarNames(table);
	    intVarsNames = this.obtainIntVarNames(table);
        enumVarsNames = this.obtainEnumVarNames(table);
        this.sTable = table;
        this.model = mod;
        this.enumTypeExpr =null;
        this.isVariable = false;
        this.varName = null;
        
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
				this.type = Type.BOOL;
                this.varName = varName;
                this.isVariable=true;

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
                    // this.varName = varName;
                    // this.isVariable=true;

					this.type = Type.ERROR;
					listError.add(new faulty.auxiliar.Error("ERROR Formula:  Var: " + intVarsNames.get(j) + " of type INT - (Formula does not support INT types).-" ));
				}
				j++;
			}			
		}
        
        boolean foundE =false;
        if(!foundB && !foundI){ //Search the variable in the enumerated var names list.
            int k=0;
			//System.out.println(" visiting variable Enumerated = " + varName + " size = " + enumVarsNames.size());
            
			while(k<enumVarsNames.size() && !foundE){
                if( enumVarsNames.get(k).equals(varName)){
                    foundE =true;
					this.type = Type.ENUMERATED;
                    this.enumTypeExpr = obtainEnumTypeVar(varName);//this.model.getEnum();
                    this.varName = new String(varName);
                    this.isVariable=true;
                   
				}
				k++;
			}
		}
		
		if(!foundB && !foundI && !foundE){
            
            if(this.isVariable){ //means that previously a variable was visited, search the constant in the previous type.
                if(enumTypeExpr!=null){ //comparison of type "var== enumConst"
                    int idCons = enumTypeExpr.getConsId(varName);
                    if(idCons!= -1){ //means that varName is a constant of the last typeEnum involved in the expression.
                        this.isVariable=false;
                        this.varName = new String(varName);
                        this.type= Type.ENUMERATED;
                        
                    }else{
                        this.type = Type.ERROR;
                        listError.add(new faulty.auxiliar.Error("ERROR Formula:  Constant: " + varName + " not found in the type of expression.-" ));
                        
                    }
                }
            }else{ // First component is a Constant ( ie: "idle == proc1.state" ) or comparison of two constants(ie: "idle == idle" )
                   // By default the constant type will be the first EnumType that includes it.
                
                LinkedList<EnumType> listETypes =this.model.getEnumTypeList();
                boolean foundC =false;
                int k=0;
                //System.out.println(" Searching constant Enumerated = " + varName + " size = " + listETypes.size());
                
                while(k<listETypes.size() && !foundC){
                    int idCons = listETypes.get(k).getConsId(varName);
                    if(idCons!= -1){ //means that varName is a constant of the last typeEnum involved in the expression.
                        this.isVariable=false;
                        this.varName = varName;
                        this.enumTypeExpr =  listETypes.get(k);
                        foundC=true;
                        this.type= Type.ENUMERATED;
                    }
                    k++;
                }
                
                if (!foundC){
			        this.type = Type.ERROR;
                    listError.add(new faulty.auxiliar.Error("ERROR Formula:  Constant: " + varName + " not found in the EnumTypes list of the model.-"));
                }
        
            }
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
	public void visit(EqComparison i) {
		FormulaElement f1 = i.getExpr1();
		FormulaElement f2 = i.getExpr2();
        
        f1.accept(this);
		Type t1 =  this.getType();
        String var1 = this.varName;
        EnumType Texpr1 = this.enumTypeExpr;
        boolean isVariableExpr1 = this.isVariable;
       // System.out.println(" ANTES Type expr1  = " + Texpr1.getName());
        
        f2.accept(this);
		Type t2 =  this.getType();
        String var2 = this.varName;
        EnumType Texpr2 = this.enumTypeExpr;
		boolean isVariableExpr2 = this.isVariable;
        
        if(t2.isEnumerated() && t1.isEnumerated() ){
           
            if(Texpr1!=null && Texpr2!=null && Texpr1.getName().equals(Texpr2.getName())){
                if(var1!=null && var2!=null){
		            i.setEnumNameComparison(Texpr1.getName());
                    i.setNameExpr1(var1);
                    i.setNameExpr2(var2);
                    i.setIsVarExpr1(isVariableExpr1);
                    i.setIsVarExpr2(isVariableExpr2);
                    i.setTypeEnum(true);
                    this.type= Type.ENUMERATED;
                }
            }else{//  two differents enumerated types 
                    this.type = Type.ERROR;
                    listError.add(new faulty.auxiliar.Error("ERROR Formula: == operator: Expected the same types for Comparation.-" ));
            }
            
        
            if(t1==Type.ERROR || t2==Type.ERROR){
			    this.type= Type.ERROR;
		    }
		
	   }
       else{ // BOOL or INT
           if(t1.isBOOLEAN() && t2.isBOOLEAN() ){
               i.setNameExpr1(var1);
               i.setNameExpr2(var2);
               i.setIsVarExpr1(isVariableExpr1);
               i.setIsVarExpr2(isVariableExpr2);
               i.setTypeBool(true);
               this.type= Type.BOOL;
           
           }else{
               if(t1.isINT() && t2.isINT() ){
                   i.setNameExpr1(var1);
                   i.setNameExpr2(var2);
                   i.setIsVarExpr1(isVariableExpr1);
                   i.setIsVarExpr2(isVariableExpr2);
                   i.setTypeInt(true);
                   this.type= Type.INT;
                   
               }else{//ERROR
                   this.type = Type.ERROR;
                   listError.add(new faulty.auxiliar.Error("ERROR Formula: == operator: Expected the same types for Comparation.-" ));
               }
           
           }
       }
        
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
    
     
     @Override
     public void visit(OXW o){
         visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
     }
     
     @Override
     public void visit(OUW o){
         visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
     }
     
     @Override
     public void visit(OWX o){
         visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
     }
     
     @Override
     public void visit(OWU o){
         visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
     }
     
     @Override
     public void visit(OWW o){
         visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
     } 
     
     
	
    @Override
	public void visit(OX o) {
		visitUnaryOp(o.getExpr1());
	}
	
    @Override
	public void visit(OU o) {
		visitBinaryOp(o.getExpr1(),o.getExpr2());
		
	}
    @Override
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
    
    
    
     @Override
     public void visit(PXW o){
         visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
     }
     
     @Override
     public void visit(PUW o){
         visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
     }
     
     @Override
     public void visit(PWX o){
         visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
     }
     
     @Override
     public void visit(PWU o){
         visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
     }
     
     @Override
     public void visit(PWW o){
         visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
     }
     
     
	
    
	public void visit(PX p){
		visitUnaryOp(p.getExpr1());		}
	
	public void visit(PU p){
		visitBinaryOp(p.getExpr1(),p.getExpr2());
	}
	
	public void visit(PW p){
		visitBinaryOp(p.getExpr1(),p.getExpr2());	}
	
/*	@Override
	public void visit(Recovery r) { // CREO Q ESTA HABRA Q ELIMINARLA
		visitBinaryOp(r.getExpr1(),r.getExpr2());
	}*/
    
    @Override
	public void visit(RXX p) {
		visitBinaryOp(p.getExpr1(),p.getExpr2());
	}
    
	@Override
	public void visit(RXU p) {
		visitTernaryOp(p.getExpr1(),p.getExpr2(), p.getExpr3());
	}
    
	@Override
	public void visit(RUX p) {
		visitTernaryOp(p.getExpr1(),p.getExpr2(), p.getExpr3());
	}
    
	@Override
	public void visit(RUU p) {
		visitQuaternaryOp(p.getExpr1(),p.getExpr2(), p.getExpr3(), p.getExpr4());
	}
    
    @Override
    public void visit(RXW o){
       visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
    }
     
    @Override
    public void visit(RUW o){
       visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
    }
     
    @Override 
    public void visit(RWX o){
        visitTernaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3());
    }
     
    @Override
    public void visit(RWU o){
        visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
    }
     
    @Override
    public void visit(RWW o){
        visitQuaternaryOp(o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());
    }
     
     
	
    @Override
	public void visit(RX r) {
		visitUnaryOp(r.getExpr1());
	}
	
    @Override
	public void visit(RU r) {
		visitBinaryOp(r.getExpr1(),r.getExpr2());
		
	}
	
    @Override
	public void visit(RW r) {
		visitBinaryOp(r.getExpr1(),r.getExpr2());
	}
    
    @Override
	public void visit(AX a) {
		visitUnaryOp(a.getExpr1());
	}
	
    @Override
	public void visit(AW a) {
		visitBinaryOp(a.getExpr1(),a.getExpr2());
	}
	
    @Override
	public void visit(AU a) {
		visitBinaryOp(a.getExpr1(),a.getExpr2());
	}
	
    @Override
	public void visit(EX e) {
		visitUnaryOp(e.getExpr1());
	}
	
    @Override
	public void visit(EW e) {
		visitBinaryOp(e.getExpr1(),e.getExpr2());
    }
	
    @Override
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
	 * Creates a list with all the names of enumerated variables of the model.
	 */
	private LinkedList<String> obtainEnumVarNames(SymbolsTable symbolsTable){
		
		LinkedList<String> varNames =new LinkedList<String>();
		
		TableLevel mainLevel = symbolsTable.getLevelSymbols(0);
		LinkedList<String> processVar = mainLevel.getEnumVarNamesProcesses();
		LinkedList<String> globalVar = mainLevel.getEnumVarNames();
		
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
	 * Return the enumerated type of the var
	 * @return Enumtype
	 */
    
    private EnumType obtainEnumTypeVar(String varName){
        
        VarEnum var = this.model.getVarEnum(varName);
        if(var!=null){
            return var.getEnumType();
        }else{
            return null;
        }
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
