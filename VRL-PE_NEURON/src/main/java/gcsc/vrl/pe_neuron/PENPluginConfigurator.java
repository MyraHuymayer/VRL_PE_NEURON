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
    private File binaryLibCopy;
    
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
            vapi.addComponent(PlottingOptions.class);
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
      //NOTE: Maybe this should be handled with IOUtils.copyFile(source, destination)
        if(VSysUtil.isMacOSX()){
            binaryLibCopy = new File(iApi.getResourceFolder(), "libug4.dylib");
        }else if(VSysUtil.isLinux()){
            // >> Not yet supported 
        }else if(VSysUtil.isWindows()){
            // >> Not yet supported 
        }else{
            System.err.println("Error: Used platform is not supported! ");
        }
        saveBinary();
        setExecutable(binaryCopy);
        saveBinaryLib();
        //copy x_86_64 directory
        File dir = new File(System.getProperty("user.dir"), "x86_64");
        copy_X86_64(dir);
    }
    
    private void saveBinary(){
        
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
    
    private void saveBinaryLib(){
//        Different platforms have different naming conventions. Windows platforms append .DLL to the library name, such as OLE32.DLL. 
//        Linux platforms use a lib prefix and a .so suffix. 
//        Mac OS X platforms have a lib prefix and a .dylib suffix.
//        Info from: http://www.mono-project.com/docs/advanced/pinvoke/
        if(VSysUtil.isMacOSX()){
            InputStream is = getClass().getClassLoader().getResourceAsStream(VSysUtil.getSystemBinaryPath()+"libug4.dylib");
            
            try{
                IOUtil.saveStreamToFile(is, binaryLibCopy);
            }catch(FileNotFoundException ex){
                Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }catch(IOException ex){
                Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void copy_X86_64(File destination) {
//        String path = VSysUtil.getSystemBinaryPath()+"x86_64/";
        try{
            IOUtil.copyDirectory(new File("/Users/myra/spaces_spaces/x86_64"), destination);
            System.out.println("x86_64 Folder not Found!");
        }catch(IOException ex){
             System.out.println("x86_64 Folder not Found!");
            Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    private void setExecutable(File file){
        if(!file.canExecute()){
            file.setExecutable(true);
        }
    }

}
