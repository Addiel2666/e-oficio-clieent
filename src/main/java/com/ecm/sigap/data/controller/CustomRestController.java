/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import com.ecm.cmisIntegracion.impl.EndpointDispatcher;
import com.ecm.sigap.config.DBVendor;
import com.ecm.sigap.data.model.*;
import com.ecm.sigap.data.model.interop.InstitucionOpe;
import com.ecm.sigap.data.model.interop.Modificacion;
import com.ecm.sigap.data.model.interop.WsSincronizaCompletaDetalle;
import com.ecm.sigap.data.model.validator.ConstraintValidatorFactoryImpl;
import com.ecm.sigap.data.service.EntityManager;
import com.ecm.sigap.eArchivo.ArchivoService;
import com.ecm.sigap.eCiudadano.model.AcuseFirmado;
import com.ecm.sigap.firma.FirmaService;
import com.ecm.sigap.interoperabilidad.InteroperabilidadService;
import com.ecm.sigap.security.util.Security;
import com.ecm.sigap.util.SignatureUtil;
import com.ecm.sigap.util.convertes.PdfConverterService;

/**
 * Arquetipo de un controlador REST.
 *
 * @author Alfredo Morales
 * @version 1.0
 */
public abstract class CustomRestController {

	/**
	 * Contexto Spring de la aplicacion.
	 */
	private static ApplicationContext appContext;

	/**
	 * Log de suscesos.
	 */
	private static final Logger log = LogManager.getLogger(CustomRestController.class);

	/** */
	protected static final ResourceBundle errorMessages = ResourceBundle.getBundle("errorMessages");

	/** Key para reemplazo en las plantillas */
	protected static final ResourceBundle plantillasKeys = ResourceBundle.getBundle("plantillaKeys");

	/** */
	protected static final String CDATA = "<![CDATA[ ";
	
	/** */
	protected static final String END_CDATA = "]]>";
	/**
	 * Configuracion global de la acplicacion.
	 */
	@Autowired
	protected Environment environment;

	/**
	 * Servicio de llamadas REST al WS de Firma Digital
	 */
	@Autowired
	@Qualifier("firmaService")
	protected FirmaService firmaEndPoint;

	/**
	 * Servicio de llamdas SOAP al ws de interoperabilidad.
	 */
	@Autowired
	@Qualifier("interoperabilidadService")
	protected InteroperabilidadService interoperabilidadEndPoint;

	/** */
	@Autowired(required = true)
	@Qualifier("pdfConverterService")
	protected PdfConverterService pdfConverterService;

	/**
	 * Servicio de llamadas REST al ws de archivo.
	 */
	@Autowired
	@Qualifier("archivoService")
	protected ArchivoService archivoService;

	/**
	 * Manejador para el tipo {@link Acceso}
	 */
	@Autowired
	@Qualifier("accesoService")
	protected EntityManager<Acceso> mngrAcceso;

	/**
	 * Manejador para el tipo {@link Area}
	 */
	@Autowired
	@Qualifier("areaService")
	protected EntityManager<Area> mngrArea;

	/**
	 * Manejador para el tipo {@link AreaAux}
	 */
	@Autowired
	@Qualifier("areaAuxService")
	protected EntityManager<AreaAux> mngrAreaAux;

	/**
	 * Manejador para el tipo {@link AreaEmpresa}
	 */
	@Autowired
	@Qualifier("areaEmpresaService")
	protected EntityManager<AreaEmpresa> mngrAreaEmpresa;

	/**
	 * Manejador para el tipo {@link AreaPromotor}
	 */
	@Autowired
	@Qualifier("areaPromotorService")
	protected EntityManager<AreaPromotor> mngrAreaPromotor;

	/**
	 * Manejador para el tipo {@link AreaRemitente}
	 */
	@Autowired
	@Qualifier("areaRemitenteService")
	protected EntityManager<AreaRemitente> mngrAreaRemitente;

	/**
	 * Manejador para el tipo {@link AreaRevisor}
	 */
	@Autowired
	@Qualifier("areaRevisorService")
	protected EntityManager<AreaRevisor> mngrAreaRevisor;

