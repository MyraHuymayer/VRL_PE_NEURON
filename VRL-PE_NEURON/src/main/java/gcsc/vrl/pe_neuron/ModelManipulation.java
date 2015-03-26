
package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.IOException;
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
            @ParamInfo(name ="End") double tstop)throws IOException{
        
        //Ensure that starting time is smaller than ending time
        if(tstart >= tstop){
            
            throw new IOException("Error: the starting value must precede the ending value! ");
        }
        //Ensure that time values are positive 
        if(tstart < 0 || tstop < 0){
            
            throw new IOException("Error: Time may not be negative! ");
        }
        
        StoreValues tuple = new StoreValues(tstart, tstop);
        
        
        System.out.println("Val 1 = "+tuple.getValue1()+" ; Val 2 = "+ tuple.getValue2());
        

        timespan.add(tuple);
        System.out.println("size of array timespan: "+timespan.size());
        
 
        if(timespan.size()>=2){
            //compare last entered values to previous values
            for(int i = 0; i < timespan.size()-1 ; i++){
                //check if timespans overlap
                
                double tstart_old = timespan.get(i).getValue1();
                double tstop_old =  timespan.get(i).getValue2();
                double tstart_new = tuple.getValue1();
                double tstop_new = tuple.getValue2();
                
                //case tstart_old smallest val and tstop_old highest value 
                if(tstart_old <= tstart_new  
                        && tstop_old >= tstop_new){
                    
                    //remove the last element since the new values are bordered by an older input
                    timespan.remove(timespan.size() -1);
                    
                }else if(tstart_old >= tstart_new  
                        && tstop_old <= tstop_new){
                    
                    //remove the element that is engulfed by the newly inserted timespan
                    timespan.remove(i);
                    
                }else if(tstart_old <= tstart_new
                        && tstop_old <= tstop_new 
                        && tstop_old >= tstart_new){
                    //remove both the last added tuple and the old tuple 
                    timespan.remove(timespan.size() -1);
                    timespan.remove(i);
                    //add a new element with new borders 
                    StoreValues newtuple = new StoreValues(tstart_old, tstop_new);
                    timespan.add(newtuple);
                    
                }else if(tstart_old >= tstart_new 
                        && tstart_old <= tstop_new 
                        && tstop_old >= tstop_new){
                    
                    //remove both the last added tuple and the old tuple 
                    timespan.remove(timespan.size() -1);
                    timespan.remove(i);
                    //add a new element with new borders 
                    StoreValues newtuple = new StoreValues(tstart_new, tstop_old);
                    timespan.add(newtuple);
                    
                }
            }
        }
       

        
        
        
    }
    
    public void dataRaster(@ParamInfo(name ="Next datapoint for evaluation")int timeStep){
        //nur gerade Zahlen sollen man auswaehlen koennen 
        
    }
    
    public void hocFilename(@ParamInfo(name ="NEURON hoc File ", options="value=\"Fig1c1.hoc\"")String hocFile){
        
    }

    public double[] getExponents() {
        return exponents;
    }

    public ArrayList<StoreValues> getTimespan() {
        return timespan;
    }
    
    /**
     * selectSort algorithm modified after the code example on http://rosettacode.org/wiki/Sorting_algorithms/Selection_sort#Java
     * for an ArrayList<ValueStorage> 
     * @param timespan ArrayList containing all the timespans added by the user 
     * @return the sorted Arraylist of timespans
     */
    @MethodInfo(noGUI=true)
    public ArrayList<StoreValues> selectSortTimespans(ArrayList<StoreValues> timespan){
        
        //Verglichen werden immer die ersten Werte --> gespeichert werden dann aber am Schluss die 
        for(int i = 0; i<timespan.size()-1; i++){
            double smallest = Double.MAX_VALUE;
            int smallestAt = i+1;
            
            for(int j = i; j<timespan.size(); j++){
                if(timespan.get(j).getValue1() < smallest){
                    smallestAt = j;
                    smallest = timespan.get(j).getValue1();
                }
            }
            StoreValues tmp = timespan.get(i);
            timespan.set(i, timespan.get(smallestAt));
            timespan.set(smallestAt, tmp);
        }
        
        return timespan;
    }
}
//public static void sort(int[] nums){
//	for(int currentPlace = 0;currentPlace<nums.length-1;currentPlace++){
//		int smallest = Integer.MAX_VALUE;
//		int smallestAt = currentPlace+1;
//		for(int check = currentPlace; check<nums.length;check++){
//			if(nums[check]<smallest){
//				smallestAt = check;
//				smallest = nums[check];
//			}
//		}
//		int temp = nums[currentPlace];
//		nums[currentPlace] = nums[smallestAt];
//		nums[smallestAt] = temp;
//	}
//}