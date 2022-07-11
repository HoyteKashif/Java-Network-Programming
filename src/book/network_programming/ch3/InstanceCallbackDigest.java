package book.network_programming.ch3;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Example 3-7
 */
public class InstanceCallbackDigest implements Runnable {
	private String fileName;
	private InstanceCallbackDigestUserInterface callback;

	public InstanceCallbackDigest(String fileName, InstanceCallbackDigestUserInterface callback) {
		this.fileName = fileName;
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			FileInputStream in = new FileInputStream(fileName);
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			DigestInputStream din = new DigestInputStream(in, sha);
			while (din.read() != -1)
				;// read entire file
			din.close();
			callback.receiveDigest(sha.digest());
		} catch (IOException | NoSuchAlgorithmException e) {
			System.err.println(e);
		}
	}
}