	/**
	 * Manejador para el tipo {@link Asunto}
	 */
	@Autowired
	@Qualifier("asuntoService")
	protected EntityManager<Asunto> mngrAsunto;

	/** Manejador para el tipo {@link AsuntoConsulta} */
	@Autowired
	@Qualifier("asuntoConsultaService")
	protected EntityManager<AsuntoConsulta> mngrAsuntoConsulta;

	/** Manejador para el tipo {@link AsuntoConsultaEspecial} */
	@Autowired
	@Qualifier("asuntoConsultaEspecialService")
	protected EntityManager<AsuntoConsultaEspecial> mngrAsuntoConsultaEspecial;

	/**
	 * Manejador para el tipo {@link Path}
	 */
	@Autowired
	@Qualifier("pathService")
	protected EntityManager<Path> mngrPath;

	/**
	 * Manejador para el tipo {@link Auditoria }
	 */
	@Autowired
	@Qualifier("auditoriaService")
	protected EntityManager<Auditoria> mngrAuditoria;

	/**
	 * Manejador para el tipo {@link Bitacora}
	 */
	@Autowired
	@Qualifier("bitacoraService")
	protected EntityManager<Bitacora> mngrBitacora;

	/**
	 * Manejador para el tipo {@link Ciudadano}
	 */
	@Autowired
	@Qualifier("ciudadanoService")
	protected EntityManager<Ciudadano> mngrCiudadano;

	/**
	 * Manejador para el tipo {@link Configuracion }
	 */
	@Autowired
	@Qualifier("configuracionService")
	protected EntityManager<Configuracion> mngrConfiguracion;

	/**
	 * Manejador para el tipo {@link CustomAsunto }
	 */
	@Autowired
	@Qualifier("customAsuntoService")
	protected EntityManager<CustomAsunto> mngrCustomAsunto;

	/**
	 * Manejador para el tipo {@link Destinatario}
	 */
	@Autowired
	@Qualifier("destinatarioService")
	protected EntityManager<Destinatario> mngrDestinatario;

	/**
	 * Manejador para el tipo {@link DiaFestivo}
	 */
	@Autowired
	@Qualifier("diaFestivoService")
	protected EntityManager<DiaFestivo> mngrDiaFestivo;

	/**
	 * Manejador para el tipo {@link DocumentoAsunto}
	 */
	@Autowired
	@Qualifier("documentoAsuntoService")
	protected EntityManager<DocumentoAsunto> mngrDocsAsunto;

	/**
	 * Manejador para el tipo {@link DocumentoMinutario}
	 */
	@Autowired
	@Qualifier("documentoMinutarioService")
	protected EntityManager<DocumentoMinutario> mngrDocsMinutario;

	/**
	 * Manejador para el tipo {@link DocumentoRespuesta}
	 */
	@Autowired
	@Qualifier("documentoRespuestaService")
	protected EntityManager<DocumentoRespuesta> mngrDocsRespuesta;

	/**
	 * Manejador para el tipo {@link DocumentoRespuestaAux}
	 */
	@Autowired
	@Qualifier("documentoRespuestaAuxService")
	protected EntityManager<DocumentoRespuestaAux> mngrDocsRespuestaAux;

	/**
	 * Manejador para el tipo {@link Entidad}
	 */
	@Autowired
	@Qualifier("entidadService")
	protected EntityManager<Entidad> mngrEntidad;

	/**
	 * Manejador para el tipo {@link Empresa}
	 */
	@Autowired
	@Qualifier("empresaService")
	protected EntityManager<Empresa> mngrEmpresa;

	/**
	 * Manejador para el tipo {@link ExpedienteInfo}
	 */
	@Autowired
	@Qualifier("expedienteService")
	protected EntityManager<ExpedienteInfo> mngrExpediente;

	/**
	 * Manejador para el tipo {@link FavDestinatario}
	 */
	@Autowired
	@Qualifier("favoritoDestinatarioService")
	protected EntityManager<FavDestinatario> mngrfavDestinatario;

	/**
	 * Manejador para el tipo {@link FavDestinatarioCiudadano}
	 */
	@Autowired
	@Qualifier("favDestinatarioCiudadanoService")
	protected EntityManager<FavDestinatarioCiudadano> mngrfavDestinatarioCiudadano;

