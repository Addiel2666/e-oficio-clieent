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
import javax.persistence.JoinTable;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.validator.UniqueKey;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The Class Area.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "areas", //
		schema = "{SIGAP_SCHEMA}" //
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, //
		region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_AREAS", //
		schema = "{SIGAP_SCHEMA}", //
		sequenceName = "SECAREAS", //
		allocationSize = 1)
@NamedNativeQueries(value = {
		// FOLIOS POR AREA.
		@NamedNativeQuery(name = "generaFolio", //
				query = " select {SIGAP_SCHEMA}.GENERA_FOLIO(:idArea) from dual "),

		// FOLIOS CLAVE POR AREA.
		@NamedNativeQuery(name = "generaFolioClave", //
				query = " select {SIGAP_SCHEMA}.GENERA_FOLIO_CLAVE(:idArea) from dual "),

		// Desbloquea un folio no usado.
		@NamedNativeQuery(name = "desbloqueaFolio", //
				query = " select {SIGAP_SCHEMA}.DESBLOQUEA_FOLIO(:idArea, :folio) from dual "),
		// Desbloquea un folio clave no usado.
		@NamedNativeQuery(name = "desbloqueaFolioClave", //
				query = " select {SIGAP_SCHEMA}.DESBLOQUEA_FOLIO_CLAVE(:idArea, :folio) from dual "),

		// genera el siguiente folio al usar uno.
		@NamedNativeQuery(name = "addNextFolio", //
				query = " select {SIGAP_SCHEMA}.ADD_NEXT_FOLIO(:idArea, :folio) from dual "),

		// genera el siguiente folio clave al usar uno.
		@NamedNativeQuery(name = "addNextFolioClave", //
				query = " select {SIGAP_SCHEMA}.ADD_NEXT_FOLIO_CLAVE(:idArea, :folio) from dual "),

		// RUTA DEL AREA.
		@NamedNativeQuery(name = "rutaArea", //
				query = " select {SIGAP_SCHEMA}.obtiene_path_area_desc_v3(:idArea) from dual "),
		// RUTA DEL IDAREA.
		@NamedNativeQuery(name = "rutaIdArea", //
				query = " select {SIGAP_SCHEMA}.obtiene_path_area_v3(:idArea) from dual "),
		// NUMERO DE DOCUMENTO AUTOMATICO
		@NamedNativeQuery(name = "generaNumDoctoAuto", //
				query = " select {SIGAP_SCHEMA}.FOLIOSAREA_FSEL(:idArea, :idTipo) from dual "),
		// OBTIENE FOLIO AREA HEREDADA
		@NamedNativeQuery(name = "obtieneFolioAreaHeredada", //
				query = " select {SIGAP_SCHEMA}.OBTENFOLIO_AREA_HEREDADA(:idArea) from dual "),

		// areas sin folio
		@NamedNativeQuery(name = "areasSinFolios", //
				query = " SELECT a.* FROM {SIGAP_SCHEMA}.areas a inner join INSTITUCIONES i on i.IDINSTITUCION=a.IDINSTITUCION WHERE i.IDTIPOINSTITUCION='I' and NOT EXISTS (SELECT * FROM folios f WHERE f.idArea = a.idArea) "), //

		// areas sin folios desbloqueados
		@NamedNativeQuery(name = "areasSinFoliosDesbloqueados", //
				query = " SELECT a.* FROM {SIGAP_SCHEMA}.areas a inner join INSTITUCIONES i on i.IDINSTITUCION=a.IDINSTITUCION WHERE i.IDTIPOINSTITUCION='I' and NOT EXISTS (SELECT * FROM folios f WHERE f.idArea = a.idArea and f.VLOCK='D') "), //
		// areas sin descripción
		@NamedNativeQuery(name = "areasSinDescripcion", //
				query = " SELECT a.* FROM {SIGAP_SCHEMA}.areas a where a.descripcion is null"), //
		// areas sin ContentId
		@NamedNativeQuery(name = "areasSinContentId", //
				query = " select a.* from {SIGAP_SCHEMA}.areas a "
						+ " inner join {SIGAP_SCHEMA}.Instituciones i on a.IDINSTITUCION = i.IDINSTITUCION "
						+ " where i.IDTIPOINSTITUCION='I' and a.CONTENTID is null and a.DESCRIPCION is not null ")//
})
@UniqueKey(columnNames = { "descripcion", "claveDepartamental",
		"institucion.idInstitucion" }, message = "{Unique.descripcion}")
