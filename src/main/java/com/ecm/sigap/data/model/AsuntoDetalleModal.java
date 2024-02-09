package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.util.BooleanToStringConverter;
import com.ecm.sigap.data.util.ETFTToStringConverter;
import com.ecm.sigap.data.util.TipoAsuntoToStringConverter;

/**
 * The Class AsuntoConsultaMasInfo.
 *
 * @version 1.0
 * 
 */

@Entity
@Table(name = "ASUNTODETALLEMODAL")
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "ECM_SIGAP_V_CACHE_REGION")
public class AsuntoDetalleModal implements Serializable{

	/** Identificador del Asunto. */
	@Id
	@Column(name = "idAsunto")
	private Integer idAsunto;
	
	/** Identificador del Asunto Padre. */
	@Column(name = "idAsuntoPadre")
	private Integer idAsuntoPadre;
	
	/** The instruccion descripcion. */
	@Column(name = "INSTRUCCIONDESC")
	private String instruccionDescripcion;
	
	/** Indicador tipo de instruccion requiere o no respuesta. */
	@Column(name = "INSTRUCCIONRR", length = 1)
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean requiereRespuesta;
	
	/** The prioridad descripcion. */
	@Column(name = "PRIORIDADDESC")
	private String prioridadDescripcion;
	
	/** The comentario. */
	@Column(name = "comentario")
	private String comentario;

	/** The num docto. */
	@Column(name = "numDocto")
	private String numDocto;
	
	/** The fecha elaboracion. */
	@Column(name = "fechaElaboracion")
	@Type(type = "java.util.Date")
	private Date fechaElaboracion;
	
	/** The fecha recepcion. */
	@Column(name = "fechaRecepcion")
	@Type(type = "java.util.Date")
	private Date fechaRecepcion;
	
	/** The fecha compromiso. */
	@Column(name = "fechaCompromiso")
	@Type(type = "java.util.Date")
	private Date fechaCompromiso;

	/** The fecha envio. */
	@Column(name = "fechaEnvio")
	@Type(type = "java.util.Date")
	private Date fechaEnvio;
	
	/** The asunto descripcion. */
	@Column(name = "asuntoDescripcion")
	private String asuntoDescripcion;
	
	/** The id tipo registro. */
	@Column(name = "idTipoRegistro")
	private String idTipoRegistro;
	
	/** The palabra clave. */
	@Column(name = "palabraClave")
	private String palabraClave;
	
	/** The en tiempo. */
	@Column(name = "EtFt")
	@Convert(converter = ETFTToStringConverter.class)
	private EnTiempo enTiempo;
	
	/** The area. */
	@Column(name = "area")
	private String area;
	
	/** The promotor. */
	@Column(name = "promotor")
	private String promotor;
	
	/** The remitente. */
	@Column(name = "remitente")
	private String remitente;
	
	/** The nombre empresa*/
	@Column(name = "nombreempresa")
	private String remitenteEmpresa;
	
	/** The tipo asunto. */
	@Column(name = "idTipoAsunto")
	@Convert(converter = TipoAsuntoToStringConverter.class)
	private TipoAsunto tipoAsunto;
	
	/** Identificador si el Asunto / Tramite es confidencial. */
	@Column(name = "confidencial")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean confidencial;

	public Integer getIdAsunto() {
		return idAsunto;
	}

	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

	public Integer getIdAsuntoPadre() {
		return idAsuntoPadre;
	}

	public void setIdAsuntoPadre(Integer idAsuntoPadre) {
		this.idAsuntoPadre = idAsuntoPadre;
	}

	public String getInstruccionDescripcion() {
		return instruccionDescripcion;
	}

	public void setInstruccionDescripcion(String instruccionDescripcion) {
		this.instruccionDescripcion = instruccionDescripcion;
	}

	public Boolean getRequiereRespuesta() {
		return requiereRespuesta;
	}

	public void setRequiereRespuesta(Boolean requiereRespuesta) {
		this.requiereRespuesta = requiereRespuesta;
	}

	public String getPrioridadDescripcion() {
		return prioridadDescripcion;
	}

	public void setPrioridadDescripcion(String prioridadDescripcion) {
		this.prioridadDescripcion = prioridadDescripcion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getNumDocto() {
		return numDocto;
	}

	public void setNumDocto(String numDocto) {
		this.numDocto = numDocto;
	}

	public Date getFechaElaboracion() {
		return fechaElaboracion;
	}

	public void setFechaElaboracion(Date fechaElaboracion) {
		this.fechaElaboracion = fechaElaboracion;
	}

	public Date getFechaRecepcion() {
		return fechaRecepcion;
	}

	public void setFechaRecepcion(Date fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}

	public Date getFechaCompromiso() {
		return fechaCompromiso;
	}

	public void setFechaCompromiso(Date fechaCompromiso) {
		this.fechaCompromiso = fechaCompromiso;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public String getAsuntoDescripcion() {
		return asuntoDescripcion;
	}

	public void setAsuntoDescripcion(String asuntoDescripcion) {
		this.asuntoDescripcion = asuntoDescripcion;
	}

	public String getIdTipoRegistro() {
		return idTipoRegistro;
	}

	public void setIdTipoRegistro(String idTipoRegistro) {
		this.idTipoRegistro = idTipoRegistro;
	}

	public String getPalabraClave() {
		return palabraClave;
	}

	public void setPalabraClave(String palabraClave) {
		this.palabraClave = palabraClave;
	}

	public EnTiempo getEnTiempo() {
		return enTiempo;
	}

	public void setEnTiempo(EnTiempo enTiempo) {
		this.enTiempo = enTiempo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getPromotor() {
		return promotor;
	}

	public void setPromotor(String promotor) {
		this.promotor = promotor;
	}

	public String getRemitente() {
		return remitente;
	}

	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}

	public String getRemitenteEmpresa() {
		return remitenteEmpresa;
	}

	public void setRemitenteEmpresa(String remitenteEmpresa) {
		this.remitenteEmpresa = remitenteEmpresa;
	}

	public TipoAsunto getTipoAsunto() {
		return tipoAsunto;
	}

	public void setTipoAsunto(TipoAsunto tipoAsunto) {
		this.tipoAsunto = tipoAsunto;
	}

	public Boolean getConfidencial() {
		return confidencial;
	}

	public void setConfidencial(Boolean confidencial) {
		this.confidencial = confidencial;
	}
	
	
		
}
