package formula;

public class PrettyPrintVisitor implements FormulaVisitor  {

	private String formula;
	
	/**
	 * 
	 * Initializes a new visitor.
	 */
	public PrettyPrintVisitor(){
		formula = new String();
	}
	
	@Override
	public void visit(Variable v) {
		formula =  v.toString();			
	}

	@Override
	public void visit(Constant c) {
		formula =  c.toString();
		
	}

	@Override
	public void visit(Negation n) {			
		n.getExpr1().accept(this);
		formula = n.toString() + this.getPrettyFormula();		
	}

	@Override
	public void visit(Implication i) {
		FormulaElement f1 = i.getExpr1();
		FormulaElement f2 = i.getExpr2();
		visitBinaryOp(f1,i.toString(),f2);
		
	}

	@Override
	public void visit(Conjunction c) {
		FormulaElement f1 = c.getExpr1();
		FormulaElement f2 = c.getExpr2();
		visitBinaryOp(f1,c.toString(),f2);
		
	}

	@Override
	public void visit(Disjunction d) {
		FormulaElement f1 = d.getExpr1();
		FormulaElement f2 = d.getExpr2();
		visitBinaryOp(f1,d.toString(),f2);
		
	}

	@Override
	public void visit(Next n) {
		n.getExpr1().accept(this);
		formula = n.toString() + this.getPrettyFormula();			
	}

	@Override
	public void visit(Until u) {
		u.getExpr1().accept(this);
		String f1 = this.getPrettyFormula();
		u.getExpr2().accept(this);
		String f2 = this.getPrettyFormula();
		formula = "("+ f1 + " "  + u.toString() + " " + f2 +")";		
	}

	@Override
	public void visit(OXX o) {
		visitXXFormula(o.toString(),o.getExpr1(),o.getExpr2());			
	}

	@Override
	public void visit(OXU o) {
		visitXUFormula(o.toString(),o.getExpr1(),o.getExpr2(), o.getExpr3());	
		
	}

	@Override
	public void visit(OUX o) {
		visitUXFormula(o.toString(),o.getExpr1(),o.getExpr2(), o.getExpr3());	
		
	}

	@Override
	public void visit(OUU o) {
		visitUUFormula(o.toString(),o.getExpr1(),o.getExpr2(), o.getExpr3(), o.getExpr4());	
		
	}
	
	public void visit(OX o) {
		// TBD
		
	}
	
	public void visit(OU o) {
		// TBD
		
	}

	public void visit(OW o) {
		// TBD
		
	}
	
	@Override
	public void visit(PXX p) {
		visitXXFormula(p.toString(),p.getExpr1(),p.getExpr2());			
	}

	@Override
	public void visit(PXU p) {
		visitXUFormula(p.toString(),p.getExpr1(),p.getExpr2(), p.getExpr3());	
		
	}

	@Override
	public void visit(PUX p) {
		visitUXFormula(p.toString(),p.getExpr1(),p.getExpr2(), p.getExpr3());	
		
	}

	@Override
	public void visit(PUU p) {
		visitUUFormula(p.toString(),p.getExpr1(),p.getExpr2(), p.getExpr3(), p.getExpr4());	
		
	}

	public void visit(PX p){
		//TBD
	}
	
	public void visit(PU p){
		//TBD
	}
	
	public void visit(PW p){
		//TBD
	}
	
	@Override
	public void visit(Recovery r) {
		r.getExpr1().accept(this);
		String s1 = this.getPrettyFormula();
		r.getExpr2().accept(this);
		String s2 = this.getPrettyFormula();
		formula = r.toString() + "( "+ s1 + " ~> "+ s2 +" )";
		
	}
	
	public void visit(RX r) {
		// TBD
		
	}
	
	public void visit(RU r) {
		// TBD
		
	}
	
	public void visit(RW r) {
		// TBD
		
	}

