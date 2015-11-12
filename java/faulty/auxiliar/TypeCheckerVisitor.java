package faulty.auxiliar;

//import mc.BDDModel;
//import net.sf.javabdd.BDD;
//import faulty.*;
import java.util.*;




public class TypeCheckerVisitor implements AuxiliarFaultyVisitor{
	
	private Type type;
	private LinkedList<Error> listError;
	private SymbolsTable table;
	
	/**
	 * Basic constructor
	 */
	public TypeCheckerVisitor(){
		type = Type.UNDEFINED;
	    listError = new LinkedList<Error>();
	    table = new SymbolsTable();
	}
	
	
	public void visit(AuxiliarProgram a){
		
		
		a.globalVars.accept(this);
		Type globalT = this.getType();
		
		a.channels.accept(this);
		Type chT = this.getType();
		
		
		a.process.accept(this);
		Type procT= this.getType();
		
		a.mainProgram.accept(this);
		Type mainT = this.getType();
		
		if(globalT!= Type.ERROR && chT!=Type.ERROR  && procT!=Type.ERROR && mainT!= Type.ERROR ){
			type = chT;
		}
		else{
			type =Type.ERROR;
		}
		
		
	}
    
	public void visit(AuxiliarGlobalVarCollection a){
		
		LinkedList<AuxiliarVar> boolVars = a.boolVars;
		LinkedList<AuxiliarVar> intVars = a.intVars;
		AuxiliarVar var;
		
		boolean correct =true;
		
		for(int i=0;i<boolVars.size();i++){
			var =boolVars.get(i);
			var.accept(this);
			//table.addSymbol(var);
			if (this.getType() == Type.ERROR){
				correct=false;
			}
		}
		for(int i=0;i<intVars.size();i++){
			var =intVars.get(i);
			var.accept(this);
			//table.addSymbol(var);
			if (this.getType() == Type.ERROR){
				correct=false;
			}
		}
		
		if (correct){
			this.type = Type.INT; 	// by default type global vars is int, otherwise ERROR.
		}
		else{
			this.type=Type.ERROR;
		}
	    
	
    }
	
	public void visit(AuxiliarProcessCollection a){
		LinkedList<AuxiliarProcess> processList = a.getProcessList();
	
		boolean correctType = true; 
		
		for(int i=0; i<processList.size(); i++){
			processList.get(i).accept(this);
			if (this.getType()==Type.ERROR){
				correctType = false;
			}
		}
		
		if (correctType){
			this.type = Type.INT; 	// by default type correct process list is int, otherwise ERROR.
		}
		else{
			this.type=Type.ERROR;
		}
	}
	public void visit(AuxiliarChannelCollection a){
        LinkedList<AuxiliarChannel> boolChannelList = a.getBoolChannels();
        LinkedList<AuxiliarChannel> intChannelList = a.getIntChannels();
		
		Type chT;
		boolean correctType = true;
		
		
		for(int i=0; i<boolChannelList.size(); i++){
			boolChannelList.get(i).accept(this);
			chT = this.getType();
			if ( chT == Type.ERROR){
				correctType=false;
			}
			//table.addSymbol(boolChannelList.get(i));
		}

		for(int i=0; i<intChannelList.size(); i++){
			intChannelList.get(i).accept(this);
			chT = this.getType();
			if ( chT == Type.ERROR){
				correctType=false;
			}
			//table.addSymbol(intChannelList.get(i));
		}	
		
		if(correctType){
			type = Type.INT;// by defaault type correct channel list is int
		}
		else{
			type= Type.ERROR;
		}
		
	}
	
	public void visit(AuxiliarProcess a){
		LinkedList<AuxiliarBranch> branches = a.getBranches();
		LinkedList<AuxiliarChannel> usedChannels = a.getChannelIds();
		AuxiliarExpression ini=a.getInitialCond();
		AuxiliarExpression norm=a.getNormativeCond();
		LinkedList<AuxiliarVar> varB = a.getVarBool();
		LinkedList<AuxiliarVar> varI = a.getVarInt();
		boolean correct =true;
		table.incrementLevel();
		
		
		//BOOL variables
		for(int i=0;i<varB.size();i++){
			varB.get(i).accept(this);
			Type varT= this.getType();
			if ( varT == Type.ERROR){
				correct=false;
			}
		}
		
		//INT variables
		for(int i=0;i<varI.size();i++){
			varI.get(i).accept(this);
			Type varT= this.getType();
			if ( varT == Type.ERROR){
				correct=false;
			}
		}
		
		//initial Condition
		ini.accept(this);
		Type iniT = this.getType();
		if ( iniT == Type.ERROR){
			correct = false;
		}
		
		// Normative condition
		norm.accept(this);
		Type normT = this.getType();
		if ( normT == Type.ERROR){
			correct = false;
		}
		
		// USED channels
		for(int i=0;i<usedChannels.size();i++){
			usedChannels.get(i).accept(this);
			Type varC= this.getType();
			if ( varC == Type.ERROR){
				correct=false;
			}
		}
		
		// Branches of te process
		for(int i=0;i<branches.size();i++){
			branches.get(i).accept(this);
			Type varBr= this.getType();
			if ( varBr == Type.ERROR){
				correct=false;
			}
		}
		
		if(!correct){
			//System.out.println("---------- SOME ERROR IN PROCESS" + a.getName());
			this.type =Type.ERROR;
		}
		else{
			this.type = Type.INT;
		}
		
		TableLevel initialLevel = table.getLevelSymbols(0);
		if (initialLevel!=null){ //add the declared process to the fisrt level
			initialLevel.addProcess(a);
		}
		
		table.decrementLevel();
		
	}
	
