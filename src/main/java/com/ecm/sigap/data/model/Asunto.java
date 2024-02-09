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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.ecm.sigap.data.audit.aspectj.IAuditLog;
import com.ecm.sigap.data.model.util.Antecedente;
import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.Timestamp;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "Asuntos")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SequenceGenerator(name = "SEQ_ASUNTOS", //
		schema = "{SIGAP_SCHEMA}", //
		sequenceName = "SECASUNTOS", //
		allocationSize = 1)
@NamedNativeQueries(value = {

		@NamedNativeQuery(name = "isAreaAlreadyTurnada", //
				query = " select count(*) as cntr from {SIGAP_SCHEMA}.asuntos " //
						+ " where idAsuntoOrigen = :idAsuntoOrigen " //
						+ " AND IDAREADESTINO = :idArea "),

		@NamedNativeQuery(name = "getContadoresOracle", //
				query = " select {SIGAP_SCHEMA}.CONTADORES(:USUARIO, :AREA) from DUAL"),

		@NamedNativeQuery(name = "getContadoresPostgreSql", //
				query = " select {SIGAP_SCHEMA}.CONTADORES(:USUARIO, :AREA)")

})
@SecondaryTables({ //
		@SecondaryTable(name = "asuntosCorrespondencia", //
				schema = "{SIGAP_SCHEMA}", //
				pkJoinColumns = @PrimaryKeyJoinColumn(name = "idAsunto", referencedColumnName = "idAsunto"))//
})
public class Asunto implements Serializable, IAuditLog {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 921486242197032275L;

	/** Identificador del Asunto */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ASUNTOS")
	@Column(name = "idAsunto")
	private Integer idAsunto;

	/** Tipo de prioridad que tiene el Asunto / Tramite */
	@OneToOne
	@JoinColumn(name = "idPrioridad")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private TipoPrioridad prioridad;

	/** Area a la que pertenece el Asunto */
	@OneToOne
	@JoinColumn(name = "idArea")
	@Fetch(FetchMode.SELECT)
	private Area area;

	/** Identificador del Asunto padre */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;

	/**
	 * Instruccion que tiene el Tramite
	 * {@link com.ecm.sigap.data.model.TipoInstruccion}
	 */
	@OneToOne
	@JoinColumn(name = "idInstruccion")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private TipoInstruccion instruccion;

	/** Detalle del Asunto {@link AsuntoDetalle} */
	@OneToOne
	@JoinColumn(name = "idasuntodetalle")
	@Cascade({ CascadeType.DETACH, CascadeType.LOCK, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.REPLICATE, CascadeType.SAVE_UPDATE })
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private AsuntoDetalle asuntoDetalle;

	/** Tipo de Asunto {@link com.ecm.sigap.data.model.util.TipoAsunto} */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;

	/** Identificador del Sub Tipo Asunto */
	@Column(name = "idSubTipoAsunto")
	private String idSubTipoAsunto;

	/** Fecha de registro del Asunto / Tramite */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;

	/** Fecha de compromiso del Tramite */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

	/** Fecha del envio del Asunto / Tramite */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;

	/** Fecha en la que se acepto el Tramite */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;

	/** Identificador del Folio del Area */
	@Column(name = "folioArea")
	@NotNull
	@NotEmpty
	private String folioArea;

	/** Identificador si el Tramite esta en Tiempo o Fuera de Tiempo */
	@Column(name = "etft")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;

	/** Identificador del Usuario Turnador del Tramite */
	@OneToOne
	@JoinColumn(name = "idturnador")
	@Fetch(FetchMode.SELECT)
	private Representante turnador;

	/** Identificador del Usuario Destinatario del Tramite */
	@Column(name = "iddestinatario")
	private String destinatario;

	/** Identificador del Estatus del Asunto */
	@OneToOne
	@JoinColumn(name = "idEstatusAsunto")
	@Fetch(FetchMode.SELECT)
	private Status statusAsunto;

	/** Area Destino del Tramite */
	@OneToOne
	@JoinColumn(name = "idAreaDestino")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Area areaDestino;

	/** Identificador si el Tramite esta en Tiempo o Fuera de Tiempo */
	@Column(name = "especialsn")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean especial;

