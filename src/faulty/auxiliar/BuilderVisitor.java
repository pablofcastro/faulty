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
		
		a.globalVars.accept(this);
		a.channels.accept(this);
		a.process.accept(this);
		this.program = this.getProgram();
		a.mainProgram.accept(this);
		this.program = this.getProgram();
	}
	
	public void visit(AuxiliarGlobalVarCollection a){
		LinkedList<AuxiliarVar> boolVars = a.getBoolVars();
		LinkedList<AuxiliarVar> intVars = a.getIntVars();
		
		this.isDeclaration =true;
		
		for(int i=0;i<boolVars.size();i++){
			//System.out.println("---- var name: " + boolVars.get(i).getName() + " - tpe : " + boolVars.get(i).getType().toString());
			boolVars.get(i).accept(this);
			this.program.addBoolVar((VarBool)this.getVar());
			this.globalBoolVars.add((VarBool)this.getVar());
			this.var =null;
			this.expr=null;
		}
		
		for(int i=0;i<intVars.size();i++){
			//System.out.println("---- var name: " + intVars.get(i).getName() + " - tpe : " + intVars.get(i).getType().toString());
			
			intVars.get(i).accept(this);
			this.program.addIntVar((VarInt)this.getVar());
			this.globalIntVars.add((VarInt)this.getVar());
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
		this.type = Type.UNDEFINED;
		this.channel = null;
		this.isDeclaration =false;

	}
	
	public void visit(AuxiliarProcess a){
		LinkedList<AuxiliarChannel> channelIds= a.getChannelIds();
		String processName= a.getName();
		LinkedList<AuxiliarVar> intVars = a.getVarInt();
		LinkedList<AuxiliarVar> boolVars = a.getVarBool(); 
		LinkedList<AuxiliarBranch> branches = a.getBranches();
		AuxiliarExpression normCondition = a.getNormativeCond(); 
		AuxiliarExpression initialCond=a.getInitialCond();
		//Obtain all process information
		int numberVar = a.getNumVar();
		int numberChan = a.getNumChannelsUsed();
		int numberBranch = a.getNumBranches();
		int numberInst = a.getNumInstances();
		String firstInstanceName = a.getProcessInstanceName(0); //return the name of the first instance
  		
		System.out.println("\n/////////////////////////////////////////////  PROCESS : " +processName + " instance : "+ firstInstanceName);
		System.out.println("                               - number Variables : " +numberVar );
		System.out.println("                               - number Channels : " +numberChan );
		System.out.println("                               - number Branch : " +numberBranch );
		System.out.println("                               - number instances : " +numberInst );
		
		if(numberChan>0){ // process use some channel
		    this.process = new faulty.Process(processName,firstInstanceName,numberVar,numberChan,numberBranch,numberInst,this.model,false);
		}
		else{
			 this.process = new faulty.Process(processName,firstInstanceName,numberVar,numberChan,numberBranch,numberInst,this.model,true);
		}
		//public Process(String name, String instName, int numberVar, int numberChan, int numberBranch, int numberInst, BDDModel model, boolean blocking){
		     
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
			System.out.println("              --> Local var: " + boolVars.get(i).getName() );
		    this.process.addVarBool((VarBool)this.getVar());
			this.var =null;
			this.expr=null;
		}
		
		
		//Add the globals Bool vars to the process
		for(int i=0;i<this.globalBoolVars.size();i++){
			System.out.println("              --> GLOBAL var: " + globalBoolVars.get(i).getName());
		    this.process.addGlobalVarBool((VarBool)this.globalBoolVars.get(i));
		}
		
		//Add the globals Int vars to the process
	    for(int i=0;i<this.globalIntVars.size();i++){
		    this.process.addGlobalVarInt((VarInt)this.globalIntVars.get(i));
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
    	faulty.Process dupProcess;
		for (int i = 1; i< numberInst; i++){
			dupProcess = this.process.duplicate(a.getProcessInstanceName(i));
			this.program.addProcess(dupProcess); 
		}
		
		this.process=null;
		System.out.println("////////////////////////////////////////////////// END PROCESS ///////////////// ");
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
		 Code codeBr; // the code of the branch
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
		Type channelT = this.getType(); 
		
		this.isDeclaration=false;
		auxChan.accept(this);
		Channel ch = this.getChannel();
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

		// obtain the instance name to create or search the variable.
		if(this.process!=null){ //means that is a local vble
		    instName = this.process.getInstName();
		}
		else{
			instName =new String("global");
		}
		
		if(this.isDeclaration){ //global or local declaration
			
			if(varT.isINT()){
				variable = new VarInt(name,instName, this.getModel() );
			}
			if(varT.isBOOLEAN()){
			    variable = new VarBool(name, instName, this.getModel() );			
		    }
			this.var = variable;
		}else{ //Variable used in an Expression 
			
			//search as local or global variable 
			variable = this.searchVar(name, varT ,this.process);
			
			this.var = variable;
			//copy the variable to be used in an expression.
			if(varT.isINT()){
				this.expr= (IntExp)variable;

			}
			if(varT.isBOOLEAN()){
				this.expr= (BoolExp)variable;
		    }

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
		
		if(!createBiimp){ // both operands are of type INT
		    a.int1.accept(this);
		    IntExp op1 = (IntExp)this.getExpr();
		    Type op1T =this.getType();
		
		    a.int2.accept(this);
		    IntExp op2 = (IntExp)this.getExpr();
		    Type op2T =this.getType();
		
		    this.expr =  new LessBoolExp(op1,op2);
            this.type = Type.BOOL;
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
		
		this.expr =  new NegIntExp(op1,op2,this.getModel());
        this.type = Type.INT;

		
	}
	public void visit(AuxiliarMultIntExp a){
		a.exp1.accept(this);
		IntExp op1 = (IntExp)this.getExpr();
		Type op1T =this.getType();
		
		a.exp2.accept(this);
		IntExp op2 = (IntExp)this.getExpr();
		Type op2T =this.getType();
		
		this.expr =  new NegIntExp(op1,op2,this.getModel());
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
	
	/**
	 * Return the type involved in the object.
	 * @return type 
	 */
    private void createProgram(AuxiliarProgram a){
    	
    	AuxiliarMain main = a.mainProgram; //needs to know the total variables involved in the program
    	int numberIntVars=0;
    	int numberBoolVars=0;
    	int numberBoolChannels= a.channels.getNumBoolChannels(); 
    	int numberIntChannels=  a.channels.getNumIntChannels();
    	int maxLengthChannels = a.channels.getMaxLengthChannels();
    	AuxiliarGlobalVarCollection gVars = a.globalVars;
    	
    	AuxiliarProcessCollection processCollection  =a.process;
    	
    	//obtain el total number of global variables of the program
    	numberBoolVars = gVars.getTotalGlobalBoolVars();
    	numberIntVars = gVars.getTotalGlobalIntVars();
    	
    	LinkedList<String> listProcessIntances = main.getProcessInvk();
    	AuxiliarProcess proc;
    	for(int i=0; i < listProcessIntances.size();i++ ){
    		proc= processCollection.getProcess(main.getProcessType(listProcessIntances.get(i))); //search the declarated process object 
    		if(proc!=null){
    			numberIntVars += proc.getNumVarInt();
    			numberBoolVars += proc.getNumVarBool();
    		}
    	}
    	numberBoolVars=numberBoolVars;
    	
    	System.out.println("Program Information: ");
        System.out.println("                    -- numberIntVars: " + numberIntVars);
        System.out.println("                    -- numberBoolVars: " + numberBoolVars );
        System.out.println("                    -- numberBoolChannels: " + numberBoolChannels );
        System.out.println("                    -- numberIntChannels: " + numberIntChannels );
        System.out.println("                    -- maxLengthChannels: " + maxLengthChannels );
    	
    	this.program = new Program (4,numberIntVars,numberBoolVars,numberBoolChannels,numberIntChannels, maxLengthChannels, this.model, true);
    		
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
     * @return Return the channel defined in the process, null otherwise.
     */
    private Var searchVar(String name, Type t, faulty.Process proc){
    	
    	
		//System.out.println("   ...... Searching Var: " + name + " - Type: " + t.toString() + " in process: " + proc.getName() + " instance: " + proc.getInstName());
        
		Var variable =null;
		
    	if (t.isBOOLEAN()){
    		LinkedList<VarBool> globalVarBoolList = proc.getGlobalBoolVars();
    		LinkedList<VarBool> varBoolList = proc.getBoolVars();
    		variable =searchVarBoolName(name,varBoolList);
    		if(variable ==null){
    			variable = searchVarBoolName(name,globalVarBoolList);
    		}
    	}
    	
        if (t.isINT()){
        	LinkedList<VarInt> globalVarIntList = proc.getGlobalIntVars();
        	LinkedList<VarInt> varIntList = proc.getIntVars();
        	
        	variable =searchVarIntName(name,varIntList);
    		if(variable ==null){
    			variable = searchVarIntName(name,globalVarIntList);
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

    
}
