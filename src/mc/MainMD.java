package mc;

import java.io.*;

import faulty.*;
import faulty.auxiliar.*;
import formula.*;
import net.sf.javabdd.*;
import maskingDistance.*;

/**
 * This class represents the compiler.
 */
public class MainMD {
	
	  private static Program model;	
    private static FormulaElement property;
   
    
    public static void main(String[] args)
    {
       ProgramParser prog = new ProgramParser();
       FormulaParser formula;
       boolean printTrace = false;
       boolean toDot = false;
       boolean startSimulation = false;
       
       if (args.length < 2){
            System.out.println("Usage: ./faulty-mask <options> <nominal model path> <faulty model path>");
            System.out.println("Output: lim n->infinity of 1/1+n, where n is the number of faults masked");
            System.out.println("Options: \n -d : create dot file \n -t : print error trace \n -s : start simulation");
       }
       else{
           for (int i = 0; i < args.length - 2; i++){
              if (args[i].equals("-t")){
                printTrace = true;
              }
              if (args[i].equals("-s")){
                startSimulation = true;
              }
              if (args[i].equals("-d")){
                toDot = true;
              }
           }
            AuxiliarProgram spec = prog.parseAux(args[args.length - 2]);
            AuxiliarProgram imp = prog.parseAux(args[args.length - 1]);
            MaskingDistance md = new MaskingDistance();
            System.out.println("Masking Distance: "+md.calculateDistance(spec,imp));
            if (printTrace)
              md.printTraceToError();
            if (startSimulation)
              md.simulateGame();
            if (toDot)
              md.createDot();
        }
     }
}