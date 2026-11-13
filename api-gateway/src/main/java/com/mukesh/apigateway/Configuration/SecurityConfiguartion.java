package com.mukesh.apigateway.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguartion {
    private final JwtFilter filter;

    public SecurityConfiguartion(JwtFilter filter) {
        this.filter = filter;
    }

    @Bean
    public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
       return  http.csrf(csrf->csrf.disable())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
               .httpBasic(httpBasic->httpBasic.disable())
               .authorizeHttpRequests(auth->auth
                       .requestMatchers(HttpMethod.POST,"/product/addproduct").hasRole("SELLER")
                       .requestMatchers(HttpMethod.GET,"/product/getproducts").permitAll()
                       .requestMatchers(HttpMethod.GET,"/product/getproduct/{product_id}").permitAll()
                       .requestMatchers(HttpMethod.GET,"/product/findproduct/{product_id}").permitAll()
                       .requestMatchers(HttpMethod.POST,"/cart/addcart").hasRole("CUSTOMER")
                       .requestMatchers(HttpMethod.GET,"/cart/getcart").hasRole("CUSTOMER")
                       .requestMatchers(HttpMethod.GET,"/cart/getcart/{email}").denyAll()
                       .requestMatchers(HttpMethod.POST,"/order/placeorder").hasRole("CUSTOMER")
                       .requestMatchers(HttpMethod.GET,"/order/getorder/{order_id}").denyAll()
                       .requestMatchers(HttpMethod.GET,"/order/getorders").hasRole("CUSTOMER")
                       .requestMatchers(HttpMethod.POST,"/user/register").permitAll()
                       .requestMatchers(HttpMethod.POST,"/user/login").permitAll()
                       .requestMatchers(HttpMethod.POST,"/user/forgotpassword").permitAll()
                       .requestMatchers(HttpMethod.POST,"/user/resetpassword").permitAll()
                       .requestMatchers(HttpMethod.POST,"/product/Inventoryupdate").denyAll()
                       .requestMatchers(HttpMethod.POST,"/order/updateInventory/{orderId}").denyAll()
                       .requestMatchers(HttpMethod.POST,"/order/updatepayment/{orderId}").denyAll()
                       .requestMatchers(HttpMethod.DELETE,"/cart/clear-cart/{email}").denyAll()
                       .requestMatchers(HttpMethod.POST,"/payments/createorder").denyAll()
                       .requestMatchers(HttpMethod.POST,"/payments/verify").hasRole("CUSTOMER")
                       .requestMatchers(HttpMethod.GET,"/cart/productIds").denyAll()
                       .requestMatchers(HttpMethod.POST, "/send-notification")
                       .denyAll()
                       .anyRequest().authenticated()).build();
    }
}
