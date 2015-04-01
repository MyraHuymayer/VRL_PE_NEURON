package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import java.io.IOException;
import java.io.Serializable;

/**
 * This class implements the parameter estimator written by Ivo Muha, Friedrich Sch<&auml>ufele and Amine Taktak
 * @author myra
 */
@ComponentInfo(name = "Parameter Estimator", category = "Optimization/NEURON", description = "")
public class ParameterEstimator implements Serializable{
    
    public void runParameterEstimator(String path,
            ModelManipulation modeldata,
            ExpDataManipulation expdata,
            MethodOptions options,
            PE_Options param_properties)throws IOException{
        
        //TODO: 
        //1. create the luascript required by the parameter estimator
        FileCreator fc = new FileCreator();
        fc.createFile(modeldata, expdata, path, options, param_properties);
        
        //2. rufe den Parameterschaetzer mittels shellscript auf! Dazu brauchen wir vorraussichtlich VSystUtil und eventuell noch andere Klassen der VRL
        
        
    }
    
}