	public void visit(AuxiliarChannel a){
		Type origT = a.getType();
		
		
		if(a.isDeclaration()){

			//System.out.println("DECLARATION" +a.getName() + "--- Size:" + a.getSize() + "-- Type :" + a.getType().toString() );
			if(origT.isBOOLEAN() || origT.isINT() ){ // Complete declaration channel 
				
				//System.out.println("---COMPLETE DECLARATION ------" +a.getName() + "--- Size:" + a.getSize() + "-- Type :" + a.getType().toString() );
				boolean result= table.addSymbol(a);
			    if(a.getSize() > 0){
			        if (result == true){
			        	//System.out.println("---- ADDED in LEVEL : " + table.getLevel() +" CHANNEL "+ table.searchChannel(a.getName(),table.getLevel()).getName() + "--- Size:" + table.searchChannel(a.getName(),table.getLevel()).getSize() + "-- Type :" + table.searchChannel(a.getName(),table.getLevel()).getType().toString() );
			  	        type = a.getType();
			        }
			        else{
				        type = Type.ERROR;
		    	        listError.add(new Error("ErrorType - Channel Declaration " + a.getName() + " already exist."));
			        }
			    }
			    else{
			    	type = Type.ERROR;
		    	    listError.add(new Error("ErrorType - Channel Declaration  " + a.getName() + "  - Size channel should be an integer greater than 0."));
			    }
			}
			else{ // Declaration channel in a especific process (USE), needs to search in the first level an complete the channel 
				//System.out.println("---- SEARCHING in LEVEL : " +table.getLevel() +" CHANNEL "+ a.getName() );
				AuxiliarChannel ch = table.searchChannel(a.getName(), 0);
				if(ch!=null){
					//System.out.println("+++++++++++++++++++ > added in level:" +table.getLevel() +" Channel: "+ ch.getName() + "type" + ch.getType().toString() + " -- size :" + ch.getSize());
					a=ch;
					table.addSymbol(ch);
					type = ch.getType();
				}
				else{
					type = Type.ERROR;
		    	    listError.add(new Error("ErrorType - in USE clause - channel " + a.getName() + "  not found."));
				}
			}
		}
		else{ //use of a channel in a expression

			//System.out.println("Use of channel , table level : " + table.getLevel()+" - :" +a.getName() + "--- Size:" + a.getSize() + "-- Type :" + a.getType().toString() );
			int i = table.getLevel();
			AuxiliarChannel channel = table.searchChannel(a.getName(), table.getLevel()); //search only un the current level
			if (channel != null){
			    //System.out.println("............Found channel" + channel.getName() +" --size : " +channel.getSize() +" --type :" +channel.getType().toString());
				type = channel.getType();
				a =channel;
				//System.out.println("---------------------------> FINAL channel" + a.getName() +" --size : " +a.getSize() +" --type :" +a.getType().toString());
					
			}
			else{
				//System.out.println("Use of Channel .. type ERROR" + a.getName() +" --size : " +a.getSize() +" --type :" +a.getType().toString());
				type = Type.ERROR;
				listError.add(new Error("ErrorType - Channel " + a.getName() + " not found"));
			}
		}
	}
	
