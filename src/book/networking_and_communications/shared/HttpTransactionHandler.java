package book.networking_and_communications.shared;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.net.URLConnection;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Example Appendix-1. A handler for http transactions. <br>
 * <br>
 * An HTTP server needs to deal with the following during a transaction: <br>
 * <ul>
 * <li>The client sends a request that includes the name of the file. The server
 * needs to read in this request and parse various attributes of the
 * request.</li>
 * <li>The server needs to return the requested file (if it exists), along with
 * type and length information. It needs to retuan an error if it can't find the
 * file.</li>
 * </ul>
 */
public class HttpTransactionHandler {

	// response statuses
	public final static int REPLY_ERROR_NO_ERROR = 200;
	public final static int REPLY_ERROR_NO_SUCH_FILE = 404;

	// no problem
	public final static String REPLY_EXPLANATION_OK = " OK ";
	// error of some sort
	public final static String REPLY_EXPLANATION_ERROR = " ERROR ";

	// debug
	public static boolean fDebugOn = true;

	// hooks connecting us to the client
	DataInputStream fClientInputStream;
	DataOutputStream fClientOutputStream;

	// info about the content
	DataInputStream fContentInputStream;
	long fContentLength = 0;
	String fContentPath;
	String fContentFilename;
	long fContentLastModified = 0;
	int fReplyErrorCode = REPLY_ERROR_NO_ERROR; // no error by default
	String fReplyErrorExplanation = REPLY_EXPLANATION_OK;

	/**
	 * Instantiate a new transaction handler.
	 * 
	 * @param clientInputStream  The stream where the client sends us its request
	 * @param clientOutputStream The stream where we return content to the client
	 */
	public HttpTransactionHandler(DataInputStream clientInputStream, DataOutputStream clientOutputStream) {
		setIOStreams(clientInputStream, clientOutputStream);
	}

	/**
	 * Set the input and output streams for the transaction.
	 * 
	 * @param clientInputStream  The stream where the client sends us its request
	 * @param clientOutputStream The stream where we return content to the client
	 */
	public void setIOStreams(DataInputStream clientInputStream, DataOutputStream clientOutputStream) {
		fClientInputStream = clientInputStream;
		fClientOutputStream = clientOutputStream;
	}

	/**
	 * Handle the current requested transaction.
	 * 
	 * @throws Exception
	 */
	public void handleTransaction() throws Exception {
		fContentPath = this.getRequestedFilename(fClientInputStream);

		try {
			if (fContentPath.startsWith("/")) {
				// prepend a "current directory" dot
				fContentPath = "." + fContentPath;
			}

			if (fContentPath.endsWith("/")) {
				// append the default doc name...
				fContentPath += "index.html";
			}

			// try to find this file... will throw if not found
			File contentFile = new File(fContentPath);

			// get the length of the file
			fContentLength = contentFile.length();

			// get the mod date of the file
			fContentLastModified = contentFile.lastModified();

			// strips directory stuff from the filename
			fContentFilename = contentFile.getName();

			fContentInputStream = new DataInputStream(new FileInputStream(fContentPath));
		} catch (Exception couldNotOpenFileEx) {
			if (fDebugOn) {
				System.err.println("threw opening file " + fContentPath + " (" + couldNotOpenFileEx + ")");
			}
			buildErrorReplyInfo();
		}

		if (fDebugOn) {
			System.out.println("starting reply...");
		}

		/**
		 * We build a StringBufferInputStream here because the more obvious
		 * implementation: fClientOutputStream.writeBytes(getReplyHeader());
		 * 
		 * ...is EXTREMELY slow. Basically, it sends a series of TCP packets out
		 * containing single-byte data. The DataOutputStream.writeChars method is so
		 * slow because it writes out data on the output stream a single byte at a time
		 * instead of sending the data in the big blocks that TCP loves.
		 * 
		 * Using our "block move" copySrcSink method, in combination with the
		 * StringBufferInputStream, improves the efficiency tremendously.
		 */
		StringBufferInputStream headerStream = new StringBufferInputStream(getReplyHeader());
		copySrcToSink((InputStream) headerStream, (OutputStream) fClientOutputStream);
		headerStream = null;

		if (fDebugOn) {
			System.out.println("done with reply header...");
		}

		// save the content file...
		copySrcToSink((InputStream) fContentInputStream, (OutputStream) fClientOutputStream);

		if (fDebugOn) {
			System.out.println("done copying content data...");
		}
	}

