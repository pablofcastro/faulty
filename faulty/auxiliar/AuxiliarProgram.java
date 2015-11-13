package faulty.auxiliar;

import java.util.*;

public class AuxiliarProgram extends AuxiliarProgramNode{
	
	AuxiliarGlobalVarCollection globalVars;
	AuxiliarChannelCollection channels;
	AuxiliarProcessCollection process;
	AuxiliarMain mainProgram;
	
	public AuxiliarProgram(AuxiliarGlobalVarCollection gVars, AuxiliarChannelCollection channels, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
		this.globalVars = gVars;
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
		
	}
	
	public AuxiliarProgram(AuxiliarChannelCollection channels, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
		this.globalVars = new AuxiliarGlobalVarCollection();
		this.channels=channels;
		this.process = process;
		this.mainProgram = mainProgram;
		
	}
	
	public AuxiliarProgram(AuxiliarGlobalVarCollection gVars, AuxiliarProcessCollection process, AuxiliarMain mainProgram){
		this.globalVars = gVars;
		this.channels= new AuxiliarChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
	}
	
	public AuxiliarProgram( AuxiliarProcessCollection process, AuxiliarMain mainProgram){
		this.globalVars = new AuxiliarGlobalVarCollection();
		this.channels=new AuxiliarChannelCollection();
		this.process = process;
		this.mainProgram = mainProgram;
	}
	
	public void accept(AuxiliarFaultyVisitor v){
	     v.visit(this);			
	}

}
