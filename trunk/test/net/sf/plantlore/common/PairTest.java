/*
 * PairTest.java
 * JUnit based test
 *
 * Created on 12. duben 2006, 17:42
 */

package net.sf.plantlore.common;

import junit.framework.*;

/**
 *
 * @author kotoj1am
 */
public class PairTest extends TestCase {
        Pair<String, Integer> si;
        Pair<String, String> ss;
        Pair<Integer, Integer> ii;
        
    public PairTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        System.out.println("setting up");
        si = new Pair<String, Integer>("FedoraCore",5);
        ss = new Pair<String, String>("John","Doe");
        ii = new Pair<Integer,Integer>(5,6);
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PairTest.class);
        
        return suite;
    }

    /**
     * Test of getFirst method, of class net.sf.plantlore.common.Pair.
     */
    public void testGetFirst() {
        System.out.println("getFirst");
        
        String s = si.getFirst();
        assertEquals("FedoraCore",s);
        
        s = ss.getFirst();
        assertEquals("John",s);
        
        assertEquals((int)5,(int)ii.getFirst());
    }

    /**
     * Test of setFirst method, of class net.sf.plantlore.common.Pair.
     */
    public void testSetFirst() {
        System.out.println("setFirst");
        ss.setFirst("xxx");
        assertEquals("xxx", ss.getFirst());
        
        ii.setFirst(-8);
        assertEquals((int)-8, (int)ii.getFirst());
    }

    /**
     * Test of getSecond method, of class net.sf.plantlore.common.Pair.
     */
    public void testGetSecond() {
        System.out.println("getSecond");
        assertEquals(5, (int)si.getSecond());
        assertEquals("Doe", ss.getSecond());
        assertEquals(6,(int)ii.getSecond());
    }

    /**
     * Test of setSecond method, of class net.sf.plantlore.common.Pair.
     */
    public void testSetSecond() {
        System.out.println("setSecond");
        ii.setSecond(-9);
        assertEquals(-9, (int)ii.getSecond());
        
        ss.setSecond("xxx");
        assertEquals("xxx", ss.getSecond());
    }

    /**
     * Test of equals method, of class net.sf.plantlore.common.Pair.
     */
    public void testEquals() {
        System.out.println("equals");
        
        Pair<String,Integer> t = new Pair<String,Integer>("FedoraCore",5);
        assertTrue(t.equals(si));
        
        Pair<String,String> u = new Pair<String,String>("John","Doe");
        assertTrue(u.equals(ss));
        
        assertFalse(t.equals(u));
        assertFalse(t.equals(5));
    }

    /**
     * Test of toString method, of class net.sf.plantlore.common.Pair.
     */
    public void testToString() {
        System.out.println("toString");
        assertEquals("[John,Doe]",ss.toString());
        assertEquals("[5,6]",ii.toString());
        assertEquals("FedoraCore",si.toString());
    }
    
}