	/**
	 * Take a source data input stream and a sink data output stream copy input to
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

					if (fDebugOn) {
						System.out.println("bytesRead: " + bytesRead);
					}

					sink.write(tempBuf, 0, bytesRead);
					sink.flush();
				}
			} while (bytesRead >= 0);
		} catch (IOException ioEx) {
			System.err.println("copySrcToSink  failed with: " + ioEx);
		}
	}

	/**
	 * Given an HTTP 1.0 request on the srcStream, find the requested filename and
	 * return it as a string
	 * 
	 * @param srcStream
	 * @return
	 */
	protected String getRequestedFilename(DataInputStream srcStream) {
		boolean foundFilename = false;

		String retVal = "/"; // default document?

		try {
			// gets first line, should contain
			// "GET foo.html HTTP/1.0"
			String firstLineStr = srcStream.readLine();
			StringTokenizer tokSource = new StringTokenizer(firstLineStr);

			while (!foundFilename) {
				// should return a string for the next token
				String curTok = tokSource.nextToken();
				if (fDebugOn) {
					System.out.println("curTok: " + curTok);
				}

				if (curTok.equals("GET")) {
					curTok = tokSource.nextToken();
					retVal = curTok;
					foundFilename = true;
				}
			}
		} catch (Exception ex) {
			if (fDebugOn) {
				System.err.println("getRequestedFilename threw: " + ex);
			}
		}

		if (fDebugOn) {
			System.out.println("retVal: " + retVal);
		}
		return retVal;
	}

	/**
	 * Build the HTTP reply header This includes things like the MIME content type,
	 * length, mod date, and so on.
	 * 
	 * @return
	 */
	protected String getReplyHeader() {
		if (fDebugOn) {
			System.out.println("getting content type...");
		}

		String contentTypeStr = URLConnection.guessContentTypeFromName(fContentFilename);

		if (fDebugOn) {
			System.out.println("building header str...");
		}

		String headerStr = "";
		headerStr += "HTTP/1.0 " + fReplyErrorCode + fReplyErrorExplanation + "\n";
		headerStr += "Date: " + (new Date()).toGMTString() + "\n";
		headerStr += "Server: Todd's_Skanky_Web_Server/1.0\n";
		headerStr += "MIME-version: 1.0\n";
		headerStr += "Content-type: " + contentTypeStr + "\n";
		headerStr += "Last-modified: " + (new Date(fContentLastModified)).toGMTString() + "\n";
		headerStr += "Content-length: " + fContentLength;
		headerStr += "\n\n"; // this last sequence terminates the header

		if (fDebugOn) {
			System.out.println("reply header: " + headerStr);
		}

		return headerStr;
	}

	/**
	 * We couldn't find the content file the client requested, return a proper error
	 * message to the client.
	 */
	protected void buildErrorReplyInfo() {
		String errorContentStr = "Could not find the requested file: " + fContentPath + "\n";

		StringBufferInputStream localStream = new StringBufferInputStream(errorContentStr);

		// get the length of the content
		fContentLength = localStream.available();

		// get the modification date of the file (or at least fake it)
		fContentLastModified = (new Date()).getTime();

		fContentFilename = "error.html";

		fContentInputStream = new DataInputStream(localStream);

		fReplyErrorCode = REPLY_ERROR_NO_SUCH_FILE;
		fReplyErrorExplanation = REPLY_EXPLANATION_ERROR;
	}
}