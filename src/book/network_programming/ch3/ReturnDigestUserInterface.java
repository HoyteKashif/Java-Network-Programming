package book.network_programming.ch3;

import javax.xml.bind.DatatypeConverter;

public class ReturnDigestUserInterface {
	public static void main(String[] args) {

		// Example 3-4: digestion may or may not finish by the time the digest is
		// retrieved and may result in a NullPointerException being thrown
		for (String fileName : args) {
			// Calculate the digest
			ReturnDigest dr = new ReturnDigest(fileName);
			dr.start();

			// Now print the result
			StringBuilder result = new StringBuilder(fileName);
			result.append(": ");
			result.append(DatatypeConverter.printHexBinary(dr.getDigest()));
			System.out.println(result);
		}

		// Race Condition:
		// Possible solution is to not retrieve the digest until all start methods have
		// been called, this might work depending on how many arguments are passed in
		ReturnDigest[] digests = new ReturnDigest[args.length];
		for (int i = 0; i < args.length; i++) {
			digests[i] = new ReturnDigest(args[i]);
			digests[i].start();
		}

		for (int i = 0; i < args.length; i++) {
			StringBuilder result = new StringBuilder(args[i]);
			result.append(": ");
			result.append(DatatypeConverter.printHexBinary(digests[i].getDigest()));
			System.out.println(result);
		}

		// Polling:
		// Possible solution
		// Not a good approach since the main thread is constantly polling for the
		// availability of the digest and not allowing the worker thread to perform
		// execution and so not a good approach
		digests = new ReturnDigest[args.length];
		for (int i = 0; i < args.length; i++) {
			digests[i] = new ReturnDigest(args[i]);
			digests[i].start();
		}

		for (int i = 0; i < args.length; i++) {
			while (true) {
				byte[] digest = digests[i].getDigest();
				if (digest != null) {
					StringBuilder result = new StringBuilder(args[i]);
					result.append(": ");
					result.append(DatatypeConverter.printHexBinary(digest));
					System.out.println(result);
					break;
				}
			}
		}
	}
}
