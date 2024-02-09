/**
 */
package com.ecm.sigap.data.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.ecm.sigap.data.util.BooleanToStringConverter;

/**
 * The Class Representante.
 *
 * @author Gustavo Vielma
 * @version 1.0
 */
@Entity
@Table(name = "representantes")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ECM_SIGAP_V_CACHE_REGION")
@SecondaryTables({ 
	@SecondaryTable(name = "usuariosExternos", pkJoinColumns = @PrimaryKeyJoinColumn(name = "idUsuario", referencedColumnName = "idRepresentante")), //
	@SecondaryTable(name = "prefijousuarios", pkJoinColumns = @PrimaryKeyJoinColumn(name = "idRepresentante", referencedColumnName = "idRepresentante")) //
})
public class Funcionario implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 151288763338190905L;

	/** The id. */
	@Id
	@GenericGenerator(name = "SEQ_FUNCIONARIOS", //
			strategy = "com.ecm.sigap.data.util.StringSequenceGeneratorRepresentante")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FUNCIONARIOS")
	@Column(name = "idRepresentante")
	private String id;

	/** The nombres. */
	@Column(name = "nombre")
	private String nombres;

	/** The paterno. */
	@Column(name = "paterno")
	private String paterno;

	/** The materno. */
	@Column(name = "materno")
	private String materno;

	/** The nombre completo. */
	@Formula(" concat( NOMBRE , concat( ' ' , concat(PATERNO , concat(' ' ,  MATERNO)))) ")
	private String nombreCompleto;

	/** The cargo. */
	@Column(name = "cargo")
	private String cargo;

	/** The id area. */
	@OneToOne
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "idArea")
	@Fetch(value = FetchMode.SELECT)
	private AreaAuxiliar area;

	/** The id externo. */
	@Column(name = "idExterno")
	private String idExterno;

	/** The id tipo. */
	@Column(name = "idTipoRepresentante")
	private String idTipo;

	/** The id externo. */
	@Column(name = "email", table = "usuariosExternos")
	private String email;

	/** The activosn. */
	@Column(name = "activosn", table = "usuariosExternos")
	@Convert(converter = BooleanToStringConverter.class)
	private Boolean activosn;
	
	@Column(name = "prefijo", table = "prefijousuarios")
	private String prefijo;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the nombres.
	 *
	 * @return the nombres
	 */
	public String getNombres() {
		return nombres;
	}

	/**
	 * Sets the nombres.
	 *
	 * @param nombres
	 *            the new nombres
	 */
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	/**
	 * Gets the paterno.
	 *
	 * @return the paterno
	 */
	public String getPaterno() {
		return paterno;
	}

	/**
	 * Sets the paterno.
	 *
	 * @param paterno
	 *            the new paterno
	 */
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}

	/**
	 * Gets the materno.
	 *
	 * @return the materno
	 */
	public String getMaterno() {
		return materno == null ? "" : materno;
	}

	/**
	 * Sets the materno.
	 *
	 * @param materno
	 *            the new materno
	 */
	public void setMaterno(String materno) {
		this.materno = materno;
	}

	/**
	 * Gets the nombre completo.
	 *
	 * @return the nombre completo
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * Sets the nombre completo.
	 *
	 * @param nombreCompleto
	 *            the new nombre completo
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	/**
	 * Gets the cargo.
	 *
	 * @return the cargo
	 */
	public String getCargo() {
		return cargo;
	}

	/**
	 * Sets the cargo.
	 *
	 * @param cargo
	 *            the new cargo
	 */
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	/**
	 * Gets the area.
	 *
	 * @return the area
	 */
	public AreaAuxiliar getArea() {
		return area;
	}

	/**
	 * Sets the area.
	 *
	 * @param area
	 *            the new area
	 */
	public void setArea(AreaAuxiliar area) {
		this.area = area;
	}

	/**
	 * Gets the id externo.
	 *
	 * @return the id externo
	 */
	public String getIdExterno() {
		return idExterno;
	}

	/**
	 * Sets the id externo.
	 *
	 * @param idExterno
	 *            the new id externo
	 */
	public void setIdExterno(String idExterno) {
		this.idExterno = idExterno;
	}

	/**
	 * Gets the id tipo.
	 *
	 * @return the id tipo
	 */
	public String getIdTipo() {
		return idTipo;
	}

	/**
	 * Sets the id tipo.
	 *
	 * @param idTipo
	 *            the new id tipo
	 */
	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email
	 *            the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the activosn.
	 *
	 * @return the activosn
	 */
	public Boolean getActivosn() {
		return activosn;
	}

	/**
	 * Sets the activosn.
	 *
	 * @param activosn
	 *            the new activosn
	 */
	public void setActivosn(Boolean activosn) {
		this.activosn = activosn;
	}
	
	/**
	 * Gets the prefijo.
	 *
	 * @return the prefijo
	 */
	public String getPrefijo() {
        return prefijo;
    }
	
	/**
	 * Sets the prefijo.
	 *
	 * @param prefijo
	 *            the new prefijo
	 */
    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

	/**
	 * Gets the serialversionuid.
	 *
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Funcionario [id=" + id + ", nombres=" + nombres + ", paterno=" + paterno + ", materno=" + materno
				+ ", nombreCompleto=" + nombreCompleto + ", cargo=" + cargo + ", area=" + area + ", idExterno="
				+ idExterno + ", idTipo=" + idTipo + ", email=" + email + ", activosn=" + activosn + "]";
	}

}