	/**
	 * Manejador para el tipo {@link FavDestinatarioFuncionario}
	 */
	@Autowired
	@Qualifier("favDestinatarioFuncionarioService")
	protected EntityManager<FavDestinatarioFuncionario> mngrfavDestinatarioFuncionario;

	/**
	 * Manejador para el tipo {@link FavNoDestinatarioFuncionario}
	 */
	@Autowired
	@Qualifier("favNoDestinatarioFuncionarioService")
	protected EntityManager<FavNoDestinatarioFuncionario> mngrfavNoDestinatarioFuncionario;

	/**
	 * Manejador para el tipo {@link FavDestinatarioRepLegal}
	 */
	@Autowired
	@Qualifier("favDestinatarioRepLegalService")
	protected EntityManager<FavDestinatarioRepLegal> mngrfavDestinatarioRepLegal;

	/**
	 * Manejador para el tipo {@link Favorito}
	 */
	@Autowired
	@Qualifier("favoritoService")
	protected EntityManager<Favorito> mngrfavorito;

	/**
	 * Manejador para el tipo {@link FavoritoFirmante}
	 */
	@Autowired
	@Qualifier("favoritoFirmanteService")
	protected EntityManager<FavoritoFirmante> mngrFavoritoFirmante;
	/**
	 * Manejador para el tipo {@link FavoritoArea}
	 */
	@Autowired
	@Qualifier("favoritoAreaService")
	protected EntityManager<FavoritoArea> mngrFavoritoArea;

	/**
	 * Manejador para el tipo {@link FavoritoFirmante}
	 */
	@Autowired
	@Qualifier("favoritoRemitenteService")
	protected EntityManager<FavoritoRemitente> mngrFavoritoRemitente;

	/**
	 * Manejador para el tipo {@link Firmante}
	 */
	@Autowired
	@Qualifier("firmanteService")
	protected EntityManager<Firmante> mngrFirmante;

	/**
	 * Manejador para el tipo {@link FirmanteAvtivoSN}
	 */
	@Autowired
	@Qualifier("firmanteActivoSNService")
	protected EntityManager<FirmanteActivoSN> mngrFirmanteAvtivoSN;

	/**
	 * Manejador para el tipo {@link FolioPS }
	 */
	@Autowired
	@Qualifier("foliopsService")
	protected EntityManager<FolioPS> mngrFoliops;

	/**
	 * Manejador para el tipo {@link FolioPSMultiple }
	 */
	@Autowired
	@Qualifier("foliopsMultipleService")
	protected EntityManager<FolioPSMultiple> mngrFoliopsmultiple;

	/**
	 * Manejador para el tipo {@link FolioPS }
	 */
	@Autowired
	@Qualifier("foliopsClaveService")
	protected EntityManager<FolioPSClave> mngrFoliopsclave;

	/**
	 * Manejador para el tipo {@link Folio }
	 */
	@Autowired
	@Qualifier("folioService")
	protected EntityManager<Folio> mngrFolio;

	/**
	 * Manejador para el tipo {@link FolioArchivistica}
	 */
	@Autowired
	@Qualifier("folioArchivisticaService")
	protected EntityManager<FolioArchivistica> mngrFolioArchivistica;

	/**
	 * Manejador para el tipo {@link FolioArea}
	 */
	@Autowired
	@Qualifier("folioAreaService")
	protected EntityManager<FolioArea> mngrFolioArea;

	/**
	 * Manejador para el tipo {@link FolioArea}
	 */
	@Autowired
	@Qualifier("folioAreaMultilpleService")
	protected EntityManager<FolioAreaMultilple> mngrFolioAreaMultiple;

	/**
	 * Manejador para el tipo {@link Fondo}
	 */
	@Autowired
	@Qualifier("fondoService")
	protected EntityManager<Fondo> mngrFondo;

	/**
	 * Manejador para el tipo {@link Fondo}
	 */
	@Autowired
	@Qualifier("funcionarioService")
	protected EntityManager<Funcionario> mngrFuncionario;

	/**
	 * Manejador para el tipo {@link InfomexSolicitud}
	 */
	@Autowired
	@Qualifier("infomexSolicitudService")
	protected EntityManager<InfomexSolicitud> mngrInfomexSolicitud;

