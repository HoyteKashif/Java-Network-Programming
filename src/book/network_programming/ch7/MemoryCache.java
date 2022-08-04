package book.network_programming.ch7;

import java.io.IOException;
import java.net.CacheRequest;
import java.net.CacheResponse;
import java.net.ResponseCache;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Example 7-11. An in-memory ResponseCache. 
 * This class is suitable for a single-user,
 * private cache (because it ignores the private and
 * public attributes of Cache-control).
 * 
 * Java Only allows one URL cache at a time. 
 * To install or change the cache, use the static 
 * ResponseCache.setDefault() and
 * ResponseCache.getDefault() methods.
 * 
 * ResponseCache.setDefault(new MemoryCache()); 
 */
public class MemoryCache extends ResponseCache {

	private final Map<URI, SimpleCacheResponse> responses = new ConcurrentHashMap<URI, SimpleCacheResponse>();
	private final int maxEntries;

	public MemoryCache() {
		this(100);
	}

	public MemoryCache(int maxEntries) {
		this.maxEntries = maxEntries;
	}

	@Override
	public CacheResponse get(URI uri, String rqstMethod, Map<String, List<String>> rqstHeaders) throws IOException {

		if ("GET".equals(rqstMethod)) {
			SimpleCacheResponse response = responses.get(uri);
			// check expiration date
			if (response != null && response.isExpired()) {
				responses.remove(uri); // wrong in the code example
				response = null;
			}
			return response;
		} else {
			return null;
		}
	}

	@Override
	public CacheRequest put(URI uri, URLConnection conn) throws IOException {

		if (responses.size() >= maxEntries) {
			return null;
		}

		CacheControl control = new CacheControl(conn.getHeaderField("Cache-Control"));
		if (control.noStore()) {
			return null;
		} else if (!conn.getHeaderField(0).startsWith("GET ")) {
			// only cache GET
			return null;
		}

		SimpleCacheRequest request = new SimpleCacheRequest();
		SimpleCacheResponse response = new SimpleCacheResponse(request, conn, control);

		responses.put(uri, response);

		return request;
	}

}
