import java.io.*;

// A simple java file for generating instances of barrier
class Barrier{


    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("use: Barrier int");
        }
        else{
            int n = Integer.parseInt(args[0]);
            String space = "    ";
            try{
            PrintWriter file = new PrintWriter("Barrier"+n+".smv");
            // we create the process
            file.print("MODULE P(");
            for (int i=0; i<n-1; i++){
                if (i==0)
                    file.print("otherState"+i);
                else
                    file.print(", otherState"+i);
            }
            for (int i=0; i<n-1; i++){
                file.print(", otherPhase"+i);
            }
            file.print(")");
            file.println("");
            file.println("VAR");
            file.println(space + "state : {ready, executing, success, error};");
            file.println(space + "ph:{ph1, ph2};");
            file.println("ASSIGN");
            file.println("next(state) := case");
            file.print(space + "state=ready & ");
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"=ready");
                else
                    file.print(" & otherState"+i+"=ready ");
            }
            file.print(")");
            file.print(" | ");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"=executing");
                else
                    file.print(" | otherState"+i+"=executing");
            }
            file.print(")");
            file.print("):{executing, error};");
            
            // Next branch
            file.println("");
            file.print(space + "state=executing & ");
             file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(!(otherState"+i+"=ready)");
                else
                    file.print(" & !(otherState"+i+"=ready)");
            }
            file.print(")");
            file.print(" | ");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherState"+i+"=success");
                else
                    file.print(" & otherState"+i+"=success");
            }
            file.print(")");
            file.print("):{success, error};");
            
            // Next branch
            file.println("");
            file.print(space + "state=success & ");
             file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(!(otherState"+i+"=executing)");
                else
                    file.print(" & !(otherState"+i+"=executing)");
            }
            file.print(")");
            file.print("):{ready, error};");
            
            //Next branch
            file.println("");
            file.print(space + "state=error & ");
             file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(!(otherState"+i+"=executing)");
                else
                    file.print(" & !(otherState"+i+"=executing)");
            }
            file.print(")");
            file.print("):{ready, error};");
            
            //Next branch
            file.println("");
            file.println(space+"TRUE : {state, error};");
            file.println("esac;");
            
            
            file.println("");
            // ph change
            file.println("next(ph):=case");
            file.print(space + "state=success & ");
            
            
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(!(otherState"+i+"=executing)");
                else
                    file.print(" & !(otherState"+i+"=executing)");
            }
            file.print(") & ");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherPhase"+i+"=ph1 & otherState"+i+"=ready");
                else
                    file.print(" | otherPhase"+i+"=ph1 & otherState"+i+"=ready");
            }
            file.write(")) : ph1;");
            
            // Similar for ph2
            file.println("");
            file.print(space + "state=success & ");
            
            
            file.print("(");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(!(otherState"+i+"=executing)");
                else
                    file.print(" & !(otherState"+i+"=executing)");
            }
            file.print(") & ");
            for (int i = 0; i<n-1; i++){
                if (i==0)
                    file.print("(otherPhase"+i+"=ph2 & otherState"+i+"=ready");
                else
                    file.print(" | otherPhase"+i+"=ph2 & otherState"+i+"=ready");
            }
            file.println(")) : ph2;");
            
            //Another branch
            file.print(space+"state=success & ");
            for (int  i=0; i<n-1; i++){
                if (i==0)
                    file.print("otherState"+i+"=success");
                else
                     file.print(" & otherState"+i+"=success");
            }
            file.print(" & ph = ph1 : ph2;");
            file.println("");
            
             //Another branch
            file.print(space+"state=success & ");
            for (int  i=0; i<n-1; i++){
                if (i==0)
                    file.print("otherState"+i+"=success");
                else
                     file.print(" & otherState"+i+"=success");
            }
            file.print(" & ph = ph2 : ph1;");
            
            // Another branch
            file.println("");
            for (int j=0 ; j<n-1; j++){
                file.print(space+"state=error & ");
                for (int  i=0; i<n-1; i++){
                    if (i==0)
                        file.print("!(otherState"+i+"=executing)");
                    else
                        file.print(" & !(otherState"+i+"=executing)");
                }
                file.println(" & otherState"+j+" = ready : otherPhase"+j+";");
            }
            
            // Another branch
            file.print(space+"state=error & ");
            for (int  i=0; i<n-1; i++){
                if (i==0)
                    file.print("otherState"+i+"=success");
                else
                     file.print(" & otherState"+i+"=success");
            }
            file.print(": otherPhase0;");
            
            // final branch
            file.println("");
            file.println(space + "TRUE : ph;");
            file.println("esac;");
            
            file.println("DEFINE");
            file.println(space+"n := !(state=error);");
            
            file.println("");
            file.println("MODULE main");
            file.println("VAR");
            for (int i=0; i<n; i++){
                file.print(space+"p"+i+": P(");
                for (int j=0; j<n; j++){
                    if (i!=j)
                        file.print("p"+j+".state,");
                    
                }
                for (int j=0; j<n; j++){
                    if (i!=j & j<n-1 & !(i==n-1 & j==n-2))
                        file.print("p"+j+".ph,");
                    else{
                        if (i!= j & j==n-1)
                            file.print("p"+j+".ph);");
                        if (i==n-1 & j==n-2)
                            file.print("p"+j+".ph);");
                    }
                }
                file.println("");
            }
            file.println("SPEC");
            file.println(space+" AG(!(p1.ph = ph1 & p1.ph=ph2))");
            file.close();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
