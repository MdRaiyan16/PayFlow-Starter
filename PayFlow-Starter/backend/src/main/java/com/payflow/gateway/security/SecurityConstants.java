package com.payflow.gateway.security;

/**
 * Application-wide security constants.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    /**
     * JWT Header
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * JWT Prefix
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT Claim Names
     */
    public static final String ROLE_CLAIM = "role";

    /**
     * API Endpoints
     */
    public static final String AUTH_BASE_URL = "/api/v1/auth";

    public static final String LOGIN_URL = AUTH_BASE_URL + "/login";

    public static final String REGISTER_URL = AUTH_BASE_URL + "/register";

    public static final String REFRESH_TOKEN_URL = AUTH_BASE_URL + "/refresh-token";

    /**
     * Swagger URLs
     */
    public static final String[] PUBLIC_URLS = {
            LOGIN_URL,
            REGISTER_URL,
            REFRESH_TOKEN_URL,

            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",

            "/actuator/health"
    };

}