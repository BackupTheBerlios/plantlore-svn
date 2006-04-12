/*
 * Pair.java
 *
 * Created on 12. duben 2006, 17:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package net.sf.plantlore.common;

/**
 *
 * @author Jakub
 */
public class Pair<A, B> {
    private A a;
    private B b;
    
    /** Creates a new instance of Pair */
    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
    
    /** Returns the first element of this pair.
     * @return the first element of this pair
     */
    public A getFirst() {
        return a;
    }

    /** Sets the first element of this pair.
     */
    public void setFirst(A a) {
        this.a = a;
    }

    /** Returns the second element of this pair.
     * @return the second element of this pair
     */
    public B getSecond() {
        return b;
    }

    /** Sets the second element of this pair.
     * @return the second element of this pair
     */
    public void setSecond(B b) {
        this.b = b;
    }

    /** Compares this object to the parameter o.
     *
     *  Uses the elements equals methods.
     *
     * @return true if o is instance of Pair and first and second elements equal respectively.
     * @return false otherwise
     */
    public boolean equals(Object o) {
        if (!(o instanceof Pair)) //can't use generics with instanceof - it doesn't even make sense thanks to the type erasure technique using which generics are implemented
            return false;
        else 
        {
            Pair p = (Pair)o; //(Pair<C,D>)o  cast is unchecked and doesn't make sense either because the actual class is always just Pair
            return ( a.equals(p.getFirst()) && b.equals(p.getSecond()) );
        }
    }
    
    /** Converts this pair to String.
     *
     * @return "[first,second]" in case first and second are both instances of String
     * @return if only one of the elements is a String then this one is returned
     * @return "[first.toString,second.toString]" if both elements are non-strings
     * @return null otherwise
     */
    public String toString() 
    {
        if (a == null || b == null)
            return null;
        if ((a instanceof String)&&(b instanceof String))
            return "["+a+","+b+"]";

        if (a instanceof String)
            return (String) a; //heh :)

        if (b instanceof String)
            return (String) b; //hmm:)
        
        return "["+a+","+b+"]";
    }
}
