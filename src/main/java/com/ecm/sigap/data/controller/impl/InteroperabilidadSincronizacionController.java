/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAcceptableException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.AreaAuxiliar;
import com.ecm.sigap.data.model.Funcionario;
import com.ecm.sigap.data.model.Institucion;
import com.ecm.sigap.data.model.interop.WsSincronizaCompletaDetalle;
import com.ecm.sigap.data.model.util.TipoRegistroWsOpe;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;
/**
 * Controlador REST para la seccion interoperabilidad Sincronizacion.
 * 
 * @author Gustavo Vielma
 * @version 2.0
 *
 */
@RestController
public class InteroperabilidadSincronizacionController extends CustomRestController {

	/** Manejador para el tipo {@link Funcionario }. */
	@Autowired
	private FuncionarioController funcionarioController;

	/** Manejador para el tipo {@link Area }. */
	@Autowired
	private AreaController areaController;

	/** Manejador para el tipo {@link Institucion }. */
	@Autowired
	private InstitucionController institucionController;

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(InteroperabilidadSincronizacionController.class);

	/**
	 * Sincronizar elemento.
	 *
	 * @param body
	 *            the body
	 * @return the response entity
	 * @throws Exception
	 *             the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Sincroniza institucion ope", notes = "Sincroniza la institucion seleccionada")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interop/sincronizarElemento", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Object> sincronizarElemento(
			@RequestBody(required = true) Map<String, String> body) throws Exception {
		try {

			Integer idElementoOpe = Integer.parseInt(body.get("idElementoOpe").toString());
			Integer idElementoLocal = Integer.parseInt(body.get("idElementoLocal").toString());
			String tipoObjetoLocal = body.get("tipoObjetoLocal").toString();

			// tipoObjetoLoca puede ser I (Institucion), A(Area) o U (Usuario)

			// Se consuta el elemento ope (Intitucion, Area, o Usuario)
			WsSincronizaCompletaDetalle elementoOpe = mngrWsSincronizaCompletaDetalle.fetch(idElementoOpe);

			if (null != idElementoOpe && null != idElementoLocal && StringUtils.isNotBlank(tipoObjetoLocal)
					&& null != elementoOpe) {

				// Sincronizar Institucion
				if (TipoRegistroWsOpe.REGISTRO_INSTITUCION.equals(elementoOpe.getTipoRegistro())
						&& "I".equals(tipoObjetoLocal)) {

					Institucion institucionLocal = mngrInstitucion.fetch(idElementoLocal);

					// Validar si la institucion ope ya fue sincronizada con otra Institucion local
					{
						List<Criterion> restrictions = new ArrayList<Criterion>();
						restrictions.add(Restrictions.eq("idExterno", elementoOpe.getNoDistinguido()));
						List<Institucion> lstInstitucionLocal = (List<Institucion>) mngrInstitucion
								.search(restrictions);

						if (null != lstInstitucionLocal && !lstInstitucionLocal.isEmpty()) {
							throw new NotAcceptableException(
									"La institucion Ope seleccionada ya ha sido sincronizada anteriormente");
						}
					}

					// Validar si la institucion local ya fue sincronizada con otra Institucion ope
					{
						if (null != institucionLocal.getIdExterno()) {
							throw new NotAcceptableException(
									"La institucion local seleccionada ya ha sido sincronizada anteriormente");
						} else if (!institucionLocal.getDescripcion().equals(elementoOpe.getNombre())
								&& institucionLocal.getUri().equals(elementoOpe.getCustom5())) {
							throw new NotAcceptableException(
									"La institucion local seleccionada no ha sido vinculada durante la suscripcion de intancia");
						}
					}
					// Se agregan datos para actualizar
					institucionLocal.setActivo(true);
					institucionLocal.setTipo("E");
					institucionLocal.setInteropera(true);
					institucionLocal.setDescripcion(elementoOpe.getNombre());
					institucionLocal.setIdExterno(elementoOpe.getNoDistinguido());

					// Se actualiza la institucion local
					institucionController.save(institucionLocal);

					// Se valida que la institucion ope no tenga areas ni usuario
					if (!hasChildsOpe(elementoOpe.getIdRegistro())) {
						// Se procede a eliminar la institucion de la ope
						mngrWsSincronizaCompletaDetalle.delete(elementoOpe);
					}

					// Sincronizacion de AreaOpe en Institucion o en Area
				} else if (TipoRegistroWsOpe.REGISTRO_AREA.equals(elementoOpe.getTipoRegistro())) {

					// Sincronizacion de AreaOpe en Institucion
					if ("I".equals(tipoObjetoLocal)) {
						Institucion institucionLocal = mngrInstitucion.fetch(idElementoLocal);

						// Sincronizacion de AreaOpe en Institucion
						// Pendiente parece hay un error (trata al elementoLocal que es un idInstitucion
						// como un idArea)

						// Sincroniza AreaOpe en Area Local
					} else if ("A".equals(tipoObjetoLocal)) {

						boolean titularAreaNuevo = false;

						// Consulta el area local seleccionada
						Area areaOld_ = mngrArea.fetch(idElementoLocal);

						// Validar que el area Ope no haya sido sincronizada anteriormente
						{
							List<Criterion> restrictions = new ArrayList<Criterion>();
							restrictions.add(Restrictions.eq("idExterno", elementoOpe.getNoDistinguido()));
							List<Area> lstAreaLocal = (List<Area>) mngrInstitucion.search(restrictions);

							if (null != lstAreaLocal && !lstAreaLocal.isEmpty()) {
								throw new NotAcceptableException(
										"El Area Ope seleccionada ya ha sido sincronizada anteriormente");
							}
						}

						// Validar que el area Local no haya sido sincronizada anteriormente
						{
							if (null != areaOld_.getIdExterno()) {
								throw new NotAcceptableException(
										"El Area local seleccionada ya ha sido sincronizada anteriormente");
							}
						}

						// Sincroniza AreaOpe en Area Local
						Area areaLocal = construyeArea(areaOld_, elementoOpe, false);

						// Obtener los datos de los usuarios recibidos que tienen como
						// IDPADRE el IDENTIFICADOR del area
						List<WsSincronizaCompletaDetalle> lstUsuariosOpe = getInfoDetalleOPEidPadreTipo(
								elementoOpe.getIdRegistro().toString(), "U");

						// Sincroniza datos Area OPE con los del Area Local
						areaLocal = mezclaAreas(areaLocal, areaOld_);
						// Actualizacion de Area en BD, Antes de procesar y eliminar los usuarios
						areaController.save(areaLocal);

						// Sincronizar usuarios del AreaOpe al area Local
						for (WsSincronizaCompletaDetalle usuarioOpe : lstUsuariosOpe) {
							Funcionario funcionarioExt = construyeUsuario(areaLocal.getIdArea(), usuarioOpe);

							// Validar si existe el funcionario, para saber si se debe crear o actualizar
							List<Funcionario> lstFuncionario = (List<Funcionario>) funcionarioController
									.search(funcionarioExt).getBody();

							if (lstFuncionario.size() > 0) {
								// El usuario existe, se actualiza
								funcionarioExt.setId(lstFuncionario.get(0).getId());
							}
							funcionarioController.save(funcionarioExt);

							// Establecer los datos del titular del Area
							if (usuarioOpe.getCustom3().equals("true")) {

								areaLocal.setTitularCargo(funcionarioExt.getCargo());
								areaLocal.setTitular(mngrRepresentante.fetch(funcionarioExt.getId()));
								titularAreaNuevo = true;
							}
							// eliminar usuario ya creado
							mngrWsSincronizaCompletaDetalle.delete(usuarioOpe);
						}
						// Vuelve a actualizar el area si encontro un usuario titular en AreaOPE
						if (titularAreaNuevo) {
							areaController.save(areaLocal);
						}

						// Eliminar Area sincronizada si no tiene areas o usurios hijas
						if (!hasChildsOpe(elementoOpe.getIdRegistro())) {
							mngrWsSincronizaCompletaDetalle.delete(elementoOpe);
							// FALTA ELIMINAR WsSincronizaCompleta
							// svcquerycat.eliminarInstitucionDetalleSincronizada(idOPE);
						}

					}

				} else if (TipoRegistroWsOpe.REGISTRO_USUARIO.equals(elementoOpe.getTipoRegistro())
						&& "U".equals(tipoObjetoLocal)) {

					// Sincronizacion de UsuarioOpe a UsuarioLocal

					Funcionario funcionarioLocal = mngrFuncionario.fetch(idElementoLocal);

					if (null != funcionarioLocal) {
						// Validar que el usuarioLocal no haya sido sincronizado anteriormente
						{
							if (null != funcionarioLocal.getIdExterno()) {
								throw new NotAcceptableException(
										"El usuario local seleccionado ya ha sido sincronizado anteriormente");
							}
						}

						funcionarioLocal = construyeUsuario(funcionarioLocal.getArea().getIdArea(), elementoOpe);

						// Validar si existe el funcionario, para saber si se debe crear o actualizar
						List<Funcionario> lstFuncionario = (List<Funcionario>) funcionarioController
								.search(funcionarioLocal).getBody();

						if (lstFuncionario.size() > 0) {
							// El usuario existe, se setea el id para que actualize
							funcionarioLocal.setId(lstFuncionario.get(0).getId());
						}

						// Se crea o Actualiza el funcionario (usuario)
						funcionarioController.save(funcionarioLocal);

						// Se elimina usuario Ope
						mngrWsSincronizaCompletaDetalle.delete(elementoOpe);

					} else {
						return new ResponseEntity<Object>(new Object(), HttpStatus.BAD_REQUEST);
					}

				} else {
					return new ResponseEntity<Object>(new Object(), HttpStatus.BAD_REQUEST);
				}
			} else {
				return new ResponseEntity<Object>(new Object(), HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<Object>(new Object(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Crear estructura.
	 *
	 * @param body
	 *            the body
	 * @return the response entity
	 * @throws Exception
	 *             the exception
	 */
	
	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Crear estructura", notes = "Crea una estructura para la institucion seleccionada")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sacg-user-id", value ="Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-area-id", value ="Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-content-user", value ="Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-token", value ="Token cifrado", required = true, dataType = "int", paramType = "header"),
		@ApiImplicitParam(name = "sacg-user-key", value ="Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
	@ApiResponses (value = {
			@ApiResponse (code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse (code = 201, message = "Creado"),
			@ApiResponse (code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse (code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse (code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse (code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse (code = 500, message = "Error del servidor")})
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/interop/crearEstructura", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Object> crearEstructura(@RequestBody(required = true) Map<String, String> body)
			throws Exception {
		try {

			Integer idElementoOpe = Integer.parseInt(body.get("idElementoOpe").toString());
			Integer idElementoLocal = Integer.parseInt(body.get("idElementoLocal").toString());
			String tipoObjetoLocal = body.get("tipoObjetoLocal").toString();

			// tipoObjetoLoca puede ser I (Institucion), A(Area) o U (Usuario)

			// Se consuta el elemento ope (Intitucion, Area, o Usuario)
			WsSincronizaCompletaDetalle elementoOpe = mngrWsSincronizaCompletaDetalle.fetch(idElementoOpe);

			if (null != idElementoOpe && null != idElementoLocal && StringUtils.isNotBlank(tipoObjetoLocal)
					&& null != elementoOpe) {

				// Crear Estructura de institucionOpe
				if (TipoRegistroWsOpe.REGISTRO_INSTITUCION.equals(elementoOpe.getTipoRegistro())
						&& StringUtils.isBlank(body.get("idElementoOpe").toString())) {

					// Crear Estructura Institucion
					Institucion nuevaInstitucionLocal = new Institucion();

					// Por defecto, todas las Instituciones creadas por Sincronizacion seran ACTIVAS
					// y EXTERNAS
					nuevaInstitucionLocal.setActivo(true);
					nuevaInstitucionLocal.setTipo("E");
					nuevaInstitucionLocal.setInteropera(true);

					// Establecemos los datos obtenidos de OPE
					nuevaInstitucionLocal.setAbreviatura(elementoOpe.getNoDistinguido());
					nuevaInstitucionLocal.setClave(elementoOpe.getNoDistinguido());
					nuevaInstitucionLocal.setDescripcion(elementoOpe.getNombre());
					nuevaInstitucionLocal.setIdExterno(elementoOpe.getNoDistinguido());

					nuevaInstitucionLocal = institucionController.save(nuevaInstitucionLocal).getBody();

					// ProcesarEstructura Area
					crearEstructuraArea(nuevaInstitucionLocal, null, elementoOpe);

					// Crear Estructura AreaOpe en AreaLocal
				} else if (TipoRegistroWsOpe.REGISTRO_AREA.equals(elementoOpe.getTipoRegistro())
						&& "A".equals(tipoObjetoLocal) && null != idElementoLocal) {

					Area areaLocal = mngrArea.fetch(idElementoLocal);
					if (null != areaLocal) {
						crearEstructuraArea(areaLocal.getInstitucion(), areaLocal.getIdArea(), elementoOpe);
					} else {
						throw new BadRequestException("El Area local no exite en bd");
					}
					// Crear estructura Usuario en Area
				} else if (TipoRegistroWsOpe.REGISTRO_USUARIO.equals(elementoOpe.getTipoRegistro())
						&& "A".equals(tipoObjetoLocal) && null != idElementoLocal) {

					Area areaLocal = mngrArea.fetch(idElementoLocal);
					if (null != areaLocal) {
						Funcionario funcionarioLocal = new Funcionario();

						funcionarioLocal = construyeUsuario(areaLocal.getIdArea(), elementoOpe);

						// Validar si existe el funcionario, para saber si se debe crear o actualizar
						List<Funcionario> lstFuncionario = (List<Funcionario>) funcionarioController
								.search(funcionarioLocal).getBody();

						if (lstFuncionario.size() > 0) {
							// El usuario existe, se setea el id para que actualize
							funcionarioLocal.setId(lstFuncionario.get(0).getId());
						}

						// Se crea o Actualiza el funcionario (usuario)
						funcionarioController.save(funcionarioLocal);

						// Se elimina usuario Ope
						mngrWsSincronizaCompletaDetalle.delete(elementoOpe);

					}
				} else {
					throw new BadRequestException("El Area local no exite en bd");
				}

			} else {
				return new ResponseEntity<Object>(new Object(), HttpStatus.BAD_REQUEST);
			}

			return new ResponseEntity<Object>(new Object(), HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 * Crear estructura area.
	 *
	 * @param institucionLocal
	 *            the institucion local
	 * @param idAreaPadreLocal
	 *            the id area padre local
	 * @param elementoOpe
	 *            the elemento ope
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private void crearEstructuraArea(Institucion institucionLocal, Integer idAreaPadreLocal,
			WsSincronizaCompletaDetalle elementoOpe) throws Exception {

		// Obtiene las Areas hijas de la InstitucionOpe
		List<WsSincronizaCompletaDetalle> areasOpeHijas = getInfoDetalleOPEidPadreTipo(
				elementoOpe.getIdRegistro().toString(), TipoRegistroWsOpe.REGISTRO_AREA.getValue());

		boolean titularAreaNuevo = false;

		// Procesar areashijasOpe de la InstitucionOpe
		for (WsSincronizaCompletaDetalle areaOpeHija : areasOpeHijas) {
			Area nuevaAreaLocal = new Area();
			// Se construye Objeto Area con datos del AreaOpe
			nuevaAreaLocal = construyeArea(nuevaAreaLocal, elementoOpe, true);

			if (null != institucionLocal) {
				nuevaAreaLocal.setInstitucion(institucionLocal);
			}
			if (null != idAreaPadreLocal) {
				nuevaAreaLocal.setIdAreaPadre(idAreaPadreLocal);
			}
			// Se crea la nueva area local
			nuevaAreaLocal = areaController.save(nuevaAreaLocal).getBody();

			// Obtiene los Usuario hijos del AreaOpe
			List<WsSincronizaCompletaDetalle> usuariosOpeHijos = getInfoDetalleOPEidPadreTipo(
					areaOpeHija.getIdRegistro().toString(), TipoRegistroWsOpe.REGISTRO_USUARIO.getValue());

			// Procesar usuariohijosOpe del AreaOpe
			for (WsSincronizaCompletaDetalle usuarioHijoOpe : usuariosOpeHijos) {
				Funcionario nuevoFuncionarioExt = new Funcionario();

				// Se construye Objeto Funcionario con datos del UsuarioOpe
				nuevoFuncionarioExt = construyeUsuario(nuevaAreaLocal.getIdArea(), elementoOpe);

				// Validar si existe el funcionario, para saber si se debe crear o actualizar
				List<Funcionario> lstFuncionario = (List<Funcionario>) funcionarioController.search(nuevoFuncionarioExt)
						.getBody();

				if (lstFuncionario.size() > 0) {
					// El usuario existe, se setea el id para que actualize
					nuevoFuncionarioExt.setId(lstFuncionario.get(0).getId());
				}
				// Se crea el nuevo funcionario para el area
				funcionarioController.save(nuevoFuncionarioExt);

				// Establecer los datos del titular del Area
				if (usuarioHijoOpe.getCustom3().equals("true")) {
					nuevaAreaLocal.setTitularCargo(nuevoFuncionarioExt.getCargo());
					nuevaAreaLocal.setTitular(mngrRepresentante.fetch(nuevoFuncionarioExt.getId()));
					titularAreaNuevo = true;
				}
			}
			// Vuelve a actualizar la nuevaAreaLocal si encontro un usuario titular en
			// AreaOPE
			if (titularAreaNuevo) {
				areaController.save(nuevaAreaLocal);
			}

			if (hasChildsOpe(areaOpeHija.getIdRegistro())) {
				// crea la estructura de subArea
				crearEstructuraArea(nuevaAreaLocal.getInstitucion(), nuevaAreaLocal.getIdArea(), areaOpeHija);
			}

		}

	}

	/**
	 * Checks for childs ope.
	 *
	 * @param idRegistroOpe
	 *            the id registro ope
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	public boolean hasChildsOpe(Integer idRegistroOpe) throws Exception {

		String query = String
				.format("select count(*) from wssincronizacompletadetalle where idpadre in (select identificador "
						+ " from WSSINCRONIZACOMPLETADETALLE " + " where idregistro = %s )", idRegistroOpe);
		Boolean result = false;
		List<BigDecimal> hasChildsOpe = (List<BigDecimal>) mngrWsSincronizaCompletaDetalle.execNativeQuery(query, null);

		if (null != hasChildsOpe && !hasChildsOpe.isEmpty() && hasChildsOpe.get(0).intValue() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * Construye area.
	 *
	 * @param area
	 *            the area
	 * @param elementoOpe
	 *            the elemento ope
	 * @param creaArea
	 *            the crea area
	 * @return the area
	 * @throws Exception
	 *             the exception
	 */
	public Area construyeArea(Area area, WsSincronizaCompletaDetalle elementoOpe, boolean creaArea) throws Exception {

		String noDistringuido = elementoOpe.getNoDistinguido();

		// Si el area se esta creando el Area y no se enviaron los datos de
		// Nombre Distringuido, se usan los datos de Identificador. Para el caso
		// de sincronizacion no se hace este cambio ya que se colocan los que
		// tiene actualmente la institucion
		if ((null == noDistringuido || noDistringuido.isEmpty() || "null".equals(noDistringuido)) && creaArea) {

			noDistringuido = elementoOpe.getIdentificador();
		}
		area.setActivo(true);
		area.setInteropera(true);
		area.setClaveDepartamental(noDistringuido);
		area.setClave(noDistringuido);
		area.setCveArea(noDistringuido);
		area.setDescripcion(elementoOpe.getNombre());
		area.setIdExterno(elementoOpe.getIdentificador());
		area.setSiglas(noDistringuido);

		return area;
	}

	/**
	 * Gets the info detalle OP eid padre tipo.
	 *
	 * @param idpadre
	 *            the idpadre
	 * @param tiregistro
	 *            the tiregistro
	 * @return the info detalle OP eid padre tipo
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	public List<WsSincronizaCompletaDetalle> getInfoDetalleOPEidPadreTipo(String idpadre, String tiregistro)
			throws Exception {
		StringBuffer cmd = new StringBuffer();
		cmd.append(" SELECT IDREGISTRO FROM WSSINCRONIZACOMPLETADETALLE ");
		cmd.append(" WHERE IDPADRE in ( select identificador ");
		cmd.append(" from WSSINCRONIZACOMPLETADETALLE ");
		cmd.append(" where idregistro = ");
		cmd.append(idpadre);
		cmd.append(" ) ");
		if (tiregistro != null) {
			cmd.append(" and TIREGISTRO = '");
			cmd.append(tiregistro);
			cmd.append("'");
		}

		List<BigDecimal> items = (List<BigDecimal>) mngrWsSincronizaCompletaDetalle.execNativeQuery(cmd.toString(),
				null);
		List<Integer> idsRegistro = items.stream().map(i -> i.intValue()).collect(Collectors.toList());

		List<WsSincronizaCompletaDetalle> lstElementosOpe = new ArrayList<>();

		if (null != idsRegistro && !idsRegistro.isEmpty()) {
			// * * * * * * * * * * * * * * * * * * * * * *
			List<Criterion> restrictions = new ArrayList<>();

			restrictions.add(Restrictions.in("idRegistro", idsRegistro));

			// * * * * * * * * * * * * * * * * * * * * * *
			lstElementosOpe = (List<WsSincronizaCompletaDetalle>) mngrWsSincronizaCompletaDetalle.search(restrictions);

		}
		return lstElementosOpe;

	}

	/**
	 * Construye usuario.
	 *
	 * @param idArea
	 *            the id area
	 * @param elementoOpe
	 *            the elemento ope
	 * @return the funcionario
	 * @throws Exception
	 *             the exception
	 */
	public Funcionario construyeUsuario(Integer idArea, WsSincronizaCompletaDetalle elementoOpe) throws Exception {

		String[] nombreCompletoUsuario;
		String nombre = "";
		String paterno = "";
		String materno = "";
		int tnombre = 0;

		Funcionario funcionarioExt = new Funcionario();
		AreaAuxiliar areaFuncionario = new AreaAuxiliar();
		areaFuncionario.setIdArea(idArea);
		funcionarioExt.setArea(areaFuncionario);

		String noDistringuido = elementoOpe.getNoDistinguido();

		if (null == noDistringuido || noDistringuido.isEmpty() || "null".equals(noDistringuido)) {
			noDistringuido = elementoOpe.getIdentificador();
		}

		funcionarioExt.setIdExterno(noDistringuido);
		funcionarioExt.setCargo(elementoOpe.getCustom1());
		funcionarioExt.setEmail(elementoOpe.getCustom2());
		funcionarioExt.setIdTipo("A");
		funcionarioExt.setActivosn(true);

		// Separacion de los elementos del nombre
		nombreCompletoUsuario = elementoOpe.getNombre().split(" ");
		tnombre = nombreCompletoUsuario.length;
		materno = nombreCompletoUsuario[--tnombre];
		paterno = nombreCompletoUsuario[--tnombre];
		if (tnombre == 1) {
			nombre = nombreCompletoUsuario[0];
		} else {
			for (int i = 0; i < tnombre; i++) {
				nombre += nombreCompletoUsuario[i];
				if (i < tnombre - 1) {
					nombre += " ";
				}
			}
		}
		funcionarioExt.setNombres(nombre);

		// Limpiamos el nombre ya que se utiliza el mismo objeto
		// en las iteracionessobre el los usuarios de un area
		nombre = "";
		funcionarioExt.setPaterno(paterno);
		funcionarioExt.setMaterno(materno);

		return funcionarioExt;
	}

	/**
	 * Mezcla areas.
	 *
	 * @param areaLocalSinc
	 *            the area local sinc
	 * @param areaOld
	 *            the area old
	 * @return the area
	 */
	public Area mezclaAreas(Area areaLocalSinc, Area areaOld) {

		// Valida que los datos que toma del areaOpe, si estan vacio o nulos coloca los
		// anteriores
		// no realiza la validacion en el construyeArea porque es usado por otros
		// metodos donde si los deja tal cual como está en la Ope.

		// Validamos los valores del campo CLAVE
		if (StringUtils.isBlank(areaLocalSinc.getClaveDepartamental())
				|| "null".equals(areaLocalSinc.getClaveDepartamental())) {

			areaLocalSinc.setClave(areaOld.getClave());
		}

		// Validamos los valores del campo CVEAREA
		if (StringUtils.isBlank(areaLocalSinc.getCveArea()) || "null".equals(areaLocalSinc.getCveArea())) {

			areaLocalSinc.setCveArea(areaOld.getCveArea());
		}

		// Validamos los valores del campo SIGLAS
		if (StringUtils.isBlank(areaLocalSinc.getSiglas()) || "null".equals(areaLocalSinc.getSiglas())) {

			areaLocalSinc.setSiglas(areaOld.getSiglas());
		}

		// Validamos los valores del campo TITULAR CARGO
		if (StringUtils.isBlank(areaLocalSinc.getTitularCargo()) || "null".equals(areaLocalSinc.getTitularCargo())) {

			areaLocalSinc.setTitularCargo(areaOld.getTitularCargo());
		}

		// Validamos los valores del campo TITULAR USUARIO
		if (null == areaLocalSinc.getTitular()) {

			areaLocalSinc.setTitular(areaOld.getTitular());
		}

		// Validamos los valores del campo CLAVECDD
		if (StringUtils.isBlank(areaLocalSinc.getClave()) || "null".equals(areaLocalSinc.getClave())) {

			areaLocalSinc.setClave(areaOld.getClave());
		}

		// Asignamos el Identificador del Area padre
		areaLocalSinc.setIdAreaPadre(areaOld.getIdAreaPadre());
		return areaLocalSinc;
	}
}