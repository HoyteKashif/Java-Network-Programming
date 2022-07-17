package book.network_programming.ch5;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Example Page 134-135, use of getContent(Class[] classes)
 */
public class SourceViewer2 {
	public static void main(String[] args) {
		try {

			URL u = new URL("https://www.nwu.org");
			Class<?>[] types = new Class[3];
			types[0] = String.class;
			types[1] = Reader.class;
			types[2] = InputStream.class;
			Object o = u.getContent(types);

			if (o instanceof String) {
				System.out.println(o);
			} else if (o instanceof Reader) {
				int c;
				Reader r = (Reader) o;
				while ((c = r.read()) != -1) {
					System.out.print((char) c);
				}
				r.close();
			} else if (o instanceof InputStream) {
				int c;
				InputStream in = (InputStream) o;
				while ((c = in.read()) != -1) {
					System.out.write(c);
				}
				in.close();
			} else {
				System.out.println("Error: unexpected type" + o.getClass());
			}

		} catch (MalformedURLException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
