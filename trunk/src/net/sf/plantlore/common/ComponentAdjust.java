/*
 * ComponentAdjust.java
 *
 * Created on 18. leden 2006, 20:12
 *
 */

package net.sf.plantlore.common;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JComponent;

/**
 *
 * @author Jakub
 */
public class ComponentAdjust
{
    ArrayList list;
    boolean computed = false;
    int 
            maxW = 0, 
            minW = Integer.MAX_VALUE, 
            maxH = 0, 
            minH = Integer.MAX_VALUE;
    float
            avgW = 0,
            avgH = 0;
    
    /** Creates a new instance of ComponentAdjust */
    public ComponentAdjust()
    {
        list = new ArrayList();
    }
    
    public void add(JComponent c) {
        list.add(c);
        computed = false;
    }
    
    public void clear() {
        list.clear();
    }
    
    private void compute() {
        JComponent c;
        Dimension d;
        
        if (list.size() < 1)
            return;
        
        Iterator it = list.iterator();        
        while (it.hasNext()) {
            c = (JComponent) it.next();
            d = c.getPreferredSize();
            maxW = d.width > maxW ? d.width : maxW;
            minW = d.width < minW ? d.width : minW;
            maxH = d.height > maxH ? d.height : maxH;
            minH = d.height < minH ? d.height : minH;
            avgW += d.width;
            avgH += d.height;
        }
        avgW = avgW / list.size();
        avgH = avgH / list.size();
        
        computed = true;
    }//compute()
    
    public void setMaxWidth() {
        JComponent c;
        if (!computed)
            compute();
        
        Dimension d = new Dimension(maxW, (int)avgH);

        Iterator it = list.iterator();
        while (it.hasNext()) {
            c = (JComponent) it.next();
            c.setPreferredSize(d);
        }
    }//setMaxWidth()
}



