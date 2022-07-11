package book.network_programming.ch3;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Example 3-3 A thread that uses an accessor method to return the digest
 */
public class ReturnDigest extends Thread {
	private String fileName;
	private byte[] digest;

	public ReturnDigest(String fileName) {
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
			digest = sha.digest();
		} catch (IOException e) {
			System.err.println(e);
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e);
		}
	}

	public byte[] getDigest() {
		return digest;
	}
}
