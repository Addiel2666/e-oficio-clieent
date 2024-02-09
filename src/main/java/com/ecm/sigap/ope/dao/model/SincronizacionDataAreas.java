package com.ecm.sigap.ope.dao.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Sincronizacion completa de directorio con cada institucion con la que se
 * interopera,
 * 
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "OPESINCRONIZACIONDATAAREAS")
@SequenceGenerator(name = "SEQ_ID", sequenceName = "SECOBJETOS", allocationSize = 1)
public class SincronizacionDataAreas implements Serializable {

	/**	*/
	private static final long serialVersionUID = 6390628560135969486L;

	/**  */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ID")
	@Column(name = "ID")
	private Integer id;

	/** */
	@Column(name = "IDEXTERNO")
	private String idExterno;

	/** */
	@Column(name = "DESCRIPCION")
	private String descripcion;

	/**/
	@Column(name = "IDCATALOGO")
	private Integer idCatalogo;

	/**/
	@Column(name = "IDUSUARIOTITULAR")
	private String idUsuarioTitular;

	/** */
	@Column(name = "IDAREAPADRE")
	private String idAreaPadre;

	/** */
	@OneToMany
	@JoinColumn(name = "idArea")
	private List<SincronizacionDataUsuarios> usuarios;

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
	 * @return the usuarios
	 */
	public List<SincronizacionDataUsuarios> getUsuarios() {
		return usuarios;
	}

	/**
	 * @param usuarios the usuarios to set
	 */
	public void setUsuarios(List<SincronizacionDataUsuarios> usuarios) {
		this.usuarios = usuarios;
	}

	/**
	 * @return the idCatalogo
	 */
	public Integer getIdCatalogo() {
		return idCatalogo;
	}

	/**
	 * @param idCatalogo the idCatalogo to set
	 */
	public void setIdCatalogo(Integer idCatalogo) {
		this.idCatalogo = idCatalogo;
	}

	/**
	 * @return the idUsuarioTitular
	 */
	public String getIdUsuarioTitular() {
		return idUsuarioTitular;
	}

	/**
	 * @param idUsuarioTitular the idUsuarioTitular to set
	 */
	public void setIdUsuarioTitular(String idUsuarioTitular) {
		this.idUsuarioTitular = idUsuarioTitular;
	}

	/**
	 * @return the idAreaPadre
	 */
	public String getIdAreaPadre() {
		return idAreaPadre;
	}

	/**
	 * @param idAreaPadre the idAreaPadre to set
	 */
	public void setIdAreaPadre(String idAreaPadre) {
		this.idAreaPadre = idAreaPadre;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
