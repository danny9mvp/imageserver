/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imageserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 *
 * @author ASUS
 */
public class ServerOutputStream extends OutputStream{
    
    private JTextArea loggingTextArea;
    
    public ServerOutputStream(JTextArea jTextArea){
        this.loggingTextArea = jTextArea;        
    }

    @Override
    public void write(int b) throws IOException {        
        this.loggingTextArea.append(String.valueOf((char)b));
    }
    
}
