/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JOptionPane;
//import java.io.File;
/**
 *
 * @author myra
 */
//diese klasse soll eine xml datei speichern mit der benoetigten parameter anzahl
//NOTE: Evtl macht es mehr Sinn die beiden Methoden in eigenen Klassen unterzubringen und in einer Dritten Klasse die xml Datei erstellen? 
@ComponentInfo(name = "Options for Parameter Estimator", category = "Optimization/NEURON", description = "")
public class PE_Options implements Serializable{
    
    private static final long serialVersionUID = 1L;
//    private File paramEst;
    private ArrayList<String> parameter_names =new ArrayList<String>();
//    parameter_names = 
    private ArrayList<String> xml_line = new ArrayList<String>();
    
    private transient ArrayList<StoreValues> params = new ArrayList<StoreValues>();
     

    

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
        
        if(xml_line == null){
            xml_line = new ArrayList<String>();
        }
        
        if(params == null ){
           params = new ArrayList<StoreValues>(); 
        }
        
        parameter_names.add(paramName);
        StoreValues param_name_val = new StoreValues(paramName, parameter);
        
        params.add(param_name_val);
        
        if(parameter_names.size() >=2){
            for(int ii = 0; ii < parameter_names.size()-1; ii++){
                System.out.println("Parameter name in Array: "+parameter_names.get(ii)+ "; Parameter name:" +paramName);
                if(paramName.equals(parameter_names.get(ii))){
                    parameter_names.remove(parameter_names.size()-1);
                    throw new IOException("Error: Parameter names must differ from each other!");
                }
            }
            System.out.println("----------------------------------------------------------------------------" );
        }
        
        
        System.out.println(parameter_names.get(parameter_names.size()-1));
        System.out.println("Size of parameter names array list: "+parameter_names.size());
        if(min >= parameter){
            throw new IOException("Error: Minimal limit value must be smaller than the parameter value!");
        }
        if(max <= parameter){
            throw new IOException("Error: Maximal limit value must exceed the parameter value! ");
        }
        
        String line;
        //produzieren eine String der Form:
        if(paramtype.equals("Parameter")){
            line = "<Parameter Value=\""+parameter+"\" Max=\""+max+"\" Min=\""+min+"\" Name=\""+paramName+"\"/>";
            xml_line.add(line);
        }else if(paramtype.equals("Fixer_Parameter")){
            line = "<Fixer_Parameter Value=\""+parameter+"\" Max=\""+max+"\" Min=\""+min+"\" Name=\""+paramName+"\"/>";
            xml_line.add(line);
        }
        
//        NOTE: wenn man am schluss fertig mit dem Array ist sollte man es loeschen 
        System.out.println("TEST: Size of array: "+xml_line.size());
        
    }
    
    public void clearParameterNames(){
        parameter_names.clear();
    }
    
    public void clearXML_line(){
        xml_line.clear();
    }

  
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
       
       for(int i = 0; i< parameter_names.size(); i++){
           if(paramName.equals(parameter_names.get(i))){
               parameter_names.remove(i);
               params.remove(i);
               System.out.println("Parameter removed :)!");
           }
       }
       
   }
   
   public ArrayList<String> finishedParamGeneration(){
       ArrayList<String> for_file = new ArrayList<String>();
       
       for(int i = 0; i< xml_line.size(); i++){
           for_file.add(xml_line.get(i));
       } 
       
       clearXML_line();
       clearParameterNames();
       
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