package faulty.auxiliar;

import mc.*;
import net.sf.javabdd.BDD;
import faulty.*;
import java.util.*;


public class BuilderVisitor implements AuxiliarFaultyVisitor{
	
	
	private static BDDModel model; // the model
	private static Program program;
	private LinkedList<faulty.Process> processDecl;
	private LinkedList<Channel> globalChannels; 
	private LinkedList<VarBool> globalBoolVars;
	private LinkedList<VarInt> globalIntVars;
	private LinkedList<VarEnum> globalEnumVars;
    private LinkedList<EnumType> enumeratedTypes;
    private EnumType currentEnumType;
    private faulty.Process process;
	private Branch branch;
	private Var var;
	private Code code;
	private Expression expr;
	private Channel channel;
	private Type type; //vble used to distinguish the type in expressions, variables and channels
	private boolean isDeclaration; 
	private SymbolsTable table;
	
	/**
	 * Basic constructor
	 */
	public BuilderVisitor(SymbolsTable table){
		this.model = new BDDModel();
	    this.table = table;
	    this.processDecl=new LinkedList<faulty.Process>();
	    this.globalChannels = new LinkedList<Channel>();
	    this.globalBoolVars = new LinkedList<VarBool>();
	    this.globalIntVars = new LinkedList<VarInt>();
        this.globalEnumVars = new LinkedList<VarEnum>();
        this.enumeratedTypes = new LinkedList<EnumType>();
        this.currentEnumType=null;
	    this.program = null;
	    this.process=null;
	    this.var =null;
	    this.code =null;
	    this.expr =null;
	    this.channel = null;
	    this.branch=null;
	    this.type = Type.UNDEFINED;
	    this.isDeclaration=false;
	}
	
	
	public void visit(AuxiliarProgram a){
		
		this.createProgram(a);
		
        // enumerated types
        for(int i=0;i < a.enumTypes.size();i++){
            a.enumTypes.get(i).accept(this);
            EnumType eType = (EnumType)this.getEnumType();
            this.program.addEnumType(eType);
            //this.enumeratedTypes.add(eType);
			this.var =null;
			this.expr=null;
        }
            
        
		a.globalVars.accept(this);
		a.channels.accept(this);
		a.process.accept(this);
		this.program = this.getProgram();
		a.mainProgram.accept(this);
		this.program = this.getProgram();
        //System.out.println("-------------------------------------------------------------------------------------  ");
        //System.out.println("--  " + this.program.toString());
        //System.out.println("-------------------------------------------------------------------------------------  ");
        
	}
    
    public void visit(AuxiliarEnumType a){
        EnumType eType = new EnumType(a.getName(), a.getSize(), a.getNumVars(), program);
        // enumerated types
        for(int i=0;i<a.getSize();i++){
            String constant = a.getCons(i);
            if (constant!=null){
                eType.addCons(constant,i);
            }
        }
            
        this.enumeratedTypes.add(eType);
        this.currentEnumType = eType;
    }
        
	
	public void visit(AuxiliarGlobalVarCollection a){
		LinkedList<AuxiliarVar> boolVars = a.getBoolVars();
		LinkedList<AuxiliarVar> intVars = a.getIntVars();
        LinkedList<AuxiliarVar> enumVars = a.getEnumVars();
        
		
		this.isDeclaration =true;
		
		for(int i=0;i<boolVars.size();i++){
			boolVars.get(i).accept(this);
			this.program.addBoolVar((VarBool)this.getVar());
			this.globalBoolVars.add((VarBool)this.getVar());
			this.var =null;
			this.expr=null;
		}
		
		for(int i=0;i<intVars.size();i++){
			intVars.get(i).accept(this);
			this.program.addIntVar((VarInt)this.getVar());
			this.globalIntVars.add((VarInt)this.getVar());
			this.var =null;
			this.expr=null;
		}
        
        for(int i=0;i<enumVars.size();i++){
			enumVars.get(i).accept(this);
			this.program.addEnumVar((VarEnum)this.getVar());
			this.globalEnumVars.add((VarEnum)this.getVar());
			this.var =null;
			this.expr=null;
		}
        
		this.isDeclaration =false;
	}
	
