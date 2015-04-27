package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
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
        private transient ArrayList<String> intermediate_res;
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
	public void runParameterEstimator(String path,
		ModelManipulation modeldata,
		ExpDataManipulation expdata,
		MethodOptions options,
		PE_Options param_properties) throws IOException, ParserConfigurationException {

        //TODO: 
		//1. create the luascript required by the parameter estimator
		FileCreator fc = new FileCreator();
		fc.createFile(modeldata, expdata, path, options, param_properties);

		//2. rufe den Parameterschaetzer mittels shellscript auf! Dazu brauchen wir vorraussichtlich VSystUtil und eventuell noch andere Klassen der VRL
	
                newton n = new newton();
		n.load_from_xml(path + "paramEst.xml");
		try {
			n.perform_fit();
                               
		} catch (Exception ex) {
			Logger.getLogger(ParameterEstimator.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(0);
		}
                
                defect = n.getDefect_tracking();
                parameter_development = n.getParameter_development();
                intermediate_res = n.getRel_param_file_names();

	}

    @OutputInfo(name = "defect")
    public ArrayList<Double> getDefect() {
        return defect;
    }
    
    @OutputInfo(name = "parameter_variances")
    public ArrayList<Parameter_Set> getParameter_Variances() {
        return parameter_development;
    }
    
    @OutputInfo(name = "intermediate_results")
    public ArrayList<String> getIntermediate_results() {
        return intermediate_res;
    }
    
    
    

        
}
