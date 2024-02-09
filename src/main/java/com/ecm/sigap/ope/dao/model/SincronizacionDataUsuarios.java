package com.ecm.sigap.ope.dao.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "OPESINCRONIZACIONDATAAREAS")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SECOBJETOS", allocationSize = 1)
public class SincronizacionDataUsuarios implements Serializable {

	/**  */
	private static final long serialVersionUID = -6142383147036237811L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
	@Column(name = "ID")
	private Integer id;

	@Column(name = "IDAREA")
	private Integer idArea;

	@Column(name = "IDEXTERNO")
	private String idExterno;

	/** */
	@Column(name = "NOMBRECOMPLETO")
	private String nombreCompleto;

	/** */
	@Column(name = "PUESTO")
	private String puesto;

	/** */
	@Column(name = "CORREOELECTRONICO")
	private String correoElectronico;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
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
	 * @return the idExterno
	 */
	public String getIdExterno() {
		return idExterno;
	}

	/**
	 * @param idExterno the idExterno to set
	 */
	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	/**
	 * @return the nombreCompleto
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * @param nombreCompleto the nombreCompleto to set
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/**
	 * @return the puesto
	 */
	public String getPuesto() {
		return puesto;
	}

	/**
	 * @param puesto the puesto to set
	 */
	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}

	/**
	 * @return the correoElectronico
	 */
	public String getCorreoElectronico() {
		return correoElectronico;
	}

	/**
	 * @param correoElectronico the correoElectronico to set
	 */
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

}
