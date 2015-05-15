package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author myra
 */
@ComponentInfo(name = "Data File Manipulation", category = "Optimization/NEURON", description = "")
public class ExpDataManipulation implements Serializable{
    private static final long serialVersionUID = 1L;
    private double[] exponents;
    private File dataFile;
    
    /*-----------------------------------------------------------------------------------------------------------------------------------------------*/
      /**
     * Entry of the time and current units that were returned by the model
     * @param timeUnit Unit of time in seconds (s) or milliseconds (ms) --> conversion to ms
     * @param currentUnit Unit of current in picoampere (pA), nanoampere (nA), microampere (uA), milliampere (mA) or ampere (A) --> conversion to mA
     */
    public void modelUnits(@ParamInfo(name ="", style="selection", options="value=[\"ms\", \"s\"]")String timeUnit, 
            @ParamInfo(name ="", style="selection", options="value=[\"pA\", \"nA\",\"uA\", \"mA\", \"A\"]")String currentUnit){
        
        exponents = new double[2];
        
        double time_exp = 0;
        
        if( timeUnit.equals("s")){
            time_exp = 3;
        }else{
            time_exp = 0;
        }
        
        double current_exp = 0;
        
       if(currentUnit.equals("nA")){
            current_exp = 3;
        }else if(currentUnit.equals("uA")){
            current_exp = 6;
        }else if(currentUnit.equals("mA")){
            current_exp = 9;
        }else if(currentUnit.equals("A")){
            current_exp = 12;
        }else{
            current_exp = 0;
        }
        
   
       exponents[0] = time_exp;
       exponents[1] = current_exp;

       
                      
        
    }
    
    /**
     * Set the experimental data file from which we want to read!
     * @param dataFile data file 
     * @throws IOException 
     */
    public void dataFile(@ParamInfo(name = "Data file",
            style = "load-dialog", options = "") File dataFile) throws IOException{
         

        if(dataFile.getCanonicalPath().endsWith(".txt") != true){
            throw new IOException("Error: Data file has to be a textfile! ");
        }else{
            this.dataFile = dataFile;
        }
    }

    @MethodInfo(noGUI=true)
    public double[] getExponents() {
        return exponents;
    }

    @MethodInfo(noGUI=true)
    public File getDataFile() {
        return dataFile;
    }
    
    
    
}
