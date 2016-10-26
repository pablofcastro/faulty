package faulty.auxiliar;



public interface AuxiliarFaultyVisitor {

	public void visit(AuxiliarProgram a);
    public void visit(AuxiliarEnumType a);
	public void visit(AuxiliarGlobalVarCollection a);
	public void visit(AuxiliarProcessCollection a);
	public void visit(AuxiliarChannelCollection a);
	public void visit(AuxiliarProcess a);
	public void visit(AuxiliarParam a);
    public void visit(AuxiliarChannel a);
	public void visit(AuxiliarBranch a);
	public void visit(AuxiliarChanAssign a);
	public void visit(AuxiliarChanAccess a);
	public void visit(AuxiliarVarAssign a);
	public void visit(AuxiliarVar a);
	public void visit(AuxiliarAndBoolExp a);
	public void visit(AuxiliarBiimpBoolExp a);
	public void visit(AuxiliarOrBoolExp a);
	public void visit(AuxiliarNegBoolExp a);
	public void visit(AuxiliarGreaterBoolExp a);
	public void visit(AuxiliarLessBoolExp a);
	public void visit(AuxiliarEqBoolExp a);
	public void visit(AuxiliarConsBoolExp a);
	public void visit(AuxiliarNegIntExp a);
	public void visit(AuxiliarSumIntExp a);
	public void visit(AuxiliarDivIntExp a);
	public void visit(AuxiliarMultIntExp a);
	public void visit(AuxiliarConsIntExp a);
	public void visit(AuxiliarMain a);
	public void visit(AuxiliarProcessDecl a);
    public void visit(AuxiliarInvkProcess a);
	
	
	
}