	public void visit(AX a) {
		// TBD
		
	}
	
	public void visit(AW a) {
		// TBD
		
	}
	
	public void visit(AU a) {
		// TBD
		
	}
	
	public void visit(EX E) {
		// TBD
		
	}
	
	public void visit(EW e) {
		// TBD
		
	}
	
	public void visit(EU e) {
		// TBD
		
	}
	
	
	@Override
	public void visit(AXX a) {
		visitXXFormula(a.toString(),a.getExpr1(),a.getExpr2());	
		
	}

	@Override
	public void visit(AXU a) {
		visitXUFormula(a.toString(),a.getExpr1(),a.getExpr2(), a.getExpr3());
		
	}

	@Override
	public void visit(AUX a) {
		visitUXFormula(a.toString(),a.getExpr1(),a.getExpr2(), a.getExpr3());
		
	}

	@Override
	public void visit(AUU a) {
		visitUUFormula(a.toString(),a.getExpr1(),a.getExpr2(), a.getExpr3(), a.getExpr4());
	}

	@Override
	public void visit(EXX e) {
		visitXXFormula(e.toString(),e.getExpr1(),e.getExpr2());	
		
	}

	@Override
	public void visit(EXU e) {
		visitXUFormula(e.toString(),e.getExpr1(),e.getExpr2(), e.getExpr3());	
		
	}

	@Override
	public void visit(EUX e) {
		visitUXFormula(e.toString(),e.getExpr1(),e.getExpr2(), e.getExpr3());	
		
	}

	@Override
	public void visit(EUU e) {
		visitUUFormula(e.toString(),e.getExpr1(),e.getExpr2(), e.getExpr3(), e.getExpr4());			
	}
	
	
	public void printFormula(){
	    System.out.println(formula);
	}
	
	public String getPrettyFormula(){
		return formula;
	}
	
	private void visitBinaryOp(FormulaElement f1, String op, FormulaElement f2 ){
		
		f1.accept(this);
		String s1 =  this.getPrettyFormula();
		f2.accept(this);
		String s2 =  this.getPrettyFormula();
		formula = ( "(" + s1 + " " + op + " " + s2 + ")" );
	}
	
	private void visitXXFormula(String op,FormulaElement f1, FormulaElement f2){
		f1.accept(this);
		String s1 = this.getPrettyFormula();
		f2.accept(this);
		String s2 = this.getPrettyFormula();
		formula = op+ "( X"+ s1 + " ~> X"+ s2 +" )";		
	}
	
	private void visitXUFormula(String op,FormulaElement f1, FormulaElement f2, FormulaElement f3){
		f1.accept(this);
		String s1 = this.getPrettyFormula();
		f2.accept(this);
		String s2 = this.getPrettyFormula();
		f3.accept(this);
		String s3 = this.getPrettyFormula();
		formula = op+ "( X"+ s1 + " ~> "+ s2 +" U " + s3+ " )";		
	}
	
	private void visitUXFormula(String op, FormulaElement f1, FormulaElement f2, FormulaElement f3){
		f1.accept(this);
		String s1 = this.getPrettyFormula();
		f2.accept(this);
		String s2 = this.getPrettyFormula();
		f3.accept(this);
		String s3 = this.getPrettyFormula();
		formula = op + "( "+ s1 + " U " + s2 + " ~>  X" + s3 + " )";		
	}
	
	private void visitUUFormula(String op, FormulaElement f1, FormulaElement f2, FormulaElement f3, FormulaElement f4){
		f1.accept(this);
		String s1 = this.getPrettyFormula();
		f2.accept(this);
		String s2 = this.getPrettyFormula();
		f3.accept(this);
		String s3 = this.getPrettyFormula();
		f4.accept(this);
		String s4 = this.getPrettyFormula();
		formula = op + "( "+ s1 + " U " + s2 + " ~> " + s3 + " U " + s4 + " )";		
	}

}
