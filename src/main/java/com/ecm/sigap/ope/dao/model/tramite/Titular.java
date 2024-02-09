package com.ecm.sigap.ope.dao.model.tramite;

import com.ecm.sigap.data.model.Area;
import com.ecm.sigap.data.model.Rol;
import com.ecm.sigap.data.model.Usuario;

public class Titular {

	private Long id;
	private String prefijo;
	private String nombres;
	private String paterno;
	private String materno;
	private String nombreCompleto;
	private String cargo;
	private Area area;
	private Integer idExterno;
	private String idTipo;
	private Boolean activosn;
	private Rol rol;
	private Usuario usuario;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrefijo() {
		return prefijo;
	}
	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getPaterno() {
		return paterno;
	}
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}
	public String getMaterno() {
		return materno;
	}
	public void setMaterno(String materno) {
		this.materno = materno;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public String getCargo() {
		return cargo;
	}
	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public Integer getIdExterno() {
		return idExterno;
	}
	public void setIdExterno(Integer idExterno) {
		this.idExterno = idExterno;
	}
	public String getIdTipo() {
		return idTipo;
	}
	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}
	public Boolean getActivosn() {
		return activosn;
	}
	public void setActivosn(Boolean activosn) {
		this.activosn = activosn;
	}
	public Rol getRol() {
		return rol;
	}
	public void setRol(Rol rol) {
		this.rol = rol;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	@Override
	public String toString() {
		return "Titular [id=" + id + ", prefijo=" + prefijo + ", nombres=" + nombres + ", paterno=" + paterno
				+ ", materno=" + materno + ", nombreCompleto=" + nombreCompleto + ", cargo=" + cargo + ", area=" + area
				+ ", idExterno=" + idExterno + ", idTipo=" + idTipo + ", activosn=" + activosn + ", rol=" + rol
				+ ", usuario=" + usuario + "]";
	}
	
	
}