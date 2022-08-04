package book.network_programming.ch7;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Example 7-17. Download a web page with a URLConnection
 */
public class SourceViewer4 {
	public static void main(String[] args) {
		try {
			URL u = new URL(args[0]);
			HttpURLConnection uc = (HttpURLConnection) u.openConnection();
			try (InputStream raw = uc.getInputStream()) {
				printFromStream(raw);
			} catch (IOException e) {
				printFromStream(uc.getErrorStream());
			}
		} catch (MalformedURLException e) {
			System.err.println(args[0] + " is not a parsable URL");
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private static void printFromStream(InputStream raw) throws IOException {
		try (InputStream buffer = new BufferedInputStream(raw)) {
			Reader reader = new InputStreamReader(buffer);
			int c;
			while ((c = reader.read()) != -1) {
				System.out.print((char) c);
			}
		}
	}
}
