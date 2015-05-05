package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
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
        //NOTE: Es muss noch die Option implementiert werden, dass eine xml Datei gelesen wird, die schon existiert, d.h. Nutzer sollte auch Kontrolle dar\"uber haben -- evtl auch in einer anderen Klasse!      
    
    
        private transient ArrayList<Double> defect;
        private transient ArrayList<Parameter_Set> parameter_development;
        private transient ArrayList<String> fileNames;
        //NOTE: maybe therer should be a second method where an already existing xml file is loaded
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
		PE_Options param_properties) throws IOException, ParserConfigurationException {
                
            

                String path = options.getBasePath();
              
                final File dir = new File(System.getProperty("user.dir")); 

		//1. create the luascript required by the parameter estimator
		FileCreator fc = new FileCreator();
		fc.createFile(modeldata, expdata, path, options, param_properties, dir.getCanonicalPath()+"/");
                
		//2. rufe den Parameterschaetzer mittels shellscript auf! Dazu brauchen wir vorraussichtlich VSystUtil und eventuell noch andere Klassen der VRL
                parameter_estimation("paramEst.xml", path);
                
	}
      
        /**
         * run the parameter estimator with an existing project
         * @param xml existing xml file 
         * @throws IOException
         * @throws ParserConfigurationException 
         */
        public void runParameterEstimator(@ParamInfo(name ="xml file", style = "load-dialog", options = "") File xml) throws IOException, ParserConfigurationException{
           
            if (!xml.getCanonicalPath().endsWith(".xml")) {
                throw new IOException("Error: chosen File is not an xml file!");
            }
            String name = xml.getName();
            String path = xml.getCanonicalPath().replace(name, "");
            
            parameter_estimation(name, path);
           
        }
        
        /**
         * Method to call the functionality of the parameter estimator: only requires the xml file and the path to the xml file
         * @param xml_name the name of the xml file
         * @param path the path to the directory where the xml file is located 
         * @throws IOException
         * @throws ParserConfigurationException 
         */
        @SuppressWarnings("UseSpecificCatch")
        private void parameter_estimation(String xml_name, String path)throws IOException, ParserConfigurationException{ //im Prinzip muesste die jetzt auch funktionieren 
            
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
                    
                    if(textfiles != null){
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

                defect = n.getDefect_tracking();
                parameter_development = n.getParameter_development();
                fileNames = n.getRel_param_file_names();
                
                //delete param and data file -- they are not needed anymore and just use space
                IOUtil.deleteDirectory(new File(path+"param")); 
                IOUtil.deleteDirectory(new File(path+"data"));

            
            
            
//            final File dir = new File(System.getProperty("user.dir"));       
//            
//            final FilenameFilter textFilter = new FilenameFilter(){            
//                @Override
//                public boolean accept(File dir, String name) {
//                    return name.toLowerCase().endsWith(".txt"); 
//                }
//            };
//            
//            //shutdownhook ensuring that all text files are deleted from user directory if this method is terminated too early
//            Runtime.getRuntime().addShutdownHook( new Thread() {
//                @Override public void run() {               
//                    File[] textfiles = dir.listFiles(textFilter);
//                    if(textfiles != null || textfiles.length != 0){
//                        for(File f : textfiles){
//                            f.delete();
//                        }
//                    } 
//                }
//            } );
//            
//            File tmp_dir = IOUtil.createTempDir();
//            String tmp_name = tmp_dir.getCanonicalPath();
//            
//            newton n = new newton();
//            n.load_from_xml(xml_name);
//            try {
//                n.perform_fit();
//               
//            } catch (Exception ex) {
//                Logger.getLogger(ParameterEstimator.class.getName()).log(Level.SEVERE, null, ex);
//                System.exit(0);
//            }
//            
//            File[] files = dir.listFiles(textFilter);      
//            
//            System.out.println("How many text files are in the directory? >> "+files.length);
//            for(File f : files){
//                boolean move = IOUtil.move(f, tmp_dir);
//                System.out.println("No movement took place? "+move);
//            }
//                
//                
//                defect = n.getDefect_tracking();
//                parameter_development = n.getParameter_development();
//                fileNames = n.getRel_param_file_names();
//                
//                //delete param and data file -- they are not needed anymore and just use space
//               // IOUtil.deleteDirectory(new File(path+"param")); 
//               // IOUtil.deleteDirectory(new File(path+"data"));
        }
       /*<--*/

    @OutputInfo(name = "defect")
    public ArrayList<Double> getDefect() {
        return defect;
    }
    
    @OutputInfo(name = "parameter_variances")
    public ArrayList<Parameter_Set> getParameter_Variances() {
        return parameter_development;
    }
    
//    private void extractRelevantFiles(ModelManipulation modeldata){
//        
//    }
//    @OutputInfo(name = "intermediate_results")
//    public ArrayList<String> getFileNames() {
//        return fileNames;
//    }
    
    
    

        
}
