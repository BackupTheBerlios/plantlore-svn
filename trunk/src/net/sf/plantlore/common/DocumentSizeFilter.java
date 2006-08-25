/*
 * DocumentSizeFilter.java
 *
 * Created on 23. srpen 2006, 16:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class DocumentSizeFilter extends DocumentFilter {
    int maxCharacters;

    public DocumentSizeFilter(int maxChars) {
        maxCharacters = maxChars;
    }

    @Override
    public void insertString(FilterBypass fb, int offs,String str, AttributeSet a)
        throws BadLocationException 
    {
        if (str == null || str.length() == 0)
            return;
        
        //if the string is short enough then simply insert it
        if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
            fb.insertString(offs, str, a);
        else { //else cut it if there's  some space left
            int spaceLeft = maxCharacters - fb.getDocument().getLength();
            if (spaceLeft <= 0)
                return;
            
            fb.insertString(offs, str.substring(0,spaceLeft), a);
        }
    }//insertString
    
    @Override
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
        throws BadLocationException 
    {
        if (str == null) { 
            fb.replace(offs, length, str, a);
            return;
        }

        if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters)
            fb.replace(offs, length, str, a);
        else {
            int spaceLeft = maxCharacters - fb.getDocument().getLength() + length;
            if (spaceLeft <= 0)
                return;
            
            fb.replace(offs, length, str.substring(0,spaceLeft), a);
        }
    }//replace

}//DocumentSizeFilter


