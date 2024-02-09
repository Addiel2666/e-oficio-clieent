/**
 * 
 */
package com.ecm.sigap.security.auth;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Alejandro Guzman
 *
 */
public class SacgAuthenticationToken extends AbstractAuthenticationToken
		implements Serializable {

	private static final long serialVersionUID = -4598887743740907446L;
	private final Object credentials;
	private final Object principal;
	private final UserDetails userDetails;

	/**
	 * 
	 * @param key
	 * @param principal
	 * @param credentials
	 * @param authorities
	 * @param userDetails
	 */
	public SacgAuthenticationToken( final Object principal,
			final Object credentials,
			final Collection<? extends GrantedAuthority> authorities,
			final UserDetails userDetails) {

		super(authorities);

		if ((principal == null)
				|| "".equals(principal) || (credentials == null)
				|| "".equals(credentials) || (authorities == null)
				|| (userDetails == null)) {
			throw new IllegalArgumentException(
					"Cannot pass null or empty values to constructor");
		}

		this.principal = principal;
		this.credentials = credentials;
		this.userDetails = userDetails;
		setAuthenticated(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.Authentication#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.Authentication#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	/**
	 * 
	 * @return
	 */
	public UserDetails getUserDetails() {
		return userDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" - SacgAuthenticationToken [credentials=" + credentials);
		sb.append(", principal=" + principal + ", userDetails=" + userDetails
				+ "]");

		return (sb.toString());
	}

}
