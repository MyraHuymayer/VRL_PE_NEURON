/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gcsc.vrl.pe_neuron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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
    public static void main(String[] args) throws IOException, FileNotFoundException, URISyntaxException {
        PE_Options peo = new PE_Options();
        peo.generateParameterSet("Parameter", 23.656, 20, 26, "a");
        peo.generateParameterSet("Parameter", 2, 1, 3, "za");
        peo.generateParameterSet("Parameter", -0.5, -1, 0, "b");
        peo.generateParameterSet("Parameter", 0.0070002648, 0.005, 0.009, "c");
        peo.generateParameterSet("Parameter", 9, 8, 10, "q");
        
        MethodOptions mo = new MethodOptions();
        File dir = new File("/Users/myra/NEURON-Projects/Parameter_Estimation/VRL-Plugin/VRL-PE_NEURON/build/resources/main/");
        File script = new File("/Users/myra/NEURON-Projects/Parameter_Estimation/VRL-Plugin/VRL-PE_NEURON/build/resources/main/paramEst.lua");
        mo.setPE_Methods("bfgs-sqp-fs", "wolf", 11, 50, dir, script, 10E-6);
        
        ModelManipulation mm = new ModelManipulation();
        
        mm.hocFilename("Fig1c1.hoc");
        mm.modelUnits("ms", "nA");
        double[] e = mm.getExponents();
        System.out.println("time conversion: "+e[0]);
        System.out.println("current conversion: "+e[1]);
        
        
        mm.addVariable("gmax", 30.5);
        mm.addVariable("pulse", -10);
        mm.addVariable("test", 0.764);
        //ArrayList<StoreValues> nVar = mm.getVariables();
        
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
        
        mm.dataRaster(10);
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
        
        
        //experimental model data 
        ExpDataManipulation edm = new ExpDataManipulation();
        edm.modelUnits("ms", "pA");
        File datafile = new File("/Users/myra/NEURON-Projects/Parameter_Estimation/One_Compartmental_Model_AType_current/Trace_1_9_3_1.txt");
        edm.dataFile(datafile);
        
        WriteToLua wtl = new WriteToLua();
        wtl.setPath("/Users/myra/NEURON-Projects/Parameter_Estimation/VRL-Plugin/VRL-PE_NEURON/src/main/resources/");
        wtl.setMethod_options(mo);
        wtl.setModeldata(mm);
        wtl.setParams(peo);
        wtl.setExpdata(edm);
        wtl.copyParamEst_frame();
        wtl.rewriteScriptFile();

    }
//    public static void main(String[] args) {
//        System.err.println("ERROR: this is a VRL Plugin!");
//        System.err.println(" --> call the 'installVRLPlugin' task to build and install this plugin.");
//    }
}
