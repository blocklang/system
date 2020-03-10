package com.blocklang.system.config.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.blocklang.system.constant.WebSite;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf()
				.disable() // 因为使用 JWT，所以禁用 csrf
				.cors() // 允许跨域访问
				.and()
			// 认证过程中发生异常时的处理类
			.exceptionHandling()
				// 如果出现异常，则跳转到首页（单页面应用只有一个页面，即首页）
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(WebSite.HOME_URL))
				.and()
			.sessionManagement() // 因为使用token，所以禁用 Session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
			.authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				// 允许访问所有静态资源
				.antMatchers(HttpMethod.GET, "/**/*.js", "/**/*.css", "/**/*.map").permitAll()
				.antMatchers(HttpMethod.GET, WebSite.HOME_URL, "/login","/register").permitAll()
				.antMatchers(HttpMethod.POST, "/users", "/users/login").permitAll()
				.anyRequest().authenticated();

		http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
		// setAllowCredentials(true) is important, otherwise:
		// The value of the 'Access-Control-Allow-Origin' header in the response must
		// not be the wildcard '*' when the request's credentials mode is 'include'.
		configuration.setAllowCredentials(true);
		// setAllowedHeaders is important! Without it, OPTIONS preflight request
		// will fail with 403 Invalid CORS request
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}