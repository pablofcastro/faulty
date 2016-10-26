import java.io.*;

// A simple java file for generating instances of barrier faulty
class BarrierFaulty{

    
    // A simple barrier protocol, with two processes and two phases
    // the protocol is described by Arora, Kulkarni.
    //
    // the main complexity of this protocol comes from the high connectivity between the processes: every process
    // need to check the state of the others ones, which implemented by shared memory
    // implies that we have many global variables, and branches in any process has to check these global vars.
    // the protocol behaves in the following way:
    // ready -> executing -> success, if an error is detected the process comebacks to a safe state an starts again,
    // the implementation above deals with detectable errors, but this can be adapted to deal with other kinds of faults.
    
    private static void writeProcess(PrintWriter file){
        
        file.println("    ");
        file.println("    ");
        file.println("Process P(myState: state, myPhase : ph, otherState : state, otherPhase : ph){");
        file.println("Initial:  (myState == ready)  && (myPhase == ph0);");
	    file.println("Normative: !(myState == error);");
        file.println("    ");
        file.println("    (myState == ready) && ((otherState == ready) || (otherState == executing)) -> myState = executing;");
        file.println("    (myState == executing) && ((otherState == success) || (otherState == executing)) -> myState = success;");
        file.println("    (myState == success)  && (!(otherState == executing) && (otherState == ready)) -> myPhase = otherPhase , myState = ready;");
        file.println("    (myState == error) && (!(otherState == executing) && (otherState == ready)) -> myPhase = otherPhase, myState = ready;");
        file.println("    (myState == error) && (otherState == success) -> myPhase = otherPhase, myState = ready;");
        file.println("    true -> myState = error;");
        file.println("}");
        file.println("    ");
        file.println("    ");
        
        
    
    }

    public static void main(String[] args){
        if (args.length == 0){
            System.out.println("use: Barrier int");
        }
        else{
            int n = Integer.parseInt(args[0]);
            String space = "    ";
            try{
            PrintWriter file = new PrintWriter("Barrier"+n+".test");
          
                
            file.println("Enum state = {ready, executing, sucess, error}");
            //Define the enumerated of phases
            file.println("Enum ph = {ph0, ph1}");
                
            file.println("    ");
            
            //declare global var
            for (int i=0; i<n; i++){
                file.println("Global state"+i+ " : state;");
            }
                
            file.println("    ");
            
            
            for (int i=0; i<n; i++){
                file.println("Global php"+i+ " : ph;");
            }
                            
                         
            // we create the process
            writeProcess(file);
            
            
           // Main
            file.println("Main(){ ");
            file.println("  ");
                         
            //Define each process intances
            for (int i=0; i<n; i++){
                file.println(space + "p"+i +" : P;");
            }
                
            file.println("    ");
            file.println("    ");
                
            //Define each process intances
            for (int i=0; i<n; i++){
                if(i==(n-1)){
                    file.println(space + "run  p"+ i +"(state"+i+", php"+i+" , state0" + " ,php0 );");
                    
                }
                else{
                    file.println(space + "run  p"+ i +"(state"+i+", php"+i+" , state"+(i+1) + " ,php"+(i+1) +" );");
                }
            }
                         
            file.println("}");
            file.close();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
