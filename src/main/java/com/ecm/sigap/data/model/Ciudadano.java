/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.List;

//import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class Ciudadano.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "ciudadanos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_CIUDADANOS", sequenceName = "SECCIUDADANOS", allocationSize = 1)
public class Ciudadano implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7310759549014771222L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_CIUDADANOS")
	@Column(name = "idCiudadano")
	private Integer id;

	/** The nombres. */
	@Column(name = "nombre")
	private String nombres;

	/** The paterno. */
	@Column(name = "paterno")
	private String paterno;

	/** The materno. */
	@Column(name = "materno")
	private String materno;

	/** The nombre completo. */
	@Formula(" concat( NOMBRE , concat( ' ' , concat(PATERNO , concat(' ' ,  MATERNO)))) ")
	private String nombreCompleto;

	/** The homonimo. */
	@Column(name = "homonimo")
	private String homonimo;

	/** The rfc. */
	@Column(name = "rfc")
	private String rfc;

	/** The curp. */
	@Column(name = "curp")
	private String curp;

	/** The email. */
	@Column(name = "email")
	private String email;

	/** The id tipo. */
	@Column(name = "idTipoCiudadano")
	private String idTipo;
	
	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activosn;

	/** Domicilio de la Empresa. */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = Domicilio.class)
	@CollectionTable(name = "domicilios", joinColumns = { @JoinColumn(name = "idOrigen") })
	@JoinColumn(name = "idEmpresa", insertable = true, updatable = true, referencedColumnName = "idOrigen")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.ALL)
	@Where(clause = "idtipoorigen = 'C'")
	private List<Domicilio> domicilio;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the nombres.
	 *
	 * @return the nombres
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Sets the nombres.
	 *
	 * @param nombres
	 *            the new nombres
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Gets the paterno.
	 *
	 * @return the paterno
	 */
	public String getPaterno() {
		return paterno;
	}

	/**
	 * Sets the paterno.
	 *
	 * @param paterno
	 *            the paterno to set
	 */
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	/**
	 * Gets the materno.
	 *
	 * @return the materno
	 */
	public String getMaterno() {
		return null != materno ? materno : "";
	}

	/**
	 * Sets the materno.
	 *
	 * @param materno
	 *            the materno to set
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
	}

	/**
	 * Gets the nombre completo.
	 *
	 * @return the nombre completo
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * Sets the nombre completo.
	 *
	 * @param nombreCompleto
	 *            the new nombre completo
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Gets the homonimo.
	 *
	 * @return the homonimo
	 */
	public String getHomonimo() {
		return homonimo;
	}

	/**
	 * Sets the homonimo.
	 *
	 * @param homonimo
	 *            the homonimo to set
	 */
	public void setHomonimo(String homonimo) {
		this.homonimo = homonimo;
	}

	/**
	 * Gets the rfc.
	 *
	 * @return the rfc
	 */
	public String getRfc() {
		return rfc;
	}

	/**
	 * Sets the rfc.
	 *
	 * @param rfc
	 *            the rfc to set
	 */
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	/**
	 * Gets the curp.
	 *
	 * @return the curp
	 */
	public String getCurp() {
		return curp;
	}

	/**
	 * Sets the curp.
	 *
	 * @param curp
	 *            the curp to set
	 */
	public void setCurp(String curp) {
		this.curp = curp;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the id tipo.
	 *
	 * @return the idTipo
	 */
	public String getIdTipo() {
		return idTipo;
	}

	/**
	 * Sets the id tipo.
	 *
	 * @param idTipo
	 *            the idTipo to set
	 */
	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}

	/**
	 * @return the activosn
	 */
	public Boolean getActivosn() {
		return activosn;
	}

	/**
	 * @param activosn the activosn to set
	 */
	public void setActivosn(Boolean activosn) {
		this.activosn = activosn;
	}

	/**
	 * Obtiene el domicilio del Ciudadano.
	 *
	 * @return domicilio del Ciudadano
	 */
	public List<Domicilio> getDomicilio() {
		return domicilio;
	}

	/**
	 * Sets the domicilio.
	 *
	 * @param domicilio
	 *            the domicilio to set
	 */
	public void setDomicilio(List<Domicilio> domicilio) {
		this.domicilio = domicilio;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ciudadano [id=" + id + ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno
				+ ", nombreCompleto=" + nombreCompleto + ", homonimo=" + homonimo + ", rfc=" + rfc + ", curp=" + curp
				+ ", email=" + email + ", idTipo=" + idTipo + ", activosn=" + activosn + ", domicilio=" + domicilio + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

}
