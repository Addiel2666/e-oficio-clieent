/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class Area.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "favoritos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class Favorito implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -366300005792098087L;

	/** The favorito. */
	@EmbeddedId
	private FavoritoKey favoritoKey;

	/** The firm area. */
	@Column(name = "firmArea", insertable = false, updatable = false)
	private String firmArea;

	/** The siglas area. */
	@Column(name = "siglasArea", insertable = false, updatable = false)
	private String siglasArea;

	/** The areactivosn. */
	@Column(name = "areactivosn", insertable = false, updatable = false)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean areactivosn;

	/** The institucion. */
	@Column(name = "institucion", insertable = false, updatable = false)
	private String institucion;

	/** The id tipo institucion. */
	@Column(name = "idTipoInstitucion", insertable = false, updatable = false)
	private String idTipoInstitucion;

	/** The institucion activosn. */
	@Column(name = "institucionActivosn", insertable = false, updatable = false)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean institucionActivosn;

	/** The abrev institucion. */
	@Column(name = "abrevInstitucion", insertable = false, updatable = false)
	private String abrevInstitucion;

	/** The id tipo firmante. */
	@Column(name = "idTipoFirmante", insertable = false, updatable = false)
	private String idTipoFirmante;

	/** The paterno. */
	@Column(name = "paterno", insertable = false, updatable = false)
	private String paterno;

	/** The materno. */
	@Column(name = "materno", insertable = false, updatable = false)
	private String materno;

	/** The nombre. */
	@Column(name = "nombre", insertable = false, updatable = false)
	private String nombre;

	/** The cargo. */
	@Column(name = "cargo", insertable = false, updatable = false)
	private String cargo;

	/** The area representate. */
	@Column(name = "areaRepresentate", insertable = false, updatable = false)
	private String areaRepresentate;

	/** The cvearea representante. */
	@Column(name = "cveareaRepresentante", insertable = false, updatable = false)
	private String cveareaRepresentante;

	/** The usuario activosn. */
	@Column(name = "usuarioActivosn", insertable = false, updatable = false)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean usuarioActivosn;

	/** The id tipo usuario. */
	@Column(name = "idTipoUsuario", insertable = false, updatable = false)
	private String idTipoUsuario;

	/**
	 * Gets the favorito key.
	 *
	 * @return the favorito key
	 */
	public FavoritoKey getFavoritoKey() {
		return favoritoKey;
	}

	/**
	 * Sets the favorito key.
	 *
	 * @param favoritoKey
	 *            the new favorito key
	 */
	public void setFavoritoKey(FavoritoKey favoritoKey) {
		this.favoritoKey = favoritoKey;
	}

	/**
	 * Gets the firm area.
	 *
	 * @return the firm area
	 */
	public String getFirmArea() {
		return firmArea;
	}

	/**
	 * Sets the firm area.
	 *
	 * @param firmArea
	 *            the new firm area
	 */
	public void setFirmArea(String firmArea) {
		this.firmArea = firmArea;
	}

	/**
	 * Gets the siglas area.
	 *
	 * @return the siglas area
	 */
	public String getSiglasArea() {
		return siglasArea;
	}

	/**
	 * Sets the siglas area.
	 *
	 * @param siglasArea
	 *            the new siglas area
	 */
	public void setSiglasArea(String siglasArea) {
		this.siglasArea = siglasArea;
	}

	/**
	 * Gets the areactivosn.
	 *
	 * @return the areactivosn
	 */
	public Boolean getAreactivosn() {
		return areactivosn;
	}

	/**
	 * Sets the areactivosn.
	 *
	 * @param areactivosn
	 *            the new areactivosn
	 */
	public void setAreactivosn(Boolean areactivosn) {
		this.areactivosn = areactivosn;
	}

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public String getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion
	 *            the new institucion
	 */
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}

	/**
	 * Gets the id tipo institucion.
	 *
	 * @return the id tipo institucion
	 */
	public String getIdTipoInstitucion() {
		return idTipoInstitucion;
	}

	/**
	 * Sets the id tipo institucion.
	 *
	 * @param idTipoInstitucion
	 *            the new id tipo institucion
	 */
	public void setIdTipoInstitucion(String idTipoInstitucion) {
		this.idTipoInstitucion = idTipoInstitucion;
	}

	/**
	 * Gets the institucion activosn.
	 *
	 * @return the institucion activosn
	 */
	public Boolean getInstitucionActivosn() {
		return institucionActivosn;
	}

	/**
	 * Sets the institucion activosn.
	 *
	 * @param institucionActivosn
	 *            the new institucion activosn
	 */
	public void setInstitucionActivosn(Boolean institucionActivosn) {
		this.institucionActivosn = institucionActivosn;
	}

	/**
	 * Gets the abrev institucion.
	 *
	 * @return the abrev institucion
	 */
	public String getAbrevInstitucion() {
		return abrevInstitucion;
	}

	/**
	 * Sets the abrev institucion.
	 *
	 * @param abrevInstitucion
	 *            the new abrev institucion
	 */
	public void setAbrevInstitucion(String abrevInstitucion) {
		this.abrevInstitucion = abrevInstitucion;
	}

	/**
	 * Gets the id tipo firmante.
	 *
	 * @return the id tipo firmante
	 */
	public String getIdTipoFirmante() {
		return idTipoFirmante;
	}

	/**
	 * Sets the id tipo firmante.
	 *
	 * @param idTipoFirmante
	 *            the new id tipo firmante
	 */
	public void setIdTipoFirmante(String idTipoFirmante) {
		this.idTipoFirmante = idTipoFirmante;
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
	 *            the new paterno
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
	 *            the new materno
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
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
	 * @param nombre
	 *            the new nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Gets the cargo.
	 *
	 * @return the cargo
	 */
	public String getCargo() {
		return cargo;
	}

	/**
	 * Sets the cargo.
	 *
	 * @param cargo
	 *            the new cargo
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * Gets the area representate.
	 *
	 * @return the area representate
	 */
	public String getAreaRepresentate() {
		return areaRepresentate;
	}

	/**
	 * Sets the area representate.
	 *
	 * @param areaRepresentate
	 *            the new area representate
	 */
	public void setAreaRepresentate(String areaRepresentate) {
		this.areaRepresentate = areaRepresentate;
	}

	/**
	 * Gets the cvearea representante.
	 *
	 * @return the cvearea representante
	 */
	public String getCveareaRepresentante() {
		return cveareaRepresentante;
	}

	/**
	 * Sets the cvearea representante.
	 *
	 * @param cveareaRepresentante
	 *            the new cvearea representante
	 */
	public void setCveareaRepresentante(String cveareaRepresentante) {
		this.cveareaRepresentante = cveareaRepresentante;
	}

	/**
	 * Gets the usuario activosn.
	 *
	 * @return the usuario activosn
	 */
	public Boolean getUsuarioActivosn() {
		return usuarioActivosn;
	}

	/**
	 * Sets the usuario activosn.
	 *
	 * @param usuarioActivosn
	 *            the new usuario activosn
	 */
	public void setUsuarioActivosn(Boolean usuarioActivosn) {
		this.usuarioActivosn = usuarioActivosn;
	}

	/**
	 * Gets the id tipo usuario.
	 *
	 * @return the id tipo usuario
	 */
	public String getIdTipoUsuario() {
		return idTipoUsuario;
	}

	/**
	 * Sets the id tipo usuario.
	 *
	 * @param idTipoUsuario
	 *            the new id tipo usuario
	 */
	public void setIdTipoUsuario(String idTipoUsuario) {
		this.idTipoUsuario = idTipoUsuario;
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
		return "Favorito [favoritoKey=" + favoritoKey + ", firmArea=" + firmArea + ", siglasArea=" + siglasArea
				+ ", areactivosn=" + areactivosn + ", institucion=" + institucion + ", idTipoInstitucion="
				+ idTipoInstitucion + ", institucionActivosn=" + institucionActivosn + ", abrevInstitucion="
				+ abrevInstitucion + ", idTipoFirmante=" + idTipoFirmante + ", paterno=" + paterno + ", materno="
				+ materno + ", nombre=" + nombre + ", cargo=" + cargo + ", areaRepresentate=" + areaRepresentate
				+ ", cveareaRepresentante=" + cveareaRepresentante + ", usuarioActivosn=" + usuarioActivosn
				+ ", idTipoUsuario=" + idTipoUsuario + "]";
	}

}
