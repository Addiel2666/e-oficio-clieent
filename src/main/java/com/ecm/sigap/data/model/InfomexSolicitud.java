/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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

import com.ecm.sigap.data.model.validator.UniqueKey;

/**
 * The Class InfomexSolicitud.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "ifaiSisiSolicitudes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@UniqueKey(columnNames = { "infomexSolicitudKey.folioSisi",
		"infomexSolicitudKey.idInstitucion" }, message = "{Unique.descripcion}")
public class InfomexSolicitud implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1745609078912191347L;

	/** The Ifai sisi solicitud key. */
	@EmbeddedId
	private InfomexSolicitudKey infomexSolicitudKey;

	/** The us_unienl. */
	@Column(name = "us_unienl")
	private String us_unienl;

	/** */
	@Transient
	private String us_subEnl;

	/** The us_tipo. */
	@Column(name = "us_tipo")
	private String us_tipo;

	/** The us_fecrecepcion. */
	@Column(name = "us_fecRecepcion")
	@Type(type = "java.util.Date")
	private Date us_fecRecepcion;

	/** The us_replegal. */
	@Column(name = "us_repLegal")
	private String us_repLegal;

	/** The us_rfc. */
	@Column(name = "us_rfc")
	private String us_rfc;

	/** The us_apepat. */
	@Column(name = "us_apePat")
	private String us_apePat;

	/** The us_apemat. */
	@Column(name = "us_apeMat")
	private String us_apeMat;

	/** The us_nombre. */
	@Column(name = "us_nombre")
	private String us_nombre;

	/** The us_curp. */
	@Column(name = "us_curp")
	private String us_curp;

	/** The us_calle. */
	@Column(name = "us_calle")
	private String us_calle;

	/** The us_numext. */
	@Column(name = "us_numExt")
	private String us_numExt;

	/** The us_numint. */
	@Column(name = "us_numInt")
	private String us_numInt;

	/** The us_col. */
	@Column(name = "us_col")
	private String us_col;

	/** The ke_claest. */
	@Column(name = "ke_claEst")
	private String ke_claEst;

	/** The kmu_clamun. */
	@Column(name = "kmu_claMun")
	private String kmu_claMun;

	/** The us_codpos. */
	@Column(name = "us_codPos")
	private String us_codPos;

	/** The us_tel. */
	@Column(name = "us_tel")
	private String us_tel;

	/** The us_corele. */
	@Column(name = "us_corEle")
	private String us_corEle;

	/** The us_idpais. */
	@Column(name = "us_idPais")
	private String us_idPais;

	/** The us_edoext. */
	@Column(name = "us_edoExt")
	private String us_edoExt;

	/** The us_ciudadext. */
	@Column(name = "us_ciudadExt")
	private String us_ciudadExt;

	/** The us_sexo. */
	@Column(name = "us_sexo")
	private String us_sexo;

	/** The us_fecnac. */
	@Column(name = "us_fecNac")
	@Type(type = "java.util.Date")
	private Date us_fecNac;

	/**  */
	@Column(name = "us_ocupacion")
	private String us_ocupacion;

	/**  */
	@OneToOne
	@JoinColumn(name = "us_modEnt")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(value = FetchMode.SELECT)
	private InfomexModalidadEntrega modoEntrega;

	/** The us_otromod. */
	@Column(name = "us_otroMod")
	private String us_otroMod;

	/** The us_arcdes. */
	@Column(name = "us_arcDes")
	private String us_arcDes;

	/** The us_datdes. */
	@Column(name = "us_datDes")
	private String us_datDes;

	/** The us_otrosdatos. */
	@Column(name = "us_otrosDatos")
	private String us_otrosDatos;

	/**  */
	@OneToOne
	@JoinColumn(name = "idArchivo")
	@Cascade(value = { CascadeType.ALL })
	@Fetch(value = FetchMode.SELECT)
	private InfomexArchivo archivo;

	/** The descrespuesta. */
	@Column(name = "descRespuesta")
	private String descRespuesta;

	/** The status splicitud. */
	@OneToOne
	@JoinColumn(name = "ideStatusSolicitud")
	@Fetch(value = FetchMode.SELECT)
	private Status statuSolicitud;

	/** El folder en el repositorio donde se almacenan los archivos adjuntos. */
	@Column(name = "contentId")
	private String contentId;

	/** */
	@OneToOne
	@JoinColumn(name = "status")
	@NotFound(action = NotFoundAction.IGNORE)
	@Fetch(FetchMode.SELECT)
	private InfomexStatus status;

	/** */
	@Formula("{SIGAP_SCHEMA}.ASUNTOGENERADOINFOMEX(folioSisi)")
	private Integer idAsunto;

	/**
	 * Gets the infomex solicitud key.
	 *
	 * @return the infomex solicitud key
	 */
	public InfomexSolicitudKey getInfomexSolicitudKey() {
		return infomexSolicitudKey;
	}

	/**
	 * Sets the infomex solicitud key.
	 *
	 * @param infomexSolicitudKey the new infomex solicitud key
	 */
	public void setInfomexSolicitudKey(InfomexSolicitudKey infomexSolicitudKey) {
		this.infomexSolicitudKey = infomexSolicitudKey;
	}

	/**
	 * Gets the us_unienl.
	 *
	 * @return the us_unienl
	 */
	public String getUs_unienl() {
		return us_unienl;
	}

	/**
	 * Sets the us_unienl.
	 *
	 * @param us_unienl the new us_unienl
	 */
	public void setUs_unienl(String us_unienl) {
		this.us_unienl = us_unienl;
	}

	/**
	 * Gets the us_tipo.
	 *
	 * @return the us_tipo
	 */
	public String getUs_tipo() {
		return us_tipo;
	}

	/**
	 * Sets the us_tipo.
	 *
	 * @param us_tipo the new us_tipo
	 */
	public void setUs_tipo(String us_tipo) {
		this.us_tipo = us_tipo;
	}

	/**
	 * Gets the us_fec recepcion.
	 *
	 * @return the us_fec recepcion
	 */
	public Date getUs_fecRecepcion() {
		return us_fecRecepcion;
	}

	/**
	 * Sets the us_fec recepcion.
	 *
	 * @param us_fecRecepcion the new us_fec recepcion
	 */
	public void setUs_fecRecepcion(Date us_fecRecepcion) {
		this.us_fecRecepcion = us_fecRecepcion;
	}

	/**
	 * Gets the us_rep legal.
	 *
	 * @return the us_rep legal
	 */
	public String getUs_repLegal() {
		return us_repLegal;
	}

	/**
	 * Sets the us_rep legal.
	 *
	 * @param us_repLegal the new us_rep legal
	 */
	public void setUs_repLegal(String us_repLegal) {
		this.us_repLegal = us_repLegal;
	}

	/**
	 * Gets the us_rfc.
	 *
	 * @return the us_rfc
	 */
	public String getUs_rfc() {
		return us_rfc;
	}

	/**
	 * Sets the us_rfc.
	 *
	 * @param us_rfc the new us_rfc
	 */
	public void setUs_rfc(String us_rfc) {
		this.us_rfc = us_rfc;
	}

	/**
	 * Gets the us_ape pat.
	 *
	 * @return the us_ape pat
	 */
	public String getUs_apePat() {
		return us_apePat;
	}

	/**
	 * Sets the us_ape pat.
	 *
	 * @param us_apePat the new us_ape pat
	 */
	public void setUs_apePat(String us_apePat) {
		this.us_apePat = us_apePat;
	}

	/**
	 * Gets the us_ape mat.
	 *
	 * @return the us_ape mat
	 */
	public String getUs_apeMat() {
		return us_apeMat;
	}

	/**
	 * Sets the us_ape mat.
	 *
	 * @param us_apeMat the new us_ape mat
	 */
	public void setUs_apeMat(String us_apeMat) {
		this.us_apeMat = us_apeMat;
	}

	/**
	 * Gets the us_nombre.
	 *
	 * @return the us_nombre
	 */
	public String getUs_nombre() {
		return us_nombre;
	}

	/**
	 * Sets the us_nombre.
	 *
	 * @param us_nombre the new us_nombre
	 */
	public void setUs_nombre(String us_nombre) {
		this.us_nombre = us_nombre;
	}

	/**
	 * Gets the us_curp.
	 *
	 * @return the us_curp
	 */
	public String getUs_curp() {
		return us_curp;
	}

	/**
	 * Sets the us_curp.
	 *
	 * @param us_curp the new us_curp
	 */
	public void setUs_curp(String us_curp) {
		this.us_curp = us_curp;
	}

	/**
	 * Gets the us_calle.
	 *
	 * @return the us_calle
	 */
	public String getUs_calle() {
		return us_calle;
	}

	/**
	 * Sets the us_calle.
	 *
	 * @param us_calle the new us_calle
	 */
	public void setUs_calle(String us_calle) {
		this.us_calle = us_calle;
	}

	/**
	 * Gets the us_num ext.
	 *
	 * @return the us_num ext
	 */
	public String getUs_numExt() {
		return us_numExt;
	}

	/**
	 * Sets the us_num ext.
	 *
	 * @param us_numExt the new us_num ext
	 */
	public void setUs_numExt(String us_numExt) {
		this.us_numExt = us_numExt;
	}

	/**
	 * Gets the us_num int.
	 *
	 * @return the us_num int
	 */
	public String getUs_numInt() {
		return us_numInt;
	}

	/**
	 * Sets the us_num int.
	 *
	 * @param us_numInt the new us_num int
	 */
	public void setUs_numInt(String us_numInt) {
		this.us_numInt = us_numInt;
	}

	/**
	 * Gets the us_col.
	 *
	 * @return the us_col
	 */
	public String getUs_col() {
		return us_col;
	}

	/**
	 * Sets the us_col.
	 *
	 * @param us_col the new us_col
	 */
	public void setUs_col(String us_col) {
		this.us_col = us_col;
	}

	/**
	 * Gets the us_cod pos.
	 *
	 * @return the us_cod pos
	 */
	public String getUs_codPos() {
		return us_codPos;
	}

	/**
	 * Sets the us_cod pos.
	 *
	 * @param us_codPos the new us_cod pos
	 */
	public void setUs_codPos(String us_codPos) {
		this.us_codPos = us_codPos;
	}

	/**
	 * Gets the us_tel.
	 *
	 * @return the us_tel
	 */
	public String getUs_tel() {
		return us_tel;
	}

	/**
	 * Sets the us_tel.
	 *
	 * @param us_tel the new us_tel
	 */
	public void setUs_tel(String us_tel) {
		this.us_tel = us_tel;
	}

	/**
	 * Gets the us_cor ele.
	 *
	 * @return the us_cor ele
	 */
	public String getUs_corEle() {
		return us_corEle;
	}

	/**
	 * Sets the us_cor ele.
	 *
	 * @param us_corEle the new us_cor ele
	 */
	public void setUs_corEle(String us_corEle) {
		this.us_corEle = us_corEle;
	}

	/**
	 * Gets the us_edo ext.
	 *
	 * @return the us_edo ext
	 */
	public String getUs_edoExt() {
		return us_edoExt;
	}

	/**
	 * Sets the us_edo ext.
	 *
	 * @param us_edoExt the new us_edo ext
	 */
	public void setUs_edoExt(String us_edoExt) {
		this.us_edoExt = us_edoExt;
	}

	/**
	 * Gets the us_ciudad ext.
	 *
	 * @return the us_ciudad ext
	 */
	public String getUs_ciudadExt() {
		return us_ciudadExt;
	}

	/**
	 * Sets the us_ciudad ext.
	 *
	 * @param us_ciudadExt the new us_ciudad ext
	 */
	public void setUs_ciudadExt(String us_ciudadExt) {
		this.us_ciudadExt = us_ciudadExt;
	}

	/**
	 * Gets the us_sexo.
	 *
	 * @return the us_sexo
	 */
	public String getUs_sexo() {
		return us_sexo;
	}

	/**
	 * Sets the us_sexo.
	 *
	 * @param us_sexo the new us_sexo
	 */
	public void setUs_sexo(String us_sexo) {
		this.us_sexo = us_sexo;
	}

	/**
	 * Gets the us_fec nac.
	 *
	 * @return the us_fec nac
	 */
	public Date getUs_fecNac() {
		return us_fecNac;
	}

	/**
	 * Sets the us_fec nac.
	 *
	 * @param us_fecNac the new us_fec nac
	 */
	public void setUs_fecNac(Date us_fecNac) {
		this.us_fecNac = us_fecNac;
	}

	/**
	 * Gets the us_otro mod.
	 *
	 * @return the us_otro mod
	 */
	public String getUs_otroMod() {
		return us_otroMod;
	}

	/**
	 * Sets the us_otro mod.
	 *
	 * @param us_otroMod the new us_otro mod
	 */
	public void setUs_otroMod(String us_otroMod) {
		this.us_otroMod = us_otroMod;
	}

	/**
	 * Gets the us_arc des.
	 *
	 * @return the us_arc des
	 */
	public String getUs_arcDes() {
		return us_arcDes;
	}

	/**
	 * Sets the us_arc des.
	 *
	 * @param us_arcDes the new us_arc des
	 */
	public void setUs_arcDes(String us_arcDes) {
		this.us_arcDes = us_arcDes;
	}

	/**
	 * Gets the us_dat des.
	 *
	 * @return the us_dat des
	 */
	public String getUs_datDes() {
		return us_datDes;
	}

	/**
	 * Sets the us_dat des.
	 *
	 * @param us_datDes the new us_dat des
	 */
	public void setUs_datDes(String us_datDes) {
		this.us_datDes = us_datDes;
	}

	/**
	 * Gets the us_otros datos.
	 *
	 * @return the us_otros datos
	 */
	public String getUs_otrosDatos() {
		return us_otrosDatos;
	}

	/**
	 * Sets the us_otros datos.
	 *
	 * @param us_otrosDatos the new us_otros datos
	 */
	public void setUs_otrosDatos(String us_otrosDatos) {
		this.us_otrosDatos = us_otrosDatos;
	}

	/**
	 * Gets the desc respuesta.
	 *
	 * @return the desc respuesta
	 */
	public String getDescRespuesta() {
		return descRespuesta;
	}

	/**
	 * Sets the desc respuesta.
	 *
	 * @param descRespuesta the new desc respuesta
	 */
	public void setDescRespuesta(String descRespuesta) {
		this.descRespuesta = descRespuesta;
	}

	/**
	 * Gets the status splicitud.
	 *
	 * @return the status splicitud
	 */
	public Status getStatuSolicitud() {
		return statuSolicitud;
	}

	/**
	 * Sets the status solicitud.
	 *
	 * @param statusSplicitud the new status solicitud
	 */
	public void setStatuSolicitud(Status statuSolicitud) {
		this.statuSolicitud = statuSolicitud;
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
	 * 
	 * /**
	 * 
	 * @return the archivo
	 */
	public InfomexArchivo getArchivo() {
		return archivo;
	}

	/**
	 * @param archivo the archivo to set
	 */
	public void setArchivo(InfomexArchivo archivo) {
		this.archivo = archivo;
	}

	/**
	 * @return the ke_claEst
	 */
	public String getKe_claEst() {
		return ke_claEst;
	}

	/**
	 * @param ke_claEst the ke_claEst to set
	 */
	public void setKe_claEst(String ke_claEst) {
		this.ke_claEst = ke_claEst;
	}

	/**
	 * @return the kmu_claMun
	 */
	public String getKmu_claMun() {
		return kmu_claMun;
	}

	/**
	 * @param kmu_claMun the kmu_claMun to set
	 */
	public void setKmu_claMun(String kmu_claMun) {
		this.kmu_claMun = kmu_claMun;
	}

	/**
	 * @return the us_idPais
	 */
	public String getUs_idPais() {
		return us_idPais;
	}

	/**
	 * @param us_idPais the us_idPais to set
	 */
	public void setUs_idPais(String us_idPais) {
		this.us_idPais = us_idPais;
	}

	/**
	 * @return the us_ocupacion
	 */
	public String getUs_ocupacion() {
		return us_ocupacion;
	}

	/**
	 * @param us_ocupacion the us_ocupacion to set
	 */
	public void setUs_ocupacion(String us_ocupacion) {
		this.us_ocupacion = us_ocupacion;
	}

	/**
	 * @return the us_subEnl
	 */
	public String getUs_subEnl() {
		return us_subEnl;
	}

	/**
	 * @return the modoEntrega
	 */
	public InfomexModalidadEntrega getModoEntrega() {
		return modoEntrega;
	}

	/**
	 * @param modoEntrega the modoEntrega to set
	 */
	public void setModoEntrega(InfomexModalidadEntrega modoEntrega) {
		this.modoEntrega = modoEntrega;
	}

	/**
	 * @param us_subEnl the us_subEnl to set
	 */
	public void setUs_subEnl(String us_subEnl) {
		this.us_subEnl = us_subEnl;
	}

	/**
	 * @return the folderId
	 */
	public String getContentId() {
		return contentId;
	}

	/**
	 * @param folderId the folderId to set
	 */
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	/**
	 * @return the status
	 */
	public InfomexStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(InfomexStatus status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InfomexSolicitud [infomexSolicitudKey=" + infomexSolicitudKey + ", us_unienl=" + us_unienl
				+ ", us_tipo=" + us_tipo + ", us_fecRecepcion=" + us_fecRecepcion + ", us_repLegal=" + us_repLegal
				+ ", us_rfc=" + us_rfc + ", us_apePat=" + us_apePat + ", us_apeMat=" + us_apeMat + ", us_nombre="
				+ us_nombre + ", us_curp=" + us_curp + ", us_calle=" + us_calle + ", us_numExt=" + us_numExt
				+ ", us_numInt=" + us_numInt + ", us_col=" + us_col + ", ke_claEst=" + ke_claEst + ", kmu_claMun="
				+ kmu_claMun + ", us_codPos=" + us_codPos + ", us_tel=" + us_tel + ", us_corEle=" + us_corEle
				+ ", us_idPais=" + us_idPais + ", us_edoExt=" + us_edoExt + ", us_ciudadExt=" + us_ciudadExt
				+ ", us_sexo=" + us_sexo + ", us_fecNac=" + us_fecNac + ", us_ocupacion=" + us_ocupacion
				+ ", us_modEnt=" + modoEntrega + ", us_otroMod=" + us_otroMod + ", us_arcDes=" + us_arcDes
				+ ", us_datDes=" + us_datDes + ", us_otrosDatos=" + us_otrosDatos + ", archivo=" + archivo
				+ ", descRespuesta=" + descRespuesta + ", statuSolicitud=" + statuSolicitud + "]";
	}

	public Integer getIdAsunto() {
		return idAsunto;
	}

	public void setIdAsunto(Integer idAsunto) {
		this.idAsunto = idAsunto;
	}

}
