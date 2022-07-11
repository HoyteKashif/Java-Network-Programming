package book.network_programming.ch2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Encoding {

	public static void main(String[] args) throws IOException {
		System.out.println(getMacCyrillicString(System.in));
	}

	public static String getMacCyrillicStringUsingBuffer(InputStream in) throws IOException {
		Reader r = new InputStreamReader(in, "MacCyrillic");
		r = new BufferedReader(r, 1024);
		StringBuilder sb = new StringBuilder();
		int c;

		while ((c = r.read()) != -1) {
			if (((char) c) == ' ') {
				break;
			}
			sb.append((char) c);
		}
		return sb.toString();

	}

	public static String getMacCyrillicString(InputStream in) throws IOException {
		InputStreamReader r = new InputStreamReader(in, "MacCyrillic");
		StringBuilder sb = new StringBuilder();
		int c;
		while ((c = r.read()) != -1) {

			if (((char) c) == ' ') {
				break;
			}

			sb.append((char) c);
		}
		return sb.toString();
	}
}
