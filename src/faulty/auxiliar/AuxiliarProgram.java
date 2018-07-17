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
    public ExplicitCompositeModel toGraph(){
        ExplicitCompositeModel m = new ExplicitCompositeModel();
        LinkedList<ExplicitModel> procs = new LinkedList<ExplicitModel>();

        //states in m are lists of states (from processes)
        //calculate initial state
        CompositeNode init = new CompositeNode(new LinkedList<Node>(), globalVars.getBoolVars());
        for (AuxiliarProcess proc : process.getProcessList()){
            ExplicitModel p = proc.toGraph();
            procs.add(p);
            init.getNodes().add(p.getInitial());
            init.updateState(p.getInitial()); //pre: todos los procesos inicializan las vars globales igual
        }
        m.addNode(init);
        m.setInitial(init);

        TreeSet<CompositeNode> set = new TreeSet<CompositeNode>();
        set.add(m.getInitial());

        //build the whole model
        while(!set.isEmpty()){
            CompositeNode curr = set.pollFirst();
            for (int i = 0; i < curr.getNodes().size(); i++){
                for(Node n_ : procs.get(i).getSuccessors(curr.getNodes().get(i))){
                    //CompositeNode curr_ = new CompositeNode((LinkedList<Node>)curr.getNodes().clone());
                    CompositeNode curr_ = curr.clone();
                    curr_.getNodes().set(i,n_);
                    curr_.updateState(n_); //if there are any modifications to shared vars on n_ then update global state
                    CompositeNode toOld = m.search(curr_);
                    if (toOld == null){
                        //System.out.println("curr:"+curr);
                        //System.out.println("curr_:"+curr_);
                        m.addNode(curr_);
                        m.addEdge(curr,curr_);
                        set.add(curr_);
                    }
                    else{
                        m.addEdge(curr,toOld);
                    }
                }
            }
        }
        //ExplicitModel res = m.flatten();
        System.out.println(m.toString());
        return m;
        //TODO: flatten all states (union)
    }

}
