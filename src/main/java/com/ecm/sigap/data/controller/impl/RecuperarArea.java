package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.util.SerializationHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.AreaAuxiliar;
import com.ecm.sigap.data.model.AreaPromotor;
import com.ecm.sigap.data.model.Folio;
import com.ecm.sigap.data.model.FolioArea;
import com.ecm.sigap.data.model.FolioAreaKey;
import com.ecm.sigap.data.model.Parametro;
import com.ecm.sigap.data.model.ParametroKey;
import com.ecm.sigap.data.model.Permiso;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Tema;
import com.ecm.sigap.data.model.TipoDocumento;
import com.ecm.sigap.data.model.TipoEvento;
import com.ecm.sigap.data.model.TipoExpediente;
import com.ecm.sigap.data.model.TipoInstruccion;
import com.ecm.sigap.data.model.TipoPrioridad;
import com.ecm.sigap.data.service.EntityManager;

@RestController
@SuppressWarnings("unchecked")
public class RecuperarArea extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(RecuperarArea.class);

	@RequestMapping(value = "/recuperarArea", method = RequestMethod.GET)
	public String recuperarAreas(@RequestParam(value = "id", required = true) Serializable id) {

		Map<String, Object> resultRecover = new HashMap<String, Object>();

		long init = System.currentTimeMillis();

		// Lista de ids de Areas a recuperar
		List<Integer> listIDAreaRecuperar = new ArrayList<Integer>();

		listIDAreaRecuperar.add(Integer.valueOf((String) id));
		// listIDAreaRecuperar.add(6344);

		StringBuilder result = new StringBuilder();
		String salto = System.getProperty("line.separator");
		String idAreaTemplate = getParamApp("SIGAPTEMPLATE", "IDAREA");
		Area template = mngrArea.fetch(Integer.valueOf(idAreaTemplate));

		// ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
		HashSet<Integer> idAreasError = new HashSet<>();
		HashSet<Integer> idAreasNoExiste = new HashSet<>();
		int contador = 0;
		for (Integer idArea : listIDAreaRecuperar) {
			contador++;
			Area area = mngrArea.fetch(Integer.valueOf((String) id));
			if (null != area && StringUtils.isNotBlank(area.getDescripcion())) {

				// taskExecutor.execute(new ThreadRecuperarAreas(idArea,
				// template.getIdArea(), result, salto, contador,
				// idAreasError));
				ThreadRecuperarAreas recuperar = new ThreadRecuperarAreas(idArea, template.getIdArea(), result, salto,
						contador, idAreasError);
				;
				recuperar.run();
			} else {
				idAreasNoExiste.add(idArea);
			}
		}
		// taskExecutor.shutdown();
		//
		// try {
		//
		// taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		//
		// } catch (InterruptedException e) {
		// 
		// }
		if (!idAreasError.isEmpty()) {
			result.append("ID DE AREAS QUE FALLARON: >>> ").append(salto);

			for (Integer idAreaFallo : idAreasError) {
				result.append(idAreaFallo).append(salto);
			}
		} else {
			result.append("ID DE AREAS QUE FALLARON 0 >>> ").append(salto);
		}

		if (!idAreasNoExiste.isEmpty()) {
			result.append("ID DE AREAS QUE QUE NO EXISTE O TIENE DESCRIPCION = NULL  >>> ").append(salto);

			for (Integer idAreaNoExiste : idAreasNoExiste) {
				result.append(idAreaNoExiste).append(salto);
			}
		}
		long fin = System.currentTimeMillis(); // Instante final del
												// procesamiento
		long tiempoTotal = (fin - init) / 1000;
		log.debug("Tiempo total de procesamiento: " + tiempoTotal + " Segundos");
		result.append("TIEMPO TOTAL EJECUCION >>> ").append(tiempoTotal + " Segundos").append(salto);

		resultRecover.put("resultRecover", result.toString());
		return result.toString();

	}

	public class ThreadRecuperarAreas {

		private int idArea;
		private int idAreaTemplate;
		private StringBuilder result;
		private String salto;
		private int contador;
		HashSet<Integer> idAreasError = new HashSet<>();

		public ThreadRecuperarAreas(int idArea, int idAreaTemplate, StringBuilder result, String salto, int contador,
				HashSet<Integer> idAreasError) {

			this.idArea = idArea;
			this.idAreaTemplate = idAreaTemplate;
			this.result = result;
			this.salto = salto;
			this.contador = contador;
			this.idAreasError = idAreasError;
		}

		public void run() {
			Area item = null;
			List<Criterion> restrictions = new ArrayList<>();
			try {

				item = mngrArea.fetch(idArea);

				log.debug("Iniciando Recuperacion Area: " + idArea + " numero contador " + contador);
				log.debug("Iniciando Recuperacion Area: " + idArea);
				result.append("Iniciando Recuperacion Area: " + idArea).append(salto).append("####" + contador)
						.append(salto);

				// Roles Validacion y generar
				// * * * * * * * * * * * * * * * * * * * * * *
				restrictions.clear();
				restrictions.add(Restrictions.eq("idArea", item.getIdArea()));
				List<Rol> rolesExist = (List<Rol>) mngrRol.search(restrictions);

				if (rolesExist.isEmpty()) {
					log.debug("Recuperando roles: " + idArea);
					result.append("Recuperando roles: " + idArea).append(salto);
					// replicar roles y permisos del template
					saveRoles(idAreaTemplate, item.getIdArea());
				} else {
					log.debug("Ya tiene Roles el area: " + idArea);
					result.append("Ya tiene Roles el area: " + idArea).append(salto);
				}

				// FoliosAreas Validacion y generar
				// * * * * * * * * * * * * * * * * * * * * * *
				restrictions.clear();
				restrictions.add(Restrictions.eq("folioAreaKey.idArea", item.getIdArea()));
				List<FolioArea> listFolioArea = (List<FolioArea>) mngrFolioArea.search(restrictions);
				if (listFolioArea.isEmpty()) {
					log.debug("Recuperando FoliosArea: " + idArea);
					result.append("Recuperando FoliosArea: " + idArea).append(salto);
					// replicar el folio inicial del template y folios de
					// documentos
					saveFoliosAsuntos(idAreaTemplate, item.getIdArea());
				} else {
					log.debug("Ya tiene FoliosArea: " + idArea);
					result.append("Ya tiene FoliosArea: " + idArea).append(salto);
				}

				// replicar catalogos template
				String resulCat = saveCatalogosVailidar(idAreaTemplate, item.getIdArea());
				result.append(resulCat).append(salto);

				restrictions.clear();
				restrictions.add(Restrictions.eq("areaPromotorKey.idArea", item.getIdArea()));
				List<AreaPromotor> listAreaPromotor = (List<AreaPromotor>) mngrAreaPromotor.search(restrictions);
				if (listAreaPromotor.isEmpty()) {
					log.debug("Recuperando AreaPromotor: " + idArea);
					result.append("Recuperando AreaPromotor: " + idArea).append(salto);
					// replicar areas promotoras template
					saveAreasPromotores(idAreaTemplate, item.getIdArea());
				} else {
					log.debug("Ya tiene AreaPromotor: " + idArea);
					result.append("Ya tiene AreaPromotor: " + idArea).append(salto);
				}
				// El Area template 5 no tiene Remitentes
				// restrictions.clear();
				// restrictions.add(Restrictions.eq("areaRemitenteKey.idArea",
				// item.getIdArea()));
				// List<AreaRemitente> listAreaRemitente = (List<AreaRemitente>)
				// mngrAreaRemitente.search(restrictions);
				// if (listAreaRemitente.isEmpty()) {
				// log.debug("Recuperando AreaRemitente: " + idArea);
				// result.append("Recuperando AreaRemitente: " +
				// idArea).append(salto);
				// // replicar areas remitentes template
				// saveAreasRemitentes(idAreaTemplate, item.getIdArea());
				// } else {
				// log.debug("Ya tiene AreaRemitente: " + idArea);
				// result.append("Ya tiene AreaRemitente: " +
				// idArea).append(salto);
				// }

				restrictions.clear();
				restrictions.add(Restrictions.eq("parametroKey.idArea", item.getIdArea()));
				restrictions.add(Restrictions.eq("parametroKey.idSeccion", "CONTENTFOLDER"));
				List<Parametro> listParametrosContent = (List<Parametro>) mngrParametro.search(restrictions);
				if (listParametrosContent.isEmpty()) {
					log.debug("Recuperando Parametros CONTENTFOLDER: " + idArea);
					result.append("Recuperando  Parametros CONTENTFOLDER: " + idArea).append(salto);
					// replicar parametros de content
					saveParametrosContent(idAreaTemplate, item.getIdArea());
				} else {
					log.debug("Ya tiene  Parametros CONTENTFOLDER: " + idArea);
					result.append("Ya tiene  Parametros CONTENTFOLDER: " + idArea).append(salto);
				}

				restrictions.clear();
				restrictions.add(Restrictions.eq("parametroKey.idArea", item.getIdArea()));
				restrictions.add(Restrictions.eq("parametroKey.idSeccion", "WEBSERVICES"));
				List<Parametro> listParametrosWS = (List<Parametro>) mngrParametro.search(restrictions);
				if (listParametrosWS.isEmpty()) {
					log.debug("Recuperando Parametros WEBSERVICES: " + idArea);
					result.append("Recuperando  Parametros WEBSERVICES: " + idArea).append(salto);
					// replicar parametros de ws
					saveParametrosWS(item.getIdArea(), false);
				} else {
					log.debug("Ya tiene  Parametros WEBSERVICES: " + idArea);
					result.append("Ya tiene  Parametros WEBSERVICES: " + idArea).append(salto);
				}

				restrictions.clear();
				restrictions.add(Restrictions.eq("folioKey.idArea", item.getIdArea()));
				List<Folio> listFolios = (List<Folio>) mngrFolio.search(restrictions);
				if (listFolios.isEmpty()) {
					log.debug("Recuperando  Folios: " + idArea);
					result.append("Recuperando   Folios: " + idArea).append(salto);
					// foliacion por area
					saveFoliosPorArea(idAreaTemplate, item.getIdArea());
				} else {
					log.debug("Ya tiene  Folios: " + idArea);
					result.append("Ya tiene  Folios: " + idArea).append(salto);
				}

				restrictions.clear();
				restrictions.add(Restrictions.eq("parametroKey.idArea", item.getIdArea()));
				restrictions.add(Restrictions.eq("parametroKey.idSeccion", "FOLIODOC"));
				List<Parametro> listFoliosDoc = (List<Parametro>) mngrParametro.search(restrictions);
				if (listFoliosDoc.isEmpty()) {
					log.debug("Recuperando  Folios Doc: " + idArea);
					result.append("Recuperando   Folios Doc: " + idArea).append(salto);
					// replicar parametros de content
					saveParametrosFoliosDoc(idAreaTemplate, item.getIdArea());
				} else {
					log.debug("Ya tiene  Folios Doc: " + idArea);
					result.append("Ya tiene  Folios Doc: " + idArea).append(salto);
				}
				try {
					log.debug("Recuperando  Folder Grupos ACL: " + idArea);
					result.append("Recuperando   Folder, Grupos y ACL: " + idArea).append(salto);
					// replicar folders y aplicar acl
					String resultSaveFolders = saveFoldersAreaConValidacion(item);
					result.append(resultSaveFolders).append(salto);
				} catch (Exception e) {
					log.debug("ERROR >>> Procesando: " + idArea);
					
					idAreasError.add(idArea);
					result.append("ERROR >>> Procesando Folder Grupos ACL: " + idArea).append(salto)
							.append(e.getLocalizedMessage()).append(salto);
				}

				log.debug("Fin Recuperacion Area " + idArea);
				result.append("Fin Recuperacion Area " + idArea).append(salto);
			} catch (Exception e) {
				log.debug("ERROR >>> Procesando: " + idArea);
				idAreasError.add(idArea);
				
				result.append("ERROR >>> Procesando: " + idArea).append(salto).append(e.getLocalizedMessage())
						.append(salto);
			}
		}
	}

	/**
	 * 
	 * @param idAreaOrigen
	 * @param idAreaDestino
	 * @throws Exception
	 */
	private String saveCatalogosVailidar(Integer idAreaOrigen, Integer idAreaDestino) throws Exception {

		StringBuilder result = new StringBuilder();
		String salto = System.getProperty("line.separator");

		// * * * * * * * * * * * * * * * * * * * * * *
		List<Criterion> restrictions = new ArrayList<Criterion>();
		restrictions.add(Restrictions.eq("area.idArea", idAreaDestino));

		List<Tema> lisTema = (List<Tema>) mngrTema.search(restrictions);
		if (lisTema.isEmpty()) {
			log.debug("Recuperando Tema: " + idAreaDestino);
			result.append("Recuperando Tema: " + idAreaDestino).append(salto);
			saveCat(idAreaOrigen, idAreaDestino, Tema.class, mngrTema, "idTema");
		} else {
			log.debug("Ya tiene Tema: " + idAreaDestino);
			result.append("Ya tiene Tema: " + idAreaDestino).append(salto);
		}

		List<TipoDocumento> lisTipoDocumento = (List<TipoDocumento>) mngrTipoDocumento.search(restrictions);
		if (lisTipoDocumento.isEmpty()) {
			log.debug("Recuperando TipoDocumento: " + idAreaDestino);
			result.append("Recuperando TipoDocumento: " + idAreaDestino).append(salto);
			saveCat(idAreaOrigen, idAreaDestino, TipoDocumento.class, mngrTipoDocumento, "idTipoDocumento");
		} else {
			log.debug("Ya tiene TipoDocumento: " + idAreaDestino);
			result.append("Ya tiene TipoDocumento: " + idAreaDestino).append(salto);
		}

		List<TipoEvento> lisTipoEvento = (List<TipoEvento>) mngrTipoEvento.search(restrictions);
		if (lisTipoEvento.isEmpty()) {
			log.debug("Recuperando TipoEvento: " + idAreaDestino);
			result.append("Recuperando TipoEvento: " + idAreaDestino).append(salto);
			saveCat(idAreaOrigen, idAreaDestino, TipoEvento.class, mngrTipoEvento, "idEvento");
		} else {
			log.debug("Ya tiene TipoEvento: " + idAreaDestino);
			result.append("Ya tiene TipoEvento: " + idAreaDestino).append(salto);
		}

		List<TipoInstruccion> lisTipoInstruccion = (List<TipoInstruccion>) mngrTipoInstruccion.search(restrictions);
		if (lisTipoInstruccion.isEmpty()) {
			log.debug("Recuperando TipoInstruccion: " + idAreaDestino);
			result.append("Recuperando TipoInstruccion: " + idAreaDestino).append(salto);
			saveCat(idAreaOrigen, idAreaDestino, TipoInstruccion.class, mngrTipoInstruccion, "idInstruccion");
		} else {
			log.debug("Ya tiene TipoInstruccion: " + idAreaDestino);
			result.append("Ya tiene TipoInstruccion: " + idAreaDestino).append(salto);
		}

		List<TipoPrioridad> listTipoPrioridad = (List<TipoPrioridad>) mngrTipoPrioridad.search(restrictions);
		if (listTipoPrioridad.isEmpty()) {
			log.debug("Recuperando TipoPrioridad: " + idAreaDestino);
			result.append("Recuperando TipoPrioridad: " + idAreaDestino).append(salto);
			saveCat(idAreaOrigen, idAreaDestino, TipoPrioridad.class, mngrTipoPrioridad, "idPrioridad");
		} else {
			log.debug("Ya tiene TipoPrioridad: " + idAreaDestino);
			result.append("Ya tiene TipoPrioridad: " + idAreaDestino).append(salto);
		}

		List<TipoExpediente> listTipoExpediente = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);
		if (listTipoExpediente.isEmpty()) {
			log.debug("Recuperando TipoExpediente: " + idAreaDestino);
			result.append("Recuperando TipoExpediente: " + idAreaDestino).append(salto);
			// cuando es expediente se consulta el folder por default
			String idExpDefault = getExpedienteDefault(idAreaOrigen);

			saveCat(idAreaOrigen, idAreaDestino, TipoExpediente.class, mngrTipoExpediente, "idExpediente",
					idExpDefault);
		} else {
			log.debug("Ya tiene TipoExpediente: " + idAreaDestino);
			result.append("Ya tiene TipoExpediente: " + idAreaDestino).append(salto);
		}

		return result.toString();

	}

	private String saveFoldersAreaConValidacion(Area item) throws Exception {

		StringBuilder result = new StringBuilder();
		String salto = System.getProperty("line.separator");

		String idArea = item.getIdArea().toString();

		// folder de area
		String nombreFolder = getNombreFolder(item);
		IEndpoint superUser = EndpointDispatcher.getInstance();

		String startFolder = getParamApp("CABINET");

		String folderIdArea = null;
		boolean createdFolderArea = false;

		String nameFolderDCTM = null;
		if (StringUtils.isNotBlank(item.getContentId())) {

			// valida si existe el folder del area
			nameFolderDCTM = superUser.getObjectName(item.getContentId().toLowerCase());

		} else {
			// valida si existe el folder del area por la descripcion
			try {
				folderIdArea = superUser.getFolderIdByPath(startFolder + "/" + nombreFolder);
			} catch (Exception e) {
				
			}
		}

		if (null != nameFolderDCTM) {

			folderIdArea = item.getContentId();

		} else if (StringUtils.isBlank(folderIdArea)) {
			try {
				folderIdArea = superUser.createFolder(//
						startFolder, //
						environment.getProperty("folderTypeArea"), //
						nombreFolder);

				createdFolderArea = true;
				log.debug("CREADO FOLDER DE AREA: " + item.getIdArea());
				result.append("CREADO FOLDER DE AREA " + item.getIdArea()).append(salto);
			} catch (Exception e) {
				
				log.debug("ERROR >> CREANDODO FOLDER DE AREA: " + item.getIdArea());
				result.append("ERROR >> CREANDODO FOLDER DE AREA " + item.getIdArea()).append(salto);
				throw new Exception(">>> ERROR creando el Folder");
			}
		}

		String idGrupRepo = superUser.getIdGrupo(environment.getProperty("grpSigap") + idArea);
		if (StringUtils.isBlank(idGrupRepo)) {
			// crear grupos
			superUser.createGroup(environment.getProperty("grpSigap") + idArea, "");
			result.append("CREADO GRUPO DE AREA " + item.getIdArea()).append(salto);
		}
		String idGrupConfRepo = superUser.getIdGrupo(environment.getProperty("grpSigapConf") + idArea);
		if (StringUtils.isBlank(idGrupConfRepo)) {
			superUser.createGroup(environment.getProperty("grpSigapConf") + idArea, "");

			result.append("CREADO GRUPOCONF DE AREA " + item.getIdArea()).append(salto);
		}

		// valida que exista el ACL

		boolean exitAcl = superUser.existeAclByName(environment.getProperty("aclSigapName") + item.getIdArea());
		if (!exitAcl) {
			// SET ACL
			Map<String, String> additionalData = new HashMap<>();
			additionalData.put("idArea", idArea);

			try {
				superUser.setACL(folderIdArea, environment.getProperty("aclNameFolderArea"), additionalData);
				result.append("CREADO ACL DE AREA " + item.getIdArea()).append(salto);
			} catch (Exception e) {
				throw new Exception(">>> ERROR Creando ACL setAcl");
			}

			// obtener el nombre del acl recien creado
			String aclName = ((List<String>) superUser.getObjectProperty(folderIdArea, "acl_name")).get(0);
			// renombrar con el estanda de acls de area
			String newAclName = environment.getProperty("aclSigapName") + item.getIdArea();
			try {
				superUser.renameAcl(aclName, newAclName);
				result.append("RENOMBRADO ACL DE AREA " + item.getIdArea()).append(salto);
			} catch (Exception e) {
				throw new Exception(">>> ERROR Renombrando ACL");
			}
			try {
				// set properties de area
				Map<String, Object> properties = new HashMap<>();

				properties.put("acl_name", newAclName);
				properties.put("idarea", String.valueOf(item.getIdArea()));
				superUser.setProperties(folderIdArea, properties);

				result.append("SETEADO PROPIEDADES DE ACL DE AREA " + item.getIdArea()).append(salto);
			} catch (Exception e) {
				throw new Exception(">>> ERROR Seteando Properties del area al ACL");
			}
		}
		if (createdFolderArea || StringUtils.isBlank(item.getContentId())
				|| !folderIdArea.equals(item.getContentId())) {
			item.setContentId(folderIdArea);
			mngrArea.update(item);
		}

		// verificar id del folder
		if (StringUtils.isNotBlank(item.getContentId())) {

			// crear los folders de parametros del area nueva
			String resultParam = saveFoldersParametros(item);
			result.append(resultParam).append(salto);
			// crear folders de expedientes
			String resultExp = saveFoldersExpedientes(item);
			result.append(resultExp).append(salto);

			// crear folders de plantillas de area
			// saveFoldersPlantillas(superUser, item);
		}
		return result.toString();

	}

	/////////////////////////////////////////////////////////// METODOS NO
	/////////////////////////////////////////////////////////// MODIFICADOS
	/////////////////////////////////////////////////////////// ////////////////////////////////////////////////////7
	private void saveRoles(Integer idAreaOrigen, Integer idAreaDestino) throws Exception {
		// obtener roles area plantilla
		List<Rol> roles = getRoles(idAreaOrigen);
		for (Rol rol : roles) {
			Rol newRol = new Rol();
			// obtenemos el idRol original antes de
			// cambiarlo por el nuevo
			Integer idRolTemplate = rol.getIdRol();

			// insertar roles en la nueva area, se pone id
			// null para que genere el nuevo
			newRol.setIdArea(idAreaDestino);
			// rol.setIdRol(null);
			newRol.setActivo(rol.getActivo());
			newRol.setAtributos(rol.getAtributos());
			newRol.setDescripcion(rol.getDescripcion());
			newRol.setIdAreaLim(rol.getIdAreaLim());
			newRol.setTipo(rol.getTipo());
			mngrRol.save(newRol);

			// GUARDAR LOS PERMISOS PARA CADA ROL DEL AREA
			// TEMPLATE PERO EN LA NUEVA AREA
			savePermisosRol(newRol, idAreaOrigen, idRolTemplate);
		}
	}

	/**
	 * 
	 * @param idArea
	 * @return
	 */
	private List<Rol> getRoles(Integer idArea) {
		List<Rol> lst = new ArrayList<Rol>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("idArea", idArea));

		lst = (List<Rol>) mngrRol.search(restrictions);

		return lst;
	}

	private void savePermisosRol(Rol rol, Integer idAreaTemplate, Integer idRolTemplate) throws Exception {
		List<Permiso> lst = new ArrayList<>();

		// obtener los permisos template
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("permisoKey.idArea", idAreaTemplate));
		restrictions.add(Restrictions.eq("permisoKey.idRol", idRolTemplate));

		lst = (List<Permiso>) mngrPermiso.search(restrictions);

		for (Permiso p : lst) {
			// setear el area y rol del nuevo rol creado en la nueva area
			p.getPermisoKey().setIdArea(rol.getIdArea());
			p.getPermisoKey().setIdRol(rol.getIdRol());

			mngrPermiso.save(p);
		}

	}

	private void saveFoliosAsuntos(Integer idAreaOrigen, Integer idAreaDestino) throws Exception {
		// traemos el folio inicial
		List<Folio> lst = new ArrayList<>();
		Folio inicial = new Folio();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("folioKey.idArea", idAreaOrigen));

		lst = (List<Folio>) mngrFolio.search(restrictions);
		if (lst != null && lst.size() != 1) {
			throw new Exception("El folio inicial es distinto de 1");
		} else {
			inicial = lst.get(0);
		}

		// guardar los folios de documento de area nueva usando el folio inicial
		// tipoFolio 0 = asunto
		// tipoFolio 1 = respuesta
		// tipoFolio 2 = customimss??
		for (int i = 0; i <= 2; i++) {
			FolioArea fa = new FolioArea();
			fa.setFolioAreaKey(new FolioAreaKey(idAreaDestino, i));
			fa.setFolio(inicial.getFolioKey().getFolio());

			mngrFolioArea.save(fa);
		}

	}

	private void saveAreasPromotores(Integer idAreaOrigen, Integer idAreaDestino) throws Exception {
		// obtener areas promotores origen
		List<AreaPromotor> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("areaPromotorKey.idArea", idAreaOrigen));

		items = (List<AreaPromotor>) mngrAreaPromotor.search(restrictions);

		for (AreaPromotor ap : items) {
			// replicar registros para el area destino
			ap.getAreaPromotorKey().setIdArea(idAreaDestino);
			mngrAreaPromotor.save(ap);
		}
	}

	private void saveParametrosContent(Integer idAreaOrigen, Integer idAreaDestino) throws Exception {
		// obtener parametros origen
		List<Parametro> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("parametroKey.idArea", idAreaOrigen));
		restrictions.add(Restrictions.eq("parametroKey.idSeccion", "CONTENTFOLDER"));

		items = (List<Parametro>) mngrParametro.search(restrictions);

		for (Parametro p : items) {
			// replicar registros para el area destino
			p.getParametroKey().setIdArea(idAreaDestino);
			p.setValor(null);
			mngrParametro.save(p);
		}
	}

	private void saveParametrosWS(Integer idAreaDestino, boolean isUpdate) throws Exception {
		Parametro turnoExt = new Parametro();
		turnoExt.setParametroKey(new ParametroKey());
		turnoExt.getParametroKey().setIdArea(idAreaDestino);
		turnoExt.getParametroKey().setIdSeccion("WEBSERVICES");
		turnoExt.getParametroKey().setIdClave("TURNOSEXTERNOS");
		turnoExt.setValor(isUpdate ? "1" : "0");// cuando es nueva queda en cero

		Parametro turnoExtI = new Parametro();
		turnoExtI.setParametroKey(new ParametroKey());
		turnoExtI.getParametroKey().setIdArea(idAreaDestino);
		turnoExtI.getParametroKey().setIdSeccion("WEBSERVICES");
		turnoExtI.getParametroKey().setIdClave("TURNOSEXTERNOSI");
		turnoExtI.setValor(isUpdate ? "1" : "0");// cuando es nueva queda en
													// cero

		if (isUpdate) {
			mngrParametro.update(turnoExt);
			mngrParametro.update(turnoExtI);
		} else {
			mngrParametro.save(turnoExt);
			mngrParametro.save(turnoExtI);
		}
	}

	private Integer saveFoliosPorArea(int idAreaOrigen, int idAreaDestino) throws Exception {
		HashMap<String, Object> params = new HashMap<>();
		Integer result = null;
		params.put("idAreaDestino", idAreaDestino);
		params.put("idAreaOrigen", idAreaOrigen);
		result = mngrFolio.execUpdateQuery("foliosPorArea", params);
		return result;
	}

	private void saveParametrosFoliosDoc(Integer idAreaOrigen, Integer idAreaDestino) throws Exception {
		// obtener parametros origen
		List<Parametro> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("parametroKey.idArea", idAreaOrigen));
		restrictions.add(Restrictions.eq("parametroKey.idSeccion", "FOLIODOC"));

		items = (List<Parametro>) mngrParametro.search(restrictions);

		for (Parametro p : items) {
			// replicar registros para el area destino
			p.getParametroKey().setIdArea(idAreaDestino);
			mngrParametro.save(p);
		}
	}

	private <T> void saveCat(Integer idAreaOrigen, Integer idAreaDestino, Class<T> clazz, EntityManager<T> mngr,
			String idProperty) throws Exception {
		saveCat(idAreaOrigen, idAreaDestino, clazz, mngr, idProperty, null);
	}

	private <T> void saveCat(Integer idAreaOrigen, Integer idAreaDestino, Class<T> clazz, EntityManager<T> mngr,
			String idProperty, String idExpDefault) throws Exception {
		//
		List<T> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("area.idArea", idAreaOrigen));
		restrictions.add(Restrictions.eq("activo", true));

		items = (List<T>) mngr.search(restrictions);

		for (T t : items) {

			T newT = (T) SerializationHelper.clone((Serializable) t);
			/*
			 * antes de poner el id en null para insertar, recuperamos el valor del
			 * expediente para saber si es el expediente default y si es guardamos la
			 * bandera para replicar el default del area
			 */
			boolean defaultExp = false;
			if (t instanceof TipoExpediente) {
				if (idExpDefault != null && idExpDefault.equals(((TipoExpediente) newT).getIdExpediente())) {
					defaultExp = true;
				}
			}

			// set id catalogo y area
			PropertyUtils.setProperty(newT, idProperty, null);
			if ((newT instanceof TipoInstruccion) || (newT instanceof TipoPrioridad)) {
				AreaAuxiliar areaAux = new AreaAuxiliar();
				areaAux.setIdArea(idAreaDestino);
				PropertyUtils.setNestedProperty(newT, "area", areaAux);
			} else {
				PropertyUtils.setNestedProperty(newT, "area.idArea", idAreaDestino);
			}
			mngr.save(newT);

			// si el objeto es tipoExpediente replicar el parametro de
			// expediente default para esa area
			if (newT instanceof TipoExpediente) {
				if (defaultExp) {
					saveExpedienteDefautl(idAreaDestino, ((TipoExpediente) newT).getIdExpediente());
				}
			}
		}
	}

	private String getExpedienteDefault(Integer idArea) throws Exception {
		List<Parametro> lst = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("parametroKey.idArea", idArea));
		restrictions.add(Restrictions.eq("parametroKey.idSeccion", "DEFAULT"));
		restrictions.add(Restrictions.eq("parametroKey.idClave", "IDEXPEDIENTE"));

		lst = (List<Parametro>) mngrParametro.search(restrictions);

		if (lst != null && lst.size() != 1) {
			throw new Exception("Numero de registros de default idExpediente != 1");
		}

		Parametro defaultExpParam = lst.get(0);
		String defaultExp = defaultExpParam.getValor();

		if (defaultExp != null && defaultExp.length() > 20) {
			defaultExp = defaultExp.substring(0, 20);
		}

		return defaultExp;
	}

	private String getNombreFolder(Area item) {
		// folder de area
		StringBuilder nombreFolder = new StringBuilder();
		nombreFolder.append(item.getInstitucion().getIdInstitucion()).append("_").append(item.getDescripcion())
				.append("_").append(item.getIdArea());

		return nombreFolder.toString();
	}

	private String saveFoldersParametros(Area area) throws Exception {
		StringBuilder result = new StringBuilder();
		String salto = System.getProperty("line.separator");

		List<Parametro> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("parametroKey.idArea", area.getIdArea()));
		restrictions.add(Restrictions.eq("parametroKey.idSeccion", "CONTENTFOLDER"));

		items = (List<Parametro>) mngrParametro.search(restrictions);

		for (Parametro p : items) {

			// crear los subfolders y actualizar registro con el contentId
			TipoExpediente newExp = new TipoExpediente();
			newExp.setArea(area);
			newExp.setDescripcion(p.getParametroKey().getIdClave());
			newExp.setContentId(p.getValor());
			String idFolderParam;
			boolean actualizaContentIdParam = false;
			try {
				idFolderParam = createExpedienteConValidacion(newExp);
				if (StringUtils.isBlank(p.getValor()) || (!idFolderParam.equalsIgnoreCase(p.getValor()))) {
					actualizaContentIdParam = true;

				}
			} catch (Exception e) {
				result.append("ERROR ACTUALIZANDO O CREADO SUBFOLDER " + newExp + " DEL AREA " + area.getIdArea())
						.append(salto);
				throw new Exception(">>> ERROR Creando Expediente " + newExp.getDescripcion());
			}
			if (actualizaContentIdParam) {
				p.setValor(idFolderParam);
				mngrParametro.update(p);

				result.append(
						"ACTUALIZADO O CREADO SUBFOLDER " + newExp.getDescripcion() + " DEL AREA " + area.getIdArea())
						.append(salto);
			}
		}
		return result.toString();
	}

	private String saveFoldersExpedientes(Area area) throws Exception {

		StringBuilder result = new StringBuilder();
		String salto = System.getProperty("line.separator");

		List<TipoExpediente> items = new ArrayList<>();
		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("area.idArea", area.getIdArea()));

		items = (List<TipoExpediente>) mngrTipoExpediente.search(restrictions);

		boolean actualizaContentIdExp = false;
		for (TipoExpediente te : items) {
			String idFolderParam;
			try {
				// crear los subfolders y actualizar registro con el contentId
				idFolderParam = createExpedienteConValidacion(te);
				if (!idFolderParam.equalsIgnoreCase(te.getContentId())) {
					actualizaContentIdExp = true;
					result.append(
							"ACTUALIZADO O CREADO SUBFOLDER " + te.getDescripcion() + " DEL AREA " + area.getIdArea())
							.append(salto);
				}
			} catch (Exception e) {
				result.append("ERROR ACTUALIZANDO O CREADO SUBFOLDER " + te.getDescripcion() + " DEL AREA "
						+ area.getIdArea()).append(salto);
				throw new Exception(">>> ERROR Creando Expediente " + te.getDescripcion());
			}
			// createSubfolderArea(folderIdArea, te.getDescripcion(),
			// additionalData);
			if (actualizaContentIdExp) {

				te.setContentId(idFolderParam);
				te.setActivo(true);
				mngrTipoExpediente.update(te);
			}
		}
		return result.toString();
	}

	public String createExpedienteConValidacion(TipoExpediente exp) throws Exception {
		String expObjectId = "";
		try {
			IEndpoint repo = EndpointDispatcher.getInstance();

			log.debug("::::: obtener el path del area");
			exp.setArea(mngrArea.fetch(exp.getArea().getIdArea()));
			if (exp.getArea() == null || exp.getArea().getContentId() == null) {
				throw new Exception("No se pudo recuperar el identificador del folder de area");
			}

			String pathArea = repo.getObjectPath(exp.getArea().getContentId());
			String pathSubfolder = "";
			if (StringUtils.isNotBlank(exp.getContentId()))
				pathSubfolder = repo.getObjectPath(exp.getContentId());
			String subFolderName = pathSubfolder.substring(pathSubfolder.lastIndexOf("/") + 1);
			if (StringUtils.isBlank(subFolderName)) {
				subFolderName = exp.getDescripcion();
			}
			// boolean existeObjectIdSubfolder = false;
			// if (null!=pathSubfolder && !pathSubfolder.contains("Bad ID
			// given") || !pathSubfolder.contains("[DM_API_E_EXIST]error")) {
			// existeObjectIdSubfolder = true;
			// }
			log.debug(":::::: verificar si ya existe el contentId");
			String pathExpediente = pathArea + "/" + subFolderName;
			if (repo.existeCarpeta(pathExpediente)) {
				log.debug(":::::: el folder existe, devolver el robjectid del expediente");
				expObjectId = repo.getFolderIdByPath(pathExpediente);

			} else {
				log.debug(":::::: el folder NO existe, crear expediente");
				expObjectId = repo.createFolderIntoId(exp.getArea().getContentId(),
						environment.getProperty("subfolderTypeArea"), //
						exp.getDescripcion());

				// linkear carpeta de area
				log.debug(":::::: el metodo createFolderIntoId ya hace el link");

				log.debug(":::::: setear ACL, se usa el mismo del folder de area");
				String aclArea = ((List<String>) repo.getObjectProperty(exp.getArea().getContentId(), "acl_name"))
						.get(0);
				repo.setACLByDQL(expObjectId, aclArea);

				log.debug(":::::: agregar atributos");
				Map<String, Object> properties = new HashMap<>();
				properties.put("acl_name", aclArea);
				properties.put("idarea", String.valueOf(exp.getArea().getIdArea()));
				properties.put("idsubfolder", exp.getIdExpediente());
				properties.put("idrecord", "1");
				repo.setProperties(expObjectId, properties);

				log.debug(" NUEVO FOLDER \"" + exp.getDescripcion() + " creado. ID :: " + expObjectId);

			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			setInactive(exp);
			throw new Exception("No fue posible crear el folder del expediente, el expediente ha quedado inactivo");
		}

		return expObjectId;
	}

	/**
	 * 
	 * @param exp
	 */
	private void setInactive(TipoExpediente exp) {
		if (exp != null && exp.getIdExpediente() != null) {
			exp.setActivo(false);
			mngrTipoExpediente.update(exp);
		}
	}

	private void saveExpedienteDefautl(Integer idArea, String idExpediente) throws Exception {
		Parametro paramExp = new Parametro();
		paramExp.setParametroKey(new ParametroKey(idArea, "DEFAULT", "IDEXPEDIENTE"));
		paramExp.setValor(idExpediente);

		mngrParametro.save(paramExp);
	}

}
