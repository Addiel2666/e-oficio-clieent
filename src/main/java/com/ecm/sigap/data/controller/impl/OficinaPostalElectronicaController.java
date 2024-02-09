/**
 * Copyright (c) 2024 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.util.TreeNode;
import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Institucion;
import com.ecm.sigap.data.model.Usuario;
import com.ecm.sigap.ope.client.OpeClient;
import com.ecm.sigap.ope.client.impl.OpeClientImpl;
import com.ecm.sigap.ope.dao.model.Configuration;
import com.ecm.sigap.ope.dao.model.RegistroInstancia;
import com.ecm.sigap.ope.dao.model.SincronizacionData;
import com.ecm.sigap.ope.dao.model.SincronizacionDataAreas;
import com.ecm.sigap.ope.dao.model.SincronizacionDataUsuarios;
import com.ecm.sigap.ope.dao.service.RepositoryConfiguration;
import com.ecm.sigap.ope.dao.service.RepositoryRegistroInstancia;
import com.ecm.sigap.ope.dao.service.RepositorySincronizacionDirectorio;
import com.ecm.sigap.ope.model.ResponseRecibirSubscripcion;
import com.ecm.sigap.ope.model.ResponseSincronizacionCompleta;
import com.ecm.sigap.ope.model.StatusRegistro;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@RestController
public class OficinaPostalElectronicaController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(OficinaPostalElectronicaController.class);

	/** Repositorio para el tipo {@link RegistroInstancia} */
	@Autowired
	@Qualifier("repositoryRegistroInstancia")
	protected RepositoryRegistroInstancia repoRegistroInstancia;

	/** Repositorio para el tipo {@link RepositoryConfiguration} */
	@Autowired
	@Qualifier("repositoryConfiguration")
	protected RepositoryConfiguration repoConfiguration;

	/** Repositorio para el tipo {@link SincronizacionData} */
	@Autowired
	@Qualifier("repositorySincronizacionDirectorio")
	protected RepositorySincronizacionDirectorio repoSincronizacionDirectorio;

	@RequestMapping(value = "/oficinapostalelectronica/registros", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<RegistroInstancia>> registros(
			@RequestBody(required = false) RegistroInstancia so) throws Exception {
		try {

			List<RegistroInstancia> registros = new ArrayList<>();

			registros = repoRegistroInstancia.search(so);

			return new ResponseEntity<List<RegistroInstancia>>(registros, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/oficinapostalelectronica/solicitarRegistro", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<RegistroInstancia> registro(@RequestBody RegistroInstancia body)
			throws Exception {
		try {

			Configuration nombreCorto = repoConfiguration.fetch("ope-nombre-corto");
			Configuration nombre = repoConfiguration.fetch("ope-nombre");
			Configuration url = repoConfiguration.fetch("ope-url");

			OpeClient client = OpeClientImpl.instanciate(body.getUrl(), nombreCorto.getValue());

			ResponseRecibirSubscripcion r = client.solicitarSubscripcion(nombre.getValue(), nombreCorto.getValue(),
					url.getValue());

			RegistroInstancia item = new RegistroInstancia();
			item.setAlias(body.getAlias());
			item.setDescripcion(body.getDescripcion());
			item.setUrl(body.getUrl());
			item.setStatus(StatusRegistro.REGISTRADO);
			item.setFechaRegistro(new Date());

			item = repoRegistroInstancia.save(item);

			client.confirmarSubscripcion(item.getId().toString(), nombreCorto.getValue(), url.getValue());

			item.setStatus(StatusRegistro.CONFIRMADO);
			repoRegistroInstancia.update(item);

			// obtener catalogo completo de la institucion,
			ResponseSincronizacionCompleta catalogo = client.solicitarSincronizacionCompleta(nombre.getValue(),
					nombreCorto.getValue(), url.getValue());

			// GUARDAR CATALOGO A BD
			SincronizacionData data = new SincronizacionData();
			data.setFechaRegistro(new Date());
			data.setIdVersion(catalogo.getVersionCatalogo());
			data.setIdRegistro(item.getId());

			List<SincronizacionDataAreas> dataAreas = catalogo.getAreasInteropera()//
					.stream().map(area -> {

						SincronizacionDataAreas objArea = new SincronizacionDataAreas();
						objArea.setIdExterno(area.getIdExterno());
						objArea.setDescripcion(area.getDescripcion());
						objArea.setIdAreaPadre(area.getAreaPadre());
						objArea.setIdUsuarioTitular(area.getTitular());

						List<SincronizacionDataUsuarios> dataUsuarios = area.getUsuario()//
								.stream().map(usuario -> {

									SincronizacionDataUsuarios objUsuario = new SincronizacionDataUsuarios();
									objUsuario.setNombreCompleto(usuario.getNombre());
									objUsuario.setPuesto(usuario.getPuesto());
									objUsuario.setCorreoElectronico(usuario.getCorreoElectronico());
									return objUsuario;

								}).collect(Collectors.toList());

						objArea.setUsuarios(dataUsuarios);

						return objArea;

					}).collect(Collectors.toList());

			data.setAreas(dataAreas);
			repoSincronizacionDirectorio.save(data);

			item.setVersionCatalogo(catalogo.getVersionCatalogo());
			repoRegistroInstancia.update(item);

			return new ResponseEntity<RegistroInstancia>(body, HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/oficinapostalelectronica/asignarInstitucion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<RegistroInstancia> asignarInstitucion(
			@RequestParam(value = "idRegistro", required = true) Integer idRegistro, //
			@RequestParam(value = "idInstitucion", required = true) Integer idInstitucion) //
			throws Exception {
		try {

			RegistroInstancia registro = repoRegistroInstancia.fetch(idRegistro);
			Institucion inst = mngrInstitucion.fetch(idInstitucion);

			if (registro == null || inst == null //
					|| registro.getIdInstitucion() != null //
					|| registro.getStatus() != StatusRegistro.CONFIRMADO)
				return new ResponseEntity<RegistroInstancia>(registro, HttpStatus.BAD_REQUEST);

			registro.setIdInstitucion(idInstitucion);

			repoRegistroInstancia.update(registro);

			return new ResponseEntity<RegistroInstancia>(registro, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/oficinapostalelectronica/config", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, String>> obtenerConfiguracion() //
			throws Exception {
		try {

			Map<String, String> response = new HashMap<>();

			Configuration nombreCorto = repoConfiguration.fetch("ope-nombre-corto");
			Configuration nombre = repoConfiguration.fetch("ope-nombre");
			Configuration url = repoConfiguration.fetch("ope-url");
			Configuration idInstitucion  = repoConfiguration.fetch("ope-id-institucion");

			response.put(nombreCorto.getKey(), nombreCorto.getValue());
			response.put(nombre.getKey(), nombre.getValue());
			response.put(url.getKey(), url.getValue());
			response.put(idInstitucion.getKey(), idInstitucion.getValue());

			return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	@RequestMapping(value = "/oficinapostalelectronica/treeInstitucion", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<TreeNode<Object>> getTreeInstitucionOpe(
			@RequestParam(value = "id", required = true) Integer id) throws Exception {
		try {

			Institucion institucion = mngrInstitucion.fetch(id);

			TreeNode<Object> top;

			if (null != institucion) {

				HashMap<String, String> tmp = new HashMap<>();
				tmp.put("descripcion", institucion.getDescripcion());
				tmp.put("id", institucion.getIdInstitucion().toString());
				tmp.put("idExterno", institucion.getIdExterno());
				tmp.put("type", "INSTITUCION");

				top = new TreeNode<Object>(tmp);
				getChildAreas(top, institucion);

			} else {
				return new ResponseEntity<TreeNode<Object>>(new TreeNode<Object>(null), HttpStatus.OK);
			}

			return new ResponseEntity<TreeNode<Object>>(top, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}
	}

	/**
	 * Obtener Areas direccion general,
	 * 
	 * @param nodeTop
	 * @param institucion
	 */
	@SuppressWarnings("unchecked")
	private void getChildAreas(TreeNode<Object> nodeTop, Institucion institucion) {

		try {

			List<Criterion> restrictions = new ArrayList<>();
			restrictions.add(Restrictions.eq("activo", true));
			restrictions.add(Restrictions.eq("institucion", institucion));
			restrictions.add(Restrictions.isNull("idAreaPadre"));

			List<Area> areas = (List<Area>) mngrArea.search(restrictions);

			areas.forEach(area_ -> {

				HashMap<String, String> tmp = new HashMap<>();
				tmp.put("descripcion", area_.getDescripcion());
				tmp.put("id", area_.getIdArea().toString());
				tmp.put("idExterno", area_.getIdExterno());
				tmp.put("type", "AREA");

				TreeNode<Object> nodeArea = new TreeNode<Object>(tmp);

				getChildUsuarios(nodeArea, area_);
				getChildAreas(nodeArea, institucion, area_);

				nodeTop.add(nodeArea);

			});

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}

	}

	/**
	 * Obtener Subareas hijas,
	 * 
	 * @param nodeArea
	 * @param institucion
	 * @param area
	 */
	@SuppressWarnings("unchecked")
	private void getChildAreas(TreeNode<Object> nodeArea, Institucion institucion, Area area) {

		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("activo", true));
		restrictions.add(Restrictions.eq("institucion", institucion));
		restrictions.add(Restrictions.eq("idAreaPadre", area.getIdArea()));

		List<Area> subAreas = (List<Area>) mngrArea.search(restrictions);

		subAreas.forEach(subArea -> {

			HashMap<String, String> tmp = new HashMap<>();
			tmp.put("descripcion", subArea.getDescripcion());
			tmp.put("id", subArea.getIdArea().toString());
			tmp.put("idExterno", subArea.getIdExterno());
			tmp.put("type", "AREA");

			TreeNode<Object> nodeSubArea = new TreeNode<Object>(tmp);

			getChildUsuarios(nodeArea, subArea);
			getChildAreas(nodeArea, institucion, subArea);

			nodeArea.add(nodeSubArea);

		});

	}

	/**
	 * 
	 * Obtener usarios del area,
	 * 
	 * @param nodeArea
	 * @param area_
	 */
	@SuppressWarnings("unchecked")
	private void getChildUsuarios(TreeNode<Object> nodeArea, Area area_) {

		List<Criterion> restrictions = new ArrayList<>();
		restrictions.add(Restrictions.eq("activo", true));
		restrictions.add(Restrictions.eq("idArea", area_.getIdArea()));

		List<Usuario> usuarios = (List<Usuario>) mngrUsuario.search(restrictions);

		usuarios.forEach(user_ -> {

			HashMap<String, String> tmp = new HashMap<>();
			tmp.put("descripcion",
					user_.getApellidoPaterno()
							+ (StringUtils.isBlank(user_.getMaterno()) ? "" : " " + user_.getMaterno()) + ", "
							+ user_.getNombres());
			tmp.put("id", user_.getIdUsuario());
			tmp.put("idExterno", "");// TODO
			tmp.put("type", "USUARIO");

			TreeNode<Object> nodeUsuario = new TreeNode<Object>(tmp);
			nodeArea.add(nodeUsuario);

		});

	}

}