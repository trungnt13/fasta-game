
package org.tntstudio.utils.encode;

public final class HexEncoder {
	private static final byte[] encodingTable = {(byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', (byte)'6',
		(byte)'7', (byte)'8', (byte)'9', (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f'};

	/*
	 * set up the decoding table.
	 */
	private static final byte[] decodingTable = new byte[128];

	static {
		initialiseDecodingTable();
	}

	private static void initialiseDecodingTable () {
		for (int i = 0; i < encodingTable.length; i++) {
			decodingTable[encodingTable[i]] = (byte)i;
		}

		decodingTable['A'] = decodingTable['a'];
		decodingTable['B'] = decodingTable['b'];
		decodingTable['C'] = decodingTable['c'];
		decodingTable['D'] = decodingTable['d'];
		decodingTable['E'] = decodingTable['e'];
		decodingTable['F'] = decodingTable['f'];
	}

	private HexEncoder () {
	}

	/** encode the input data producing a Hex output stream.
	 * 
	 * @return the number of bytes produced. */
	public static String toHex (byte[] data) {
		return toHex(data, 0, data.length);
	}

	/** encode the input data producing a Hex output stream.
	 * 
	 * @return the number of bytes produced. */
	public static String toHex (byte[] data, int off, int length) {
		char[] result = new char[length * 2];
		int count = 0;

		for (int i = off; i < (off + length); i++) {
			int v = data[i] & 0xff;

			result[2 * count] = (char)encodingTable[(v >>> 4)];
			result[2 * count + 1] = (char)encodingTable[v & 0xf];
			count++;
		}

		return new String(result);
	}

	private static boolean ignore (char c) {
		return (c == '\n' || c == '\r' || c == '\t' || c == ' ');
	}

	/** decode the Hex encoded byte data writing it to the given output stream, whitespace characters will be ignored.
	 * 
	 * @return the number of bytes produced. */
	public static byte[] toByte (String data, int off, int length) {
		byte[] result = new byte[length / 2];

		byte b1, b2;
		int outLen = 0;

		int end = off + length;

		while (end > off) {
			if (!ignore((char)data.charAt(end - 1))) {
				break;
			}

			end--;
		}

		int i = off;
		while (i < end) {
			while (i < end && ignore((char)data.charAt(i))) {
				i++;
			}

			b1 = decodingTable[data.charAt(i++)];

			while (i < end && ignore((char)data.charAt(i))) {
				i++;
			}

			b2 = decodingTable[data.charAt(i++)];

			result[outLen++] = (byte)((b1 << 4) | b2);

			outLen++;
		}

		return result;
	}

	/** decode the Hex encoded String data writing it to the given output stream, whitespace characters will be ignored.
	 * 
	 * @return the number of bytes produced. */
	public static byte[] toByte (String data) {
		byte[] result = new byte[data.length() / 2];

		byte b1, b2;
		int length = 0;

		int end = data.length();

		while (end > 0) {
			if (!ignore(data.charAt(end - 1))) {
				break;
			}

			end--;
		}

		int i = 0;
		while (i < end) {
			while (i < end && ignore(data.charAt(i))) {
				i++;
			}

			b1 = decodingTable[data.charAt(i++)];

			while (i < end && ignore(data.charAt(i))) {
				i++;
			}

			b2 = decodingTable[data.charAt(i++)];

			result[length++] = (byte)((b1 << 4) | b2);

		}

		return result;
	}
}
