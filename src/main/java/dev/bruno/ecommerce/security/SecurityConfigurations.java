package dev.bruno.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"))
                )
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                                .requestMatchers(
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/health",
                                        "/error"
                                ).permitAll()

                                .requestMatchers(HttpMethod.POST, "/product/create-product").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/product/update-product/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/product/delete-product/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/coupon").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/coupon/**").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET, "/product").authenticated()
                                .requestMatchers(HttpMethod.GET, "/product/**").authenticated()

                                .requestMatchers(HttpMethod.POST, "/cart/create-cart").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/cart/edit-cart/{cartId}").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/cart/{cartId}/coupons").authenticated()
                                .requestMatchers(HttpMethod.GET, "/cart/me").authenticated()
                                .requestMatchers(HttpMethod.GET, "/cart/{cartId}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/cart/delete-cart/{cartId}").authenticated()

                                .requestMatchers(HttpMethod.POST, "/payment/charge").authenticated()
                                .requestMatchers(HttpMethod.GET, "/payment/**").authenticated()

                                .anyRequest().authenticated()

                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
