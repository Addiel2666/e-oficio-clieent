/**
 * Copyright (c) 2020 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Clase de entidad que representa la tabla FOLIOPSMULTIPLE
 *
 * @author ECM SOLUTIONS
 * @version 1.0
 *
 *          Creacion de la clase
 *
 */
@Entity
@Table(name = "FOLIOPSMULTIPLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_FOLIOSPSMULTIPLE", sequenceName = "SECOBJETOS", allocationSize = 1)
public class FolioPSMultiple implements java.io.Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6374469197424723568L;

	/** Identificador de la foliadora */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FOLIOSPSMULTIPLE")
	@Column(name = "ID", unique = true, nullable = false, precision = 38, scale = 0)
	private Integer id;

	/** Prefijo usado para generar el Numero de Documento y Oficio */
	@Column(name = "PREFIJOFOLIO", length = 20)
	private String prefijoFolio;

	/** Sufijo usado para generar el Numero de Documento y Oficio */
	@Column(name = "SUFIJOFOLIO", length = 20)
	private String sufijoFolio;

	/** Identificador del Area */
	@Id
	@Column(name = "IDAREA", nullable = false, precision = 38, scale = 0)
	private Integer idArea;

	/** Descripcion de la foliadora */
	@Column(name = "DESCRIPCION", nullable = false, length = 1)
	private String descripcion;
	
	/** Foliador unico */
	@Column(name = "FOLIADORUNICO", length = 1)
	private String foliadorUnicoSN;
	
	/** Comparte folios */
	@Column(name = "COMPARTEFOLIO", length = 1)
	private String comparteFolioSN;
	
	/** id de la foliadora que hereda */
	@Column(name = "IDFOLIOHEREDA", length = 50)
	private String idFolioHeredado;
	
	/** Tipo de folio Propio/Herencia */
	@Column(name = "TIPO", length = 1)
	private String tipo;
	
	/** Identificador del Area Herencia */
	@Column(name = "IDAREAHEREDA")
	private Integer idAreaHereda;

	/**
	 * Constructor por defecto de la clase
	 */
	public FolioPSMultiple() {
		super();
		// Constructor vacio
	}

	/**
	 * Minimo contructor de la clase
	 *
	 * @param id
	 *            Identificador
	 */
	public FolioPSMultiple(Integer id, Integer idArea) {
		this.id = id;
		this.idArea = idArea;
	}

	/**
	 * Obtiene el Prefijo
	 *
	 * @return Prefijo
	 */
	public String getPrefijoFolio() {

		return (null != this.prefijoFolio) ? this.prefijoFolio : "";
	}

	/**
	 * Asigna el Prefijo
	 *
	 * @param prefijofolio
	 *            Prefijo
	 */
	public void setPrefijoFolio(String prefijofolio) {
		this.prefijoFolio = prefijofolio;
	}

	/**
	 * Obtiene el Sufijo
	 *
	 * @return Sufijo
	 */
	public String getSufijoFolio() {

		return (null != this.sufijoFolio) ? this.sufijoFolio : "";
	}

	/**
	 * Asigna el Sufijo
	 *
	 * @param sufijofolio
	 *            Sufijo
	 */
	public void setSufijoFolio(String sufijofolio) {

		if (null != sufijofolio) {

			this.sufijoFolio = sufijofolio;

		} else {

			this.sufijoFolio = "";
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdArea() {
		return idArea;
	}

	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the foliadorUnicoSN
	 */
	public String getFoliadorUnicoSN() {
		return foliadorUnicoSN;
	}

	/**
	 * @param foliadorUnicoSN the foliadorUnicoSN to set
	 */
	public void setFoliadorUnicoSN(String foliadorUnicoSN) {
		this.foliadorUnicoSN = foliadorUnicoSN;
	}

	/**
	 * @return the comparteFolioSN
	 */
	public String getComparteFolioSN() {
		return comparteFolioSN;
	}

	/**
	 * @param comparteFolioSN the comparteFolioSN to set
	 */
	public void setComparteFolioSN(String comparteFolioSN) {
		this.comparteFolioSN = comparteFolioSN;
	}

	/**
	 * @return the idFolioHeredado
	 */
	public String getIdFolioHeredado() {
		return idFolioHeredado;
	}

	/**
	 * @param idFolioHeredado the idFolioHeredado to set
	 */
	public void setIdFolioHeredado(String idFolioHeredado) {
		this.idFolioHeredado = idFolioHeredado;
	}

	/**
	 * @return the tipo
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the idAreaHereda
	 */
	public Integer getIdAreaHereda() {
		return idAreaHereda;
	}

	/**
	 * @param idAreaHereda the idAreaHereda to set
	 */
	public void setIdAreaHereda(Integer idAreaHereda) {
		this.idAreaHereda = idAreaHereda;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FolioPSMultiple [id=" + id + ", prefijoFolio=" + prefijoFolio + ", sufijoFolio=" + sufijoFolio
				+ ", idArea=" + idArea + ", descripcion=" + descripcion + ", foliadorUnicoSN=" + foliadorUnicoSN
				+ ", comparteFolioSN=" + comparteFolioSN + ", idFolioHeredado=" + idFolioHeredado + ", tipo=" + tipo
				+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

}
