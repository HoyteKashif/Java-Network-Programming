package book.network_programming.ch7;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SourceViewer3 {
	public static void main(String[] args) {

		args = new String[1];
		args[0] = "https://www.oreilly.com";

		for (int i = 0; i < args.length; i++) {
			try {
				// Open the URLConnection for reading
				URL u = new URL(args[i]);
				HttpURLConnection uc = (HttpURLConnection) u.openConnection();
				int code = uc.getResponseCode();
				String response = uc.getResponseMessage();
				// first HTTP request line, version included
				// System.out.println(uc.getHeaderField(0));
				System.out.println("HTTP/1.x " + code + " " + response);
				for (int j = 1;; j++) {
					String header = uc.getHeaderField(j);
					String key = uc.getHeaderFieldKey(j);
					if (header == null || key == null) {
						break;
					}
					System.out.println(uc.getHeaderFieldKey(j) + ": " + header);
				}
				System.out.println();

				try (InputStream in = new BufferedInputStream(uc.getInputStream())) {
					// chain the InputStream to a Reader
					Reader r = new InputStreamReader(in);
					int c;
					while ((c = r.read()) != -1) {
						System.out.print((char) c);
					}
				}
			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not a parsable URL");
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
