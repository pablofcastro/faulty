package masking;

import java.util.*;
import net.sf.javabdd.*;
import formula.*;
import faulty.*;
import mc.*;

public class MaskingRelation {

    private static BDDModel model;
    private static int[] labels = {0,1}; // variables to be masked


    /*
        Function that does existential quantification on bdd according to selector
        If selector < 0 then remove X, if == 0 then remove Y, else remove Z 
    */
    private static BDD smooth(BDD bdd, int selector){
        int varNum = model.getFactory().varNum();
        if (selector < 0){
            return addExists(0, varNum/3, bdd, model.getFactory());
        }
        if (selector == 0){
            return addExists(varNum/3, 2*varNum/3, bdd, model.getFactory());
        }
        if (selector > 0){
            return addExists(2*varNum/3, varNum, bdd, model.getFactory());
        }
        return Program.myFactory.zero();
    }

    /*
        Bouali and de Simone's symbolic method of calculating reachable states 
    */
    private static BDD reachableStates(){
        int varNum = model.getFactory().varNum();
        BDD trans = smooth(model.getTransitions(), 1);
        BDD states = model.getIni();
        BDD newStates = states;
        while (!newStates.isZero()){
            BDD temp = states;
            states = states.or(smooth(trans.and(states),-1).replaceWith(pairing(-1,0)));
            newStates = states.and(temp.not());
        }
        return states;
    }

    /*
        Existential quantification of variables
    */
    private static BDD addExists(int from, int until, BDD b, BDDFactory f){
        
        BDD var;

        for(int k=from; k< until; k++){             
            var = f.ithVar(k);
            b = b.exist(var);
        }       
        return b;       
    }

    /*
        Function that returns a pair of variables where the old one is gonna be replaced by the new one
    */
    private static BDDPairing pairing(int newv, int oldv ){      

        //obtain the identifier of each variable of the BDD.
        int varNum = model.getFactory().varNum() / 3;
        BDDPairing pairs = model.getFactory().makePair(); 

        if (oldv < 0){ // old = s
            if (newv == 0){ // new = s'
                for (int i=0; i<varNum; i++){
                    pairs.set(i, i + varNum);
                    // System.out.println("pair ( " + i + ", " + ((varNum)+i) + " )" );
                } 
            }
            if (newv > 0){ // new = s*
                for (int i=0; i<varNum; i++){
                    pairs.set(i, i + 2*varNum);
                } 
            }
        }
        if (oldv == 0){ // old = s'
            if (newv < 0){ // new = s
                for (int i=0; i<varNum; i++){
                    pairs.set(i + varNum, i);
                } 
            }
            if (newv > 0){ // new = s*
                for (int i=0; i<varNum; i++){
                    pairs.set(i + varNum, i + 2*varNum);
                } 
            }
        }
        if (oldv > 0){ // old = s*
            if (newv < 0){ // new = s
                for (int i=0; i<varNum; i++){
                    pairs.set(i + 2*varNum, i);
                } 
            }
            if (newv == 0){ // new = s'
                for (int i=0; i<varNum; i++){
                    pairs.set(i + 2*varNum, i + varNum);
                } 
            }
        }      

        return pairs;
    }

    /*
        Calculates Bad pairs of Masking Relation given the preimages of the related X and Y
    */
    private static BDD bad(BDD preX, BDD preY){
        BDD result,t1,t2,b1,b2;
        t1 = preX.not();
        t2 = preY.not();
        b1 = smooth(preX.and(t2), -1); 
        b2 = smooth(t1.and(preY), -1);
        result = (b1.or(b2)).replaceWith(pairing(-1,1));
        return result;
    }

    /*private static BDD bad2(BDD posX, BDD posY){
        BDD result,t1,t2,b1,b2;
        t1 = posX.not();
        t2 = posY.not();
        b1 = posX.and(t2); 
        b2 = t1.and(posY);
        result = smooth(b1.or(b2),1);
        return result;
    }*/

    /*
        Custom bdd printer
    */
    private static String prettyBDD(String s){
        String result = "";
        if (s.length() > 0){
            for (int i = 1; i < s.length(); i++){
                if (s.charAt(i) == ':'){
                    result = result + model.getVarName(Integer.parseInt(String.valueOf(s.charAt(i-1))));
                }
                else{
                    result = result + s.charAt(i-1);
                    if (s.charAt(i) == '<')
                        result = result + "\n";
                }
            }
            result = result + s.charAt(s.length()-1);
        }
        return result;
    }