	public void visit(AuxiliarProcessCollection a){
		
		LinkedList<AuxiliarProcess> pList = a.getProcessList();
		
		for(int i=0;i<pList.size();i++){
			pList.get(i).accept(this);
		}
		
	}
	public void visit(AuxiliarChannelCollection a){
		LinkedList<AuxiliarChannel> intChannels = a.getIntChannels();
		LinkedList<AuxiliarChannel> boolChannels = a.getBoolChannels();
        LinkedList<AuxiliarChannel> enumChannels = a.getEnumChannels();
		
		this.isDeclaration =true;
		
		for(int i=0;i<intChannels.size();i++){
			intChannels.get(i).accept(this);
			this.program.addIntChannel((IntChannel)this.getChannel());
			this.globalChannels.add(this.getChannel());
		}
		
		for(int i=0;i<boolChannels.size();i++){
			boolChannels.get(i).accept(this);
			this.program.addBoolChannel((BoolChannel)this.getChannel());
			this.globalChannels.add(this.getChannel());
		}
        
        /*for(int i=0;i<enumChannels.size();i++){ // to be implemeted in future versions
			enumChannels.get(i).accept(this);
			this.program.addEnumChannel((EnumChannel)this.getChannel());
			this.globalChannels.add(this.getChannel());
		}*/
        
		this.type = Type.UNDEFINED;
		this.channel = null;
		this.isDeclaration =false;

	}
	
	public void visit(AuxiliarProcess a){
		LinkedList<AuxiliarChannel> channelIds= a.getChannelIds();
		String processName= a.getName();
		LinkedList<AuxiliarVar> intVars = a.getVarInt();
		LinkedList<AuxiliarVar> boolVars = a.getVarBool();
        LinkedList<AuxiliarVar> enumVars = a.getVarEnum();
        
        
		LinkedList<AuxiliarBranch> branches = a.getBranches();
		AuxiliarExpression normCondition = a.getNormativeCond(); 
		AuxiliarExpression initialCond=a.getInitialCond();
		//Obtain all process information
		int numberVar = a.getNumVar();
		int numberChan = a.getNumChannelsUsed();
		int numberBranch = a.getNumBranches();
		int numberInst = a.getNumInstances();
		String firstInstanceName = a.getProcessInstanceName(0); //return the name of the first instance
        LinkedList<AuxiliarParam> declParameters = a.getParamList();
        LinkedList<AuxiliarExpression> invkParameters = a.getInvkParametersList(firstInstanceName); //return the list of invocation parameters of this intance.
        
        
  		
		if(numberChan>0){ // process use some channel
		    this.process = new faulty.Process(program, processName,firstInstanceName,numberVar,numberChan,numberBranch,numberInst,this.model,false);
		}
		else{
			 this.process = new faulty.Process(program, processName,firstInstanceName,numberVar,numberChan,numberBranch,numberInst,this.model,true);
		}
		     
		this.isDeclaration=false;
		// Add channels to the process
		for(int i=0;i<channelIds.size();i++){
			Channel globCh=  this.searchGlobalChannel(channelIds.get(i).getName()); //add to the process the used Channel
			this.process.addChannel(globCh);
			this.channel=null;
		}
		
		this.isDeclaration=true;
		// Add Int Vars to the process
		for(int i=0;i<intVars.size();i++){
			intVars.get(i).accept(this);
			this.process.addVarInt((VarInt)this.getVar());
			this.var =null;
			this.expr =null;
		}
		
		// Add Bool Vars to the process
		for(int i=0;i<boolVars.size();i++){
			boolVars.get(i).accept(this);
			//System.out.println("              --> Local var: " + boolVars.get(i).getName() );
		    this.process.addVarBool((VarBool)this.getVar());
			this.var =null;
			this.expr=null;
		}
        
        
        // Add Enum Vars to the process
		for(int i=0;i<enumVars.size();i++){
			enumVars.get(i).accept(this);
			this.process.addVarEnum((VarEnum)this.getVar());
			this.var =null;
			this.expr=null;
		}
		
		
		
		//Add the globals Bool vars to the process
		for(int i=0;i<this.globalBoolVars.size();i++){
			//System.out.println("              --> GLOBAL var: " + globalBoolVars.get(i).getName());
		    this.process.addGlobalVarBool((VarBool)this.globalBoolVars.get(i));
		}
		
		//Add the globals Int vars to the process
	    for(int i=0;i<this.globalIntVars.size();i++){
		    this.process.addGlobalVarInt((VarInt)this.globalIntVars.get(i));
		}
        
        //Add the globals Enum vars to the process
	    for(int i=0;i<this.globalEnumVars.size();i++){
		    this.process.addGlobalVarEnum((VarEnum)this.globalEnumVars.get(i));
		}
        
        
        
        if(declParameters.size()>0){ //process use parameters
            //Add the boolean parameters of the process
            LinkedList<ParamBool>  boolParamList = createBoolParamList(declParameters,invkParameters);
            this.process.setBoolParams(boolParamList);
            
            //Add the int parameters of the process
            LinkedList<ParamInt>  intParamList = createIntParamList(declParameters,invkParameters);
            this.process.setIntParams(intParamList);
            
            
            //Add the int parameters of the process
            LinkedList<ParamEnum>  enumParamList = createEnumParamList(declParameters,invkParameters);
            this.process.setEnumParams(enumParamList);

        }
        
        
		// Add Branches to the process
		this.isDeclaration=false;
		for(int i=0;i<branches.size();i++){
			branches.get(i).accept(this);
			this.process.addBranch(this.getBranch());
			this.branch =null;
		}
		
		initialCond.accept(this);
		this.process.setInitialCond((BoolExp) this.getExpr());
		this.expr=null;
		
		normCondition.accept(this);
		this.process.setNormativeCond((BoolExp) this.getExpr());
		this.expr=null;
		
		this.program.addProcess(this.process); //add the first intance of the process;
		
        
		// add all the instances declarated in the model.
    	faulty.Process dupProcess=null;
		for (int i=1; i< numberInst; i++){
			if(declParameters.size()>0){ //process use parameters
                //Add the boolean parameters of the process
                LinkedList<ParamBool>  boolParamList = createBoolParamList(declParameters,a.getInvkParametersList(a.getProcessInstanceName(i)));
                //Add the int parameters of the process
                LinkedList<ParamInt>  intParamList = createIntParamList(declParameters,a.getInvkParametersList(a.getProcessInstanceName(i)));
                
                dupProcess = this.process.duplicate(a.getProcessInstanceName(i),boolParamList, intParamList); 
            }
			else{
				dupProcess = this.process.duplicate(a.getProcessInstanceName(i)); 
			}
            this.program.addProcess(dupProcess);
            
            
		}
        
		this.process=null;
	}
	
