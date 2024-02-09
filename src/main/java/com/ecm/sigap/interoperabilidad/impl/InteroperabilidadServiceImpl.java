/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.interoperabilidad.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Component;

import com.ecm.sigap.interoperabilidad.InteroperabilidadService;

import mx.com.ecmsolutions.sigap.interoperabilidad.MensajeNoEnviado_Exception;
import mx.com.ecmsolutions.sigap.interoperabilidad.SigapInteroperabilidadWSName;
import mx.com.ecmsolutions.sigap.interoperabilidad.SigapInteroperabilidadWSServiceName;

/**
 * Implementacion del servicio de interoperabilidad.
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Component("interoperabilidadService")
public final class InteroperabilidadServiceImpl implements InteroperabilidadService {

	/**
	 * 
	 */
	public InteroperabilidadServiceImpl() {
		super();
	}

	/** Archivo de configuracion del servicio de interoperabilidad ws2. */
	private static ResourceBundle configInterop = ResourceBundle.getBundle("interoperabilidad");

	/**
	 * 
	 * Obtiene el enpoint de comunicacion al webservice de interoperabilidad.
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	private SigapInteroperabilidadWSName getEnpoint() throws MalformedURLException {

		String url = configInterop.getString("urlServiceWSDL");

		URL urlSigap = new URL(null, url);

		SigapInteroperabilidadWSServiceName wsClient = new SigapInteroperabilidadWSServiceName(urlSigap);

		SigapInteroperabilidadWSName wsInstance = wsClient.getSigapInteroperabilidadWSPort();

		return wsInstance;

	}

	public static void main(String[] args) throws MalformedURLException, MensajeNoEnviado_Exception {
		InteroperabilidadServiceImpl service = new InteroperabilidadServiceImpl();

		// service.getEnpoint().registrarInstancia(); //MENSAJE VERIFICADO
		// service.getEnpoint().sincronizarDirectorioCompleto(); //
		service.getEnpoint().sincronizarDirectorioParcial();
		List<String> areasDest = new ArrayList<>();
		areasDest.add("646");
		// String mensajeOficio = service.getEnpoint().generarOficioElectronico("1845",
		// "SOLICITUD", null, areasDest);
		// service.getEnpoint().registrarOficioElectronico(1845, mensajeOficio,
		// "SOLICITUD", areasDest, null);

		// System.out.println(mensajeOficio);
		// System.out.println("FIN...");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * generarDocumentoElectronico()
	 */
	@Override
	public String generarDocumentoElectronico(String asunto, String tipoSolicitud, List<String> areasDestino,
			List<String> areasCopia) throws MalformedURLException, MensajeNoEnviado_Exception {
		String mensajeOficio = getEnpoint().generarOficioElectronico(asunto, tipoSolicitud.toUpperCase(), areasDestino,
				areasCopia);
		return mensajeOficio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * registrarInstancia()
	 */
	@Override
	public void registrarInstancia() throws MalformedURLException, MensajeNoEnviado_Exception {
		getEnpoint().registrarInstancia();
		;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * registrarOficioElectronico()
	 */
	@Override
	public void registrarOficioElectronico(String asunto, String oficioElectronico, String tipoSolicitud,
			List<String> areasDestino, List<String> areasCopia)
			throws MalformedURLException, MensajeNoEnviado_Exception {

		getEnpoint().registrarOficioElectronico(asunto, oficioElectronico, tipoSolicitud, areasDestino, areasCopia);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * respuestaSuscripcionInstancias()
	 */
	@Override
	public void respuestaSuscripcionInstancias(List<String> institucionesDestino, boolean respuesta)
			throws MalformedURLException, MensajeNoEnviado_Exception {

		getEnpoint().respuestaSuscripcionInstancias(institucionesDestino, respuesta);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * sincronizarDirectorioCompleto()
	 */
	@Override
	public void sincronizarDirectorioCompleto() throws MalformedURLException, MensajeNoEnviado_Exception {

		getEnpoint().sincronizarDirectorioCompleto();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * sincronizarDirectorioParcial()
	 */
	@Override
	public void sincronizarDirectorioParcial() throws MalformedURLException, MensajeNoEnviado_Exception {

		getEnpoint().sincronizarDirectorioParcial();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.interoperabilidad.InteroperabilidadService#
	 * SolicitarSuscripcionInstancias()
	 */
	@Override
	public void SolicitarSuscripcionInstancias(List<String> list)
			throws MalformedURLException, MensajeNoEnviado_Exception {

		getEnpoint().solicitarSuscripcionInstancias(list);

	}

}
