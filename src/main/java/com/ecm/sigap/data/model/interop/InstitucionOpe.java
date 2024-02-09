/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.interop;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.model.util.StatusInstitucionOpe;
import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.StatusInstitucionOpeToStringConverter;

/**
 * The Class InstitucionOpe.
 *
 * @author Gustavo Vielmas
 * @version 1.0
 */
@Entity
@Table(name = "instituciones_ope")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "nombre", "uri" }, message = "{Unique.descripcion}")
@NamedNativeQueries(value = {

		// Procesa Match de Instituciones OPE
		@NamedNativeQuery(name = "procesarMatchInstOpe", //
				query = " call institucionMatchInstOPE (:idInstitucionOPE, :status,:idInstitucion)"),
		@NamedNativeQuery(name = "procesarMatchInstOpe_POSTGRESQL", //
				query = " select * from {SIGAP_SCHEMA}.institucionMatchInstOPE (:idInstitucionOPE, :status,:idInstitucion)"),

		// Procesa Match de Instituciones OPE
		@NamedNativeQuery(name = "institucionOpeDel", //
				query = " call institucionope_del (:idInstitucionOPE)"),
		@NamedNativeQuery(name = "institucionOpeDel_POSTGRESQL", //
				query = " select * from {SIGAP_SCHEMA}.institucionope_del (:idInstitucionOPE)")

})
public class InstitucionOpe implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1645912272382899434L;

	/** The id institucion ope. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer idInstitucionOpe;

	/** The nombre. */
	@Column(name = "nombre")
	private String nombre;

	/** The nombre corto. */
	@Column(name = "nombreCorto")
	private String nombreCorto;

	/** The endpoint. */
	@Column(name = "endpoint")
	private String endpoint;

	/** The uri. */
	@Column(name = "uri")
	private String uri;

	/** The estatus {@link com.ecm.sigap.data.model.util.StatusInstitucionOpe} */
	@Column(name = "estatus")
	@Convert(converter = StatusInstitucionOpeToStringConverter.class)
	private StatusInstitucionOpe estatus;

	/** The id mensaje suscripcion. */
	@Column(name = "idMensajeSuscripcion")
	private String idMensajeSuscripcion;

	/**
	 * Gets the id institucion ope.
	 *
	 * @return the id institucion ope
	 */
	public Integer getIdInstitucionOpe() {
		return idInstitucionOpe;
	}

	/**
	 * Sets the id institucion ope.
	 *
	 * @param idInstitucionOpe the new id institucion ope
	 */
	public void setIdInstitucionOpe(Integer idInstitucionOpe) {
		this.idInstitucionOpe = idInstitucionOpe;
	}

	/**
	 * Gets the nombre.
	 *
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Sets the nombre.
	 *
	 * @param nombre the new nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Gets the nombre corto.
	 *
	 * @return the nombre corto
	 */
	public String getNombreCorto() {
		return nombreCorto;
	}

	/**
	 * Sets the nombre corto.
	 *
	 * @param nombreCorto the new nombre corto
	 */
	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}

	/**
	 * Gets the endpoint.
	 *
	 * @return the endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * Sets the endpoint.
	 *
	 * @param endpoint the new endpoint
	 */
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the uri.
	 *
	 * @param uri the new uri
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the estatus.
	 *
	 * @return the estatus
	 */
	public StatusInstitucionOpe getEstatus() {
		return estatus;
	}

	/**
	 * Sets the estatus.
	 *
	 * @param estatus the new estatus
	 */
	public void setEstatus(StatusInstitucionOpe estatus) {
		this.estatus = estatus;
	}

	/**
	 * Gets the id mensaje suscripcion.
	 *
	 * @return the id mensaje suscripcion
	 */
	public String getIdMensajeSuscripcion() {
		return idMensajeSuscripcion;
	}

	/**
	 * Sets the id mensaje suscripcion.
	 *
	 * @param idMensajeSuscripcion the new id mensaje suscripcion
	 */
	public void setIdMensajeSuscripcion(String idMensajeSuscripcion) {
		this.idMensajeSuscripcion = idMensajeSuscripcion;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InstitucionOpe [idInstitucionOpe=" + idInstitucionOpe + ", nombre=" + nombre + ", nombreCorto="
				+ nombreCorto + ", endpoint=" + endpoint + ", uri=" + uri + ", estatus=" + estatus
				+ ", idMensajeSuscripcion=" + idMensajeSuscripcion + "]";
	}

}
