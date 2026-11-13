package com.mukesh.apigateway.Configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterRegressionTest {
    @Test void authenticatedRequestGetsTrustedUserEmail() throws Exception {
        JwtService jwt = mock(JwtService.class);
        when(jwt.ValidateToken("valid")).thenReturn(true);
        when(jwt.ExtractEmailFromToken("valid")).thenReturn("customer@example.com");
        when(jwt.ExtractRole("valid")).thenReturn("CUSTOMER");
        var request = new MockHttpServletRequest("GET", "/cart/getcart");
        request.addHeader("Authorization", "Bearer valid");
        request.addHeader("useremail", "attacker@example.com");
        var response = new MockHttpServletResponse();
        var captured = new java.util.concurrent.atomic.AtomicReference<String>();
        new JwtFilter(jwt).doFilter(request, response, (req, resp) -> {
            captured.set(((HttpServletRequest) req).getHeader("useremail"));
        });
        assertEquals("customer@example.com", captured.get());
        assertEquals(200, response.getStatus());
    }

    @Test void expiredJwtReturnsUnauthorized() throws Exception {
        JwtService jwt = mock(JwtService.class);
        when(jwt.ValidateToken("expired")).thenReturn(false);
        var request = new MockHttpServletRequest("GET", "/cart/getcart");
        request.addHeader("Authorization", "Bearer expired");
        var response = new MockHttpServletResponse();
        new JwtFilter(jwt).doFilter(request, response, new MockFilterChain());
        assertEquals(401, response.getStatus());
    }
}
