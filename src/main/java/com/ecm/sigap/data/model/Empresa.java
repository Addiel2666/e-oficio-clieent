/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * 
 * Clase de identidad de la tabla EMPRESAS
 * 
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@SequenceGenerator(name = "SEQ_EMPRESAS", sequenceName = "SECEMPRESAS", allocationSize = 1)
@Table(name = "empresas")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Empresa implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6099958198026707278L;

	/** Identificador de la empresa */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EMPRESAS")
	@Column(name = "idEmpresa")
	private Integer id;

	/** Nombre de la empresa */
	@Column(name = "nombre")
	private String nombre;

	/** RFC de la empresa */
	@Column(name = "rfc")
	private String rfc;

	/** Identificador del Objeto en el Repositorio */
	@Column(name = "contentId")
	private String contentId;
	
	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activosn;
	
	/** Domicilio de la Empresa */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = Domicilio.class)
	@CollectionTable(name = "domicilios", joinColumns = { @JoinColumn(name = "idOrigen") })
	@JoinColumn(name = "idEmpresa", insertable = true, updatable = true, referencedColumnName = "idOrigen")
	@Fetch(value = FetchMode.SUBSELECT)
	@Cascade(CascadeType.ALL)
	@Where(clause = "idtipoorigen = 'E'")
	private List<Domicilio> domicilio;
	
	@Transient
	private boolean exactSearch;

	/**
	 * Obtiene el Identificador de la empresa
	 * 
	 * @return Identificador de la empresa
	 */
	public Integer getId() {

		return id;
	}

	/**
	 * Asigna el Identificador de la empresa
	 * 
	 * @param id
	 *            Identificador de la empresa
	 */
	public void setId(Integer id) {

		this.id = id;
	}

	/**
	 * Obtiene el Nombre de la empresa
	 * 
	 * @return Nombre de la empresa
	 */
	public String getNombre() {

		return nombre;
	}

	/**
	 * Asigna el Nombre de la empresa
	 * 
	 * @param nombre
	 *            Nombre de la empresa
	 */
	public void setNombre(String nombre) {

		this.nombre = nombre;
	}

	/**
	 * Obtiene el RFC de la empresa
	 * 
	 * @return RFC de la empresa
	 */
	public String getRfc() {

		return rfc;
	}

	/**
	 * Asigna el RFC de la empresa
	 * 
	 * @param rfc
	 *            RFC de la empresa
	 */
	public void setRfc(String rfc) {

		this.rfc = rfc;
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

	public List<Domicilio> getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(List<Domicilio> domicilio) {
		this.domicilio = domicilio;
	}

	/**
	 * @return the exactSearch
	 */
	public boolean isExactSearch() {
		return exactSearch;
	}

	/**
	 * @param exactSearch the exactSearch to set
	 */
	public void setExactSearch(boolean exactSearch) {
		this.exactSearch = exactSearch;
	}

	@Override
	public String toString() {
		return "Empresa [id=" + id + ", nombre=" + nombre + ", rfc=" + rfc + ", activosn=" + activosn + ", domicilio=" + domicilio + "]";
	}

}