    /*
        Initial partition of Masking relation: (S x S') && (labels(S) == labels(S'))
    */
    private static BDD init(){
        int varNum = model.getFactory().varNum();
        BDD states = reachableStates();
        BDD states2 = states.id().replaceWith(pairing(0,-1));
        //System.out.println("states_X: " + states.toString() + "\n");
        //System.out.println("states_Y: " + states2.toString()+ "\n");
        BDD eq = Program.myFactory.one();
        varNum = varNum / 3;
        for (int i = 0; i < labels.length; i++){
            eq = eq.and(Program.myFactory.ithVar(labels[i]).biimp(Program.myFactory.ithVar(labels[i] + varNum)));
        }
        return (states.and(states2)).and(eq);
            
    }

    /*
        Calculates Masking relation based on Bouali and de Simone's symbolic bisimulation method
    */
    private static BDD sim(){
        BDD initPartition = init();
        BDD rel = initPartition;
        BDD oldRel = Program.myFactory.zero();
        BDD states = reachableStates();
        BDD trans,bad,preX,preY,t,badB2,badB3,badB4,relB2,relB3,relB4;
        trans = model.getTransitions().and(states);     
        //System.out.println("TRANS: \n" + trans.toString()+ "\n");

        while (!rel.biimp(oldRel).isOne()) { // fix point loop
            oldRel = rel;
            bad = Program.myFactory.zero();
            //System.out.println("REL: \n" + oldRel.toString()+ "\n");
            t = smooth(trans.id(),1).replaceWith(pairing(1,-1));                                                    // T(z,y) = [z<-x]trans(x,y)                 
            //System.out.println("RELATION B2 \n");
            relB2 = rel.id().and(model.getNormative());                                                             // R2 : N x S
            //System.out.println("RELB2: \n" + relB2.toString()+ "\n");
            preX = smooth(relB2.and(t), 0);                                                                         // preX(x,z) = exists y: R2(x,y) && T(z,y)
            //System.out.println("PRE_X: \n" + preX.toString()+ "\n");
            preY = preX.id().replaceWith(pairing(0,1));                                                             // preY(x,y) = [y<-z]preX(x,z)
            //System.out.println("PRE_Y: \n" + preY.toString()+ "\n");
            badB2 = bad(preX.id(),preY.id());                                                                       // badB2(x,y) = [x<-z] exists x: (preX XOR preY)
            //System.out.println("RELATION B3 \n");
            relB3 = rel.id().and(model.getNormative()).and(model.getNormative().id().replaceWith(pairing(0,-1)));   // R3 : N x N
            //System.out.println("RELB3: \n" + relB3.toString()+ "\n");
            preX = smooth(relB3.and(t), 0);                                                                         // preX(x,z) = exists y: R3(x,y) && T(z,y)
            //System.out.println("PRE_X: \n" + preX.toString()+ "\n");
            preY = preX.id().replaceWith(pairing(0,1));                                                             // preY(x,y) = [y<-z]preX(x,z)
            //System.out.println("PRE_Y: \n" + preY.toString()+ "\n");
            badB3 = bad(preX.id(),preY.id());                                                                       // badB3(x,y) = [x<-z] exists x: (preX XOR preY)
            badB3 = badB3.replaceWith(pairing(1,-1)).replaceWith(pairing(-1,0)).replaceWith(pairing(0,1));          // invert x and y on preX
            //System.out.println("RELATION B4 \n");
            relB4 = rel.id().and(model.getNormative()).and(model.getNormative().id().not().replaceWith(pairing(0,-1))); // R4.1 : N x F
            //System.out.println("RELB4: \n" + relB4.toString()+ "\n");
            preX = smooth(relB4.and(t), 0);                                                                         // preX(x,z) = exists y: R4.1(x,y) && T(z,y)
            //System.out.println("PRE_X: \n" + preX.toString()+ "\n");
            preY = preX.id().replaceWith(pairing(0,1));                                                             // preY(x,y) = [y<-z]preX(x,z)
            //System.out.println("PRE_Y: \n" + preY.toString()+ "\n");
            badB4 = bad(preX.id(),preY.id());                                                                       // badB4(x,y) = [x<-z] exists x: (preX XOR preY)
            badB4 = badB4.and((bad(preX,rel.id().and(model.getNormative().id().not().replaceWith(pairing(0,-1)))))); // && the R4.2 constraint 
            badB4 = badB4.replaceWith(pairing(1,-1)).replaceWith(pairing(-1,0)).replaceWith(pairing(0,1));           //invert x and y
            bad = badB2.or(badB3).or(badB4);
            //System.out.println("BAD: \n" + (rel.and(bad)).toString()+ "\n");
                                                                                                                    // R(x,y) = oldR(x,y) && !bad(x,y)
            rel = oldRel.and(bad.not());
            //System.out.println("REL: \n" + rel.toString()+ "\n");
        }

        return rel;
    }

