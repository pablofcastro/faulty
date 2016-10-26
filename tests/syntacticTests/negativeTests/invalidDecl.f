

Channel C[4] of INT;
Channel B[3] of BOOL;

/*  ------------- Producer --------


*/
Process Producer uses C,B{

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp > 1;

B.get() == TRUE -> safety=FALSE, C.put(1)=23;

safety==FALSE -> temp=(temp + 1), B.put(0)=FALSE;
}

Main() {

Process Consumer uses C,B{

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp > 1;

temp > 1 -> safety=!safety, B.put(1)=safety, temp= temp - 1;

}

  Run p1();
  Run p2();
  Run c1();
}