	public void visit(AuxiliarBranch a){
		LinkedList<AuxiliarCode> aList = a.assignList;
		AuxiliarExpression expr = a.guard;
		Type exprT;
		expr.accept(this);
		exprT = this.getType();
		
		//Type assigListT = Type.BOOL; //By default all branches are BOOL type 
		boolean correct =true;
		for(int i=0;i<aList.size();i++){
			aList.get(i).accept(this);
			if ( this.getType() == Type.ERROR){
				correct= false;
			}
		}
		
		
		if(exprT!=Type.ERROR && correct ){
			this.type = Type.BOOL;
		}
		else{
			
			System.out.println("ERROR in BRANCH");
			this.type =Type.ERROR;
		}
	}
	
	
	public void visit(AuxiliarChanAssign a){
		
		AuxiliarChannel var = a.chanName;
		var.accept(this);
		Type chT = this.getType();
		
		AuxiliarExpression expr = a.exp;
		expr.accept(this);
		Type exprT = this.getType();
		
		if( (chT.isBOOLEAN() && exprT.isBOOLEAN()) || (chT.isINT() && exprT.isINT() ) ){
		    type = chT;
		}
	    else{
	    	System.out.println("ERROR PUT OPERATION");
		    type = Type.ERROR;
		    listError.add(new Error("ErrorType: PUT operation, channel :" + a.chanName.getName() ));
		}
	}
	
	public void visit(AuxiliarVarAssign a){
		AuxiliarVar var = a.var;
		var.accept(this);
		Type varT = this.getType();
		
		AuxiliarExpression expr = a.exp;
	    expr.accept(this);
		Type exprT = this.getType();
		
		if( (varT.isBOOLEAN() && exprT.isBOOLEAN()) || (varT.isINT() && exprT.isINT() ) ){
			type = varT;
		}
		else{
		    type = Type.ERROR;
		    listError.add(new Error("ErrorType: Assignation of var :" + a.var.getName()));
		}	
	}