	public void visit(AuxiliarChannel a){
	    
	    Channel chan;
	    Type chT = a.getType();
	    if (this.isDeclaration) { //needs to add this channel to the program
            if( chT.isINT() ){
            	chan = new IntChannel(a.getName(),a.getSize(),this.model);
		        this.type= Type.INT;
		    }
		    else{
		        chan =new BoolChannel(a.getName(),a.getSize(),this.model);
		        this.type = Type.BOOL;
		    }
	    }
	    else{//channel is used in an expression 
	    	 chT = searchTypeChannel(a.getName()); 
	    	 chan = searchChannel(a.getName(), this.process);
	    	 if( chT.isINT() ){
	    		 this.type= Type.INT;
	    	 }
			 else{
			     this.type = Type.BOOL;
			 }
	    }
	    this.channel = chan;
	   
	}
	
	public void visit(AuxiliarBranch a){
		 AuxiliarExpression guard = a.getGuard(); // the guard of the branch
		 LinkedList<AuxiliarCode> assignList =a.getAssignList();
		 String instName = this.process.getInstName(); // a name of the instance to which this branch belongs
		 boolean isFaulty = a.getIsFaulty(); // it says if the branch is a faulty one or not
		 
		 guard.accept(this);
		 BoolExp guardBr = (BoolExp)this.getExpr(); // the guard of the branch
		 //Code codeBr; // the code of the branch
         LinkedList<Code> listAssignations = new LinkedList<Code>();
		 for(int i=0; i < assignList.size();i++ ){
			 assignList.get(i).accept(this);
			 listAssignations.add(this.getCode());
		 }
		 
		 ListAssign list = new ListAssign(listAssignations);
		 this.branch = new Branch(instName ,guardBr, list, isFaulty,this.process);
	}
	
