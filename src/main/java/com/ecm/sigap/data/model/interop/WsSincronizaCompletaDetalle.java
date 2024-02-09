/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model.interop;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.ecm.sigap.data.model.util.TipoRegistroWsOpe;
import com.ecm.sigap.data.util.TipoRegistroWsOpeToStringConverter;

/**
 * The Class WsSincronizaCompletaDetalle.
 *
 * @author Gustavo Vielmas
 * @version 1.0
 */
@Entity
@Table(name = "wsSincronizaCompletaDetalle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class WsSincronizaCompletaDetalle implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6186873914068011320L;

	/** The id registro. */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRegistro")
	private Integer idRegistro;

	/** The nombre. */
	@Column(name = "identificador")
	private String identificador;

	/** The nombre. */
	@Column(name = "nombre")
	private String nombre;

	/** The no distinguido. */
	@Column(name = "noDistinguido")
	private String noDistinguido;

	/** The id padre. */
	@Column(name = "idPadre")
	private String idPadre;

	/** The estatus {@link com.ecm.sigap.data.model.util.TipoRegistroWsOpe} */
	@Column(name = "tiRegistro")
	@Convert(converter = TipoRegistroWsOpeToStringConverter.class)
	private TipoRegistroWsOpe tipoRegistro;

	/** The custom 1. */
	@Column(name = "custom1")
	private String custom1;

	/** The custom 2. */
	@Column(name = "custom2")
	private String custom2;

	/** The custom 3. */
	@Column(name = "custom3")
	private String custom3;

	/** The custom 4. */
	@Column(name = "custom4")
	private String custom4;

	/** The custom 5. */
	@Column(name = "custom5")
	private String custom5;

	/** The area data. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "identificador", referencedColumnName = "idarea", insertable = false, updatable = false)
	@NotFound(action = NotFoundAction.IGNORE)
	@Where(clause = "tiregistro = 'A'")
	@Fetch(value = FetchMode.SELECT)
	private WsSincronizaCompleta areaData;

	/**
	 * Gets the id registro.
	 *
	 * @return the id registro
	 */
	public Integer getIdRegistro() {
		return idRegistro;
	}

	/**
	 * Gets the identificador.
	 *
	 * @return the identificador
	 */
	public String getIdentificador() {
		return identificador;
	}

	/**
	 * Sets the identificador.
	 *
	 * @param identificador the new identificador
	 */
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
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
	 * Gets the no distinguido.
	 *
	 * @return the no distinguido
	 */
	public String getNoDistinguido() {
		return noDistinguido;
	}

	/**
	 * Sets the no distinguido.
	 *
	 * @param noDistinguido the new no distinguido
	 */
	public void setNoDistinguido(String noDistinguido) {
		this.noDistinguido = noDistinguido;
	}

	/**
	 * Gets the id padre.
	 *
	 * @return the id padre
	 */
	public String getIdPadre() {
		return idPadre;
	}

	/**
	 * Sets the id padre.
	 *
	 * @param idPadre the new id padre
	 */
	public void setIdPadre(String idPadre) {
		this.idPadre = idPadre;
	}

	/**
	 * Gets the tipo registro.
	 *
	 * @return the tipo registro
	 */
	public TipoRegistroWsOpe getTipoRegistro() {
		return tipoRegistro;
	}

	/**
	 * Sets the tipo registro.
	 *
	 * @param tipoRegistro the new tipo registro
	 */
	public void setTipoRegistro(TipoRegistroWsOpe tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	/**
	 * Gets the custom 1.
	 *
	 * @return the custom 1
	 */
	public String getCustom1() {
		return custom1;
	}

	/**
	 * Sets the custom 1.
	 *
	 * @param custom1 the new custom 1
	 */
	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}

	/**
	 * Gets the custom 2.
	 *
	 * @return the custom 2
	 */
	public String getCustom2() {
		return custom2;
	}

	/**
	 * Sets the custom 2.
	 *
	 * @param custom2 the new custom 2
	 */
	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}

	/**
	 * Gets the custom 3.
	 *
	 * @return the custom 3
	 */
	public String getCustom3() {
		return custom3;
	}

	/**
	 * Sets the custom 3.
	 *
	 * @param custom3 the new custom 3
	 */
	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}

	/**
	 * Gets the custom 4.
	 *
	 * @return the custom 4
	 */
	public String getCustom4() {
		return custom4;
	}

	/**
	 * Sets the custom 4.
	 *
	 * @param custom4 the new custom 4
	 */
	public void setCustom4(String custom4) {
		this.custom4 = custom4;
	}

	/**
	 * Gets the custom 5.
	 *
	 * @return the custom 5
	 */
	public String getCustom5() {
		return custom5;
	}

	/**
	 * Sets the custom 5.
	 *
	 * @param custom5 the new custom 5
	 */
	public void setCustom5(String custom5) {
		this.custom5 = custom5;
	}

	/**
	 * Gets the area data.
	 *
	 * @return the area data
	 */
	public WsSincronizaCompleta getAreaData() {
		return areaData;
	}

	/**
	 * Sets the area data.
	 *
	 * @param areaData the new area data
	 */
	public void setAreaData(WsSincronizaCompleta areaData) {
		this.areaData = areaData;
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
	 * Sets the id registro.
	 *
	 * @param idRegistro the new id registro
	 */
	public void setIdRegistro(Integer idRegistro) {
		this.idRegistro = idRegistro;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WsSincronizaCompletaDetalle [idRegistro=" + idRegistro + ", identificador=" + identificador
				+ ", nombre=" + nombre + ", noDistinguido=" + noDistinguido + ", idPadre=" + idPadre + ", tipoRegistro="
				+ tipoRegistro + ", custom1=" + custom1 + ", custom2=" + custom2 + ", custom3=" + custom3 + ", custom4="
				+ custom4 + ", custom5=" + custom5 + ", areaData=" + areaData + "]";
	}

}
