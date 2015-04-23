package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.math.Trajectory;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import param_est.Parameter;
import param_est.Parameter_Set;

/**
 * In this Class the User can choose which results of the parameter estimator shall be plotted.
 * @author myra
 */
@ComponentInfo(name = "Plotting Options", category = "Optimization/NEURON", description = "")
public class PlottingOptions implements Serializable{
    //NOTE: Still requires testing!! but for that the ParameterEstimation first needs to work
    private transient StoreValues window;
    
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
    
    public void setTimeWindow(double tstart, double tstop){ //default --> uebernimmt timerange, die Nutzer in ModelManipulation angegeben hat. 
        
        window = new StoreValues(tstart, tstop);
        
    }
    

    
    //TESTING REQUIRED!
    public void calculateParameterVariance(ArrayList<Parameter_Set> param_development){
        //get Parameter names and Values
        double[] values;
        double[] variances;
        String[] names;
        
        int num_of_params = param_development.get(0).getNames().length;
        names = new String[num_of_params];
        variances = new double[num_of_params];
        //copy names from first element of param_development
//        for(int i = 0; i < num_of_params; i++){
//            names[i] = param_development.get(0).getNames()[i];
//        }
        //sollte die obige For-Schleife ersetzen --> testen!! 
        System.arraycopy(param_development.get(0).getNames(), 0, names, 0, num_of_params);
        
        // calculate variance for each Parameter --> for that we can use org.apache.commons.math3.stat.descriptive.moment
        for(int i = 0; i< num_of_params; i++){
            
            values = new double[param_development.size()];
            
            for(int j = 0; j< param_development.size(); j++){
                Variance v = new Variance();
                //so muesste das stimmen - 
                values[j] = param_development.get(j).getValues()[i];

                double var = v.evaluate(values);
                
                variances[i] = var;

            }
        }
        
        // order parameter variances from highest to lowest variance
        sortHighToLow(variances, names);
        
        //print variance of each parameter to popup window -- 
        String msg = "";
        
        for(int i = 0; i< variances.length; i++){
            msg = names[i]+" = "+variances[i]+"\n";
        }
        //Result printed to popup dialog when this method is invoked
        JOptionPane.showMessageDialog(null, msg);
    }
    
    /**
     * Method to explicitly sort two arrays that are logically connected: i.e.parameter names and parameter values.  
     * @param nums values, stored in a double array
     * @param names names that appertain to the values
     */
    //TESTING REQUIRED!
    private static void sortHighToLow(double[] nums, String[] names){  
        
        for(int i = 0; i<nums.length-1; i++){  
            double biggest = Double.MIN_VALUE;
            int biggestAt = i+1;

            for(int j = i; j<nums.length;j++){
                if(nums[j]>biggest){
                        biggestAt = j;
                        biggest = nums[j];
                    }
            }
            
            double temp = nums[i];
            String tmp = names[i];
            nums[i] = nums[biggestAt];
            names[i] = names[biggestAt];
            
            nums[biggestAt] = temp;
            names[biggestAt] = tmp;
        }
    } 
    
    public void plotFinalResults(/*input?*/){
        
    }
    
    public void plotIntermediateResults(/*input?*/){
        
    }
}
 