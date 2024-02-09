/**
 * 
 */
package com.ecm.sigap.data.controller.impl.async;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ecm.sigap.data.controller.impl.RespuestaConsultaController;
import com.ecm.sigap.data.controller.impl.RespuestaController;
import com.ecm.sigap.data.model.Asunto;
import com.ecm.sigap.data.model.Destinatario;
import com.ecm.sigap.data.model.DocumentoRespuesta;
import com.ecm.sigap.data.model.Respuesta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.util.SubTipoAsunto;
import com.ecm.sigap.data.model.util.TipoTimestamp;
import com.ecm.sigap.data.service.EntityManager;
import com.ecm.sigap.firma.FirmaService;

/**
 * @author alfredo morales
 * @version 1.0
 *
 */
@Component
public class AceptarRechazarRespuesta {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(AceptarRechazarRespuesta.class);

	/**
	 * Manejador para el tipo {@link Asunto}
	 */
	@Autowired
	@Qualifier("asuntoService")
	protected EntityManager<Asunto> mngrAsunto;

	/**
	 * Manejador para el tipo {@link Respuesta}
	 */
	@Autowired
	@Qualifier("respuestaService")
	private EntityManager<Respuesta> mngrRespuesta;

	/**
	 * Manejador para el tipo {@link Status}
	 */
	@Autowired
	@Qualifier("statusService")
	protected EntityManager<Status> mngrStatus;

	/**
	 * Manejador para el tipo {@link Destinatario}
	 */
	@Autowired
	@Qualifier("destinatarioService")
	protected EntityManager<Destinatario> mngrDestinatario;

	/**
	 * Manejador para el tipo {@link DocumentoRespuesta}
	 */
	@Autowired
	@Qualifier("documentoRespuestaService")
	protected EntityManager<DocumentoRespuesta> mngrDocsRespuesta;

	/**
	 * Servicio de llamadas REST al WS de Firma Digital
	 */
	@Autowired
	@Qualifier("firmaService")
	protected FirmaService firmaEndPoint;

	/**
	 * Manejador para el tipo {@link RespuestaConsultaController}
	 */
	@Autowired
	protected RespuestaController respuestaController;

	/**
	 * 
	 * @param resp
	 * @param tipots
	 * @return
	 */
	public String getStampedData(Respuesta resp, TipoTimestamp tipots) {
		String toBeStamped = resp.getIdRespuesta() + "-" + tipots.getTipo();

		return toBeStamped;
	}

	/**
	 * 
	 * @param idArea
	 * @return
	 */
	public Destinatario getDestinatarioInterno(Integer idArea) {

		String query = "SELECT d from Destinatario d WHERE d.idArea = " + idArea + " AND d.idSubTipoAsunto='"
				+ SubTipoAsunto.C + "'";

		List<Destinatario> destinatario = (List<Destinatario>) mngrDestinatario.execQuery(query);

		if (null == destinatario || destinatario.isEmpty() || destinatario.size() > 1) {
			return null;
		}

		return destinatario.get(0);
	}

	/**
	 * 
	 * @param idRespuesta
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DocumentoRespuesta> getDocumentosRespuesta(Integer idRespuesta) throws Exception {

		List<Criterion> restrictions = new ArrayList<Criterion>();

		restrictions.add(Restrictions.eq("idRespuesta", idRespuesta));

		return (List<DocumentoRespuesta>) mngrDocsRespuesta.search(restrictions);
	}

}
