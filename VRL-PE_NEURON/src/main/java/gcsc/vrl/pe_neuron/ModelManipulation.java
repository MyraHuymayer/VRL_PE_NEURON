
package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author myra
 */
@ComponentInfo(name = "Model Manipulation", category = "Optimization/NEURON", description = "")
public class ModelManipulation implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private double[] exponents;
    private transient ArrayList<StoreValues> timespan = new ArrayList<StoreValues>();

    //Wie sollen die Einheiten konvertiert werden? 
    //NOTE: das wird jetzt erst mal so implementiert, dass 
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
        
        if(currentUnit.equals("pA")){
            current_exp = -12;
        }else if(currentUnit.equals("nA")){
            current_exp = -9;
        }else if(currentUnit.equals("uA")){
            current_exp = -6;
        }else if(currentUnit.equals("A")){
            current_exp = 3;
        }else{
            current_exp = 0;
        }
        
   
       exponents[0] = time_exp;
       exponents[1] = current_exp;

       
                      
        
    }
    
    public void relevantTimeSpan(@ParamInfo(name ="Start")double tstart, 
            @ParamInfo(name ="End") double tstop){
       
        
        
        StoreValues tuple = new StoreValues(tstart, tstop);
        
        
        

        timespan.add(tuple);
        System.out.println("size of array timespan: "+timespan.size());
       

        
        
        
    }
    
    public void dataRaster(@ParamInfo(name ="Next datapoint for evaluation")int timeStep){
        
    }
    
    public void hocFilename(@ParamInfo(name ="NEURON hoc File ", options="value=\"Fig1c1.hoc\"")String hocFile){
        
    }

    public double[] getExponents() {
        return exponents;
    }

    public ArrayList<StoreValues> getTimespan() {
        return timespan;
    }
    
    
}
