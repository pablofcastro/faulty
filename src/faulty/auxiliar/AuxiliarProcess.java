package faulty.auxiliar;


import java.util.*;
import java.io.*;
import graph.*;



public class AuxiliarProcess extends AuxiliarProgramNode {

	LinkedList<AuxiliarChannel> channelIds;
    LinkedList<AuxiliarParam> paramList;

	String processName;
	LinkedList<AuxiliarVar> intVars; // it has a collection of local variables of type int
	LinkedList<AuxiliarVar> boolVars; // it has a collections of local variables of type boolean
    LinkedList<AuxiliarVar> enumVars; // it has a collections of local variables of type enumerated
    
	LinkedList<AuxiliarBranch> branches; // a collection of branches (Bi -> Ci)
	AuxiliarExpression normCondition; // normative condition for characterising the non-faulty part of the program
	AuxiliarExpression initialCond; // the initial valuations of the local variables.
	LinkedList<String> processInstanceNames; // Names of the differents process Instances
	LinkedList<AuxiliarInvkProcess> processInvkParameters; // Invocation Parameters of each process instance.
	
	
	public AuxiliarProcess(String name){
		this.processName = name;
		channelIds =new LinkedList<AuxiliarChannel>();
		paramList = new LinkedList<AuxiliarParam>();

        intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		branches = new LinkedList<AuxiliarBranch>();
		normCondition = null;
	    initialCond = null;
	    processInstanceNames= new LinkedList<String>();
        processInvkParameters = new LinkedList<AuxiliarInvkProcess>();
		
	}
	
	
	public AuxiliarProcess(String name,AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarVar> varList,LinkedList<AuxiliarBranch> branchList){
		this.processName = name;
		channelIds =new LinkedList<AuxiliarChannel>();
		paramList = new LinkedList<AuxiliarParam>();
        initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
		processInvkParameters= new LinkedList<AuxiliarInvkProcess>();
		
		 /* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add(varList.get(i));
            }
            else{
                if ( varList.get(i).getType().isINT() ){
                    intVars.add(varList.get(i));
                }else{
                    enumVars.add(varList.get(i));
                }
            }
        }
		
		
	}
	
	public AuxiliarProcess(AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarVar> varList,LinkedList<AuxiliarBranch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<AuxiliarChannel>();
        paramList = new LinkedList<AuxiliarParam>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
        processInvkParameters= new LinkedList<AuxiliarInvkProcess>();
		
		
		/* --- Adding the Declarated Vbles according the type ---*/
        for (int i = 0; i < varList.size(); i++){
            
            if ( varList.get(i).getType().isBOOLEAN() ){
            	boolVars.add(varList.get(i));
            }
            else{
                if ( varList.get(i).getType().isINT() ){
                    intVars.add(varList.get(i));
                }else{
                    enumVars.add(varList.get(i));
                }
            }
        }
		
		
	}
	
	public AuxiliarProcess(AuxiliarExpression iniC, AuxiliarExpression normC,LinkedList<AuxiliarBranch> branchList){
		this.processName = new String();
		channelIds =new LinkedList<AuxiliarChannel>();
        paramList = new LinkedList<AuxiliarParam>();
		initialCond =iniC;
		normCondition =normC;
		branches = branchList;
		intVars = new LinkedList<AuxiliarVar>();
		boolVars = new LinkedList<AuxiliarVar>();
        enumVars = new LinkedList<AuxiliarVar>();
		processInstanceNames= new LinkedList<String>();
        processInvkParameters= new LinkedList<AuxiliarInvkProcess>();
		
	}
	
	public String getName(){
		
		return processName;
	}
	
	
    public LinkedList<AuxiliarChannel> getChannelIds(){
		
		return channelIds;
	}
    
    public LinkedList<AuxiliarParam> getParamList(){
		
		return paramList;
	}
    
    public LinkedList<AuxiliarBranch> getBranches(){
		
		return branches;
	}
    
    public LinkedList<AuxiliarVar> getVarInt(){
		
		return intVars;
	}
 
    public LinkedList<AuxiliarVar> getVarBool(){
        
		return boolVars;
	}
    
    public LinkedList<AuxiliarVar> getVarEnum(){
        
		return enumVars;
	}


    
    public AuxiliarExpression getInitialCond(){
    	return initialCond;
    	
    }
    
    public AuxiliarExpression getNormativeCond(){
    	return normCondition;
    	
    }
    
    /**
     *
     * @param processName Name of the process.
     * @return return the list with the invocation parameters of the process with that name, an empty list otherwise.-
     */
    
