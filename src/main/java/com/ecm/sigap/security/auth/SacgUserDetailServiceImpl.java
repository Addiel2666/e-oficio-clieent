/**
 * 
 */
package com.ecm.sigap.security.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.service.EntityManager;

/**
 * @author aguzman
 *
 */
public class SacgUserDetailServiceImpl implements UserDetailsService, AuthenticationUserDetailsService<Authentication> {

	/** Manejador para el tipo {@link com.ecm.sigap.data.model.Usuario} */
	@Autowired
	@Qualifier("usuarioService")
	protected EntityManager<Usuario> mngrUsuario;

	/** */
	private static final Logger log = LogManager.getLogger(SacgUserDetailServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

		if (log.isDebugEnabled()) {

			log.debug("::: Cargando la informacion completa del usuario " + userId);
		}

		Usuario usuario = mngrUsuario.fetch(userId);

		if (null == usuario) {
			log.error("::: El usuario " + userId + " no esta registrado en la BD");
			throw new UsernameNotFoundException("Usuario '" + userId + "' no encontrado");

		}

		// TODO Cambiar esto para que se obtengas los datos de Rol del usuario
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return new User(userId, "", authorities);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.
	 * AuthenticationUserDetailsService
	 * #loadUserDetails(org.springframework.security.core.Authentication)
	 */
	@Override
	public UserDetails loadUserDetails(Authentication token) throws UsernameNotFoundException {

		if (log.isDebugEnabled()) {
			log.debug("::: Iniciando el metodo loadUserByUsername(Authentication)");
		}

		return loadUserByUsername(token.getName());

	}
}
