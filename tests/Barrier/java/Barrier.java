import java.io.*;

// A simple java file for generating instances of barrier for faulty
class Barrier{


    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("use: Barrier int");
        }
        else{
            int n = Integer.parseInt(args[0]);
            String space = "    ";
            try{
            PrintWriter file = new PrintWriter("Barrier"+n+".test");
            
            file.println("Enum state = {ready, executing, success, error};");
            file.println("Enum ph = {ph0, ph1};");
            
            for (int i=0; i<n; i++){
                file.println("Global state"+i+" : state;");
            }
            
            for (int i=0; i<n; i++){
                file.println("Global php"+i+" : ph; // phase for process"+i);
            }
            
            
            // we create the process
            file.print("Process P(myState : state, myPhase : ph, ");
            
            for (int i=0; i<n-1; i++){
                if (i==0)
                    file.print("otherState"+i+" : state");
                else
                    file.print(", otherState"+i+" : state");
            }
            for (int i=0; i<n-1; i++){
                file.print(", otherPhase"+i+" : ph");
            }
            file.print("){");
            file.println("");
            file.println(space+"Initial: (myState == ready) && (myPhase == ph0);");
            file.println(space+"Normative: !(myState == error);");
            file.println("");
            
            
            file.println("");
            file.println("//CPB1");
            file.print(space + "myState==ready && ");
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"==ready)");
                else
                    file.print(" && (otherState"+i+"==ready) ");
            }
            file.print(")");
            file.print(" || ");
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"==executing)");
                else
                    file.print(" || (otherState"+i+"==executing)");
            }
            file.print(")");
            file.println("-> myState=executing;");
            
            file.println("");
            file.println("//CPB2");
            // Next branch
            file.print(space + "myState==executing && ");
             file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("!(otherState"+i+"==ready)");
                else
                    file.print(" && !(otherState"+i+"==ready)");
            }
            file.print(")");
            file.print(" || ");
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"==success)");
                else
                    file.print(" || (otherState"+i+"==success)");
            }
            file.print(")");
            file.println("->myState = success;");
            
            // Next branch
            file.println("");
            file.println("//CPB3");
            //CPB3
            for (int j=0; j<n-1; j++){
                file.print(space + "myState==success && ");
                //file.print("(");
                for (int i = 0; i<n-1; i++){
                    if (i==0)
                        file.print("(!(otherState"+i+"==executing)");
                    else
                        file.print(" && !(otherState"+i+"==executing)");
                }
                file.print(") && otherState"+j+"== ready -> myState = ready, myPhase=otherPhase"+j+";");
                file.println("");
            }
            
            // for (int j=0; j<n-1; j++){
            //    file.print(space + "myState==success && ");
                //file.print("(");
            //    for (int i = 0; i<n-1; i++){
            //        if (i==0)
            //            file.print("(!(otherState"+i+"==executing)");
            //        else
            //            file.print(" && !(otherState"+i+"==executing)");
            //    }
            //    file.print(") && otherState"+j+"== ready && otherPhase"+j+"==ph1 -> myState = ready, myPhase=ph1;");
            //    file.println("");
            //}

         
            file.print(space + "myState==success && myPhase == ph0 && ");
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"==success)");
                else
                    file.print(" && (otherState"+i+"==success)");
            }
            file.println(")-> myPhase = ph1;");
            
            
            file.print(space + "myState==success && myPhase == ph1 && ");
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"==success)");
                else
                    file.print(" && (otherState"+i+"==success)");
            }
            file.println(")-> myPhase = ph0;");
            
            //CB4
            file.println("");
            file.println("//CB4");
            for (int j=0; j<n-1; j++){
                file.print(space + "myState==error && ");
                file.print("(");
                for (int i = 0; i<n-1; i++){
                    if (i==0)
                        file.print("!(otherState"+i+"==executing)");
                    else
                        file.print(" && !(otherState"+i+"==executing)");
                }
                file.print(")");
                file.println("&& (otherState"+j+"== ready)   -> myState = ready, myPhase = otherPhase"+j+";");
            }
           // for (int j=0; j<n-1; j++){
           //     file.print(space + "myState==error && ");
           //     file.print("(");
           //     for (int i = 0; i<n-1; i++){
           //         if (i==0)
           //             file.print("!(otherState"+i+"==executing)");
           //         else
           //             file.print(" && !(otherState"+i+"==executing)");
           //     }
           //     file.print(")");
           //     file.println("&& (otherState"+j+"== ready) && (otherPhase"+j+"==ph1)  -> myState = ready, myPhase = ph1;");
           // }
            
            //for (int j=0; j<n-1; j++){
                file.print(space + "myState==error && ");
                file.print("(");
                for (int i = 0; i<n-1; i++){
                    if (i==0)
                        file.print("(otherState"+i+"==success)");
                    else
                        file.print(" && (otherState"+i+"==success)");
                }
                file.print(")");
                file.println("-> myState = ready, myPhase = otherPhase0;");
            //}

            
           // for (int j=0; j<n-1; j++){
           //     file.print(space + "myState==error && ");
           //     file.print("(");
           //     for (int i = 0; i<n-1; i++){
           //         if (i==0)
           //             file.print("(otherState"+i+"==success)");
           //         else
           //             file.print(" && (otherState"+i+"==success)");
           //     }
           //     file.print(")");
           //     file.println("&& (otherPhase"+j+"==ph0)  -> myState = ready, myPhase = ph0;");
           // }
            file.println(space + "true -> myState = error;");
            
            file.println("}");
            
            
            file.println("Main(){");
            for (int i = 0; i < n; i++){
                file.println(space+"p"+i+":P;");
            }
            
            for (int i=0; i<n; i++){
                file.print(space+"run p"+i+"(");
                file.print("state"+i+", php"+i);
                for (int j=0; j<n; j++){
                    if (i!=j)
                        file.print(", state"+j);
                    
                }
                for (int j=0; j<n; j++){
                    if (i!=j)
                        file.print(", php"+j);
                    
                }
                
                file.println(");");
            }
            file.println("}");
            file.close();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
