
package org.tntstudio.utils.encode;

public abstract class Crypto {
	public static final int WEAK_ALGORITHM = 0;
	public static final int STRONG_ALGORITHM = 1;

	public static final String PROVIDER = "BC";
	public static final int SALT_LENGTH = 20;

	protected static final int IV_LENGTH = 16;
	protected static final int PBE_ITERATION_COUNT = 100;

	protected static final String RANDOM_ALGORITHM = "SHA1PRNG";
	protected static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	protected static final String SECRET_KEY_ALGORITHM = "AES";

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
