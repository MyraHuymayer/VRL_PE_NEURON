package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.math.Trajectory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import param_est.Parameter_Set;

/**Kv4_csi.mod
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
    
    //Nochmal checken ob der nullte Schritt abgedeckt ist!! 
    public void plotIntermediateResults(ArrayList<String> intermediate_res){
        ArrayList<Trajectory> inter_res = new ArrayList<Trajectory>();
        int wolf =-1 , param =-1, vs=-1, zoom = -1; //default value
        // read filenames 
        for(String s : intermediate_res){
            
            //from String extract zoom, wolf, param and vs values 
            Pattern p = Pattern.compile("\\d+");
            Pattern p1 = Pattern.compile("min_\\d+");
            Pattern p2 = Pattern.compile("plus_\\d+");
            Pattern p3 = Pattern.compile("wolf\\d+");
            Pattern p4 = Pattern.compile("_z_\\d+");
            Pattern p5 = Pattern.compile("param_\\d");
            Pattern p6 = Pattern.compile("param_wolf\\d+_\\d+");
            
            Matcher m1 = p1.matcher(s);
            Matcher m2 = p2.matcher(s);
            Matcher m3 = p3.matcher(s);
            Matcher m4 = p4.matcher(s);
            Matcher m5 = p5.matcher(s);
            Matcher m6 = p6.matcher(s);
            
            if(m1.find() == true ){
                String tmp = m1.group();
                Matcher m = p.matcher(tmp);
                if(m.find() == true){
                  
                    param = Integer.parseInt(m.group());
                  
                }
                
            }else if(m2.find() == true){
                String tmp = m2.group();
                Matcher m = p.matcher(tmp);
                if(m.find() == true){
                    param = Integer.parseInt(m.group());
                   
                }
                

            }else{
                 
                param = 99;
               
            }
            
            if(m3.find() == true){
                String tmp = m3.group();
                Matcher m = p.matcher(tmp);
                
                if(m.find() == true){
                    wolf = Integer.parseInt(m.group());
                }
                
            }else{
                wolf = 99;
            }
            
            if(m4.find() == true){
                String tmp = m4.group();
                Matcher m = p.matcher(tmp);
                
                if(m.find() == true){
                    zoom = Integer.parseInt(m.group());
                }
            }else{
                zoom = 99;
            }
            
            if(m5.find() == true){
                String tmp = m5.group();
                Matcher m = p.matcher(tmp);
                
                if(m.find() == true){
                    vs = Integer.parseInt(m.group());
                }
                
            }else if(m6.find() == true){
                String tmp = m6.group();
                tmp = tmp.replaceAll("param_wolf\\d+_", "");
                vs = Integer.parseInt(tmp);
                
            }
            System.out.println("------------------------");
            System.out.println("param = "+param);
            System.out.println("wolf = "+wolf);
            System.out.println("zoom = "+zoom);
            System.out.println("vs = "+ vs);
            System.out.println("------------------------");
           
             // choose neuron output file according to the name; is there a better way to solve this ? 
        
             //import neuron data and store them in a trajectory
            

        }
       
        
        //create successive pictures of the Trajectories
    }
}
 