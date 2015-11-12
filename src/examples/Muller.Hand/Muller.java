import java.util.*;


import mc.*;
import mc.VarInfo.Type;
import formula.*;
import net.sf.javabdd.*;
import faulty.*;

/**
 * This class models the Muller circuti using the Faulty language.
 */
public class Muller{

	
	
	/**
	 * Prints the set of truth assignments specified by this list.
	 * @param sol
	 */
	private static void  printSolutions( List sol){

		Iterator i = sol.iterator();
		System.out.print("\nSolutions = ");          
		while (i.hasNext()) {     	
			byte[] c1 = (byte[])i.next();

			if (c1 != null){
				System.out.print("\n[");  
				for (int j=0; j < c1.length; j++   ){
					System.out.print(c1[j]); 

					if (j != c1.length -1) //if not is the last element, prints ","
						System.out.print(",");
				} 
				System.out.print("]\n");  
			}
		} 
		System.out.print("\n\n");  
	}
	/*//////////////////////////////// MAIN //////////////////////////////////////////////////////*/    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Muller2();
	}
	
	private static void Muller2(){
		// Muller with 2 inputs
		
		// we create the program
		// if x = z ^ y = z -> x,y:=!x,!y
		// z != maj -> z,u = maj,maj
		// x != z ^ y=z -> y = !y
		// x=z ^ y!=z -> x = !X
		// x=z ^ y =z -> x=!x
		// x=z ^ y =z -> y=!y
	
		BDDModel model = new BDDModel();
		Program myProgram = new Program(4,0,30,0,0,0, model, true);
		
		// we define the variables
		VarBool x = new VarBool("x", model);
		VarBool y = new VarBool("y", model);
		VarBool z = new VarBool("z", model);
		VarBool u = new VarBool("u", model);
		
		// define the expressions needed
		NegBoolExp not_x = new NegBoolExp(x);  // !x
		NegBoolExp not_y = new NegBoolExp(y);  // !y
		AndBoolExp a1 = new AndBoolExp(x,y);   // x&y
		AndBoolExp a2 = new AndBoolExp(x,z);   // x&z
		AndBoolExp a3 = new AndBoolExp(y,z);   // y&z
		OrBoolExp o1 = new OrBoolExp(a1,a2);   // (x&y || x&z)
		OrBoolExp maj = new OrBoolExp(o1,a3);  // (x&y || x&z !! y&z)
		BiimpBoolExp x_eq_z = new BiimpBoolExp(x,z); // x = z
		BiimpBoolExp y_eq_z = new BiimpBoolExp(y,z); // y = z
		BiimpBoolExp z_eq_maj = new BiimpBoolExp(z,maj); // z = maj
		NegBoolExp z_neq_maj = new NegBoolExp(z_eq_maj); // z != maj
		BiimpBoolExp x_eq_y = new BiimpBoolExp(x,y); // x = y
		NegBoolExp x_neq_y = new NegBoolExp(x_eq_y); // x != y
		NegBoolExp x_neq_z = new NegBoolExp(x_eq_z); // x != z
		NegBoolExp y_neq_z = new NegBoolExp(y_eq_z); // y != z
		AndBoolExp xz_and_yz = new AndBoolExp(x_eq_z, y_eq_z); // x=z & y=z
		AndBoolExp xz_and_nyz = new AndBoolExp(x_eq_z, y_neq_z); // x=z & y!=z
		AndBoolExp nxz_and_yz = new AndBoolExp(x_neq_z, y_eq_z); // x!=z & y=z
		
	
		
		// we define the assignations
		VarAssign assign1 = new VarAssign(x,not_x); 
		VarAssign assign2 = new VarAssign(y, not_y);
		LinkedList<VarAssign> cxy = new LinkedList<VarAssign>();
		cxy.add(assign1);
		cxy.add(assign2);
		ListAssign chxy = new ListAssign(cxy);
		VarAssign assign3 = new VarAssign(z,maj);
		VarAssign assign4 = new VarAssign(u, maj);
		LinkedList<VarAssign> czu = new LinkedList<VarAssign>();
		cxy.add(assign3);
		cxy.add(assign4);
		ListAssign chzu = new ListAssign(czu);
		
		// ad a process
		faulty.Process process = new faulty.Process("Process", 1, 3, 3, 1, model, true);
		process.addVarBool(x);
		process.addVarBool(y);
		process.addVarBool(z);
		process.addVarBool(u);
		process.setInitialCond(x.getBDD().and(y.getBDD()).and(z.getBDD()));
		process.setNormativeCond(x.getBDD().biimp(y.getBDD()));
		ConsBoolExp tt = new ConsBoolExp(true);
		
		// we create the branches
		Branch branch1 = new Branch(xz_and_yz, chxy, false, process);
		Branch branch2 = new Branch(z_neq_maj, chzu, false, process);
		Branch branch3 = new Branch(xz_and_nyz, assign1, false, process);
		Branch branch4 = new Branch(nxz_and_yz, assign2, false, process);
		Branch branch5 = new Branch(xz_and_yz, assign1, false, process);
		Branch branch6 = new Branch(xz_and_yz, assign2, false, process);
		process.addBranch(branch1);
		process.addBranch(branch2);
		process.addBranch(branch3);
		process.addBranch(branch4);
		process.addBranch(branch5);
		process.addBranch(branch6);
		
		
		// add the process to the program
		myProgram.addProcess(process);
		
		
		// we model check a property
		FormulaElement vx = new Variable("x");
		FormulaElement vy = new Variable("y");
		FormulaElement vz = new Variable("z");
		FormulaElement vxy = new Conjunction("&", vx, vy);
		FormulaElement vxyz = new Conjunction("&", vxy, vz);
				
		FormulaElement neg_x = new Negation("!x",vx);
		FormulaElement neg_y = new Negation("!y",vy);
		FormulaElement neg_z = new Negation("!z", vz);
		FormulaElement nvxy = new Conjunction("&", neg_x, neg_y);
		FormulaElement nvxyz = new Conjunction("&", nvxy, neg_z);
		
		
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		
		FormulaElement au = new AU("", vz, nvxyz);
		FormulaElement imp = new Implication("", vxyz, au);
		FormulaElement eu = new EU("", t, neg_z);
	
		
		//myProgram.buildModel().getTransitions().printSet();
		if (DCTL_MC.mc_algorithm(imp, myProgram.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}			
	}
	
	
	private static void Muller2Improved(){
		// Muller with 2 inputs
		
		// we create the program
		// if x = z ^ y = z -> x,y:=!x,!y
		// z != maj -> z = maj
		// x != z ^ y=z -> y = !y
		// x=z ^ y!=z -> x = !x
		// x=z ^ y=z -> x=!x
		// x=z ^ y=z -> y=!y
	
		BDDModel model = new BDDModel();
		Program myProgram = new Program(4,0,6,0,0,0, model,true);
		
		// we define the variables
		VarBool x = new VarBool("x", model);
		VarBool y = new VarBool("y", model);
		VarBool z = new VarBool("z", model);
		
		// define the expressions needed
		NegBoolExp not_x = new NegBoolExp(x);  // !x
		NegBoolExp not_y = new NegBoolExp(y);  // !y
		AndBoolExp a1 = new AndBoolExp(x,y);   // x&y
		AndBoolExp a2 = new AndBoolExp(x,z);   // x&z
		AndBoolExp a3 = new AndBoolExp(y,z);   // y&z
		OrBoolExp o1 = new OrBoolExp(a1,a2);   // (x&y || x&z)
		OrBoolExp maj = new OrBoolExp(o1,a3);  // (x&y || x&z !! y&z)
		BiimpBoolExp x_eq_z = new BiimpBoolExp(x,z); // x = z
		BiimpBoolExp y_eq_z = new BiimpBoolExp(y,z); // y = z
		BiimpBoolExp z_eq_maj = new BiimpBoolExp(z,maj); // z = maj
		NegBoolExp z_neq_maj = new NegBoolExp(z_eq_maj); // z != maj
		BiimpBoolExp x_eq_y = new BiimpBoolExp(x,y); // x = y
		NegBoolExp x_neq_y = new NegBoolExp(x_eq_y); // x != y
		NegBoolExp x_neq_z = new NegBoolExp(x_eq_z); // x != z
		NegBoolExp y_neq_z = new NegBoolExp(y_eq_z); // y != z
		AndBoolExp xz_and_yz = new AndBoolExp(x_eq_z, y_eq_z); // x=z & y=z
		AndBoolExp xz_and_nyz = new AndBoolExp(x_eq_z, y_neq_z); // x=z & y!=z
		AndBoolExp nxz_and_yz = new AndBoolExp(x_neq_z, y_eq_z); // x!=z & y=z
		
	
		
		// we define the assignations
		VarAssign assign1 = new VarAssign(x,not_x); 
		VarAssign assign2 = new VarAssign(y, not_y);
		LinkedList<VarAssign> cxy = new LinkedList<VarAssign>();
		cxy.add(assign1);
		cxy.add(assign2);
		ListAssign chxy = new ListAssign(cxy);
		VarAssign assign3 = new VarAssign(z,maj);
		//VarAssign assign4 = new VarAssign(u, maj);
		//LinkedList<VarAssign> czu = new LinkedList<VarAssign>();
		//cxy.add(assign3);
		//cxy.add(assign4);
		//ListAssign chz = new ListAssign(cz);
		
		// ad a process
		faulty.Process process = new faulty.Process("Process", 1, 3, 3, 1, model, true);
		process.addVarBool(x);
		process.addVarBool(y);
		process.addVarBool(z);
		//process.addVarBool(u);
		process.setInitialCond(x.getBDD().and(y.getBDD()).and(z.getBDD()));
		process.setNormativeCond(x.getBDD().biimp(y.getBDD()));
		ConsBoolExp tt = new ConsBoolExp(true);
		
		// we create the branches
		Branch branch1 = new Branch(xz_and_yz, chxy, false, process);
		Branch branch2 = new Branch(z_neq_maj, assign3, false, process);
		Branch branch3 = new Branch(xz_and_nyz, assign1, false, process);
		Branch branch4 = new Branch(nxz_and_yz, assign2, false, process);
		Branch branch5 = new Branch(xz_and_yz, assign1, false, process);
		Branch branch6 = new Branch(xz_and_yz, assign2, false, process);
		process.addBranch(branch1);
		process.addBranch(branch2);
		process.addBranch(branch3);
		process.addBranch(branch4);
		process.addBranch(branch5);
		process.addBranch(branch6);
		
		
		// add the process to the program
		myProgram.addProcess(process);
		
		
		// we model check a property
		FormulaElement vx = new Variable("x");
		FormulaElement vy = new Variable("y");
		FormulaElement vz = new Variable("z");
		FormulaElement vxy = new Conjunction("&", vx, vy);
		FormulaElement vxyz = new Conjunction("&", vxy, vz);
		
		
				
		FormulaElement neg_x = new Negation("!x",vx);
		FormulaElement neg_y = new Negation("!y",vy);
		FormulaElement neg_z = new Negation("!z", vz);
		FormulaElement ximpy = new Disjunction("",neg_x,vy);
		FormulaElement nvxy = new Conjunction("&", neg_x, neg_y);
		FormulaElement nvxyz = new Conjunction("&", nvxy, neg_z);
		
		
		FormulaElement au = new AW("", vz, nvxy);
		FormulaElement ou = new OW("", vz, nvxy);
		FormulaElement ant = new Negation("",vxyz);
		FormulaElement form = new Disjunction("", ant, au);
		
		
		FormulaElement imp = new Implication("", vxyz, au);
	
		FormulaElement imp2 = new Implication("", vx, vy);
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		FormulaElement ex = new AX("",vz, null);
		//FormulaElement ex = new AX("",vz, null);
		FormulaElement nex = new Negation("", ex);
		//myProgram.buildModel().getTransitions().printSet();
		//System.out.println("numero de variables:"+myProgram.declaredVars);
		//System.out.println(myProgram.buildModel().getTransitions().satCount());

		if (DCTL_MC.mc_algorithm(ou, myProgram.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}
	}
	
	/**
	 * It creates a join for two muller circuit
	 * it is only a majority circuit
	 */
	public static faulty.Process join(VarBool x, VarBool y, VarBool z, BDDModel model){
		
		AndBoolExp a1 = new AndBoolExp(x,y);   // x&y
		AndBoolExp a2 = new AndBoolExp(x,z);   // x&z
		AndBoolExp a3 = new AndBoolExp(y,z);   // y&z
		OrBoolExp o1 = new OrBoolExp(a1,a2);   // (x&y || x&z)
		OrBoolExp maj = new OrBoolExp(o1,a3);  // (x&y || x&z !! y&z)
		BiimpBoolExp x_eq_z = new BiimpBoolExp(x,z); // x = z
		BiimpBoolExp z_eq_maj = new BiimpBoolExp(z,maj); // z = maj
		NegBoolExp z_neq_maj = new NegBoolExp(z_eq_maj); // z != maj
		
		VarAssign assign = new VarAssign(z,maj);
		faulty.Process process = new faulty.Process("Join", 1, 3, 3, 1, model, true);
		process.addGlobalVarBool(x);
		process.addGlobalVarBool(y);
		process.addGlobalVarBool(z);
		Branch branch = new Branch(z_neq_maj, assign, false, process);
		process.addBranch(branch);
		process.setInitialCond(z.getBDD());
		process.setNormativeCond(Program.myFactory.one());
		return process;
	}
	
	
	/**
	 * It creates the instance of a circuit with the given inputs,
	 * only z is global when join is false, otherwise it is a circuit
	 * to join the others and all the variables are global
	 */
	public static faulty.Process Muller2Par(VarBool x, VarBool y, VarBool z, boolean join, BDDModel model){
		// Parametric Muller with 2 inputs
		
		// we create the program
		// if x = z ^ y = z -> x,y:=!x,!y
		// z != maj -> z = maj
		// x != z ^ y=z -> y = !y
		// x=z ^ y!=z -> x = !x
		// x=z ^ y=z -> x=!x
		// x=z ^ y =z -> y=!y
			
		//BDDModel model = new BDDModel();
		//Program myProgram = new Program(4,0,30,0,0,0, model);
			
		// we define the variables
		//VarBool x = new VarBool("x", model);
		//VarBool y = new VarBool("y", model);
		//VarBool z = new VarBool("z", model);
		
		// define the expressions needed
		NegBoolExp not_x = new NegBoolExp(x);  // !x
		NegBoolExp not_y = new NegBoolExp(y);  // !y
		AndBoolExp a1 = new AndBoolExp(x,y);   // x&y
		AndBoolExp a2 = new AndBoolExp(x,z);   // x&z
		AndBoolExp a3 = new AndBoolExp(y,z);   // y&z
		OrBoolExp o1 = new OrBoolExp(a1,a2);   // (x&y || x&z)
		OrBoolExp maj = new OrBoolExp(o1,a3);  // (x&y || x&z !! y&z)
		BiimpBoolExp x_eq_z = new BiimpBoolExp(x,z); // x = z
		BiimpBoolExp y_eq_z = new BiimpBoolExp(y,z); // y = z
		BiimpBoolExp z_eq_maj = new BiimpBoolExp(z,maj); // z = maj
		NegBoolExp z_neq_maj = new NegBoolExp(z_eq_maj); // z != maj
		BiimpBoolExp x_eq_y = new BiimpBoolExp(x,y); // x = y
		NegBoolExp x_neq_y = new NegBoolExp(x_eq_y); // x != y
		NegBoolExp x_neq_z = new NegBoolExp(x_eq_z); // x != z
		NegBoolExp y_neq_z = new NegBoolExp(y_eq_z); // y != z
		AndBoolExp xz_and_yz = new AndBoolExp(x_eq_z, y_eq_z); // x=z & y=z
		AndBoolExp xz_and_nyz = new AndBoolExp(x_eq_z, y_neq_z); // x=z & y!=z
		AndBoolExp nxz_and_yz = new AndBoolExp(x_neq_z, y_eq_z); // x!=z & y=z
				
			
				
		// we define the assignations
		VarAssign assign1 = new VarAssign(x,not_x); 
		VarAssign assign2 = new VarAssign(y, not_y);
		LinkedList<VarAssign> cxy = new LinkedList<VarAssign>();
		cxy.add(assign1);
		cxy.add(assign2);
		ListAssign chxy = new ListAssign(cxy);
		VarAssign assign3 = new VarAssign(z,maj);
		
				
		// add a process
		faulty.Process process = new faulty.Process("Muller1", 1, 3, 3, 1, model, true);
		
		if (!join){
			process.addVarBool(x);
			process.addVarBool(y);
			process.addGlobalVarBool(z); // z is global
		}
		else{
			process.addGlobalVarBool(x);
			process.addGlobalVarBool(y);
			process.addGlobalVarBool(z);
		}
		
		//process.addVarBool(u);
		process.setInitialCond(x.getBDD().and(y.getBDD()).and(z.getBDD()));
		process.setNormativeCond(x.getBDD().biimp(y.getBDD()));
		ConsBoolExp tt = new ConsBoolExp(true);
				
		// we create the branches
		Branch branch1 = new Branch(xz_and_yz, chxy, false, process);
		Branch branch2 = new Branch(z_neq_maj, assign3, false, process);
		Branch branch3 = new Branch(xz_and_nyz, assign1, false, process);
		Branch branch4 = new Branch(nxz_and_yz, assign2, false, process);
		Branch branch5 = new Branch(xz_and_yz, assign1, false, process);
		Branch branch6 = new Branch(xz_and_yz, assign2, false, process);
		process.addBranch(branch1);
		process.addBranch(branch2);
		process.addBranch(branch3);
		process.addBranch(branch4);
		process.addBranch(branch5);
		process.addBranch(branch6);
				
		return process;		// return de process
		// add the process to the program
		//myProgram.addProcess(process);	
		
		
	}
	
	public static void testMullerPar(){
		BDDModel model = new BDDModel();
		Program muller2 = new Program(4,0,6,0,0,0, model, true);
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool z0 = new VarBool("z0", model);
		faulty.Process C1 = Muller2Par(x0,x1,z0, false, model);
		muller2.addBoolVar(z0);
		muller2.addProcess(C1);
		muller2.getBDD().printSet();
	}
	
	public static void Muller4(){
		BDDModel model = new BDDModel();
		Program muller4 = new Program(4,0,14,0,0,0, model, true);
		
		
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool z0 = new VarBool("z0", model);
		faulty.Process C1 = Muller2Par(x0,x1,z0, false, model);
		
		VarBool x2 = new VarBool("x2", model);
		VarBool x3 = new VarBool("x3", model);
		VarBool z1 = new VarBool("z1", model);

		faulty.Process C2 = Muller2Par(x2,x3,z1,false,model);
		
		VarBool z2 = new VarBool("z2", model);
		faulty.Process  C3 = join(z0,z1,z2,  model);
		C3.setInitialCond(z2.getBDD());
		muller4.addBoolVar(z0);
		muller4.addBoolVar(z1);
		muller4.addBoolVar(z2);
		muller4.addProcess(C1);
		muller4.addProcess(C2);
		muller4.addProcess(C3);
		
		
		// we model check a property
		FormulaElement vx0 = new Variable("x0");
		FormulaElement vx1 = new Variable("x1");
		FormulaElement vx2 = new Variable("x2");
		FormulaElement vx3 = new Variable("x3");
		FormulaElement vz0 = new Variable("z0");
		FormulaElement vz1 = new Variable("z1");
		FormulaElement vz2 = new Variable("z2");
		FormulaElement vx0x1 = new Conjunction("&", vx0, vx1);
		FormulaElement vx0x1x2 = new Conjunction("&",vx0x1, vx2);
		FormulaElement vx0x1x2x3 = new Conjunction("&",vx0x1x2, vx3);
		
		FormulaElement vx0x1x2x3z2 = new Conjunction("&",vx0x1x2x3,vz2);		
		FormulaElement vx0y1z0 = new Conjunction("&", vx0x1, vz0);
						
		FormulaElement neg_x0 = new Negation("!x0",vx0);
		FormulaElement neg_x1 = new Negation("!x1",vx1);
		FormulaElement neg_x2 = new Negation("!x2", vx2);
		FormulaElement neg_x3 = new Negation("!x2", vx3);
		FormulaElement neg_z0 = new Negation("!z2", vz0);
		FormulaElement neg_z2 = new Negation("!z2", vz2);
		FormulaElement nvx0x1 = new Conjunction("&", neg_x0, neg_x1);
		FormulaElement nvx0x1x2 = new Conjunction("&", nvx0x1, neg_x2);
		FormulaElement nvx0x1x2x3 = new Conjunction("&", nvx0x1x2, neg_x3);
				
		FormulaElement au = new AW("", vz2, nvx0x1x2x3);
		FormulaElement imp = new Implication("", vx0x1x2x3z2, au);
			
		FormulaElement ou = new OW("", vz2, nvx0x1x2x3);
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		//muller4.buildModel().getTransitions().and(muller4.buildModel().getIni()).printSet();
		
		//System.out.println(muller4.buildModel().getTransitions().satCount());

		//if (DCTL_MC.mc_algorithm(au, muller4.buildModel())) {
		//			System.out.println("Property is TRUE in the model.");
		//}
		//else{
		//			System.out.println("Property is FALSE in the model.");
		//}		
		
		if (DCTL_MC.mc_algorithm(ou, muller4.buildModel())) {
			System.out.println("Property is TRUE in the model.");
		}
		else{
			System.out.println("Property is FALSE in the model.");
		}		
		
		
	}
	
	public static void Muller6(){
		BDDModel model = new BDDModel();
		Program muller6 = new Program(4,0,22,0,0,0, model, true);
		
		
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool z0 = new VarBool("z0", model);
		faulty.Process C1 = Muller2Par(x0,x1,z0, false, model);
		
		VarBool x2 = new VarBool("x2", model);
		VarBool x3 = new VarBool("x3", model);
		VarBool z1 = new VarBool("z1", model);

		faulty.Process C2 = Muller2Par(x2,x3,z1,false,model);
		
		VarBool z2 = new VarBool("z2", model);
		faulty.Process  C3 = join(z0,z1,z2,  model);
		C3.setInitialCond(z2.getBDD());
		muller6.addBoolVar(z0);
		muller6.addBoolVar(z1);
		muller6.addBoolVar(z2);
		muller6.addProcess(C1);
		muller6.addProcess(C2);
		muller6.addProcess(C3);
		
		VarBool x4 = new VarBool("x4", model);
		VarBool x5 = new VarBool("x5", model);
		VarBool z3 = new VarBool("z3", model);
		faulty.Process C4 = Muller2Par(x4,x5,z3, false, model);
		C4.setInitialCond(z3.getBDD());
		muller6.addBoolVar(z3);
		muller6.addProcess(C4);
		
		VarBool z4 = new VarBool("z4", model);
		faulty.Process  C5 = join(z2,z3,z4,  model);
		C5.setInitialCond(z4.getBDD());
		muller6.addBoolVar(z4);
		muller6.addProcess(C5);
		
		// we model check a property
		FormulaElement vx0 = new Variable("x0");
		FormulaElement vx1 = new Variable("x1");
		FormulaElement vx2 = new Variable("x2");
		FormulaElement vx3 = new Variable("x3");
		FormulaElement vx4 = new Variable("x4");
		FormulaElement vx5 = new Variable("x5");
		
		FormulaElement vz0 = new Variable("z0");
		FormulaElement vz1 = new Variable("z1");
		FormulaElement vz2 = new Variable("z2");
		FormulaElement vz3 = new Variable("z3");
		FormulaElement vz4 = new Variable("z4");
		
		FormulaElement vx0x1 = new Conjunction("&", vx0, vx1);
		FormulaElement vx0x1x2 = new Conjunction("&",vx0x1, vx2);
		FormulaElement vx0x1x2x3 = new Conjunction("&",vx0x1x2, vx3);
		FormulaElement vx0x1x2x3x4 = new Conjunction("&",vx0x1x2x3, vx4);
		FormulaElement vx0x1x2x3x4x5 = new Conjunction("&",vx0x1x2x3x4, vx5);
		
		FormulaElement vx0x1x2x3z2 = new Conjunction("&",vx0x1x2x3,vz2);		
		FormulaElement vx0y1z0 = new Conjunction("&", vx0x1, vz0);
						
		FormulaElement neg_x0 = new Negation("!x0",vx0);
		FormulaElement neg_x1 = new Negation("!x1",vx1);
		FormulaElement neg_x2 = new Negation("!x2", vx2);
		FormulaElement neg_x3 = new Negation("!x2", vx3);
		FormulaElement neg_x4 = new Negation("!x2", vx4);
		FormulaElement neg_x5 = new Negation("!x2", vx5);
		FormulaElement neg_z0 = new Negation("!z2", vz0);
		FormulaElement neg_z2 = new Negation("!z2", vz2);
		FormulaElement nvx0x1 = new Conjunction("&", neg_x0, neg_x1);
		FormulaElement nvx0x1x2 = new Conjunction("&", nvx0x1, neg_x2);
		FormulaElement nvx0x1x2x3 = new Conjunction("&", nvx0x1x2, neg_x3);
		FormulaElement nvx0x1x2x3x4 = new Conjunction("&", nvx0x1x2x3, neg_x4);
		FormulaElement nvx0x1x2x3x4x5 = new Conjunction("&", nvx0x1x2x3x4, neg_x5);
				
		FormulaElement au = new AW("", vz4, nvx0x1x2x3x4);
		FormulaElement imp = new Implication("", vx0x1x2x3z2, au);
		FormulaElement ou = new OW("", vz4, nvx0x1x2x3x4);	
		
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		//muller4.buildModel().getTransitions().and(muller4.buildModel().getIni()).printSet();
		
		//System.out.println(muller6.buildModel().getTransitions().satCount());

		if (DCTL_MC.mc_algorithm(ou, muller6.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}		
		
	}
	
	public static void Muller8(){
		BDDModel model = new BDDModel();
		Program muller8 = new Program(4,0,30,0,0,0, model, true);
		
		// create one circuit of 4
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool z0 = new VarBool("z0", model);
		faulty.Process C1 = Muller2Par(x0,x1,z0, false, model);
		
		VarBool x2 = new VarBool("x2", model);
		VarBool x3 = new VarBool("x3", model);
		VarBool z1 = new VarBool("z1", model);

		faulty.Process C2 = Muller2Par(x2,x3,z1,false,model);
		
		VarBool z2 = new VarBool("z2", model);
		faulty.Process  C3 = join(z0,z1,z2, model);
		muller8.addBoolVar(z0);
		muller8.addBoolVar(z1);
		muller8.addBoolVar(z2);
		muller8.addProcess(C1);
		muller8.addProcess(C2);
		muller8.addProcess(C3);
		
		// ========= create another circuit of 4
		VarBool x4 = new VarBool("x4", model);
		VarBool x5 = new VarBool("x5", model);
		VarBool z3 = new VarBool("z3", model);
		faulty.Process C4 = Muller2Par(x4,x5,z3, false, model);
				
		VarBool x6 = new VarBool("x6", model);
		VarBool x7 = new VarBool("x7", model);
		VarBool z4 = new VarBool("z4", model);

		faulty.Process C5 = Muller2Par(x6,x7,z4,false,model);
				
		VarBool z5 = new VarBool("z5", model);
		faulty.Process  C6 = join(z3,z4,z5, model);
		muller8.addBoolVar(z3);
		muller8.addBoolVar(z4);
		muller8.addBoolVar(z5);
		muller8.addProcess(C4);
		muller8.addProcess(C5);
		muller8.addProcess(C6);
		
		// join both circuits
		VarBool z6 = new VarBool("z6", model);
		muller8.addBoolVar(z6);
		faulty.Process  C7 = join(z2,z5,z6, model);
		C7.setInitialCond(z6.getBDD());
		muller8.addProcess(C7);
	
		
		
		// we model check a property
		FormulaElement vx0 = new Variable("x0");
		FormulaElement vx1 = new Variable("x1");
		FormulaElement vx2 = new Variable("x2");
		FormulaElement vx3 = new Variable("x3");
		FormulaElement vx4 = new Variable("x4");
		FormulaElement vx5 = new Variable("x5");
		FormulaElement vx6 = new Variable("x6");
		FormulaElement vx7 = new Variable("x7");
		FormulaElement vz0 = new Variable("z0");
		FormulaElement vz1 = new Variable("z1");
		FormulaElement vz2 = new Variable("z2");
		FormulaElement vz3 = new Variable("z3");
		FormulaElement vz4 = new Variable("z4");
		FormulaElement vz5 = new Variable("z5");
		FormulaElement vz6 = new Variable("z6");
		
		
		FormulaElement vx0x1 = new Conjunction("&", vx0, vx1);
		FormulaElement vx0x1x2 = new Conjunction("&",vx0x1, vx2);
		FormulaElement vx0x1x2x3 = new Conjunction("&",vx0x1x2, vx3);
		FormulaElement vx0x1x2x3x4 = new Conjunction("&",vx0x1x2x3, vx4);
		FormulaElement vx0x1x2x3x4x5 = new Conjunction("&",vx0x1x2x3x4, vx5);
		FormulaElement vx0x1x2x3x4x5x6 = new Conjunction("&",vx0x1x2x3x4x5, vx6);
		FormulaElement vx0x1x2x3x4x5x6x7 = new Conjunction("&",vx0x1x2x3x4x5x6, vx7);
				
		FormulaElement vx0x1x2x3x4x5x6x7z6 = new Conjunction("&",vx0x1x2x3,vz6);		
		FormulaElement vx0y1z0 = new Conjunction("&", vx0x1, vz0);
								
		FormulaElement neg_x0 = new Negation("!x0",vx0);
		FormulaElement neg_x1 = new Negation("!x1",vx1);
		FormulaElement neg_x2 = new Negation("!x2", vx2);
		FormulaElement neg_x3 = new Negation("!x3", vx3);
		FormulaElement neg_x4 = new Negation("!x4",vx4);
		FormulaElement neg_x5 = new Negation("!x5",vx5);
		FormulaElement neg_x6 = new Negation("!x6", vx6);
		FormulaElement neg_x7 = new Negation("!x7", vx7);
		FormulaElement neg_vz6 = new Negation("!z6", vz6);
		
		FormulaElement nvx0x1 = new Conjunction("&", neg_x0, neg_x1);
		FormulaElement nvx0x1x2 = new Conjunction("&", nvx0x1, neg_x2);
		FormulaElement nvx0x1x2x3 = new Conjunction("&", nvx0x1x2, neg_x3);
		FormulaElement nvx0x1x2x3x4 = new Conjunction("&", nvx0x1x2x3, neg_x4);
		FormulaElement nvx0x1x2x3x4x5 = new Conjunction("&", nvx0x1x2x3x4, neg_x5);
		FormulaElement nvx0x1x2x3x4x5x6 = new Conjunction("&", nvx0x1x2x3x4x5, neg_x6);
		FormulaElement nvx0x1x2x3x4x5x6x7 = new Conjunction("&", nvx0x1x2x3x4x5x6, neg_x7);
						
		FormulaElement au = new AW("", vz6, nvx0x1x2x3x4x5x6x7);
		FormulaElement imp = new Implication("", vx0x1x2x3x4x5x6x7z6, au);
					
		FormulaElement ou = new OW("", vz6, nvx0x1x2x3x4x5x6x7);
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		
		//System.out.println(muller8.buildModel().getTransitions().satCount());
		if (DCTL_MC.mc_algorithm(ou, muller8.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}		
				
	}
	
	public static void Muller10(){
		BDDModel model = new BDDModel();
		Program muller10 = new Program(4,0,38,0,0,0, model, true);
		
		// create one circuit of 4
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool z0 = new VarBool("z0", model);
		faulty.Process C1 = Muller2Par(x0,x1,z0, false, model);
		
		VarBool x2 = new VarBool("x2", model);
		VarBool x3 = new VarBool("x3", model);
		VarBool z1 = new VarBool("z1", model);

		faulty.Process C2 = Muller2Par(x2,x3,z1,false,model);
		
		VarBool z2 = new VarBool("z2", model);
		faulty.Process  C3 = join(z0,z1,z2, model);
		muller10.addBoolVar(z0);
		muller10.addBoolVar(z1);
		muller10.addBoolVar(z2);
		muller10.addProcess(C1);
		muller10.addProcess(C2);
		muller10.addProcess(C3);
		
		// ========= create another circuit of 4
		VarBool x4 = new VarBool("x4", model);
		VarBool x5 = new VarBool("x5", model);
		VarBool z3 = new VarBool("z3", model);
		faulty.Process C4 = Muller2Par(x4,x5,z3, false, model);
				
		VarBool x6 = new VarBool("x6", model);
		VarBool x7 = new VarBool("x7", model);
		VarBool z4 = new VarBool("z4", model);

		faulty.Process C5 = Muller2Par(x6,x7,z4,false,model);
				
		VarBool z5 = new VarBool("z5", model);
		faulty.Process  C6 = join(z3,z4,z5, model);
		muller10.addBoolVar(z3);
		muller10.addBoolVar(z4);
		muller10.addBoolVar(z5);
		muller10.addProcess(C4);
		muller10.addProcess(C5);
		muller10.addProcess(C6);
		
		// join both circuits
		VarBool z6 = new VarBool("z6", model);
		muller10.addBoolVar(z6);
		faulty.Process  C7 = join(z2,z5,z6, model);
		C7.setInitialCond(z6.getBDD());
		muller10.addProcess(C7);
	
		// we create a new circuit of 2
		VarBool x8 = new VarBool("x8", model);
		VarBool x9 = new VarBool("x9", model);
		VarBool z7 = new VarBool("z7", model);
		muller10.addBoolVar(z7);
		faulty.Process C8 = Muller2Par(x8,x9,z7, false, model);
		C8.setInitialCond(z7.getBDD());
		muller10.addProcess(C8);
		
		
		
		// join both circuits
		VarBool z8 = new VarBool("z8", model);
		muller10.addBoolVar(z8);
		faulty.Process  C9 = join(z6,z7,z8, model);
		C9.setInitialCond(z8.getBDD());
		muller10.addProcess(C9);
		
		
		
		// we model check a property
		FormulaElement vx0 = new Variable("x0");
		FormulaElement vx1 = new Variable("x1");
		FormulaElement vx2 = new Variable("x2");
		FormulaElement vx3 = new Variable("x3");
		FormulaElement vx4 = new Variable("x4");
		FormulaElement vx5 = new Variable("x5");
		FormulaElement vx6 = new Variable("x6");
		FormulaElement vx7 = new Variable("x7");
		FormulaElement vx8 = new Variable("x8");
		FormulaElement vx9 = new Variable("x9");
		
		FormulaElement vz0 = new Variable("z0");
		FormulaElement vz1 = new Variable("z1");
		FormulaElement vz2 = new Variable("z2");
		FormulaElement vz3 = new Variable("z3");
		FormulaElement vz4 = new Variable("z4");
		FormulaElement vz5 = new Variable("z5");
		FormulaElement vz6 = new Variable("z6");
		FormulaElement vz7 = new Variable("z7");
		FormulaElement vz8 = new Variable("z8");
		
		
		FormulaElement vx0x1 = new Conjunction("&", vx0, vx1);
		FormulaElement vx0x1x2 = new Conjunction("&",vx0x1, vx2);
		FormulaElement vx0x1x2x3 = new Conjunction("&",vx0x1x2, vx3);
		FormulaElement vx0x1x2x3x4 = new Conjunction("&",vx0x1x2x3, vx4);
		FormulaElement vx0x1x2x3x4x5 = new Conjunction("&",vx0x1x2x3x4, vx5);
		FormulaElement vx0x1x2x3x4x5x6 = new Conjunction("&",vx0x1x2x3x4x5, vx6);
		FormulaElement vx0x1x2x3x4x5x6x7 = new Conjunction("&",vx0x1x2x3x4x5x6, vx7);
		FormulaElement vx0x1x2x3x4x5x6x7x8 = new Conjunction("&",vx0x1x2x3x4x5x6x7, vx8);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8, vx9);
				
		FormulaElement vx0x1x2x3x4x5x6x7z6 = new Conjunction("&",vx0x1x2x3,vz6);		
		FormulaElement vx0y1z0 = new Conjunction("&", vx0x1, vz0);
								
		FormulaElement neg_x0 = new Negation("!x0",vx0);
		FormulaElement neg_x1 = new Negation("!x1",vx1);
		FormulaElement neg_x2 = new Negation("!x2", vx2);
		FormulaElement neg_x3 = new Negation("!x3", vx3);
		FormulaElement neg_x4 = new Negation("!x4",vx4);
		FormulaElement neg_x5 = new Negation("!x5",vx5);
		FormulaElement neg_x6 = new Negation("!x6", vx6);
		FormulaElement neg_x7 = new Negation("!x7", vx7);
		FormulaElement neg_x8 = new Negation("!x6", vx8);
		FormulaElement neg_x9 = new Negation("!x7", vx9);
		FormulaElement neg_vz8 = new Negation("!z6", vz8);
		
		FormulaElement nvx0x1 = new Conjunction("&", neg_x0, neg_x1);
		FormulaElement nvx0x1x2 = new Conjunction("&", nvx0x1, neg_x2);
		FormulaElement nvx0x1x2x3 = new Conjunction("&", nvx0x1x2, neg_x3);
		FormulaElement nvx0x1x2x3x4 = new Conjunction("&", nvx0x1x2x3, neg_x4);
		FormulaElement nvx0x1x2x3x4x5 = new Conjunction("&", nvx0x1x2x3x4, neg_x5);
		FormulaElement nvx0x1x2x3x4x5x6 = new Conjunction("&", nvx0x1x2x3x4x5, neg_x6);
		FormulaElement nvx0x1x2x3x4x5x6x7 = new Conjunction("&", nvx0x1x2x3x4x5x6, neg_x7);
		FormulaElement nvx0x1x2x3x4x5x6x7x8 = new Conjunction("&", nvx0x1x2x3x4x5x6x7, neg_x8);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8, neg_x9);
						
		FormulaElement au = new AW("", vz8, nvx0x1x2x3x4x5x6x7x8x9);
		FormulaElement ou = new OW("", vz8, nvx0x1x2x3x4x5x6x7x8x9);
		//FormulaElement imp = new Implication("", vx0x1x2x3x4x5x6x7z6, au);
					
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		
		//System.out.println(muller10.buildModel().getTransitions().satCount());
		if (DCTL_MC.mc_algorithm(ou, muller10.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}		
			
		
		
		
	}
	
	public static void Muller16(){
		// an implementation of muller with 16 inputs
		BDDModel model = new BDDModel();
		Program muller16 = new Program(4,0,58,0,0,0, model, true);
		
		// create a circuit of 8
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool z0 = new VarBool("z0", model);
		faulty.Process C1 = Muller2Par(x0,x1,z0, false, model);
				
		VarBool x2 = new VarBool("x2", model);
		VarBool x3 = new VarBool("x3", model);
		VarBool z1 = new VarBool("z1", model);

		faulty.Process C2 = Muller2Par(x2,x3,z1,false,model);
				
		VarBool z2 = new VarBool("z2", model);
		faulty.Process  C3 = join(z0,z1,z2, model);
		muller16.addBoolVar(z0);
		muller16.addBoolVar(z1);
		muller16.addBoolVar(z2);
		muller16.addProcess(C1);
		muller16.addProcess(C2);
		muller16.addProcess(C3);
				
		VarBool x4 = new VarBool("x4", model);
		VarBool x5 = new VarBool("x5", model);
		VarBool z3 = new VarBool("z3", model);
		faulty.Process C4 = Muller2Par(x4,x5,z3, false, model);
						
		VarBool x6 = new VarBool("x6", model);
		VarBool x7 = new VarBool("x7", model);
		VarBool z4 = new VarBool("z4", model);

		faulty.Process C5 = Muller2Par(x6,x7,z4,false,model);
						
		VarBool z5 = new VarBool("z5", model);
		faulty.Process  C6 = join(z3,z4,z5, model);
		muller16.addBoolVar(z3);
		muller16.addBoolVar(z4);
		muller16.addBoolVar(z5);
		muller16.addProcess(C4);
		muller16.addProcess(C5);
		muller16.addProcess(C6);
				
		
		// Another circuit of 8
		// create a circuit of 8
		VarBool x8 = new VarBool("x8", model);
		VarBool x9 = new VarBool("x9", model);
		VarBool z6 = new VarBool("z6", model);
		faulty.Process C7 = Muller2Par(x8,x9,z6, false, model);
						
		VarBool x10 = new VarBool("x10", model);
		VarBool x11 = new VarBool("x11", model);
		VarBool z7 = new VarBool("z7", model);

		faulty.Process C8 = Muller2Par(x10,x11,z7,false,model);
						
		VarBool z8 = new VarBool("z8", model);
		faulty.Process  C9 = join(z6,z7,z8, model);
		muller16.addBoolVar(z6);
		muller16.addBoolVar(z7);
		muller16.addBoolVar(z8);
		muller16.addProcess(C7);
		muller16.addProcess(C8);
		muller16.addProcess(C9);
						
		VarBool x12 = new VarBool("x12", model);
		VarBool x13 = new VarBool("x13", model);
		VarBool z9 = new VarBool("z9", model);
		faulty.Process C10 = Muller2Par(x12,x12,z9, false, model);
								
		VarBool x14 = new VarBool("x14", model);
		VarBool x15 = new VarBool("x15", model);
		VarBool z10 = new VarBool("z10", model);

		faulty.Process C11 = Muller2Par(x14,x15,z10,false,model);
								
		VarBool z11 = new VarBool("z11", model);
		faulty.Process  C12 = join(z9,z10,z11, model);
		muller16.addBoolVar(z9);
		muller16.addBoolVar(z10);
		muller16.addBoolVar(z11);
		muller16.addProcess(C10);
		muller16.addProcess(C11);
		muller16.addProcess(C12);
		
		
		// join both circuits
		VarBool z12 = new VarBool("z12", model);
		muller16.addBoolVar(z12);
		faulty.Process  C13 = join(z8,z11,z12, model);
		C12.setInitialCond(z12.getBDD());
		muller16.addProcess(C12);
		
		// calculate the property
		// we model check a property
		FormulaElement vx0 = new Variable("x0");
		FormulaElement vx1 = new Variable("x1");
		FormulaElement vx2 = new Variable("x2");
		FormulaElement vx3 = new Variable("x3");
		FormulaElement vx4 = new Variable("x4");
		FormulaElement vx5 = new Variable("x5");
		FormulaElement vx6 = new Variable("x6");
		FormulaElement vx7 = new Variable("x7");
		FormulaElement vx8 = new Variable("x8");
		FormulaElement vx9 = new Variable("x9");
		FormulaElement vx10 = new Variable("x10");
		FormulaElement vx11 = new Variable("x11");
		FormulaElement vx12 = new Variable("x12");
		FormulaElement vx13 = new Variable("x13");
		FormulaElement vx14 = new Variable("x14");
		FormulaElement vx15 = new Variable("x15");
		
		
		
		FormulaElement vz0 = new Variable("z0");
		FormulaElement vz1 = new Variable("z1");
		FormulaElement vz2 = new Variable("z2");
		FormulaElement vz3 = new Variable("z3");
		FormulaElement vz4 = new Variable("z4");
		FormulaElement vz5 = new Variable("z5");
		FormulaElement vz6 = new Variable("z6");
		FormulaElement vz7 = new Variable("z7");
		FormulaElement vz8 = new Variable("z8");
		FormulaElement vz9 = new Variable("z9");
		FormulaElement vz10 = new Variable("z10");
		FormulaElement vz11 = new Variable("z11");
		FormulaElement vz12 = new Variable("z12");
				
				
		FormulaElement vx0x1 = new Conjunction("&", vx0, vx1);
		FormulaElement vx0x1x2 = new Conjunction("&",vx0x1, vx2);
		FormulaElement vx0x1x2x3 = new Conjunction("&",vx0x1x2, vx3);
		FormulaElement vx0x1x2x3x4 = new Conjunction("&",vx0x1x2x3, vx4);
		FormulaElement vx0x1x2x3x4x5 = new Conjunction("&",vx0x1x2x3x4, vx5);
		FormulaElement vx0x1x2x3x4x5x6 = new Conjunction("&",vx0x1x2x3x4x5, vx6);
		FormulaElement vx0x1x2x3x4x5x6x7 = new Conjunction("&",vx0x1x2x3x4x5x6, vx7);
		FormulaElement vx0x1x2x3x4x5x6x7x8 = new Conjunction("&",vx0x1x2x3x4x5x6x7, vx8);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8, vx9);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9, vx10);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10x11 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9x10, vx11);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10x11x12 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9x10x11, vx12);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10x11x12x13 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9x10x11x12, vx13);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9x10x11x12x13, vx14);
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14x15 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14, vx15);
		
						
		FormulaElement vx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14x15z12 = new Conjunction("&",vx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14x15,vz12);		
		FormulaElement vx0y1z0 = new Conjunction("&", vx0x1, vz0);
										
		FormulaElement neg_x0 = new Negation("!x0",vx0);
		FormulaElement neg_x1 = new Negation("!x1",vx1);
		FormulaElement neg_x2 = new Negation("!x2", vx2);
		FormulaElement neg_x3 = new Negation("!x3", vx3);
		FormulaElement neg_x4 = new Negation("!x4",vx4);
		FormulaElement neg_x5 = new Negation("!x5",vx5);
		FormulaElement neg_x6 = new Negation("!x6", vx6);
		FormulaElement neg_x7 = new Negation("!x7", vx7);
		
		FormulaElement neg_x8 = new Negation("!x0",vx8);
		FormulaElement neg_x9 = new Negation("!x1",vx9);
		FormulaElement neg_x10 = new Negation("!x2", vx10);
		FormulaElement neg_x11 = new Negation("!x3", vx11);
		FormulaElement neg_x12 = new Negation("!x4",vx12);
		FormulaElement neg_x13 = new Negation("!x5",vx13);
		FormulaElement neg_x14 = new Negation("!x6", vx14);
		FormulaElement neg_x15 = new Negation("!x7", vx15);
		
		FormulaElement neg_vz12 = new Negation("!z12", vz12);
				
		FormulaElement nvx0x1 = new Conjunction("&", neg_x0, neg_x1);
		FormulaElement nvx0x1x2 = new Conjunction("&", nvx0x1, neg_x2);
		FormulaElement nvx0x1x2x3 = new Conjunction("&", nvx0x1x2, neg_x3);
		FormulaElement nvx0x1x2x3x4 = new Conjunction("&", nvx0x1x2x3, neg_x4);
		FormulaElement nvx0x1x2x3x4x5 = new Conjunction("&", nvx0x1x2x3x4, neg_x5);
		FormulaElement nvx0x1x2x3x4x5x6 = new Conjunction("&", nvx0x1x2x3x4x5, neg_x6);
		FormulaElement nvx0x1x2x3x4x5x6x7 = new Conjunction("&", nvx0x1x2x3x4x5x6, neg_x7);
		FormulaElement nvx0x1x2x3x4x5x6x7x8 = new Conjunction("&", nvx0x1x2x3x4x5x6x7, neg_x8);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8, neg_x9);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9x10 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8x9, neg_x10);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9x10x11 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8x9x10, neg_x11);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9x10x11x12 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8x9x10x11, neg_x12);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9x10x11x12x13 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8x9x10x11x12, neg_x13);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8x9x10x11x12x13, neg_x14);
		FormulaElement nvx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14x15 = new Conjunction("&", nvx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14, neg_x15);
								
		FormulaElement au = new AW("", vz12, nvx0x1x2x3x4x5x6x7x8x9x10x11x12x13x14x15);
		//FormulaElement imp = new Implication("", vx0x1x2x3x4x5x6x7z6, au);
							
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
				
		//System.out.println(muller16.buildModel().getTransitions().satCount());
		//System.out.println(muller8.buildModel().getTransitions().satCount());
		if (DCTL_MC.mc_algorithm(au, muller16.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}				
		
	}
	
	
	
	public static void Muller3(){
		// Muller with 3 inputs:
		// x0 = z1 & x1 = z1 & x2 = z1 -> x0,x1,x2 = !x0,!x1,!x2
		// z0 != maj(x0,x1) -> z0 = maj
		// z1 != maj(z0,x2) -> z1 = maj(z0,x2)
		// x0=z0 && x1 != z0 -> x:=!x
		// x0!=z0 && y=z0 -> y:=!y
		// x2!=z1 && z1!=z0 -> x2:=!x2
		
		BDDModel model = new BDDModel();
		Program myProgram = new Program(4,0,10,0,0,0, model, true);
		
		// we define the variables
		VarBool x0 = new VarBool("x0", model);
		VarBool x1 = new VarBool("x1", model);
		VarBool x2 = new VarBool("x2", model);
		VarBool z0 = new VarBool("z0", model);
		VarBool z1 = new VarBool("z1", model);
		
		// define the expressions needed
		NegBoolExp not_x0 = new NegBoolExp(x0);  // !x
		NegBoolExp not_x1 = new NegBoolExp(x1);  // !y
		NegBoolExp not_x2 = new NegBoolExp(x2);  // !y
		
		AndBoolExp a1 = new AndBoolExp(x0,x1);   // x&y
		AndBoolExp a2 = new AndBoolExp(x0,z0);   // x&z
		AndBoolExp a3 = new AndBoolExp(x1,z0);   // y&z
		OrBoolExp o1 = new OrBoolExp(a1,a2);   // (x&y || x&z)
		OrBoolExp majx0x1z0 = new OrBoolExp(o1,a3);  // (x0&x1 || x0&z0 !! x1&z0)
		
		
		AndBoolExp a4 = new AndBoolExp(z0,z1);   // z0&z1
		AndBoolExp a5 = new AndBoolExp(z0,x2);   // z0&x2
		AndBoolExp a6 = new AndBoolExp(z1,x2);   // z1&x2
		OrBoolExp o2 = new OrBoolExp(a4,a5);   // (x&y || x&z)
		OrBoolExp majz0x2z1 = new OrBoolExp(o2,a6);  // (z0&z1 || z0&x2 !! z1&x2)
		
		
		
		BiimpBoolExp x0_eq_z1 = new BiimpBoolExp(x0,z1); // x0 = z1
		BiimpBoolExp x1_eq_z1 = new BiimpBoolExp(x1,z1); // x1 = z1
		BiimpBoolExp x2_eq_z1 = new BiimpBoolExp(x2,z1); // x1 = z1
		BiimpBoolExp z0_eq_majx0x1z0 = new BiimpBoolExp(z0,majx0x1z0); // z0 = maj
		NegBoolExp z0_neq_majx0x1z0 = new NegBoolExp(z0_eq_majx0x1z0); // z0 != maj
		BiimpBoolExp z1_eq_majz0x2z1 = new BiimpBoolExp(z1, majz0x2z1);
		NegBoolExp z1_neq_majz0x2z1 = new NegBoolExp(z1_eq_majz0x2z1); // z1 != maj
		
		BiimpBoolExp x0_eq_x1 = new BiimpBoolExp(x0,x1); // x0 = x1
		NegBoolExp x0_neq_x1 = new NegBoolExp(x0_eq_x1); // x0 != x1
		BiimpBoolExp x0_eq_z0 = new BiimpBoolExp(x0,z0);
		NegBoolExp x0_neq_z0 = new NegBoolExp(x0_eq_z0); // x0 != z0
		BiimpBoolExp x1_eq_z0 =  new BiimpBoolExp(x1,z0);
		NegBoolExp x1_neq_z0 = new NegBoolExp(x1_eq_z0); // y != z
		AndBoolExp x0z1_and_x1z1 = new AndBoolExp(x0_eq_z1, x1_eq_z1); // x0=z1 & x1=z1
		AndBoolExp x0z1_and_x1z1_and_x2z1 = new AndBoolExp(x2_eq_z1, x0z1_and_x1z1);
		AndBoolExp x0z0_and_nx1z0 = new AndBoolExp(x0_eq_z0, x1_neq_z0); // x0=z0 & x1!=z0
		AndBoolExp nx0z0_and_x1z0 = new AndBoolExp(x0_neq_z0, x1_eq_z0); // x0!=z0 & x1=z0
	//	BiimpBoolExp x2_eq_z1 = new BiimpBoolExp(x2,z1); // x2 = z1
		BiimpBoolExp z1_eq_z0 = new BiimpBoolExp(z1,z0); // z1 = z0
		NegBoolExp z1_neq_z0 = new NegBoolExp(z1_eq_z0); // z1!= z0
		AndBoolExp x2z1_and_nz0z1 = new AndBoolExp(x2_eq_z1, z1_neq_z0);
		
		
		// add a process
		faulty.Process process = new faulty.Process("Process", 1, 3, 3, 1, model, true);
		process.addVarBool(x0);
		process.addVarBool(x1);
		process.addVarBool(x2);
		process.addVarBool(z0);
		process.addVarBool(z1);
		process.setInitialCond(x0.getBDD().and(x1.getBDD()));
		process.setNormativeCond(x0.getBDD().biimp(x1.getBDD()));
		ConsBoolExp tt = new ConsBoolExp(true);
		
		// we define the assignations
		VarAssign assign1 = new VarAssign(x0,not_x0); 
		VarAssign assign2 = new VarAssign(x1, not_x1);
		VarAssign assign3 = new VarAssign(x2, not_x2);
		LinkedList<VarAssign> chx0x1x2 = new LinkedList<VarAssign>();
		chx0x1x2.add(assign1);
		chx0x1x2.add(assign2);
		chx0x1x2.add(assign3);
		
		ListAssign cx0x1x2 = new ListAssign(chx0x1x2);
		VarAssign assign4 = new VarAssign(z0,majx0x1z0);
		VarAssign assign5 = new VarAssign(z1, majz0x2z1);
		//LinkedList<VarAssign> czu = new LinkedList<VarAssign>();
		//cxy.add(assign3);
		//cxy.add(assign4);
		//ListAssign chzu = new ListAssign(czu);
		
		// we create the branches
		Branch branch1 = new Branch(x0z1_and_x1z1_and_x2z1, cx0x1x2, false, process);
		Branch branch2 = new Branch(z0_neq_majx0x1z0, assign4, false, process);
		Branch branch3 = new Branch(z1_neq_majz0x2z1, assign5, false, process);
		Branch branch4 = new Branch(x0z0_and_nx1z0, assign1, false, process);
		Branch branch5 = new Branch(nx0z0_and_x1z0, assign2, false, process);
		Branch branch6 = new Branch(x2z1_and_nz0z1, assign3, false, process);
		process.addBranch(branch1);
		process.addBranch(branch2);
		process.addBranch(branch3);
		process.addBranch(branch4);
		process.addBranch(branch5);
		process.addBranch(branch6);
		
	myProgram.addProcess(process);
		
		
		// we model check a property
		FormulaElement vx0 = new Variable("x0");
		FormulaElement vx1 = new Variable("x1");
		FormulaElement vx2 = new Variable("x2");
		FormulaElement vz0 = new Variable("z0");
		FormulaElement vz1 = new Variable("z1");
		FormulaElement vx0x1 = new Conjunction("&", vx0, vx1);
		FormulaElement vx0x1x2 = new Conjunction("&",vx0x1, vx2);
		FormulaElement vx0x1x2z1 = new Conjunction("&",vx0x1x2,vz1);		
		FormulaElement vx0y1z0 = new Conjunction("&", vx0x1, vz0);
				
		FormulaElement neg_x0 = new Negation("!x0",vx0);
		FormulaElement neg_x1 = new Negation("!x1",vx1);
		FormulaElement neg_x2 = new Negation("!x2", vx2);
		FormulaElement nvx0x1 = new Conjunction("&", neg_x0, neg_x1);
		FormulaElement nvx0x1x2 = new Conjunction("&", nvx0x1, neg_x2);
		
		FormulaElement au = new AU("", vz1, nvx0x1x2);
		FormulaElement imp = new Implication("", vx0x1x2z1, au);
	
		FormulaElement t = new Constant("tt", true);
		FormulaElement f = new Constant("tt", false);
		//myProgram.buildModel().getTransitions().printSet();
		if (DCTL_MC.mc_algorithm(au, myProgram.buildModel())) {
					System.out.println("Property is TRUE in the model.");
		}
		else{
					System.out.println("Property is FALSE in the model.");
		}		
		
		
	}


}
