package com.IsilERPSpring.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="Rol")
public class Rol {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="nombre")
	private String nombre;

	@ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	 public Set<Usuario> getUsuarios() {
	      return usuarios;
	  }
	 
	 public void setUsuarios(Set<Usuario> usuarios) {
	     this.usuarios = usuarios;
	 }
}
