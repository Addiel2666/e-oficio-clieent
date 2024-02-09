/**
 * 
 */
package com.ecm.sigap.firma.model;

import com.ecm.sigap.data.model.util.SignContentType;
import com.ecm.sigap.data.model.util.TipoFirma;

/**
 * @author Alfredo Morales
 *
 */
public class IniciarFirmaObjecto {

	/** */
	private String fileB64;
	/** */
	private String fileName;
	/** */
	private TipoFirma tipoFirma;
	/** */
	private SignContentType signContentType;

	/** */
	public IniciarFirmaObjecto(String fileB64, String fileName, TipoFirma tipoFirma, SignContentType signContentType) {
		super();
		this.fileB64 = fileB64;
		this.fileName = fileName;
		this.tipoFirma = tipoFirma;
		this.signContentType = signContentType;
	}

	/**
	 * @return the fileB64
	 */
	public String getFileB64() {
		return fileB64;
	}

	/**
	 * @param fileB64 the fileB64 to set
	 */
	public void setFileB64(String fileB64) {
		this.fileB64 = fileB64;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the tipoFirma
	 */
	public TipoFirma getTipoFirma() {
		return tipoFirma;
	}

	/**
	 * @param tipoFirma the tipoFirma to set
	 */
	public void setTipoFirma(TipoFirma tipoFirma) {
		this.tipoFirma = tipoFirma;
	}

	/**
	 * @return the signContentType
	 */
	public SignContentType getSignContentType() {
		return signContentType;
	}

	/**
	 * @param signContentType the signContentType to set
	 */
	public void setSignContentType(SignContentType signContentType) {
		this.signContentType = signContentType;
	}

}
