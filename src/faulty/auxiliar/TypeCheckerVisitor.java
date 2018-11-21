package faulty.auxiliar;

//import mc.BDDModel;
//import net.sf.javabdd.BDD;
//import faulty.*;
import java.util.*;




public class TypeCheckerVisitor implements AuxiliarFaultyVisitor{
	
	private Type type;
	private LinkedList<Error> listError;
	private SymbolsTable table;
    private AuxiliarEnumType currentEnumeratedType;
    
	/**
	 * Basic constructor
	 */
	public TypeCheckerVisitor(){
		type = Type.UNDEFINED;
	    listError = new LinkedList<Error>();
	    table = new SymbolsTable();
        currentEnumeratedType=null;
	}
	
	
	public void visit(AuxiliarProgram a){
		
		 
		//Type eType = Type.ERROR;
		Type eType = Type.UNDEFINED;
		AuxiliarEnumType enumT;
        for(int i=0;i< a.enumTypes.size();i++){
            
			enumT =a.enumTypes.get(i);
			enumT.accept(this);
		    eType= this.getType();
        }
        
       
        
		a.globalVars.accept(this);
		Type globalT = this.getType();
		
		a.channels.accept(this);
		Type chT = this.getType();
						
		a.process.accept(this);		
		Type procT= this.getType();
		
		a.mainProgram.accept(this);
		Type mainT = this.getType();
		
		if(globalT!= Type.ERROR && chT!=Type.ERROR  && procT!=Type.ERROR && mainT!= Type.ERROR && eType!=Type.ERROR){
			type = chT;
            
            calculatesEnumVars(a); // Calculates and set in each enum type the number of instances of this type of the program.
            
            
		}
		else{
			type =Type.ERROR;			
		}
		
	}
    
    public void visit(AuxiliarEnumType a){
        
        boolean added =table.addSymbol(a);
        //System.out.println("----------------> enum declaration " + a.getName() + " const 0 "+ a.getCons(0) + " - size = "+ a.getSize());
        
        if(!added){
            type = Type.ERROR;
            listError.add(new Error("ErrorType - The Enumerated Type " + a.getName() + ", already exist."));
        }
        
    }
    
