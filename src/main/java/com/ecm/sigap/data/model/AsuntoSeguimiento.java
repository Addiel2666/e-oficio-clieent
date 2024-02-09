/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.StatusAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.StatusAsuntoConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "ASUNTOSEGUIMIENTO")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoSeguimiento implements Serializable {

	/** */
	private static final long serialVersionUID = 3586641145966882171L;

	/** Identificador del Asunto */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** The id asunto origen. */
	@Column(name = "idAsuntoOrigen")
	private Integer idAsuntoOrigen;

	/** Identificador del Asunto padre */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;

	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

	/** The tipo asunto. */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** The folio area. */
	@Column(name = "folioArea")
	private String folioArea;

	/** Identificador si el Tramite esta en Tiempo o Fuera de Tiempo */
	@Column(name = "etft")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;

	/** The area. */
	@Column(name = "area")
	private String area;

	/** The titular area destino. */
	@Column(name = "titularArea")
	private String titularArea;

	/** Area a la que pertenece el Asunto */
	@OneToOne
	@JoinColumn(name = "AREADESTINO")
	@Fetch(FetchMode.SELECT)
	private Area areaDestino;

	/** Indicador si el Tipo de Instruccion requiere o no respuesta. */
	@Column(name = "INSTRUCCIONRR", length = 1)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuesta;
	
	/** The status turno. */
	@Column(name = "STATUS")
	@Convert(converter = StatusAsuntoConverter.class)
	private StatusAsunto statusTurno;

	/** Comentario de Rechazo del Tramite */
	@Column(name = "comentarioRechazo")
	private String comentarioRechazo;

	/** The num docto. */
	@Column(name = "numDocto")
	private String numDocto;

	/** The fecha elaboracion. */
	@Column(name = "fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracion;

	/** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

	/** The fecha envio. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** The fecha compromiso. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/** The asunto descripcion. */
	@Column(name = "asuntoDescripcion")
	private String asuntoDescripcion;

	/** The comentario. */
	@Column(name = "comentario")
	private String comentario;

	/** The promotor. */
	@Column(name = "promotor")
	private String promotor;

	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;

	/** */
	@OneToOne
	@JoinColumn(name = "ULTIMA_RESPUESTA_ID")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private RespuestaSeguimiento respuesta;

	@Transient
	private String level;

	/**
	 * @return the idAsunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * @param idAsunto the idAsunto to set
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * @return the idAsuntoPadre
	 */
	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	/**
	 * @param idAsuntoPadre the idAsuntoPadre to set
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	/**
	 * @return the tipoAsunto
	 */
	public TipoAsunto getTipoAsunto() {
		return tipoAsunto;
	}

	/**
	 * @param tipoAsunto the tipoAsunto to set
	 */
	public void setTipoAsunto(TipoAsunto tipoAsunto) {
		this.tipoAsunto = tipoAsunto;
	}

	/**
	 * @return the confidencial
	 */
	public Boolean getConfidencial() {
		return confidencial;
	}

	/**
	 * @param confidencial the confidencial to set
	 */
	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
	}

	/**
	 * @return the folioArea
	 */
	public String getFolioArea() {
		return folioArea;
	}

	/**
	 * @param folioArea the folioArea to set
	 */
	public void setFolioArea(String folioArea) {
		this.folioArea = folioArea;
	}

	/**
	 * @return the enTiempo
	 */
	public EnTiempo getEnTiempo() {
		return enTiempo;
	}

	/**
	 * @param enTiempo the enTiempo to set
	 */
	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
	}

	/**
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * @param area the area to set
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * @return the requiereRespuesta
	 */
	public Boolean getRequiereRespuesta() {
		return requiereRespuesta;
	}

	/**
	 * @param requiereRespuesta the requiereRespuesta to set
	 */
	public void setRequiereRespuesta(Boolean requiereRespuesta) {
		this.requiereRespuesta = requiereRespuesta;
	}

	/**
	 * @return the idAsuntoOrigen
	 */
	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	/**
	 * @param idAsuntoOrigen the idAsuntoOrigen to set
	 */
	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
	}

	/**
	 * @return the comentarioRechazo
	 */
	public String getComentarioRechazo() {
		return comentarioRechazo;
	}

	/**
	 * @param comentarioRechazo the comentarioRechazo to set
	 */
	public void setComentarioRechazo(String comentarioRechazo) {
		this.comentarioRechazo = comentarioRechazo;
	}

	/**
	 * @return the respuesta
	 */
	public RespuestaSeguimiento getRespuesta() {
		return respuesta;
	}

	/**
	 * @param respuesta the respuesta to set
	 */
	public void setRespuesta(RespuestaSeguimiento respuesta) {
		this.respuesta = respuesta;
	}

	/**
	 * @return the numDocto
	 */
	public String getNumDocto() {
		return numDocto;
	}

	/**
	 * @param numDocto the numDocto to set
	 */
	public void setNumDocto(String numDocto) {
		this.numDocto = numDocto;
	}

	/**
	 * @return the fechaElaboracion
	 */
	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	/**
	 * @param fechaElaboracion the fechaElaboracion to set
	 */
	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	/**
	 * @return the fechaCompromiso
	 */
	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	/**
	 * @param fechaCompromiso the fechaCompromiso to set
	 */
	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	/**
	 * @return the fechaEnvio
	 */
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * @param fechaEnvio the fechaEnvio to set
	 */
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * @return the fechaAcuse
	 */
	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	/**
	 * @param fechaAcuse the fechaAcuse to set
	 */
	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	/**
	 * @return the asuntoDescripcion
	 */
	public String getAsuntoDescripcion() {
		return asuntoDescripcion;
	}

	/**
	 * @param asuntoDescripcion the asuntoDescripcion to set
	 */
	public void setAsuntoDescripcion(String asuntoDescripcion) {
		this.asuntoDescripcion = asuntoDescripcion;
	}

	/**
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * @param comentario the comentario to set
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	/**
	 * @return the promotor
	 */
	public String getPromotor() {
		return promotor;
	}

	/**
	 * @param promotor the promotor to set
	 */
	public void setPromotor(String promotor) {
		this.promotor = promotor;
	}

	/**
	 * @return the instruccionDescripcion
	 */
	public String getInstruccionDescripcion() {
		return instruccionDescripcion;
	}

	/**
	 * @param instruccionDescripcion the instruccionDescripcion to set
	 */
	public void setInstruccionDescripcion(String instruccionDescripcion) {
		this.instruccionDescripcion = instruccionDescripcion;
	}

	/**
	 * @return the titularArea
	 */
	public String getTitularArea() {
		return titularArea;
	}

	/**
	 * @param titularArea the titularArea to set
	 */
	public void setTitularArea(String titularArea) {
		this.titularArea = titularArea;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the areaDestino
	 */
	public Area getAreaDestino() {
		return areaDestino;
	}

	/**
	 * @param areaDestino the areaDestino to set
	 */
	public void setAreaDestino(Area areaDestino) {
		this.areaDestino = areaDestino;
	}

	/**
	 * @return the statusTurno
	 */
	public StatusAsunto getStatusTurno() {
		return statusTurno;
	}

	/**
	 * @param statusTurno the statusTurno to set
	 */
	public void setStatusTurno(StatusAsunto statusTurno) {
		this.statusTurno = statusTurno;
	}

}