	public void visit(AuxiliarVar a){
		Type origT = a.getType();
		
		//if(origT.isBOOLEAN() && origT.isINT()){ //declaration
		if(a.isDeclaration()){
		    table.addSymbol(a);
			type=origT;
		}
		else{ //type is undefined or error
			if (origT.isUndefined() ){
				
			    AuxiliarVar var=null;
			    int i= table.getLevel();
			    
			    boolean found=false;
			    //search the variable in the symbol table
			    while(i >= 0 && !found){
			    	var = table.searchVar(a.getName(), i);
			    	if(var!=null){
				    	//System.out.println("encontro vble" + var.getName() + "level table : " + i + " - type: " +var.getType().toString() );
				        found = true;
					    type = var.getType();
					    a.setType(var.getType());
				        a = var;
				    }
				    else{
					    type = Type.ERROR;
			    	    listError.add(new Error("ErrorType - Variable " + a.getName() + " not found"));
			        }
				    i--;
			    }
	        }
			else{// type is ERROR
			    type = Type.ERROR;
			}
		}
		
		//System.out.println("----------------> FINAL vble" + a.getName() + " -- type: " + a.getType().toString() );
        
}
	

	
	public void visit(AuxiliarAndBoolExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isBOOLEAN() && typeExp2.isBOOLEAN() ){
	    	type= Type.BOOL;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Conjunction operation: Expected types Boolean."));
	    }
	}
	public void visit(AuxiliarBiimpBoolExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isBOOLEAN() && typeExp2.isBOOLEAN() ){
	    	type= Type.BOOL;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Biimplication operation: Expected types Boolean."));
	    }
	}
	
	public void visit(AuxiliarOrBoolExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isBOOLEAN() && typeExp2.isBOOLEAN() ){
	    	type= Type.BOOL;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Disjunction operation: Expected types Boolean."));
	    }
		
		
	}
	public void visit(AuxiliarNegBoolExp a){
		
		a.exp.accept(this);
		Type typeExp = this.getType();
	    if(typeExp.isBOOLEAN()  ){
	    	type= Type.BOOL;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Negation operation: Expected types Boolean."));
	    }

		
	}
	public void visit(AuxiliarGreaterBoolExp a){
		a.int1.accept(this);
		Type typeExp1 = this.getType();
		a.int2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	type= Type.BOOL;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Greater operator: Expected types Integer."));
	    }
		
	}
	public void visit(AuxiliarLessBoolExp a){
		a.int1.accept(this);
		Type typeExp1 = this.getType();
		a.int2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	type= Type.BOOL;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Less operator: Expected types Integer."));
	    }
	}
	public void visit(AuxiliarEqBoolExp a){
		a.int1.accept(this);
		Type typeExp1 = this.getType();
		a.int2.accept(this);
		Type typeExp2 = this.getType();
		//System.out.println("operation type First Op " + typeExp1.toString() + "type second op " +  typeExp2.toString() );
		
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	//System.out.println(" ------> INT first op" + typeExp1.toString() + "type second op " +  typeExp2.toString() );
    		
	    	type= Type.BOOL;
	    }
	    else{
	    	if(typeExp1.isBOOLEAN() && typeExp2.isBOOLEAN() ){
	    		//System.out.println(" ------> BOOLEAN first op" + typeExp1.toString() + "type second op " +  typeExp2.toString() );
	    		a.setCreateBiimp(true); //marks to create the biimplication for boolean expression 
		    	type= Type.BOOL;
		    }
		    else{
		    	//System.out.println(" ------> ERROR first op" + typeExp1.toString() + "type second op " +  typeExp2.toString() );
	    		
		    	type = Type.ERROR;
		    	listError.add(new Error("ErrorType - == operator: Expected the same types for Comparation."));
		    }	 
	    }
	}
	public void visit(AuxiliarConsBoolExp a){
        this.type = Type.BOOL;
	}
	public void visit(AuxiliarNegIntExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	type= Type.INT;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Subtraction operation: Expected types Integer."));
	    }
	}
	public void visit(AuxiliarSumIntExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	type= Type.INT;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Sum operation: Expected types Integer."));
	    }
		
	}
	public void visit(AuxiliarDivIntExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	type= Type.INT;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Division operation: Expected types Integer."));
	    }
	}
	public void visit(AuxiliarMultIntExp a){
		a.exp1.accept(this);
		Type typeExp1 = this.getType();
		a.exp2.accept(this);
		Type typeExp2 = this.getType();
	    if(typeExp1.isINT() && typeExp2.isINT() ){
	    	type= Type.INT;
	    }
	    else{
	    	type = Type.ERROR;
	    	listError.add(new Error("ErrorType - Multiplication operation: Expected types Integer."));
	    }
	}
	
	public void visit(AuxiliarConsIntExp a){
		this.type = Type.INT;
	}
	
	public void visit(AuxiliarChanAccess a){
		AuxiliarChannel chan = a.getChannel();
	    chan.accept(this);
		Type chT = this.getType();
		a.setChannel( table.searchChannel(chan.getName(), table.getLevel() ));
		if(chT.isBOOLEAN() || chT.isINT()){
			this.type= chT;
		   /* System.out.println("Chan Access - complete channel information :  TYPE : " + chT.toString());
		    System.out.println("--------------------------------");
		    System.out.println("Chan name : "+ a.getChannel().getName() );
		    System.out.println("Chan size : "+ a.getChannel().getSize() );
		    System.out.println("Chan type: " + a.getChannel().getType().toString() );
		    System.out.println("--------------------------------"); */
		}		
		else{
			//System.out.println("ERROR GET OPERATION-------------------------");
			type = Type.ERROR;
	    	listError.add(new Error("ErrorType - GET operation - Channel " + a.channel.getName() + " not found"));
	    }
		
	}
	
	public void visit(AuxiliarMain a){

		LinkedList<AuxiliarProcessDecl> pDecl = a.processDecl;
		LinkedList<String> pInvk = a.processInvk;
		Type procT;
		type = Type.INT; //By default all correct process are INT type, otherwise ERROR.
		
		for(int i=0; i< pDecl.size();i++){
			pDecl.get(i).accept(this);
			procT = this.getType();
			if(procT == Type.ERROR){
				type = procT;
				listError.add(new Error("ErrorType - Main Program - Process Declarations Error."));
			}
		}
		
		if(!a.isCorrectInvk()){
			type = Type.ERROR;
			listError.add(new Error("ErrorType - Main Program in process Invocation - Process instance not Found ."));
		}
		
		TableLevel initialLevel = table.getLevelSymbols(0);
		AuxiliarProcess proc = null;
		for(int i=0; i<pInvk.size(); i++ ){
			proc = initialLevel.getProcess(a.getProcessType(pInvk.get(i)));
			if(proc !=null){
				proc.addInstanceName(pInvk.get(i)); //add the instance name to the auxiliarProcess for later generation of instances.
			}
		}
		
	}
	
	public void visit(AuxiliarProcessDecl a){
		TableLevel level = table.getLevelSymbols(0);
		AuxiliarProcess proc = level.getProcess(a.getType());
        if(proc!=null){
        	type = Type.INT;
        }
        else{
        	type = Type.ERROR;
			listError.add(new Error("ErrorType - Main Program in process Declaration - Process name not Found ."));        	
        }
	}
	
	/**
	 * Return the type involved in the object.
	 * @return type 
	 */
    public Type getType(){
    	return type;
    }
    
    /**
	 * Return the SymbolsTable involved in the program
	 * @return type 
	 */
    public SymbolsTable getSymbolTable(){
    	return table;
    }

    /**
	 * Return the List of errors
	 * @return type 
	 */
    public LinkedList<Error> getErrorList(){
    	return listError;
    }

}
