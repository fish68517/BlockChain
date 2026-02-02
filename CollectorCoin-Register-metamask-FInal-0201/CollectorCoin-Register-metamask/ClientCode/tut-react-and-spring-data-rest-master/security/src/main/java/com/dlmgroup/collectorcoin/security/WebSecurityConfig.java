package com.dlmgroup.collectorcoin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

import com.dlmgroup.collectorcoin.security.jwt.AuthEntryPointJwt;
import com.dlmgroup.collectorcoin.security.jwt.AuthTokenFilter;
import com.dlmgroup.collectorcoin.security.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
      http.cors();
      http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
	    	.authorizeRequests()
          .antMatchers("/built/**", "/main.css", "/css/**", "/js/**", "/assets/**").permitAll() // resources
          .antMatchers("/api/auth/**").permitAll()
          .antMatchers("/api/test/**").permitAll()
          .antMatchers("/collectorcoin/**").permitAll() // socket connections
          .antMatchers( // react pages
            "/",
            "/public/**",
            "/login",
            "/register",
            "/logout",
            "/myportfolio",
            "/myportfolio/*",
            "/newlisting",
		        "/web3-test",
            "/newbid",
			      "/selectfrombids",
			      "/admin",
            "/restorerportfolio",
            "/listings/*",
            "/investedPortfolio",
            "/auction"
          ).permitAll()
          .anyRequest().authenticated()
          .and()
          .logout(logout -> logout // expose logout to react
            .logoutUrl("/api/auth/signout")
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true)
          );
          // .and()
	      	// .formLogin()
          // .loginPage("/index.html")
          // .defaultSuccessUrl("/", true)
          // .permitAll()
          // .and()
			//.httpBasic()
			// .logout()
			// 	.logoutSuccessUrl("/login");

	    /* ORIGINAL SAMPLE - BEGIN */
	    http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	    /* ORIGINAL SAMPLE - END */
  	}
}
