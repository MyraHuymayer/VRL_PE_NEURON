package gcsc.vrl.pe_neuron;

import data_storage.DataTraces;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.io.IOUtil;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import param_est.Parameter_Set;
import param_est.newton;

/**
 * This class implements the parameter estimator written by Ivo Muha, Friedrich
 * Sch<&auml>ufele and Amine Taktak. It also stores data relevant for plotting. 
 * @author myra
 */
@ComponentInfo(name = "Parameter Estimator", category = "Optimization/NEURON", description = "")
public class ParameterEstimator implements Serializable {    
    
        private transient ArrayList<Double> defect;
        private transient ArrayList<Parameter_Set> parameter_development;
//        private transient ArrayList<DataTraces[]> intermediate_res;
        private transient DataTraces[] final_results;

	private static final long serialVersionUID = 1L;

        /**
         * run the parameter estimator 
         * @param path basepath
         * @param modeldata Implementations concerning the NEURON model, such as the relevant time range, units, the number of timesteps etc
         * @param expdata Implementations concerning the experimental data, e.g. the units
         * @param options Options concerning the methods used in the parameter estimator itself
         * @param param_properties Options concerning the parameters that are to be estimated
         * @throws IOException
         * @throws ParserConfigurationException 
         */
	@SuppressWarnings("UseSpecificCatch")
	public void runParameterEstimator(ModelManipulation modeldata,
		ExpDataManipulation expdata,
		MethodOptions options,
		PE_Options param_properties) throws IOException, ParserConfigurationException, Exception {
                
            

                String path = options.getBasePath();
              
                final File dir = new File(System.getProperty("user.dir")); 

		//1. create the luascript required by the parameter estimator
		FileCreator fc = new FileCreator();
		fc.createFile(modeldata, expdata, path, options, param_properties, dir.getCanonicalPath()+"/");
                
                File dataFile = expdata.getDataFile();
                String[] neuronOut = modeldata.getNEURONout();
		//2. rufe den Parameterschaetzer mittels shellscript auf! Dazu brauchen wir vorraussichtlich VSystUtil und eventuell noch andere Klassen der VRL
                parameter_estimation("paramEst.xml", path, dataFile, neuronOut);
                
                double[] exp = modeldata.getExponents();
                
                final_results[1].multiply_xValues(Math.pow(10, exp[0]));
                final_results[1].multiply_yValues(Math.pow(10, exp[1]));
                final_results[2].multiply_xValues(Math.pow(10, exp[0]));
                final_results[2].multiply_yValues(Math.pow(10, exp[1]));

                double exp_data[] = expdata.getExponents();
                final_results[0].multiply_xValues(Math.pow(10, exp_data[0]));
                final_results[0].multiply_yValues(Math.pow(10, exp_data[1]));

	}
      
//        /**
//         * run the parameter estimator with an existing project
//         * @param xml existing xml file 
//         * @throws IOException
//         * @throws ParserConfigurationException 
//         */
//        @Deprecated
//        public void runParameterEstimator(@ParamInfo(name ="xml file", style = "load-dialog", options = "") File xml) throws IOException, ParserConfigurationException, Exception{
//           
//            if (!xml.getCanonicalPath().endsWith(".xml")) {
//                throw new IOException("Error: chosen File is not an xml file!");
//            }
//            String name = xml.getName();
//            String path = xml.getCanonicalPath().replace(name, "");
//            //TODO
//            parameter_estimation(name, path);
//           
//        }
        
        /**
         * Method to call the functionality of the parameter estimator
         * @param xml_name xml file name required by the parameter estimator
         * @param path path to the xml file 
         * @param data_file Textfile containing the data points 
         * @param neuronOutput naming convention for the modelfile names
         * @throws IOException
         * @throws ParserConfigurationException
         * @throws Exception 
         */
        @SuppressWarnings("UseSpecificCatch")
        private void parameter_estimation(String xml_name, String path, File data_file, String [] neuronOutput)throws IOException, ParserConfigurationException, Exception{ 
            
                final File dir = new File(System.getProperty("user.dir")); 

                final FilenameFilter textFilter = new FilenameFilter(){
                
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".txt"); 
                    }
                };

                Runtime.getRuntime().addShutdownHook( new Thread() {
                @Override public void run() {
                    File[] textfiles = dir.listFiles(textFilter);
                    
                    if(textfiles != null || textfiles.length != 0){
                        for(File f : textfiles){
                            f.delete();
                        }
                       
                    }
                    
                }
                } );
                           
                //call the parameter estimator
                newton n = new newton();
		n.load_from_xml(path+xml_name);
		try {
			n.perform_fit();
                               
		} catch (Exception ex) {
			Logger.getLogger(ParameterEstimator.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(0);
		}
                                
                //get the defect from the parameter estimator             
                defect = n.getDefect_tracking();
                //get the 
                parameter_development = n.getParameter_development();
                //ArrayList<String> fileNames_intermediate = n.getRel_param_file_names();
                
                //get updated model values 
                String updated_model = n.getUpdated();
                DataPreparation reading = new DataPreparation();
                reading.extract_from_file(data_file.getCanonicalPath(), updated_model, neuronOutput, dir); 
                final_results = reading.getResults();
                
                //delete param and data file -- they are not needed anymore and just use space
                IOUtil.deleteDirectory(new File(path+"param")); 
                IOUtil.deleteDirectory(new File(path+"data"));
                
        }

    @OutputInfo(name = "defect")
    public ArrayList<Double> getDefect() {
        return defect;
    }
    
    @OutputInfo(name = "parameter_variances")
    public ArrayList<Parameter_Set> getParameter_Variances() {
        return parameter_development;
    }
    
    
    public DataTraces[] getFinal_results() {

        return final_results;
    }
    

}