	public void visit(AuxiliarGlobalVarCollection a){
		
		LinkedList<AuxiliarVar> boolVars = a.getBoolVars();
		LinkedList<AuxiliarVar> intVars = a.getIntVars();
        LinkedList<AuxiliarVar> enumVars = a.getEnumVars();
		
		AuxiliarVar var;
		
		boolean correct =true;
		
		for(int i=0;i<boolVars.size();i++){
			var =boolVars.get(i);
			var.accept(this);
			if (this.getType() == Type.ERROR){
				correct=false;
			}
		}
		for(int i=0;i<intVars.size();i++){
			var =intVars.get(i);
			var.accept(this);
			if (this.getType() == Type.ERROR){
				correct=false;
			}
		}
        
        for(int i=0;i<enumVars.size();i++){
			var =enumVars.get(i);
            var.accept(this);
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
		LinkedList<AuxiliarChannel> enumChannelList = a.getEnumChannels();
		
		Type chT;
		boolean correctType = true;
		
		
		for(int i=0; i<boolChannelList.size(); i++){
			boolChannelList.get(i).accept(this);
			chT = this.getType();
			if ( chT == Type.ERROR){
				correctType=false;
			}
        }

		for(int i=0; i<intChannelList.size(); i++){
			intChannelList.get(i).accept(this);
			chT = this.getType();
			if ( chT == Type.ERROR){
				correctType=false;
			}
        }
        
        
        for(int i=0; i<enumChannelList.size(); i++){
			enumChannelList.get(i).accept(this);
			chT = this.getType();
			if ( chT == Type.ERROR){
				correctType=false;
			}
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
        LinkedList<AuxiliarVar> varE = a.getVarEnum();
        
        LinkedList<AuxiliarParam> paramList = a.getParamList();

		boolean correct =true;
		table.incrementLevel();
        
        
        //Parameters
		for(int i=0;i<paramList.size();i++){
			paramList.get(i).accept(this);
			Type parT= this.getType();
			if ( parT == Type.ERROR){
				correct=false;
			}
		}
		
		
		
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
        
        //ENUM variables
		for(int i=0;i<varE.size();i++){
			varE.get(i).accept(this);
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
			if(origT.isBOOLEAN() || origT.isINT() || origT.isEnumerated()){ // Complete declaration channel
                
                if(origT.isEnumerated()){ // Indicates an error
                    type = Type.ERROR;
                    listError.add(new Error("FUTURE FEATURE -  Current version does not support Channels of enumerated types."));
			        
                }
				
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
					boolean added = table.addSymbol(ch);
                    if(!added){
                        type = Type.ERROR;
		    	        listError.add(new Error("ErrorType - Channel Declaration " + a.getName() + " already exist."));
                    }
					else{
                        type = ch.getType();
                    }
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
			this.type =Type.ERROR;
		}
	}
	
	
	public void visit(AuxiliarChanAssign a){
		
		AuxiliarChannel channel = a.chanName;
		channel.accept(this);
		Type chT = this.getType();
		
		AuxiliarExpression expr = a.exp;
        if(chT.isEnumerated()){ //if is enumerated search in the first level de types declarated.
            
            TableLevel initialLevel = table.getLevelSymbols(0);
            currentEnumeratedType = initialLevel.getEnumeratedType(channel.getEnumName());//set the enumerated type information of the current assignation
            
        }
        
		expr.accept(this);
		Type exprT = this.getType();
		
		if( (chT.isBOOLEAN() && exprT.isBOOLEAN()) || (chT.isINT() && exprT.isINT() ) || (chT.isEnumerated() && exprT.isEnumerated()) ){
			type = chT;
		}
	    else{
	    	type = Type.ERROR;
		    listError.add(new Error("ErrorType: PUT operation, channel :" + a.chanName.getName() ));
		}
	}
	
	public void visit(AuxiliarVarAssign a){
		AuxiliarVar var = a.var;
		var.accept(this);
		Type varT = this.getType();
        String enumExp1 = null;
        String enumExp2 = null;
        //System.out.println(" -------------------Visiting AuxiliarVarAssign------ Var= " + var.getName() + "-- Type:"  + var.getEnumName() + " --------- \n");
        
        
        Type exprT;
        AuxiliarExpression expr = a.exp;
		if(varT.isEnumerated()){ //if is enumerated search in the first level de types declarated.
            
            TableLevel initialLevel = table.getLevelSymbols(0);
            currentEnumeratedType = initialLevel.getEnumeratedType(var.getEnumName());//set the enumerated type information of the current assignation
            enumExp1 = var.getEnumName();
        }
        expr.accept(this);
        exprT = this.getType();
        if(exprT.isEnumerated()){
            enumExp2 = currentEnumeratedType.getName();
        }
            
        //System.out.println(" ------------------ Visiting AuxiliarVarAssign------ expr= " + "-- TypEEEEEE: "  + exprT + " --------- \n");
            
        
		
        
		if( (varT.isBOOLEAN() && exprT.isBOOLEAN()) || (varT.isINT() && exprT.isINT() ) || ((varT.isEnumerated() && exprT.isEnumerated() && ( enumExp1.equals(enumExp2)) )) ){
			type = varT;
		}
		else{
		    type = Type.ERROR;
		    listError.add(new Error("ErrorType: Assignation of var :" + a.var.getName()));
		}	
	}

	public void visit(AuxiliarVar a){
		Type origT = a.getType();
        if(a.isDeclaration()){
            
            
            if(origT.isEnumerated()){
                
                TableLevel initialLevel = table.getLevelSymbols(0);
                AuxiliarEnumType enumT = initialLevel.getEnumeratedType(a.getEnumName()); //search the enumerated type.
                if(enumT!=null){
                    a.setEnumType(enumT); //set the complete information of the type.
                    //System.out.println("VISITING var  declaration " + a.getName() + " type "+ a.getEnumName() + " -- etype " + enumT.getName() + " - size = "+ enumT.getSize());
                }
                else{
                    type = Type.ERROR;
                    listError.add(new Error("ErrorType - Var Declaration " + a.getName() + ", Type " + a.getEnumName() + " not found."));
                }
                
            }
            
            
		    boolean added =table.addSymbol(a);
            if(!added){
                type = Type.ERROR;
                listError.add(new Error("ErrorType - Var Declaration " + a.getName() + " already exist."));
            }
            else{
                type=origT;
            }
		}
		else{ //type is undefined or error
			if (origT.isUndefined() ){
				
                int i= table.getLevel();
                boolean found=false;
			    
                //Checks that not be a parameter.
                AuxiliarParam param = table.searchParam(a.getName(), i);
                if(param!=null){
                	
                    found = true;
                    type = param.getType();
                    a.setType(param.getType());
                    a.setEnumName(param.getEnumName());
                    TableLevel initialLevel = table.getLevelSymbols(0);
                    this.currentEnumeratedType = initialLevel.getEnumeratedType(param.getEnumName()); // bug fixed, CECI: CHECK THIS OUT                                                
                }
                else{
                
			        AuxiliarVar var=null;			       
			        //search the variable in the symbol table
			        while(i >= 0 && !found){
                        var = table.searchVar(a.getName(), i);
                        
			    	    if(var!=null){
                            found = true;
					        type = var.getType();
					        a.setType(var.getType());
                            a.setEnumName(var.getEnumName());
                            //System.out.println("----------------> Var Founded = " + a.getName() + " -- in Enumerated Type " + var.getEnumName() + "Type: " + var.getType().getStringValue() );
                            
                            TableLevel initialLevel = table.getLevelSymbols(0);
                            this.currentEnumeratedType = initialLevel.getEnumeratedType(var.getEnumName());//set the enumerated type information of the current assignation o comparison
                            a = var;
				        }
				        i--;
			        }
                    
                    if(!found){//search if its a constant of the current enumerated type
                        //System.out.println("----------------> VAR Not found -----Searching constant = " + this.currentEnumeratedType );

                        if(this.currentEnumeratedType!=null){
                            //System.out.println("----------------> Searching constant = " + a.getName() + " -- in Enumerated Type " + this.currentEnumeratedType.getName() );

                            found = this.currentEnumeratedType.existConstant(a.getName());
                            if(found){
                                //System.out.println("----------------> Exist constant!!" + a.getName()  );

                                type= Type.ENUMERATED;
                                a.setType(type);
                                a.setEnumName(this.currentEnumeratedType.getName());
                                a.setEnumType(this.currentEnumeratedType);//set the complete information if the enumerated type.
                                
                            }
                        }
                    }
                    
                    if(!found){
                        type = Type.ERROR;
                        listError.add(new Error("ErrorType - Variable or Parameter or Constant = " + a.getName() + ", not found"));
                    }

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
			
        String tEnum1 = null;
        String tEnum2 = null;
        if(typeExp1.isEnumerated()){ //if is enumerated search in the first level of declarated types.   
        	tEnum1 = this.currentEnumeratedType.getName();
        }
        
		a.int2.accept(this);
		Type typeExp2 = this.getType();
        if(typeExp2.isEnumerated()){ //if is enumerated search in the first level of declarated types.
            tEnum2 = this.currentEnumeratedType.getName();
        }
       
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
                
                if(typeExp1.isEnumerated() && typeExp2.isEnumerated() ){
                    
                    if(tEnum1.equals(tEnum2)){
                       // System.out.println(" ------> ENUMERATED first op" + typeExp1.toString() + "type second op " +  typeExp2.toString() + "EnumType:" + tEnum2);
                        a.setIsEnumerated(true); //mark that comparation involves 2 enum expressions.
                        a.setEnumType(tEnum2);
                        type= Type.BOOL;
                    }else{
                        type = Type.ERROR;
                        listError.add(new Error("ErrorType - == operator: Expected the same enumerated types for Comparation."));
                    }
                }
                else{
                    //System.out.println(" ------> ERROR first op" + typeExp1.toString() + "type second op " +  typeExp2.toString() );
                    type = Type.ERROR;
                    listError.add(new Error("ErrorType - == operator: Expected the same types for Comparation."));
                }
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
		if(chT.isBOOLEAN() || chT.isINT() || chT.isEnumerated()){
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
	
    public void visit(AuxiliarParam a){
        	
        Type origT = a.getType();
        String paramName = a.getDeclarationName();
		
		if(a.isDeclaration()){			
            if(origT.isEnumerated()){
               
                TableLevel initialLevel = table.getLevelSymbols(0);
                AuxiliarEnumType enumT = initialLevel.getEnumeratedType(a.getEnumName()); //search the enumerated type.
                
                if(enumT!=null){
                    a.setEnumType(enumT); //set the complete information of the type.
                    //System.out.println("Parameter declaration " + paramName + " type "+ a.getEnumName() + " -- etype " + enumT.getName() + " - size = "+ enumT.getSize());
                    
                }
                else{
                    
                    type = Type.ERROR;
                    listError.add(new Error("ErrorType - Parameter " + paramName + " type :"+ a.getEnumName()+  " not found."));
                
                }
            }
		    boolean added = table.addSymbol(a);
            
            if (!added){ //Already exist an element with the same id at this level.
                type = Type.ERROR;
                listError.add(new Error("ErrorType - Parameter " + paramName + " : Ambiguous statement - already exist a Channel or Variable with the same name."));
            }
            else{
			    type=origT;
            }
		}		
		/*else{ //type is undefined or error   - TODO:CHECK IF IT IS NEEDED.(solo visita el parametro en la declaracion)
            System.out.println("//////////////////////////////////Parameter USEEEEEEEE ");
            
			if (origT.isUndefined() ){
				
			    AuxiliarParam par=null;
			    int level= table.getLevel();
                
			    par = table.searchParam(paramName, level);
                if(par!=null){
				    type = par.getType();
                    a.setEnumName(par.getEnumName());
					a.setType(par.getType());
				    a = par;
                    System.out.println("Parameter useeeee " + paramName + " type "+ a.getEnumName() );
                    
                }
			    else{
			    	type = Type.ERROR;
		    	    listError.add(new Error("ErrorType - Parameter " + paramName + " not found"));
		        }
                
	        }
			else{// type is ERROR
			    type = Type.ERROR;
			}
		}*/
        
    }
    
    
	
    public void visit(AuxiliarInvkProcess a){
        
        Type procT;
		TableLevel initialLevel = table.getLevelSymbols(0);
        AuxiliarVar gVar =null;
        String varName;
        
        LinkedList<AuxiliarExpression> invkParam = a.getInvkValues();
        for(int i=0; i< invkParam.size();i++){
            gVar = (AuxiliarVar)invkParam.get(i); //Obtain the name of the variable used in the process invocation.
            varName = gVar.getName();
            gVar = initialLevel.getVar(varName); // Search the global vble with that name
            
            if (gVar==null) {
                type = Type.ERROR;
                listError.add(new Error("ErrorType - Main Program - Invocation of process: "+ a.getInstanceName() +"  - Variable: " + varName +" - not found ."));
            }
            else{
                invkParam.set(i,gVar); //replace the parameter by the reference of the global variable.
            }
        }
        
    }
    
    
    
    public void visit(AuxiliarMain a){

		LinkedList<AuxiliarProcessDecl> pDecl = a.processDecl;
		LinkedList<AuxiliarInvkProcess> pInvk = a.getProcessInvk();
        
		Type procT;
		this.type = Type.INT; //By default all correct process are INT type, otherwise ERROR.
		
		for(int i=0; i< pDecl.size();i++){
			pDecl.get(i).accept(this);
			procT = this.getType();
        }
		
		if(!a.isCorrectInvk()){
			
			listError.add(new Error("ErrorType -  Process Invocation - Process instance not Found ."));
		}
		else{
		    TableLevel initialLevel = table.getLevelSymbols(0);
		    AuxiliarProcess proc = null;
            AuxiliarInvkProcess infoInvoc;
            String procNam;
		    for(int i=0; i<pInvk.size(); i++ ){
                procNam = pInvk.get(i).getInstanceName();
			    proc = initialLevel.getProcess(a.getProcessType(procNam));
			    if(proc !=null){
                    infoInvoc = pInvk.get(i);
                    infoInvoc.accept(this);
                    procT = this.getType();
                    
                    if (procT!=Type.ERROR){
                        
                        //Parameter vs InvocationsParameters - Check number of parameters
                        LinkedList<AuxiliarParam> paramList = proc.getParamList();
                        LinkedList<AuxiliarExpression>  invkParametersList= infoInvoc.getInvkValues();
                        
                        if(invkParametersList.size() != paramList.size()){ //TODO: Pensar chequear esto antes de llamar al Visitor de AusiliarinvkProcess.
                            listError.add(new Error("ErrorType - Invocation of process: "+ procNam +" - The number of parameters does not match its definition."));
                            
                        }
                        else{
                            
                            for(int j=0;j<paramList.size();j++){
                                AuxiliarParam par= paramList.get(j);
                                String declName = par.getDeclarationName();
                                
                                Type declT= par.getType();
                               /* System.out.println("Parameter declaration " + declName + " type "+ declT.toString() );
                                
                                if(par.getEnumType()!=null && declT.isEnumerated()){
                                   System.out.println(" enumerated: " +  par.getEnumName()+ " -- etype: " + par.getEnumType().getName() + " - size = "+ par.getEnumType().getSize());
                                }*/
                                
                                AuxiliarVar gVar = (AuxiliarVar)invkParametersList.get(j);
                                /*System.out.println("Parameter invok: " + gVar.getName() + " type: "+ gVar.getType().toString() );
                                
                                if(gVar.getEnumType()!=null && gVar.getType().isEnumerated()){
                                     System.out.println( " enumerated = " + gVar.getEnumName() + " -- etype " + gVar.getEnumType().getName() + " - size = "+ gVar.getEnumType().getSize()+ " \n\n\n");
                                }*/
                                
                                
                                if( gVar == null){
                                    listError.add(new Error("ErrorType1 - Invocation of process : "+ procNam +" - Type of parameters does not match its definition."));
                                }else{
                                    
                                    if( gVar.getType().isEnumerated()){
                                        if(gVar.getEnumType()== null){
                                            listError.add(new Error("ErrorType2 - Invocation of process : "+ procNam +" - Type of parameters does not match its definition."));
                                            
                                        }
                                        
                                        if(!gVar.getEnumName().equals(par.getEnumName())){
                                            listError.add(new Error("ErrorType3 - Invocation of process : "+ procNam +" - Type of parameters does not match its definition."));
                                            
                                        }
                                            
                                    
                                    }else{
                                       if( gVar.getType()!= declT){
                                          listError.add(new Error("ErrorType4 - Invocation of process : "+ procNam +" - Type of parameters does not match its definition."));
                                       }
                                    }
                                }
                                
                            }
                        }
				        proc.addInstanceName(infoInvoc.getInstanceName()); //add the instance name to the auxiliarProcess for later generation of instances.
                        proc.addInvkParam(infoInvoc); //Add the invocation parameters
                    }
                }
		    }
        }
		
        if(listError.size()>0){
            this.type = Type.ERROR;
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
			listError.add(new Error("ErrorType - Main Program - Process Declaration - Process name not Found ."));        	
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
    
    
    /**
	 * Calculates and set in each enumType the number of instances of this type of the program.
	 * 
	 */
    
    private void calculatesEnumVars(AuxiliarProgram p){
    
        AuxiliarEnumType enumT;
        String eName;
        int numEnumT;
        for(int i=0;i< p.enumTypes.size();i++){
			enumT =p.enumTypes.get(i);
            eName = enumT.getName();
            numEnumT= getNumInstances(eName, p.globalVars.getEnumVars(),p.channels.getEnumChannels(), p.process.getProcessList());
            enumT.setNumVars(numEnumT);
        }
        
	}
    
    
    private int getNumInstances(String eName, LinkedList<AuxiliarVar> globalVarList,LinkedList<AuxiliarChannel> enumChannelList , LinkedList<AuxiliarProcess> processList){
        
        int numEnumInstances=0;
       
        //calculates the number of global var of this enumType
        for(int i=0;i<globalVarList.size();i++){
			AuxiliarVar var =globalVarList.get(i);
            if(var.getEnumName().equals(eName)){
                numEnumInstances++;
            }
		}
        
        //calculates the number of global channel of this enumType
        for(int i=0;i<enumChannelList.size();i++){
			AuxiliarChannel ch =enumChannelList.get(i);
            if(ch.getEnumName().equals(eName)){
                numEnumInstances= numEnumInstances + ch.getSize();  // TODO: CHECK if is correct set the worst case..
            }
		}
        
        //calculates the number of var and parameters of this enumType by each process
        for(int i=0;i<processList.size();i++){
			AuxiliarProcess proc =processList.get(i);
            numEnumInstances= numEnumInstances + proc.getNumEnumProcessInstances(eName);
            
		}
        
        return numEnumInstances;
    }
    

}
