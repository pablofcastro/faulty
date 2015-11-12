package faulty.auxiliar;

public enum Type {
	INT,
	BOOL,
	UNDEFINED,
	ERROR;
	
	@Override
	public String toString() {
		switch(this) {
			case INT:
				return "INT";
	        case BOOL:
				return "BOOL";
	        case UNDEFINED:
				return "UNDEFINED";
	        case ERROR:
				return "ERROR";

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
	
	public boolean isUndefined() {
		if (this == Type.UNDEFINED) {
			return true;
		}
		
		return false;
	}

}
