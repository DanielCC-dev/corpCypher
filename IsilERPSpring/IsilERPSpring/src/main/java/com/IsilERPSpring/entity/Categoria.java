package com.IsilERPSpring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="Categoria")
public class Categoria {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 	@Column(name = "id_categoria")
	    private int id_categoria;
	    
	    @Column(name = "nombre_categoria")
	    private String nombre_categoria;

		public int getId_categoria() {
			return id_categoria;
		}

		public void setId_categoria(int id_categoria) {
			this.id_categoria = id_categoria;
		}

		public String getNombre_categoria() {
			return nombre_categoria;
		}

		public void setNombre_categoria(String nombre_categoria) {
			this.nombre_categoria = nombre_categoria;
		}

	    
	    
}
