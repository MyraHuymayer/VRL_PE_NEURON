package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
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
        
        String tmp = "<Settings Method=\""+method+"\" LS_Method=\""+ls_method+"\" LS_Steps=\""+ls_steps+"\" Steps=\""+steps+"\" default_search_length=\"1.0\" data_directory=\"";
        tmp = tmp +basePath+"\" script=\" -ex "+lua+"\" ugshell=\"ugshell\" defect_adjust_abs=\"1.0E-5\" defect_adjust_rel=\"0.15\" norm_grad=\"";
        tmp = tmp +norm_grad+"\" min_step_abs=\"1.0E-6\" min_step_rel=\"1.0E-5\" verbose=\"false\"/>";
        
        
          method_info.setHeadline(tmp); 
//          System.out.println(method_info.getHeadline());
        return method_info;

    }
    
    public String getBasePath(){
        return basePath;
    }
}
