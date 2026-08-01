package com.mukesh.apigateway.Configuration;

import io.jsonwebtoken.Header;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authheader=request.getHeader("Authorization");
        String token=null;
        if(authheader!=null && authheader.startsWith("Bearer ")){
            //get  the token from the header
            token=authheader.substring(7);
        }
        if(token !=null && jwtService.ValidateToken(token)) {
                String email = jwtService.ExtractEmailFromToken(token);
                String role = jwtService.ExtractRole(token);

                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_"+role));
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                if(request.getRequestURI().equals("/cart/addcart")) {
                    System.out.println("url "+request.getRequestURI());
                    Enumeration<String> headerNames = request.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        System.out.println(headerNames.nextElement());
                    }
                    CustomServletRequestWrapper wrapper = new CustomServletRequestWrapper(request);
                    System.out.println(email);
                    System.out.println(role);
                    wrapper.addHeader("useremail",email);
                    wrapper.addHeader("role",role);
                    System.out.println("========================");
                    System.out.println("url"+request.getRequestURI());
                    Enumeration<String> afterheaders=wrapper.getHeaderNames();
                    while (afterheaders.hasMoreElements()) {
                        System.out.println(afterheaders.nextElement());
                    }
                    filterChain.doFilter(wrapper,response);
                    return;
                }

                if(request.getRequestURI().equals("/cart/getcart")) {
                    CustomServletRequestWrapper wrapper = new CustomServletRequestWrapper(request);
                    wrapper.addHeader("useremail",email);
                    wrapper.addHeader("role",role);
                    filterChain.doFilter(wrapper,response);
                    return;
                }

                if(request.getRequestURI().equals("/order/placeorder")) {
                    CustomServletRequestWrapper wrapper = new CustomServletRequestWrapper(request);
                    Enumeration<String> headerNames = wrapper.getHeaderNames();
                    while (headerNames.hasMoreElements()) {
                        System.out.println(headerNames.nextElement());
                    }
                    wrapper.addHeader("useremail",email);
                    wrapper.addHeader("role",role);
                    filterChain.doFilter(wrapper,response);
                    return;
                }

                if(request.getRequestURI().equals("/order/getorders")) {
                    CustomServletRequestWrapper wrapper = new CustomServletRequestWrapper(request);
                    wrapper.addHeader("useremail",email);
                    wrapper.addHeader("role",role);
                    filterChain.doFilter(wrapper,response);
                    return;
                }
        }
        filterChain.doFilter(request,response);
    }
}
