package book.network_programming.ch2;

import java.io.IOException;
import java.io.InputStream;

public class InputStreams {

	public static void read10Bytes(InputStream in) throws IOException {
		byte[] input = new byte[10];
		for (int i = 0; i < input.length; i++) {

			// unsigned byte from 0 - 255
			int b = in.read();

			// end of stream
			if (b == -1) {
				break;
			}

			// signed byte from -128 to 127
			input[i] = (byte) b;
		}
	}

	public static void read1024Bytes(InputStream in) throws IOException {
		int bytesRead = 0;
		int bytesToRead = 1024;
		byte[] input = new byte[bytesToRead];
		while (bytesRead < bytesToRead) {
			int result = in.read(input, bytesRead, bytesToRead - bytesRead);
			// end of stream
			if (result == -1) {
				break;
			}
			bytesRead += result;
		}
	}

	public static void readUsingAvailable(InputStream in) throws IOException {
		int bytesAvailable = in.available();

		if (bytesAvailable == 0) {
			// end of stream
		}

		byte[] input = new byte[bytesAvailable];
		int bytesRead = in.read(input, 0, bytesAvailable);
		System.out.println(bytesRead);
	}

	public static int signedToUnsignedByte(byte b) {
		return b >= 0 ? b : 256 + b;
	}
}
