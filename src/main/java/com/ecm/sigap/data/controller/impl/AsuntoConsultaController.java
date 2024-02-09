/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.OutputStream;
import java.io.Serializable;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.controller.util.EscapedLikeRestrictions;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.AsuntoCSV;
import com.ecm.sigap.data.model.AsuntoConsultaEspecial;
import com.ecm.sigap.data.model.AsuntoDetalleModal;
import com.ecm.sigap.data.model.AsuntoRechazadoConsulta;
import com.ecm.sigap.data.model.AsuntoRecibidoConsulta;
import com.ecm.sigap.data.model.Status;
import com.ecm.sigap.data.model.TramiteCSV;
import com.ecm.sigap.data.model.TramiteConsulta;
import com.ecm.sigap.data.model.util.EnTiempo;
import com.ecm.sigap.data.model.util.TipoAsunto;
import com.ecm.sigap.data.model.util.TipoRegistro;
import com.ecm.sigap.data.util.FechaUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Asunto}
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class AsuntoConsultaController extends CustomRestController {

    /**
     * Log de suscesos.
     */
    private static final Logger log = LogManager.getLogger(AsuntoConsultaController.class);

    /**
     * Referencia hacia el REST controller de {@link PermisoController}.
     */
    @Autowired
    private PermisoController permisoControlle;

    /**
     * @param body
     * @return
     */

    /*
     * Documentacion con swagger
     */
    @ApiOperation(value = "Consulta tramites", notes = "Consulta la lista de tramites")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
            @ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
            @ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
            @ApiResponse(code = 403, message = "No posee los permisos necesarios"),
            @ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
            @ApiResponse(code = 500, message = "Error del servidor")})

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/asunto/consultar", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RequestWrapper<List<TramiteConsulta>>> search(
            @RequestBody(required = true) RequestWrapper<TramiteConsulta> body) {

        TramiteConsulta asuntoConsulta = body.getObject();
        Map<String, Object> params = body.getParams();
        log.debug("PARAMETROS DE BUSQUEDA :: " + body);

        String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
        Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

        Integer cantidadRegistros = null;

        if (null != body.getSize())
            cantidadRegistros = body.getSize();

        Integer empezarEn = null;

        if (null != body.getBeginAt())
            empezarEn = body.getBeginAt();

        boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);

        Long completeCount = 0L;

        try {
            // Lista de retricciones
            List<Criterion> restrictions = new ArrayList<>();
            
            restrictions = createCriterionConsultarTramite(idArea, asuntoConsulta, params, verConfidencial);

            List<Order> orders = new ArrayList<Order>();
            if (body.getOrders() != null && !body.getOrders().isEmpty()) {
                for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
                    if (order.isDesc())
                        orders.add(Order.desc(order.getField()));
                    else
                        orders.add(Order.asc(order.getField()));
                }
            }

            List<TramiteConsulta> list;
            
            if (Objects.isNull(empezarEn) || Objects.isNull(cantidadRegistros)) {
                list = (List<TramiteConsulta>) mngrTramiteConsulta//
                        .search(restrictions, orders);
                completeCount = (long) list.size();
            } else {
                ProjectionList projections = Projections.projectionList();
                projections.add(Projections.countDistinct("idAsunto").as("countr"));

                List<TramiteConsulta> search = mngrTramiteConsulta.search(restrictions, null, projections, null, null);
                Map<String, Long> map = (Map<String, Long>) search.get(0);
                completeCount = map.get("countr");
                
                list = (List<TramiteConsulta>) mngrTramiteConsulta//
                        .search(restrictions, orders, null, cantidadRegistros, empezarEn);
            }

            Map<String, Object> paramResult = new HashMap<>();
            paramResult.put("total", completeCount);
            RequestWrapper<List<TramiteConsulta>> respuesta = new RequestWrapper<List<TramiteConsulta>>();
            respuesta.setObject(list);
            respuesta.setParams(paramResult);

            return new ResponseEntity<RequestWrapper<List<TramiteConsulta>>>(respuesta, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());

            throw e;
        }
    }

    /**
     * @param listBody
     * @return
     */

    /*
     * Documentacion con swagger
     */
    @ApiOperation(value = "Consulta asuntos", notes = "Consulta la lista de asuntos")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
            @ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
            @ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
            @ApiResponse(code = 403, message = "No posee los permisos necesarios"),
            @ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
            @ApiResponse(code = 500, message = "Error del servidor")})

    @RequestMapping(value = "/asunto/consultar2", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<RequestWrapper<List<AsuntoConsultaEspecial>>> search2(
            @RequestBody(required = true) List<RequestWrapper<AsuntoConsultaEspecial>> listBody) {

        String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
        Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
        boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);

        RequestWrapper<AsuntoConsultaEspecial> objeto1 = listBody.get(0);
        Integer cantidadRegistros = null;
        Integer empezarEn = null;
        
        if (null != objeto1.getBeginAt())
            empezarEn = objeto1.getBeginAt();
        
        if (null != objeto1.getSize())
            cantidadRegistros = objeto1.getSize();
        
        List<String> queries = createQueryConsulta(listBody, verConfidencial);
        
        String sql = "select ace from AsuntoConsultaEspecial ace ";
        String sql2 = "select count(ace.idAsunto) from AsuntoConsultaEspecial ace ";
        
        if (queries.get(1) != "") {
            sql = sql + " where (" + queries.get(0) + ") and (" + queries.get(1) + ")";
            sql2 = sql2 + " where (" + queries.get(0) + ") and (" + queries.get(1) + ")";
        } else {
            sql = sql + " where " + queries.get(0);
            sql2 = sql2 + " where " + queries.get(0);
        }
        
        //---------------------------
        
        Map<String, Object> paramResult = new HashMap<>();
        RequestWrapper<List<AsuntoConsultaEspecial>> listAsuntos = new RequestWrapper<List<AsuntoConsultaEspecial>>();
        List<AsuntoConsultaEspecial> count = mngrAsuntoConsultaEspecial.execQuery(sql2);
        
        paramResult.put("total", count.get(0));
        
        sql = sql + " order by ace.folioArea desc";
        
        List<AsuntoConsultaEspecial> asuntos = (List<AsuntoConsultaEspecial>) 
                mngrAsuntoConsultaEspecial.execQuery(sql, empezarEn, cantidadRegistros);
        
        listAsuntos.setObject(asuntos);
        listAsuntos.setParams(paramResult);
        
        return new ResponseEntity<RequestWrapper<List<AsuntoConsultaEspecial>>>(listAsuntos, HttpStatus.OK);
    }

    public static String convertListToString(List<String> strlist) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(strlist)) {
            for (int i = 0; i < strlist.size(); i++) {
                if (i == 0) {
                    sb.append("'").append(strlist.get(i)).append("'");
                } else {
                    sb.append(",").append("'").append(strlist.get(i)).append("'");
                }
            }
        }
        return sb.toString();

    }

    /**
     *
     */
    private static final ResourceBundle plantillasKeys = ResourceBundle.getBundle("plantillaKeys");

    /**
     * @param listBody
     * @return
     */

    /*
     * Documentacion con swagger
     */
    @ApiOperation(value = "Descarga CSV", notes = "Descarga los tramites en un archivo excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Se realizo de forma exitosa la descarga"),
            @ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
            @ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
            @ApiResponse(code = 403, message = "No posee los permisos necesarios"),
            @ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
            @ApiResponse(code = 500, message = "Error del servidor")})

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/asunto/csv", method = RequestMethod.POST)
    public void toDownloadCSV(@RequestBody(required = true) List<RequestWrapper<TramiteCSV>> listBody,
                              HttpServletResponse response, HttpServletRequest request) throws Exception {
        List<TramiteCSV> listT = new ArrayList<>();
        List<TramiteCSV> unionT = new ArrayList<>();

        TramiteCSV asuntoConsulta;
        Map<String, Object> params;

        String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
        Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
        Integer cantidadRegistros;
        Integer empezarEn;

        boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);

        List<Criterion> restrictions = new ArrayList<Criterion>();
        List<Order> orders;

        for (RequestWrapper<TramiteCSV> body : listBody) {
            asuntoConsulta = body.getObject();
            params = body.getParams();

            cantidadRegistros = null;

            if (null != body.getSize())
                cantidadRegistros = body.getSize();

            empezarEn = null;

            if (null != body.getBeginAt())
                empezarEn = body.getBeginAt();

            try {
                restrictions = createCriterionTramiteCSV(idArea, asuntoConsulta, params, verConfidencial);

                orders = new ArrayList<Order>();
                if (body.getOrders() != null && !body.getOrders().isEmpty()) {
                    for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
                        if (order.isDesc())
                            orders.add(Order.desc(order.getField()));
                        else
                            orders.add(Order.asc(order.getField()));
                    }
                }

                if (Objects.isNull(empezarEn) || Objects.isNull(cantidadRegistros)) {
    				List<TramiteCSV> lstA = new ArrayList<>();
    				int firtsResult = 0;
    				final int fetchSize = Integer.parseInt(environment.getProperty("csv.tram.max.size.data", "100"));
    				do {
    					lstA = (List<TramiteCSV>) mngrTramiteCSV.search(restrictions, orders, null, fetchSize, firtsResult);
    					listT.addAll(lstA);
    					firtsResult += fetchSize;
    				} while(lstA.size() == fetchSize);
                } else {
                    listT = (List<TramiteCSV>) mngrTramiteCSV//
                            .search(restrictions, orders, null, cantidadRegistros, empezarEn);
                }

                unionT.addAll(listT);
            } catch (Exception e) {
                log.error(e.getLocalizedMessage());
                throw e;
            }
        }

        StringBuilder builder = new StringBuilder();
        String columnNamesList;
       
        try(OutputStream outputStream = response.getOutputStream()){
			
				response.setContentType("text/csv;charset=utf-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"ConsultaTramites.csv\"");
				try {

					columnNamesList = plantillasKeys.getString("columnNamesList2");

					builder.append(columnNamesList + "\n");
					for (TramiteCSV asunto : unionT) {
						builder.append(
								asunto.getTipoAsunto() != null ? TipoAsunto.fromTipo(asunto.getTipoAsunto().getValue())
										: "");
						builder.append(',');

						builder.append('"').append(asunto.getStatusTurno() != null ? asunto.getStatusTurno() : "")
								.append('"');
						builder.append(',');
						builder.append(
								asunto.getTitularAreaDestino() != null ? '"' + asunto.getTitularAreaDestino() + '"'
										: "");
						builder.append(',');
						builder.append(asunto.getCargoTitularAreaDestino() != null
								? '"' + asunto.getCargoTitularAreaDestino() + '"'
								: "");
						builder.append(',');
						builder.append(asunto.getAreaDestino() != null ? '"' + asunto.getAreaDestino() + '"' : "");
						builder.append(',');
						builder.append(asunto.getInstruccionDescripcion() != null
								? escapeText(asunto.getInstruccionDescripcion())
								: "");
						builder.append(',');
						builder.append(asunto.getComentario() != null ? escapeText(asunto.getComentario()) : "");
						builder.append(',');
						builder.append(asunto.getFechaEnvio() != null ? FechaUtil.getDateFormat(asunto.getFechaEnvio(),environment) : "");
						builder.append(',');
						builder.append(asunto.getFechaAcuse() != null ? FechaUtil.getDateFormat(asunto.getFechaAcuse(),environment) : "");
						builder.append(',');
						builder.append(
								asunto.getFechaCompromiso() != null ? FechaUtil.getDateFormat(asunto.getFechaCompromiso(),environment) : "");
						builder.append(',');
						builder.append(
								asunto.getEnTiempo() != null ? EnTiempo.fromTipo(asunto.getEnTiempo().getTipo()) : "");
						builder.append(',');
						builder.append(asunto.getPrioridadDescripcion() != null
								? StringEscapeUtils.escapeCsv(asunto.getPrioridadDescripcion())
								: "");
						builder.append(',');
						builder.append(asunto.getEspecialsn().equals("S") ? plantillasKeys.getString("yes")
								: plantillasKeys.getString("no"));
						builder.append(',');

						builder.append('"').append(asunto.getIdAsuntoOrigen() != null ? asunto.getIdAsuntoOrigen() : "")
								.append('"');

						builder.append(',');
						builder.append(
								asunto.getFolioAreaAsuntoPadre() != null ? '"' + asunto.getFolioAreaAsuntoPadre() + '"'
										: "");
						builder.append(',');
						builder.append(asunto.getNumDoctoPadre() != null
								? StringEscapeUtils.escapeCsv(asunto.getNumDoctoPadre())
								: "");
						builder.append(',');
						builder.append(asunto.getFechaElaboracionPadre() != null
								? FechaUtil.getDateFormat(asunto.getFechaElaboracionPadre(),environment)
								: "");
						builder.append(',');
						builder.append(
								asunto.getFechaRegistroPadre() != null ? FechaUtil.getDateFormat(asunto.getFechaRegistroPadre(),environment)
										: "");
						builder.append(',');
						builder.append(asunto.getAsuntoDescripcionPadre() != null
								? escapeText(asunto.getAsuntoDescripcionPadre())
								: "");
						builder.append(',');
						builder.append(
								asunto.getFirmanteAsuntoPadre() != null ? '"' + asunto.getFirmanteAsuntoPadre() + '"'
										: "");
						builder.append(',');
						builder.append(
								asunto.getFirmanteCargoPadre() != null ? '"' + asunto.getFirmanteCargoPadre() + '"'
										: "");
						builder.append(',');
						builder.append(
								asunto.getRemitentePadre() != null ? '"' + asunto.getRemitentePadre() + '"' : "");
						builder.append(',');
						builder.append(asunto.getPromotorPadre() != null ? '"' + asunto.getPromotorPadre() + '"' : "");
						builder.append(',');
						builder.append(asunto.getTipoAsuntoPadre() != null
								? TipoAsunto.fromTipo(asunto.getTipoAsuntoPadre().getValue())
								: "");
						builder.append(',');
						builder.append(asunto.getPromotorPadre() != null ? '"' + asunto.getPromotorPadre() + '"' : "");
						builder.append(',');
						builder.append('"')
								.append(asunto.getStatusAsuntoPadre() != null ? asunto.getStatusAsuntoPadre() : "")
								.append('"');
						builder.append('\n');
					}
				} catch (Exception e) {
					log.error(e.getLocalizedMessage());
				}
						
			final String outputResult = builder.toString();
			outputStream.write(outputResult.getBytes("UTF-8"));
			outputStream.flush();
        } catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }

    /**
     * @param text
     * @return
     */
    private String escapeText(String text) {
        return '"' + text.replace("\"", "\"\"") + '"';
    }
    
    private List<Criterion> createCriterionTramiteCSV(Integer idArea, TramiteCSV asuntoConsulta,
			Map<String, Object> params, boolean verConfidencial) {
		List<Criterion> restrictions = new ArrayList<Criterion>();

		if (asuntoConsulta.getSubTipoAsunto() != null) {
			restrictions.add(Restrictions.eq("subTipoAsunto", asuntoConsulta.getSubTipoAsunto()));
		}

		if (StringUtils.isNotBlank(asuntoConsulta.getIdDestinatario())) {
			restrictions.add(EscapedLikeRestrictions.ilike("idDestinatario", asuntoConsulta.getIdDestinatario(),
					MatchMode.EXACT));
		}
		
		if (asuntoConsulta.getIdAreaDestino() != null)
			restrictions.add(Restrictions.eq("idAreaDestino", asuntoConsulta.getIdAreaDestino()));

		if (asuntoConsulta.getIdArea() != null)
			restrictions.add(Restrictions.eq("idArea", asuntoConsulta.getIdArea()));
		
		if(asuntoConsulta.getIdTipo() != null)
			restrictions.add(Restrictions.eq("idTipo", asuntoConsulta.getIdTipo()));
		
		if (StringUtils.isNotBlank(asuntoConsulta.getClave()))
            restrictions.add(EscapedLikeRestrictions.ilike("clave", asuntoConsulta.getClave(), MatchMode.ANYWHERE));
		
		if (!verConfidencial)
			restrictions.add(Restrictions.eq("confidencial", Boolean.FALSE));

		if (params != null) {

			if (params.containsKey("respondidoSN")) {
				Boolean val = Boolean.parseBoolean(params.get("respondidoSN").toString());
				if (val) {
					restrictions.add(Restrictions.not(Restrictions.eq("respuestasEnviadas", 0)));
				} else {
					restrictions.add(Restrictions.not(Restrictions.gt("respuestasEnviadas", 0)));
				}

			}

			if (params.containsKey("especialsn")) {
				restrictions.add(Restrictions.eq("especialsn", params.get("especialsn").toString()));
			}
			
            if (params.containsKey("idStatusTurnoIn")) {
                List<Integer> val = (List<Integer>) params.get("idStatusTurnoIn");
                if (!val.isEmpty())
                    restrictions.add(Restrictions.in("idStatusTurno", val));
            }
            
            if (params.containsKey("vencimientoIn")) {
                List<String> val_ = (List<String>) params.get("vencimientoIn");
                List<EnTiempo> val = new ArrayList<>();
                for (String enTiempo : val_) {
                    val.add(EnTiempo.valueOf(enTiempo));
                }
                if (!val.isEmpty()) {

                    if (val.contains(EnTiempo.EN_TIEMPO)) {
                        restrictions.add(Restrictions.or(//
                                Restrictions.in("enTiempo", val), //
                                Restrictions.isNull("enTiempo")));
                    } else {
                        restrictions.add(Restrictions.in("enTiempo", val));
                    }

                }
            }
			
			if (params.containsKey("idStatusAsuntoIn")) {
				List<Integer> val = (List<Integer>) params.get("idStatusAsuntoIn");
				if (!val.isEmpty())
					restrictions.add(Restrictions.in("idStatusAsunto", val));
			}

			if (params.containsKey("tipoAsuntoIn")) {
				List<String> val_ = (List<String>) params.get("tipoAsuntoIn");
				List<TipoAsunto> val = new ArrayList<>();
				for (String tipoAsunto : val_) {
					val.add(TipoAsunto.valueOf(tipoAsunto));
				}
				if (!val.isEmpty())
					restrictions.add(Restrictions.in("tipoAsunto", val));
			}
		
			if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
				restrictions.add(Restrictions.between("fechaRegistro", //
						new Date((Long) params.get("fechaRegistroInicial")),
						new Date((Long) params.get("fechaRegistroFinal"))));
			} else if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") == null) {
				restrictions.add(Restrictions.ge("fechaRegistro", new Date((Long) params.get("fechaRegistroInicial"))));
			} else if (params.get("fechaRegistroInicial") == null && params.get("fechaRegistroFinal") != null) {
				restrictions.add(Restrictions.le("fechaRegistro", new Date((Long) params.get("fechaRegistroFinal"))));
			}

			
		} 

		return restrictions;
	}
    
    @SuppressWarnings("unchecked")
    private List<Criterion> createCriterionConsultarTramite(Integer idArea, TramiteConsulta asuntoConsulta,
                                                     Map<String, Object> params, boolean verConfidencial) {
    	List<Criterion> restrictions = new ArrayList<Criterion>();
    	
    	if (asuntoConsulta.getIdAsunto() != null)
            restrictions.add(Restrictions.eq("idAsunto", asuntoConsulta.getIdAsunto()));
        
        if (asuntoConsulta.getIdTipoRegistro() != null)
            restrictions.add(Restrictions.eq("idTipoRegistro", asuntoConsulta.getIdTipoRegistro()));

        if (asuntoConsulta.getTipoAsunto() != null)
            restrictions.add(Restrictions.eq("tipoAsunto", asuntoConsulta.getTipoAsunto()));

        if (asuntoConsulta.getSubTipoAsunto() != null) {
            restrictions.add(Restrictions.eq("subTipoAsunto", asuntoConsulta.getSubTipoAsunto()));
        }

        if (StringUtils.isNotBlank(asuntoConsulta.getIdDestinatario())) {
            restrictions.add(
                    EscapedLikeRestrictions.ilike("idDestinatario", asuntoConsulta.getIdDestinatario(), MatchMode.EXACT));
        }
                
        if (asuntoConsulta.getIdAreaDestino() != null)
            restrictions.add(Restrictions.eq("idAreaDestino", asuntoConsulta.getIdAreaDestino()));

        if (asuntoConsulta.getTitularAreaDestino() != null)
            restrictions.add(Restrictions.eq("idTitularAreaDestino", asuntoConsulta.getTitularAreaDestino()));

        if (StringUtils.isNotBlank(asuntoConsulta.getAreaDestino()))
            restrictions.add(Restrictions.eq("areaDestino", asuntoConsulta.getAreaDestino()));

        if (asuntoConsulta.getIdArea() != null)
            restrictions.add(Restrictions.eq("idArea", asuntoConsulta.getIdArea()));

        if (asuntoConsulta.getIdAsuntoOrigen() != null)
            restrictions.add(Restrictions.eq("idAsuntoOrigen", asuntoConsulta.getIdAsuntoOrigen()));
       
        if (asuntoConsulta.getIdTipo() != null)
            restrictions.add(Restrictions.eq("idTipo", asuntoConsulta.getIdTipo()));

        if (StringUtils.isNotBlank(asuntoConsulta.getTipo()))
            restrictions.add(EscapedLikeRestrictions.ilike("tipo", asuntoConsulta.getTipo(), MatchMode.ANYWHERE));

        if (StringUtils.isNotBlank(asuntoConsulta.getClave()))
            restrictions.add(EscapedLikeRestrictions.ilike("clave", asuntoConsulta.getClave(), MatchMode.ANYWHERE));

        if (!verConfidencial)
            restrictions.add(Restrictions.eq("confidencial", Boolean.FALSE));

        // ++++++++++++++ PARAMS

        if (params != null) {

            if (params.containsKey("respondidoSN")) {
                Boolean val = Boolean.parseBoolean(params.get("respondidoSN").toString());
                if (val) {
                    restrictions.add(Restrictions.not(Restrictions.eq("respuestasEnviadas", 0)));
                } else {
                    restrictions.add(Restrictions.not(Restrictions.gt("respuestasEnviadas", 0)));
                }

            }

            if (params.containsKey("especialsn")) {
                restrictions.add(Restrictions.eq("especialsn", params.get("especialsn").toString()));
            }
           
            if (params.containsKey("idStatusTurnoIn")) {
                List<Integer> val = (List<Integer>) params.get("idStatusTurnoIn");
                if (!val.isEmpty())
                    restrictions.add(Restrictions.in("idStatusTurno", val));
            }
            
            if (params.containsKey("vencimientoIn")) {
                List<String> val_ = (List<String>) params.get("vencimientoIn");
                List<EnTiempo> val = new ArrayList<>();
                for (String enTiempo : val_) {
                    val.add(EnTiempo.valueOf(enTiempo));
                }
                if (!val.isEmpty()) {

                    if (val.contains(EnTiempo.EN_TIEMPO)) {
                        restrictions.add(Restrictions.or(//
                                Restrictions.in("enTiempo", val), //
                                Restrictions.isNull("enTiempo")));
                    } else {
                        restrictions.add(Restrictions.in("enTiempo", val));
                    }

                }
            }
          
            if (params.containsKey("tipoAsuntoIn")) {
                List<String> val_ = (List<String>) params.get("tipoAsuntoIn");
                List<TipoAsunto> val = new ArrayList<>();
                for (String tipoAsunto : val_) {
                    val.add(TipoAsunto.valueOf(tipoAsunto));
                }
                if (!val.isEmpty())
                    restrictions.add(Restrictions.in("tipoAsunto", val));
            }
           
            if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
                restrictions.add(Restrictions.between("fechaRegistro", //
                        new Date((Long) params.get("fechaRegistroInicial")),
                        new Date((Long) params.get("fechaRegistroFinal"))));
            } else if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") == null) {
                restrictions
                        .add(Restrictions.ge("fechaRegistro", new Date((Long) params.get("fechaRegistroInicial"))));
            } else if (params.get("fechaRegistroInicial") == null && params.get("fechaRegistroFinal") != null) {
                restrictions
                        .add(Restrictions.le("fechaRegistro", new Date((Long) params.get("fechaRegistroFinal"))));
            }


        }
    	
    	return restrictions;
    }
    
    
    private String getStringNormalize(String cadena){
        String source = Normalizer.normalize(cadena, Normalizer.Form.NFD);
        source =  source.replaceAll("[^\\p{ASCII}]", "");
        return source.toLowerCase();
    }
    
    /**
     * @param listBody
     * @return
     */


    /*
     * Documentacion con swagger
     */
    @ApiOperation(value = "Descarga CSV", notes = "Descarga los asuntos en un archivo excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Se realizo de forma exitosa la descarga"),
            @ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
            @ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
            @ApiResponse(code = 403, message = "No posee los permisos necesarios"),
            @ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
            @ApiResponse(code = 500, message = "Error del servidor")})

    @RequestMapping(value = "/asunto/csv2", method = RequestMethod.POST)
    public void toDownloadCSV2(@RequestBody(required = true) List<RequestWrapper<AsuntoConsultaEspecial>> listBody,
           HttpServletResponse response, HttpServletRequest request) throws Exception {

        String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
        Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
        boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);

		int firtsResult = 0;
		final int maxResult = Integer.parseInt(environment.getProperty("csv.asun.max.size.data", "100"));
		
        StringBuilder builder = new StringBuilder();
        String columnNamesList;
        
        try(OutputStream outputStream = response.getOutputStream()){
			        String sql = "select ace from AsuntoCSV ace ";
			        
			        List<String> queries = createQueryConsulta(listBody, verConfidencial);
			        

			        if (queries.get(1) != "") {
			            sql = sql + " where (" + queries.get(0) + ") and (" + queries.get(1) + ")";
			        } else {
			            sql = sql + " where " + queries.get(0);
			        }

			        //---------------------------
			        sql = sql + " order by ace.folioArea desc";
			        
				List<AsuntoCSV> asuntos = new ArrayList<>();
		        List<AsuntoCSV> lstA = new ArrayList<>();
				do {
					lstA = (List<AsuntoCSV>) mngrAsuntoCSV.execQuery(sql, firtsResult, maxResult);
		            asuntos.addAll(lstA);
					firtsResult += maxResult;
				} while(lstA.size() == maxResult);
				
				response.setContentType("text/csv;charset=utf-8");
				response.setHeader("Content-Disposition", "attachment; filename=\"ConsultaAsuntos.csv\"");

				try {
					columnNamesList = plantillasKeys.getString("columnNamesList1");
					builder.append(columnNamesList + "\n");
					String str = "";
					for (AsuntoCSV asunto : asuntos) {
					
						builder.append(asunto.getFolioArea() != null ? '"' + asunto.getFolioArea() + '"' : "");
						builder.append(',');
						builder.append(
								asunto.getNumDocto() != null ? StringEscapeUtils.escapeCsv(asunto.getNumDocto()) : "");
						builder.append(',');
					    builder.append(
								asunto.getFechaElaboracion() != null ? FechaUtil.getDateFormat(asunto.getFechaElaboracion(),environment): "");
						builder.append(',');
						builder.append(asunto.getFechaRegistro() != null ? FechaUtil.getDateFormat(asunto.getFechaRegistro(),environment) : "");
						builder.append(',');
						builder.append(
								asunto.getAsuntoDescripcion() != null ? escapeText(asunto.getAsuntoDescripcion()) : "");
						builder.append(',');
						builder.append(
								asunto.getFirmanteAsunto() != null ? '"' + asunto.getFirmanteAsunto() + '"' : "");
						builder.append(',');
						builder.append(asunto.getFirmanteCargo() != null ? '"' + asunto.getFirmanteCargo() + '"' : "");
						builder.append(',');
						builder.append(asunto.getArea() != null ? '"' + asunto.getArea() + '"' : "");
						builder.append(',');
						builder.append(asunto.getPromotor() != null ? '"' + asunto.getPromotor() + '"' : "");
						builder.append(',');

						if (asunto.getIdTipoRegistro().equals(TipoRegistro.CONTROL_GESTION.getValue())) {
							str = plantillasKeys.getString("sigap");
						} else {
							str = plantillasKeys.getString("infomex");
						}

						builder.append('"').append(str != null ? str : "").append('"').append(',');

						builder.append(asunto.getRemitente() != null ? '"' + asunto.getRemitente() + '"' : "");
						builder.append(',');
						builder.append(EnTiempo.fromTipo(asunto.getEnTiempo().getTipo()));
						builder.append(',');
						builder.append(TipoAsunto.fromTipo(asunto.getTipoAsunto().getValue()));
						builder.append(',');

						builder.append('"')
								.append(asunto.getIdAsuntoOrigen() != null ? "" + asunto.getIdAsuntoOrigen() : "")
								.append('"');

						builder.append(',');
						builder.append(asunto.getFolioIntermedio() != null
								? StringEscapeUtils.escapeCsv(asunto.getFolioIntermedio())
								: "");
						builder.append(',');

						str = asunto.getPaternoTurnadorPadre() != null ? asunto.getPaternoTurnadorPadre() : "";
						str += asunto.getMaternoTurnadorPadre() != null ? " " + asunto.getMaternoTurnadorPadre() : "";
						str += asunto.getNombresTurnadorPadre() != null ? " " + asunto.getNombresTurnadorPadre() : "";
						builder.append('"').append(str).append('"');
						builder.append(',');

						str = asunto.getApellidoPaternoTurnador() != null ? asunto.getApellidoPaternoTurnador() : "";
						str += asunto.getApellidoMaternoTurnador() != null ? " " + asunto.getApellidoMaternoTurnador()
								: "";
						str += asunto.getNombreTurnador() != null ? " " + asunto.getNombreTurnador() : "";
						builder.append('"').append(str).append('"');
						builder.append(',');

						builder.append('"').append(asunto.getStatusAsunto()).append('"');
						builder.append(',');
						builder.append('"').append(asunto.getTipoAsunto().getValue() !="A" ? asunto.getFirmanteDestinatario() : "N/A").append('"');
						builder.append(',');
						builder.append(
								'"' + (StringUtils.isBlank(asunto.getAntecedentes()) ? "" : asunto.getAntecedentes())
										+ '"');
						builder.append('\n');
					}

				} catch (Exception e) {

					log.error(e.getLocalizedMessage());
				}
							
			final String outputResult = builder.toString();
			outputStream.write(outputResult.getBytes("UTF-8"));
			outputStream.flush();
        } catch (Exception e) {
			log.error(e.getMessage());
			throw e;
		}
    }
    
    @SuppressWarnings("unchecked")
    private List<String> createQueryConsulta(List<RequestWrapper<AsuntoConsultaEspecial>> listBody, boolean verConfidencial) {
        List<String> validaciones = new ArrayList<String>();
        String validacion = ""; 
        
        for (RequestWrapper<AsuntoConsultaEspecial> body : listBody) {

            AsuntoConsultaEspecial asuntoConsulta = body.getObject();
            Map<String, Object> params = body.getParams();
            

            validacion = "(";

            // --- INICIO FILTROS DEL OBJECT
            {
                if (asuntoConsulta.getIdArea() != null)
                    validacion = validacion + " ace.idArea = " + asuntoConsulta.getIdArea();

                if (asuntoConsulta.getIdAreaDestino() != null) {
                    if (validacion == "(")
                        validacion = validacion + " ace.idAreaDestino = " + asuntoConsulta.getIdAreaDestino();
                    else
                        validacion = validacion + " and ace.idAreaDestino = " + asuntoConsulta.getIdAreaDestino();
                }

                if (asuntoConsulta.getTipoAsunto() != null)
                    validacion = validacion + " and ace.tipoAsunto = '" + asuntoConsulta.getTipoAsunto() + "'";

                if (asuntoConsulta.getStatusAsunto() != null && asuntoConsulta.getIdStatusAsunto() != null) {
                    Status stat = mngrStatus.fetch(asuntoConsulta.getIdStatusAsunto());
                    if (stat != null) {
                        validacion = validacion + " and ace.statusAsunto = " + stat;
                    }
                }

            }
            //  FIN FILTROS DEL OBJECT

            // --- INICIO FILTROS PARAMS POR ASUNTO
            if (params != null) {

                if (params.containsKey("idStatusTurnoNotIn")) {
                    List<String> listString = (List<String>) params.get("idStatusTurnoNotIn");
                    String listString2 = listString.toString();
                    String val = listString2.substring(1, listString2.length() - 1);

                    if (!val.isEmpty())
                        validacion = validacion + " and not (ace.idStatusTurno in (" + val + "))";
                }

                if (params.containsKey("idStatusTurnoIn")) {
                    List<String> listString = (List<String>) params.get("idStatusTurnoIn");
                    String listString2 = listString.toString();
                    String val = listString2.substring(1, listString2.length() - 1);

                    if (!val.isEmpty()) {
                        if (params.containsKey("vencimientoIn")) {
                            List<String> listString3 = (List<String>) params.get("vencimientoIn");
                            String val2 = convertListToString(listString3);

                            if (!val2.isEmpty()) {
                                validacion = validacion + " and (ace.idStatusTurno in (" + val + ") and ace.enTiempo in (" + val2 + ") )";
                            } else {
                                validacion = validacion + " and (ace.idStatusTurno in (" + val + "))";
                            }
                        } else {
                            validacion = validacion + " and (ace.idStatusTurno in (" + val + "))";
                        }
                    }
                }

                if (params.containsKey("idStatusAsuntoNotIn")) {
                    List<String> listString = (List<String>) params.get("idStatusAsuntoNotIn");
                    String listString2 = listString.toString();
                    String val = listString2.substring(1, listString2.length() - 1);

                    if (!val.isEmpty())
                        validacion = validacion + " and not (ace.idStatusAsunto in (" + val + "))";
                }

                if (params.containsKey("idStatusAsuntoIn")) {
                    List<String> listString = (List<String>) params.get("idStatusAsuntoIn");
                    boolean contain_0 = listString.contains(0);
                    
                    if(contain_0) {
                        int index_0 = listString.indexOf(0);
                        listString.remove(index_0);
                    }
                    
                    String listString2 = "", val = "";
                    
                    if(listString.size() > 0) {
                         listString2 = listString.toString();
                         val = listString2.substring(1, listString2.length()-1);
                    }
                    
                    if (params.containsKey("vencimientoIn")) {
                        List<String> listVencimiento = (List<String>) params.get("vencimientoIn");
                        String val2 = convertListToString(listVencimiento);
                        
                        if (!val2.isEmpty()) {
                            if(contain_0 && !val.isEmpty()) {
                                validacion = validacion + "and (ace.idStatusAsunto in (0) or (ace.idStatusAsunto in ("+val+") and ace.enTiempo in (" + val2 + ") ))";
                            } else {
                                if(!val.isEmpty()) {
                                    validacion = validacion + " and (ace.idStatusAsunto in (" + val + "))";
                                }
                                validacion = validacion + " and (ace.enTiempo in (" + val2 + ") )";
                            }
                        } else {
                            if(!val.isEmpty()) {
                                
                                 if((val.indexOf("3")==0 || val.indexOf("7")==0)){
                                	validacion = validacion + " and (ace.idStatusAsunto in (" + val + "))";
                                }else{
                                	validacion = validacion + " and (ace.idStatusAsunto in (0) or (ace.idStatusAsunto in (" + val + ")))";
                                }
                            }else {
                                if(contain_0)
                                    validacion = validacion + " and (ace.idStatusAsunto in (0))";
                            }
                        }
                    } else {                        
                        if (!val.isEmpty()) {
                            if(contain_0)
                                val = val + ", 0";
                            validacion = validacion + " and (ace.idStatusAsunto in (" + val + "))";                         
                        }
                    }
                }

                if (params.containsKey("tipoAsuntoIn")) {
                    List<String> listString = (List<String>) params.get("tipoAsuntoIn");
                    String val = convertListToString(listString);

                    if (!val.isEmpty())
                        validacion = validacion + " and (ace.tipoAsunto in (" + val + "))";
                }

                if (params.containsKey("omitirNoAceptados")) {

                    Boolean val = (Boolean) params.get("omitirNoAceptados");

                    if (val) {
                        validacion = validacion + " and ace.fechaAcuse is not null ";
                    }
                }

                validacion = validacion + ")";
                validaciones.add(validacion);

            }
            // --- FIN FILTROS PARAMS POR ASUNTO

        } // FIN FOR()

        validacion = "";
        if (validaciones.size() > 0)
            validacion = validacion + validaciones.get(0);

        if (validaciones.size() > 1)
            validacion = validacion + " or " + validaciones.get(1);

        RequestWrapper<AsuntoConsultaEspecial> objeto1 = listBody.get(0);
        AsuntoConsultaEspecial asuntoConsulta = objeto1.getObject();
        Map<String, Object> params = objeto1.getParams();

        String validacion2 = "";

        // INICIO CAMPOS COMPARTIDOS
        {
            if (!verConfidencial) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.confidencial = " + Boolean.FALSE :
                        validacion2 + " and ace.confidencial = " + Boolean.FALSE;
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getTipo())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " LOWER(ace.tipo) LIKE '%" + asuntoConsulta.getTipo().toLowerCase() + "%'" :
                        validacion2 + " and LOWER(ace.tipo) LIKE '%" + asuntoConsulta.getTipo().toLowerCase() + "%'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getClave())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " MY_UNACCENT(LOWER(ace.clave)) LIKE '%" + getStringNormalize( asuntoConsulta.getClave() ) + "%'" :
                        validacion2 + " and MY_UNACCENT(LOWER(ace.clave)) LIKE '%" + getStringNormalize( asuntoConsulta.getClave() ) + "%'";
                ;
            }

            if (params.containsKey("documentosPublicados")) {
                Boolean val = Boolean.parseBoolean(params.get("documentosPublicados").toString());
                if (val) {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " not ace.documentosPublicados = 0" :
                            validacion2 + " and not ace.documentosPublicados = 0";
                } else {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " not ace.documentosPublicados > 0" :
                            validacion2 + " and not ace.documentosPublicados > 0";
                }

            }

            if (params.containsKey("respondidoSN")) {
                Boolean val = Boolean.parseBoolean(params.get("respondidoSN").toString());
                if (val) {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " not ace.respuestasEnviadas = 0" :
                            validacion2 + " and not ace.respuestasEnviadas = 0";
                } else {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " not ace.respuestasEnviadas > 0" :
                            validacion2 + " and not ace.respuestasEnviadas > 0";
                }

            }

            if (asuntoConsulta.getIdTipo() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idTipo = " + asuntoConsulta.getIdTipo() :
                        validacion2 + " and ace.idTipo = " + asuntoConsulta.getIdTipo();
            }


            if (StringUtils.isNotBlank(asuntoConsulta.getComentario())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " MY_UNACCENT(LOWER(ace.comentario)) LIKE '%" + getStringNormalize( asuntoConsulta.getComentario() ) + "%'" :
                        validacion2 + " and MY_UNACCENT(LOWER(ace.comentario)) LIKE '%" + getStringNormalize( asuntoConsulta.getComentario() ) + "%'";
            }
            
            if (StringUtils.isNotBlank(asuntoConsulta.getAnotacion())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " MY_UNACCENT(LOWER(ace.anotacion)) LIKE '%" + getStringNormalize( asuntoConsulta.getAnotacion() ) + "%'" :
                        validacion2 + " and MY_UNACCENT(LOWER(ace.anotacion)) LIKE '%" + getStringNormalize( asuntoConsulta.getAnotacion() ) + "%'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getPalabraClave())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " MY_UNACCENT(LOWER(ace.palabraClave)) LIKE '%" + getStringNormalize( asuntoConsulta.getPalabraClave() ) + "%'" :
                        validacion2 + " and MY_UNACCENT(LOWER(ace.palabraClave)) LIKE '%" + getStringNormalize( asuntoConsulta.getPalabraClave() ) + "%'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getAsuntoDescripcion())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " MY_UNACCENT(LOWER(ace.asuntoDescripcion)) LIKE '%" + getStringNormalize(asuntoConsulta.getAsuntoDescripcion() ) + "%'" :
                        validacion2 + " and MY_UNACCENT(LOWER(ace.asuntoDescripcion)) LIKE '%" + getStringNormalize(asuntoConsulta.getAsuntoDescripcion() ) + "%'";
            }


            if (asuntoConsulta.getNumDocto() != null) {
                if( asuntoConsulta.getNumDocto().contains("\"") ) {
                    String[] word = asuntoConsulta.getNumDocto().split("\"");
                    if(word.length > 0) {
                        String numDocto = word[1];
                        validacion2 = (validacion2 == "") ?
                                validacion2 + " LOWER(ace.numDocto) = '" + numDocto.toLowerCase() + "'" :
                                validacion2 + " and LOWER(ace.numDocto) = '" + numDocto.toLowerCase() + "'";
                    }
                } else {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " LOWER(ace.numDocto) LIKE '%" + asuntoConsulta.getNumDocto().toLowerCase() + "%'" :
                            validacion2 + " and LOWER(ace.numDocto) LIKE '%" + asuntoConsulta.getNumDocto().toLowerCase() + "%'";
                }
            }

            if (asuntoConsulta.getFolioIntermedio() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " LOWER(ace.folioIntermedio) LIKE '%" + asuntoConsulta.getFolioIntermedio().toLowerCase() + "%'" :
                        validacion2 + " and LOWER(ace.folioIntermedio) LIKE '%" + asuntoConsulta.getFolioIntermedio().toLowerCase() + "%'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getFolioArea())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " LOWER(ace.folioArea) LIKE '%" + asuntoConsulta.getFolioArea().toLowerCase() + "%'" :
                        validacion2 + " and LOWER(ace.folioArea) LIKE '%" + asuntoConsulta.getFolioArea().toLowerCase() + "%'";

            }

            if (asuntoConsulta.getIdRemitente() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idRemitente = " + asuntoConsulta.getIdRemitente() :
                        validacion2 + " and ace.idRemitente = " + asuntoConsulta.getIdRemitente();
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getRemitente())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.remitente = '" + asuntoConsulta.getRemitente() + "'" :
                        validacion2 + " and ace.remitente = '" + asuntoConsulta.getRemitente() + "'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getArea())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.area = '" + asuntoConsulta.getArea() + "'" :
                        validacion2 + " and ace.area = '" + asuntoConsulta.getArea() + "'";
            }

            if (asuntoConsulta.getIdPromotor() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idPromotor = " + asuntoConsulta.getIdPromotor() :
                        validacion2 + " and ace.idPromotor = " + asuntoConsulta.getIdPromotor();
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getPromotor())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.promotor = '" + asuntoConsulta.getPromotor() + "'" :
                        validacion2 + " and ace.promotor = '" + asuntoConsulta.getPromotor() + "'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getIdFirmante())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idFirmante = '" + asuntoConsulta.getIdFirmante() + "'" :
                        validacion2 + " and ace.idFirmante = '" + asuntoConsulta.getIdFirmante() + "'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getFirmanteAsunto())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.firmanteAsunto = '" + asuntoConsulta.getFirmanteAsunto() + "'" :
                        validacion2 + " and ace.firmanteAsunto = '" + asuntoConsulta.getFirmanteAsunto() + "'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getFirmanteCargo())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.firmanteCargo = '" + asuntoConsulta.getFirmanteCargo() + "'" :
                        validacion2 + " and ace.firmanteCargo = '" + asuntoConsulta.getFirmanteCargo() + "'";
            }

            if (StringUtils.isNotBlank(asuntoConsulta.getIdDirigidoA())) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idDirigidoA = '" + asuntoConsulta.getIdDirigidoA() + "'" :
                        validacion2 + " and ace.idDirigidoA = '" + asuntoConsulta.getIdDirigidoA() + "'";
            }

            if (asuntoConsulta.getIdTipoDocumento() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idTipoDocumento = " + asuntoConsulta.getIdTipoDocumento() :
                        validacion2 + " and ace.idTipoDocumento = " + asuntoConsulta.getIdTipoDocumento();
            }

            if (asuntoConsulta.getIdExpediente() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idExpediente = '" + asuntoConsulta.getIdExpediente() + "'" :
                        validacion2 + " and ace.idExpediente = '" + asuntoConsulta.getIdExpediente() + "'";
            }

            if (asuntoConsulta.getIdTema() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idTema = " + asuntoConsulta.getIdTema() :
                        validacion2 + " and ace.idTema = " + asuntoConsulta.getIdTema();
            }

            if (asuntoConsulta.getIdSubTema() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idSubTema = " + asuntoConsulta.getIdSubTema() :
                        validacion2 + " and ace.idSubTema = " + asuntoConsulta.getIdSubTema();
            }

            if (asuntoConsulta.getIdEvento() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idEvento = " + asuntoConsulta.getIdEvento() :
                        validacion2 + " and ace.idEvento = " + asuntoConsulta.getIdEvento();
            }

            if (asuntoConsulta.getIdAsunto() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idAsunto = " + asuntoConsulta.getIdAsunto() :
                        validacion2 + " and ace.idAsunto = " + asuntoConsulta.getIdAsunto();
            }

            if (asuntoConsulta.getIdAsuntoOrigen() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.idAsuntoOrigen = " + asuntoConsulta.getIdAsuntoOrigen() :
                        validacion2 + " and ace.idAsuntoOrigen = " + asuntoConsulta.getIdAsuntoOrigen();
            }

            if (asuntoConsulta.getDocumentosAdjuntos() != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.documentosAdjuntos = " + asuntoConsulta.getDocumentosAdjuntos() :
                        validacion2 + " and ace.documentosAdjuntos = " + asuntoConsulta.getDocumentosAdjuntos();
            }

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            // FILSTROS PARA FECHAS
            if (params.get("fechaRecepcionInicial") != null && params.get("fechaRecepcionFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " fechaRecepcion between to_date('" + fmt.format(params.get("fechaRecepcionInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaRecepcionFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and fechaRecepcion between to_date('" + fmt.format(params.get("fechaRecepcionInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaRecepcionFinal")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaRecepcionInicial") != null && params.get("fechaRecepcionFinal") == null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaRecepcion >= to_date('" + fmt.format(params.get("fechaRecepcionInicial")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaRecepcion >= to_date('" + fmt.format(params.get("fechaRecepcionInicial")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaRecepcionInicial") == null && params.get("fechaRecepcionFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " and ace.fechaRecepcion <= to_date('" + fmt.format(params.get("fechaRecepcionFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaRecepcion <= to_date('" + fmt.format(params.get("fechaRecepcionFinal")) + "', 'yyyy/MM/dd')";
            }

            if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " fechaRegistro between to_date('" + fmt.format(params.get("fechaRegistroInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaRegistroFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and fechaRegistro between to_date('" + fmt.format(params.get("fechaRegistroInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaRegistroFinal")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaRegistroInicial") != null && params.get("fechaRegistroFinal") == null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaRegistro >= to_date('" + fmt.format(params.get("fechaRegistroInicial")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaRegistro >= to_date('" + fmt.format(params.get("fechaRegistroInicial")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaRegistroInicial") == null && params.get("fechaRegistroFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaRegistro <= to_date('" + fmt.format(params.get("fechaRegistroFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaRegistro <= to_date('" + fmt.format(params.get("fechaRegistroFinal")) + "', 'yyyy/MM/dd')";
            }

            if (params.get("fechaElaboracionInicial") != null && params.get("fechaElaboracionFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " fechaElaboracion between to_date('" + fmt.format(params.get("fechaElaboracionInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaElaboracionFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and fechaElaboracion between to_date('" + fmt.format(params.get("fechaElaboracionInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaElaboracionFinal")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaElaboracionInicial") != null && params.get("fechaElaboracionFinal") == null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaElaboracion >= to_date('" + fmt.format(params.get("fechaElaboracionInicial")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaElaboracion >= to_date('" + fmt.format(params.get("fechaElaboracionInicial")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaElaboracionInicial") == null && params.get("fechaElaboracionFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaElaboracion <= to_date('" + fmt.format(params.get("fechaElaboracionFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaElaboracion <= to_date('" + fmt.format(params.get("fechaElaboracionFinal")) + "', 'yyyy/MM/dd')";
            }

            if (params.get("fechaCompromisoInicial") != null && params.get("fechaCompromisoFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaCompromiso between to_date('" + fmt.format(params.get("fechaCompromisoInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaCompromisoFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaCompromiso between to_date('" + fmt.format(params.get("fechaCompromisoInicial")) + "', 'yyyy/MM/dd')" +
                                " and to_date('" + fmt.format(params.get("fechaCompromisoFinal")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaCompromisoInicial") != null && params.get("fechaCompromisoFinal") == null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaCompromiso >= to_date('" + fmt.format(params.get("fechaCompromisoInicial")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaCompromiso >= to_date('" + fmt.format(params.get("fechaCompromisoInicial")) + "', 'yyyy/MM/dd')";

            } else if (params.get("fechaCompromisoInicial") == null && params.get("fechaCompromisoFinal") != null) {
                validacion2 = (validacion2 == "") ?
                        validacion2 + " ace.fechaCompromiso <= to_date('" + fmt.format(params.get("fechaCompromisoFinal")) + "', 'yyyy/MM/dd')" :
                        validacion2 + " and ace.fechaCompromiso <= to_date('" + fmt.format(params.get("fechaCompromisoFinal")) + "', 'yyyy/MM/dd')";
            }

            if (params.get("fechaEventoInicial") != null && params.get("fechaEventoFinal") != null) {
                if (params.get("horaEvento") != null) {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " ace.fechaEvento = to_timestamp('" + fmt2.format(params.get("fechaEventoInicial")) + "', 'yyyy/MM/dd HH24:MI:SS')" :
                            validacion2 + " and ace.fechaEvento = to_timestamp('" + fmt2.format(params.get("fechaEventoInicial")) + "', 'yyyy/MM/dd HH24:MI:SS')";
                } else {
                    validacion2 = (validacion2 == "") ?
                            validacion2 + " truncadate(ace.fechaEvento) = to_date('" + fmt.format(params.get("fechaEventoInicial")) + "', 'yyyy/MM/dd')" :
                            validacion2 + " and truncadate(ace.fechaEvento) = to_date('" + fmt.format(params.get("fechaEventoInicial")) + "', 'yyyy/MM/dd')";
                }
            }
        }
        // FIN CAMPOS COMPARTIDOS
        
        List<String> variables = new ArrayList<String>();
        variables.add(validacion);
        variables.add(validacion2);
        
        return(variables);
    }
    
    @ApiOperation(value = "Consulta asunto detalle modal", notes = "Consulta informacion asunto para modal ver detalle")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
            @ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
            @ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
            @ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
            @ApiResponse(code = 403, message = "No posee los permisos necesarios"),
            @ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
            @ApiResponse(code = 500, message = "Error del servidor")})

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/asunto/consultar/detalle", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<AsuntoDetalleModal>> searchAsuntoDetalleModal(
			@RequestParam(value = "id", required = true) String id) {

         String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
        Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));
        
        boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);

        try {
            
            List<Criterion> restrictions = new ArrayList<>();
            restrictions.add(Restrictions.eq("idAsunto", Integer.valueOf(id)));
            
            if(!verConfidencial){
            	restrictions.add(Restrictions.eq("confidencial", Boolean.FALSE));
            }
           
            List<AsuntoDetalleModal> list = (List<AsuntoDetalleModal>) mngrAsuntoDetalleModal//
                    .search(restrictions);
            

            return new ResponseEntity<List<AsuntoDetalleModal>>(list, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());

            throw e;
        }
    }

	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Consulta de asuntos recibidos (Bandeja de entrada)", notes = "Consulta de asuntos recibidos (Bandeja de entrada)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/asuntos/recibidos/consulta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestWrapper<List<AsuntoRecibidoConsulta>>> getAsuntosRecibidos(
			@RequestBody(required = true) RequestWrapper<AsuntoRecibidoConsulta> body) {

		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		log.debug("PARAMETROS DE BUSQUEDA :: " + body);
		AsuntoRecibidoConsulta asuntoRecConsulta = body.getObject();
		Map<String, Object> params = body.getParams();
		List<Order> orders = new ArrayList<Order>();
		Map<String, Object> paramResult = new HashMap<>();
		RequestWrapper<List<AsuntoRecibidoConsulta>> respuesta = new RequestWrapper<List<AsuntoRecibidoConsulta>>();

		try {

			boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);
			List<Criterion> restrictions = createCriterionARC(idArea, asuntoRecConsulta, params, verConfidencial);

			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			}

			List<AsuntoRecibidoConsulta> list = (List<AsuntoRecibidoConsulta>) mngrAsuntoRecibidoConsulta
					.search(restrictions, orders);
			paramResult.put("total", list.size());
			respuesta.setObject(list);
			respuesta.setParams(paramResult);
			return new ResponseEntity<RequestWrapper<List<AsuntoRecibidoConsulta>>>(respuesta, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @param idArea
	 * @param asuntoRecibido
	 * @param params
	 * @param verConfidencial
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Criterion> createCriterionARC(Integer idArea, AsuntoRecibidoConsulta asuntoRecibido,
			Map<String, Object> params, boolean verConfidencial) {
		List<Criterion> restrictions = new ArrayList<Criterion>();

		if (asuntoRecibido.getIdAsunto() != null)
			restrictions.add(Restrictions.eq("idAsunto", asuntoRecibido.getIdAsunto()));

		if (asuntoRecibido.getIdAsuntoPadre() != null)
			restrictions.add(Restrictions.eq("idAsuntoPadre", asuntoRecibido.getIdAsuntoPadre()));

		if (asuntoRecibido.getIdTipoRegistro() != null)
			restrictions.add(Restrictions.eq("idTipoRegistro", asuntoRecibido.getIdTipoRegistro()));

		if (asuntoRecibido.getTipoAsunto() != null)
			restrictions.add(Restrictions.eq("tipoAsunto", asuntoRecibido.getTipoAsunto()));

		if (asuntoRecibido.getIdRemitente() != null)
			restrictions.add(Restrictions.eq("idRemitente", asuntoRecibido.getIdRemitente()));

		if (StringUtils.isNotBlank(asuntoRecibido.getRemitente()))
			restrictions.add(Restrictions.eq("remitente", asuntoRecibido.getRemitente()));

		if (asuntoRecibido.getIdAreaDestino() != null)
			restrictions.add(Restrictions.eq("idAreaDestino", asuntoRecibido.getIdAreaDestino()));

		if (StringUtils.isNotBlank(asuntoRecibido.getAreaDestino()))
			restrictions.add(Restrictions.eq("areaDestino", asuntoRecibido.getAreaDestino()));

		if (asuntoRecibido.getIdArea() != null)
			restrictions.add(Restrictions.eq("idArea", asuntoRecibido.getIdArea()));

		if (StringUtils.isNotBlank(asuntoRecibido.getArea()))
			restrictions.add(Restrictions.eq("area", asuntoRecibido.getArea()));

		if (asuntoRecibido.getStatusAsunto() != null && asuntoRecibido.getIdStatusAsunto() != null) {
			Status stat = mngrStatus.fetch(asuntoRecibido.getIdStatusAsunto());
			if (stat != null)
				restrictions.add(Restrictions.eq("statusAsunto", stat));
		}

		if (asuntoRecibido.getIdTipo() != null)
			restrictions.add(Restrictions.eq("idTipo", asuntoRecibido.getIdTipo()));

		if (StringUtils.isNotBlank(asuntoRecibido.getTipo()))
			restrictions.add(EscapedLikeRestrictions.ilike("tipo", asuntoRecibido.getTipo(), MatchMode.ANYWHERE));

		if (!verConfidencial)
			restrictions.add(Restrictions.eq("confidencial", Boolean.FALSE));

		if (params != null) {

			if (params.containsKey("idStatusAsuntoIn")) {
				List<Integer> val = (List<Integer>) params.get("idStatusAsuntoIn");
				if (!val.isEmpty())
					restrictions.add(Restrictions.in("idStatusAsunto", val));
			}

			if (params.containsKey("tipoAsuntoIn")) {
				List<String> val_ = (List<String>) params.get("tipoAsuntoIn");
				List<TipoAsunto> val = new ArrayList<>();
				for (String tipoAsunto : val_) {
					val.add(TipoAsunto.valueOf(tipoAsunto));
				}
				if (!val.isEmpty())
					restrictions.add(Restrictions.in("tipoAsunto", val));
			}

			// FILSTROS PARA FECHAS
			if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") != null) {
				restrictions.add(Restrictions.between("fechaEnvio", //
						new Date((Long) params.get("fechaEnvioInicial")),
						new Date((Long) params.get("fechaEnvioFinal"))));
			} else if (params.get("fechaEnvioInicial") != null && params.get("fechaEnvioFinal") == null) {
				restrictions.add(Restrictions.ge("fechaEnvio", new Date((Long) params.get("fechaEnvioInicial"))));
			} else if (params.get("fechaEnvioInicial") == null && params.get("fechaEnvioFinal") != null) {
				restrictions.add(Restrictions.le("fechaEnvio", new Date((Long) params.get("fechaEnvioFinal"))));
			}

			// Parametros de En Tiempo, Fuera de Tiempo, Por vencer
			if (params.get("etfts") != null) {

				JSONObject etfts = new JSONObject("{" + params.get("etfts") + "}");
				JSONArray etftArray = etfts.getJSONArray("etft");
				Set<EnTiempo> tipos = new HashSet<>();
				for (int i = 0; i < etftArray.length(); ++i) {
					JSONObject etft = etftArray.getJSONObject(i);
					tipos.add(EnTiempo.valueOf(etft.getString("tipo")));
				}

				// Los registros que tengan esta columna en null se consideran En Tiempo
				if (tipos.contains(EnTiempo.EN_TIEMPO)) {
					restrictions
							.add(Restrictions.or(Restrictions.in("enTiempo", tipos), Restrictions.isNull("enTiempo")));
				} else {
					restrictions.add(Restrictions.in("enTiempo", tipos));
				}
			}

			if (params.containsKey("idAreaORIdAreaDestino")) {
				Integer val = Integer.parseInt(params.get("idAreaORIdAreaDestino").toString());

				restrictions.add(Restrictions.or(//
						Restrictions.eq("idAreaDestino", val), //
						Restrictions.and(Restrictions.eq("idArea", val), Restrictions.isNull("idAreaDestino")))//
				);

			}

			if (params.containsKey("idArea")) {
				Integer val = Integer.parseInt(params.get("idArea").toString());
				restrictions.add(Restrictions.eq("idArea", val));
			}

		}

		return restrictions;
	}

	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Consulta de asuntos/tramites rechazados(Bandeja tramites rechazados)", notes = "Consulta de asuntos/tramites rechazados(Bandeja tramites rechazados)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Se realizo de forma exitosa la consulta"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/asuntos/rechazados/consulta", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<RequestWrapper<List<AsuntoRechazadoConsulta>>> getTramitesRechazados(
			@RequestBody(required = true) RequestWrapper<AsuntoRechazadoConsulta> body) {

		String idUsuario = getHeader(HeaderValueNames.HEADER_USER_ID);
		Integer idArea = Integer.parseInt(getHeader(HeaderValueNames.HEADER_AREA_ID));

		log.debug("PARAMETROS DE BUSQUEDA :: " + body);
		AsuntoRechazadoConsulta asuntoRecConsulta = body.getObject();
		Map<String, Object> params = body.getParams();
		List<Criterion> restrictions = new ArrayList<>();
		List<Order> orders = new ArrayList<Order>();
		Map<String, Object> paramResult = new HashMap<>();
		RequestWrapper<List<AsuntoRechazadoConsulta>> respuesta = new RequestWrapper<List<AsuntoRechazadoConsulta>>();

		try {

			boolean verConfidencial = permisoControlle.verConfidencial(idUsuario, idArea);
			
			// restrictions
			if (asuntoRecConsulta.getTipoAsunto() != null)
				restrictions.add(Restrictions.eq("tipoAsunto", asuntoRecConsulta.getTipoAsunto()));

			if (asuntoRecConsulta.getIdArea() != null)
				restrictions.add(Restrictions.eq("idArea", asuntoRecConsulta.getIdArea()));

			if (!verConfidencial)
				restrictions.add(Restrictions.eq("confidencial", Boolean.FALSE));

			// ++++++++++++++ PARAMS

			if (params != null) {

				if (params.containsKey("especialsn")) {
					restrictions.add(Restrictions.eq("especialsn", params.get("especialsn").toString()));
				}

				if (params.containsKey("idStatusTurnoIn")) {
					List<Integer> val = (List<Integer>) params.get("idStatusTurnoIn");
					if (!val.isEmpty())
						restrictions.add(Restrictions.in("idStatusTurno", val));
				}

				if (params.containsKey("tipoAsuntoIn")) {
					List<String> val_ = (List<String>) params.get("tipoAsuntoIn");
					List<TipoAsunto> val = new ArrayList<>();
					for (String tipoAsunto : val_) {
						val.add(TipoAsunto.valueOf(tipoAsunto));
					}
					if (!val.isEmpty())
						restrictions.add(Restrictions.in("tipoAsunto", val));
				}

				// Parametros de En Tiempo, Fuera de Tiempo, Por vencer
				if (params.get("etfts") != null) {

					JSONObject etfts = new JSONObject("{" + params.get("etfts") + "}");
					JSONArray etftArray = etfts.getJSONArray("etft");
					Set<EnTiempo> tipos = new HashSet<>();
					for (int i = 0; i < etftArray.length(); ++i) {
						JSONObject etft = etftArray.getJSONObject(i);
						tipos.add(EnTiempo.valueOf(etft.getString("tipo")));
					}

					// Se agrega esta condicion ya que los registros que tenga
					// esta columna en null se consideran En Tiempo
					if (tipos.contains(EnTiempo.EN_TIEMPO)) {
						restrictions.add(
								Restrictions.or(Restrictions.in("enTiempo", tipos), Restrictions.isNull("enTiempo")));
					} else {
						restrictions.add(Restrictions.in("enTiempo", tipos));
					}
				}

				if (params.containsKey("inFolioArea")) {
					List<String> val = (ArrayList<String>) params.get("inFolioArea");
					restrictions.add(Restrictions.in("folioArea", val));

				}

				if (params.containsKey("idArea")) {
					Integer val = Integer.parseInt(params.get("idArea").toString());
					restrictions.add(Restrictions.eq("idArea", val));
				}

			}

			if (body.getOrders() != null && !body.getOrders().isEmpty()) {
				for (com.ecm.sigap.data.controller.util.Order order : body.getOrders()) {
					if (order.isDesc())
						orders.add(Order.desc(order.getField()));
					else
						orders.add(Order.asc(order.getField()));
				}
			}

			List<AsuntoRechazadoConsulta> list = (List<AsuntoRechazadoConsulta>) mngrAsuntoRechazadoConsulta
					.search(restrictions, orders);
			paramResult.put("total", list.size());
			respuesta.setObject(list);
			respuesta.setParams(paramResult);
			return new ResponseEntity<RequestWrapper<List<AsuntoRechazadoConsulta>>>(respuesta, HttpStatus.OK);

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			throw e;
		}
	}
}
