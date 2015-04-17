package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import eu.mihosoft.vrl.system.VRLPlugin;
import eu.mihosoft.vrl.system.VSysUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author myra
 */
public class PENPluginConfigurator extends VPluginConfigurator{
    
    private File binaryCopy; 
    
    public PENPluginConfigurator(){
        //specification of plugin name and version
        setIdentifier(new PluginIdentifier("ParameterEstimation_NEURON","0.1"));
        
        //description of the plugin
        setDescription("Tool to estimate up to 20 parameters  <br>"
                + "of a channel markov model implemented in NEURON.");
        
        //copyright info
        setCopyrightInfo("Parameter Estimation for NEURON model",
                "(c) Myra Huymayer", "www.gcsc.uni-frankfurt.com", "License name?", "License text?");
    }
    
     @Override
    public void register(PluginAPI api){
        
        //register plugin with canvas
        if(api instanceof VPluginAPI){
            VPluginAPI vapi = (VPluginAPI) api;
            vapi.addComponent(PE_Options.class);
            vapi.addComponent(MethodOptions.class);
            vapi.addComponent(CreateXML.class);
            vapi.addComponent(ModelManipulation.class);
            vapi.addComponent(ExpDataManipulation.class);
            vapi.addComponent(ParameterEstimator.class);
        }
    }
    
    @Override
    public void unregister(PluginAPI api){
        //nothing to unregister
    }
    
    @Override
    public void init(InitPluginAPI iApi){
        //nothing to init

    }

    @Override
    public void install(InitPluginAPI iApi) {
        binaryCopy = new File(iApi.getResourceFolder(), "ugshell"); 
        saveBinary();
        setExecutable(binaryCopy);
    }
    
    private void saveBinary(){
        //irgendwas stimmt mit dieser Methode noch nicht! Das muss ich nochmal testen! 
        InputStream is = getClass().getClassLoader().getResourceAsStream(
                VSysUtil.getSystemBinaryPath()+"ugshell");
        
        try{
            IOUtil.saveStreamToFile(is, binaryCopy);
        }catch(FileNotFoundException ex){
            Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }catch(IOException ex){
            Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setExecutable(File file){
        if(!file.canExecute()){
            file.setExecutable(true);
        }
    }

}
