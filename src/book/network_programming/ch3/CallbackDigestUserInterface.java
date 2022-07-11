package book.network_programming.ch3;

import javax.xml.bind.DatatypeConverter;

/*
 * Example 3-6
 */
public class CallbackDigestUserInterface {
	public static void main(String[] args) {
		for (String fileName : args) {
			CallbackDigest cb = new CallbackDigest(fileName);
			Thread t = new Thread(cb);
			t.start();
		}
	}

	public static void receiveDigest(byte[] digest, String fileName) {
		StringBuilder result = new StringBuilder(fileName);
		result.append(": ");
		result.append(DatatypeConverter.printHexBinary(digest));
		System.out.println(result);
	}
}
