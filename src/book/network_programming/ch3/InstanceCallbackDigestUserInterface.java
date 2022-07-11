package book.network_programming.ch3;

import javax.xml.bind.DatatypeConverter;

/*
 * Example 3-8
 */
public class InstanceCallbackDigestUserInterface {
	private String fileName;
	private byte[] digest;

	public InstanceCallbackDigestUserInterface(String fileName) {
		this.fileName = fileName;
	}

	public void calculateDigest() {
		InstanceCallbackDigest cb = new InstanceCallbackDigest(fileName, this);
		Thread t = new Thread(cb);
		t.start();
	}

	void receiveDigest(byte[] digest) {
		this.digest = digest;
		System.out.println(this);
	}

	@Override
	public String toString() {
		String result = fileName + ": ";
		if (digest != null) {
			result += DatatypeConverter.printHexBinary(digest);
		} else {
			result += "digest not available";
		}
		return result;
	}

	public static void main(String[] args) {
		for (String fileName : args) {
			InstanceCallbackDigestUserInterface d = new InstanceCallbackDigestUserInterface(fileName);
			d.calculateDigest();
		}
	}
}
