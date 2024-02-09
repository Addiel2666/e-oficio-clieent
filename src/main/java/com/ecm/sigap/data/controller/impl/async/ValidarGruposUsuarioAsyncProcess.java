/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl.async;

import java.util.List;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ecm.sigap.data.controller.impl.AccesoController;
import com.ecm.sigap.data.controller.impl.UsuarioController;
import com.ecm.sigap.data.model.Acceso;
import com.ecm.sigap.data.model.Usuario;

/**
 * Este proceso valida que el usuario indicado tiene los grupos de su area y de
 * las areas a las que tiene accesso.
 * 
 * @author alfredo morales
 * @version 1.0
 *
 */
@Component
public class ValidarGruposUsuarioAsyncProcess {

	/** Referencia hacia el controller {@link UsuarioController}. */
	@Autowired
	private UsuarioController usuarioController;

	/** Referencia hacia el controller {@link AccesoController}. */
	@Autowired
	private AccesoController accesoController;

	/** Log de suscesos. */
	private Logger log = LogManager.getLogger(ValidarGruposUsuarioAsyncProcess.class);

	/**
	 * Configuracion global de la acplicacion.
	 */
	@Autowired
	protected Environment environment;

	/**
	 * 
	 * @param user
	 */
	@Async
	public synchronized void process(Usuario user) {

		try {

			// - - -

			try {
				usuarioController.agregarGrupos(user);
			} catch (Exception e) {
				
			}

			// - - -

			ResponseEntity<List<Acceso>> areasConAccessoResponse//
					= usuarioController.getAreasUsuario(user.getIdUsuario(), user.getIdArea());

			if (areasConAccessoResponse.getStatusCode() == HttpStatus.OK) {

				List<Acceso> areasConAccesso = areasConAccessoResponse.getBody();

				for (Acceso acceso : areasConAccesso) {
					try {
						if (acceso.getAccesoKey().getArea().getIdArea() != user.getIdArea())
							accesoController.agregarGrupoUsuario(acceso);
					} catch (Exception e) {
						
					}
				}
			}

			// - - -

			log.debug("<<< VALIDACION COMPLETA! ");

		} catch (Exception e) {
			
		}

	}

}
