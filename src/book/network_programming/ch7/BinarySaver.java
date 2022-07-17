package book.network_programming.ch7;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * Example 7-3. Downloading a binary file from a website and saving it to disk
 */
public class BinarySaver {
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i++) {
			try {
				URL root = new URL(args[i]);
				saveBinaryFile(root);
			} catch (MalformedURLException e) {
				System.err.println(args[i] + " is not URL I understand.");
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public static void saveBinaryFile(URL u) throws IOException {
		URLConnection uc = u.openConnection();
		String contentType = uc.getContentType();
		int contentLength = uc.getContentLength();
		if (contentType.startsWith("text/") || contentLength == -1) {
			throw new IOException("This is not a binary file.");
		}

		try (InputStream raw = uc.getInputStream()) {
			InputStream in = new BufferedInputStream(raw);
			byte[] data = new byte[contentLength];
			int offset = 0;
			while (offset < contentLength) {
				int bytesRead = in.read(data, offset, data.length - offset);
				if (bytesRead == -1) {
					break;
				}
				offset += bytesRead;
			}

			if (offset != contentLength) {
				throw new IOException("Only read " + offset + " bytes; Expected " + contentLength + " bytes");
			}

			String filename = u.getFile();
			filename = filename.substring(filename.lastIndexOf('/') + 1);
			try (FileOutputStream fout = new FileOutputStream(filename)) {
				fout.write(data);
				fout.flush();
			}
		}
	}
}
