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
        peo.generateParameterSet("Parameter", 10.6487684024285, 8, 12, "a_new");
        peo.generateParameterSet("Parameter", 0.16476952304755, 0, 0.3, "za_new");
        peo.generateParameterSet("Parameter", 0.0469805625, 0, 0.08, "b_new");
        peo.generateParameterSet("Parameter", -1.24767461670338, -2, 0, "zb_new");
        peo.generateParameterSet("Parameter", 1.55484159715843, 1, 2, "c_new");
        peo.generateParameterSet("Parameter", 0.261053062727906, 0, 0.4, "zc_new");
        peo.generateParameterSet("Parameter", 2.18850240262452, 1, 3, "d_new");
        peo.generateParameterSet("Parameter", -0.964599220089739, -2, -0.1, "zd_new");
        peo.generateParameterSet("Parameter", 13.97932084773197, 10, 15, "k_new");
        peo.generateParameterSet("Parameter", 0.0388578913093126, 0, 0.06, "zk_new");
        peo.generateParameterSet("Parameter", 3.0, 0, 6, "l_new");
        peo.generateParameterSet("Parameter", -0.0677680004028726, -1, 0, "zl_new");
        peo.generateParameterSet("Parameter", 0.224594294615607, 0, 0.5, "f_new");
        peo.generateParameterSet("Parameter", 1.27765286086874, 0, 2, "q_new");
        peo.generateParameterSet("Parameter", 0.104162375070777, 0, 0.5, "kci_new");
        peo.generateParameterSet("Parameter", 0.002318596416033745, 0, 0.004, "kic_new");
        
        MethodOptions mo = new MethodOptions();
        File dir = new File("/Users/myra/NEURON-Projects/Parameter_Estimation/One_Compartmental_Model_AType_current/");

        mo.setPE_Methods("bfgs-sqp-fs", "wolf", 11, 50, dir, 10E-6);
        
        ModelManipulation mm = new ModelManipulation();
        
        mm.hocFilename("Fig1c1.hoc");
        mm.setNameForOutputFile("Fig1c1", "kv4_OC");
        
        mm.modelUnits("ms", "mA");
        double[] e = mm.getExponents();
//        System.out.println("time conversion: "+e[0]);
//        System.out.println("current conversion: "+e[1]);
        
        
        mm.addVariable("KMULTP", 30.4e-9);
        mm.addVariable("relpulse", 0);
        //ArrayList<StoreValues> nVar = mm.getVariables();
        
        mm.relevantTimeSpan(1199.05, 1215); 
        mm.relevantTimeSpan(1215.05, 1225); 
        mm.relevantTimeSpan(1225.05, 1300); 
        
        
        mm.dataRaster(10);
        ArrayList<StoreValues> timespans = mm.getTimespan();

        
        ArrayList<StoreValues> newList = mm.selectSortTimespans(timespans);
        
        for(int i = 0; i< newList.size(); i++){
            System.out.print("Value 1 " + newList.get(i).getValue1()+"; ");
            System.out.print("Value 2 " + newList.get(i).getValue2()+ "\n");
        }
        
        
        //experimental model data 
        ExpDataManipulation edm = new ExpDataManipulation();
        edm.modelUnits("s", "A");
        File datafile = new File("/Users/myra/NEURON-Projects/Parameter_Estimation/One_Compartmental_Model_AType_current/Trace_1_9_7_1.txt");
        edm.dataFile(datafile);
        
        ParameterEstimator pe = new ParameterEstimator();
        pe.runParameterEstimator("/Users/myra/NEURON-Projects/Parameter_Estimation/One_Compartmental_Model_AType_current/", mm, edm, mo, peo);
        
//        WriteToLua wtl = new WriteToLua();
//        wtl.setPath("/Users/myra/NEURON-Projects/Parameter_Estimation/One_Compartmental_Model_AType_current/");
//        wtl.setMethod_options(mo);
//        wtl.setModeldata(mm);
//        wtl.setParams(peo);
//        wtl.setExpdata(edm);
//        wtl.copyParamEst_frame();
//        wtl.rewriteScriptFile();

    }
//    public static void main(String[] args) {
//        System.err.println("ERROR: this is a VRL Plugin!");
//        System.err.println(" --> call the 'installVRLPlugin' task to build and install this plugin.");
//    }
}