    public LinkedList<AuxiliarExpression> getInvkParametersList(String processName){
        
        
        for(int i=0;i<processInvkParameters.size();i++){
            String pName = processInvkParameters.get(i).getInstanceName();
            
            if(pName.equals(processName)){
                return processInvkParameters.get(i).getInvkValues();
            }
            
        }
        return new LinkedList<AuxiliarExpression>();
        
	}
    
    
    /**
     *
     * @param processName Name of the process.
     * @return return the list with the boolean parameters of the process with that name, an empty list otherwise.-
     */
    
    public LinkedList<AuxiliarExpression> getInvkBoolParamList(String processName){
        
        LinkedList<AuxiliarExpression> boolPar = new LinkedList<AuxiliarExpression>();
        LinkedList<AuxiliarExpression> invkPar = this.getInvkParametersList(processName);
        
        for(int i=0;i<invkPar.size();i++){
            AuxiliarVar var = (AuxiliarVar)invkPar.get(i);
            if(var.getType().isBOOLEAN()){
                boolPar.add(invkPar.get(i));
            }
        }
        
        return boolPar;
	}
    
    
    /**
     *
     * @param processName Name of the process.
     * @return return the list with the integer parameters of the process with that name, an empty list otherwise.-
     */
    
    public LinkedList<AuxiliarExpression> getInvkIntParamList(String processName){
        
        LinkedList<AuxiliarExpression> intPar = new LinkedList<AuxiliarExpression>();
        LinkedList<AuxiliarExpression> invkPar = this.getInvkParametersList(processName);
        
        for(int i=0;i<invkPar.size();i++){
            AuxiliarVar var = (AuxiliarVar)invkPar.get(i);
            
            if(var.getType().isINT()){
                intPar.add(invkPar.get(i));
            }
        }
        
        return intPar;
	}
    
    
    
    /**
     *
     * @param i ith intance position
     * @return return the name of the ith instance, null if not exist.-
     */
    public String getProcessInstanceName(int i){
    	
    	if ( i < processInstanceNames.size()){
    		return processInstanceNames.get(i);
    	}
    	else{
    		return null;
    	}
    	
    }
    
    public int getNumVar(){
    	
        return ( boolVars.size() + intVars.size() + enumVars.size());
    }
    
    public int getNumVarInt(){
    	
        return ( intVars.size());
    }
   
    public int getNumVarEnumerated(){
    	
        return ( enumVars.size());
    }
    
    
    public int getNumVarBool(){
    	
    	int auxVars =0;
    	if (this.channelIds.size()>0){ //Means that the process use at least one channel
    		
    		for(int i=0; i< this.branches.size();i++){
    			auxVars++; //needs 1 boolean variable extra for each branch 
    		}
    		
    	}
    	return ( boolVars.size() + auxVars);
    }
    
    public int getNumBranches(){
    	
        return branches.size();
    }

    public int getNumInstances(){
    	
        return processInstanceNames.size();
    }


    
    public int getNumChannelsUsed(){
	
    return  channelIds.size();
   }
	
    public void addInstanceName(String instanceName){
    	processInstanceNames.add(instanceName);
    }
    
    public void addChannelId(String chId){
    	channelIds.add(new AuxiliarChannel(chId,true));
    	
    }
    
    public void addParam(String parId){
    	paramList.add(new AuxiliarParam(parId));
    	
    }
    
    public void addInvkParam(AuxiliarInvkProcess invP){
    	processInvkParameters.add(invP);
    }
    
    
    public void addBranchList(LinkedList<AuxiliarBranch> list){
    	branches = list;
    	
    }
    
    public void setInitialCond(AuxiliarExpression ini){
    	initialCond = ini;
    	
    }
    
    public void setNormativeCond(AuxiliarExpression norm){
    	normCondition = norm;
    	
    }
    
    public void setName(String name){
    	processName = name;
    	
    }
    