	/**
	 * Manejador para el tipo {@link InfomexModalidadEntrega}
	 */
	@Autowired
	@Qualifier("infomexModalidadEntregaService")
	protected EntityManager<InfomexModalidadEntrega> mngrInfomexModalidadEntrega;

	/**
	 * Manejador para el tipo {@link InfomexStatus}
	 */
	@Autowired
	@Qualifier("infomexStatusService")
	protected EntityManager<InfomexStatus> mngrInfomexStatus;

	/**
	 * Manejador para el tipo {@link Institucion}
	 */
	@Autowired
	@Qualifier("institucionService")
	protected EntityManager<Institucion> mngrInstitucion;

	/**
	 * Manejador para el tipo {@link InstitucionOpe}
	 */
	@Autowired
	@Qualifier("institucionOpeService")
	protected EntityManager<InstitucionOpe> mngrInstitucionOpe;

	/**
	 * Manejador para el tipo {@link Minutario}
	 */
	@Autowired
	@Qualifier("minutarioService")
	protected EntityManager<Minutario> mngrMinutario;

	/**
	 * Manejador para el tipo {@link MinutarioDestinatario}
	 */
	@Autowired
	@Qualifier("minutarioDestinatarioService")
	protected EntityManager<MinutarioDestinatario> mngrMinutarioDestinatario;

	/**
	 * Manejador para el tipo {@link Permiso}
	 */
	@Autowired
	@Qualifier("permisoService")
	protected EntityManager<Permiso> mngrPermiso;

	/**
	 * Manejador para el tipo {@link Plantilla}
	 */
	@Autowired
	@Qualifier("plantillaService")
	protected EntityManager<Plantilla> mngrPlantilla;

	/**
	 * Manejador para el tipo {@link Remitente}
	 */
	@Autowired
	@Qualifier("remitenteService")
	protected EntityManager<Remitente> mngrRemitente;

	/**
	 * Manejador para el tipo {@link Representante}
	 */
	@Autowired
	@Qualifier("representanteService")
	protected EntityManager<Representante> mngrRepresentante;

	/**
	 * Manejador para el tipo {@link RepresentanteLegal}
	 */
	@Autowired
	@Qualifier("representanteLegalService")
	protected EntityManager<RepresentanteLegal> mngrRepresentanteLegal;

	/**
	 * Manejador para el tipo {@link Respuesta}
	 */
	@Autowired
	@Qualifier("respuestaService")
	protected EntityManager<Respuesta> mngrRespuesta;

	/**
	 * Manejador para el tipo {@link RespuestaCount}
	 */
	@Autowired
	@Qualifier("respuestaCountService")
	protected EntityManager<RespuestaCount> mngrRespuestaCount;

	/**
	 * Manejador para el tipo {@link RespuestaCopia}
	 */
	@Autowired
	@Qualifier("respuestaCopiaService")
	protected EntityManager<RespuestaCopia> mngrRespuestaCopia;

	/**
	 * Manejador para el tipo {@link Rol}
	 */
	@Autowired
	@Qualifier("rolService")
	protected EntityManager<Rol> mngrRol;

	/**
	 * Manejador para el tipo {@link Seccion}
	 */
	@Autowired
	@Qualifier("seccionService")
	protected EntityManager<Seccion> mngrSeccion;

	/**
	 * Manejador para el tipo {@link Serie}
	 */
	@Autowired
	@Qualifier("serieService")
	protected EntityManager<Serie> mngrSerie;

	/**
	 * Manejador para el tipo {@link Status}
	 */
	@Autowired
	@Qualifier("statusService")
	protected EntityManager<Status> mngrStatus;

	/**
	 * Manejador para el tipo {@link StatusExpediente}
	 */
	@Autowired
	@Qualifier("statusExpedienteService")
	protected EntityManager<StatusExpediente> mngrStatusExpediente;

	/**
	 * Manejador para el tipo {@link SubFondo}
	 */
	@Autowired
	@Qualifier("subFondoService")
	protected EntityManager<SubFondo> mngrSubFondo;

