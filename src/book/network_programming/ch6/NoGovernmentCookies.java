package book.network_programming.ch6;

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

/*
 * Example 6-1. A cookie policy that blocks all .gov cookies but allows others
 */
public class NoGovernmentCookies implements CookiePolicy {

	@Override
	public boolean shouldAccept(URI uri, HttpCookie cookie) {
		if (uri.getAuthority().toLowerCase().endsWith(".gov") || cookie.getDomain().toLowerCase().endsWith(".gov")) {
			return false;
		}
		return true;
	}

}
