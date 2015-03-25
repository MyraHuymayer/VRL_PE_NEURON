package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
/**
 *
 * @author myra
 */
public class PENPluginConfigurator extends VPluginConfigurator{
    
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

}