	/**
	 * Manejador para el tipo {@link SubSeccion}
	 */
	@Autowired
	@Qualifier("subSeccionService")
	protected EntityManager<SubSeccion> mngrSubSeccion;

	/**
	 * Manejador para el tipo {@link SubSerie}
	 */
	@Autowired
	@Qualifier("subSerieService")
	protected EntityManager<SubSerie> mngrSubSerie;

	/**
	 * Manejador para el tipo {@link SubTema}
	 */
	@Autowired
	@Qualifier("subTemaService")
	protected EntityManager<SubTema> mngrSubTema;

	/**
	 * Manejador para el tipo {@link Tema}
	 */
	@Autowired
	@Qualifier("temaService")
	protected EntityManager<Tema> mngrTema;

	/**
	 * Manejador para el tipo {@link TipoDocumento}
	 */
	@Autowired
	@Qualifier("tipoDocumentoService")
	protected EntityManager<TipoDocumento> mngrTipoDocumento;

	/**
	 * Manejador para el tipo {@link TipoEvento}
	 */
	@Autowired
	@Qualifier("tipoEventoService")
	protected EntityManager<TipoEvento> mngrTipoEvento;

	/**
	 * Manejador para el tipo {@link TipoExpediente}
	 */
	@Autowired
	@Qualifier("tipoExpedienteService")
	protected EntityManager<TipoExpediente> mngrTipoExpediente;

	/**
	 * Manejador para el tipo {@link TipoInstruccion}
	 */
	@Autowired
	@Qualifier("tipoInstruccionService")
	protected EntityManager<TipoInstruccion> mngrTipoInstruccion;

	/**
	 * Manejador para el tipo {@link TipoPrioridad}
	 */
	@Autowired
	@Qualifier("tipoPrioridadService")
	protected EntityManager<TipoPrioridad> mngrTipoPrioridad;

	/**
	 * Manejador para el tipo {@link TipoRespuesta}
	 */
	@Autowired
	@Qualifier("tipoRespuestaService")
	protected EntityManager<TipoRespuesta> mngrTipoRespuesta;

	/**
	 * Manejador para el tipo {@link Favorito}
	 */
	@Autowired
	@Qualifier("titularNoFavoritoService")
	protected EntityManager<TitularNoFavorito> mngrTitularNoFavorito;

	/**
	 * Manejador para el tipo {@link Usuario}
	 */
	@Autowired
	@Qualifier("usuarioService")
	protected EntityManager<Usuario> mngrUsuario;

	/**
	 * Manejador para el tipo {@link AcuseFirmado}
	 */
	@Autowired
	@Qualifier("acuseFirmadoService")
	protected EntityManager<AcuseFirmado> mngrAcuseFirmado;

	/**
	 * Manejador para el tipo {@link GrupoEnvio}
	 */
	@Autowired
	@Qualifier("grupoEnvioService")
	protected EntityManager<GrupoEnvio> mngrGrupoEnvio;

	/**
	 * Manejador para el tipo {@link DestinatarioGrupoEnvio}
	 */
	@Autowired
	@Qualifier("destinatarioGrupoEnvioService")
	protected EntityManager<DestinatarioGrupoEnvio> mngrDestinatarioGrupoEnvio;

	/**
	 * Manejador para el tipo {@link UsuarioCapacita}
	 */
	@Autowired
	@Qualifier("usuarioCapacitaService")
	protected EntityManager<UsuarioCapacita> mngrUsuarioCapacita;

	/**
	 * Manejador para el tipo {@link ValorDocumentalPrimario}
	 */
	@Autowired
	@Qualifier("valorDocumentalPrimarioService")
	protected EntityManager<ValorDocumentalPrimario> mngrValorDocumentalPrimario;

	/**
	 * Manejador para el tipo {@link ValorDocumentalSecundario}
	 */
	@Autowired
	@Qualifier("valorDocumentalSecundarioService")
	protected EntityManager<ValorDocumentalSecundario> mngrValorDocumentalSecundario;

	/**
	 * Manejador para el tipo {@link ParametroApp }
	 */
	@Autowired
	@Qualifier("parametroAppService")
	protected EntityManager<ParametroApp> mngrParamApp;

