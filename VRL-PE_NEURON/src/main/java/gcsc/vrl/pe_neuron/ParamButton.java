package gcsc.vrl.pe_neuron;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.visual.VButton;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.io.Serializable;
//import eu.mihosoft.vrl.reflection.VCanvasPopupMenu;
import eu.mihosoft.vrl.visual.CanvasActionListener;
/**
 *
 * @author myra
 */
@ComponentInfo(name = "Generate Parameter Set", category = "Optimization/NEURON", description = "")
public class ParamButton extends JFrame implements Serializable, CanvasActionListener{
        
    private static final long serialVersionUID = 1L;

    
   public ParamButton(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(100,100);
        setLocation(100,100);
        
        VButton button = new VButton("button1");
        button.addActionListener(this);
        add(button);
        
        setVisible(true);
    }
    
    
    @Override
    @MethodInfo(noGUI=true)
    public void actionPerformed(ActionEvent e){
	String command = e.getActionCommand();
        
        if(command.equals("button1")){
            popupmessage();
        }
	
    }
    
    @MethodInfo(noGUI=true)
    public void popupmessage(){
        JOptionPane.showMessageDialog(this, "Hello World");
    }
    
    public void vpopupcanvas(){
        
    }


}
