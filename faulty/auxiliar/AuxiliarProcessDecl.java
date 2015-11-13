package faulty.auxiliar;


	public class AuxiliarProcessDecl extends AuxiliarProgramNode {

		String name;
		String type;
		
		public AuxiliarProcessDecl(String n, String t){
			this.name = n;
			this.type = t;
		}
		
		public String getType(){
			
			return type;
		}
		
		
	    public String getName(){
			
			return name;
		}
	    
	    
		public void accept(AuxiliarFaultyVisitor v){
		     v.visit(this);			
		}
		
	}
	

