package com.ecm.sigap.data.controller.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.RESTController;
import com.ecm.sigap.data.controller.util.RequestWrapper;
import com.ecm.sigap.data.model.Auditoria;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 * Controladores REST para manejo de elementos tipo
 * {@link com.ecm.sigap.data.model.Auditoria}
 *
 * @author Roberto Rincon
 * @version 1.0
 *
 */

@RestController
public class AuditoriaController extends CustomRestController implements RESTController<Auditoria> {

    /** Log de suscesos. */
    private static final Logger log = LogManager.getLogger(BitacoraController.class);

    /*
   	 * Documentacion con swagger
   	 */
   	
   	@ApiOperation(value = "Consulta auditoria", notes = "Consulta los movimientos de areas y usuarios del sistema")
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
    @RequestMapping(value = "/auditoria", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<RequestWrapper<List<Auditoria>>> 
   		search(@RequestBody(required = true) RequestWrapper<Auditoria> body) throws Exception {

        try {
            Auditoria item = body.getObject();
            Map<String, Object> params = body.getParams();
			Long completeCount = 0L;
			
			Integer cantidadRegistros = null;

			if (null != body.getSize())
				cantidadRegistros = body.getSize();

			Integer empezarEn = null;

			if (null != body.getBeginAt())
				empezarEn = body.getBeginAt();

            List<Criterion> restrictions = new ArrayList<Criterion>();

            if (item.getIdUsuario() != null)
                restrictions.add(Restrictions.ilike("idUsuario", item.getIdUsuario()));

            if (item.getTipoEntity() != null)
                restrictions.add(Restrictions.eq("tipoEntity", item.getTipoEntity()));
            
            if(item.getInformacion() != null)
            	restrictions.add(Restrictions.ilike("informacion", item.getInformacion(), MatchMode.ANYWHERE));

            if (item.getIdArea() != null)
                restrictions.add(Restrictions.eq("idArea", item.getIdArea()));

            if (item.getNombreEntity() != null)
                restrictions.add(Restrictions.ilike("nombreEntity", item.getNombreEntity()));

            if (item.getIdEntity() != null)
                restrictions.add(Restrictions.eq("idEntity", item.getIdEntity()));
            
            if (item.getInstitucionId() != null)
            	restrictions.add(Restrictions.eq("institucionId", item.getInstitucionId()));
            
            if (item.getOrigenId() != null)
            	restrictions.add(Restrictions.eq("origenId", item.getOrigenId()));

            if (params != null) {

                if (params.get("accion") != null)
                    restrictions.add(Restrictions.eq("accion", params.get("accion")));

                // FILSTROS PARA FECHAS
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
			
			ProjectionList projections = Projections.projectionList();
			
			projections.add(Projections.countDistinct("id").as("countr"));

            List<Order> orders = new ArrayList<Order>();
            orders.add(Order.desc("fechaRegistro"));

			List<Auditoria> lst = mngrAuditoria.search(restrictions, orders, null, cantidadRegistros, empezarEn);
			List<?> totalRegistros = mngrAuditoria.search(restrictions, null, projections, null, null);
			

			Map<String, Long> map = (Map<String, Long>) totalRegistros.get(0);

			completeCount = map.get("countr");

			Map<String, Object> paramResult = new HashMap<>();
			paramResult.put("total", completeCount);
			
			RequestWrapper<List<Auditoria>> bitacora = new RequestWrapper<List<Auditoria>>();
			bitacora.setObject(lst);
			bitacora.setParams(paramResult);
			
			log.debug("Size found >> " + lst.size());
			log.debug("Size found >> " + completeCount);

			return new ResponseEntity<RequestWrapper<List<Auditoria>>>(bitacora, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            
            throw e;
        }

    }

    @Override
    public ResponseEntity<Auditoria> get(Serializable id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Serializable id) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseEntity<List<?>> search(Auditoria object) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseEntity<Auditoria> save(Auditoria object) throws Exception {
        throw new UnsupportedOperationException();
    }
}
