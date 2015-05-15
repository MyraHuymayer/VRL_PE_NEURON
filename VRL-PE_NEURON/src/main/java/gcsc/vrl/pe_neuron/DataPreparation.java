package gcsc.vrl.pe_neuron;

import data_storage.DataTraces;
import import_data.read_datafiles.ReadTextFile;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author myra
 */
public class DataPreparation {

    private DataTraces[] results;
    
    /*----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

    /**
     * read columns from file and write to Data Traces (stores ArrayList<Double> of time and current)
     * @param data_file_name file name of experimental data; this is the canonanical path to the file 
     * @param param_file_name parameter file name produced by parameter estimation to extract the important values for the model file names
     * @param neuronOut naming convention for the model file names
     * @param dir_modeldata directory where the NEURON output model files are located 
     * @throws Exception 
     */
    public void extract_from_file(String data_file_name, String param_file_name, String[] neuronOut, File dir_modeldata) throws Exception{
        results = new DataTraces[3];
        
        //read data file and store in a trajectory 
        ReadTextFile readData = new ReadTextFile();
        readData.readDataFromFile(2, data_file_name);
        ArrayList<Double> data_t = readData.getTime(1);
        ArrayList<Double> data_I = readData.getCurrent(2);
        
        DataTraces data = store_data(data_t, data_I);

        results[0] = data;
        
        String model_file_b4PE = neuronOut[0]+"_99_0_99_"+neuronOut[2]+"_99.txt"; 

        //retrieve the information to find the relevant model output files
        String model_file_afterPE = retrieve_info(param_file_name, neuronOut);
        
        //read the relevant model output files from the directory where they are stored
         final FilenameFilter textFilter = new FilenameFilter(){
                
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".txt"); 
                    }
                };
         
         File[] files_in_dir = dir_modeldata.listFiles(textFilter);
         

             
             for(File f : files_in_dir){
                 
                 if(f.getName().equals(model_file_b4PE)){

                     ReadTextFile read = new ReadTextFile();
                     read.readDataFromFile(2, f.getCanonicalPath());
                     ArrayList<Double> time = read.getTime(1);
                     ArrayList<Double> current = read.getCurrent(2);


                     DataTraces modeldata = store_data(time, current);              
                     results[1] = (modeldata);

                 }
                 
                 if(f.getName().equals(model_file_afterPE)){

                     ReadTextFile read = new ReadTextFile();
                     read.readDataFromFile(2, f.getCanonicalPath());
                     ArrayList<Double> time = read.getTime(1);
                     ArrayList<Double> current = read.getCurrent(2);
                     
                     DataTraces modeldata = store_data(time, current);
                     
                     results[2] = modeldata;
                 }
                 
             }
             
//         }
       
    }

   /**
    * extract the NEURON output file name that is to be read from 
    * @param model_file_name the param file name created by the parameter estimator
    * @param neuronOut Filename convention for the NEURON output file given in ModelManipulation
    * @return filename 
    */
    private String retrieve_info(String model_file_name, String[] neuronOut){     
        
        int wolf =-1 , param =-1, vs=-1, zoom = -1; //default value
        
        // read filenames 

            
        //from String extract zoom, wolf, param and vs values 
        Pattern p = Pattern.compile("\\d+");
        Pattern p1 = Pattern.compile("min_\\d+");
        Pattern p2 = Pattern.compile("plus_\\d+");
        Pattern p3 = Pattern.compile("wolf\\d+");
        Pattern p4 = Pattern.compile("_z_\\d+");
        Pattern p5 = Pattern.compile("param_\\d");
        Pattern p6 = Pattern.compile("param_wolf\\d+_\\d+"); //NOTE, dass die ausgaben bei den anderen linesearch verfahren anders heissen, das muss auch noch implementiert werden
        
        Matcher m1 = p1.matcher(model_file_name);
        Matcher m2 = p2.matcher(model_file_name);
        Matcher m3 = p3.matcher(model_file_name);
        Matcher m4 = p4.matcher(model_file_name);
        Matcher m5 = p5.matcher(model_file_name);
        Matcher m6 = p6.matcher(model_file_name);
        
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
        
//        System.out.println("------------------------");
//        System.out.println("param = "+param);
//        System.out.println("wolf = "+wolf);
//        System.out.println("zoom = "+zoom);
//        System.out.println("vs = "+ vs);
//        System.out.println("------------------------");  
        
        String file_name = neuronOut[0]+"_"+wolf+"_"+vs+"_"+zoom+"_"+neuronOut[2]+"_"+param+".txt";
       
            
        return file_name;
    }
    
    /**
     * store data in Data Traces (x and y are both stored as ArrayList<Double>)
     * @param x ArrayList of x values 
     * @param y ArrayList of y values
     * @return Data Traces stores the two ArrayLists >> matching ArrayLists are kept together
     */
    private DataTraces store_data(ArrayList<Double> x, ArrayList<Double> y){
        
        DataTraces trajectory = new DataTraces();
        
        trajectory.setTraces(x, y);

        return trajectory;
    }
    
    public DataTraces[] getResults() {
        
        return results;
    }
    
    
}
