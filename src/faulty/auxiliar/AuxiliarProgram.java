package faulty.auxiliar;

import java.util.*;


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

    public String toJava(){
        String tEnums,prog,globals,procs,main;
        prog = "";
        tEnums = "";
        for (int i = 0; i < enumTypes.size(); i++){
            tEnums += "public enum " + enumTypes.get(i).getName() + "{";
            for (int j = 0; j < enumTypes.get(i).cons.length; j++){
                tEnums += enumTypes.get(i).getCons(j);
                if (j == enumTypes.get(i).cons.length-1)
                    tEnums += "}\n";
                else
                    tEnums += ",";
            } 
        }

        procs = "";
        for (int i = 0; i < process.getProcessList().size(); i++){
            procs += process.getProcessList().get(i).toJava() + "\n\n";
        }

        globals = "";
        for (int i = 0; i < globalVars.getBoolVars().size(); i++){
            AuxiliarVar v = globalVars.getBoolVars().get(i);
            globals += "public "+v.getType() + " " + v.getName() + ";\n";
        }
        for (int i = 0; i < globalVars.getIntVars().size(); i++){
            AuxiliarVar v = globalVars.getIntVars().get(i);
            globals += "public "+v.getType() + " " + v.getName() + ";\n";
        }
        for (int i = 0; i < globalVars.getEnumVars().size(); i++){
            AuxiliarVar v = globalVars.getEnumVars().get(i);
            globals += "public "+v.getType() + " " + v.getName() + ";\n";
        }

        main = "  public static void main(String[] args){\n";
        for (int i = 0; i < mainProgram.getProcessDecl().size(); i++){
            AuxiliarProcessDecl curr = mainProgram.getProcessDecl().get(i);
            main += "    "+ curr.getType() + " " + curr.getName() + " = new "+curr.getType()+"()"+";\n";
        }
        for (int i = 0; i < mainProgram.getProcessDecl().size(); i++){
            AuxiliarProcessDecl curr = mainProgram.getProcessDecl().get(i);
            main += "    "+ curr.getName() + ".start();\n";
        }
        main += "}\n\n";

        prog += "public class Program {\n\n" + tEnums + globals + procs + main + "}";
        return prog;
    }

}
