/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecm.cmisIntegracion.util.FileUtil;
import com.ecm.sigap.data.controller.CustomRestController;
import com.ecm.sigap.data.model.AsuntoConsulta;
import com.ecm.sigap.data.model.DocumentoAsunto;
import com.ecm.sigap.data.model.Representante;
import com.ecm.sigap.data.model.util.PlantillaExcel;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiOperation;

/**
 *
 * @author Carlos Sotolongo
 * @version 1.0
 *
 */
@RestController
public class PlantillaExcelController extends CustomRestController {

	/** Log de suscesos. */
	private static final Logger log = LogManager.getLogger(EmpresaController.class);

	/** */
	@Autowired(required = false)
	@Qualifier("plantillasExcel")
	private String plantillasExcel;

	/**
	 * Plantillas excel disponibles en el sistema.
	 *
	 * @param response
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Obtiene plantillas excel", notes = "Obtiene plantillas excel disponibles en el sistema")
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
	
	@RequestMapping(value = "/plantillasExcel/plantillas.json", method = RequestMethod.GET)
	public void obtenerPlantillasDisponibles(HttpServletResponse response) throws Exception {

		try {

			if (plantillasExcel == null)
				throw new Exception(errorMessages.getString("plantillasExcelNoJsonFile"));

			InputStream is = new ByteArrayInputStream(plantillasExcel.getBytes(StandardCharsets.UTF_8));

			response.setContentType("application/json");

			IOUtils.copy(is, response.getOutputStream());

		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}

	}

	/**
	 *
	 * @param dto
	 * @return
	 * @throws Exception
	 */
	
	/*
	 * Documentacion con swagger
	 */
	