    //Simulation with post, not working
    /*private static BDD sim2(){
        BDD initPartition = init();
        BDD rel = initPartition;
        BDD oldRel = Program.myFactory.zero();
        BDD states = reachableStates();
        BDD trans, transN, transF, bad,posX,posY,t,badB2,badB3,badB4;
        trans = model.getTransitions().and(states);
        transN = trans.and(model.getNormative().id().replaceWith(pairing(0,-1)));
        transF = trans.and(model.getNormative().id().not().replaceWith(pairing(0,-1)));     

        while (!rel.biimp(oldRel).isOne()) { // fix point loop
            //when debugging, preX and preY should be read backwards, i.e <s',s>
            oldRel = rel;
            System.out.println("OLDREL: \n" + rel.toString()+ "\n");

            bad = Program.myFactory.zero();

            //Calculate the preimage of the S states of the relation
            System.out.println("ORIG_TRANS: \n" + trans.toString()+ "\n");         
            
            System.out.println("RELATION B2 \n");

            t = smooth(transN.id(),1).replaceWith(pairing(1,0));
            posX = smooth(rel.and(t.id()), 0); // posX(x,z)
            //System.out.println("transN: \n" + t.toString()+ "\n");
            System.out.println("POS_X: \n" + posX.toString()+ "\n");

            t = smooth(trans.id(),1).replaceWith(pairing(1,0));
            posY = smooth(rel.and(t.id().replaceWith(pairing(0,-1))), -1); // posY(y,z)
            System.out.println("POS_Y: \n" + posY.toString()+ "\n");

            //Calculate bad pairs of the relation
            badB2 = bad2(posX.id(),posY.id());

            System.out.println("RELATION B3 \n");
            relB3 = rel.id().and(model.getNormative()).and(model.getNormative().id().replaceWith(pairing(0,-1))); //R : N x N
            System.out.println("RELB3: \n" + relB3.toString()+ "\n");

            preX = smooth(relB3.and(t), 0); // E(x,z)
            System.out.println("PRE_X: \n" + preX.toString()+ "\n");

            preY = preX.id().replaceWith(pairing(0,1)); // E(x,y)
            System.out.println("PRE_Y: \n" + preY.toString()+ "\n");

            badB3 = bad(preX.id(),preY.id());//.and(model.getNormative().id().replaceWith(pairing(0,-1))).and(model.getNormative());
            badB3 = badB3.replaceWith(pairing(1,-1)).replaceWith(pairing(-1,0)).replaceWith(pairing(0,1)); //invert x and y

            System.out.println("RELATION B4 \n");
            relB4 = rel.id().and(model.getNormative()).and(model.getNormative().id().not().replaceWith(pairing(0,-1))); //R : N x F
            System.out.println("RELB4: \n" + relB4.toString()+ "\n");

            preX = smooth(relB4.and(t), 0); // E(x,z)
            System.out.println("PRE_X: \n" + preX.toString()+ "\n");

            preY = preX.id().replaceWith(pairing(0,1)); // E(x,y)
            System.out.println("PRE_Y: \n" + preY.toString()+ "\n");

            badB4 = bad(preX.id(),preY.id()).replaceWith(pairing(1,-1));
            badB4 = badB4.replaceWith(pairing(-1,0)).replaceWith(pairing(0,1)); //invert x and y
            badB4 = badB4.and((bad(preX.id(),relB4)));
            //bad = badB2.or(badB3).or(badB4);
            bad = badB2;
            //bad = badB2.or(badB3);
            System.out.println("BAD: \n" + (rel.and(bad)).toString()+ "\n");
            //Refine relation
            rel = rel.and(bad.not());
            System.out.println("REL: \n" + rel.toString()+ "\n");
        }

        return rel;
    }*/

    /*
    * Main algorithm
    */
    public static BDD maskingRelation(Program p){
        model = p.buildBisimModel(); //buildBisimModel for the naming of extra variables
        System.out.println("vars: "+ model.getFactory().varNum());
        BDD result = sim();
        System.out.println("sim rel size: " + result.nodeCount());
        System.out.println("simulation relation: \n" + prettyBDD(result.toString()));
        return result;
    }

}