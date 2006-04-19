/*
 * AddEditCtrl.java
 *
 * Created on 4. duben 2006, 10:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.client;

/**
 *
 * @author reimei
 */
public class AddEditCtrl {
    private boolean inEditMode = false;
    private boolean inAddMode = true;
    
    /** Creates a new instance of AddEditCtrl */
    public AddEditCtrl(boolean edit) {
        this.inEditMode = edit;
        this.inAddMode = ! edit;
    }
    
}
