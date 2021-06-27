package net.erp.eveline.security;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Component
@Order(HIGHEST_PRECEDENCE)
public class XSSFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(XSSFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        var servlet = (HttpServletRequest) request;
        if (!servlet.getServletPath().matches("(.*)/sba/(.*)")) {
            var wrappedRequest = new XSSRequestWrapper(servlet);
            var body = IOUtils.toString(wrappedRequest.getReader());
            if (!StringUtils.isBlank(body)) {
                body = XSSUtils.stripXSS(body);
                wrappedRequest.resetInputStream(replaceTildeChars(body).getBytes(StandardCharsets.UTF_8));
            }
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(servlet, response);
        }
    }

    private String replaceTildeChars(String body) {
        body = body.replace( "Ã¡", "á" );
        body = body.replace( "Ã©", "é" );
        body = body.replace( "Ã­", "í" );
        body = body.replace( "Ã³", "ó" );
        body = body.replace( "Ãº", "ú" );

        // caracteres tildes mayusculas
        body = body.replace( "Ã", "Á" );
        body = body.replace( "Ã‰", "É" );
        body = body.replace( "Ã", "Í" );
        body = body.replace( "Ã“", "Ó" );
        body = body.replace( "Ãš", "Ú" );


        // caracteres tildes inversas minusculas
        body = body.replace( "Ã ", "a" );
        body = body.replace( "Ã¨", "e" );
        body = body.replace( "Ã¬", "i" );
        body = body.replace( "Ã²", "o" );
        body = body.replace( "Ã¹", "u" );

        // caracteres tildes inversas mayusculas
        body = body.replace( "Ã€", "A" );
        body = body.replace( "Ãˆ", "E" );
        body = body.replace( "ÃŒ", "I" );
        body = body.replace( "Ã’", "O" );
        body = body.replace( "Ã™", "U" );

        // caracteres ñ minuscula y mayuscula
        body = body.replace( "Ã‘", "ñ" );
        body = body.replace( "Ã±", "Ñ" );

        return body;
    }
}
