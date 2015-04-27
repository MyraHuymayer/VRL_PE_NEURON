package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author myra
 */
@ComponentInfo(name = "Implement Parameter Options", category = "Optimization/NEURON", description = "")
public class CreateXML implements Serializable{
    
    private static final long serialVersionUID = 1L;   
    
    public void implementPEOptions(@ParamInfo(name ="Method options", options="")HeadLine estimator_methods, 
            @ParamInfo(name ="Parameter options", options="") ArrayList<String> parameter_options,
            @ParamInfo(name ="Base path", options="", style="silent") String path) throws IOException{
        
        File paramEst = new File(path+"paramEst.xml");
        String headline = estimator_methods.getHeadline();
         
        try{
            FileOutputStream is = new FileOutputStream(paramEst);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            Writer writer = new BufferedWriter(osw);
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"+"\n");
            writer.write("<java_newton>\n");
            writer.write("\t"+headline+"\n");
            for(int i = 0; i < parameter_options.size(); i++){
                writer.write("\t"+parameter_options.get(i)+"\n");
            }
            writer.write("</java_newton>");
            writer.close();
            
        }catch(IOException e){
            String filename = paramEst.getCanonicalPath();
            System.err.println("Error: Problem writing to the textfile "+filename+"!");
        }
        
    }
}
