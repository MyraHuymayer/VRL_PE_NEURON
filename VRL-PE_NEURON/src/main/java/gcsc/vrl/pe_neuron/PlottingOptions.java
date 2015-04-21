package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.math.Trajectory;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * In this Class the User can choose which results of the parameter estimator shall be plotted.
 * @author myra
 */
@ComponentInfo(name = "Plotting Options", category = "Optimization/NEURON", description = "")
public class PlottingOptions implements Serializable{
    //NOTE: Still requires testing!!
    
    
    /**
     * Choose this method to plot the defect development
     * @param defect defect development
     * @return defect development as Trajectory
     */
    @OutputInfo(name = "Defect development")
    public Trajectory plotDefectDevelopment(ArrayList<Double> defect){
        
        Trajectory result = new Trajectory("defect");
        
        for(int i = 0; i < defect.size(); i++){
            //i should be the number of steps made by the parameter estimator
            result.add(i, defect.get(i));
        }
        
        return result;
    }
}
