package book.network_programming.ch4;

import java.net.InetAddress;
import java.net.UnknownHostException;

// Page 101 Bytes returned are unsigned, Java doesn't have an unsigned byte primitive data type. 
public class AddressConversion {
	public static void main(String[] args) throws UnknownHostException {
		InetAddress localhost = InetAddress.getLocalHost();
		byte[] addressBytes = localhost.getAddress();
		for (byte aByte : addressBytes) {
			System.out.println("signed: " + aByte + " unsigned: " + unsignedByte(aByte));
		}
		System.out.println(localhost.getHostAddress());
	}

	public static int unsignedByte(byte signedByte) {
		// signed byte is either positive or negative
		// signed byte is automatically promoted to an int before the addition is
		// performed, so wraparound is not a problem
		return signedByte < 0 ? signedByte + 256 : signedByte;
	}
}
