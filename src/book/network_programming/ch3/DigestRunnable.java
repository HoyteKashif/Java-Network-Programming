package book.network_programming.ch3;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class DigestRunnable implements Runnable {
	private String fileName;

	public DigestRunnable(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void run() {
		try {
			FileInputStream in = new FileInputStream(fileName);
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			DigestInputStream din = new DigestInputStream(in, sha);
			while (din.read() != -1)
				;
			din.close();
			byte[] digest = sha.digest();

			StringBuilder result = new StringBuilder(fileName);
			result.append(": ");
			result.append(DatatypeConverter.printHexBinary(digest));
			System.out.println(result);
		} catch (IOException e) {
			System.err.println(e);
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e);
		}

	}

	public static void main(String[] args) {
		for (String fileName : args) {
			Thread t = new Thread(new DigestRunnable(fileName));
			t.start();
		}
	}
}
