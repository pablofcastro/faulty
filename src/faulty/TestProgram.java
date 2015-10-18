package faulty;
import net.sf.javabdd.*;
import java.util.*;
import mc.*;

/**
 * A simple class to test the faulty program language
 */

public class TestProgram{
  
	/**
	 * The main method to test the programs
	 */
	public static void main(String[] args){
		prodConsExample();
	}
	
	/**
	 * A simple process with unique branch
	 * te -> te:=!te
	 */
	private static void simpleTest(){
		BDDModel model= new BDDModel();
		Program myProgram = new Program(4,0,100,0,0,0, model);
		VarBool te = new VarBool("te", model);	
		Process process = new Process("example", 1, 3, 3, 1, model);
		process.addVarBool(te);
		ConsBoolExp tt = new ConsBoolExp(true);
		NegBoolExp neg_te = new NegBoolExp(te);
		VarAssign assign = new VarAssign(te,neg_te);
		
		Branch branch1 = new Branch(te, assign, false, process);
		
		process.addBranch(branch1);
		
		process.getBDD().printSet();
	}
	
	/**
	 * A simple test with only two vars: te and coffee, we have the following process
	 * te -> te := !te
	 * coffee -> coffee := !coffee
	 */
	private static  void test1(){
		BDDModel model = new BDDModel();
		Program myProgram = new Program(4,0,100,0,0,0, model);
		VarBool cafe = new VarBool("cafe", model);
		VarBool te = new VarBool("te", model);	
		//VarBool choc = new VarBool("choc");
		
		Process process = new Process("example", 1, 3, 3, 1, model);
		process.addVarBool(cafe);
		process.addVarBool(te);
		
		NegBoolExp neg_cafe = new NegBoolExp(cafe);
		NegBoolExp neg_te = new NegBoolExp(te);
		
		VarAssign assign = new VarAssign(cafe, neg_cafe);
		VarAssign assign2 = new VarAssign(te, neg_te);
		Branch branch1 = new Branch(cafe, assign, false, process);
		Branch branch2 = new Branch(te, assign2, false, process);
		//BDD initial = cafe.getBDD().and(te.getBDD());

		process.addBranch(branch1);
		process.addBranch(branch2);
		//process.setInitialCond(initial);
		
		myProgram.addProcess(process);
		myProgram.getBDD().printSet();
	}
	
	/**
	 * Now we create two processes
	 */
	private static  void test2(){
		
		BDDModel model = new BDDModel();
		// we create the program
		Program myProgram = new Program(4,0,100,0,0,0, model);
		
		// we create a process for coffee
		VarBool cafe = new VarBool("cafe", model);
		NegBoolExp neg_cafe = new NegBoolExp(cafe);
		Process process1 = new Process("CoffeeProcess", 1, 3, 3, 1, model);
		process1.addVarBool(cafe);
		VarAssign assign1 = new VarAssign(cafe, neg_cafe);
		Branch branch1 = new Branch(cafe, assign1, false, process1);
		process1.addBranch(branch1);
		BDD initial1 = cafe.getBDD();
		process1.setInitialCond(initial1);
		
		// we create a process for te
		VarBool te = new VarBool("te", model);
		NegBoolExp neg_te = new NegBoolExp(te);
		Process process2 = new Process("TeProcess", 1, 3, 3, 1, model);
		process2.addVarBool(te);	
		VarAssign assign2 = new VarAssign(te, neg_te);
		Branch branch2 = new Branch(te, assign2, false, process2);
		process2.addBranch(branch2);
		BDD initial2 = te.getBDD();
		process2.setInitialCond(initial2);
		
		
		myProgram.addProcess(process1);
		myProgram.addProcess(process2);
		myProgram.getBDD().printSet();
	}
	
	/**
	 * A simple test for non-determinism, a process that non-deterministically 
	 * changes the value of a variable x
	 */
	 private static void testNonDeterm(){
		// we create the program
		 
		BDDModel model = new BDDModel();
		Program myProgram = new Program(4,0,100,0,0,0, model);
		VarBool x = new VarBool("x", model);
		NegBoolExp not_x = new NegBoolExp(x);
		ConsBoolExp tt = new ConsBoolExp(true);
		VarAssign assign1 = new VarAssign(x,not_x);
		VarAssign assign2 = new VarAssign(x,x);
		Process process = new Process("Process", 1, 3, 3, 1, model);
		process.addVarBool(x);
		Branch branch1 = new Branch(x, assign1, false, process);
		Branch branch2 = new Branch(x, assign2, false, process);
		process.addBranch(branch1);
		process.addBranch(branch2);
		myProgram.addProcess(process);
		myProgram.getBDD().printSet();
	 }
	 