	/** Comentario del Asunto */
	@Column(name = "comentario")
	private String comentario;

	/** Comentario de Rechazo del Tramite */
	@Column(name = "comentarioRechazo")
	private String comentarioRechazo;

	/** */
	@Column(name = "atributos")
	private String atributo;

	/** Instrucción adicional */
	@Column(name = "anotaciones")
	private String anotacion;

	/** Lista de Antecedentes del Asunto / Tramite */
	@ElementCollection(fetch = FetchType.EAGER, targetClass = Antecedente.class)
	@CollectionTable(name = "ANTECEDENTESMULTIPLES", //
			schema = "{SIGAP_SCHEMA}", //
			joinColumns = { @JoinColumn(name = "idAsunto") })
	@JoinColumn(name = "idAsunto", updatable = true)
	@Fetch(value = FetchMode.SUBSELECT)
	private List<Antecedente> antecedentes;

	/** */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;

	/** */
	@Column(name = "idResponsable")
	private String responsable;

	/** Identificador del Objeto en el Repositorio */
	@Column(name = "contentId")
	private String contentId;

	/**
	 * Tipo de Expediente asociado al Asunto / Tramite {@link TipoExpediente}
	 */
	@OneToOne
	@JoinColumn(name = "idExpediente")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private TipoExpediente tipoExpediente;

	/** Identificador del Estatus del Turno */
	@OneToOne
	@JoinColumn(name = "idEstatusTurno")
	@Fetch(FetchMode.SELECT)
	private Status statusTurno;

