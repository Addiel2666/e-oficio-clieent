/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class RepresentanteLegal.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "repLegalView")
@SequenceGenerator(name = "SEQ_REPRESENTANTES_LEGALES", sequenceName = "SECREPLEGAL", allocationSize = 1)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class RepresentanteLegal implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7732623194114043766L;

	/** The id. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REPRESENTANTES_LEGALES")
	@Column(name = "idReplegal")
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
	
	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activosn;

	/** The id rep legal 2. */
	@Column(name = "idreplegal2", insertable = false, updatable = false)
	private String idRepLegal2;

	/** The empresa. */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "idEmpresa")
	@Fetch(value = FetchMode.SELECT)
	private Empresa empresa;

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
		return materno == null ? "" : materno;
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
	 * Gets the empresa.
	 *
	 * @return the empresa
	 */
	public Empresa getEmpresa() {
		return empresa;
	}

	/**
	 * Sets the empresa.
	 *
	 * @param empresa
	 *            the empresa to set
	 */
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	/**
	 * Gets the id rep legal 2.
	 *
	 * @return the id rep legal 2
	 */
	public String getIdRepLegal2() {
		return idRepLegal2;
	}

	/**
	 * Sets the id rep legal 2.
	 *
	 * @param idRepLegal2
	 *            the new id rep legal 2
	 */
	public void setIdRepLegal2(String idRepLegal2) {
		this.idRepLegal2 = idRepLegal2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RepresentanteLegal [id=" + id + ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno
				+ ", nombreCompleto=" + nombreCompleto + ", homonimo=" + homonimo + ", rfc=" + rfc + ", curp=" + curp
				+ ", email=" + email + ", activosn=" + activosn + ", idRepLegal2=" + idRepLegal2 + ", empresa=" + empresa + "]";
	}

}
