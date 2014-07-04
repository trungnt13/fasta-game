
package org.tntstudio.utils.encode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.tntstudio.TNTRuntimeException;
import org.tntstudio.core.Top;

public class AndroidCrypto extends Crypto {

	public static final String PROVIDER = "BC";
	public static final int SALT_LENGTH = 20;
	public static final int IV_LENGTH = 16;
	public static final int PBE_ITERATION_COUNT = 100;

	private static final String RANDOM_ALGORITHM = "SHA1PRNG";
	private static final String HASH_ALGORITHM = "SHA-512";
	private static final String PBE_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";
	private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String SECRET_KEY_ALGORITHM = "AES";

	private SecretKey mBackupKey;

	AndroidCrypto () {
	}

	public String encrypt (String clearText) {
		if (mBackupKey == null) {
			throw new TNTRuntimeException("Generate Secret Key first");
		}
		return encrypt(mBackupKey, clearText);
	}

	public String decrypt (String encrypted) {
		if (mBackupKey == null) {
			throw new TNTRuntimeException("Generate Secret Key first");
		}
		return decrypt(mBackupKey, encrypted);
	}

	public String encrypt (String password, String salt, String clearText) {
		generatedSecretKey(password, salt);
		return encrypt(mBackupKey, clearText);
	}

	public String decrypt (String password, String salt, String encrypted) {
		generatedSecretKey(password, salt);
		return decrypt(mBackupKey, encrypted);
	}

	String encrypt (SecretKey secret, String cleartext) {
		try {

			byte[] iv = generateIv();

			String ivHex = HexEncoder.toHex(iv);
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			Cipher encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
			encryptionCipher.init(Cipher.ENCRYPT_MODE, secret, ivspec);
			byte[] encryptedText = encryptionCipher.doFinal(cleartext.getBytes("UTF-8"));
			String encryptedHex = HexEncoder.toHex(encryptedText);

			return ivHex + encryptedHex;

		} catch (Exception e) {
			throw new TNTRuntimeException("Unable to encrypt", e);
		}
	}

	String decrypt (SecretKey secret, String encrypted) {
		try {
			Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
			String ivHex = encrypted.substring(0, IV_LENGTH * 2);
			String encryptedHex = encrypted.substring(IV_LENGTH * 2);
			IvParameterSpec ivspec = new IvParameterSpec(HexEncoder.toByte(ivHex));
			decryptionCipher.init(Cipher.DECRYPT_MODE, secret, ivspec);
			byte[] decryptedText = decryptionCipher.doFinal(HexEncoder.toByte(encryptedHex));
			String decrypted = new String(decryptedText, "UTF-8");
			return decrypted;
		} catch (Exception e) {
			throw new TNTRuntimeException("Unable to decrypt", e);
		}
	}

	public Crypto generatedSecretKey (String password, String salt) {
		if (Top.tgame != null) password = generatePassword(password, Top.tgame.getGameIdentification());
		try {
			PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), HexEncoder.toByte(salt), PBE_ITERATION_COUNT, 256);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGORITHM, PROVIDER);
			SecretKey tmp = factory.generateSecret(pbeKeySpec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), SECRET_KEY_ALGORITHM);
			mBackupKey = secret;
			return this;
		} catch (Exception e) {
			throw new TNTRuntimeException("Unable to get secret key", e);
		}
	}

	final String generatePassword (String password, String secondaryPassword) {
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for (int i = 0; i < secondaryPassword.length(); i++) {
			if (secondaryPassword.charAt(i) == '.') {
				builder.append(password.charAt(count++));
			} else
				builder.append(secondaryPassword.charAt(i));
			if (count >= password.length()) count = 0;
		}
		return builder.toString();
	}

	public String getHash (String password, String salt) {
		if (Top.tgame != null) password = generatePassword(password, Top.tgame.getGameIdentification());
		try {
			String input = password + salt;
			MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM, PROVIDER);
			byte[] out = md.digest(input.getBytes("UTF-8"));
			return HexEncoder.toHex(out);
		} catch (Exception e) {
			throw new TNTRuntimeException("Unable to get hash", e);
		}
	}

	public String generateSalt () {
		try {
			SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
			byte[] salt = new byte[SALT_LENGTH];
			random.nextBytes(salt);
			String saltHex = HexEncoder.toHex(salt);
			return saltHex;
		} catch (Exception e) {
			throw new TNTRuntimeException("Unable to generate salt", e);
		}
	}

	byte[] generateIv () {
		SecureRandom random;
		try {
			random = SecureRandom.getInstance(RANDOM_ALGORITHM);
			byte[] iv = new byte[IV_LENGTH];
			random.nextBytes(iv);
			return iv;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