	/** */
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "asuntoTimestamps", //
			schema = "{SIGAP_SCHEMA}", //
			joinColumns = { @JoinColumn(name = "idAsunto") })
	@JoinColumn(name = "idAsunto")
	@Cascade(CascadeType.ALL)
	@Fetch(value = FetchMode.SUBSELECT)
	@JsonIgnore
	private List<Timestamp> timestamps;

	/** Usuario al que fue asignado el Asunto */
	@OneToOne(targetEntity = Usuario.class)
	@JoinColumn(name = "asignadoA")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Usuario asignadoA;

	/** Tipo de Documento {@link TipoDocumento} */
	@OneToOne
	@JoinColumn(name = "idTipoDocumento", //
			table = "asuntosCorrespondencia", //
			nullable = true, //
			referencedColumnName = "idTipoDocumento")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private TipoDocumento tipoDocumento;

	/** Identificador del Area */
	@Column(name = "idArea", table = "asuntosCorrespondencia")
	private Integer idArea;

	/** Identificador del Expediente */
	@OneToOne
	@JoinColumn(name = "idExpediente", table = "asuntosCorrespondencia", nullable = true, referencedColumnName = "idExpediente")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private TipoExpediente expediente;

	/** */
	@OneToOne
	@JoinColumn(name = "idTema", table = "asuntosCorrespondencia", nullable = true, referencedColumnName = "idTema")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private Tema tema;

	/** Identificador del Evento */
	@OneToOne
	@JoinColumn(name = "idEvento", table = "asuntosCorrespondencia", nullable = true, referencedColumnName = "idEvento")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private TipoEvento evento;

	/** */
	@Type(type = "java.util.Date")
	@Column(name = "EVENTOFECHAHORA", table = "asuntosCorrespondencia", length = 7)
	private Date fechaEvento;

	/** */
	@OneToOne
	@JoinColumn(name = "idSubTema", table = "asuntosCorrespondencia", nullable = true, referencedColumnName = "idSubTema")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private SubTema subTema;

	/** id padre mas antiguo */
	@Column(name = "idAsuntoOrigen")
	private Integer idAsuntoOrigen;

	/** cuenta cuantos documentos tiene el asunto */
	@Formula(value = "{SIGAP_SCHEMA}.TRAMITEHASDOCS(idAsunto)")
	private Integer documentosAdjuntos;

	/** cuenta cuantos documentos tiene el asunto */
	@Formula(value = "{SIGAP_SCHEMA}.ASUNTOHASDOCPUBLISHED(idAsunto)")
	private Integer documentosPublicados;

	@Transient
	private Asunto asuntoPadre;

	/**
	 * Constructor por defecto de la clase
	 */
	public Asunto() {
		super();
	}

	/**
	 * Constructor de la clase
	 * 
	 * @param Identificador del Asunto
	 */
	public Asunto(int idAsunto) {

		this.idAsunto = idAsunto;
	}

	/**
	 * Obtiene el Identificador del Asunto
	 * 
	 * @return Identificador del Asunto
	 */
	public Integer getIdAsunto() {

		return idAsunto;
	}

	/**
	 * Asigna el Identificador del Asunto
	 * 
	 * @param idAsunto Identificador del Asunto
	 */
	public void setIdAsunto(Integer idAsunto) {

		this.idAsunto = idAsunto;
	}

	/**
	 * Obtiene el Tipo de Asunto
	 * 
	 * @return Tipo de Asunto
	 */
	public TipoAsunto getTipoAsunto() {

		return tipoAsunto;
	}

	/**
	 * Asigna el Tipo de Asunto
	 * 
	 * @param tipoAsunto Tipo de Asunto
	 */
	public void setTipoAsunto(TipoAsunto tipoAsunto) {

		this.tipoAsunto = tipoAsunto;
	}

	/**
	 * Obtiene la Fecha de registro del Asunto / Tramite
	 * 
	 * @return Fecha de registro del Asunto / Tramite
	 */
	public Date getFechaRegistro() {

		return fechaRegistro;
	}

	/**
	 * Asigna la Fecha de registro del Asunto / Tramite
	 * 
	 * @param fechaRegistro Fecha de registro del Asunto / Tramite
	 */
	public void setFechaRegistro(Date fechaRegistro) {

		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Obtiene la Fecha de compromiso del Tramite
	 * 
	 * @return Fecha de compromiso del Tramite
	 */
	public Date getFechaCompromiso() {

		return fechaCompromiso;
	}

	/**
	 * Asigna la Fecha de compromiso del Tramite
	 * 
	 * @param fechaCompromiso Fecha de compromiso del Tramite
	 */
	public void setFechaCompromiso(Date fechaCompromiso) {

		this.fechaCompromiso = fechaCompromiso;
	}

	/**
	 * Obtiene la Fecha del envio del Asunto / Tramite
	 * 
	 * @return Fecha del envio del Asunto / Tramite
	 */
	public Date getFechaEnvio() {

		return fechaEnvio;
	}

	/**
	 * Asigna la Fecha del envio del Asunto / Tramite
	 * 
	 * @param fechaEnvio Fecha del envio del Asunto / Tramite
	 */
	public void setFechaEnvio(Date fechaEnvio) {

		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Obtiene la Fecha en la que se acepto el Tramite
	 * 
	 * @return Fecha en la que se acepto el Tramite
	 */
	public Date getFechaAcuse() {

		return fechaAcuse;
	}

	/**
	 * Asigna la Fecha en la que se acepto el Tramite
	 * 
	 * @param fechaAcuse Fecha en la que se acepto el Tramite
	 */
	public void setFechaAcuse(Date fechaAcuse) {

		this.fechaAcuse = fechaAcuse;
	}

	/**
	 * @return the asuntoDetalle
	 */
	public AsuntoDetalle getAsuntoDetalle() {
		return asuntoDetalle;
	}

	/**
	 * @param asuntoDetalle the asuntoDetalle to set
	 */
	public void setAsuntoDetalle(AsuntoDetalle asuntoDetalle) {

		this.asuntoDetalle = asuntoDetalle;
	}

	/**
	 * Obtiene el Tipo de prioridad que tiene el Asunto / Tramite
	 * 
	 * @return Tipo de prioridad que tiene el Asunto / Tramite
	 */
	public TipoPrioridad getPrioridad() {

		return prioridad;
	}

	/**
	 * Asigna el Tipo de prioridad que tiene el Asunto / Tramite
	 * 
	 * @param prioridad Tipo de prioridad que tiene el Asunto / Tramite
	 */
	public void setPrioridad(TipoPrioridad prioridad) {

		this.prioridad = prioridad;
	}

	/**
	 * Obtiene el Area a la que pertenece el Asunto
	 * 
	 * @return Area a la que pertenece el Asunto
	 */
	public Area getArea() {

		return area;
	}

	/**
	 * Asigna el Area a la que pertenece el Asunto
	 * 
	 * @param area Area a la que pertenece el Asunto
	 */
	public void setArea(Area area) {

		this.area = area;
	}

	/**
	 * Obtiene el Identificador del Folio del Area
	 * 
	 * @return Identificador del Folio del Area
	 */
	public String getFolioArea() {

		return folioArea;
	}

	/**
	 * Asigna el Identificador del Folio del Area
	 * 
	 * @param folioArea Identificador del Folio del Area
	 */
	public void setFolioArea(String folioArea) {

		this.folioArea = folioArea;
	}

	/**
	 * @return the turnador
	 */
	public Representante getTurnador() {

		return turnador;
	}

	/**
	 * @param turnador the turnador to set
	 */
	public void setTurnador(Representante turnador) {

		this.turnador = turnador;
	}

	/**
	 * @return the destinatario
	 */
	public String getDestinatario() {

		return destinatario;
	}

	/**
	 * @param destinatario the destinatario to set
	 */
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
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
	 * Obtiene la Instruccion que tiene el Tramite
	 * 
	 * @return Instruccion que tiene el Tramite
	 */
	public TipoInstruccion getInstruccion() {

		return instruccion;
	}

	/**
	 * Asigna la Instruccion que tiene el Tramite
	 * 
	 * @param instruccion Instruccion que tiene el Tramite
	 */
	public void setInstruccion(TipoInstruccion instruccion) {

		this.instruccion = instruccion;
	}

	/**
	 * @return the contentId
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * @param contentId the contentId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	/**
	 * Obtiene el Identificador del Asunto padre
	 * 
	 * @return Identificador del Asunto padre
	 */
	public Integer getIdAsuntoPadre() {

		return idAsuntoPadre;
	}

	/**
	 * Asigna el Identificador del Asunto padre
	 * 
	 * @param idAsuntoPadre Identificador del Asunto padre
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {

		this.idAsuntoPadre = idAsuntoPadre;
	}

	/**
	 * @return the statusTurno
	 */
	public Status getStatusTurno() {
		return statusTurno;
	}

	/**
	 * @param statusTurno the statusTurno to set
	 */
	public void setStatusTurno(Status statusTurno) {
		this.statusTurno = statusTurno;
	}

	/**
	 * @return the statusAsunto
	 */
	public Status getStatusAsunto() {
		return statusAsunto;
	}

	/**
	 * @param statusAsunto the statusAsunto to set
	 */
	public void setStatusAsunto(Status statusAsunto) {
		this.statusAsunto = statusAsunto;
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
	 * Obtiene el Identificador del Sub Tipo Asunto
	 * 
	 * @return Identificador del Sub Tipo Asunto
	 */
	public String getIdSubTipoAsunto() {

		return idSubTipoAsunto;
	}

	/**
	 * Asigna el Identificador del Sub Tipo Asunto
	 * 
	 * @param idSubTipoAsunto Identificador del Sub Tipo Asunto
	 */
	public void setIdSubTipoAsunto(String idSubTipoAsunto) {

		this.idSubTipoAsunto = idSubTipoAsunto;
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
	 * @return the enTiempo
	 */
	public EnTiempo getEnTiempo() {
		// if (null == enTiempo) {
		// return EnTiempo.E;
		// }
		return enTiempo;
	}

	/**
	 * @param enTiempo the enTiempo to set
	 */
	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
	}

	/**
	 * @return the idTipoRegistro
	 */
	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	/**
	 * @param idTipoRegistro the idTipoRegistro to set
	 */
	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	/**
	 * @return the antecedentes
	 */
	public List<Antecedente> getAntecedentes() {
		return antecedentes;
	}

	/**
	 * @param antecedentes the antecedentes to set
	 */
	public void setAntecedentes(List<Antecedente> antecedentes) {
		this.antecedentes = antecedentes;
	}

	/**
	 * @return the timestamps
	 */
	public List<Timestamp> getTimestamps() {
		return timestamps;
	}

	/**
	 * @param timestamps the timestamps to set
	 */
	public void setTimestamps(List<Timestamp> timestamps) {
		this.timestamps = timestamps;
	}

	/**
	 * @return the tipoExpediente
	 */
	public TipoExpediente getTipoExpediente() {
		return tipoExpediente;
	}

	/**
	 * @param tipoExpediente the tipoExpediente to set
	 */
	public void setTipoExpediente(TipoExpediente tipoExpediente) {
		this.tipoExpediente = tipoExpediente;
	}

	/**
	 * @return the asignadoA
	 */
	public Usuario getAsignadoA() {
		return asignadoA;
	}

	/**
	 * @param asignadoA the asignadoA to set
	 */
	public void setAsignadoA(Usuario asignadoA) {
		this.asignadoA = asignadoA;
	}

	/**
	 * @return the especial
	 */
	public Boolean getEspecial() {
		return especial;
	}

	/**
	 * @param especial the especial to set
	 */
	public void setEspecial(Boolean especial) {
		this.especial = especial;
	}

	/**
	 * @return the atributo
	 */
	public String getAtributo() {
		return atributo;
	}

	/**
	 * @param atributo the atributo to set
	 */
	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}

	/**
	 * @return the anotacion
	 */
	public String getAnotacion() {
		return anotacion;
	}

	/**
	 * @param anotacion the anotacion to set
	 */
	public void setAnotacion(String anotacion) {
		this.anotacion = anotacion;
	}

	/**
	 * @return the responsable
	 */
	public String getResponsable() {
		return responsable;
	}

	/**
	 * @param responsable the responsable to set
	 */
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getIdAsuntoOrigen() {
		return idAsuntoOrigen;
	}

	/**
	 * 
	 * @param idAsuntoOrigen
	 */
	public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
		this.idAsuntoOrigen = idAsuntoOrigen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ecm.sigap.data.audit.aspectj.IAuditLog#getId()
	 */
	@Override
	@JsonIgnore
	public String getId() {
		return String.valueOf(this.idAsunto);
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
		
		if (this.getTipoAsunto().getValue().equals(TipoAsunto.ASUNTO.getValue())) {
			sb.append("Asunto").append("<br>");
			this.setIdAsuntoOrigen(idAsunto);
		} else {
			sb.append("Tramite").append("<br>");
		}
		
		sb.append("Síntesis: ").append( (asuntoDetalle != null) ? asuntoDetalle.getAsuntoDescripcion() : "null").append("<br>")
		.append("Área: ").append((area != null) ? area.getIdArea() : "null").append("<br>")
		.append("Folio Área: ").append((folioArea != null) ? folioArea : "null").append("<br>")
		.append("Asignadoa a: ").append( (asignadoA != null) ? asignadoA.getIdUsuario() : "null").append("<br>")
		.append("Instrucción: ").append((instruccion != null) ? instruccion.getDescripcion() : "null").append("<br>")
		.append("Fecha acuse: ").append(fechaAcuse).append("<br>")
		.append("Fecha compromiso: ").append(fechaCompromiso).append("<br>")
		.append("Turnador: ").append(turnador).append("<br>")
		.append("Destinatario: ").append(destinatario).append("<br>")
		.append("Responsable: ").append(responsable).append("<br>")
		.append("Categoría: ").append(tipoAsunto).append("<br>")
		.append("Status asunto: ").append( (statusAsunto != null) ? statusAsunto.getDescripcion() : "null").append("<br>")
		.append("Status turno: ").append((statusTurno != null) ? statusTurno.getDescripcion() : "null").append("<br>")
		.append("Tema: ").append( (tema != null) ? tema.getDescripcion() : "null").append("<br>")
		
		.append("Id asunto: ").append(idAsunto).append("<br>")
		.append("Id asunto padre: ").append(idAsuntoPadre).append("<br>")
		.append("Id área destino: ").append( (areaDestino != null) ? areaDestino.getIdArea() : "null").append("<br>")
		.append("Id detalle: ").append( (asuntoDetalle != null) ? asuntoDetalle.getIdAsuntoDetalle() : "null").append("<br>")
		.append("Id instrucción: ").append((instruccion != null) ? instruccion.getIdInstruccion() : "null").append("<br>")
		.append("Id origen: ").append(idAsuntoOrigen).append("<br>")
		.append("Id prioridad: ").append((prioridad != null) ? prioridad.getIdPrioridad() : "null").append("<br>")
		.append("Id status asunto: ").append((statusAsunto != null) ? statusAsunto.getIdStatus() : "null").append("<br>")
		.append("Id status turno: ").append((statusTurno != null) ? statusTurno.getIdStatus() : "null").append("<br>");

		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Asunto [idAsunto=" + idAsunto + ", prioridad=" + prioridad + ", area=" + area + ", idAsuntoPadre="
				+ idAsuntoPadre + ", instruccion=" + instruccion + ", asuntoDetalle=" + asuntoDetalle + ", tipoAsunto="
				+ tipoAsunto + ", idSubTipoAsunto=" + idSubTipoAsunto + ", fechaRegistro=" + fechaRegistro
				+ ", fechaCompromiso=" + fechaCompromiso + ", fechaEnvio=" + fechaEnvio + ", fechaAcuse=" + fechaAcuse
				+ ", folioArea=" + folioArea + ", enTiempo=" + enTiempo + ", turnador=" + turnador + ", destinatario="
				+ destinatario + ", statusAsunto=" + statusAsunto + ", areaDestino=" + areaDestino + ", especial="
				+ especial + ", comentario=" + comentario + ", comentarioRechazo=" + comentarioRechazo + ", atributo="
				+ atributo + ", anotacion=" + anotacion + ", antecedentes=" + antecedentes + ", idTipoRegistro="
				+ idTipoRegistro + ", responsable=" + responsable + ", contentId=" + contentId + ", tipoExpediente="
				+ tipoExpediente + ", statusTurno=" + statusTurno + ", timestamps=" + timestamps + ", asignadoA="
				+ asignadoA + "]";
	}

	/**
	 * @return the documentosAdjuntos
	 */
	public Integer getDocumentosAdjuntos() {
		return documentosAdjuntos;
	}

	/**
	 * @param documentosAdjuntos the documentosAdjuntos to set
	 */
	public void setDocumentosAdjuntos(Integer documentosAdjuntos) {
		this.documentosAdjuntos = documentosAdjuntos;
	}

	/**
	 * @return the tipoDocumento
	 */
	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	/**
	 * @param tipoDocumento the tipoDocumento to set
	 */
	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the expediente
	 */
	public TipoExpediente getExpediente() {
		return expediente;
	}

	/**
	 * @param expediente the expediente to set
	 */
	public void setExpediente(TipoExpediente expediente) {
		this.expediente = expediente;
	}

	/**
	 * @return the tema
	 */
	public Tema getTema() {
		return tema;
	}

	/**
	 * @param tema the tema to set
	 */
	public void setTema(Tema tema) {
		this.tema = tema;
	}

	/**
	 * @return the evento
	 */
	public TipoEvento getEvento() {
		return evento;
	}

	/**
	 * @param evento the evento to set
	 */
	public void setEvento(TipoEvento evento) {
		this.evento = evento;
	}

	/**
	 * @return the fechaEvento
	 */
	public Date getFechaEvento() {
		return fechaEvento;
	}

	/**
	 * @param fechaEvento the fechaEvento to set
	 */
	public void setFechaEvento(Date fechaEvento) {
		this.fechaEvento = fechaEvento;
	}

	/**
	 * @return the subTema
	 */
	public SubTema getSubTema() {
		return subTema;
	}

	/**
	 * @param subTema the subTema to set
	 */
	public void setSubTema(SubTema subTema) {
		this.subTema = subTema;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getDocumentosPublicados() {
		return documentosPublicados;
	}

	/**
	 * 
	 * @param documentosPublicados
	 */
	public void setDocumentosPublicados(Integer documentosPublicados) {
		this.documentosPublicados = documentosPublicados;
	}

	public Asunto getAsuntoPadre() {
		return asuntoPadre;
	}

	public void setAsuntoPadre(Asunto asuntoPadre) {
		this.asuntoPadre = asuntoPadre;
	}
}
