import java.io.*;

// A simple java file for generating instances of Memory for faulty
class Memory{


    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("use: Memory int");
        }
        else{
            int n = Integer.parseInt(args[0]);
            String space = "    ";
            try{
            PrintWriter file = new PrintWriter("Memory"+n+".test");
            String indent = "    ";
            // we create the process
            file.println("Process Memory{");
            file.println(indent+"w:BOOL;");
            file.println(indent+"r:BOOL;");
            for (int i = 0; i < n; i++){
                file.println(indent+"c"+i+":BOOL;");
            }
            // the initial condition
            file.print(indent+"Initial: w && ");
            for (int i = 0; i < n; i++){
                if (i==0)
                    file.print("c"+i);
                else
                    file.print(" && "+"c"+i);
            }
            file.println(" && r;");
            
            // the normative condition
            file.print(indent + "Normative: ");
            for (int i = 0; i < n-1; i++){
                if (i==0)
                    file.print("c"+i+" == "+"c"+(i+1) );
                else
                    file.print("&& c"+i+" == "+"c"+(i+1) );
              // for (int j = i; j < n; j++){
              //      if (i != j)
              //          if (i==0 & j == 1)
              //              file.print("c"+i+" == "+"c"+j );
              //          else
              //              file.print(" && c"+i+" == "+"c"+j );
              //  }
            }
            file.println(";");
            
            // the branches
            file.print(indent+"true -> w=!w");
            for (int i = 0; i < n; i++){
                file.print(",c"+i+"=!c"+i);
            }
            file.print(",r=");
            int[] input = new int[n];
            for (int i=0; i<n; i++){
                input[i] = i;
            }
            String majority = Comb.comb((n/2)+1, input, -1);
            file.print(majority+";");
            file.println("");
            
            for (int i=1; i<n; i++){
                file.print(indent+"true -> c"+i+"=!c"+i+", ");
                file.print("r = ");
                String maj = Comb.comb((n/2)+1, input, i);
                file.println(maj+";");
            
            }
            file.println("}");
            
            file.println("Main(){");
            file.println(indent+"m1:Memory;");
            file.println(indent+"run m1();");
            file.println("}");
            
            
            //int[] input = {1,2,3,4,5,6,7};
            //System.out.println(Comb.comb(4, input, -1));
            
            //file.print("r = ");
            //for ()
            //
            //
            //
            //}
            
            //file.println("SPEC");
            //file.println(space+" AG(!(p1.ph = ph1 & p1.ph=ph2))");
            file.close();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
