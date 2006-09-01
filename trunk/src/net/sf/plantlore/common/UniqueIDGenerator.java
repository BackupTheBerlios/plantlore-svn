package net.sf.plantlore.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * A unique identifier generator.
 *
 * Based on http://www.javapractices.com/Topic56.cjp
 * 
 * @author Erik Kratochvíl (discontinuum@gmail.com)
 * @since 2006-08-30
 *
 */
public class UniqueIDGenerator {
	
	/**
	 * Encode the digest to some human readable format.
	 * 
	 * @param input	The digest from SHA1 or MD5
	 * @return
	 */
	static private String encode( byte[] input){
	    StringBuffer result = new StringBuffer();
	    char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	    for( byte b : input ) {
	      result.append( digits[ (b&0xf0) >> 4 ] );
	      result.append( digits[ b&0x0f] );
	    }
	    return result.toString();
	  }

	/**
	 * 
	 * @return A new unique identifier or null if it is not possible to create the identifier.
	 */
	public static String generate() {
		String base = 
			System.getProperty("os.name") + " " +
			System.getProperty("user.name") + " " + 
			System.getProperty("user.language") + " " +
			System.currentTimeMillis();
		
		try {
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");		      
			base = base + " " + prng.nextInt();
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			return encode( sha.digest(base.getBytes()) );
		}
		catch ( NoSuchAlgorithmException ex ) {
			return null;
		}
	}

}