public final class Area implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 9148234048624086294L;

	/** The id area. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_AREAS")
	@Column(name = "idArea", insertable = false)
	private Integer idArea;

	/** The descripcion. */
	@Column(name = "descripcion")
	private String descripcion;

	/** The institucion. */
	@OneToOne
	@JoinColumn(name = "idInstitucion")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Institucion institucion;

	/** The id area padre. */
	@Column(name = "idAreaPadre")
	private Integer idAreaPadre;

	/** The titular. */
	@OneToOne
	@JoinColumn(name = "titularUsuario")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Representante titular;

	/** The titular cargo. */
	@Column(name = "titularCargo")
	private String titularCargo;

	/** The clave. */
	@Column(name = "claveCDD")
	private String clave;

	/** The clave departamental. */
	@Column(name = "clave")
	private String claveDepartamental;

	/** The cve area. */
	@Column(name = "cveArea")
	private String cveArea;

	/** The id externo. */
	@Column(name = "idExterno")
	private String idExterno;

	/** The siglas. */
	@Column(name = "siglas")
	private String siglas;

	/** ID del folder en el repositorio documental. */
	@Column(name = "contentId")
	private String contentId;

	/** The activo. */
	@Column(name = "activosn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activo;

	/** The interopera. */
	@Column(name = "interoperasn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean interopera;

	/** The ruta. */
	@Formula("{SIGAP_SCHEMA}.obtiene_path_area_desc_v3(idArea)")
	private String ruta;

	/** The ruta. */
	@Formula("{SIGAP_SCHEMA}.obtiene_path_area_v3(idArea)")
	private String rutaId;

	/** orden de los resultados al hacer busquedas ej. idArea, descripcion */
	@Transient
	private String order;

	/** area para limitar las busquedas hacia abajo en la estructura. */
	@Transient
	private Integer limite;

	/** area padre. */
	@OneToOne(targetEntity = AreaPadre.class)
	@JoinTable(name = "AreasPadresOnly", //
			joinColumns = { @JoinColumn(name = "idArea") }, //
			inverseJoinColumns = //
			{ @JoinColumn(name = "idAreaPadre", insertable = false, updatable = false) })
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private AreaPadre areaPadre;

	@Transient
	private boolean exactSearch;
	
	@Transient
	private String activeInactive;

	/**
	 * 
	 */
	public Area() {
		super();
	}

	/**
	 * 
	 * @param idArea
	 */
	public Area(Integer idArea) {
		super();
		this.idArea = idArea;
	}

	/**
	 * Gets the id area.
	 *
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the descripcion.
	 *
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * Sets the descripcion.
	 *
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Gets the institucion.
	 *
	 * @return the institucion
	 */
	public Institucion getInstitucion() {
		return institucion;
	}

	/**
	 * Sets the institucion.
	 *
	 * @param institucion the institucion to set
	 */
	public void setInstitucion(Institucion institucion) {
		this.institucion = institucion;
	}

	/**
	 * Gets the id area padre.
	 *
	 * @return the id area padre
	 */
	public Integer getIdAreaPadre() {
		return idAreaPadre;
	}

	/**
	 * Sets the id area padre.
	 *
	 * @param idAreaPadre the new id area padre
	 */
	public void setIdAreaPadre(Integer idAreaPadre) {
		this.idAreaPadre = idAreaPadre;
	}

	/**
	 * Gets the titular.
	 *
	 * @return the titular
	 */
	public Representante getTitular() {
		return titular;
	}

	/**
	 * Sets the titular.
	 *
	 * @param titular the titular to set
	 */
	public void setTitular(Representante titular) {
		this.titular = titular;
	}

	/**
	 * Gets the titular cargo.
	 *
	 * @return the titularCargo
	 */
	public String getTitularCargo() {
		return titularCargo;
	}

	/**
	 * Sets the titular cargo.
	 *
	 * @param titularCargo the titularCargo to set
	 */
	public void setTitularCargo(String titularCargo) {
		this.titularCargo = titularCargo;
	}

	/**
	 * Gets the clave.
	 *
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * Sets the clave.
	 *
	 * @param clave the clave to set
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}

	/**
	 * Gets the clave departamental.
	 *
	 * @return the clave departamental
	 */
	public String getClaveDepartamental() {
		return claveDepartamental;
	}

	/**
	 * Sets the clave departamental.
	 *
	 * @param claveDepartamental the new clave departamental
	 */
	public void setClaveDepartamental(String claveDepartamental) {
		this.claveDepartamental = claveDepartamental;
	}

	/**
	 * Gets the cve area.
	 *
	 * @return the cve area
	 */
	public String getCveArea() {
		return cveArea;
	}

	/**
	 * Sets the cve area.
	 *
	 * @param cveArea the new cve area
	 */
	public void setCveArea(String cveArea) {
		this.cveArea = cveArea;
	}

	/**
	 * Gets the id externo.
	 *
	 * @return the id externo
	 */
	public String getIdExterno() {
		return idExterno;
	}

	/**
	 * Sets the id externo.
	 *
	 * @param idExterno the new id externo
	 */
	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
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
	 * Gets the siglas.
	 *
	 * @return the siglas
	 */
	public String getSiglas() {
		return siglas;
	}

	/**
	 * Sets the siglas.
	 *
	 * @param siglas the siglas to set
	 */
	public void setSiglas(String siglas) {
		this.siglas = siglas;
	}

	/**
	 * Gets the interopera.
	 *
	 * @return the interopera
	 */
	public Boolean getInteropera() {
		return interopera;
	}

	/**
	 * Sets the interopera.
	 *
	 * @param interopera the interopera to set
	 */
	public void setInteropera(Boolean interopera) {
		this.interopera = interopera;
	}

	/**
	 * Gets the activo.
	 *
	 * @return the activo
	 */
	public Boolean getActivo() {
		return activo;
	}

	/**
	 * Sets the activo.
	 *
	 * @param activo the activo to set
	 */
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	/**
	 * Gets the ruta.
	 *
	 * @return the ruta
	 */
	public String getRuta() {
		return ruta;
	}

	/**
	 * Sets the ruta.
	 *
	 * @param ruta the new ruta
	 */
	public void setRuta(String ruta) {
		this.ruta = ruta;
	}

	/**
	 * Gets the content id.
	 *
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * Sets the content id.
	 *
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	/**
	 * Gets the areaPadre.
	 *
	 * @return the areaPadre
	 */
	public AreaPadre getAreaPadre() {
		return areaPadre;
	}

	/**
	 * Sets the setAreaPadre.
	 *
	 * @param areaPadre the areaPadre to set
	 */
	public void setAreaPadre(AreaPadre areaPadre) {
		this.areaPadre = areaPadre;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Area [idArea=" + idArea + ", descripcion=" + descripcion + ", institucion=" + institucion
				+ ", idAreaPadre=" + idAreaPadre + ", titular=" + titular + ", titularCargo=" + titularCargo
				+ ", clave=" + clave + ", claveDepartamental=" + claveDepartamental + ", siglas=" + siglas
				+ ", contentId=" + contentId + ", activo=" + activo + ", interopera=" + interopera + ", ruta=" + ruta
				+ ", rutaId=" + rutaId + ", order=" + order + ", limite=" + limite + ", areaPadre=" + areaPadre + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idArea == null) ? 0 : idArea.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Area other = (Area) obj;
		if (idArea == null) {
			if (other.idArea != null)
				return false;
		} else if (!idArea.equals(other.idArea))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.audit.aspectj.IAuditLog#getId()
	 */
	@Override
	@JsonIgnore
	public String getId() {

		return String.valueOf(this.idArea);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.audit.aspectj.IAuditLog#getLogDeatil()
	 */
	@Override
	@JsonIgnore
	public String getLogDeatil() {
		StringBuilder sb = new StringBuilder();
		sb.append("Área: ").append(descripcion).append("<br>")
		.append("Siglas: ").append(siglas).append("<br>")
		.append("Clave: ").append(clave).append("<br>")
		.append("Clave departamental: ").append(claveDepartamental).append("<br>")
		.append("Institución: ").append(institucion.getDescripcion()).append("<br>")
		.append("Área padre: ").append( (areaPadre != null) ? areaPadre.getDescripcion() : "null" ).append("<br>")
		.append("Activo: ").append(activo).append("<br>")
		.append("Titular: ").append((titular != null )? titular.getNombreCompleto() : "null").append("<br>")
		.append("Cargo titular: ").append(titularCargo).append("<br>")
		.append("Id content: ").append(contentId).append("<br>")
		.append("Interoperabilidad: ").append(interopera)
		.append("Id área padre: ").append(idAreaPadre).append("<br>")
		.append("Id Area: ").append(idArea).append("<br>")
		.append("Id institución: ").append(institucion.getIdInstitucion()).append("<br>")
		.append("Id titular: ").append((titular != null )? titular.getId() : "null").append("<br>");

		return sb.toString();
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * Sets the order.
	 *
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * Gets the ruta id.
	 *
	 * @return the rutaId
	 */
	public String getRutaId() {
		return rutaId;
	}

	/**
	 * Sets the ruta id.
	 *
	 * @param rutaId the rutaId to set
	 */
	public void setRutaId(String rutaId) {
		this.rutaId = rutaId;
	}

	/**
	 * Gets the limite.
	 *
	 * @return the limite
	 */
	public Integer getLimite() {
		return limite;
	}

	/**
	 * Sets the limite.
	 *
	 * @param limite the limite to set
	 */
	public void setLimite(Integer limite) {
		this.limite = limite;
	}

	public boolean isExactSearch() {
		return exactSearch;
	}

	public void setExactSearch(boolean exactSearch) {
		this.exactSearch = exactSearch;
	}

	public String getActiveInactive() {
		return activeInactive;
	}

	public void setActiveInactive(String activeInactive) {
		this.activeInactive = activeInactive;
	}
	
	

}
