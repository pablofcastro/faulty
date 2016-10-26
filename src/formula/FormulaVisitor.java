package formula;

public interface FormulaVisitor {
	
	public void visit(Variable v);
	public void visit(Constant c);
	public void visit(Negation n);
	public void visit(Implication i);
	public void visit(EqComparison e);
    public void visit(Conjunction c);
	public void visit(Disjunction d);
	public void visit(Next n);
	public void visit(Until u);
    public void visit(Weak w);
	public void visit(OXX o);
	public void visit(OXU o);
	public void visit(OXW o);
	public void visit(OUX o);
	public void visit(OUU o);
    public void visit(OUW o);
    public void visit(OWX o);
	public void visit(OWU o);
    public void visit(OWW o); 
    public void visit(OX o);
	public void visit(OU o);
	public void visit(OW o);
	public void visit(PXX o);
	public void visit(PXU o);
	public void visit(PXW o);
	public void visit(PUX o);
	public void visit(PUU o);
    public void visit(PUW o);
    public void visit(PWX o);
	public void visit(PWU o);
    public void visit(PWW o); 
    public void visit(PX p);
	public void visit(PU p);
	public void visit(PW p);
	public void visit(RX r);
	public void visit(RU r);
	public void visit(RW r);
	public void visit(RXX o);
	public void visit(RXU o);
	public void visit(RXW o);
    public void visit(RUX o);
	public void visit(RUU o);
    public void visit(RUW o);
    public void visit(RWX o);
	public void visit(RWU o);
    public void visit(RWW o);
    public void visit(AXX a);
	public void visit(AXU a);
	public void visit(AUX a);
	public void visit(AUU a);
	public void visit(AX a);
	public void visit(AU a);
	public void visit(AW a);
	public void visit(EXX e);
	public void visit(EXU e);
	public void visit(EUX e);
	public void visit(EUU e);
	public void visit(EX e);
	public void visit(EU e);
	public void visit(EW e);
}