	/**
	 * Visit a PUT operation
	 * @param a
	 */
	public void visit(AuxiliarChanAssign a){
		AuxiliarExpression auxExpr= a.exp;
		AuxiliarChannel auxChan = a.chanName;
		
		this.isDeclaration=false;
		auxChan.accept(this);
		Channel ch = this.getChannel();
        Type channelT = this.getType(); 
		this.channel =null;
		
		auxExpr.accept(this);
		Expression expr = this.getExpr();
		Type exprT = this.getType();
		this.expr =null;
		

		if(channelT.isBOOLEAN() && exprT.isBOOLEAN()){
		    this.code = new BoolChanAssign((BoolChannel)ch,(BoolExp)expr);
	    }
		
		if(channelT.isINT() && exprT.isINT()){
		    this.code = new IntChanAssign((IntChannel)ch,(IntExp)expr);
		}
	}
	
	public void visit(AuxiliarVarAssign a){
		AuxiliarExpression auxExpr= a.exp;
		AuxiliarVar auxVar = a.var;
		
		auxVar.accept(this);
		Var variable = this.getVar();
		auxExpr.accept(this);
		Expression expr = this.getExpr();
		
		this.code = new VarAssign(variable,expr);
	}

	public void visit(AuxiliarVar a){
		
        
        String name =a.getName();
		Type varT = a.getType();
		Var variable=null;
		String instName;
        ConsEnum consEnumVar=null;
        
		// obtain the instance name to create or search the variable.
		if(this.process!=null){ //means that is a local vble
		    instName = this.process.getInstName();
		}
		else{
			instName =new String("global");
		}
        
		if(this.isDeclaration){ //global or local declaration
            //System.out.println("VAR Declaration ---------------------------------------  ");
            
			if(varT.isINT()){
                //System.out.println("VAR INT --- name= "+name+" - instName= "+instName);
                variable = new VarInt(name,instName, this.getModel() );
			}
			if(varT.isBOOLEAN()){
                //System.out.println("VAR BOOLEAN --- name= "+name+" - instName= "+instName);
                variable = new VarBool(name, instName, this.getModel() );			
		    }
            
            if(varT.isEnumerated()){
                //System.out.println("VAR ENUMERATED --- name= "+name+" - instName= "+instName+" - EnumName= "+ a.getEnumName());
			    variable = new VarEnum(name, instName, this.getModel(), this.getProgram(), a.getEnumName() );
		    }
			
			this.var = variable;
            //System.out.println(" --------------------------------------------------------------  ");
            
		}else{ //Variable used in an Expression
           // System.out.println("VAR use ---------------------------------------  ");
           // System.out.println("VAR name=" + name + " - type: " + varT.toString() + " enumName: " + a.getEnumName());
            
            
            //search as local or global variable 
			variable = this.searchVar(name, varT ,this.process);
            
            if (variable!=null){
                this.var = variable;
            }
            else{//enumerated constant
                //System.out.println("enumerated constant" + name + " - type: " + varT.toString() + " enumName: " + a.getEnumName());
                
                //variable = new VarEnum(this.getModel(), this.getProgram(), a.getEnumName());
                //((VarEnum)variable).addEnumValue(a.getEnumType().getConsId(name));
                
                consEnumVar = new ConsEnum(name, a.getEnumName(), this.getProgram());

            }
            
            
			//copy the variable to be used in an expression.
			if(varT.isINT()){
                this.expr= (IntExp)variable;
            }
			if(varT.isBOOLEAN()){
            	this.expr= (BoolExp)variable;
            }
            if(varT.isEnumerated()){
                
                if (variable ==null){ // Enumerated constanst
            	this.expr= (EnumExp)consEnumVar;
                    //System.out.println(" ENUMERATED constant --- = ");
                }else{
                    this.expr = (EnumExp)variable;
                    //System.out.println(" ENUMERATED var --- = ");
                    
                }
                
            }
            //System.out.println(" --------------------------------------------------------------  ");
            

		}
		
    }
	

	
	public void visit(AuxiliarAndBoolExp a){
		a.exp1.accept(this);
		BoolExp op1 = (BoolExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		BoolExp op2 = (BoolExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new AndBoolExp(op1,op2);
        this.type = Type.BOOL;

	
	}
	public void visit(AuxiliarBiimpBoolExp a){
		a.exp1.accept(this);
		BoolExp op1 = (BoolExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		BoolExp op2 = (BoolExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new BiimpBoolExp(op1,op2);
        this.type = Type.BOOL;
	}
	
	public void visit(AuxiliarOrBoolExp a){
		a.exp1.accept(this);
		BoolExp op1 = (BoolExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		BoolExp op2 = (BoolExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new OrBoolExp(op1,op2);
        this.type = Type.BOOL;
		
	}
	public void visit(AuxiliarNegBoolExp a){
		a.exp.accept(this);
		BoolExp op1 = (BoolExp)this.getExpr();
		Type op1T =this.getType();
		
		this.expr =  new NegBoolExp(op1);
        this.type = Type.BOOL;

		
	}
	public void visit(AuxiliarGreaterBoolExp a){
		a.int1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		
		a.int2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new GreaterBoolExp(op1,op2);
        this.type = Type.BOOL;
		
	}
	public void visit(AuxiliarLessBoolExp a){
		a.int1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		
		a.int2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new LessBoolExp(op1,op2);
        this.type = Type.BOOL;
	}
	public void visit(AuxiliarEqBoolExp a){
		boolean createBiimp = a.getCreateBiimp();
        boolean isEnumerated = a.isEnumerated();
		
		if(!createBiimp){ // both operands are of type INT or enumerated
		    
            if(!isEnumerated){
                 a.int1.accept(this);
            
		         IntExp op1 = (IntExp)this.getExpr();
		         Type op1T =this.getType();
		
		         a.int2.accept(this);
		         IntExp op2 = (IntExp)this.getExpr();
		         Type op2T =this.getType();
    
                 this.expr =  new EqBoolExp(op1,op2);
                 this.type = Type.BOOL;
            }
            else{ //both operands are enumerated
                a.int1.accept(this);
                
                EnumExp op1 = (EnumExp)this.getExpr();
                Type op1T =this.getType();
                
                a.int2.accept(this);
                EnumExp op2 = (EnumExp)this.getExpr();
                Type op2T =this.getType();
                
                
                
                EnumType typeEq = this.getProgram().getEnum(a.getEnumType());
                this.expr =  new EqEnumExp(op1,op2,typeEq);
                this.type = Type.BOOL;
            }
		}
		else{ // both operands are of type BOOL , so needs to create an equivalence.
			
			AuxiliarBiimpBoolExp biimp = new AuxiliarBiimpBoolExp(a.int1,a.int2);
			biimp.accept(this);
			this.type = Type.BOOL;
			this.expr = this.getExpr();
		}
	}
	
	public void visit(AuxiliarConsBoolExp a){
		this.expr= new ConsBoolExp(a.value);
		this.type = Type.BOOL;
	}
	
	public void visit(AuxiliarNegIntExp a){
		a.exp1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new NegIntExp(op1,op2,this.getModel());
        this.type = Type.INT;
	}
	
	public void visit(AuxiliarSumIntExp a){
		
		a.exp1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new SumIntExp(op1,op2,this.getModel());
        this.type = Type.INT;
		
	}
	public void visit(AuxiliarDivIntExp a){
		a.exp1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		this.expr=null;
		
		a.exp2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new DivIntExp(op1,op2,this.getModel());
        this.type = Type.INT;

		
	}
	public void visit(AuxiliarMultIntExp a){
		a.exp1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new MultIntExp(op1,op2,this.getModel());
        this.type = Type.INT;

	
	}
	
	public void visit(AuxiliarConsIntExp a){
		this.expr= new ConsIntExp(a.value.intValue());
		this.type = Type.INT;
	}
	
	/**
	 * GET operation
	 * @param a
	 */
	public void visit(AuxiliarChanAccess a){
		this.isDeclaration=false;
	    AuxiliarChannel chan = a.getChannel();
	    chan.accept(this);
	    Channel channel = this.getChannel();
	    this.channel=null;
	    Type channelType = this.getType();
	    
	    if(channelType.isINT()){
	    	this.expr = new IntChanAccess((IntChannel)channel);
	    	this.type = Type.INT;
	    }
	    
	    if(channelType.isBOOLEAN()){
	    	this.expr = new BoolChanAccess((BoolChannel)channel);
	    	this.type = Type.BOOL;
	    }

	    
	}
	
	public void visit(AuxiliarMain a){


	}
	
	public void visit(AuxiliarProcessDecl a){
		
	}
    
    
    public void visit(AuxiliarParam a){
		
	}
    
    
    public void visit(AuxiliarInvkProcess a){
		
	}
    
	
	/**
	 * Return the type involved in the object.
	 * @return type 
	 */
    private void createProgram(AuxiliarProgram a){
    	
    	AuxiliarMain main = a.mainProgram; //needs to know the total variables involved in the program
    	int numberIntVars=0;
    	int numberBoolVars=0;
        //int numberEnumVars=0;
    	int numberBoolChannels= a.channels.getNumBoolChannels(); 
    	int numberIntChannels=  a.channels.getNumIntChannels();
    	int numberEnumChannels=  a.channels.getNumEnumChannels();
        int maxLengthChannels = a.channels.getMaxLengthChannels();
        int maxEnumSize = a.getMaxEnumSize();
        
    	AuxiliarGlobalVarCollection gVars = a.globalVars;
    	
    	AuxiliarProcessCollection processCollection  =a.process;
    	
    	//obtain el total number of global variables of the program
    	numberBoolVars = gVars.getTotalGlobalBoolVars();
    	numberIntVars = gVars.getTotalGlobalIntVars();
    	//numberEnumVars = gVars.getTotalGlobalEnumVars();
        
        LinkedList<AuxiliarEnumType> auxEnumList = a.getAuxiliarEnumList();
        LinkedList<EnumType>  temporalEnumList = new LinkedList<EnumType>(); //Temporal List used to calculates the correct size of the new program
        EnumType eType;
        AuxiliarEnumType auxET;
        for(int i = 0; i<auxEnumList.size();i++){
            auxET = auxEnumList.get(i);
            eType = new EnumType(auxET.getName(), auxET.getSize(), auxET.getNumVars());
            temporalEnumList.add(eType);
        
        }
        
    	LinkedList<AuxiliarInvkProcess> listProcessIntances = main.getProcessInvk();
    	AuxiliarProcess proc;
    	for(int i=0; i < listProcessIntances.size();i++ ){
    		proc= processCollection.getProcess(main.getProcessType(listProcessIntances.get(i).getInstanceName())); //search the declarated process object
    		if(proc!=null){
    			numberIntVars += proc.getNumVarInt();
    			numberBoolVars += proc.getNumVarBool();
    			//numberEnumVars += proc.getNumVarEnumerated();
    		}
    	}
    	
    	this.program = new Program (4,numberIntVars,numberBoolVars, temporalEnumList, numberBoolChannels, numberIntChannels, maxLengthChannels, this.model, true);  	
    }
    
    /**
	 * Return the SymbolsTable involved in the program
	 * @return type 
	 */
    public SymbolsTable getSymbolTable(){
    	return table;
    }

    public Program getProgram(){
    	return this.program;
    	
    }
    
    private Channel getChannel(){
    	return this.channel;
    	
    }
    
    private BDDModel getModel(){
    	return this.model;
    	
    }
    
    private Branch getBranch(){
    	return this.branch;
    }
    
    private LinkedList<faulty.Process> getProcessDecl(){
    	return this.processDecl;
    }

    /**
     * 
     * @return Return the instance of Var involved.
     */
    private Var getVar(){
    	return this.var;
    }
    
    private Code getCode(){
    	return this.code;
    }
    
    private Type getType(){
    	return this.type;
    }
    
    private EnumType getEnumType(){
    	return this.currentEnumType;
    }
    
    
    
    private Expression getExpr(){
    	return this.expr;
    }
    
    /**
     * Precondition: proc !=null
     * @param name Name of the channel  
     * @param proc Process where will be searh the process definition.
     * @return Return the channel defined in the process, null otherwise.
     */
    private Channel searchChannel(String name, faulty.Process proc){
    	LinkedList<Channel> chanList = proc.getChannels();
    	Channel ch =null;
    	int i = 0;
    	boolean found =false;
    	while( i < chanList.size() && !found){
    		if(chanList.get(i).getName().equals(name)){
    			found =true;
    			ch = chanList.get(i); 
    		}
    	    i++;
    	}
    	return ch;
    }
    
    /**
     * @param name Name of the channel  
     * @return Return the type of channel defined in the process, Type.UNDEFINED otherwise.
     */
    private Type searchTypeChannel(String name){
    	 TableLevel globalDecls = table.getLevelSymbols(0); 
    	 AuxiliarChannel chan =null;
    	 
    	 if(globalDecls.existChannel(name)){
    		 chan = globalDecls.getChannel(name);
    	 }
    	
    	 if(chan!=null){
    		 return  chan.getType();
    	 }else{
    		 return Type.UNDEFINED;
    	 }	 
    }
    
    /**
     * 
     * @param name Name of the channel  
     * @return Return the channel defined in the program, null otherwise.
     */
    private Channel searchGlobalChannel(String name){
    	LinkedList<Channel> chanList = this.globalChannels;
    	Channel ch =null;
    	int i = 0;
    	boolean found =false;
    	while( i < chanList.size() && !found){
    		if(chanList.get(i).getName().equals(name)){
    			found =true;
    			ch = chanList.get(i); 
    		}
    	    i++;
    	}
    	return ch;
    }



    
    
    /**
     * Precondition: var !=null
     * @param name Name of the var  
     * @param proc Process where will be searh the variable definition.
     * @return Return the variable defined in the process, null otherwise.
     */
    private Var searchVar(String name, Type t, faulty.Process proc){
    	
       Var variable =null;
		
       if (t.isBOOLEAN()){
    		LinkedList<VarBool> globalVarBoolList = proc.getGlobalBoolVars();
    		LinkedList<VarBool> varBoolList = proc.getBoolVars();
    		variable =searchVarBoolName(name,varBoolList);
    		if(variable ==null){//global variable
    			variable = searchVarBoolName(name,globalVarBoolList);
                if(variable ==null){//parameter
                    LinkedList<ParamBool> declParameters = proc.getBoolPars();
                    variable = searchParBoolName(name, declParameters);
                }
            }
    	}
    	
        if (t.isINT()){
        	LinkedList<VarInt> globalVarIntList = proc.getGlobalIntVars();
        	LinkedList<VarInt> varIntList = proc.getIntVars();
        	
        	variable =searchVarIntName(name,varIntList);
    		if(variable ==null){//global variable
    			variable = searchVarIntName(name,globalVarIntList);
                if(variable ==null){//parameter
                    LinkedList<ParamInt> declParameters = proc.getIntPars();
                    variable = searchParIntName(name, declParameters);
                }
            }
        
    	}
        
        if (t.isEnumerated()){
        	LinkedList<VarEnum> globalVarEnumList = proc.getGlobalEnumVars();
        	LinkedList<VarEnum> varEnumList = proc.getEnumVars();
        	
            variable =searchVarEnumName(name,varEnumList);
    		if(variable ==null){//global variable
    			variable = searchVarEnumName(name,globalVarEnumList);
                if(variable ==null){//parameter
                    LinkedList<ParamEnum> declParameters = proc.getEnumPars();
                    variable = searchParEnumName(name, declParameters);
                }
            }
            
    	}
        
        return variable;
    }
    
    
    private Var searchVarIntName(String name, LinkedList<VarInt> list){
    	Var variable =null;
    	int i = 0;
    	boolean found =false;
    	while( i < list.size() && !found){
    		if(list.get(i).getName().equals(name)){
    			found =true;
    			variable = list.get(i); 
    		}
    	    i++;
    	}
    	return variable;
    	
    }
    
    
    private Var searchVarBoolName(String name, LinkedList<VarBool> list){
    	Var variable =null;
    	int i = 0;
    	boolean found =false;
    	while( i < list.size() && !found){
    		if(list.get(i).getName().equals(name)){
    			found =true;
    			variable = list.get(i); 
    		}
    	    i++;
    	}
    	return variable;
    	
    }
    
    private Var searchVarEnumName(String name, LinkedList<VarEnum> list){
    	Var variable =null;
    	int i = 0;
    	boolean found =false;
    	while( i < list.size() && !found){
    		if(list.get(i).getName().equals(name)){
    			found =true;
    			variable = list.get(i);
    		}
    	    i++;
    	}
    	return variable;
    	
    }
    
    
    
    /**
     * @param name: Name of the parameter
     * @param declPar: List of parameters used in the declaration
     * @return Returns the parameter with the name received, if it exist, null otherwise.
     */
    
    private Var searchParIntName(String name, LinkedList<ParamInt> declPar){
    	Var ref =null;
    	int i = 0;
    	boolean found =false;
        ParamInt  par = null;
    	while( i < declPar.size() && !found){
            par = declPar.get(i);
    		if(par.getName().equals(name)){
    			found =true;
    			//ref = par.getReference(); //return the reference
                ref = par; //return the reference
                
    		}
    	    i++;
    	}
        return ref;
    	
    }
    
    
    /**
     * @param name: Name of the parameter
     * @param declPar: List of parameters used in the declaration
     * @return Returns the parameter with the name received, if it exist, null otherwise.
     */
    private Var searchParBoolName(String name, LinkedList<ParamBool> declPar){
        Var ref =null;
    	int i = 0;
    	boolean found =false;
        ParamBool  par = null;
    	while( i < declPar.size() && !found){
            par = declPar.get(i);
    		if(par.getName().equals(name)){
    			found =true;
    			//ref = par.getReference(); //return the reference
                ref = par; //return the reference
                
    		}
    	    i++;
    	}
        
       return ref;
    	
    }
    
    /**
     * @param name: Name of the parameter
     * @param declPar: List of parameters used in the declaration
     * @return Returns the parameter with the name received, if it exist, null otherwise.
     */
    private Var searchParEnumName(String name, LinkedList<ParamEnum> declPar){
        Var ref =null;
    	int i = 0;
    	boolean found =false;
        ParamEnum  par = null;
    	while( i < declPar.size() && !found){
            par = declPar.get(i);
    		if(par.getName().equals(name)){
    			found =true;
    			//ref = par.getReference(); //return the reference
                ref = par; //return the reference
                
    		}
    	    i++;
    	}
        
        return ref;
    	
    }
    
    
    
    
    /**
     * Precondition: dParam.size = iParam.size
     * @param dParam List of parameters used in the declaration
     * @param iParam List of parameters used in the invocation
     * @return Returns the list of boolean parameters used in this process instance. 
     */
    private LinkedList<ParamBool>  createBoolParamList(LinkedList<AuxiliarParam> dParam , LinkedList<AuxiliarExpression> iParam){
        
        LinkedList<ParamBool> pBool = new LinkedList<ParamBool>();
        
		for(int i=0;i<dParam.size();i++){
			AuxiliarParam declPar = dParam.get(i);
			AuxiliarVar invkPar= (AuxiliarVar)iParam.get(i);
            
            if(declPar.getType().isBOOLEAN()){
                VarBool ref = (VarBool)this.searchVarBoolName(invkPar.getName(),this.globalBoolVars); //return the reference to the global var with that name.
                pBool.add(new ParamBool(declPar.getDeclarationName(), ref));
            }
        }
        
        
        return pBool;
        
    
    }
    
    
    /**
     * Precondition: dParam.size = iParam.size
     * @param dParam List of parameters used in process declaration
     * @param iParam List of parameters used in process invocation
     * @return Return the list of integer parameters used in this process instance.
     */
    private LinkedList<ParamInt>  createIntParamList(LinkedList<AuxiliarParam> dParam , LinkedList<AuxiliarExpression> iParam){
       
        
        LinkedList<ParamInt> pInt = new LinkedList<ParamInt>();
        
		for(int i=0;i<dParam.size();i++){
			AuxiliarParam declPar = dParam.get(i);
            AuxiliarVar invkPar= (AuxiliarVar)iParam.get(i);
            
            if(declPar.getType().isINT()){
                VarInt ref = (VarInt)this.searchVarIntName(invkPar.getName(),this.globalIntVars); //return the reference to the global var with that name.
                pInt.add(new ParamInt(declPar.getDeclarationName(), ref));
            }
        }
        return pInt;
        
        
    }

    
    /**
     * Precondition: dParam.size = iParam.size
     * @param dParam List of parameters used in process declaration
     * @param iParam List of parameters used in process invocation
     * @return Return the list of enumerates parameters used in this process instance.
     */
    private LinkedList<ParamEnum>  createEnumParamList(LinkedList<AuxiliarParam> dParam , LinkedList<AuxiliarExpression> iParam){
        
        
        LinkedList<ParamEnum> pEnum = new LinkedList<ParamEnum>();
        
		for(int i=0;i<dParam.size();i++){
			AuxiliarParam declPar = dParam.get(i);
            AuxiliarVar invkPar= (AuxiliarVar)iParam.get(i);
            
            if(declPar.getType().isEnumerated()){
                VarEnum ref = (VarEnum)this.searchVarEnumName(invkPar.getName(),this.globalEnumVars); //return the reference to the global var with that name.
                pEnum.add(new ParamEnum(declPar.getDeclarationName(), ref));
            }
        }
        return pEnum;
        
        
    }

    
    
}
