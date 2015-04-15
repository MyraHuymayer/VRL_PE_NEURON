/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import param_est.save_load;
//import java.io.File;
/**
 *
 * @author myra
 */
//TODO/NICE2HAVE: bei start der VRL sollen alle Arrays aufgeraeumt werden 
@ComponentInfo(name = "Options for Parameter Estimator", category = "Optimization/NEURON", description = "")
public class PE_Options implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private ArrayList<String> parameter_names =new ArrayList<String>();
    private ArrayList<String> xml_line = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    
    private transient ArrayList<StoreValues> params = new ArrayList<StoreValues>();
     
/*-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    

//    was muss abgefangen werden? 
//          ==> Parameternamen duerfen nicht gleich sein
//          ==> Min darf nicht groesser als parameter sein/Max nicht kleiner
    public void generateParameterSet(@ParamInfo(name = "", style="selection", options="value=[\"Parameter\", \"Fixer_Parameter\"]") String paramtype, //siehe z.B. Fixerparameter bei Ivo
            @ParamInfo(name = "Parameter value") double parameter, 
            @ParamInfo(name = "Minimal value")double min,
            @ParamInfo(name = "Maximal value")double max, 
            @ParamInfo(name = "Parameter name") String paramName) throws IOException{
        
        
    
        if(parameter_names == null){
            parameter_names =new ArrayList<String>();
        }
        
        
        if(params == null ){
           params = new ArrayList<StoreValues>(); 
        }
        if(type == null){
            type = new ArrayList<String>();
        }
        
        parameter_names.add(paramName);
        type.add(paramtype);
        StoreValues param_name_val = new StoreValues(paramName, parameter);
        
        params.add(param_name_val);
        
        if(parameter_names.size() >=2){
            for(int ii = 0; ii < parameter_names.size()-1; ii++){
//                System.out.println("Parameter name in Array: "+parameter_names.get(ii)+ "; Parameter name:" +paramName);
                if(paramName.equals(parameter_names.get(ii))){
                    parameter_names.remove(parameter_names.size()-1);
                    throw new IOException("Error: Parameter names must differ from each other!");
                }
            }
//            System.out.println("----------------------------------------------------------------------------" );
        }
        
        
//        System.out.println(parameter_names.get(parameter_names.size()-1));
//        System.out.println("Size of parameter names array list: "+parameter_names.size());
        if(min >= parameter){
            throw new IOException("Error: Minimal limit value must be smaller than the parameter value!");
        }
        if(max <= parameter){
            throw new IOException("Error: Maximal limit value must exceed the parameter value! ");
        }
        
        
        //produzieren eine String der Form:
        writeXML_line(paramtype, parameter, min, max, paramName);
        
//        NOTE: wenn man am schluss fertig mit dem Array ist sollte man es loeschen 
//        System.out.println("TEST: Size of array: "+xml_line.size());
        
    }
    
    /**
     * Method to store a String element to xml_line Array that will later be written to the xml file: input is the same as in generateParameterSet
     * @param paramtype parameter type that will be set in generateParameterSet(): can be either a Parameter or a Fixer_Parameter
     * @param parameter Value of the parameter
     * @param min minimal limit
     * @param max maximal limit 
     * @param paramName parameter name 
     */
    private void writeXML_line(String paramtype, double parameter, double min, double max, String paramName){
        
        if(xml_line == null){
            xml_line = new ArrayList<String>();
        }
        
        String line;
        if(paramtype.equals("Parameter")){
            line = "<Parameter Value=\""+parameter+"\" Max=\""+max+"\" Min=\""+min+"\" Name=\""+paramName+"\"/>";
            xml_line.add(line);
        }else if(paramtype.equals("Fixer_Parameter")){
            line = "<Fixer_Parameter Value=\""+parameter+"\" Max=\""+max+"\" Min=\""+min+"\" Name=\""+paramName+"\"/>";
            xml_line.add(line);
        }
    }
    
    /**
     * delete all elements from the parameterNames
     */
    public void clearParameters(){
        if(!parameter_names.isEmpty() && parameter_names != null){     
            parameter_names.clear();
        }
        
        if(!xml_line.isEmpty() && xml_line !=null){
            xml_line.clear();
        }
        
        if(!type.isEmpty() && type != null){
            type.clear();
        }
        if(!params.isEmpty() && params != null){
            params.clear();
        }
    }
    
    /**
     * Invocation of this method shows a popup window containing the parameters already added 
     */
   public void displayParameters(){
       String message = "";
       for(int i =0; i<xml_line.size(); i++){
           String tmp = xml_line.get(i);
           tmp = tmp.replace("<", "");
           tmp = tmp.replace("/>", "");
           message = message+tmp+" \n";
       }
      JOptionPane.showMessageDialog(null, message);
      
   }
   public void deleteByName(@ParamInfo(name = "Parameter name") String paramName){
       
       if(parameter_names != null){
            for(int i = 0; i< parameter_names.size(); i++){
                if(paramName.equals(parameter_names.get(i))){
                    xml_line.remove(i);
                    parameter_names.remove(i);
                    params.remove(i);
                    type.remove(i);
                    System.out.println("Parameter removed :)!");
                }
            }
       }
       
   }
   

   /**
    * Load an xml file containing only the name, type and value of the parameters --> 
    * @param file choose the file, where the parameters were stored 
    */
    public void loadParameters(@ParamInfo(name="Parameter file ", style = "load-dialog", options = "")File file, 
            @ParamInfo(name="Parameter Variation in percent ", style = "", options = "value = 0.15") double percentage) throws ParserConfigurationException{
        
        if(parameter_names == null){
            parameter_names =new ArrayList<String>();
        }
        
        
        if(params == null ){
           params = new ArrayList<StoreValues>(); 
        }
        if(type == null){
            type = new ArrayList<String>();
        }
        save_load load_parameters = new save_load();
        load_parameters.read_file(file);
        
        if(load_parameters.get_name_of_root_Element().equals("store_parameters")){
            Element E = load_parameters.get_root_Element();
//            Element E1 = load_parameters.get_Element("Parameters", E);s
            
            NodeList nl = load_parameters.get_NodeList("Parameters", E);
            
            for(int i = 0; i<nl.getLength(); i++){
                Element E1 = (Element) nl.item(i);
                String paramName = load_parameters.get_Attribute("Name", E1);
                double paramVal = load_parameters.get_Attribute_double("Value", E1);
                String paramType = load_parameters.get_Attribute("Type", E1);
                double min;
                double max;
                
                if(paramVal < 0){
                     min = paramVal + (paramVal * percentage);
                     max =  paramVal - (paramVal * percentage);
                }else{
                    min = paramVal - (paramVal * percentage);
                    max =  paramVal + (paramVal * percentage);
                }
                StoreValues sv = new StoreValues(paramName, paramVal);
                writeXML_line(paramType,paramVal, min, max, paramName);
                params.add(sv);
                parameter_names.add(paramName);
                type.add(paramType);
            }
        }
//        load_xml.create_rootElement("store_parameters");
        
     
    } 

    public void storeParameters(@ParamInfo(name="Store Parameters", style = "load-folder-dialog", options = "")String file_name) throws ParserConfigurationException{

        save_load store_parameters = new save_load();
        Element E = store_parameters.create_rootElement("store_parameters");
        
        for(int i = 0; i< params.size(); i++){
            
            Element E1 = store_parameters.create_Element("Parameters", E);
            Double val = params.get(i).getValue1();
            store_parameters.add_Attribute("Type", type.get(i), E1);
            store_parameters.add_Attribute("Name", params.get(i).getVarName(), E1);
            store_parameters.add_Attribute("Value", val.toString(), E1);
        }
        store_parameters.write_file(file_name);
    }
  
    /**
     * After inserting all parameter options this methods returns all Strings that will be written to the xml file. 
     * @return parameter options that will be included in xml file
     */
   public ArrayList<String> finishedParamGeneration(){
       ArrayList<String> for_file = new ArrayList<String>();
       
       for(int i = 0; i< xml_line.size(); i++){
           for_file.add(xml_line.get(i));
       } 
       
//       clearXML_line();
//       clearParameterNames(); //das macht Probleme beim ParameterSchaetzer
       
       return for_file;
   }

   @MethodInfo(noGUI=true)
    public ArrayList<StoreValues> getParams() {
        return params;
    }

   @MethodInfo(noGUI=true)
    public ArrayList<String> getParameter_names() {
        return parameter_names;
    }

   @MethodInfo(noGUI=true)
    public ArrayList<String> getXml_line() {
        return xml_line;
    }

   
}
// @ComponentInfo(name="Add Integers", category="Custom")
// class AddIntegers implements Serializable {
// 
//   private static final long serialVersionUID=1;
// 
//   public Integer add(
//     @ParamGroupInfo(group="Group a|true|no description")
//     Integer a, 
//     @ParamGroupInfo(group="Group a")
//     Integer b, 
//     @ParamGroupInfo(group="Group b|true|no description")
//     boolean b2,
//     @ParamGroupInfo(group="Group b")
//     @ParamInfo(name="", style="selection", options="value=[\"ITem 1\", \"Item 2\"]") String s){
//     return a+b;
//   }
// }