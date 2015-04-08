package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.io.ByteArrayClassLoader;
import eu.mihosoft.vrl.system.VSysUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author myra
 */
@ComponentInfo(name = "Method Options", category = "Optimization/NEURON", description = "")
public class MethodOptions implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
//    private transient HeadLine method_info = new HeadLine();
    private String basePath;
    
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
        String path2UG = "";
        URL url;
        
        if(VSysUtil.isMacOSX()){
            url = getClass().getClassLoader().getResource("Mac/ugshell");
            
            if(url.getProtocol().endsWith("jar")){
                String jarPath = url.getPath().substring(5, url.getPath().indexOf("VRL-PE_NEURON.jar!"));
//                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
//                Enumeration<JarEntry> entries = jar.entries();
                System.out.println("#############################"+jarPath);
                
                File file = new File(jarPath+"/ugshell");
                
                ByteArrayClassLoader bacl = new ByteArrayClassLoader();
                InputStream in = bacl.getResourceAsStream("Mac/ugshell");
                Files.copy(in, file.getCanonicalFile().toPath());
                path2UG = file.getCanonicalPath();
//                while(entries.hasMoreElements()){
//                    String name = entries.nextElement().getName();
//                    if(name.contains("Mac/ugshell")){
//                        path2UG = name;
//                        System.out.println("TEST elemente der jar Datei: "+name);
//                    }
                    
//                }
                
                System.out.println("path to ugshell: "+path2UG );
                
            }else{
                path2UG = url.getPath();
                System.out.println("path to ugshell: "+path2UG );
            }
        }
        
        String tmp = "<Settings Method=\""+method+"\" LS_Method=\""+ls_method+"\" LS_Steps=\""+ls_steps+"\" Steps=\""+steps+"\" default_search_length=\"1.0\" data_directory=\"";
        tmp = tmp +basePath+"\" script=\" -ex "+lua+"\" ugshell=\""+path2UG+"\" defect_adjust_abs=\"1.0E-5\" defect_adjust_rel=\"0.15\" norm_grad=\"";
        tmp = tmp +norm_grad+"\" min_step_abs=\"1.0E-6\" min_step_rel=\"1.0E-5\" verbose=\"false\"/>";
        
        
          method_info.setHeadline(tmp); 
//          System.out.println(method_info.getHeadline());
        return method_info;

    }
    
    public String getBasePath(){
        return basePath;
    }
}
