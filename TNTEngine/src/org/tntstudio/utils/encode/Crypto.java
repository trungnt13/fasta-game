
package org.tntstudio.utils.encode;


public abstract class Crypto {

	/** Return encrypted text from given clear text throw Exception if does not call
	 * {@link Crypto#generatedSecretKey(String, String)} first */
	public abstract String encrypt (String clearText);

	/** Return decrypted text from given encrypted text throw Exception if does not call
	 * {@link Crypto#generatedSecretKey(String, String)} first */
	public abstract String decrypt (String encrypted);

	/** Return encrypted text from given clear text, use given <b>salt</b> - against repeat attack throw Exception if does not call
	 * {@link Crypto#generatedSecretKey(String, String)} first */
	public abstract String encrypt (String password, String salt, String clearText);

	/** Return decrypted text from given encrypted text, use given <b>salt</b> - against repeat attack throw Exception if does not
	 * call {@link Crypto#generatedSecretKey(String, String)} first */
	public abstract String decrypt (String password, String salt, String encrypted);

	/** Generate Secret Key depend on password and salt before decrypt or encrypt data. */
	public abstract Crypto generatedSecretKey (String password, String salt);

	/** Generate passowrd */
	abstract String generatePassword (String password, String secondaryPassword);

	/** Generate Hash */
	public abstract String getHash (String password, String salt);

	/** Generate Salt, special element in order to make stronger password */
	public abstract String generateSalt ();

	/** Create random byte array */
	abstract byte[] generateIv ();
}
