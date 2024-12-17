package com.IsilERPSpring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Distrito") 
public class Distrito {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_distrito")
    private int id_distrito;

	@Column(name = "nombre_distrito")
	private String nombre_distrito;

	public int getId_distrito() {
		return id_distrito;
	}

	public void setId_distrito(int id_distrito) {
		this.id_distrito = id_distrito;
	}

	public String getNombre_distrito() {
		return nombre_distrito;
	}

	public void setNombre_distrito(String nombre_distrito) {
		this.nombre_distrito = nombre_distrito;
	}

}
