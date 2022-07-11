package book.network_programming.ch2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreams {
	public static void main(String[] args) throws IOException {
		generateCharacters(System.out);
	}

	/*
	 * Write a single byte at a time p.27-28
	 */
	public static void generateCharacters(OutputStream out) throws IOException {
		int firstPrintableCharacter = 33;
		int numberOfPrintableCharacters = 94;
		int numberOfCharactersPerLine = 72;

		int start = firstPrintableCharacter;

		while (true) /* infinite loop */
		{
			for (int i = start; i < start + numberOfCharactersPerLine; i++) {
				out.write(((i - firstPrintableCharacter) % numberOfPrintableCharacters) + firstPrintableCharacter);
			}
			out.write('\r'); // carriage-return
			out.write('\n'); // linefeed
			start = ((start + 1) - firstPrintableCharacter) % numberOfPrintableCharacters + firstPrintableCharacter;
		}
	}

	/*
	 * Write a single byte Array at a time p.28
	 */
	public static void _2_generateCharacters(OutputStream out) throws IOException {
		int firstPrintableCharacter = 33;
		int numberOfPrintableCharacters = 94;
		int numberOfCharactersPerLine = 72;
		int start = firstPrintableCharacter;
		byte[] line = new byte[numberOfCharactersPerLine + 2]; // the +2 is for the carriage-return and linefeed

		while (true) /* infinite loop */
		{
			for (int i = start; i < start + numberOfCharactersPerLine; i++) {
				line[i - start] = (byte) ((i - firstPrintableCharacter) % numberOfPrintableCharacters
						+ firstPrintableCharacter);
			}
			line[72] = (byte) '\r'; // carriage-return
			line[73] = (byte) '\n'; // linefeed
			out.write(line);
			start = ((start + 1) - firstPrintableCharacter) % numberOfPrintableCharacters + firstPrintableCharacter;
		}
	}

	/*
	 * Common pattern for any object that needs to be cleaned up before it is
	 * garbage collected
	 */
	public static void disposePattern() {

		/*
		 * Jave 1.6 and earlier
		 */
		OutputStream out = null;
		try {
			out = new FileOutputStream("/tmp/data.txt");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// log or ignore
				}
			}
		}

		/*
		 * Java 7 and later
		 */
		try (OutputStream _out = new FileOutputStream("/tmp/data.txt")) {
			// work with the output stream
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