	 /**
	  * A simple test for integers
	  * b -> x = 1
	  */
	 private static void testInts(){
		 BDDModel model = new BDDModel();
		 Program myProgram = new Program(2,1,100,0,0,0, model);
		 VarInt x = new VarInt("x", model);
		 ConsIntExp one = new ConsIntExp(1);
		 VarBool b = new VarBool("b", model);
		 VarAssign assign = new VarAssign(x, one);
		 Process process = new Process("Process",1,3,3,1, model);
		 process.addVarInt(x);
		 process.addVarBool(b);
		 Branch branch = new Branch(b, assign, false, process);
		 
		 process.addBranch(branch);
		 
		 myProgram.addProcess(process);
		 myProgram.getBDD().printSet();
		 
	 }
	 
	 
	 /**
	  * Another test for integer, we test the sum:
	  * b-> x = x+1
	  */
	 private static void testSum(){
		 BDDModel model = new BDDModel();
		 Program myProgram = new Program(2,1,100,0,0,0, model);
		 VarInt x = new VarInt("x", model);
		 //x.getBDD().printSet();
		 ConsIntExp one = new ConsIntExp(1);
		 SumIntExp sum = new SumIntExp(x,one, model);
		 VarBool b = new VarBool("b", model);
		 VarAssign assign = new VarAssign(x, sum);
		 assign.getBDD();
		 Process process = new Process("Process",1,3,3,1, model);
		 process.addVarInt(x);
		 process.addVarBool(b);
		 Branch branch = new Branch(b, assign, false, process);
		 
		 process.addBranch(branch);
		 
		 myProgram.addProcess(process);
		 myProgram.getBDD().printSet();
		 
		 
	 }
	 
	 /**
	  * A Simple test for substraction
	  * Process()
	  * 	init x = 3;
	  * 	b -> x := x - 1;
	  */
	 private static void testSub(){
		 
		 BDDModel model = new BDDModel();
		 Program myProgram = new Program(2,1,100,0,0,0, model);
		 
		 ConsIntExp three = new ConsIntExp(3);
		 VarInt x = new VarInt("x", three.getBits(), model);
		 ConsIntExp one = new ConsIntExp(1);
		 NegIntExp sub = new NegIntExp(x,one, model);
		 VarBool b = new VarBool("b", model);
		 VarAssign assign = new VarAssign(x, sub);
		 Process process = new Process("Process",1,3,3,1, model);
		 process.addVarInt(x);
		 process.addVarBool(b);
		 Branch branch = new Branch(b, assign, false, process);
		 
		 process.addBranch(branch);
		 
		 myProgram.addProcess(process);
		 myProgram.getBDD().printSet();
		 
	 }
	 
	 
	 /**
	  * The multiplication needs to be fixed, not implemented yet
	  */
	 private static void testMult(){
		 BDDModel model = new BDDModel();
		 Program myProgram = new Program(10,1,100,0,0,0, model);
		 ConsIntExp two = new ConsIntExp(2);
		 VarInt x = new VarInt("x", two.getBits(), model);	
		 MultIntExp mult = new MultIntExp(x,two, model);
		 VarBool b = new VarBool("b", model);
		 VarAssign assign = new VarAssign(x, mult);
		 Process process = new Process("Process",1,3,3,1, model);
		 process.addVarInt(x);
		 process.addVarBool(b);
		 Branch branch = new Branch(b, assign, false, process);
		 process.addBranch(branch);	 
		 myProgram.addProcess(process);
		 myProgram.getBDD().printSet();
	 }
	 
	 
	 /**
	  * A simple example to test insertion in channels
	  * tt -> ch := tt
	  */
	 private static void insertItemChan(){
		 
		 	BDDModel model = new BDDModel();
		 	Program myProgram = new Program(2,1,100,0,0,0, model);
			
			// The channel
			BoolChannel chan = new BoolChannel(2, model);
			
			// Definition of Process Producer
			ConsBoolExp tt = new ConsBoolExp(true);
			ConsBoolExp ff = new ConsBoolExp(false);
			BoolChanAssign chanAssign = new BoolChanAssign(chan,tt);
            Process producer = new Process("Producer",1,3,3,1, model);
			Branch branch1 = new Branch(tt, chanAssign, false, producer);
			producer.addChannel(chan);
			producer.addBranch(branch1);
			
			myProgram.addProcess(producer);
			myProgram.getBDD().printSet();
	 }
	 
	 /**
	  * A simple test for channel producer-consumer example  
	  * Process Producer()
	  * 	tt -> chan.put(tt)
	  * 
	  * Process Consumer()
	  * 	tt -> x := chan.get()
	  */
	  private static void prodConsExample(){
		  
		BDDModel model = new BDDModel();
		Program myProgram = new Program(2,1,100,0,0,0, model);
		
		// The channel
		BoolChannel chan = new BoolChannel(1, model);
		
		// Definition of Process one: Producer
		ConsBoolExp tt = new ConsBoolExp(true);
		BoolChanAssign chanAssign = new BoolChanAssign(chan,tt);
		Process producer = new Process("Producer",1,3,3,1, model);
		Branch branch1 = new Branch(tt, chanAssign, false, producer);
		producer.addChannel(chan);
		producer.addBranch(branch1);
		
		// Definition of Process two: Consumer
		VarBool x = new VarBool("x", model);		
		Process consumer = new Process("Consumer",1,3,3,1, model);
		BoolChanAccess chanAccess = new BoolChanAccess(chan);
		VarAssign assign2 = new VarAssign(x, chanAccess);
		Branch branch2 =  new Branch(tt, assign2, false, consumer);
		consumer.addVarBool(x);
		consumer.addChannel(chan);
		consumer.addBranch(branch2);
		
		//producer.getBDD().printSet();
		//consumer.getBDD().and(chan.inv()).printSet();
		myProgram.addProcess(producer);
		myProgram.addProcess(consumer);
		myProgram.addBoolChannel(chan);
		myProgram.getBDD().printSet();	  
	  }
}
