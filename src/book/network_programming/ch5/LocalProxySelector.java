package book.network_programming.ch5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/*
 * Example 5.9 A ProxySelector that remembers what it can connect to.
 */
public class LocalProxySelector extends ProxySelector {
	private List<URI> failed = new ArrayList<>();

	@Override
	public List<Proxy> select(URI uri) {

		List<Proxy> result = new ArrayList<>();

		if (failed.contains(uri) || !("http".equalsIgnoreCase(uri.getScheme()))) {
			result.add(Proxy.NO_PROXY);
		} else {
			SocketAddress proxyAddress = new InetSocketAddress("proxy.example.com", 8000);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
			result.add(proxy);
		}

		return result;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		failed.add(uri);
	}

	public static void main(String[] args) {
		// Each Virtual Machine has exactly one ProxySelector. To Change the
		// ProxySelector, pass the new selector to the static ProxySelector.setDefault()
		// method.
		ProxySelector selector = new LocalProxySelector();
		ProxySelector.setDefault(selector);
	}

}
