/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gcsc.vrl.pe_neuron;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author myra
 */
public class Main {

    /**
     * @param args the command line arguments
     */
//    
    public static void main(String[] args) throws IOException {
        ModelManipulation mm = new ModelManipulation();
        
        mm.modelUnits("ms", "nA");
        
        mm.relevantTimeSpan(800, 889);
        mm.relevantTimeSpan(30, 34);
        mm.relevantTimeSpan(69, 100);
        mm.relevantTimeSpan(1, 9);
        mm.relevantTimeSpan(43, 44);
        
        ArrayList<StoreValues> timespans = mm.getTimespan();
        
        for(int i = 0; i<timespans.size(); i++){
            System.out.print("Value 1 " + timespans.get(i).getValue1()+"; ");
            System.out.print("Value 2 " + timespans.get(i).getValue2()+ "\n");
        }
        System.out.println("-------------------------------------------------");
        System.out.println("");
        
        ArrayList<StoreValues> newList = mm.selectSortTimespans(timespans);
        
        for(int i = 0; i< newList.size(); i++){
            System.out.print("Value 1 " + newList.get(i).getValue1()+"; ");
            System.out.print("Value 2 " + newList.get(i).getValue2()+ "\n");
        }
//        ArrayList<Double[]> bla = mm.getTimespan();
//        for(int i =0; i < bla.size(); i++){
//            System.out.println("timespan ["+i+"] tstart = "+bla.get(i)[0]);
//            System.out.println("timespan ["+i+"] tstop = "+bla.get(i)[1]);
//        }
    }
//    public static void main(String[] args) {
//        System.err.println("ERROR: this is a VRL Plugin!");
//        System.err.println(" --> call the 'installVRLPlugin' task to build and install this plugin.");
//    }
}
