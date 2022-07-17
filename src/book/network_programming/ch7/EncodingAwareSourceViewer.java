package book.network_programming.ch7;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * Example 7-2. Download a web page with the correct character set
 */
public class EncodingAwareSourceViewer {
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				// set default encoding
				String encoding = "ISO-8859-1";
				URL u = new URL(args[i]);
				URLConnection uc = u.openConnection();
				String contentType = uc.getContentType();
				int encodingStart = contentType.indexOf("charset=");
				if (encodingStart != -1) {
					encoding = contentType.substring(encodingStart + 8);
				}
				InputStream in = new BufferedInputStream(uc.getInputStream());
				Reader r = new InputStreamReader(in, encoding);
				int c;
				while ((c = r.read()) != -1) {
					System.out.print((char) c);
				}
				r.close();
			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not a parseable URL");
			} catch (UnsupportedEncodingException e) {
				System.err.println("Server sent an encoding Java does not support: " + e.getMessage());
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
