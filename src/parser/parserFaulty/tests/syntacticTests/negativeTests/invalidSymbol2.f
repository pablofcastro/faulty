Channel C[10] of INT;

/*  ------------- Producer --------

This process is only for testing purposes of "Faulty" syntax.- 

*/
Process Producer uses C{

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp $ 1;

safety == TRUE -> safety=FALSE, temp=23;

safety==FALSE -> temp=(temp + 1), safety=!safety;
}


Main() {

  p1: Producer;
  p2: Producer;
  
  Run p1();
  Run p2();
}
