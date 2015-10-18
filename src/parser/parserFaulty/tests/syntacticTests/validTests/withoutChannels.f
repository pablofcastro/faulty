

/*  ------------- Producer --------


*/
Process Producer {

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp > 1;

safety == TRUE -> safety=FALSE, temp=23;

safety==FALSE -> temp=(temp + 1), safety=!safety;
}

/*  ----------- Consumer -------------- 

testing long comments.
*/

Process Consumer {

temp: INT;
safety:BOOL;

INIT: safety==TRUE;

NORMATIVE:temp > 1;

temp > 1 -> safety=!safety, temp= temp - 1;

}

Main() {

  p1: Producer;
  p2: Producer;
  c1: Consumer;

  Run p1();
  Run p2();
  Run c1();
}
