/*
 * PostTaskAction.java
 *
 * Created on 3. září 2006, 23:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

/**
 *
 * @author fraktalek
 */
public interface PostTaskAction {    
    public void afterStopped(Object value) throws Exception;
}
