package book.network_programming.ch5;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 * Example 5-7. is a program that uses URLEncoder.encode() to print various encoded strings.
 * 
 * x-www-form-urlencoded strings
 * 
 */
public class EncoderTest {
	public static void main(String[] args) {
		try {
			System.out.println(URLEncoder.encode("This string has spaces", "UTF-8"));
			System.out.println(URLEncoder.encode("This*string*has*asterisks", "UTF-8"));
			System.out.println(URLEncoder.encode("This%string%has%percent%signs", "UTF-8"));
			System.out.println(URLEncoder.encode("This+string+has+pluses", "UTF-8"));
			System.out.println(URLEncoder.encode("This/string/has/slases", "UTF-8"));
			System.out.println(URLEncoder.encode("This\"string\"has\"quote\"marks", "UTF-8"));
			System.out.println(URLEncoder.encode("This:string:has:colons", "UTF-8"));
			System.out.println(URLEncoder.encode("This~string~has~tildes", "UTF-8"));
			System.out.println(URLEncoder.encode("This(string)has(parentheses)", "UTF-8"));
			System.out.println(URLEncoder.encode("This.string.has.periods", "UTF-8"));
			System.out.println(URLEncoder.encode("This=string=has=equal=signs", "UTF-8"));
			System.out.println(URLEncoder.encode("This&string&has&ampersands", "UTF-8"));
			// e with accent Alt Code (Alt + 0233)
			System.out.println(URLEncoder.encode("Thiséhasénon-ASCII characters", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Broken VM does not support UTF-8");
		}
	}
}
