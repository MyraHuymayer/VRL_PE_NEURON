
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
    private int nextDataPoint;
    private String hocFile;
    private transient ArrayList<StoreValues> variables = new ArrayList<StoreValues>();
    private String out_part1;
    private String out_part2;
    /*------------------------------------------------------------------------------------------------------------------------------------------------------------*/
   
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
    
    /**
     * User entry of the time span, in which residua will be calculated; more than one entry is possible. 
     * @param tstart starting time
     * @param tstop ending time
     * @throws IOException 
     */
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
        
        
//        System.out.println("Val 1 = "+tuple.getValue1()+" ; Val 2 = "+ tuple.getValue2());

        if(timespan == null){
            timespan = new ArrayList<StoreValues>();
        }

        timespan.add(tuple);
//        System.out.println("size of array timespan: "+timespan.size());
        
 
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
    
    /**
      * Choose the next data point that is to be evaluated (Reduction of Residua/data points). NOTE that only even values can be entered. 
      * @param nextDataPoint next data point to be chooesen (e.g. timeStep = 40 --> choose the 40. data point from the current point)
      * @throws IOException 
      */
    public void dataRaster(@ParamInfo(name ="Next datapoint for evaluation")int nextDataPoint) throws IOException{
        //check if next data point is even!
        //TODO: nochmal darueber nachdenken ob das so wirklich Sinn macht 
        if(nextDataPoint%2 != 0){
            throw new IOException("Error: Only even numbers to be inserted! ");
        }else{
            this.nextDataPoint = nextDataPoint;
        } 
    }
    
    /**
     * Enter the name of the hoc file that is to be called in the lua file. 
     * @param hocFile name of the hoc-file
     * @throws IOException 
     */
    public void hocFilename(@ParamInfo(name ="NEURON hoc File ", options="value=\"Fig1c1.hoc\"")String hocFile) throws IOException{
        
        if(hocFile.contains(".hoc")){
            
            this.hocFile = hocFile;            
        }else{
            throw new IOException("Error: Not a hoc File! ");
        }    
    }

    @MethodInfo(noGUI=true)
    public ArrayList<StoreValues> getTimespan() {
        return timespan;
    }

    @MethodInfo(noGUI=true)
    public int getNextDataPoint() {
        return nextDataPoint;
    }

    @MethodInfo(noGUI=true)
    public String getHocFile() {
        return hocFile;
    }
    
    /**
     * set a new variable that is relevant or needs to be changed in hoc file
     * @param varName name of variable
     * @param varVal value of variable
     */
    public void addVariable(@ParamInfo(name ="Variable name ")String varName, 
            @ParamInfo(name ="Variable value ")double varVal
            ){
        
        StoreValues var = new StoreValues(varName, varVal);
        if(variables == null){
            variables = new ArrayList<StoreValues>();
        }
        variables.add(var);
        
    }

    @MethodInfo(noGUI=true)
    public ArrayList<StoreValues> getVariables() {
        return variables;
    }
    
    //Nicht sicher ob ich das so beibehalten moechte! 
    public void setNameForOutputFile(String out_part1, String out_part2 ){
        
        this.out_part1 = out_part1;
        this.out_part2 = out_part2;
    }
    
    public String getOut_part1(){
        return out_part1;
    }
    
     public String getOut_part2(){
        return out_part2;
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
    
    @MethodInfo(noGUI=true)
    public double[] getExponents() {
        return exponents;
    }
}