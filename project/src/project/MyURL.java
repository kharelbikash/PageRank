package project;

import java.net.URI;
import java.net.URISyntaxException;

public class MyURL {

	public static String parseURL(String url) {
		String res = null;
		try {
			//String scheme = url.indexOf(':') != -1 ? url.substring(0, url.indexOf(':')) : "http";
			URI u = new URI(encode(url.trim()));
			String scheme = u.getScheme() != null ? u.getScheme() : "http";
			if (!u.isOpaque() && (scheme.equals("http") || scheme.equals("https"))) {
				String auth = u.getAuthority();
				String path = u.getPath();
				if (auth == null) {
					int i = path.indexOf('/');
					auth = path.substring(0, i == -1 ? path.length() : i);
					path = i == -1 ? null : "/" + path.substring(i+1);
				}
				//System.out.println("scheme=" + scheme + " auth=" + auth + " path=" + path);
				res = new URI(scheme, auth, path, null, null).toASCIIString();
			}
		}
		catch (URISyntaxException e) {
			System.out.println(url);
		}
		return res;
	}
	
	private static String encode(String input) {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isUnsafe(ch)) {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            } else {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch) {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch) {
        if (ch > 128 || ch < 0)
            return true;
        return " %$&+,;=?@<>%[]{}\"()".indexOf(ch) >= 0;
    }

	
	/**
	 * @param args
	 */
	
}