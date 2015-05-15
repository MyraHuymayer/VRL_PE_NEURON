package gcsc.vrl.pe_neuron;

import data_storage.DataTraces;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.math.Trajectory;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import param_est.Parameter_Set;

/**
 * In this Class the User can choose which results of the parameter estimator shall be plotted or displayed.
 * @author myra
 */
@ComponentInfo(name = "Options for Plotting", category = "Optimization/NEURON", description = "")
public class PlottingOptions implements Serializable{

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
                System.out.println("values =" + values[j]);

                double var = v.evaluate(values);
                
                variances[i] = var;

            }
        }
        
        // order parameter variances from highest to lowest variance
        sortHighToLow(variances, names);
        
        //print variance of each parameter to popup window -- 
        String msg = "";
        
        for(int i = 0; i< variances.length; i++){
            msg = msg + names[i]+" = "+variances[i]+"\n";
        }
        //Result printed to popup dialog when this method is invoked
        JOptionPane.showMessageDialog(null, msg);
    }
    
    /**
     * Method to explicitly sort two arrays that are logically connected: i.e.parameter names and parameter values.  
     * @param nums values, stored in a double array
     * @param names names that appertain to the values
     */
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
    
    /**
     * writes the final results of the experimental/reference data, the initial model and the updated model to a trajectory and returns this.
     * @param final_results An array of the Data Traces of the experimental data, the initial model data and the updated model data
     * @return an array of three trajectories
     */
    @OutputInfo(style = "multi-out",
    elemTypes = {Trajectory.class, Trajectory.class, Trajectory.class},
    elemNames = {"data files", "model before parameter estimation", "model after parameter estimation"},
    elemStyles = {"default", "default", "default", "default"})
    public Object[] plotResults(DataTraces[] final_results){
        
        Trajectory[] result = new Trajectory[3];
        result[0] = new Trajectory("experimental data");
        result[1] = new Trajectory("initial model");
        result[2] = new Trajectory("updated model");
        
        for(int i = 0; i< result.length; i++){
            result[i].setxAxisLabel("time [ms]");
            result[i].setyAxisLabel("current [mA]");
        }
        
        for(int i = 0; i < final_results[0].getX_trace().size(); i++){
            
            double x = final_results[0].getX_trace().get(i);
            
            double y = final_results[0].getY_trace().get(i);
           
            
            result[0].add(x, y);
           
        }   

        for(int j = 0; j < final_results[1].getX_trace().size(); j++){
            
            double x = final_results[1].getX_trace().get(j);
            double y = final_results[1].getY_trace().get(j); 
            result[1].add(x, y);
        }
        
        for(int j = 0; j < final_results[2].getX_trace().size(); j++){
            double x = final_results[2].getX_trace().get(j);
            double y = final_results[2].getY_trace().get(j);
            result[2].add(x , y);
        }
        
        Object[] objResult = new Object[result.length];
        
        System.arraycopy(result, 0, objResult, 0, result.length);

        return objResult;

    }
    
    //Nochmal checken ob der nullte Schritt abgedeckt ist!!
    //NOTE: Maybe we have to change our approach! 
    //TODO: a lot should be outsourced! 
//    public void plotIntermediateResults(/*Input*/) {
    
//    }
}
 