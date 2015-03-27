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
        double[] e = mm.getExponents();
        System.out.println("time conversion: "+e[0]);
        System.out.println("current conversion: "+e[1]);
        
        mm.relevantTimeSpan(800, 889); //1.
        mm.relevantTimeSpan(30, 69); //2.
        mm.relevantTimeSpan(69, 100); //3. --muesste mit 2. gemerged werden
        mm.relevantTimeSpan(1, 9); //4.
        mm.relevantTimeSpan(9943, 9948); //5.
        mm.relevantTimeSpan(1110, 1500); //6.
        mm.relevantTimeSpan(1200, 1300); //7. muesste geloescht werden, da in 6. enthalten 
        mm.relevantTimeSpan(2000, 2500); //8. muesste geloescht werden, da in 9. enthalten
        mm.relevantTimeSpan(1999, 2600); //9.
        mm.relevantTimeSpan(4000, 4050); //10.
        mm.relevantTimeSpan(3990, 4020); //11. muesste mit 10. gemerged werden
        mm.relevantTimeSpan(9946, 9950); //12. --muesste mit 5. gemerged werden
        
        ArrayList<StoreValues> timespans = mm.getTimespan();
        
        System.out.println("");
        System.out.println("---------------------------------------------------");
        for(int i = 0; i<timespans.size(); i++){
            System.out.println("Array entry: "+i+": ");
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

    }
//    public static void main(String[] args) {
//        System.err.println("ERROR: this is a VRL Plugin!");
//        System.err.println(" --> call the 'installVRLPlugin' task to build and install this plugin.");
//    }
}
