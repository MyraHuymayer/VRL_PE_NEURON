package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.io.IOUtil;
import eu.mihosoft.vrl.system.VRLPlugin;
import eu.mihosoft.vrl.system.VSysUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Choose the project that you want to use 
 * @author myra
 */
@ComponentInfo(name = "Load NEURON Project", category = "Optimization/NEURON", description = "")
public class LoadProject implements Serializable{
    
     private static final long serialVersionUID = 1L;
    
     /**
      * When invoked a NEURON project is copied to the specified data directory and the compiled mod files are moved to the user directory
      * @param data_directory base path 
      * @param project name of the NEURON project (choice between two projects)
      */
     public void loadProject( @ParamInfo(name ="Base path", style = "load-folder-dialog", options = "") File data_directory, //NOTE that this should be the basepath
            @ParamInfo(name ="", style="selection", options="value=[\"Kv4 CSI\", \"Kv4 CSI-OSI hybrid\"]") String project) {
        
         InputStream is = null;
         String project_name = "";
         

         if(project.equals("Kv4 CSI")){
             project_name = "Kv4_CSI.zip";
             System.out.println("Project name  = Kv4_CSI.zip");
         }
         
         if(project.equals("Kv4 CSI-OSI hybrid")){
             project_name = "Kv4_CSIOSI.zip";
             System.out.println("Project name = Kv4_CSIOSI.zip");
         }
     
         is = getClass().getClassLoader().getResourceAsStream(VSysUtil.getSystemBinaryPath() +"NEURON_Project/" + project_name);
        
         System.out.println(VSysUtil.getSystemBinaryPath()+"NEURON_Project/"+project_name);
         
         File projectCopy = new File(data_directory, project_name);
         
        if(is != null ){
            System.out.println("Stream was found!");
            try{
                //copy the project file (this is a zip file) to the data directory 
                IOUtil.saveStreamToFile(is, projectCopy);
                //unpack the project file
                IOUtil.unzip(projectCopy, data_directory);
                
                //delete the zip file 
                projectCopy.delete();
                File dir = new File(System.getProperty("user.dir"), "x86_64");
                if(dir.exists()){
                    IOUtil.deleteDirectory(dir);  
                    
                }
                
                //move the x86_64 folder to the user directory
                IOUtil.copyDirectory(new File(data_directory.getCanonicalPath()+"/x86_64"), dir);
                //delete x86_64 directory from basebath - it is not useful for the user at all
                IOUtil.deleteDirectory(new File(data_directory.getCanonicalPath()+"/x86_64"));
              
                
            }catch(FileNotFoundException ex){

                Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }catch(IOException ex){
                
                Logger.getLogger(VRLPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

                  
    }
}
