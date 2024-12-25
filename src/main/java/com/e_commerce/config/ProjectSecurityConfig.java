package com.e_commerce.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.e_commerce.filter.CsrfCookieFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class ProjectSecurityConfig {
	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
		CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
		http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
					@Override
					public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
						CorsConfiguration config = new CorsConfiguration();
						config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
						config.setAllowedMethods(Collections.singletonList("*"));
						config.setAllowCredentials(true);
						config.setAllowedHeaders(Collections.singletonList("*"));
						config.setExposedHeaders(Arrays.asList("Authorization"));
						config.setMaxAge(3600L);
						return config;
					}
				}))
//				.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//						.ignoringRequestMatchers("/contact", "/register","/api/cart/**")
//						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//				.addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//				.requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Only HTTP
				
				.csrf(csrf->csrf.disable())
				.authorizeHttpRequests((requests) -> requests.requestMatchers("/api/users/allUsers").hasRole("ADMIN").
						requestMatchers("/api/users/**").hasRole("USER")
						.requestMatchers("/api/products/**").hasAnyRole("USER", "ADMIN")
//						.requestMatchers("/api/cart/**").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/myLoans")
						.authenticated()
						.requestMatchers("/keycloak/**","/api/cart/**","/webhook/**").permitAll());
		http.oauth2ResourceServer(
				rsc -> rsc.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
		/*
		 * http.oauth2ResourceServer(rsc -> rsc.opaqueToken(otc ->
		 * otc.authenticationConverter(new KeycloakOpaqueRoleConverter())
		 * .introspectionUri(this.introspectionUri).introspectionClientCredentials(this.
		 * clientId,this.clientSecret)));
		 */
//		http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
		return http.build();
	}

}
