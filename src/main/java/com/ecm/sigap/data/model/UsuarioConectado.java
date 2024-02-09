/**
 * 
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Entity
@Table(name = "usuariosConectados")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
public class UsuarioConectado implements Serializable {

	/** */
	private static final long serialVersionUID = 7306790799272756230L;

	/** Identificador del usuario */
	@EmbeddedId
	private UsuarioConectadoKey usuarioConectadoKey;

	/** Fecha de registro del Asunto / Tramite */
	@Column(name = "loginTime")
	@Type(type = "java.util.Date")
	private Date loginTime;

	/**
	 * direccion ip del cliente.
	 */
	@Column(name = "ipAddress")
	private String ipAddress;

	/**
	 * @return the usuarioConectadoKey
	 */
	public UsuarioConectadoKey getUsuarioConectadoKey() {
		return usuarioConectadoKey;
	}

	/**
	 * @param usuarioConectadoKey the usuarioConectadoKey to set
	 */
	public void setUsuarioConectadoKey(UsuarioConectadoKey usuarioConectadoKey) {
		this.usuarioConectadoKey = usuarioConectadoKey;
	}

	/**
	 * @return the loginTime
	 */
	public Date getLoginTime() {
		return loginTime;
	}

	/**
	 * @param loginTime the loginTime to set
	 */
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((usuarioConectadoKey == null) ? 0 : usuarioConectadoKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioConectado other = (UsuarioConectado) obj;
		if (usuarioConectadoKey == null) {
			if (other.usuarioConectadoKey != null)
				return false;
		} else if (!usuarioConectadoKey.equals(other.usuarioConectadoKey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UsuarioConectado [usuarioConectadoKey=" + usuarioConectadoKey + ", loginTime=" + loginTime
				+ ", ipAddress=" + ipAddress + "]";
	}

}