	/**
	 * Manejador para el tipo {@link Parametro }
	 */
	@Autowired
	@Qualifier("parametroService")
	protected EntityManager<Parametro> mngrParametro;

	/**
	 * Manejador para el tipo {@link DocumentoAntefirmaAsunto }
	 */
	@Autowired
	@Qualifier("documentoAntefirmaAsuntoService")
	protected EntityManager<DocumentoAntefirmaAsunto> mngrDocumentoAntefirmaAsunto;

	/**
	 * Manejador para el tipo {@link DocumentoAntefirmaRespuesta }
	 */
	@Autowired
	@Qualifier("documentoAntefirmaRespuestaService")
	protected EntityManager<DocumentoAntefirmaRespuesta> mngrDocumentoAntefirmaRespuesta;

	/**
	 * Manejador para el tipo {@link Modificacion }
	 */
	@Autowired
	@Qualifier("modificacionService")
	protected EntityManager<Modificacion> mngrModificacion;

	/**
	 * Manejador para el tipo {@link RespuestaConsulta}
	 */
	@Autowired
	@Qualifier("respuestaConsultaService")
	protected EntityManager<RespuestaConsulta> mngrRespuestaConsulta;

	/**
	 * Manejador para el tipo {@link Tipo}
	 */
	@Autowired
	@Qualifier("tipoService")
	protected EntityManager<Tipo> mngrTipo;

	/**
	 * Manejador para el tipo {@link FolioClave}
	 */
	@Autowired
	@Qualifier("folioClaveService")
	protected EntityManager<FolioClave> mngrFolioClave;

	/**
	 * Manejador para el tipo {@link WsSincronizaCompletaDetalle}
	 */
	@Autowired
	@Qualifier("wsSincronizaCompletaDetalleService")
	protected EntityManager<WsSincronizaCompletaDetalle> mngrWsSincronizaCompletaDetalle;

	/**
	 * Manejador para el tipo {@link AsuntoCopiaTurnada}
	 */
	@Autowired
	@Qualifier("asuntoCopiaTurnadaService")
	protected EntityManager<AsuntoCopiaTurnada> mngrAsuntoCopiaTurnada;

	/**
	 * Manejador para el tipo {@link PruebaSintetica}
	 */
	@Autowired
	@Qualifier("pruebaSinteticaService")
	protected EntityManager<PruebaSintetica> mngrPruebaSintetica;

	/**
	 * Manejador para el tipo {@link AuditoriaPS }
	 */
	@Autowired
	@Qualifier("auditoriaPSService")
	protected EntityManager<AuditoriaPS> mngrAuditoriaPS;

	/**
	 * Manejador para el tipo {@link AsuntoSeguimiento}
	 */
	@Autowired
	@Qualifier("asuntoSeguimientoService")
	protected EntityManager<AsuntoSeguimiento> mngrAsuntoSeguimiento;

	/**
	 * Manejador para el tipo {@link DocumentoAsuntoFirmado}
	 */
	@Autowired
	@Qualifier("documentoAsuntoFirmadoService")
	protected EntityManager<DocumentoAsuntoFirmado> mngrDocsAsuntoFirmados;

	/**
	 * Manejador para el tipo {@link DocumentoRespuestaFirmado}
	 */
	@Autowired
	@Qualifier("documentoRespuestaFirmadoService")
	protected EntityManager<DocumentoRespuestaFirmado> mngrDocsRespuestaFirmados;

	/**
	 * Manejador para el tipo {@link ContadoresView}
	 */
	@Autowired
	@Qualifier("contadoresViewService")
	protected EntityManager<ContadoresView> mngrContadoresView;
	
	/** Manejador para el tipo {@link AsuntoDetalleModal} */
	@Autowired
	@Qualifier("asuntoDetalleModalService")
	protected EntityManager<AsuntoDetalleModal> mngrAsuntoDetalleModal;
	
	/** Manejador para el tipo {@link AsuntoCSV} */
	@Autowired
	@Qualifier("asuntoCSVService")
	protected EntityManager<AsuntoCSV> mngrAsuntoCSV;
	
	/**
	 * Manejador para el tipo {@link TramiteCSV}
	 */
	@Autowired
	@Qualifier("tramiteCSVService")
	protected EntityManager<TramiteCSV> mngrTramiteCSV;
	
