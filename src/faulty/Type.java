package faulty;

public enum Type {
	INT,
	BOOL;
	
	@Override
	public String toString() {
		switch(this) {
			case INT:
				return "int";
	        case BOOL:
				return "bool";
		}
		
		return null;
	}
	
	public boolean isBOOLEAN() {
		if (this == Type.BOOL) {
			return true;
		}
		
		return false;
	}
	
	public boolean isINT() {
		if (this == Type.INT) {
			return true;
		}
		
		return false;
	}

}
