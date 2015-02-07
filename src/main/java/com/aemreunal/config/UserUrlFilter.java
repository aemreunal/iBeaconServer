package com.aemreunal.config;

/*
 ***************************
 * Copyright (c) 2015      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * emre@aemreunal.com      *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

import java.io.IOException;
import java.nio.charset.Charset;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class UserUrlFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/human/") && !requestURI.startsWith("/human/register")) {
            String authUsername = getAuthUsername(request);
            String uriUsername = getUriUsername(requestURI);
            if (!authUsername.equals(uriUsername)) {
                throw new PreAuthenticatedCredentialsNotFoundException("Unauthorized access.");
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getAuthUsername(HttpServletRequest request) {
        String authorizationString = request.getHeader("Authorization");
        if (authorizationString == null) {
            throw new PreAuthenticatedCredentialsNotFoundException("Unauthorized access. Please provide preemptive HTTP Basic authorization credentials with every request.");
        }
        authorizationString = authorizationString.substring("Basic".length()).trim();
        String credentials = new String(Base64.decode(authorizationString.getBytes()), Charset.forName("UTF-8"));
        return (credentials.split(":",2))[0];
    }

    private String getUriUsername(String requestURI) {
        // +1 to include the slash after "/human", for "/human/<username>"
        requestURI = requestURI.substring(GlobalSettings.USER_PATH_MAPPING.length() + 1);
        int slashIndex = requestURI.indexOf('/');
        if (slashIndex == -1) {
            return requestURI;
        } else {
            return requestURI.substring(0, slashIndex);
        }
    }
}
