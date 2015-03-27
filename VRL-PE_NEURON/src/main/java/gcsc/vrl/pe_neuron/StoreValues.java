
package gcsc.vrl.pe_neuron;

/**
 * Class that stores two values that techniqually belong together: either two double values or a String and a double value
 * @author myra
 */
public class StoreValues {
 
    private double value1;
    private double value2; 
    private String varName;
  
    /*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    public StoreValues(double value1, double value2){
        
        this.value1 = value1;
        this.value2 = value2;
    }

    public StoreValues(String varName, double value1){
        
        this.varName = varName;
        this.value1 = value1;

    }
    
    public double getValue1() {
        return value1;
    }

    public double getValue2() {
        return value2;
    }

    public String getVarName() {
        return varName;
    }
    
    
    
}
