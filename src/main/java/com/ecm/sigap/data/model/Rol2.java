/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * Clase de entidad que representa la tabla ROLES dentro del sistema
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "roles2")
@UniqueKey(columnNames = { "rol2Key.idRol", "rol2Key.idArea" }, message = "{Unique.descripcion}")
public class Rol2 implements Serializable {

	/** */
	private static final long serialVersionUID = 7463162217621064567L;

	/** The Ifai sisi solicitud key. */
	@EmbeddedId
	private Rol2Key rol2Key;

	/** Identificador del Rol */
	@Column(name = "idRol", insertable = false, updatable = false)
	private Integer idRol;

	/** Identificador del Area al que pertenece el Rol */
	@Column(name = "idArea", insertable = false, updatable = false)
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rol [id=" + rol2Key + ", descripcion=" + descripcion + ", activo=" + activo + ", atributos=" + atributos
				+ ", idAreaLim=" + idAreaLim + ", tipo=" + tipo + ", rutas=" + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (obj == this)
			return true;

		if (!(obj instanceof Rol2)) {
			return false;
		}

		if (!Rol2.class.isAssignableFrom(obj.getClass())) {
			return false;
		}

		Rol2 tmp = (Rol2) obj;

		try {

			if (tmp.idArea == this.idArea//
					&& tmp.idRol == this.idRol//
					&& tmp.idAreaLim == this.idAreaLim //
					&& tmp.atributos.equals(this.atributos) //
					&& tmp.descripcion.equals(this.descripcion) //
					&& tmp.tipo.equals(this.tipo))
				return true;

		} catch (NullPointerException e) {
			return false;
		}

		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.idArea, this.idRol, this.idAreaLim, this.descripcion, this.tipo, this.atributos);
	}

}
