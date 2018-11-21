package faulty.auxiliar;

import java.util.*;

/**
 * This class represents the symbols table, which is as an array of linked lists.
 * 
 */
public class SymbolsTable {
	
	private ArrayList<TableLevel> table;
	private int currentLevel;

	/**
	 * Constructor
	 */
	public SymbolsTable() {
		table = new ArrayList<TableLevel>();
		currentLevel = 0;
		table.add(currentLevel,new TableLevel());
	}

	/**
	 * Get the level
	 */
	public int getLevel() {
		return currentLevel;
	}
	
	/**
	 * Increment the level
	 */
	public void incrementLevel() {
		currentLevel++;
		table.add(currentLevel,new TableLevel());
	}

	/**
	 * Decrement the level
	 */
	public void decrementLevel() {
		table.get(currentLevel).deleteLevel();
		currentLevel--;
	}

	/**
	 * Adds a channel in the given level.
	 */
	public boolean addSymbol(AuxiliarChannel ch) {
		if (!existsChannel(ch.getName(),currentLevel)) {
			table.get(currentLevel).addChannel(ch);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a variable in the given level.
	 */
	public boolean addSymbol(AuxiliarVar var) {
		if (!existsVar(var.getName(),currentLevel)) {
			table.get(currentLevel).addVar(var);
			return true;
		} else {
			return false;
		}
	}
    
    
    /**
	 * Adds a parameter in the given level.
	 */
	public boolean addSymbol(AuxiliarParam par) {
		if (!exists(par.getDeclarationName(),currentLevel)) { //if already not exist an element with the same id at the given level.
			table.get(currentLevel).addParam(par);
			return true;
		} else {
			return false;
		}
	}

	
	/**
	 * Adds a process in the given level.
	 */
	public boolean addSymbol(AuxiliarProcess proc) {
		if (!existsProcess(proc.getName(),currentLevel)) {
			table.get(currentLevel).addProcess(proc);
			return true;
		} else {
			return false;
		}
	}

    /**
	 * Adds a enumerated type in the given level.
	 */
	public boolean addSymbol(AuxiliarEnumType eType) {
		if (!existsEnumeratedType(eType.getName(),currentLevel)) {
			table.get(currentLevel).addEnumeratedType(eType);
			return true;
		} else {
			return false;
		}
	}
    


	/**
	 * Get the symbols list at a given livel.
	 */
	public TableLevel getLevelSymbols(int level) {
		return table.get(level);
	}
	
	/**
	 * Empty the table
	 */
	public void makeEmpty() {
		table.clear();
	}

	/**
	 * Empty level
	 */
	public void emptyCurrentLevel() {
		table.get(currentLevel).deleteLevel();
	}

	/**
	 * Returns true if an element with the same id already exists at the given level. 
	 */
	private boolean exists(String name,int level) {
		
		return this.getLevelSymbols(level).existNameLevel(name);
	}
	
	/**
	 * Returns true if a channel with the same id already exists at the given level. 
	 */
	private boolean existsChannel(String name,int level) {
		
		return table.get(level).existChannel(name);
	}

	/**
	 * Returns true if a variable with the same id already exists at the given level. 
	 */
	private boolean existsVar(String name,int level) {
		
		return table.get(level).existVar(name);
	}

    /**
	 * Returns true if a parameter with the same id already exists at the given level.
	 */
	private boolean existsParam(String name,int level) {
		
		return table.get(level).existParam(name);
	}

    
    
	/**
	 * Returns true if an process with the same id already exists at the given level. 
	 */
	private boolean existsProcess(String name,int level) {
		
		return table.get(level).existProcess(name);
	}

    
    /**
	 * Returns true if a Enumerated Type with the same name already exists at the given level.
	 */
	private boolean existsEnumeratedType(String name,int level) {
		
		return table.get(level).existEnumeratedType(name);
	}


	/**
	 * Search a channel by id, and returns it if it exists. Otherwise returns null
	 */
	public AuxiliarChannel searchChannel(String id,int level) {
		
		return this.getLevelSymbols(level).getChannel(id);
	}
	
	/**
	 * Search a variable by id, and returns it if it exists. Otherwise returns null
	 */
	public AuxiliarVar searchVar(String id,int level) {
		
		return this.getLevelSymbols(level).getVar(id);
	}
	
    /**
	 * Search a parameter by id, and returns it if it exists. Otherwise returns null
	 */
	public AuxiliarParam searchParam(String id,int level) {
		
		return this.getLevelSymbols(level).getParam(id);
	}
	
    
    
	/**
	 * Search a process by id, and returns it if it exists. Otherwise returns null
	 */
	public AuxiliarProcess searchProcess(String id,int level) {
		
		return this.getLevelSymbols(level).getProcess(id);
	}
    
    /**
	 * Search a enumerated Type by id, and returns it if it exists. Otherwise returns null
	 */
	public AuxiliarEnumType searchEnumeratedType(String id,int level) {
		
		return this.getLevelSymbols(level).getEnumeratedType(id);
	}


	
}