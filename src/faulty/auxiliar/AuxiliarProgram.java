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

}
