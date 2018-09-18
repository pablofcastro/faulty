package faulty.auxiliar;

import java.util.*;
import java.io.*;
import graph.*;


/**
* This class defines a AuxiliarFaulty program, it provides the basic structures: a list of enumTypes,  
* a collection of global variables( internally separating in bool and int variables),
* a collection of channels( internally separating in bool and int channels),
* and a collection of all processes defined and invocated.
* @author Ceci
**/

public class AuxiliarProgram extends AuxiliarProgramNode{
	LinkedList<AuxiliarEnumType> enumTypes;
	AuxiliarGlobalVarCollection globalVars;
	AuxiliarChannelCollection channels;
	AuxiliarProcessCollection process;
	AuxiliarMain mainProgram;
    int maxEnumSize;
	
    /** GlobalVars + Channels
     * @param gVars: Collection of all global variables classified by their type.
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(AuxiliarGlobalVarCollection gVars, AuxiliarChannelCollection channels, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = new LinkedList<AuxiliarEnumType>();
		this.globalVars = gVars;
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
		this.maxEnumSize = 0;
	}
	
    /**  Channels
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(AuxiliarChannelCollection channels, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = new LinkedList<AuxiliarEnumType>();
		this.globalVars = new AuxiliarGlobalVarCollection();
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
		
	}
	
	/**  GlobalVars
     * @param gVars: Collection of all global variables classified by their type.
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(AuxiliarGlobalVarCollection gVars, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = new LinkedList<AuxiliarEnumType>();
		this.globalVars = gVars;
		this.channels= new AuxiliarChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
	}
	
    /** !EnumTypes & !GlobalVars & !Channels
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram( AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = new LinkedList<AuxiliarEnumType>();
		this.globalVars = new AuxiliarGlobalVarCollection();
		this.channels=new AuxiliarChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
	}
    
    /** EnumTypes + GlobalVars + Channels
     * @param enumList: List of EnumTypes.
     * @param gVars: Collection of all global variables classified by their type.
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(LinkedList<AuxiliarEnumType> enumList, AuxiliarGlobalVarCollection gVars, AuxiliarChannelCollection channels, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = enumList;
		this.globalVars = gVars;
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
		
	}
	
	
    /** EnumTypes + Channels
     * @param enumList: List of EnumTypes
     * @param channels: Collection of all global channels classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(LinkedList<AuxiliarEnumType> enumList, AuxiliarChannelCollection channels, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = enumList;
		this.globalVars = new AuxiliarGlobalVarCollection();
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
		
	}
	
	
    /** EnumTypes + GlobalVars 
     * @param enumList: List of EnumTypes
     * @param gVars: Collection of all global variables classified by their type.
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(LinkedList<AuxiliarEnumType> enumList, AuxiliarGlobalVarCollection gVars, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = enumList;
		this.globalVars = gVars;
		this.channels= new AuxiliarChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
        
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
	}
	
	
    /** EnumTypes 
     * @param enumList: List of EnumTypes
     * @param process: Collection of all processes defined.
     * @param mainProgram: Collection of all process intances with their respective parameters.
     **/
	public AuxiliarProgram(LinkedList<AuxiliarEnumType> enumList,AuxiliarProcessCollection process, AuxiliarMain mainProgram){
        this.enumTypes = enumList;
		this.globalVars = new AuxiliarGlobalVarCollection();
		this.channels=new AuxiliarChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
        this.maxEnumSize = 0;
        int currentEnumsize=0;
        for(int i=0; i<enumList.size();i++){
            currentEnumsize = enumList.get(i).getSize();
            if(currentEnumsize > this.maxEnumSize){
                this.maxEnumSize = currentEnumsize;
            }
        }
	}
	
    public AuxiliarGlobalVarCollection getGlobalVars(){
        return this.globalVars;
    }
    
    public LinkedList<AuxiliarEnumType> getAuxiliarEnumList(){
        return this.enumTypes;
    }
    
