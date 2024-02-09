package com.ecm.sigap.ope.dao.model;

import java.util.Date;

import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.AsuntoDetalle;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.SubTema;
import com.ecm.sigap.data.model.Tema;
import com.ecm.sigap.data.model.TipoDocumento;
import com.ecm.sigap.data.model.TipoEvento;
import com.ecm.sigap.data.model.TipoExpediente;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.TipoPrioridad;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.data.model.util.Antecedente;
import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.ope.dao.model.tramite.StatusAsuntoTramite;
import com.ecm.sigap.ope.dao.model.tramite.Turnador;


public class EnvioTramites {
private Integer idAsunto;
private TipoPrioridad prioridad;
private Area area;
private Integer idAsuntoPadre;
private TipoInstruccion instruccion;
private AsuntoDetalle asuntoDetalle;
private TipoAsunto tipoAsunto;
private String idSubTipoAsunto;
private Date fechaRegistro;
private Date fechaCompromiso;
private Date fechaEnvio;
private Date fechaAcuse;
private String folioArea;
private EnTiempo enTiempo;
private Representante turnador;
private String destinatario;
private Status statusAsunto;
private Area areaDestino;
private Boolean especial;
private String comentario;
private String comentarioRechazo;
private String atributo;
private String anotacion;
private java.util.List<Antecedente> antecedentes;
private String idTipoRegistro;
private String responsable;
private String contentId;
private TipoExpediente tipoExpediente;
private Status statusTurno;
private Usuario asignadoA;
private TipoDocumento tipoDocumento;
private Integer idArea;
private TipoExpediente expediente;
private Tema tema;
private TipoEvento evento;
private Date fechaEvento;
private Integer documentosAdjuntos;
private Integer documentosPublicados;
private Asunto asuntoPadre;
private SubTema subTema;
private Integer idAsuntoOrigen;


public Integer getIdAsuntoOrigen() {
	return idAsuntoOrigen;
}
public void setIdAsuntoOrigen(Integer idAsuntoOrigen) {
	this.idAsuntoOrigen = idAsuntoOrigen;
}
public SubTema getSubTema() {
	return subTema;
}
public void setSubTema(SubTema subTema) {
	this.subTema = subTema;
}
public void setAreaDestino(Area areaDestino) {
	this.areaDestino = areaDestino;
}
public TipoAsunto getTipoAsunto() {
	return tipoAsunto;
}
public void setTipoAsunto(TipoAsunto tipoAsunto) {
	this.tipoAsunto = tipoAsunto;
}
public Integer getIdAsunto() {
	return idAsunto;
}
public void setIdAsunto(Integer idAsunto) {
	this.idAsunto = idAsunto;
}
public TipoPrioridad getPrioridad() {
	return prioridad;
}
public void setPrioridad(TipoPrioridad prioridad) {
	this.prioridad = prioridad;
}
public Area getArea() {
	return area;
}
public void setArea(Area area) {
	this.area = area;
}
public Integer getIdAsuntoPadre() {
	return idAsuntoPadre;
}
public void setIdAsuntoPadre(Integer idAsuntoPadre) {
	this.idAsuntoPadre = idAsuntoPadre;
}
public TipoInstruccion getInstruccion() {
	return instruccion;
}
public void setInstruccion(TipoInstruccion instruccion) {
	this.instruccion = instruccion;
}
public AsuntoDetalle getAsuntoDetalle() {
	return asuntoDetalle;
}
public void setAsuntoDetalle(AsuntoDetalle asuntoDetalle) {
	this.asuntoDetalle = asuntoDetalle;
}
public String getIdSubTipoAsunto() {
	return idSubTipoAsunto;
}
public void setIdSubTipoAsunto(String idSubTipoAsunto) {
	this.idSubTipoAsunto = idSubTipoAsunto;
}
public Date getFechaRegistro() {
	return fechaRegistro;
}
public void setFechaRegistro(Date fechaRegistro) {
	this.fechaRegistro = fechaRegistro;
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
public Date getFechaAcuse() {
	return fechaAcuse;
}
public void setFechaAcuse(Date fechaAcuse) {
	this.fechaAcuse = fechaAcuse;
}
public String getFolioArea() {
	return folioArea;
}
public void setFolioArea(String folioArea) {
	this.folioArea = folioArea;
}
public EnTiempo getEnTiempo() {
	return enTiempo;
}
public void setEnTiempo(EnTiempo enTiempo) {
	this.enTiempo = enTiempo;
}
public Representante getTurnador() {
	return turnador;
}
public void setTurnador(Representante turnador) {
	this.turnador = turnador;
}
public String getDestinatario() {
	return destinatario;
}
public void setDestinatario(String destinatario) {
	this.destinatario = destinatario;
}
public Status getStatusAsunto() {
	return statusAsunto;
}
public void setStatusAsunto(Status statusAsunto) {
	this.statusAsunto = statusAsunto;
}
public Area getAreaDestino() {
	return areaDestino;
}
public void setAreaPadre(Area areaDestino) {
	this.areaDestino = areaDestino;
}
public Boolean getEspecial() {
	return especial;
}
public void setEspecial(Boolean especial) {
	this.especial = especial;
}
public String getComentario() {
	return comentario;
}
public void setComentario(String comentario) {
	this.comentario = comentario;
}
public String getComentarioRechazo() {
	return comentarioRechazo;
}
public void setComentarioRechazo(String comentarioRechazo) {
	this.comentarioRechazo = comentarioRechazo;
}
public String getAtributo() {
	return atributo;
}
public void setAtributo(String atributo) {
	this.atributo = atributo;
}
public String getAnotacion() {
	return anotacion;
}
public void setAnotacion(String anotacion) {
	this.anotacion = anotacion;
}
public java.util.List<Antecedente> getAntecedentes() {
	return antecedentes;
}
public void setAntecedentes(java.util.List<Antecedente> antecedentes) {
	this.antecedentes = antecedentes;
}
public String getIdTipoRegistro() {
	return idTipoRegistro;
}
public void setIdTipoRegistro(String idTipoRegistro) {
	this.idTipoRegistro = idTipoRegistro;
}
public String getResponsable() {
	return responsable;
}
public void setResponsable(String responsable) {
	this.responsable = responsable;
}
public String getContentId() {
	return contentId;
}
public void setContentId(String contentId) {
	this.contentId = contentId;
}
public TipoExpediente getTipoExpediente() {
	return tipoExpediente;
}
public void setTipoExpediente(TipoExpediente tipoExpediente) {
	this.tipoExpediente = tipoExpediente;
}
public Status getStatusTurno() {
	return statusTurno;
}
public void setStatusTurno(Status statusTurno) {
	this.statusTurno = statusTurno;
}
public Usuario getAsignadoA() {
	return asignadoA;
}
public void setAsignadoA(Usuario asignadoA) {
	this.asignadoA = asignadoA;
}
public TipoDocumento getTipoDocumento() {
	return tipoDocumento;
}
public void setTipoDocumento(TipoDocumento tipoDocumento) {
	this.tipoDocumento = tipoDocumento;
}
public Integer getIdArea() {
	return idArea;
}
public void setIdArea(Integer idArea) {
	this.idArea = idArea;
}
public TipoExpediente getExpediente() {
	return expediente;
}
public void setExpediente(TipoExpediente expediente) {
	this.expediente = expediente;
}
public Tema getTema() {
	return tema;
}
public void setTema(Tema tema) {
	this.tema = tema;
}
public TipoEvento getEvento() {
	return evento;
}
public void setEvento(TipoEvento evento) {
	this.evento = evento;
}
public Date getFechaEvento() {
	return fechaEvento;
}
public void setFechaEvento(Date fechaEvento) {
	this.fechaEvento = fechaEvento;
}
public Integer getDocumentosAdjuntos() {
	return documentosAdjuntos;
}
public void setDocumentosAdjuntos(Integer documentosAdjuntos) {
	this.documentosAdjuntos = documentosAdjuntos;
}
public Integer getDocumentosPublicados() {
	return documentosPublicados;
}
public void setDocumentosPublicados(Integer documentosPublicados) {
	this.documentosPublicados = documentosPublicados;
}
public Asunto getAsuntoPadre() {
	return asuntoPadre;
}
public void setAsuntoPadre(Asunto asuntoPadre) {
	this.asuntoPadre = asuntoPadre;
}
@Override
public String toString() {
	return "EnvioTramites [idAsunto=" + idAsunto + ", prioridad=" + prioridad + ", area=" + area + ", idAsuntoPadre="
			+ idAsuntoPadre + ", instruccion=" + instruccion + ", asuntoDetalle=" + asuntoDetalle + ", tipoAsunto="
			+ tipoAsunto + ", idSubTipoAsunto=" + idSubTipoAsunto + ", fechaRegistro=" + fechaRegistro
			+ ", fechaCompromiso=" + fechaCompromiso + ", fechaEnvio=" + fechaEnvio + ", fechaAcuse=" + fechaAcuse
			+ ", folioArea=" + folioArea + ", enTiempo=" + enTiempo + ", turnador=" + turnador + ", destinatario="
			+ destinatario + ", statusAsunto=" + statusAsunto + ", areaPadre=" + areaDestino + ", especial=" + especial
			+ ", comentario=" + comentario + ", comentarioRechazo=" + comentarioRechazo + ", atributo=" + atributo
			+ ", anotacion=" + anotacion + ", antecedentes=" + antecedentes + ", idTipoRegistro=" + idTipoRegistro
			+ ", responsable=" + responsable + ", contentId=" + contentId + ", tipoExpediente=" + tipoExpediente
			+ ", statusTurno=" + statusTurno + ", asignadoA=" + asignadoA + ", tipoDocumento=" + tipoDocumento
			+ ", idArea=" + idArea + ", expediente=" + expediente + ", tema=" + tema + ", evento=" + evento
			+ ", fechaEvento=" + fechaEvento + ", documentosAdjuntos=" + documentosAdjuntos + ", documentosPublicados="
			+ documentosPublicados + ", asuntoPadre=" + asuntoPadre + "]";
}



}
