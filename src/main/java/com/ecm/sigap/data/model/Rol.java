/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.validator.constraints.NotEmpty;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * Clase de entidad que representa la tabla ROLES dentro del sistema
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "roles")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_ROLES", sequenceName = "SECOBJETOS", allocationSize = 1)
@SecondaryTables({ //
		@SecondaryTable(name = "ROL_TIPO_ASUNTO", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "IDROL", referencedColumnName = "IDROL"))//
})
public class Rol implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1563191606781267343L;

	/** Identificador del Rol */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLES")
	@Column(name = "idRol")
	private Integer idRol;

	/** Identificador del Area al que pertenece el Rol */
	@Column(name = "idArea")
	@NotNull
	private Integer idArea;

	/** Descripcion del Rol */
	@Column(name = "descripcion")
	@NotNull
	@NotEmpty
	private String descripcion;

	/** Identificador del estatus del Usuario en el sistema */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	@NotNull
	private Boolean activo;

	/** Atributos del Rol */
	@Column(name = "atributos")
	private String atributos;

	/** Identificador del Area limite */
	@Column(name = "areaLim")
	private Integer idAreaLim;

	/** Identificador del tipo de Rol */
	@Column(name = "idTipoRol")
	@NotNull
	private String tipo;

	/** Identificador del Expediente */
	@OneToOne
	@JoinColumn(name = "idTipo", table = "ROL_TIPO_ASUNTO", nullable = true, referencedColumnName = "idTipo")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private Tipo tipoAsuntoLimitado;

	/**
	 * Obtiene el Identificador del Rol
	 *
	 * @return Identificador del Rol
	 */
	public Integer getIdRol() {

		return idRol;
	}

	/**
	 * Asigna el Identificador del Rol
	 *
	 * @param idRol Identificador del Rol
	 */
	public void setIdRol(Integer idRol) {

		this.idRol = idRol;
	}

	/**
	 * Obtiene el Identificador del Area al que pertenece el Rol
	 *
	 * @return Identificador del Area al que pertenece el Rol
	 */
	public Integer getIdArea() {

		return idArea;
	}

	/**
	 * Asigna el Identificador del Area al que pertenece el Rol
	 *
	 * @param idArea Identificador del Area al que pertenece el Rol
	 */
	public void setIdArea(Integer idArea) {

		this.idArea = idArea;
	}

	/**
	 * Obtiene la Descripcion del Rol
	 *
	 * @return Descripcion del Rol
	 */
	public String getDescripcion() {

		return descripcion;
	}

	/**
	 * Asigna la Descripcion del Rol
	 *
	 * @param descripcion Descripcion del Rol
	 */
	public void setDescripcion(String descripcion) {

		this.descripcion = descripcion;
	}

	/**
	 * Obtiene el Identificador del estatus del Usuario en el sistema
	 *
	 * @return Identificador del estatus del Usuario en el sistema
	 */
	public Boolean getActivo() {

		return activo;
	}

	/**
	 * Asigna el Identificador del estatus del Usuario en el sistema
	 *
	 * @param activo Identificador del estatus del Usuario en el sistema
	 */
	public void setActivo(Boolean activo) {

		this.activo = activo;
	}

	/**
	 * Obtiene los Atributos del Rol
	 *
	 * @return Atributos del Rol
	 */
	public String getAtributos() {

		return atributos;
	}

	/**
	 * Asigna los Atributos del Rol
	 *
	 * @param atributos Atributos del Rol
	 */
	public void setAtributos(String atributos) {

		this.atributos = atributos;
	}

	/**
	 * Obtiene el Identificador del Area limite
	 *
	 * @return Identificador del Area limite
	 */
	public Integer getIdAreaLim() {

		return idAreaLim;
	}

	/**
	 * Asigna el Identificador del Area limite
	 *
	 * @param idAreaLim Identificador del Area limite
	 */
	public void setIdAreaLim(Integer idAreaLim) {

		this.idAreaLim = idAreaLim;
	}

	/**
	 * Obtiene el Identificador del tipo de Rol
	 *
	 * @return Identificador del tipo de Rol
	 */
	public String getTipo() {

		return tipo;
	}

	/**
	 * Asigna el Identificador del tipo de Rol
	 *
	 * @param tipo Identificador del tipo de Rol
	 */
	public void setTipo(String tipo) {

		this.tipo = tipo;
	}

	/**
	 * @return the tipoAsuntoLimitado
	 */
	public Tipo getTipoAsuntoLimitado() {
		return tipoAsuntoLimitado;
	}

	/**
	 * @param tipoAsuntoLimitado the tipoAsuntoLimitado to set
	 */
	public void setTipoAsuntoLimitado(Tipo tipoAsuntoLimitado) {
		this.tipoAsuntoLimitado = tipoAsuntoLimitado;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rol [idRol=" + idRol + ", idArea=" + idArea + ", descripcion=" + descripcion + ", activo=" + activo
				+ ", atributos=" + atributos + ", idAreaLim=" + idAreaLim + ", tipo=" + tipo + ", tipoAsuntoLimitado="
				+ tipoAsuntoLimitado + "]";
	}

}
