/*
 * GUIContainerModel.java
 *
 * Created on 4. listopad 2005, 23:44
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package mvcimplementationtest;

import java.util.Observable;

/**
 *
 * @author reimei
 */
public class ApplicationModel extends Observable {
    public static String OP_ADD = "+";
    public static String OP_SUBTRACT = "-";
    public static String OP_MULTIPLY = "*";
    public static String OP_DIVIDE = "/";
    public static String OP_EQUALS = "=";
    String last_operation = OP_ADD;
    String display;
    float result = 0;
    boolean clear_display; //whether to clear display because the last button clicked was an operation button
    boolean digit_written; //behave reasonably in case user wants us perform several operations in a row without typing new digits
    StringBuffer history = new StringBuffer();
    
    /** Creates a new instance of GUIContainerModel */
    public ApplicationModel() {
        result = 0;
        display = "0";
        clear_display = true;
        digit_written = false;
    }
 
    public void addDigit(String digit) {
        if (clear_display) {
            display = digit;
            clear_display = false;
        } else
            display = display + digit;
        digit_written = true;
        setChanged();
        notifyObservers();
    }
    
    public String getDisplay() {
        return display; //Je to bezpecne? radsi new String(display); ne?
                        // --> tady to bezpecne je, protoze String nelze menit
                        // problem by to ale uz byl u StringBuffer
    }
    
    public String getHistory() {
        return new String(history);
    }
    
    
    private void updateDisplay() {
        display = ""+result;
        clear_display = true;
        setChanged();
        notifyObservers();
    }
    
    public void performOperation(String operation) {
        if (digit_written)
            history.append(display);
        history.append(operation);
        if (digit_written) {
            if (last_operation.equals(OP_ADD)) {
                add();
            }

            if (last_operation.equals(OP_SUBTRACT)) {
                subtract();
            }

            if (last_operation.equals(OP_MULTIPLY)) {
                multiply();
            }

            if (last_operation.equals(OP_DIVIDE)) {
                divide();
            }
            
            if (last_operation.equals(OP_EQUALS)) {
                result = Float.parseFloat(display);
                clear_display = true;
            }
        }// if digit_written
        digit_written = false;
        last_operation = operation; //je tohle prirazeni ok? co se vlastne prirazuje? reference? meni se jen puv. string?
    }
    
    /** Adds the number on display to the result
     *
     */
    public void add() {
        float disp = Float.parseFloat(display);
        result = result + disp;
        updateDisplay();
    }

    /** Subtracts the number on display from the result
     *
     */
    public void subtract() {
        float disp = Float.parseFloat(display);
        result = result - disp;
        updateDisplay();
    }

    /** Multiplies the result by the number on display
     *
     */
    public void multiply() {
        float disp = Float.parseFloat(display);
        result = result * disp;
        updateDisplay();
    }

    /** Divides the result by the number on display 
     *
     */
    public void divide() {
        float disp = Float.parseFloat(display);
        result = result / disp;
        updateDisplay();
    }
}

