package com.payflow.gateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /*
         * Get the Authorization header.
         *
         * Expected format:
         *
         * Authorization: Bearer <JWT_TOKEN>
         */
        final String authorizationHeader =
                request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);

        /*
         * If there is no Authorization header,
         * continue with the next filter.
         *
         * This is important because public APIs such as
         * login and registration do not have a JWT.
         */
        if (authorizationHeader == null
                || !authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {

            filterChain.doFilter(request, response);
            return;
        }

        /*
         * Remove "Bearer " from the header.
         */
        final String jwt = authorizationHeader.substring(
                SecurityConstants.TOKEN_PREFIX.length()
        );

        try {

            /*
             * Extract the email/username from the JWT.
             */
            final String username = jwtService.extractUsername(jwt);

            /*
             * Only authenticate if:
             *
             * 1. Username exists
             * 2. No authentication already exists
             */
            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                /*
                 * Load the user from the database.
                 */
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                /*
                 * Validate the JWT.
                 */
                if (jwtService.isTokenValid(jwt, username)) {

                    /*
                     * Create an Authentication object.
                     *
                     * Spring Security uses this object to know
                     * who the current user is.
                     */
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    /*
                     * Attach request information to the authentication.
                     */
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    /*
                     * Tell Spring Security that the user
                     * has been authenticated.
                     */
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }

        } catch (Exception exception) {

            /*
             * If the JWT is invalid, expired, malformed,
             * or the user does not exist, we simply don't
             * authenticate the request.
             *
             * SecurityConfig will decide whether the endpoint
             * requires authentication.
             */
            SecurityContextHolder.clearContext();
        }

        /*
         * Continue the filter chain.
         */
        filterChain.doFilter(request, response);
    }
}