/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ecm.sigap.data.model.util.TipoDestinatario;

/**
 * 
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@Entity
@Table(name = "grupo_envio_destinatarios")
public final class DestinatarioGrupoEnvio implements Serializable {

	/** */
	private static final long serialVersionUID = 6868273321788720276L;

	/** */
	@EmbeddedId
	private DestinatarioGrupoEnvioKey destinatarioGrupoEnvioKey;

	/**
	 * @return the destinatarioGrupoEnvioKey
	 */
	public DestinatarioGrupoEnvioKey getDestinatarioGrupoEnvioKey() {
		return destinatarioGrupoEnvioKey;
	}

	/**
	 * @param destinatarioGrupoEnvioKey the destinatarioGrupoEnvioKey to set
	 */
	public void setDestinatarioGrupoEnvioKey(DestinatarioGrupoEnvioKey destinatarioGrupoEnvioKey) {
		this.destinatarioGrupoEnvioKey = destinatarioGrupoEnvioKey;
	}

	@Override
	public String toString() {
		return "DestinatarioGrupoEnvio [destinatarioGrupoEnvioKey=" + destinatarioGrupoEnvioKey + "]";
	}

}
