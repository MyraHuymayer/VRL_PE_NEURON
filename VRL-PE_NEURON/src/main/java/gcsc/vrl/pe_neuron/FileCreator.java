package gcsc.vrl.pe_neuron;

import java.io.IOException;

/**
 * The only function of this class is to implement the WriteToLua class, which should result in a better lucidity of the ParameterEstimator class.
 * 
 * @author myra
 */
public class FileCreator {
    private WriteToLua write2lua;
    
    /*---------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //Constructor
    public FileCreator() {
        
    }
    
    public WriteToLua getWrite2lua() {
        return write2lua;
    }

//    public void setWrite2lua(WriteToLua write2lua) {
//        this.write2lua = write2lua;
//    }
    
    /**
     * Method to copy and change the script file according to the implementations to the User specified.
     * @param modeldata 
     * @param expdata
     * @param path this has to be the base path given by the user in the Method_Options class
     * @param options
     * @param param_properties
     * @throws IOException 
     */
    public void createFile(ModelManipulation modeldata,
            ExpDataManipulation expdata, 
            String path, 
            MethodOptions options,
            PE_Options param_properties, 
            String tmp_path
            ) throws IOException{
        
        write2lua = new WriteToLua();
        
        write2lua.setPath(path); 
        write2lua.setModeldata(modeldata);
        write2lua.setExpdata(expdata);
        write2lua.setParams(param_properties);
        write2lua.setMethod_options(options);
        write2lua.setPath_tmpdir(tmp_path);
        
        write2lua.copyParamEst_frame();
        
        write2lua.rewriteScriptFile();
        
    }
}
