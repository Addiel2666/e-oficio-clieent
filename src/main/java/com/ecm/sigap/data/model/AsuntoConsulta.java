/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.SubTipoAsuntoToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * The Class AsuntoConsulta.
 *
 * @author Alfredo Morales
 * @version 1.0
 * 
 */
@Entity
@Table(name = "ASUNTOCONSULTARPLUS")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoConsulta implements Serializable {

	/** The Constant SIMPLE_DATE_FORMAT. */
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	/** The Constant SIMPLE_DATE_FORMAT_HORA. */
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT_HORA = new SimpleDateFormat("HH-mm-ss");

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -576700709893966621L;

	/** Identificador del Asunto. */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;
	
	/** The id area. */
	@Column(name = "idArea")
	private Integer idArea;
	
	/** The id tipo registro padre. */
	@Column(name = "idTipoRegistroPadre")
	private String idTipoRegistroPadre;
	
	/** The id area destino. */
	@Column(name = "idAreaDestino")
	private Integer idAreaDestino;
	
	/** The id dirigido A. */
	@Column(name = "idDirigidoA")
	private String idDirigidoA;
	
	/** The folio area asunto padre. */
	@Column(name = "folioAreaAsuntoPadre")
	private String folioAreaAsuntoPadre;
	
	/** The folio area. */
	@Column(name = "folioArea")
	private String folioArea;
	
	/** The folio intermedio. */
	@Column(name = "folioIntermedio")
	private String folioIntermedio;
	
	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;
	
	/** The num docto. */
	@Column(name = "numDocto")
	private String numDocto;
	
	/** The fecha elaboracion. */
	@Column(name = "fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracion;
	
	/** The fecha registro. */
	@Column(name = "fechaRegistro")
	@Type(type = "java.util.Date")
	private Date fechaRegistro;
	
	/** The asunto descripcion. */
	@Column(name = "asuntoDescripcion")
	private String asuntoDescripcion;
	
	/** The firmante asunto. */
	@Column(name = "firmanteAsunto")
	private String firmanteAsunto;
	
	/** The firmante cargo. */
	@Column(name = "firmanteCargo")
	private String firmanteCargo;
	
	/** The id remitente. */
	@Column(name = "idRemitente")
	private Integer idRemitente;
	
	/** The id promotor. */
	@Column(name = "idPromotor")
	private Integer idPromotor;
	
	/** The promotor. */
	@Column(name = "promotor")
	private String promotor;
	
	/** The tipo asunto. */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;
	
	/** The id tipo registro. */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;
	
	/** The area. */
	@Column(name = "area")
	private String area;
	
	/** The id status turno. */
	@Column(name = "idStatusTurno")
	private Integer idStatusTurno;

	/** The status turno. */
	@Column(name = "statusTurno")
	private String statusTurno;

	/** The id status asunto. */
	@Column(name = "idStatusAsunto")
	private Integer idStatusAsunto;

	/** The status asunto. */
	@Column(name = "statusAsunto")
	private String statusAsunto;
	
	/** The prioridad descripcion. */
	@Column(name = "PRIORIDADDESC")
	private String prioridadDescripcion;
	
	/** The remitente. */
	@Column(name = "remitente")
	private String remitente;
	
	/** The fecha compromiso. */
	@Column(name = "fechaAcuse")
	@Type(type = "java.util.Date")
	private Date fechaAcuse;
	
	/** The descripcion tipo documento. */
	@Column(name = "descTipoDocumento")
	private String descripcionTipoDocumento;
	
	/** The fecha recepcion. */
	@Column(name = "fechaRecepcion")
	@Type(type = "java.util.Date")
	private Date fechaRecepcion;
	
	/** The area destino. */
	@Column(name = "areaDestino")
	private String areaDestino;
	
	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;
	
	/** The id destinatario. */
	@Column(name = "idDestinatario")
	private String idDestinatario;
	
	/** The tipo asunto. */
	@Column(name = "idSubTipoAsunto")
	@Convert(converter = SubTipoAsuntoToStringConverter.class)
	private SubTipoAsunto subTipoAsunto;
	
	/** Identificador del Asunto Padre. */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;
	
	/** The id firmante. */
	@Column(name = "idFirmante")
	private String idFirmante;
	
	/** The id tema. */
	@Column(name = "idTema")
	private Integer idTema;

	/** The id sub tema. */
	@Column(name = "idSubTema")
	private Integer idSubTema;

	/** The id evento. */
	@Column(name = "idEvento")
	private Integer idEvento;
	
	/** The documentos count. */
	@Column(name = "documentosCount")
	private Integer documentosAdjuntos;
	
	/** las respuestas enviadas por el tramite. */
	@Column(name = "respuestasEnviadas")
	private Integer respuestasEnviadas;
	
	/** The en tiempo. */
	@Column(name = "EtFt")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;
	
	/** The titular area destino. */
	@Column(name = "titularAreaDestino")
	private String titularAreaDestino;

	/** The cargo titular area destino. */
	@Column(name = "cargoTitularAreaDestino")
	private String cargoTitularAreaDestino;
	
	/** The comentario. */
	@Column(name = "comentario")
	private String comentario;
	
	/** The especialsn. */
	@Column(name = "especialsn")
	private String especialsn;
	
	/** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

	/** The fecha envio. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;
	
		/**
	 * Gets the id asunto.
	 *
	 * @return the id asunto
	 */
	public Integer getIdAsunto() {
		return idAsunto;
	}

	/**
	 * Sets the id asunto.
	 *
	 * @param idAsunto the new id asunto
	 */
	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	/**
	 * Gets the id asunto padre.
	 *
	 * @return the id asunto padre
	 */
	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	/**
	 * Sets the id asunto padre.
	 *
	 * @param idAsuntoPadre the new id asunto padre
	 */
	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	/**
	 * Gets the prioridad descripcion.
	 *
	 * @return the prioridadDescripcion
	 */
	public String getPrioridadDescripcion() {
		return prioridadDescripcion;
	}

	/**
	 * Sets the prioridad descripcion.
	 *
	 * @param prioridadDescripcion the prioridadDescripcion to set
	 */
	public void setPrioridadDescripcion(String prioridadDescripcion) {
		this.prioridadDescripcion = prioridadDescripcion;
	}

	/**
	 * Gets the instruccion descripcion.
	 *
	 * @return the instruccionDescripcion
	 */
	public String getInstruccionDescripcion() {
		return instruccionDescripcion;
	}

	/**
	 * Sets the instruccion descripcion.
	 *
	 * @param instruccionDescripcion the instruccionDescripcion to set
	 */
	public void setInstruccionDescripcion(String instruccionDescripcion) {
		this.instruccionDescripcion = instruccionDescripcion;
	}

	

	/**
	 * Gets the folio area.
	 *
	 * @return the folio area
	 */
	public String getFolioArea() {
		return folioArea;
	}

	/**
	 * Sets the folio area.
	 *
	 * @param folioArea the new folio area
	 */
	public void setFolioArea(String folioArea) {
		this.folioArea = folioArea;
	}

	/**
	 * Gets the comentario.
	 *
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}

	/**
	 * Sets the comentario.
	 *
	 * @param comentario the new comentario
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	/**
	 * Gets the num docto.
	 *
	 * @return the num docto
	 */
	public String getNumDocto() {
		return numDocto;
	}

	/**
	 * Sets the num docto.
	 *
	 * @param numDocto the new num docto
	 */
	public void setNumDocto(String numDocto) {
		this.numDocto = numDocto;
	}

	/**
	 * Gets the fecha elaboracion.
	 *
	 * @return the fecha elaboracion
	 */
	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	/**
	 * Sets the fecha elaboracion.
	 *
	 * @param fechaElaboracion the new fecha elaboracion
	 */
	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	/**
	 * Gets the fecha registro.
	 *
	 * @return the fecha registro
	 */
	public Date getFechaRegistro() {
		return fechaRegistro;
	}

	/**
	 * Sets the fecha registro.
	 *
	 * @param fechaRegistro the new fecha registro
	 */
	public void setFechaRegistro(Date fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	/**
	 * Gets the fecha compromiso.
	 *
	 * @return the fecha compromiso
	 */
	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	/**
	 * Sets the fecha compromiso.
	 *
	 * @param fechaCompromiso the new fecha compromiso
	 */
	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	/**
	 * Gets the fecha envio.
	 *
	 * @return the fecha envio
	 */
	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	/**
	 * Sets the fecha envio.
	 *
	 * @param fechaEnvio the new fecha envio
	 */
	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	/**
	 * Gets the fecha acuse.
	 *
	 * @return the fecha acuse
	 */
	public Date getFechaAcuse() {
		return fechaAcuse;
	}

	/**
	 * Sets the fecha acuse.
	 *
	 * @param fechaAcuse the new fecha acuse
	 */
	public void setFechaAcuse(Date fechaAcuse) {
		this.fechaAcuse = fechaAcuse;
	}

	/**
	 * Gets the asunto descripcion.
	 *
	 * @return the asunto descripcion
	 */
	public String getAsuntoDescripcion() {
		return asuntoDescripcion;
	}

	/**
	 * Sets the asunto descripcion.
	 *
	 * @param asuntoDescripcion the new asunto descripcion
	 */
	public void setAsuntoDescripcion(String asuntoDescripcion) {
		this.asuntoDescripcion = asuntoDescripcion;
	}

	/**
	 * Gets the id tipo registro.
	 *
	 * @return the id tipo registro
	 */
	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	/**
	 * Sets the id tipo registro.
	 *
	 * @param idTipoRegistro the new id tipo registro
	 */
	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	/**
	 * Gets the tipo asunto.
	 *
	 * @return the tipo asunto
	 */
	public TipoAsunto getTipoAsunto() {
		return tipoAsunto;
	}

	/**
	 * Sets the tipo asunto.
	 *
	 * @param tipoAsunto the new tipo asunto
	 */
	public void setTipoAsunto(TipoAsunto tipoAsunto) {
		this.tipoAsunto = tipoAsunto;
	}

	/**
	 * Gets the folio intermedio.
	 *
	 * @return the folio intermedio
	 */
	public String getFolioIntermedio() {
		return folioIntermedio;
	}

	/**
	 * Sets the folio intermedio.
	 *
	 * @param folioIntermedio the new folio intermedio
	 */
	public void setFolioIntermedio(String folioIntermedio) {
		this.folioIntermedio = folioIntermedio;
	}

	

	/**
	 * Gets the id status turno.
	 *
	 * @return the idStatusTurno
	 */
	public Integer getIdStatusTurno() {
		return idStatusTurno;
	}

	/**
	 * Sets the id status turno.
	 *
	 * @param idStatusTurno the idStatusTurno to set
	 */
	public void setIdStatusTurno(Integer idStatusTurno) {
		this.idStatusTurno = idStatusTurno;
	}

	/**
	 * Gets the status turno.
	 *
	 * @return the statusTurno
	 */
	public String getStatusTurno() {
		return statusTurno;
	}

	/**
	 * Sets the status turno.
	 *
	 * @param statusTurno the statusTurno to set
	 */
	public void setStatusTurno(String statusTurno) {
		this.statusTurno = statusTurno;
	}

	/**
	 * Gets the id status asunto.
	 *
	 * @return the idStatusAsunto
	 */
	public Integer getIdStatusAsunto() {
		return idStatusAsunto;
	}

	/**
	 * Sets the id status asunto.
	 *
	 * @param idStatusAsunto the idStatusAsunto to set
	 */
	public void setIdStatusAsunto(Integer idStatusAsunto) {
		this.idStatusAsunto = idStatusAsunto;
	}

	/**
	 * Gets the status asunto.
	 *
	 * @return the statusAsunto
	 */
	public String getStatusAsunto() {
		return statusAsunto;
	}

	/**
	 * Sets the status asunto.
	 *
	 * @param statusAsunto the statusAsunto to set
	 */
	public void setStatusAsunto(String statusAsunto) {
		this.statusAsunto = statusAsunto;
	}

	/**
	 * Gets the en tiempo.
	 *
	 * @return the en tiempo
	 */
	public EnTiempo getEnTiempo() {
		return enTiempo;
	}

	/**
	 * Sets the en tiempo.
	 *
	 * @param enTiempo the new en tiempo
	 */
	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
	}

	/**
	 * Gets the id remitente.
	 *
	 * @return the id remitente
	 */
	public Integer getIdRemitente() {
		return idRemitente;
	}

	/**
	 * Sets the id remitente.
	 *
	 * @param idRemitente the new id remitente
	 */
	public void setIdRemitente(Integer idRemitente) {
		this.idRemitente = idRemitente;
	}

	/**
	 * Gets the remitente.
	 *
	 * @return the remitente
	 */
	public String getRemitente() {
		return remitente;
	}

	/**
	 * Sets the remitente.
	 *
	 * @param remitente the new remitente
	 */
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	/**
	 * Gets the id area destino.
	 *
	 * @return the id area destino
	 */
	public Integer getIdAreaDestino() {
		return idAreaDestino;
	}

	/**
	 * Sets the id area destino.
	 *
	 * @param idAreaDestino the new id area destino
	 */
	public void setIdAreaDestino(Integer idAreaDestino) {
		this.idAreaDestino = idAreaDestino;
	}

	/**
	 * Gets the area destino.
	 *
	 * @return the area destino
	 */
	public String getAreaDestino() {
		return areaDestino;
	}

	/**
	 * Sets the area destino.
	 *
	 * @param areaDestino the new area destino
	 */
	public void setAreaDestino(String areaDestino) {
		this.areaDestino = areaDestino;
	}

	

	/**
	 * Gets the titular area destino.
	 *
	 * @return the titular area destino
	 */
	public String getTitularAreaDestino() {
		return titularAreaDestino;
	}

	/**
	 * Sets the titular area destino.
	 *
	 * @param titularAreaDestino the new titular area destino
	 */
	public void setTitularAreaDestino(String titularAreaDestino) {
		this.titularAreaDestino = titularAreaDestino;
	}

	/**
	 * Gets the cargo titular area destino.
	 *
	 * @return the cargo titular area destino
	 */
	public String getCargoTitularAreaDestino() {
		return cargoTitularAreaDestino;
	}

	/**
	 * Sets the cargo titular area destino.
	 *
	 * @param cargoTitularAreaDestino the new cargo titular area destino
	 */
	public void setCargoTitularAreaDestino(String cargoTitularAreaDestino) {
		this.cargoTitularAreaDestino = cargoTitularAreaDestino;
	}

	/**
	 * Gets the id area.
	 *
	 * @return the id area
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * Sets the id area.
	 *
	 * @param idArea the new id area
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public String getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area the new area
	 */
	public void setArea(String area) {
		this.area = area;
	}

	/**
	 * Gets the id promotor.
	 *
	 * @return the id promotor
	 */
	public Integer getIdPromotor() {
		return idPromotor;
	}

	/**
	 * Sets the id promotor.
	 *
	 * @param idPromotor the new id promotor
	 */
	public void setIdPromotor(Integer idPromotor) {
		this.idPromotor = idPromotor;
	}

	/**
	 * Gets the promotor.
	 *
	 * @return the promotor
	 */
	public String getPromotor() {
		return promotor;
	}

	/**
	 * Sets the promotor.
	 *
	 * @param promotor the new promotor
	 */
	public void setPromotor(String promotor) {
		this.promotor = promotor;
	}

	/**
	 * Gets the id firmante.
	 *
	 * @return the id firmante
	 */
	public String getIdFirmante() {
		return idFirmante;
	}

	/**
	 * Sets the id firmante.
	 *
	 * @param idFirmante the new id firmante
	 */
	public void setIdFirmante(String idFirmante) {
		this.idFirmante = idFirmante;
	}

	public String getFirmanteAsunto() {
		return firmanteAsunto;
	}

	public void setFirmanteAsunto(String firmanteAsunto) {
		this.firmanteAsunto = firmanteAsunto;
	}

	public String getFirmanteCargo() {
		return firmanteCargo;
	}

	public void setFirmanteCargo(String firmanteCargo) {
		this.firmanteCargo = firmanteCargo;
	}

	/**
	 * Gets the id dirigido A.
	 *
	 * @return the id dirigido A
	 */
	public String getIdDirigidoA() {
		return idDirigidoA;
	}

	/**
	 * Sets the id dirigido A.
	 *
	 * @param idDirigidoA the new id dirigido A
	 */
	public void setIdDirigidoA(String idDirigidoA) {
		this.idDirigidoA = idDirigidoA;
	}

	

	/**
	 * Gets the id tema.
	 *
	 * @return the id tema
	 */
	public Integer getIdTema() {
		return idTema;
	}

	/**
	 * Sets the id tema.
	 *
	 * @param idTema the new id tema
	 */
	public void setIdTema(Integer idTema) {
		this.idTema = idTema;
	}

	/**
	 * Gets the id sub tema.
	 *
	 * @return the id sub tema
	 */
	public Integer getIdSubTema() {
		return idSubTema;
	}

	/**
	 * Sets the id sub tema.
	 *
	 * @param idSubTema the new id sub tema
	 */
	public void setIdSubTema(Integer idSubTema) {
		this.idSubTema = idSubTema;
	}

	/**
	 * Gets the id evento.
	 *
	 * @return the id evento
	 */
	public Integer getIdEvento() {
		return idEvento;
	}

	/**
	 * Sets the id evento.
	 *
	 * @param idEvento the new id evento
	 */
	public void setIdEvento(Integer idEvento) {
		this.idEvento = idEvento;
	}

	

	/**
	 * Gets the documentos adjuntos.
	 *
	 * @return the documentos adjuntos
	 */
	public Integer getDocumentosAdjuntos() {
		return documentosAdjuntos;
	}

	/**
	 * Sets the documentos adjuntos.
	 *
	 * @param documentosAdjuntos the new documentos adjuntos
	 */
	public void setDocumentosAdjuntos(Integer documentosAdjuntos) {
		this.documentosAdjuntos = documentosAdjuntos;
	}

	

	/**
	 * Gets the folio area asunto padre.
	 *
	 * @return the folio area asunto padre
	 */
	public String getFolioAreaAsuntoPadre() {
		return folioAreaAsuntoPadre;
	}

	/**
	 * Sets the folio area asunto padre.
	 *
	 * @param folioAreaAsuntoPadre the new folio area asunto padre
	 */
	public void setFolioAreaAsuntoPadre(String folioAreaAsuntoPadre) {
		this.folioAreaAsuntoPadre = folioAreaAsuntoPadre;
	}

	

	/**
	 * Gets the id tipo registro padre.
	 *
	 * @return the id tipo registro padre
	 */
	public String getIdTipoRegistroPadre() {
		return idTipoRegistroPadre;
	}

	/**
	 * Sets the id tipo registro padre.
	 *
	 * @param idTipoRegistroPadre the new id tipo registro padre
	 */
	public void setIdTipoRegistroPadre(String idTipoRegistroPadre) {
		this.idTipoRegistroPadre = idTipoRegistroPadre;
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
	 * Gets the especialsn.
	 *
	 * @return the especialsn
	 */
	public String getEspecialsn() {
		return especialsn;
	}

	/**
	 * Sets the especialsn.
	 *
	 * @param especialsn the new especialsn
	 */
	public void setEspecialsn(String especialsn) {
		this.especialsn = especialsn;
	}

	/**
	 * Gets the descripcion tipo documento.
	 *
	 * @return the descripcionTipoDocumento
	 */
	public String getDescripcionTipoDocumento() {
		return descripcionTipoDocumento;
	}

	/**
	 * Sets the descripcion tipo documento.
	 *
	 * @param descripcionTipoDocumento the descripcionTipoDocumento to set
	 */
	public void setDescripcionTipoDocumento(String descripcionTipoDocumento) {
		this.descripcionTipoDocumento = descripcionTipoDocumento;
	}

	/**
	 * Gets the fecha recepcion.
	 *
	 * @return the fechaRecepcion
	 */
	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	/**
	 * Sets the fecha recepcion.
	 *
	 * @param fechaRecepcion the fechaRecepcion to set
	 */
	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	

	/**
	 * Gets the sub tipo asunto.
	 *
	 * @return the subTipoAsunto
	 */
	public SubTipoAsunto getSubTipoAsunto() {
		return subTipoAsunto;
	}

	/**
	 * Sets the sub tipo asunto.
	 *
	 * @param subTipoAsunto the subTipoAsunto to set
	 */
	public void setSubTipoAsunto(SubTipoAsunto subTipoAsunto) {
		this.subTipoAsunto = subTipoAsunto;
	}

	/**
	 * Gets the id destinatario.
	 *
	 * @return the id destinatario
	 */
	public String getIdDestinatario() {
		return idDestinatario;
	}

	/**
	 * Sets the id destinatario.
	 *
	 * @param idDestinatario the new id destinatario
	 */
	public void setIdDestinatario(String idDestinatario) {
		this.idDestinatario = idDestinatario;
	}

	/**
	 * Gets the confidencial.
	 *
	 * @return the confidencial
	 */
	public Boolean getConfidencial() {
		return confidencial;
	}

	/**
	 * Sets the confidencial.
	 *
	 * @param confidencial the confidencial to set
	 */
	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
	}

	/**
	 * Retorna value plantilla.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String retornaValuePlantilla(String key) {
		try {
			switch (key) {
			case ("asunto.folioAsunto"):
				if (this.getFolioArea() == null)
					return "";
				else
					return this.getFolioArea();

			case ("asunto.numDoc"):
				if (this.getNumDocto() == null)
					return "";
				else
					return this.getNumDocto();

			case ("asunto.fechaElab"):
				if (this.getFechaElaboracion() == null)
					return "";
				else {
					return SIMPLE_DATE_FORMAT.format(this.getFechaElaboracion());
				}

			case ("asunto.fechaRegistro"):
				if (this.getFechaRegistro() == null)
					return "";
				else
					return SIMPLE_DATE_FORMAT.format(this.getFechaRegistro());

			case ("asunto.asuntoDescripcion"):
				if (this.getAsuntoDescripcion() == null)
					return "";
				else
					return this.getAsuntoDescripcion();

			case ("asunto.firmante.descripcion"):
				if (this.getFirmanteAsunto() == null)
					return "";
				else
					return this.getFirmanteAsunto();

			case ("asunto.firmanteCargo"):
				if (this.getFirmanteCargo() == null)
					return null;
				else
					return this.getFirmanteCargo();

			case ("asunto.idRemitente"):
				if (this.getIdRemitente() == null)
					return null;
				else
					return this.getIdRemitente().toString();

			case ("asunto.firmanteInst"):
				if (this.getPromotor() == null)
					return "";
				else
					return this.getPromotor();

			case ("asunto.tipoAsunto"):
				if (this.getTipoAsunto() == null)
					return "";
				else
					switch (this.getTipoAsunto().getValue()) {
					case "A":
						return "Asunto";
					case "C":
						return "Copia";
					case "T":
						return "Turno";
					case "E":
						return "Envio";

					default:
						return "";
					}

			case ("asunto.idTipoRegsitro"):
				if (this.getIdTipoRegistro() == null)
					return "";
				else {
					switch (this.getIdTipoRegistro()) {
					case "CONTROL_GESTION":
						return "Control de gestion";
					case "C":
						return "Control de gestion";
					case "T":
						return "Infomex";

					default:
						return "";
					}
				}

			case ("asunto.areaDescipcion"):
				if (this.getArea() == null)
					return "";
				else
					return this.getArea();

			case ("asunto.estado"):
				if (this.getStatusAsunto() == null)
					return "";
				else
					return this.getStatusAsunto();

			case ("asunto.categoria"):
				if (this.getTipoAsunto() == null)
					return "";
				else
					switch (this.getTipoAsunto().getValue()) {
					case "A":
						return "Asunto";
					case "C":
						return "Copia";
					case "T":
						return "Turno";
					case "E":
						return "Envio";

					default:
						return "";
					}

			case ("asunto.prioridadDescripcion"):
				if (this.getPrioridadDescripcion() == null)
					return "";
				else
					return this.getPrioridadDescripcion();

			case ("asunto.remitente"):
				if (this.getRemitente() == null)
					return "";
				else
					return this.getRemitente();

			case ("asunto.horaAcuse"):
				if (this.getFechaAcuse() == null)
					return "";
				else
					return SIMPLE_DATE_FORMAT_HORA.format(this.getFechaAcuse());

			case ("asunto.tipoDoc"):
				if (this.getDescripcionTipoDocumento() == null)
					return "";
				else
					return this.getDescripcionTipoDocumento();

			case ("asunto.numDocto"):
				if (this.getNumDocto() == null)
					return null;
				else
					return this.getNumDocto();

			case ("asunto.fechaRecepcion"):
				if (this.getFechaRecepcion() == null)
					return "";
				else
					return SIMPLE_DATE_FORMAT.format(this.getFechaRecepcion());

			case ("asunto.horaRecepcion"):
				if (this.getFechaRecepcion() == null)
					return "";
				else
					return SIMPLE_DATE_FORMAT_HORA.format(this.getFechaRecepcion());

			case ("asunto.vacio"):
				return "";

			case ("asunto.descripcionAreaDestino"):
				if (this.getAreaDestino() == null)
					return "";
				else
					return this.getAreaDestino();

			case ("asunto.descripcionInstruccion"):
				if (this.getInstruccionDescripcion() == null)
					return "";
				else
					return this.getInstruccionDescripcion();

			default:
				return "";
			}

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Gets the respuestas enviadas.
	 *
	 * @return the respuestasEnviadas
	 */
	public Integer getRespuestasEnviadas() {
		return respuestasEnviadas;
	}

	/**
	 * Sets the respuestas enviadas.
	 *
	 * @param respuestasEnviadas the respuestasEnviadas to set
	 */
	public void setRespuestasEnviadas(Integer respuestasEnviadas) {
		this.respuestasEnviadas = respuestasEnviadas;
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AsuntoConsulta [idAsunto=" + idAsunto + ", idAsuntoPadre=" + idAsuntoPadre + ", prioridadDescripcion="
				+ prioridadDescripcion + ", instruccionDescripcion=" + instruccionDescripcion + 
				", folioArea=" + folioArea + ", comentario=" + comentario + ", numDocto="
				+ numDocto + ", fechaElaboracion=" + fechaElaboracion + ", fechaRegistro=" + fechaRegistro
				+ ", fechaCompromiso=" + fechaCompromiso + ", fechaEnvio=" + fechaEnvio + ", fechaAcuse=" + fechaAcuse
				+ ", asuntoDescripcion=" + asuntoDescripcion + ", idTipoRegistro=" + idTipoRegistro + ", tipoAsunto="
				+ tipoAsunto + ", subTipoAsunto=" + subTipoAsunto + ", idDestinatario=" + idDestinatario
				+ ", folioIntermedio=" + folioIntermedio +", idStatusTurno="
				+ idStatusTurno + ", statusTurno=" + statusTurno + ", idStatusAsunto=" + idStatusAsunto
				+ ", statusAsunto=" + statusAsunto + ", enTiempo=" + enTiempo + ", idRemitente=" + idRemitente
				+ ", remitente=" + remitente + ", idAreaDestino=" + idAreaDestino + ", areaDestino=" + areaDestino
				+ ", titularAreaDestino=" + titularAreaDestino
				+ ", cargoTitularAreaDestino=" + cargoTitularAreaDestino + ", idArea=" + idArea + ", area=" + area
				+ ", idPromotor=" + idPromotor + ", promotor=" + promotor + ", idFirmante=" + idFirmante
				+ ", firmanteAsunto=" + firmanteAsunto + ", firmanteCargo=" + firmanteCargo + ", idDirigidoA="
				+ idDirigidoA + ", idTema="
				+ idTema + ", idSubTema=" + idSubTema + ", idEvento=" + idEvento +", documentosAdjuntos=" + documentosAdjuntos
				+ ", respuestasEnviadas=" + respuestasEnviadas
				+ ", folioAreaAsuntoPadre=" + folioAreaAsuntoPadre + ", idTipoRegistroPadre=" + idTipoRegistroPadre
				+ ", especialsn=" + especialsn + ", descripcionTipoDocumento=" + descripcionTipoDocumento
				+ ", fechaRecepcion=" + fechaRecepcion + ", confidencial=" + confidencial + "]";
	}

	
}
