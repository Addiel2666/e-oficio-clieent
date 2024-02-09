/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Clase de entidad que representa la tabla FOLIOPS_CLAVE
 *
 * @author Adan quintero
 * @version 1.0
 * <p>
 * Creacion de la clase
 */
@Entity
@Table(name = "FOLIOPS_CLAVE")
public class FolioPSClave implements java.io.Serializable {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 6374469197424723568L;

    /**
     * Identificador del Area
     */
    @Id
    @Column(name = "IDAREA", unique = true, nullable = false, precision = 38, scale = 0)
    private Integer idArea;

    /**
     * Prefijo usado para generar el Numero de Documento y Oficio
     */
    @Column(name = "PREFIJOFOLIO", length = 20)
    private String prefijoFolio;

    /**
     * Sufijo usado para generar el Numero de Documento y Oficio
     */
    @Column(name = "SUFIJOFOLIO", length = 20)
    private String sufijoFolio;

    /**
     * Constructor por defecto de la clase
     */
    public FolioPSClave() {
        super();
        // Constructor vacio
    }

    /**
     * Minimo contructor de la clase
     *
     * @param idArea Identificador del Area
     */
    public FolioPSClave(Integer idArea) {
        this.idArea = idArea;
    }

    /**
     * Full constructor de la clase
     *
     * @param idArea       Identificador del Area
     * @param prefijoFolio Prefijo del Folio
     * @param sufijoFolio  Sufijo del Folio
     */
    public FolioPSClave(Integer idArea, String prefijoFolio, String sufijoFolio) {
        this.idArea = idArea;
        this.prefijoFolio = prefijoFolio;
        this.sufijoFolio = sufijoFolio;
    }

    /**
     * Obtiene el Identificador del Area
     *
     * @return Identificador del Area
     */
    public Integer getIdArea() {

        return this.idArea;
    }

    /**
     * Asigna el Identificador del Area
     *
     * @param idarea Identificador del Area
     */
    public void setIdArea(Integer idarea) {

        this.idArea = idarea;
    }

    /**
     * Obtiene el Prefijo
     *
     * @return Prefijo
     */
    public String getPrefijoFolio() {

        return (null != this.prefijoFolio) ? this.prefijoFolio : "";
    }

    /**
     * Asigna el Prefijo
     *
     * @param prefijofolio Prefijo
     */
    public void setPrefijoFolio(String prefijofolio) {
        this.prefijoFolio = prefijofolio;
    }

    /**
     * Obtiene el Sufijo
     *
     * @return Sufijo
     */
    public String getSufijoFolio() {

        return (null != this.sufijoFolio) ? this.sufijoFolio : "";
    }

    /**
     * Asigna el Sufijo
     *
     * @param sufijofolio Sufijo
     */
    public void setSufijoFolio(String sufijofolio) {

        if (null != sufijofolio) {

            this.sufijoFolio = sufijofolio;

        } else {

            this.sufijoFolio = "";
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FolioPS [idArea=" + idArea + ", prefijoFolio=" + prefijoFolio + ", sufijoFolio=" + sufijoFolio + "]";
    }

}
