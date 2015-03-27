package gcsc.vrl.pe_neuron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author myra
 */
public class WriteToLua {
    
    //TODO: 
    //1. Make a working copy of paramEst_frame.lua
    private String path;
    private File luaFile;
    private ModelManipulation modeldata = new ModelManipulation();
    private ExpDataManipulation expdata = new ExpDataManipulation(); 

    
    
    //2. implement the changes made in the VRL Tool in the copy of paramEst.lua 
    
    /*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //Constructor
    public WriteToLua() {
        
    }
    
    /*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //unique methods
    /**
     * make a working copy of paramEst_frame.lua in the resources folder
     * @param path
     * @throws IOException 
     */
    public void copyParamEst_frame() throws IOException{
                   
        ClassLoader classLoader = getClass().getClassLoader();
        String resource = classLoader.getResource("paramEst_frame.lua").getFile();
        
        
        
        luaFile = new File(path+"paramEst.lua");
        System.out.println(luaFile.getCanonicalPath());
        
        
        FileInputStream fis = new FileInputStream(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        
        String line; 
        try{
            
            FileOutputStream is = new FileOutputStream(luaFile);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            Writer writer = new BufferedWriter(osw);
            while((line = reader.readLine()) != null){
                writer.write(line+"\n");
            }
            writer.close();
            
        }catch(IOException e){
            String filename = luaFile.getCanonicalPath();
            System.err.println("Error: Problem writing to the textfile "+filename+"!");
        }

    }    
    //note: it is more useful to make all or most changes in one method!!!!!
    public void replaceHocFile() throws IOException{
        String hocFile = modeldata.getHocFile();
        
        File tmp_out = new File(path+"tmp_output.lua");
        System.out.println(tmp_out.getCanonicalPath());
        FileInputStream fis = new FileInputStream(luaFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
        
        String line; 
        try{
            
            FileOutputStream is = new FileOutputStream(tmp_out);
            OutputStreamWriter osw = new OutputStreamWriter(is, "UTF-8");
            Writer writer = new BufferedWriter(osw);
            while((line = reader.readLine()) != null){
                if(line.contains("--[##$$ HOCFILE_String $$##]--")){
                    line = line.replace("--[##$$ HOCFILE_String $$##]--", "\""+hocFile+"\"");
                    writer.write(line+"\n");
                }else{
                    writer.write(line+"\n");
                }
            }
            writer.close();
            
        }catch(IOException e){
            String filename = tmp_out.getCanonicalPath();
            System.err.println("Error: Problem writing to the textfile "+filename+"!");
        }
        tmp_out.renameTo(luaFile);
    } 
    
    /*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //relevant getter and setter methods 
    public void setModeldata(ModelManipulation modeldata) {
        this.modeldata = modeldata;
    }

    public void setExpdata(ExpDataManipulation expdata) {
        this.expdata = expdata;
    }

    public File getLuaFile() {
        return luaFile;
    }

    public ModelManipulation getModeldata() {
        return modeldata;
    }

    public ExpDataManipulation getExpdata() {
        return expdata;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    
    
    
    
}
