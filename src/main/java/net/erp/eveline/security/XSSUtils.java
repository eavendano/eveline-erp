package net.erp.eveline.security;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.owasp.esapi.ESAPI;

public class XSSUtils {

    public static String stripXSS(final String value) {
        if (value == null) {
            return null;
        }
        var encode = ESAPI.encoder()
                .canonicalize(value)
                .replaceAll("\0", "");
        return Jsoup.clean(encode, Whitelist.none());
    }
}
