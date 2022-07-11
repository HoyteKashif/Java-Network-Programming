package book.networking_and_communications.ch2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Simple class for demonstrating the use of FileInputStream and
 * FileOutputStream
 *
 */
public class FileStreamCopy {

	public static void main(String[] args) {
		String inputFileName = "streamCopyInput.dat";
		String outputFileName = "streamCopyOutput.dat";
		if (args.length == 2) {
			inputFileName = args[0];
			outputFileName = args[1];
		}

		try {
			FileInputStream inStream = new FileInputStream(inputFileName);
			FileOutputStream outStream = new FileOutputStream(outputFileName);
			copySrcToSink((InputStream) inStream, (OutputStream) outStream);
		} catch (IOException ioEx) {
			System.err.println("copy threw: " + ioEx);
		}
	}

	/**
	 * Tak a source data input stream and a sink data output stream copy input to
	 * output
	 * 
	 * @param src
	 * @param sink
	 */
	public static void copySrcToSink(InputStream src, OutputStream sink) {
		byte[] tempBuf = new byte[1024];
		int bytesRead = 1;

		try {
			do {
				bytesRead = src.read(tempBuf, 0, 1024);

				if (bytesRead > 0) {
					System.out.println("bytesRead: " + bytesRead);
					sink.write(tempBuf, 0, bytesRead);
					sink.flush();
				}
			} while (bytesRead >= 0);
		} catch (IOException ioEx) {
			System.err.println("copySrcToSink failed with: " + ioEx);
		}
	}
}
