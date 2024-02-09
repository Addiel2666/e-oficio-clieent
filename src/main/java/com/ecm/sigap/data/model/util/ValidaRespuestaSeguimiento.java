package com.ecm.sigap.data.model.util;

import java.io.Serializable;

public class ValidaRespuestaSeguimiento implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7738181079921576344L;

	private Boolean validaRFE;
	private Boolean validaRFA;
	
	/**
	 * @return the validaRFE
	 */
	public Boolean getValidaRFE() {
		return validaRFE;
	}
	/**
	 * @param validaRFE the validaRFE to set
	 */
	public void setValidaRFE(Boolean validaRFE) {
		this.validaRFE = validaRFE;
	}
	/**
	 * @return the validaRFA
	 */
	public Boolean getValidaRFA() {
		return validaRFA;
	}
	/**
	 * @param validaRFA the validaRFA to set
	 */
	public void setValidaRFA(Boolean validaRFA) {
		this.validaRFA = validaRFA;
	}
}