    public int getMaxEnumSize(){
        return this.maxEnumSize;
    }
    
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}

    /*Generates java code implementing the complete program*/
    public String toJava(){
        String imports,tEnums,prog,globals,params,procs,main;
        imports = "import java.util.Random;"+ "\n\n";
        prog = "";
        tEnums = "";
        for (int i = 0; i < enumTypes.size(); i++){
            tEnums = "public enum " + enumTypes.get(i).getName() + "{";
            for (int j = 0; j < enumTypes.get(i).cons.length; j++){
                tEnums += enumTypes.get(i).getCons(j);
                if (j == enumTypes.get(i).cons.length-1)
                    tEnums += "}\n";
                else
                    tEnums += ",";
            }

            try{
                File file = new File("../out/" + enumTypes.get(i).getName() +".java");
                file.createNewFile();
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(tEnums);
                bw.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }   
        }

        procs = "";
        for (int i = 0; i < process.getProcessList().size(); i++){
            procs += process.getProcessList().get(i).toJava() + "\n\n";
        }

        globals = "    public static Random randomGenerator = new Random();\n";
        params = "";
        for (int i = 0; i < globalVars.getBoolVars().size(); i++){
            AuxiliarVar v = globalVars.getBoolVars().get(i);
            globals += "    public static "+"boolean" + " " + v.getName() + " = false;\n";
            params += "     "+"Bool" + " " + v.getName() + " = new Bool(false);\n";
        }
        for (int i = 0; i < globalVars.getIntVars().size(); i++){
            AuxiliarVar v = globalVars.getIntVars().get(i);
            globals += " "+"Int" + " " + v.getName() + " = 0;\n";
        }
        for (int i = 0; i < globalVars.getEnumVars().size(); i++){
            AuxiliarVar v = globalVars.getEnumVars().get(i);
            globals += " "+v.getType() + " " + v.getName() + ";\n";
        }

        main = "  public static void main(String[] args){\n" + params;
        for (int i = 0; i < mainProgram.getProcessDecl().size(); i++){
            AuxiliarProcessDecl curr = mainProgram.getProcessDecl().get(i);
            main += "    "+ curr.getType() + " " + curr.getName() + " = new "+curr.getType()+"(";
            for (int j = 0; j < process.getProcessList().size(); j++){
                if (process.getProcessList().get(j).getName().equals(curr.getType()))
                    for (int k = 0; k < process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().size(); k++){
                        if (process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().get(k) instanceof AuxiliarVar){
                            main += ((AuxiliarVar)process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().get(k)).getName();
                            if (k < process.getProcessList().get(j).processInvkParameters.get(0).getInvkValues().size()-1)
                                main += ",";
                        }
                    }
            }
            main += ")"+";\n";
        }
        for (int i = 0; i < mainProgram.getProcessDecl().size(); i++){
            AuxiliarProcessDecl curr = mainProgram.getProcessDecl().get(i);
            main += "    "+ curr.getName() + ".start();\n";
        }
        main += "}\n\n";

        prog += imports+"public class Program {\n\n" + globals + main + "}";

        try{
            File file = new File("../out/" + "Program" +".java");
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(prog);
            bw.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        toGraph();
        return prog;
    }

    /*Generates a explicit model (Kripke structure) for the complete program*/
   /* public ExplicitCompositeModel toGraph(){
        ExplicitCompositeModel m = new ExplicitCompositeModel(globalVars.getBoolVars());
        LinkedList<ExplicitModel> procs = new LinkedList<ExplicitModel>();

        //states in m are lists of states (from processes)
        //calculate initial state
        CompositeNode init = new CompositeNode(new LinkedList<Node>(), m);
        for (AuxiliarProcessDecl pDecl : mainProgram.getProcessDecl()){
            for (int i = 0; i < process.getProcessList().size(); i++){
                AuxiliarProcess proc = process.getProcessList().get(i);
                if (pDecl.getType().equals(proc.getName())){
                    ExplicitModel p = proc.toGraph(mainProgram.getProcessDecl().get(i).getName(), m);//,globalVars.getBoolVars());
                    p.createDot();
                    procs.add(p);
                    init.getNodes().add(p.getInitial());
                }
            }
        }
        
        m.addNode(init);
        m.setInitial(init);

        TreeSet<CompositeNode> iterSet = new TreeSet<CompositeNode>();
        iterSet.add(m.getInitial());

        //build the whole model
        while(!iterSet.isEmpty()){
            CompositeNode curr = iterSet.pollFirst();
            for (int i = 0; i < curr.getNodes().size(); i++){ // for each process in current global state
                Node n = curr.getNodes().get(i);
                for(Node n_ : procs.get(i).getSuccessors(n)){ //for each successor of the process create a global successor
                    Pair p = new Pair(n,n_);
                    CompositeNode curr_ = curr.clone();
                    curr_.getNodes().set(i,n_);
                    curr_.updateGlobalState(n,n_); //if there are any modifications to shared vars on n_ then update global state
                    CompositeNode toOld = m.search(curr_);
                    if (toOld == null){
                        m.addNode(curr_);
                        iterSet.add(curr_);
                        m.addEdge(curr, curr_, procs.get(i).getLabels().get(p), procs.get(i).getFaultyActions().get(p));
                    }
                    else{
                        m.addEdge(curr, toOld, procs.get(i).getLabels().get(p), procs.get(i).getFaultyActions().get(p));
                    }
                }
            }
        }
        //ExplicitModel res = m.flatten();
        m.createDot();
        return m;
    }
*/
    /*TOTRY: Create the whole model on the fly*/
    public ExplicitCompositeModel toGraph(){
        ExplicitCompositeModel m = new ExplicitCompositeModel(globalVars.getBoolVars());

        //states in m are lists of states (from processes)
        //calculate initial state
        CompositeNode init = new CompositeNode(new LinkedList<Node>(), m);
        for (AuxiliarProcessDecl pDecl : mainProgram.getProcessDecl()){
            for (int i = 0; i < process.getProcessList().size(); i++){
                AuxiliarProcess proc = process.getProcessList().get(i);
                if (pDecl.getType().equals(proc.getName())){
                    Node pInit = new Node(proc,pDecl.getName(),init,proc.getInitialCond());
                    init.getNodes().add(pInit);
                }
            }
        }
        
        m.addNode(init);
        m.setInitial(init);
        //Keep track of processes local nodes
        for (int i = 0; i < init.getNodes().size(); i++){
            m.getProcessesNodes().add(new LinkedList<Node>());
            m.getProcessesNodes().get(i).add(init.getNodes().get(i));
        }

        TreeSet<CompositeNode> iterSet = new TreeSet<CompositeNode>();
        iterSet.add(m.getInitial());

        //build the whole model
        while(!iterSet.isEmpty()){
            CompositeNode curr = iterSet.pollFirst();

            for (int i = 0; i < curr.getNodes().size(); i++){ // for each process in current global state
                Node n = curr.getNodes().get(i);
                n.setParent(curr); //NO SE PORQUE TUVE QUE FORZAR ESTE SETEO
                //System.out.println(curr);
                for (AuxiliarBranch b : n.getProcess().getBranches()){
                    if (n.satisfies(b.getGuard())){
                        if (!curr.equals(n.getParent())){
                            System.out.println(b.getLabel());
                            System.out.println(n);
                            System.out.println(n.getParent());
                            System.out.println(curr);
                        }

                        //create global successor curr_
                        CompositeNode curr_ = curr.clone();
                        //create successor n_
                        Node n_ = n.createSuccessor(curr_,b.getAssignList(),i);
                        /*if (!curr_.equals(n_.getParent())){
                            System.out.println(n_);
                            System.out.println(n_.getParent());
                            System.out.println(curr_);
                        }*/

                        n_.checkNormCondition(n.getProcess().getNormativeCond());
                        //Pair p = new Pair(n,n_);
                        curr_.getNodes().set(i,n_);
                        curr_.checkNormCondition();
                        //curr_.updateGlobalState(n,n_);
                        CompositeNode toOld = m.search(curr_);
                        if (toOld == null){
                            m.addNode(curr_);
                            iterSet.add(curr_);
                            m.addEdge(curr, curr_, n.getProcessName()+b.getLabel(),b.getIsFaulty());
                        }
                        else{
                            n_.setParent(toOld);
                            m.addEdge(curr, toOld, n.getProcessName()+b.getLabel(),b.getIsFaulty());
                        }
                    }
                }
            }
        }
        System.out.println("hey");
        //ExplicitModel res = m.flatten();
        m.createDot();
        return m;
    }

}
