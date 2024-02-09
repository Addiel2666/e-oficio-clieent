/**
 * 
 */
package com.ecm.sigap.security.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Alejandro Guzman
 *
 */
public class SacgAuthenticationProvider implements AuthenticationProvider, InitializingBean {

	/** Logger de la clase */
	private static final Logger log = LogManager.getLogger(SacgAuthenticationProvider.class);

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	/**  */
	SacgUserDetailServiceImpl sacgUserDetailsService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug("::: Ejecutando el afterPropertiesSet()");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.authentication.AuthenticationProvider#
	 * authenticate(org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		log.debug("::: Ejecutando el metodo authenticate(Authentication)");
		log.debug("::: Name " + authentication.getName());
		log.debug("::: Credentials " + authentication.getCredentials());
		log.debug("::: Principal " + authentication.getPrincipal());

		loadUserByAssertion(authentication);

		return authentication;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.authentication.AuthenticationProvider#supports
	 * (java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authentication) {

		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	/**
	 * @param sacgUserService the sacgUserService to set
	 */
	public void setSacgUserDetailsService(final SacgUserDetailServiceImpl sacgUserService) {

		this.sacgUserDetailsService = sacgUserService;
	}

	protected UserDetails loadUserByAssertion(final Authentication authentication) {

		return this.sacgUserDetailsService.loadUserDetails(authentication);
	}

}
