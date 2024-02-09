/**
 * Copyright (c) 2016 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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

import com.ecm.cmisIntegracion.client.IEndpoint;
import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.controller.HeaderValueNames;
import com.ecm.sigap.data.model.util.BytesContentDTO;
import com.ecm.sigap.util.convertes.PdfConverterService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Servicio REST para el conversor de archivos a pdf.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
@RestController
public class ConverterController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(ConverterController.class);

	/** */
	@Autowired
	private FirmaController firmaController;

	/** */
	@Autowired
	@Qualifier("pdfConverterService")
	private PdfConverterService pdfConverterService;

	/**
	 *
	 * @param objectId
	 * @return
	 * @throws ForbiddenException
	 * @throws Exception
	 */

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Covertir pdf", notes = "Convierte un documento a formato pdf para visualizarlo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/converter/asPdf", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Map<String, Object>> previewDocumento(
			@RequestParam(value = "objectId", required = true) String objectId) throws ForbiddenException, Exception {

		objectId = objectId.toLowerCase();

		String contetUser = getHeader(HeaderValueNames.HEADER_CONTENT_USER, false);
		String password = getHeader(HeaderValueNames.HEADER_USER_KEY, false);

		IEndpoint endpoint = EndpointDispatcher.getInstance(contetUser, password);

		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> props;
		String encodeBase64String;

		try {
			props = endpoint.getObjectProperties(objectId);

			if ("true".equalsIgnoreCase(props.getOrDefault("isDeleted", "false").toString()))
				encodeBase64String = Base64.encodeBase64String("El arhivo no existe".getBytes());
			else {
				encodeBase64String = endpoint.getObjectContentB64(objectId);
			}

			String fileName = ((List<?>) props.get("cmis:name")).get(0).toString();

			String pdfB64;
			if (fileName.trim().toLowerCase().endsWith(".pdf"))
				pdfB64 = encodeBase64String;
			else if (firmaController.isAnexoVersionable(fileName)) {
				pdfB64 = pdfConverterService.convertB64(fileName, encodeBase64String);
				fileName = fileName.substring(0, fileName.lastIndexOf('.') + 1) + "pdf";
			} else
				throw new BadRequestException();

			result.put("objectId", objectId);
			result.put("name", fileName);
			result.put("contentB64", pdfB64);

			return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

		} catch (ForbiddenException e) {

			log.error("El usuario '" + contetUser + "' no tiene permiso para descargar el documento " + objectId);
			log.error(e.getMessage());
			throw e;

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());

			throw e;
		}

	}

	/*
	 * Documentacion con swagger
	 */

	@ApiOperation(value = "Convertir img a pdf", notes = "Convierte una imagen en un archivo pdf")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/converter/ImgToPdf", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<BytesContentDTO> converterImgToPdf(@RequestBody() BytesContentDTO body) {
		PDDocument doc = new PDDocument();

		for (String base64 : body.getBase64Content()) {
			try {
				base64 = base64.replace("data:image/png;base64,", "");
				byte[] bytes = java.util.Base64.getDecoder().decode(base64.getBytes("UTF-8"));
				BufferedImage is = null;
				try(InputStream inputStream = new ByteArrayInputStream(bytes)){
					is = ImageIO.read(inputStream);
					PDImageXObject ximage = JPEGFactory.createFromImage(doc, is);

					PDPage page = new PDPage(new PDRectangle((float) is.getWidth(), (float) is.getHeight()));
					try(PDPageContentStream contentStream = new PDPageContentStream(doc, page,
							PDPageContentStream.AppendMode.APPEND, false)){

						contentStream.drawImage(ximage, 1f, 1f);
					} catch (Exception e) {
						log.error(e.getMessage());
						return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
					}

					doc.addPage(page);
				} catch (Exception e) {
					throw e;
				} finally{
					is.flush();
				}
			} catch (Exception e) {
				log.error(e.getMessage());
				return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
			}
		}

		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()){
			doc.save(outputStream);
			BytesContentDTO response = new BytesContentDTO();
			String base64 = java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
			response.setBase64(base64);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try {
				doc.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
	}

	/*
	 * Documentacion con swagger
	 */
	@ApiOperation(value = "Convertir pdf a img", notes = "Convierte un archivo pdf a imagen")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sacg-user-id", value = "Identificador usuario cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-area-id", value = "Identificador area cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-content-user", value = "Usuario registrado en repositorio cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-token", value = "Token cifrado", required = true, dataType = "int", paramType = "header"),
			@ApiImplicitParam(name = "sacg-user-key", value = "Llave usuario cifrado", required = true, dataType = "int", paramType = "header") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "La solicitud se realizo de forma exitosa"),
			@ApiResponse(code = 400, message = "El servidor no puede interpretar su solicitud"),
			@ApiResponse(code = 401, message = "Es necesario autenticar para obtener la respuesta solicitada"),
			@ApiResponse(code = 403, message = "No posee los permisos necesarios"),
			@ApiResponse(code = 404, message = "El servidor no pudo encontrar el contenido solicitado"),
			@ApiResponse(code = 500, message = "Error del servidor") })

	@RequestMapping(value = "/converter/PdftoImg", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<BytesContentDTO> converterPdftoImg(@RequestBody() BytesContentDTO body) {
		try {
			List<String> base64 = pdfConverterService.convertPdfToImg(body.getBase64());
			BytesContentDTO response = new BytesContentDTO();
			response.setBase64Content(base64);
			return ResponseEntity.ok(response);
		} catch (Exception e) {

		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
	}
}