    public void setChannelIds(LinkedList<String> chId){
    	LinkedList<AuxiliarChannel> list = new LinkedList<AuxiliarChannel>();
    	for(int i=0;i<chId.size();i++){
    		list.add(new AuxiliarChannel( chId.get(i), true ) );
    	}
    	channelIds = list;
    	
    }
    
    
    public void setParamList(LinkedList<AuxiliarParam> parL){
    	paramList = parL;
    }
    
    
    /**
     * Return the list of all names of the boolean variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */                       
    public LinkedList<String> getBoolVarNamesProcessInstances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
    	   for(int i = 0; i< boolVars.size(); i++){
    		   String nameB = new String (processInstanceNames.get(j) + "." + boolVars.get(i).getName());
    		   varNames.add(nameB);
    	   }
   	    }
    	return varNames;
    }
    
    
    
    /**
     * Return the list of all names of the integer variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */
    public LinkedList<String> getIntVarNamesProcessInstances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
    	   for(int i = 0; i< intVars.size(); i++){
    		   String nameI = new String (processInstanceNames.get(j) + "." + intVars.get(i).getName());
    		   varNames.add(nameI);

    	   }
   	    }
    	return varNames;
    }
    
    /**
     * Return the list of all names of the enumerated variables involved of this
     *  process (according the instance name of the process) :
     * "Process_instanceName" + "." + "VariableName"
     * @return
     */
    public LinkedList<String> getEnumVarNamesProcessInstances(){
    	LinkedList<String> varNames = new LinkedList<String>();
    	
    	for(int j=0; j< processInstanceNames.size() ; j++){
            for(int i = 0; i< enumVars.size(); i++){
                String nameI = new String (processInstanceNames.get(j) + "." + enumVars.get(i).getName());
                varNames.add(nameI);
                
            }
   	    }
    	return varNames;
    }
    
    /**
     * @return Return the total number of enumVars of a single Process.
     * 
     */
    public int getNumEnumVarsProcess(String enumName){
        
        int numberVars =0;
        int numberParam = 0;
        
        
        for(int i = 0; i< enumVars.size(); i++){
            if(enumVars.get(i).getEnumName().equals(enumName) ){
                numberVars++;
            }
        }
        
        for(int i = 0; i< paramList.size(); i++){
            if(paramList.get(i).getEnumName().equals(enumName) ){
                numberParam++;
            }
        }
        
        
    	return (numberVars+numberParam);
    }
    
    
    /**
     * @return Return the total number of enumVars according the number of process instances.
     *
     */
    public int getNumEnumProcessInstances(String enumName){
        
        
        int numberVars =0;
        int numberInstances=0;
        int numberParam = 0;
        
        for(int i = 0; i< enumVars.size(); i++){
            if(enumVars.get(i).getEnumName().equals(enumName) ){
                numberVars++;
            }
        }
        
        
        for(int i = 0; i< paramList.size(); i++){
            if (paramList.get(i).getType().isEnumerated()){//Only compare if the type of parameter is Enum.
                if(paramList.get(i).getEnumName().equals(enumName) ){
                    numberParam++;
                }
            }
        }
        
        numberInstances = processInstanceNames.size();
        
       return ((numberParam + numberVars) * numberInstances);
    }
    
    
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}
    
    /*Generates java code implementing this process*/
    public String toJava(){
        String ints,bools,enums,params,attributes,init,run,start,methods,assigns,prog;
        ints = "";
        for (int i = 0; i < intVars.size(); i++){
            ints += "  int " + intVars.get(i).getName() + ";\n"; 
        }
        bools = "";
        for (int i = 0; i < boolVars.size(); i++){
            bools += "  boolean " + boolVars.get(i).getName() + ";\n"; 
        }
        enums = "";
        for (int i = 0; i < enumVars.size(); i++){
            enums += "  "+enumVars.get(i).getEnumType().getName() + " " + enumVars.get(i).getName() + ";\n"; 
        }
        params = "";
        for (int i = 0; i < paramList.size(); i++){
            if (paramList.get(i).getType().isBOOLEAN())
                params += "  Bool" + " "+ paramList.get(i).getDeclarationName() + ";\n";
            if (paramList.get(i).getType().isINT())
                params += "  Int" + " "+ paramList.get(i).getDeclarationName() + ";\n";
            if (paramList.get(i).getType().isEnumerated())
                params += paramList.get(i).getEnumName() + " "+ paramList.get(i).getDeclarationName() + ";\n";
        }
        attributes = "  Thread t; \n" + ints + bools + enums + params +"\n";
        init = "  public " + processName + "(";
        for (int i = 0; i < paramList.size(); i++){
            if (paramList.get(i).getType().isBOOLEAN())
                init += "Bool" + " "+ paramList.get(i).getDeclarationName();
            if (paramList.get(i).getType().isINT())
                init += "Int" + " "+ paramList.get(i).getDeclarationName();
            if (paramList.get(i).getType().isEnumerated())
                init += paramList.get(i).getEnumName() + " "+ paramList.get(i).getDeclarationName();
            if (i < paramList.size() - 1)
                init += ",";
        }
        init += ") {\n";
        /*for (int i = 0; i < paramList.size(); i++){
            if (processInvkParameters.get(i).getInvkValues().get(i) instanceof AuxiliarVar){}
                init += "      "+paramList.get(i).getDeclarationName()+" = "+ ((AuxiliarVar)processInvkParameters.get(i).getInvkValues().get(i)).getName();
        }*/
        init += initToJava(initialCond, false);
        init += "\n  }\n\n";
        run = "  public void run(){\n";
        run += "    while (true){\n";
        /*for (int i = 0; i < branches.size(); i++){
            if (i < branches.size()-1)
                run += "      if (!action"+i+"())\n";
            else
                run +="        action"+(branches.size()-1)+"();\n    }\n  }\n\n";
        }*/
        run += "      switch ("+"Program.randomGenerator.nextInt("+branches.size()+")){\n";
        for (int i = 0; i < branches.size(); i++){
            run += "        case "+i+":action"+i+"();break;\n";
        }
        run += "        default:break;\n      }\n    }\n  }\n";
        start = "  public void start (){\n    if (t == null) {\n      t = new Thread(this);\n      t.start();\n    }\n  }\n\n";
        methods = "";
        for (int i = 0; i < branches.size(); i++){
            assigns = "";
            for (int j = 0; j < branches.get(i).getAssignList().size(); j++){
                AuxiliarVarAssign v = (AuxiliarVarAssign)branches.get(i).getAssignList().get(j);
                if (v.getExp() instanceof AuxiliarConsBoolExp){
                    if (hasLocalVar(v.getVar()))
                        assigns += "      "+v.getVar().getName()+"="+((AuxiliarConsBoolExp)v.getExp()).getValue() + ";\n";
                    else
                        if (hasParam(v.getVar()))
                            assigns += "      "+v.getVar().getName()+".setValue("+((AuxiliarConsBoolExp)v.getExp()).getValue() + ");\n";
                        else
                            assigns += "      Program."+v.getVar().getName()+"="+((AuxiliarConsBoolExp)v.getExp()).getValue() + ";\n";                }
                else{
                    if (v.getExp() instanceof AuxiliarVar)
                        //if (hasLocalVar(v.getVar()))
                            assigns += "      "+v.getVar().getName()+"="+((AuxiliarVar)v.getExp()).getEnumName()+"."+((AuxiliarVar)v.getExp()).getName() + ";\n";
                        //else
                        //    assigns += "      Program."+v.getVar().getName()+"="+((AuxiliarVar)v.getExp()).getEnumName()+"."+((AuxiliarVar)v.getExp()).getName() + ";\n";
                }

            }
            methods += "  synchronized private boolean action"+i+"(){\n    if ("+cnfToJava(branches.get(i).getGuard())+"){\n" + assigns + "      return true;\n    }\n    else{\n      return false;\n    }\n"+"  }\n\n"; 
        }
        methods = init + run + start + methods;
        prog = "public class " + processName + " implements Runnable { \n\n" + attributes + methods + "}";

        try{
            File file = new File("../out/" + processName +".java");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(prog);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        //toGraph();
        return prog;

    }
    
    private String cnfToJava(AuxiliarExpression e){
        if (e instanceof AuxiliarConsBoolExp)
            return ""+((AuxiliarConsBoolExp)e).getValue();
        if (e instanceof AuxiliarVar)
            if (((AuxiliarVar)e).getEnumType() != null)
                return ((AuxiliarVar)e).getEnumName() + "." + ((AuxiliarVar)e).getName();
            else
                if (hasLocalVar((AuxiliarVar)e))
                    return ((AuxiliarVar)e).getName();
                else
                    if (hasParam((AuxiliarVar)e))
                        return ((AuxiliarVar)e).getName()+".getValue()";
                    else
                        return "Program."+((AuxiliarVar)e).getName();
        if (e instanceof AuxiliarNegBoolExp)
            return "!" + cnfToJava(((AuxiliarNegBoolExp)e).exp);
        if (e instanceof AuxiliarEqBoolExp)
            return cnfToJava(((AuxiliarEqBoolExp)e).int1) + " == " + cnfToJava(((AuxiliarEqBoolExp)e).int2);
        if (e instanceof AuxiliarAndBoolExp)
            return cnfToJava(((AuxiliarAndBoolExp)e).exp1) + " && " + cnfToJava(((AuxiliarAndBoolExp)e).exp2);
        return "true";
    }

    //ASSUME e IS CNF, WITH NEGATION ON ATOMIC LEVEL ONLY
    private String initToJava(AuxiliarExpression e, boolean neg){
        if (e instanceof AuxiliarVar)
            if (((AuxiliarVar)e).getEnumType() != null)
                return ((AuxiliarVar)e).getEnumName() + "." + ((AuxiliarVar)e).getName();
            else
                if (((AuxiliarVar)e).getEnumName() != null)
                    //if (hasLocalVar((AuxiliarVar)e))
                        return ((AuxiliarVar)e).getName();
                    //else
                        //return "Program."+((AuxiliarVar)e).getName();
                else
                    if (hasLocalVar((AuxiliarVar)e))
                        if (neg)
                            return "    this." + ((AuxiliarVar)e).getName() + " = false" + ";\n";
                        else
                            return "    this." + ((AuxiliarVar)e).getName() + " = true" + ";\n";
                    else
                        if (hasParam((AuxiliarVar)e))
                            if (neg)
                                return  "    this." + ((AuxiliarVar)e).getName() + "=" + ((AuxiliarVar)e).getName() + ";\n"+
                                        "    this." + ((AuxiliarVar)e).getName() + ".setValue(false);\n";
                            else
                                return  "    this." + ((AuxiliarVar)e).getName() + "=" + ((AuxiliarVar)e).getName() + ";\n"+
                                        "    this." + ((AuxiliarVar)e).getName() + ".setValue(true);\n";
                        else
                            if (neg)
                                return "    Program." + ((AuxiliarVar)e).getName() + " = false" + ";\n";
                            else
                                return "    Program." + ((AuxiliarVar)e).getName() + " = true" + ";\n";
        if (e instanceof AuxiliarNegBoolExp)
            return initToJava(((AuxiliarNegBoolExp)e).exp, !neg); //flip the neg flag
        if (e instanceof AuxiliarEqBoolExp)
            return "    this." + initToJava(((AuxiliarEqBoolExp)e).int1, neg) + " = " + initToJava(((AuxiliarEqBoolExp)e).int2, neg) + ";\n";
        if (e instanceof AuxiliarAndBoolExp)
            return initToJava(((AuxiliarAndBoolExp)e).exp1, neg) + initToJava(((AuxiliarAndBoolExp)e).exp2, neg);
        return "";
    }
    

    /*Checks if this process has a local var v*/
    private boolean hasLocalVar(AuxiliarVar v){
        for (AuxiliarVar i : boolVars){
            if (v.getName().equals(i.getName()))
                return true;
        }
        for (AuxiliarVar i : intVars){
            if (v.getName().equals(i.getName()))
                return true;
        }
        for (AuxiliarVar i : enumVars){
            if (v.getName().equals(i.getName()))
                return true;
        }
        /*for (int i = 0; i < paramList.size(); i++){
            if (v.getName().equals(paramList.get(i).getDeclarationName()))
                return true;
        }*/
        return false;
    }

    /*Checks if this process has a parameter v*/
    private boolean hasParam(AuxiliarVar v){
        for (AuxiliarParam i : paramList){
            if (v.getName().equals(i.getDeclarationName()))
                return true;
        }
        return false;
    }

    /*Generates a explicit model (Kripke structure) for this process*/
    public ExplicitModel toGraph(String pName, ExplicitCompositeModel fullModel){//, LinkedList<AuxiliarVar> globalVars){
        ExplicitModel m = new ExplicitModel(pName, processName, boolVars, paramList, getInvkParametersList(pName), fullModel);
        //System.out.println();
        /*LinkedList<AuxiliarVar> allVars = new LinkedList<AuxiliarVar>();
        allVars.addAll(boolVars);
        allVars.addAll(globalVars);*/
        Node init = new Node(m,initialCond);
        init.checkNormCondition(normCondition);
        m.addNode(init);
        m.setInitial(init);
        TreeSet<Node> iterableSet = new TreeSet<Node>();
        iterableSet.add(init);
        while (!iterableSet.isEmpty()){
            Node from = iterableSet.pollFirst();
            for (AuxiliarBranch b : branches){
                if (from.satisfies(b.getGuard())){
                    Node to = from.createSuccessor(b.getAssignList());
                    to.checkNormCondition(normCondition);
                    Node toOld = m.search(to);
                    if (toOld == null){
                        m.addNode(to);
                        m.addEdge(from,to,b.getLabel(),b.getIsFaulty());
                        iterableSet.add(to);
                    }
                    else{
                        m.addEdge(from,toOld,b.getLabel(),b.getIsFaulty());
                    }
                }
            }
        }
        //System.out.println(m.createDot());
        return m;
    }
    
}
