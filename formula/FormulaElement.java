package formula;

public interface FormulaElement {
	 	
	 public void accept(FormulaVisitor visitor);
     public String toString();
}