	/**
	 * Manejador para el tipo {@link TramiteConsulta}
	 */
	@Autowired
	@Qualifier("tramiteConsultaService")
	protected EntityManager<TramiteConsulta> mngrTramiteConsulta;
	
	/** Manejador para el tipo {@link AsuntoAntecedente} */
	@Autowired
	@Qualifier("asuntoAntecedenteService")
	protected EntityManager<AsuntoAntecedente> mngrAsuntoRelacionado;
	
	/** Manejador para el tipo {@link AsuntoRecibidoConsulta} */
	@Autowired
	@Qualifier("asuntoRecibidoConsultaService")
	protected EntityManager<AsuntoRecibidoConsulta> mngrAsuntoRecibidoConsulta;
	
	/** Manejador para el tipo {@link AsuntoRechazadoConsulta} */
	@Autowired
	@Qualifier("asuntoRechazadoConsultaService")
	protected EntityManager<AsuntoRechazadoConsulta> mngrAsuntoRechazadoConsulta;
	
	

	/**
	 * Solicitud http.
	 */
	@Autowired
	private HttpServletRequest request;

	/** */
	@Autowired
	protected DBVendor dbVendor;

	/** */
	protected Map<HeaderValueNames, String> tempHeader;

	/** */
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");

	/**
	 * Default Constructor.
	 */
	public CustomRestController() {
		super();
		tempHeader = new HashMap<>();
	}

	/**
	 * Obtiene la instancia de contexto de la aplicacion.
	 *
	 * @return the appContext
	 */
	public ApplicationContext getAppContext() {
		return appContext;
	}

