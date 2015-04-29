package gcsc.vrl.pe_neuron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author myra
 */
public class WriteToLua {
    
    private String path;
    private File luaFile;
    private ModelManipulation modeldata = new ModelManipulation();
    private ExpDataManipulation expdata = new ExpDataManipulation(); 
    private MethodOptions method_options = new MethodOptions();
    private PE_Options params = new PE_Options();
    private String path_tmpdir;
  
    
    /*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //Constructor
    public WriteToLua() {
        
    }
    
    /*----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    //unique methods
    /**
     * make a working copy of paramEst_frame.lua in the resources folder; note that the lua file is set in this method
     * @param path
     * @throws IOException 
     */
    public void copyParamEst_frame() throws IOException{
                
        ClassLoader classLoader = getClass().getClassLoader();

//        String resource = classLoader.getResource("paramEst_frame.lua").getFile();
        InputStream resource = classLoader.getResourceAsStream("paramEst_frame.lua");

//        System.out.println("HALLLoooooo : "+resource);
        luaFile = new File(path+"paramEst.lua");
//        System.out.println(luaFile.getCanonicalPath());
        
        
        //FileInputStream fis = new FileInputStream(resource); //hier macht das Plugin Probleme-- eventuell hat das mit der Repraesentation von unserem pathname zu tun
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
//        System.out.println("Schafft er das? Oder findet er die Resource immer noch nicht?");
        
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
   
    /**
     * Method that integrates all the relevant data and functions that were given by the user in the copied lua script. 
     * @throws IOException 
     */
    public void rewriteScriptFile() throws IOException{
        //set all the relevant data
        String hocFile = modeldata.getHocFile();
        ArrayList<StoreValues> nVar = modeldata.getVariables();
        ArrayList<StoreValues> rel_time = modeldata.getTimespan();
        
        String basepath = method_options.getBasePath();
        //String path2ug = method_options.getPath2UG();
        //path2ug = path2ug.substring(1, path2ug.indexOf("ugshell"));
        //System.out.println("path to ug: " +path2ug);
//        System.out.println("BASEPATH = "+basepath);
        ArrayList<StoreValues> parameters = params.getParams();

        File tmp_out = new File(path+"tmp_output.lua");
//        System.out.println(tmp_out.getCanonicalPath());
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
                }else if(line.contains("--[##$$ PATHTMP_String $$##]--")){
                    line = line.replace("--[##$$ PATHTMP_String $$##]--", "\""+path_tmpdir+"\"");
                    writer.write(line+"\n");
                }else if(line.contains("--[##$$ PATH_String $$##]--")){
                    line = line.replace("--[##$$ PATH_String $$##]--", "\""+basepath+"\"");
                    writer.write(line+"\n");
                }else if(line.contains("--[##$$ VAR_NAME = VAR_Double $$##]--")){
                    if(!nVar.isEmpty()){
                        for(int i = 0; i < nVar.size(); i++){
                            writer.write(nVar.get(i).getVarName()+"="+nVar.get(i).getValue1()+"\n");
                        }                        
                    }
                }else if(line.contains("--[##$$ PARAMETERS_HERE $$##]--")){
                    if(parameters.isEmpty() || parameters == null){
                        System.out.println("so the parameters are null, right?");
                        throw new IOException("Error: No parameters were found! "); 
                    }else{
                        for(int i = 0; i < parameters.size(); i++){
                            writer.write(parameters.get(i).getVarName()+"="+parameters.get(i).getValue1()+"\n");
                        }
                    }
                }else if (line.contains("--[##$$ SET_PARAMETERS_HERE $$##]--")){
                    for(int i = 0; i<parameters.size(); i++){
                        writer.write("\t\t"+"HocInterpreter:execute_hoc_stmt(\""+parameters.get(i).getVarName()+" =\".."+parameters.get(i).getVarName()+"..\"\")"+"\n");
                    }
                }else if(line.contains("--[##$$ SET_VARIABLES_HERE $$##]--")){
                    if(!nVar.isEmpty()){
                        for(int i =0; i < nVar.size(); i++){
                            writer.write("\t\t\t"+"HocInterpreter:execute_hoc_stmt(\""+nVar.get(i).getVarName() +" =\".."+nVar.get(i).getVarName() +"..\"\")"+"\n");
                        }
                    }
                    
                }else if(line.contains("--[##$$ PARAMETERNAMES_HERE $$##]--")){
                    String tmp = "";
                    for(int i = 0; i < parameters.size()-1; i++){
                        tmp = tmp + parameters.get(i).getVarName() + ",";
                    }
                    tmp = tmp + parameters.get(parameters.size()-1).getVarName();
                    line = line.replace("--[##$$ PARAMETERNAMES_HERE $$##]--", tmp);
                    writer.write(line+"\n");
                }else if(line.contains("--[##$$ FUNC_clipData()_M FUNC_reduceTimesteps()_M  FUNC_appendTables() $$##]-- ")){
                    writer.write("time_mod, current_mod = clipData("+rel_time.get(0).getValue1()+","+rel_time.get(0).getValue2()+",timeModel, currentModel)\n");
                    writer.write("time_mod, current_mod = reduceTimesteps(time_mod, current_mod, "+modeldata.getNextDataPoint()+")\n");
                    if(rel_time.size() > 1){
                    
                        for(int i = 1; i< rel_time.size(); i++){
                            writer.write("t_tmp, c_tmp = clipData("+rel_time.get(i).getValue1()+","+rel_time.get(i).getValue2()+",timeModel, currentModel)\n");
                            writer.write("t_tmp, c_tmp = reduceTimesteps(t_tmp, c_tmp, "+modeldata.getNextDataPoint()+")\n");
                            writer.write("time_mod, current_mod = appendTables(time_mod,current_mod, t_tmp, c_tmp)\n");
                        }
                    }
                }else if(line.contains("--[##$$ FUNC_convertUnits()_M $$##]-- ")){
                    writer.write("timeModel = convertUnits(time_mod,"+modeldata.getExponents()[0]+")\n");
                    writer.write("currentModel = convertUnits(current_mod,"+modeldata.getExponents()[1]+")\n");
                }else if(line.contains("--[##$$ filename $$##]-- ")){
                    line = line.replace("--[##$$ filename $$##]-- ", "\""+expdata.getDataFile().getCanonicalPath()+"\"");
                    writer.write(line);
                    
                }else if(line.contains("--[##$$ FUNC_convertUnits_ED $$##]-- ")){
                    writer.write("timeData = convertUnits(timeData,"+expdata.getExponents()[0]+")\n");
                    writer.write("currentData = convertUnits(currentData,"+expdata.getExponents()[1]+")\n");
                }else if(line.contains("--[##$$ MODEL_FILENAME_PART1 $$##]--") && line.contains("--[##$$ MODEL_FILENAME_PART2 $#]--") ){
                    line = line.replace("--[##$$ MODEL_FILENAME_PART1 $$##]--", modeldata.getNEURONout()[0]);
                    line = line.replace("--[##$$ MODEL_FILENAME_PART2 $#]--",modeldata.getNEURONout()[2]);
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

    public void setMethod_options(MethodOptions method_options) {
        this.method_options = method_options;
    }

    public MethodOptions getMethod_options() {
        return method_options;
    }

    public void setParams(PE_Options params) {
        this.params = params;
    }

    public PE_Options getParams() {
        return params;
    }

    public String getPath_tmpdir() {
        return path_tmpdir;
    }

    public void setPath_tmpdir(String path_tmpdir) {
        this.path_tmpdir = path_tmpdir;
    }
   
    
}
