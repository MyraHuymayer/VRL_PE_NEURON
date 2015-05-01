package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.system.VRL;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 * @author myra
 */
@ComponentInfo(name = "Method Options", category = "Optimization/NEURON", description = "")
public class MethodOptions implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
//    private transient HeadLine method_info = new HeadLine();
    private String basePath;
    private String path2UG;
    
    /**
     * Create the first element (element name = Settings) of the XML-file, needed for the parameter estimator
     * @param method Method name that can be choosen in drop down menu in VRL Studio (at the moment only bfgs-fs-sqp can be chosen, calling the gradient method -NOTE that the name has to be changed some time in the future)
     * @param ls_method Line search method that is chosen in a drop down menu: wolf, primitive post projection or primitive pre projection
     * @param ls_steps number of steps performed by the linesearch method
     * @param steps number of steps 
     * @param data_directory data directory that can be choosen by a load folder dialog and gives the basepath where all files will be stored and main action will take place
     * @param norm_grad norm gradient, normally set to 1e-6
     * @return String that is stored in a HeadLine object, containing all relevant method data required by the xml file
     * @throws IOException 
     */
    @OutputInfo(name="MethodInfo")
    public HeadLine setPE_Methods(@ParamInfo(name ="", style="selection", options="value=[\"bfgs-sqp-fs\"]")String method,
            @ParamInfo(name ="", style="selection", options="value=[\"wolf\", \"primitive_post_projection\", \"primitive_pre_projection\"]") String ls_method,
            @ParamInfo(name ="Number of line search steps", options="value=11")int ls_steps, 
            @ParamInfo(name ="Number of steps", options="value=50")int steps, 
            @ParamInfo(name ="Base path", style = "load-folder-dialog", options = "") File data_directory,
            @ParamInfo(name="Norm gradient", options="value=1.0E-6D")double norm_grad) throws IOException{ //der Rest wird in der xml datei so gesetzt wie gehabt, da soll Nutzer keinen Einfluss drauf haben
        
        HeadLine method_info = new HeadLine();
        
        String dd = data_directory.getCanonicalPath();
        
        

        
        basePath = dd+"/";

        //lua script necessary for the parameter estimator --> since the user never has direct access or knowledge of this file, the name is defined here!
        String lua = basePath+"paramEst.lua";
        path2UG = "";        

        path2UG = VRL.getPropertyFolderManager().getResourcesFolder() + "/ugshell";
       
////        NOTE: We only need the path to ugshell in the resources folder of the plugin: probably all code following is unnecessary!! 
        
//        if(VSysUtil.isMacOSX()){
//            
//            URL url = getClass().getClassLoader().getResource("bin/osx/ugshell");
//            InputStream input = getClass().getClassLoader().getResourceAsStream("bin/osx/ugshell");
//            
//            
//            InputStream is = getClass().getClassLoader().getResourceAsStream(VSysUtil.getSystemBinaryPath()+"/ugshell"); //NOTE: das funktioniert wahrscheinlich nur in der VRL, nicht wenn es in der Main getested wird
//            System.out.println("Resource was found: "+is);
//            if(url.getProtocol().endsWith("jar")){
//                
//                
//               
//                
//                System.out.println("####################### System Binary PATH: "+ VSysUtil.getSystemBinaryPath()+"ugshell" );
//                File tmpDir = IOUtil.createTempDir();
//                
//                if (is!=null) {
//                    // read ugshell binary
//                } else {
//                    // error, platform not supported!
//                }
//                
//                
//                PENPluginConfigurator pen = new PENPluginConfigurator();
//                String folder = pen.getIdentifier().getName();
//                
//                String pathToJar = url.getPath().substring(5, url.getPath().indexOf("VRL-PE_NEURON.jar!"));
////                System.out.println("Can this possibly work??"+folder);
//                folder = pathToJar + "" + folder + "/resources/";
//                File dir = new File(folder+"Mac");
//                dir.mkdir();
//                path2UG = folder+"Mac/";
//                System.out.println("Folder path "+folder);
//                
//                VRL.getPropertyFolderManager().getResourcesFolder();
//                
//            }else{
//                //this is only relevant when tested independently from ug and should be removed later
//                path2UG = basePath;
//            }
//            
//            File file_copy = new File(path2UG+"ugshell");
//            
//            if(!file_copy.exists()){
//                file_copy.createNewFile();
//            }
//            OutputStream outstream = new FileOutputStream(file_copy); 
//            
////            System.out.println("Worked till here ! And the Resource was found: "+input);        
//            
//            IOUtils.copy(input, outstream);
//            
//            outstream.close();
//            input.close();
//            
//            if(!file_copy.canExecute()){
//                file_copy.setExecutable(true);
//            }
//            
//            path2UG = file_copy.getCanonicalPath();            
//
//        }
        
        String tmp = "<Settings Method=\""+method+"\" LS_Method=\""+ls_method+"\" LS_Steps=\""+ls_steps+"\" Steps=\""+steps+"\" default_search_length=\"1.0\" data_directory=\"";
        tmp = tmp +basePath+"\" script=\" -ex "+lua+"\" ugshell=\""+path2UG+"\" defect_adjust_abs=\"1.0E-5\" defect_adjust_rel=\"0.15\" norm_grad=\"";
        tmp = tmp +norm_grad+"\" min_step_abs=\"1.0E-6\" min_step_rel=\"1.0E-5\" verbose=\"true\"/>";
        
        
          method_info.setHeadline(tmp); 
//          System.out.println(method_info.getHeadline());
        return method_info;

    }
    
    @OutputInfo(style="silent")
    public String getBasePath(){
        return basePath;
    }
    
    /**
     * method to get the path of the ugshell binary; this should not be known by the user 
     * @return 
     */
    @MethodInfo(noGUI = true)
    public String getPath2UG() {
        return path2UG;
    }
    
    
}