	/**
	 * Desencripta el valor que se pase por parametro
	 *
	 * @param value Valor a ser desencriptado
	 * @return Valor desencriptado
	 */
	protected String decryptText(String value) {

		try {

			return Security.decript(value);

		} catch (Exception e) {
			log.error("Error al momento de desencriptar el valor con la siguiente descripcrion: " + e.getMessage());
			return value;
		}
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	protected String encryptText(String value) {

		try {

			return Security.encript(value);

		} catch (Exception e) {
			log.error("Error al momento de encriptar el valor con la siguiente descripcrion: " + e.getMessage());
			return value;
		}
	}

	/**
	 * Obtiene el valor del header inidicado.
	 *
	 * @param headerName Nombre del atributo que se desea obtener de la cabecera
	 * @param decrypted  Opcion para desencriptar o no el valor que se retorna
	 * @return Valor del header inidicado
	 */
	protected String getHeader(HeaderValueNames headerName, boolean decrypted) {
		String value;
		try {

			value = request.getHeader(headerName.getName());

			log.debug(headerName.toString() + " >> " + value);

			if (decrypted) {
				value = decryptText(value);
			}
		} catch (Exception ex) {
			value = tempHeader.get(headerName);
			if (Objects.isNull(value)) {
				throw ex;
			}
		}
		return value;
	}

	/**
	 * Obtiene el valor del header inidicado.
	 *
	 * @param headerName Nombre del atributo que se desea obtener de la cabecera
	 * @return Valor del header inidicado
	 */
	protected String getHeader(HeaderValueNames headerName) {
		return getHeader(headerName, true);
	}

	/**
	 * valida si un usuario tiene permiso de solo lectura
	 *
	 * @param idUsuario
	 * @return
	 * @throws Exception
	 */
	protected boolean esSoloLectura(String idUsuario) throws Exception {
		log.debug("::>> Validando si el usuario es solo Lectura ::>> ");
		boolean soloLectura = false;
		Usuario usuarioSesion = mngrUsuario.fetch(idUsuario);
		if (usuarioSesion != null) {
			if ("R".equalsIgnoreCase(usuarioSesion.getRol().getTipo())) {
				soloLectura = true;
			}
		} else {
			throw new Exception("El usuario no es valido");
		}

		log.debug("::>> Usuario es solo Lectura ::>> " + soloLectura);
		return soloLectura;
	}

	/**
	 * Obtiene la direccion IP del equipo que esta haciendo la peticion
	 *
	 * @return Direccion IP del equipo que esta haciendo la peticion
	 */
	protected String getRemoteIpAddress() {

		String ipAddress = request.getHeader("X-FORWARDED-FOR");

		return (ipAddress != null) ? ipAddress : request.getRemoteAddr();
	}

	/**
	 * @param entityManager
	 * @param entity
	 * @throws ConstraintViolationException
	 */
	public void validateEntity(EntityManager<?> entityManager, Serializable entity)
			throws ConstraintViolationException {

		log.debug("::>> Se inicializaron el validator");
		// Validamos que las reglas de validacion de la entidad que se recibio
		// por parametros
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		ValidatorContext validatorContext = factory.usingContext();
		validatorContext.constraintValidatorFactory(new ConstraintValidatorFactoryImpl(entityManager));
		Validator validator = validatorContext.getValidator();

		log.debug("::>> Se van a validar los constraint del Entity para " + "determinar si es valido");
		Set<ConstraintViolation<Serializable>> violations = validator.validate(entity);

		if (0 < violations.size()) {

			log.debug("::>> Existen " + violations.size() + " reglas de negocio que se estan violando");

			StringBuilder errors = new StringBuilder();

			for (ConstraintViolation<?> violation : violations) {

				errors.append(violation.getMessage());
				log.error(String.format("%s: %s%n", violation.getPropertyPath(), violation.getMessage()));
			}

			throw new ConstraintViolationException(errors.toString(), violations);
		}

		log.debug("::>> No se violo ningun constraint del Entity");
	}

	/**
	 * Obtiene el valor de un atributo en la tabla PARAMETROSAPP
	 *
	 * @param idSeccion Identificador de la seccion
	 * @param idClave   Identificador de la Clave
	 * @return Valor de un atributo en la tabla PARAMETROSAPP, en caso que no
	 *         exista, se retorna <t>null</t>
	 */
	protected String getParamApp(String idSeccion, String idClave) {

		ParametroApp param = mngrParamApp.fetch(new ParametroAppPK(idSeccion, idClave));

		return (param != null ? param.getValor() : null);
	}

	/**
	 * Obtiene el valor de un atributo en la tabla PARAMETROSAPP, cuyo 'idSeccion'
	 * es 'SIGAP'
	 *
	 * @param idClave Identificador de la Clave
	 * @return Valor de un atributo en la tabla PARAMETROSAPP, en caso que no
	 *         exista, se retorna <t>null</t>
	 */
	public String getParamApp(String idClave) {
		return getParamApp("SIGAP", idClave);
	}

	/**
	 * Obtiene la fecha actual de servidor de tiempo
	 *
	 * @return Fecha actual
	 * @throws Exception Cualquier error al momento de ejecutar el metodo
	 */
	private Date getCurrentTime(String data, String tipo) throws Exception {

		String timestamp = (String) firmaEndPoint.getTime(data, tipo).get("Tiempo");

		Date timestampToDate = SignatureUtil.timestampToDate(timestamp);

		return timestampToDate;

	}

	/**
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	protected Date getCurrentTime(String data) throws Exception {
		return getCurrentTime(data, null);
	}

	/**
	 * Valida que los folder del sistema configurados en parametrosapp existan.
	 *
	 * @throws Exception
	 */
	protected void validateSystemFolders() throws Exception {

		String[] folders = { // "COLABORATIVO",
				// "CABINETEXTERNO",
				"CABINET" };

		String param, val, objectId;

		for (int i = 0; i < folders.length; i++) {

			param = folders[i];

			val = getParamApp(param);

			log.info("Validando el parametro " + param + " con valor :: " + val);

			if (StringUtils.isBlank(val)) {
				throw new Exception("El parametro " + param + " no posee un valor.");
			}

			try {

				objectId = EndpointDispatcher.getInstance().getFolderIdByPath(val);

				if (StringUtils.isBlank(objectId)) {
					throw new Exception("El folder indicado en el parametro " + param + " no existe.");
				}

				log.info(val + " >> Object id >> " + objectId);

			} catch (Exception e) {

				log.error(e.getLocalizedMessage());
				throw e;

			}

		}

	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public String addTempHeader(HeaderValueNames key, String value) {
		return tempHeader.put(key, value);
	}

}