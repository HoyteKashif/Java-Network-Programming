package book.network_programming.ch5;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Example 5-3. Download an object
 */
public class ContentGetter {
	public static void main(String[] args) {

		args = new String[1];
//		args[0] = "https://www.oreilly.com";
//		args[1] = "https://www.oreilly.com/graphics_new/animation.gif"; // does not exists
//		args[0] = "http://www.cafeaulait.org/RelativeURLTest.class";
		args[0] = "http://www.cafeaulait.org/course/week9/spacemusic.au";

		if (args.length > 0) {
			// Open the URL for reading
			try {
				URL u = new URL(args[0]);
				Object o = u.getContent();
				System.out.println("I got a " + o.getClass().getName());
			} catch (MalformedURLException e) {
				System.err.println(args[0] + " is not a parseable URL");
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
