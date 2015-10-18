

Channel C[4] of INT;
Channel B[3] of BOOL;

/*  ------------- Producer --------


*/
Process Producer uses C,B{

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp > 1 && safety == TRUE;

B.get() == TRUE -> safety=FALSE, C.put(1)=23 + 2 + 3 * 2;

safety==FALSE -> temp=(temp + 1), B.put(0)=FALSE;
}

/*  ----------- Consumer -------------- 

testing long comments.

*/
Process Consumer uses C,B{

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp > 1 || temp ==1; // this mean --> temp >= 1

temp > 1 -> safety=!safety, B.put(1)=safety, temp= temp - 1;
temp < 1 -> safety=!safety, B.put(1)=safety, temp= temp + 1;
temp == 1 -> safety=!safety, B.put(1)=safety, temp= temp + 1;
!(temp == 1) -> safety=!safety, B.put(1)=safety, temp= temp / 1;

}

Main() {

  p1: Producer;
  p2: Producer;
  c1: Consumer;

  Run p1();
  Run p2();
  Run c1();
}
