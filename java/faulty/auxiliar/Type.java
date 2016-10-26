package faulty.auxiliar;

public enum Type {
	INT,
	BOOL,
    ENUMERATED,
	UNDEFINED,
	ERROR;

    private String stringValue;

	@Override
	public String toString() {
        switch(this) {
            case INT:
                return new String("INT");
                
            case BOOL:
                return new String("BOOL");

            case UNDEFINED:
                return new String("UNDEFINED");
                
            case ERROR:
                return new String("ERROR");
                
            case ENUMERATED:
                return new String("ENUMERATED");
                
        }

        return new String("NONE");
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


    public boolean isEnumerated() {
       if (this == Type.ENUMERATED) {
           return true;
       }
       return false;
    }

    /**
     * @return The name of the type. 
     *  e.g : Type.INT --> "INT", 
     *        Type.ENUMERATED --> "<Name of the enumerated>",etc
     **/
    public String getStringValue(){
        return stringValue;
    }

    /**
    * Set the name of the type only if is an Enumerated, if not rewrites the original types.
    **/
    public void setStringValue(String val){
        switch(this) {
            case INT:
                      stringValue= new String("INT");
                      break;
            case BOOL:
                      stringValue= new String("BOOL");
                      break;
            case UNDEFINED:
                      stringValue= new String("UNDEFINED");
                      break;
                      
            case ERROR:
                      stringValue= new String("ERROR");
                      break;
            case ENUMERATED:
                      stringValue= new String(val); //copy el value of the enumerated type.
                      break;
        }
        
    }

}