	@ApiOperation(value = "Descarga plantilla excel", notes = "Rellena una plantilla de excel con los datos obtenidos de la consulta y descarga el archivo al usuario")
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
	@RequestMapping(value = "/plantillasExcel/rellenarPlantilla", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<PlantillaExcel> rellenarPlantilla( //
			@RequestBody(required = true) PlantillaExcel plantilla) throws Exception {

		try {

			log.debug("PROCESANDO EXPORT DE PLANTILLA :: " + plantilla);

			String fileName = plantilla.getFile();
			String nombre = plantilla.getNombre();
			List<AsuntoConsulta> asuntos = plantilla.getAsuntos();

			try(InputStream excelFile = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("/plantillasExcel/" + fileName)){

				if (excelFile == null) {
					throw new BadRequestException(errorMessages.getString("plantillaExcelNoExiste"));
				}

				Workbook workbook = new XSSFWorkbook(excelFile);

				CellStyle style = workbook.createCellStyle();

				style.setBorderTop(BorderStyle.MEDIUM);
				style.setBorderBottom(BorderStyle.MEDIUM);
				style.setBorderLeft(BorderStyle.MEDIUM);
				style.setBorderRight(BorderStyle.MEDIUM);
				style.setWrapText(true);

				Boolean checkPoint = false;

				Sheet sheet = workbook.getSheetAt(0);

				Iterator<Row> rowIterator = sheet.iterator();

				Row row;
				Row rowReferencia = null;
				Cell celdaReferencia = null;
				List<String> listaEtiquetasRow = new ArrayList<>();
				// Recorremos todas las filas para mostrar el contenido de cada
				// celda
				while (rowIterator.hasNext()) {
					row = rowIterator.next();

					// Obtenemos el iterator que permite recorres todas las celdas
					// de una fila
					Iterator<Cell> cellIterator = row.cellIterator();
					Cell celda;

					while (cellIterator.hasNext()) {
						celda = cellIterator.next();

						if (celda.getStringCellValue().equals("${TITULOPLANTILLA}")) {
							celda.setCellValue(nombre);
						}
						if (celda.getStringCellValue().equals("${FECHAACTUAL}")) {
							Date fecha = new Date();
							celda.setCellValue(new SimpleDateFormat("dd-MM-yyyy").format(fecha));
						}

						Enumeration<String> keys = plantillasKeys.getKeys();

						if (celda.getStringCellValue().equals("${REPETIR_INICIO}")) {
							checkPoint = true;
							celdaReferencia = celda;
							rowReferencia = row;
						}

						if (celda.getStringCellValue().equals("${REPETIR_FIN}")) {
							checkPoint = false;
						}

						if (checkPoint) {
							for (Enumeration<String> e = keys; keys.hasMoreElements();) {
								String key = e.nextElement();
								if (key.startsWith("asunto")) {
									String value = plantillasKeys.getString(key);
									if (celda.getStringCellValue().equals(value)) {
										listaEtiquetasRow.add(key);
									}
								}
							}
							keys = plantillasKeys.getKeys();
						}

					}
				}

				if (celdaReferencia != null) {

					CellAddress address = celdaReferencia.getAddress();

					Integer posicionFila = address.getRow();
					Integer posicionColumna = address.getColumn() + 1;
					Integer referenciaColumna = address.getColumn() + 1;
					sheet.removeRow(rowReferencia);

					for (AsuntoConsulta asunto : asuntos) {
						Integer posicionDestino = posicionFila + 1;
						Row rowActual = sheet.createRow(posicionFila);
						copyRow(workbook, sheet, posicionFila, posicionDestino);
						for (String etiqueta : listaEtiquetasRow) {
							Cell cellActual = rowActual.createCell(posicionColumna);
							cellActual.setCellStyle(style);
							posicionColumna++;
							cellActual.setCellValue(asunto.retornaValuePlantilla(etiqueta));
							if (etiqueta.equals("asunto.anexos")) {
								List<Criterion> restrictions = new ArrayList<Criterion>();

								DocumentoAsunto da = new DocumentoAsunto();
								da.setIdAsunto(asunto.getIdAsunto());
								restrictions.add(Restrictions.eq("idAsunto", da.getIdAsunto()));
								List<Order> orders = new ArrayList<Order>();
								orders.add(Order.desc("fechaRegistro"));
								List<DocumentoAsunto> listaDocumentoAsunto = (List<DocumentoAsunto>) mngrDocsAsunto
										.search(restrictions, orders);
								StringBuilder sb = new StringBuilder();
								if (listaDocumentoAsunto != null) {
									for (DocumentoAsunto documentoAsunto : listaDocumentoAsunto) {
										sb.append(documentoAsunto.getObjectName()).append("\n");
									}
									cellActual.setCellValue(sb.toString());
								}
							}
							if (etiqueta.equals("asunto.descripcionDirigidoA")) {
								if (asunto.getIdDirigidoA() != null) {
									Representante representante = mngrRepresentante.fetch(asunto.getIdDirigidoA());
									if (representante != null)
										cellActual.setCellValue(representante.getNombreCompleto());
									else
										cellActual.setCellValue("");
								} else
									cellActual.setCellValue("");
							}
						}
						posicionColumna = referenciaColumna;
						posicionFila++;
					}

					for (Integer i = referenciaColumna; i <= listaEtiquetasRow.size(); i++) {
						sheet.autoSizeColumn(i);
					}
				}

				File filledFile = File.createTempFile(FileUtil.DEAULT_ECM_TEMP_FILE_PREFIX, ".xls");

				filledFile.deleteOnExit();

				try(FileOutputStream outputStream = new FileOutputStream(filledFile)){
					workbook.write(outputStream);
				} catch (Exception e) {
					throw e;
				} finally {
					workbook.close();				
				}

				String fileB64 = FileUtil.fileToStringB64(filledFile);

				filledFile.delete();

				PlantillaExcel respuesta = new PlantillaExcel();

				respuesta.setFile(fileB64);

				return new ResponseEntity<PlantillaExcel>(respuesta, HttpStatus.OK);
			} catch (Exception e) {
				log.error(e.getMessage());
				throw e;
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			
			throw e;
		}
	}

	/**
	 *
	 * @param workbook
	 * @param worksheet
	 * @param sourceRowNum
	 * @param destinationRowNum
	 */
	private static void copyRow(Workbook workbook, Sheet worksheet, int sourceRowNum, int destinationRowNum) {
		// Get the source / new row
		Row newRow = worksheet.getRow(destinationRowNum);
		Row sourceRow = worksheet.getRow(sourceRowNum);

		// If the row exist in destination, push down all rows by 1 else create
		// a new row
		if (newRow != null) {
			worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
		} else {
			newRow = worksheet.createRow(destinationRowNum);
		}

		// Loop through source columns to add to new row
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			// Grab a copy of the old/new cell
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = newRow.createCell(i);

			// If the old cell is null jump to next cell
			if (oldCell == null) {
				newCell = null;
				continue;
			}

			// Copy style from old cell and apply to new cell
			CellStyle newCellStyle = workbook.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			newCell.setCellStyle(newCellStyle);

			// If there is a cell comment, copy
			if (oldCell.getCellComment() != null) {
				newCell.setCellComment(oldCell.getCellComment());
			}

			// If there is a cell hyperlink, copy
			if (oldCell.getHyperlink() != null) {
				newCell.setHyperlink(oldCell.getHyperlink());
			}

			// Set the cell data type
			newCell.setCellType(oldCell.getCellTypeEnum());

			if (oldCell.getCellTypeEnum() == CellType.STRING)
				newCell.setCellValue(oldCell.getStringCellValue());

			else if (oldCell.getCellTypeEnum() == CellType.NUMERIC)
				newCell.setCellValue(oldCell.getNumericCellValue());

			else if (oldCell.getCellTypeEnum() == CellType.FORMULA)
				newCell.setCellFormula(oldCell.getCellFormula());

			else if (oldCell.getCellTypeEnum() == CellType.ERROR)
				newCell.setCellErrorValue(oldCell.getErrorCellValue());

			else if (oldCell.getCellTypeEnum() == CellType.BOOLEAN)
				newCell.setCellValue(oldCell.getBooleanCellValue());

			else if (oldCell.getCellTypeEnum() == CellType.BLANK)
				newCell.setCellValue(oldCell.getStringCellValue());
		}

		// If there are are any merged regions in the source row, copy to new
		// row
		for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {

			CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);

			if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
				CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
						(newRow.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
						cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
				worksheet.addMergedRegion(newCellRangeAddress);
			}

		}
	}
}
