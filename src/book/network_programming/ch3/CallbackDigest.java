package book.network_programming.ch3;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Example 3-5
 */
public class CallbackDigest implements Runnable {
	private String fileName;

	public CallbackDigest(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			FileInputStream in = new FileInputStream(fileName);
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			DigestInputStream din = new DigestInputStream(in, sha);
			while (din.read() != -1)
				; // read entire file
			din.close();
			CallbackDigestUserInterface.receiveDigest(sha.digest(), fileName);
		} catch (IOException | NoSuchAlgorithmException e) {
			System.err.println(e);
		}
	}
}
