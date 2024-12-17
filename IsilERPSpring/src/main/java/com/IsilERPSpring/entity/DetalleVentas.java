package com.IsilERPSpring.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DetalleVentas")
public class DetalleVentas {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="id_ventas")
	private Ventas ventas;
	
	//@ManyToOne
	//@JoinColumn(name="id_pedido")
	//private Pedido pedido;
	
	@ManyToOne
	@JoinColumn(name="id_articulo")
	private Articulo articulo;
	
	@Column(name = "precioItemTotal")
	private double precioItemTotal;
	
	@Column(name = "cantidad")
	private int cantidad;
	
	//@ManyToOne
	//@JoinColumn(name="id_cliente")
	//private Cliente cliente;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Ventas getVentas() {
		return ventas;
	}

	public void setVentas(Ventas ventas) {
		this.ventas = ventas;
	}
	public Articulo getArticulo() {
		return articulo;
	}

	public void setArticulo(Articulo articulo) {
		this.articulo = articulo;
	}

	public double getPrecioItemTotal() {
		return precioItemTotal;
	}

	public void setPrecioItemTotal(double precioItemTotal) {
		this.precioItemTotal = precioItemTotal;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
}
