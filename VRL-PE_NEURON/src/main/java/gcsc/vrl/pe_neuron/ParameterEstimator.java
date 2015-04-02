package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.io.ByteArrayClassLoader;
import eu.mihosoft.vrl.system.VSysUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.xml.parsers.ParserConfigurationException;
import param_est.newton;
import param_est.param_est;

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
            PE_Options param_properties)throws IOException, ParserConfigurationException{
        
        //TODO: 
        //1. create the luascript required by the parameter estimator
        FileCreator fc = new FileCreator();
        fc.createFile(modeldata, expdata, path, options, param_properties);
        
        //2. rufe den Parameterschaetzer mittels shellscript auf! Dazu brauchen wir vorraussichtlich VSystUtil und eventuell noch andere Klassen der VRL
        if(VSysUtil.isMacOSX()){
            
            ByteArrayClassLoader classloader = new ByteArrayClassLoader(); 
            InputStream in = classloader.getResourceAsStream("ugshell");
            
//            param_est estimator = new param_est();
            
            newton n = new newton();
            n.load_from_xml(path+"paramEst.xml");
            n.perform_fit();
            System.exit(0);
            
        }
        
        if(VSysUtil.isWindows()){
            System.out.println(">> Not yet supported!");
        }
        
        if(VSysUtil.isLinux()){
            System.out.println(">> Not yet supported!");
        }
        
    }
    
}
