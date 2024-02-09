/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.AsuntoCiudadano;
import com.ecm.sigap.data.model.util.TipoRegistro;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.TipoRegistroToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase de entidad que representa la tabla ASUNTOSDETALLE
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "AsuntosDetalle")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_ASUNTOSDETALLE", sequenceName = "SECASUNTOSDETALLE", allocationSize = 1)
public class AsuntoDetalle implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2317856347576254259L;

	/** Identificador del Asunto Detalle */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ASUNTOSDETALLE")
	@Column(name = "idAsuntoDetalle")
	private Integer idAsuntoDetalle;

	/** Identificador de la procedencia del Asunto / Tramite */
	@Column(name = "idProcedencia")
	private String idProcedencia;

	/** Promotor o Institucion del Asunto / Tramite */
	@OneToOne
	@JoinColumn(name = "idPromotor")
	@NotNull
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Institucion promotor;

	/** Remitente o Area del Asunto / Turno */
	@OneToOne
	@NotNull
	@JoinColumns({
			@JoinColumn(name = "idRemitente", referencedColumnName = "idRemitente", insertable = false, updatable = false),
			@JoinColumn(name = "idPromotor", referencedColumnName = "idpromotor", insertable = false, updatable = false) })
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Remitente remitente;

	@JsonIgnore
	@Column(name = "idRemitente")
	private Integer idRemitente;

	/** Fecha de recepcion del Tramite */
	@Column(name = "fechaRecepcion")
	@NotNull
	private Date fechaRecepcion;

	/** Fecha de Elaboracion */
	@Column(name = "fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracion;

	/** Numero de Documento o Numero de Oficio */
	@Column(name = "numDocto")
	private String numDocto;

	@JsonIgnore
	@Transient
	private Integer idFolioMultiple;
	
	/** Usuario firmante del Asunto / Tramite */
	@OneToOne
	@JoinColumnsOrFormulas({
			@JoinColumnOrFormula (column = @JoinColumn(name = "idPromotor", referencedColumnName = "idPromotor", insertable = false, updatable = false)),
			@JoinColumnOrFormula(column = @JoinColumn(name = "idRemitente", referencedColumnName = "idremitente", insertable = false, updatable = false)),
			@JoinColumnOrFormula(column = @JoinColumn(name = "firmante", referencedColumnName = "idFirmante", insertable = false, updatable = false)),
			@JoinColumnOrFormula(formula = @JoinFormula(value="case when idPromotor = 3 then 'CIUDADANO' WHEN idPromotor = 2 THEN 'REPLEGAL' ELSE 'REPRESENTANTE' END", referencedColumnName = "tipoFirmante"))
			})
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Firmante firmante;

	/** The id firmante. */
	@Column(name = "firmante")
	private String idFirmante;

	/** Descripcion del Cargo del Firmante */
	@Column(name = "firmanteCargo", length = 300)
	private String firmanteCargo;

	/** Usuario al cual va dirigido el Tramite */
	@OneToOne
	@JoinColumn(name = "dirigidoA")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Usuario dirigidoA;
	
	/** Id del funcionario externo al que va dirigido el tramite */
    @Column(name = "iddirigidoa")
    private String idDirigidoA;

	/** Descripcion del Asunto */
	@Column(name = "AsuntoDescripcion")
	private String asuntoDescripcion;

	/** Identificador del Asunto Infomex */
	@Column(name = "idExterno")
	private String idExterno;

	/** Identificador del Tipo de Registro */
	@Column(name = "idTipoRegistro")
	@Convert(converter = TipoRegistroToStringConverter.class)
	private TipoRegistro tipoRegistro;

	/** Descripcion del cargo del Dirigido A */
	@Column(name = "dirigidoACargo")
	private String dirigidoACargo;

	/** Identificador si el Asunto / Tramite es confidencial */
	@Column(name = "confidencialSN")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

	/** Folio Intermedio del Asunto */
	@Column(name = "folioIntermedio")
	private String folioIntermedio;

	/** Palabras claves del Asunto */
	@Column(name = "palabraClave")
	private String palabraClave;

	/** */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "ASUNTOSCIUDADANOS", joinColumns = { @JoinColumn(name = "idAsuntoDetalle") })
	@JoinColumn(name = "idAsuntoDetalle")
	@Fetch(value = FetchMode.SUBSELECT)
	private List<AsuntoCiudadano> ciudadanos;

	/** Identificador si el Numero de Oficio se genera Automatico o no */
	@JsonIgnore
	@Transient
	private boolean isNumDoctoAuto;

	/** */
	@OneToOne(targetEntity = Tipo.class)
	@JoinColumn(name = "idTipo")
	@Fetch(FetchMode.SELECT)
	private Tipo tipo;

	/**  */
	@Column(name = "clave")
	private String clave;

	/** */
	@JsonIgnore
	@Transient
	private boolean isClaveAuto;

	/**
	 * Constructor por defecto de la clase
	 */
	public AsuntoDetalle() {
		super();
	}

	/**
	 * Obtiene la Descripcion del Asunto
	 * 
	 * @return Descripcion del Asunto
	 */
	public String getAsuntoDescripcion() {

		return asuntoDescripcion;
	}

	/**
	 * Asigna la Descripcion del Asunto
	 * 
	 * @param asuntoDescripcion Descripcion del Asunto
	 */
	public void setAsuntoDescripcion(String asuntoDescripcion) {

		this.asuntoDescripcion = asuntoDescripcion;
	}

	/**
	 * Obtiene el Identificador del Asunto Detalle
	 * 
	 * @return Identificador del Asunto Detalle
	 */
	public Integer getIdAsuntoDetalle() {

		return idAsuntoDetalle;
	}

	/**
	 * Asigna el Identificador del Asunto Detalle
	 * 
	 * @param idAsuntoDetalle Identificador del Asunto Detalle
	 */
	public void setIdAsuntoDetalle(Integer idAsuntoDetalle) {

		this.idAsuntoDetalle = idAsuntoDetalle;
	}

	/**
	 * Obtiene el Identificador de la procedencia del Asunto / Tramite
	 * 
	 * @return Identificador de la procedencia del Asunto / Tramite
	 */
	public String getIdProcedencia() {

		return idProcedencia;
	}

	/**
	 * Obtiene la descripcion del Identificador de la procedencia del Asunto /
	 * Tramite
	 * 
	 * @return Descripcion del Identificador de la procedencia del Asunto / Tramite
	 */
	public String getIdProcedenciaS() {

		if ("I".equalsIgnoreCase(idProcedencia))
			return "INTERNO";
		else if ("E".equalsIgnoreCase(idProcedencia))
			return "EXTERNO";
		else if ("S".equalsIgnoreCase(idProcedencia))
			return "SALIDA";
		else
			return "";
	}

	/**
	 * Asigna el Identificador de la procedencia del Asunto / Tramite
	 * 
	 * @param idProcedencia Identificador de la procedencia del Asunto / Tramite
	 */
	public void setIdProcedencia(String idProcedencia) {

		this.idProcedencia = idProcedencia;
	}

	/**
	 * Obtiene el Numero de Documento o Numero de Oficio
	 * 
	 * @return Numero de Documento o Numero de Oficio
	 */
	public String getNumDocto() {

		return numDocto;
	}

	/**
	 * Asigna el Numero de Documento o Numero de Oficio
	 * 
	 * @param numDocto Numero de Documento o Numero de Oficio
	 */
	public void setNumDocto(String numDocto) {

		this.numDocto = numDocto;
	}

	/**
	 * Obtiene la Fecha de recepcion del Tramite
	 * 
	 * @return Fecha de recepcion del Tramite
	 */
	public Date getFechaRecepcion() {

		return fechaRecepcion;
	}

	/**
	 * Asigna la Fecha de recepcion del Tramite
	 * 
	 * @param fechaRecepcion Fecha de recepcion del Tramite
	 */
	public void setFechaRecepcion(Date fechaRecepcion) {

		this.fechaRecepcion = fechaRecepcion;
	}

	/**
	 * Obtiene la Fecha de Elaboracion
	 * 
	 * @return Fecha de Elaboracion
	 */
	public Date getFechaElaboracion() {

		return fechaElaboracion;
	}

	/**
	 * Asigna la Fecha de Elaboracion
	 * 
	 * @param fechaElaboracion Fecha de Elaboracion
	 */
	public void setFechaElaboracion(Date fechaElaboracion) {

		this.fechaElaboracion = fechaElaboracion;
	}

	/**
	 * @return the dirigidoACargo
	 */
	public String getDirigidoACargo() {

		return dirigidoACargo;
	}

	/**
	 * @param dirigidoACargo the dirigidoACargo to set
	 */
	public void setDirigidoACargo(String dirigidoACargo) {

		this.dirigidoACargo = dirigidoACargo;
	}

	/**
	 * Obtiene el Identificador si el Asunto / Tramite es confidencial
	 * 
	 * @return Identificador si el Asunto / Tramite es confidencial
	 */
	public Boolean getConfidencial() {

		return confidencial;
	}

	/**
	 * Asigna el Identificador si el Asunto / Tramite es confidencial
	 * 
	 * @param confidencial Identificador si el Asunto / Tramite es confidencial
	 */
	public void setConfidencial(Boolean confidencial) {

		this.confidencial = confidencial;
	}

	/**
	 * Obtiene el Promotor o Institucion del Asunto / Tramite
	 * 
	 * @return Promotor o Institucion del Asunto / Tramite
	 */
	public Institucion getPromotor() {
		return promotor;
	}

	/**
	 * Asigna el Promotor o Institucion del Asunto / Tramite
	 * 
	 * @param promotor Promotor o Institucion del Asunto / Tramite
	 */
	public void setPromotor(Institucion promotor) {

		this.promotor = promotor;
	}

	/**
	 * Obtiene el Folio Intermedio del Asunto
	 * 
	 * @return Folio Intermedio del Asunto
	 */
	public String getFolioIntermedio() {

		return folioIntermedio;
	}

	/**
	 * Asigna el Folio Intermedio del Asunto
	 * 
	 * @param folioIntermedio Folio Intermedio del Asunto
	 */
	public void setFolioIntermedio(String folioIntermedio) {

		this.folioIntermedio = folioIntermedio;
	}

	/**
	 * Obtiene las Palabras claves del Asunto
	 * 
	 * @return Palabras claves del Asunto
	 */
	public String getPalabraClave() {

		return palabraClave;
	}

	/**
	 * Asigna las Palabras claves del Asunto
	 * 
	 * @param palabraClave Palabras claves del Asunto
	 */
	public void setPalabraClave(String palabraClave) {

		this.palabraClave = palabraClave;
	}

	/**
	 * Obtiene el Usuario al cual va dirigido el Tramite
	 * 
	 * @return Usuario al cual va dirigido el Tramite
	 */
	public Usuario getDirigidoA() {

		return dirigidoA;
	}

	/**
	 * Asigna el Usuario al cual va dirigido el Tramite
	 * 
	 * @param dirigidoA Usuario al cual va dirigido el Tramite
	 */
	public void setDirigidoA(Usuario dirigidoA) {

		this.dirigidoA = dirigidoA;
	}
	
	public String getIdDirigidoA() {
        return idDirigidoA;
    }

    public void setIdDirigidoA(String idDirigidoA) {
        this.idDirigidoA = idDirigidoA;
    }

    /**
	 * Obtiene la Descripcion del Cargo del Firmante
	 * 
	 * @return Descripcion del Cargo del Firmante
	 */
	public String getFirmanteCargo() {

		return firmanteCargo;
	}

	/**
	 * Asigna la Descripcion del Cargo del Firmante
	 * 
	 * @param firmanteCargo Descripcion del Cargo del Firmante
	 */
	public void setFirmanteCargo(String firmanteCargo) {

		this.firmanteCargo = firmanteCargo;
	}

	/**
	 * Obtiene el Usuario firmante del Asunto / Tramite
	 * 
	 * @return Usuario firmante del Asunto / Tramite
	 */
	public Firmante getFirmante() {

		return firmante;
	}

	/**
	 * Asigna el Usuario firmante del Asunto / Tramite
	 * 
	 * @param firmante Usuario firmante del Asunto / Tramite
	 */
	public void setFirmante(Firmante firmante) {

		this.firmante = firmante;
	}

	/**
	 * @return the idFirmante
	 */
	@JsonIgnore
	public String getIdFirmante() {

		return idFirmante;
	}

	/**
	 * @param idFirmante the idFirmante to set
	 */
	@JsonProperty("idFirmante")
	public void setIdFirmante(String idFirmante) {

		this.idFirmante = idFirmante;
	}

	/**
	 * Obtiene el Tipo de Registro
	 * 
	 * @return Tipo de Registro
	 */
	public TipoRegistro getTipoRegistro() {

		return tipoRegistro;
	}

	/**
	 * Asigna el Tipo de Registro
	 * 
	 * @param tipoRegistro Tipo de Registro
	 */
	public void setTipoRegistro(TipoRegistro tipoRegistro) {

		this.tipoRegistro = tipoRegistro;
	}

	/**
	 * Obtiene el Remitente o Area del Asunto / Turno
	 * 
	 * @return Remitente o Area del Asunto / Turno
	 */
	public Remitente getRemitente() {

		return remitente;
	}

	/**
	 * Asigna el Remitente o Area del Asunto / Turno
	 * 
	 * @param remitente Remitente o Area del Asunto / Turno
	 */
	public void setRemitente(Remitente remitente) {

		this.remitente = remitente;
	}

	/**
	 * @return the idRemitente
	 */
	@JsonIgnore
	public Integer getIdRemitente() {
		return idRemitente;
	}

	/**
	 * @param idRemitente the idRemitente to set
	 */
	@JsonProperty("idRemitente")
	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	/**
	 * @return the ciudadanos
	 */
	public List<AsuntoCiudadano> getCiudadanos() {
		return ciudadanos;
	}

	/**
	 * @param ciudadanos the ciudadanos to set
	 */
	public void setCiudadanos(List<AsuntoCiudadano> ciudadanos) {
		this.ciudadanos = ciudadanos;
	}

	/**
	 * Obtiene el Identificador si el Numero de Oficio se genera Automatico o no
	 * 
	 * @return Identificador si el Numero de Oficio se genera Automatico o no
	 */
	@JsonIgnore
	public boolean isNumDoctoAuto() {

		return isNumDoctoAuto;
	}

	/**
	 * Asigna el Identificador si el Numero de Oficio se genera Automatico o no
	 * 
	 * @param isNumDoctoAuto Identificador si el Numero de Oficio se genera
	 *                       Automatico o no
	 */
	@JsonProperty("numDoctoAuto")
	public void setNumDoctoAuto(boolean isNumDoctoAuto) {

		this.isNumDoctoAuto = isNumDoctoAuto;
	}

	/**
	 * Obtiene el Identificador del Asunto Infomex
	 * 
	 * @return Identificador del Asunto Infomex
	 */
	public String getIdExterno() {

		return idExterno;
	}

	/**
	 * Asigna el Identificador del Asunto Infomex
	 * 
	 * @param idExterno Identificador del Asunto Infomex
	 */
	public void setIdExterno(String idExterno) {

		this.idExterno = idExterno;
	}

	/**
	 * @return the tipo
	 */
	public Tipo getTipo() {
		return tipo;
	}

	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * @param clave the clave to set
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AsuntoDetalle [idAsuntoDetalle=" + idAsuntoDetalle + ", idProcedencia=" + idProcedencia + ", promotor="
				+ promotor + ", remitente=" + remitente + ", idRemitente=" + idRemitente + ", fechaRecepcion="
				+ fechaRecepcion + ", fechaElaboracion=" + fechaElaboracion + ", numDocto=" + numDocto + ", firmante="
				+ firmante + ", idFirmante=" + idFirmante + ", firmanteCargo=" + firmanteCargo + ", dirigidoA="
				+ dirigidoA + ", asuntoDescripcion=" + asuntoDescripcion + ", idExterno=" + idExterno
				+ ", tipoRegistro=" + tipoRegistro + ", dirigidoACargo=" + dirigidoACargo + ", confidencial="
				+ confidencial + ", folioIntermedio=" + folioIntermedio + ", palabraClave=" + palabraClave
				+ ", ciudadanos=" + ciudadanos + ", isNumDoctoAuto=" + isNumDoctoAuto + ", tipo=" + tipo + ", clave="
				+ clave + ", idFolioMultiple=" + idFolioMultiple + ", idDirigidoA=" + idDirigidoA + "]";
	}

	/**
	 * @return the isClaveAuto
	 */
	@JsonIgnore
	public boolean isClaveAuto() {
		return isClaveAuto;
	}

	/**
	 * @param isClaveAuto the isClaveAuto to set
	 */
	@JsonProperty("claveAuto")
	public void setClaveAuto(boolean isClaveAuto) {
		this.isClaveAuto = isClaveAuto;
	}

	/**
	 * @return the idFolioMultiple
	 */
	@JsonIgnore
	public Integer getIdFolioMultiple() {
		return idFolioMultiple;
	}

	/**
	 * @param idFolioMultiple the idFolioMultiple to set
	 */
	@JsonProperty("idFolioMultiple")
	public void setIdFolioMultiple(Integer idFolioMultiple) {
		this.idFolioMultiple = idFolioMultiple;
	}

}
