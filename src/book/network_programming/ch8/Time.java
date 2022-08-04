package book.network_programming.ch8;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;

/**
 * Example 8-3. A time protocol client. Specified in RFC 868 specifies that the
 * time be sent as the number of seconds since midnight, January 1, 1900,
 * Greenwich Mean Time. However, this is not sent as an ASCII string like
 * 2,524,521,600 or -1297728000. Rather it is sent as a 32-bit , unsigned,
 * big-endian binary number.
 */
public class Time {
//	private static final String HOSTNAME = "time.nist.gov";
	private static final String HOSTNAME = "localhost";

	public static void main(String[] args) throws IOException, ParseException {
		Date d = Time.getDateFromNetwork();
		System.out.println("It is " + d);
	}

	public static Date getDateFromNetwork() throws IOException, ParseException {
		// The time protocol sets the epock at 1900, the Java Date class at 1970. This
		// number converts between them.
		long differenceBetweenEpochs = 2208988800L;
		
		//If you'd rather not use the magic number, uncomment
		// the following section which calculates it directly.
		/*
		 * TimeZone gmt = TimeZone.getTimeZone("GMT");
		 * Calendar epoch1900 = Calendar.getInstance(gmt);
		 * epoch1900.set(1900,01,01,00,00,00);
		 * long epoch1900ms = epoch1900.getTime().getTime();
		 * Calendar epoch1970 = Calendar.getInstance(gmt);
		 * epoch1970.set(1970,01,01,00,00,00);
		 * long epoch1970ms = epoch1970.getTime().getTime();
		 * 
		 * long differenceInMs = epoch1970ms - epoch1900ms;
		 * long differenceBetweenEpochs = differenceInMs/1000;
		 */

		Socket socket = null;
		try {
			socket = new Socket(HOSTNAME, 37);
			socket.setSoTimeout(15000);

			InputStream raw = socket.getInputStream();

			long secondsSince1900 = 0;

			for (int i = 0; i < 4; i++) {
				secondsSince1900 = (secondsSince1900 << 8) | raw.read();
			}

			long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;
			long msSince1970 = secondsSince1970 * 1000;
			Date time = new Date(msSince1970);
			return time;
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException ex) {
			}
		}
	}
}
