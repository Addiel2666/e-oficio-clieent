package com.ecm.sigap.data.model.util;

public class FoliadoraMultiple {

	// * * * Folio PS * * * //
	private Integer id;

	private String prefijoFolio;

	private String sufijoFolio;

	private Integer idArea;

	private String descripcion;

	// * * * Folio Area * * * //

	private Integer idFoliopsMultiple;

	/** Tipo de folio Respuesta/Asunto*/
	private Integer idTipoFolio;

	private Integer folio;

	private String vlock;

	// * * * otro * * * //

	/** The comparte folio SN. */
	private String comparteFolioSN;

	/** The foliador unico SN. */
	private String foliadorUnicoSN;

	/** The id foliadora herencia. */
	private String idFolioHeredado;
	
	/** Id area foliadora herencia */
	private Integer idAreaHereda;
	
	/** Tipo de foliadora Heredado/Propio */ 
	private String tipoFoliadora;

	/** The Folio asunto. */
	private Integer FolioAsunto;

	/** The Folio respuesta. */
	private Integer FolioRespuesta;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the prefijoFolio
	 */
	public String getPrefijoFolio() {
		return prefijoFolio;
	}

	/**
	 * @param prefijoFolio
	 *            the prefijoFolio to set
	 */
	public void setPrefijoFolio(String prefijoFolio) {
		this.prefijoFolio = prefijoFolio;
	}

	/**
	 * @return the sufijoFolio
	 */
	public String getSufijoFolio() {
		return sufijoFolio;
	}

	/**
	 * @param sufijoFolio
	 *            the sufijoFolio to set
	 */
	public void setSufijoFolio(String sufijoFolio) {
		this.sufijoFolio = sufijoFolio;
	}

	/**
	 * @return the idArea
	 */
	public Integer getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea
	 *            the idArea to set
	 */
	public void setIdArea(Integer idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion
	 *            the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return the idFoliopsMultiple
	 */
	public Integer getIdFoliopsMultiple() {
		return idFoliopsMultiple;
	}

	/**
	 * @param idFoliopsMultiple
	 *            the idFoliopsMultiple to set
	 */
	public void setIdFoliopsMultiple(Integer idFoliopsMultiple) {
		this.idFoliopsMultiple = idFoliopsMultiple;
	}

	/**
	 * @return the idTipoFolio
	 */
	public Integer getIdTipoFolio() {
		return idTipoFolio;
	}

	/**
	 * @param idTipoFolio
	 *            the idTipoFolio to set
	 */
	public void setIdTipoFolio(Integer idTipoFolio) {
		this.idTipoFolio = idTipoFolio;
	}

	/**
	 * @return the folio
	 */
	public Integer getFolio() {
		return folio;
	}

	/**
	 * @param folio
	 *            the folio to set
	 */
	public void setFolio(Integer folio) {
		this.folio = folio;
	}

	/**
	 * @return the vlock
	 */
	public String getVlock() {
		return vlock;
	}

	/**
	 * @param vlock
	 *            the vlock to set
	 */
	public void setVlock(String vlock) {
		this.vlock = vlock;
	}

	/**
	 * @return the comparteFolioSN
	 */
	public String getComparteFolioSN() {
		return comparteFolioSN;
	}

	/**
	 * @param comparteFolioSN
	 *            the comparteFolioSN to set
	 */
	public void setComparteFolioSN(String comparteFolioSN) {
		this.comparteFolioSN = comparteFolioSN;
	}

	/**
	 * @return the foliadorUnicoSN
	 */
	public String getFoliadorUnicoSN() {
		return foliadorUnicoSN;
	}

	/**
	 * @param foliadorUnicoSN
	 *            the foliadorUnicoSN to set
	 */
	public void setFoliadorUnicoSN(String foliadorUnicoSN) {
		this.foliadorUnicoSN = foliadorUnicoSN;
	}


	/**
	 * @return the idFolioHeredado
	 */
	public String getIdFolioHeredado() {
		return idFolioHeredado;
	}

	/**
	 * @param idFolioHeredado the idFolioHeredado to set
	 */
	public void setIdFolioHeredado(String idFolioHeredado) {
		this.idFolioHeredado = idFolioHeredado;
	}

	/**
	 * @return the tipoFoliadora
	 */
	public String getTipoFoliadora() {
		return tipoFoliadora;
	}

	/**
	 * @param tipoFoliadora the tipoFoliadora to set
	 */
	public void setTipoFoliadora(String tipoFoliadora) {
		this.tipoFoliadora = tipoFoliadora;
	}

	/**
	 * @return the folioAsunto
	 */
	public Integer getFolioAsunto() {
		return FolioAsunto;
	}

	/**
	 * @param folioAsunto
	 *            the folioAsunto to set
	 */
	public void setFolioAsunto(Integer folioAsunto) {
		FolioAsunto = folioAsunto;
	}

	/**
	 * @return the folioRespuesta
	 */
	public Integer getFolioRespuesta() {
		return FolioRespuesta;
	}

	/**
	 * @param folioRespuesta
	 *            the folioRespuesta to set
	 */
	public void setFolioRespuesta(Integer folioRespuesta) {
		FolioRespuesta = folioRespuesta;
	}

	/**
	 * @return the idAreaHereda
	 */
	public Integer getIdAreaHereda() {
		return idAreaHereda;
	}

	/**
	 * @param idAreaHereda the idAreaHereda to set
	 */
	public void setIdAreaHereda(Integer idAreaHereda) {
		this.idAreaHereda = idAreaHereda;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FoliadoraMultiple [id=" + id + ", prefijoFolio=" + prefijoFolio + ", sufijoFolio=" + sufijoFolio
				+ ", idArea=" + idArea + ", descripcion=" + descripcion + ", idFoliopsMultiple=" + idFoliopsMultiple
				+ ", idTipoFolio=" + idTipoFolio + ", folio=" + folio + ", vlock=" + vlock + ", comparteFolioSN="
				+ comparteFolioSN + ", foliadorUnicoSN=" + foliadorUnicoSN + ", idFolioHeredado=" + idFolioHeredado
				+ ", FolioAsunto=" + FolioAsunto + ", FolioRespuesta=" + FolioRespuesta + "]";
	}

	

}
