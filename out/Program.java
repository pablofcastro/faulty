
    public class Program {

public enum state{thinking,hungry,eating,error}
public BOOL fork0;
public BOOL fork1;
public class OddPhil implements Runnable { 

  Thread t; 
  boolean hasL;
  boolean hasR;
  state s;

  public OddPhil() {

  }

  public void run(){
    if (!action0())
    if (!action1())
    if (!action2())
    if (!action3())
    if (!action4())
    if (!action5())
    if (!action6())
    if (!action7())
      action8;
  }

  public void start (){
    if (t == null) {
      t = new Thread(this);
      t.start ();
    }
  }

  synchronized private boolean action0(){
    if (s == state.thinking){
      s=state.hungry;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action1(){
    if (s == state.thinking){
      s=state.thinking;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action2(){
    if (s == state.hungry && forkL && !hasL && !hasR){
      forkL=false;
      hasL=true;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action3(){
    if (s == state.hungry && !forkL && !hasL && !hasR){
      s=state.hungry;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action4(){
    if (s == state.hungry && hasL && forkR && !hasR){
      forkR=false;
      hasR=true;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action5(){
    if (s == state.hungry && hasL && !forkR && !hasR){
      forkL=true;
      hasL=false;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action6(){
    if (s == state.hungry && hasL && hasR){
      s=state.eating;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action7(){
    if (s == state.eating){
      s=state.thinking;
      forkL=true;
      forkR=true;
      hasR=false;
      hasL=false;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action8(){
    if (true){
      s=state.error;
      return true;
    }
    else{
      return false;
    }
  }

}

public class EvenPhil implements Runnable { 

  Thread t; 
  boolean hasL;
  boolean hasR;
  state s;

  public EvenPhil() {

  }

  public void run(){
    if (!action0())
    if (!action1())
    if (!action2())
    if (!action3())
    if (!action4())
    if (!action5())
    if (!action6())
    if (!action7())
      action8;
  }

  public void start (){
    if (t == null) {
      t = new Thread(this);
      t.start ();
    }
  }

  synchronized private boolean action0(){
    if (s == state.thinking){
      s=state.hungry;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action1(){
    if (s == state.thinking){
      s=state.thinking;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action2(){
    if (s == state.hungry && forkR && !hasL && !hasR){
      forkR=false;
      hasR=true;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action3(){
    if (s == state.hungry && !forkR && !hasR && !hasL){
      s=state.hungry;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action4(){
    if (s == state.hungry && hasR && forkL && !hasL){
      forkL=false;
      hasL=true;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action5(){
    if (s == state.hungry && hasR && !forkL && !hasL){
      forkR=true;
      hasR=false;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action6(){
    if (s == state.hungry && hasL && hasR){
      s=state.eating;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action7(){
    if (s == state.eating){
      s=state.thinking;
      forkL=true;
      forkR=true;
      hasR=false;
      hasL=false;
      return true;
    }
    else{
      return false;
    }
  }

  synchronized private boolean action8(){
    if (true){
      s=state.error;
      return true;
    }
    else{
      return false;
    }
  }

}

  public static void main(String[] args){
    OddPhil phil1 = new OddPhil();
    EvenPhil phil2 = new EvenPhil();
    phil1.start();
    phil2.start();
}

};
